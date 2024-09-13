package uk.tw.energy.domain;

import java.util.List;

/**
 * Represents a collection of meter readings for a specific smart meter.
 *
 * @param smartMeterId The unique identifier of the smart meter
 * @param electricityReadings A list of electricity readings from the smart meter
 */
public record MeterReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {}
