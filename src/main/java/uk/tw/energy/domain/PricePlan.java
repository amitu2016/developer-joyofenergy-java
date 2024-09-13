package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a price plan for electricity consumption.
 */
public class PricePlan {
    private final String energySupplier;
    private final String planName;
    private final BigDecimal unitRate; // unit price per kWh
    private final List<PeakTimeMultiplier> peakTimeMultipliers;

    /**
     * Constructs a new PricePlan.
     *
     * @param planName The name of the price plan
     * @param energySupplier The name of the energy supplier
     * @param unitRate The base unit rate per kWh
     * @param peakTimeMultipliers List of peak time multipliers
     */
    public PricePlan(String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    /**
     * @return The name of the energy supplier
     */
    public String getEnergySupplier() {
        return energySupplier;
    }

    /**
     * @return The name of the price plan
     */
    public String getPlanName() {
        return planName;
    }

    /**
     * @return The base unit rate per kWh
     */
    public BigDecimal getUnitRate() {
        return unitRate;
    }

    /**
     * Calculates the price for a given date and time, applying peak time multipliers if applicable.
     *
     * @param dateTime The date and time for which to calculate the price
     * @return The calculated price per kWh
     */
    public BigDecimal getPrice(LocalDateTime dateTime) {
        return peakTimeMultipliers.stream()
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
                .findFirst()
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);
    }

    /**
     * Represents a peak time multiplier for a specific day of the week.
     */
    static class PeakTimeMultiplier {
        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        /**
         * Constructs a new PeakTimeMultiplier.
         *
         * @param dayOfWeek The day of the week for this multiplier
         * @param multiplier The multiplier to apply to the base rate
         */
        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
