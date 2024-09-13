package uk.tw.energy.builders;

import java.util.ArrayList;
import java.util.List;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

/**
 * Builder class for creating MeterReadings objects.
 * Provides methods to set the smart meter ID and generate electricity readings.
 */
public class MeterReadingsBuilder {

    private static final String DEFAULT_METER_ID = "id";
    private String smartMeterId = DEFAULT_METER_ID;
    private List<ElectricityReading> electricityReadings = new ArrayList<>();

    /**
     * Sets the smart meter ID for the MeterReadings object.
     * @param smartMeterId The ID of the smart meter.
     * @return This MeterReadingsBuilder instance for method chaining.
     */
    public MeterReadingsBuilder setSmartMeterId(String smartMeterId) {
        this.smartMeterId = smartMeterId;
        return this;
    }

    /**
     * Generates a default number (5) of electricity readings.
     * @return This MeterReadingsBuilder instance for method chaining.
     */
    public MeterReadingsBuilder generateElectricityReadings() {
        return generateElectricityReadings(5);
    }

    /**
     * Generates a specified number of electricity readings.
     * @param number The number of electricity readings to generate.
     * @return This MeterReadingsBuilder instance for method chaining.
     */
    public MeterReadingsBuilder generateElectricityReadings(int number) {
        ElectricityReadingsGenerator readingsBuilder = new ElectricityReadingsGenerator();
        this.electricityReadings = readingsBuilder.generate(number);
        return this;
    }

    /**
     * Builds and returns a MeterReadings object with the configured smart meter ID and electricity readings.
     * @return A new MeterReadings object.
     */
    public MeterReadings build() {
        return new MeterReadings(smartMeterId, electricityReadings);
    }
}
