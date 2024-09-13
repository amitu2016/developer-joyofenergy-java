package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a single electricity reading from a smart meter.
 *
 * @param time The timestamp of the reading
 * @param reading The electricity consumption in kW
 */
public record ElectricityReading(Instant time, BigDecimal reading) {}
