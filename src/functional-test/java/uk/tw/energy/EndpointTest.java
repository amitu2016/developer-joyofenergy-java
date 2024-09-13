package uk.tw.energy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

/**
 * Functional test for the electricity meter reading and price plan comparison endpoints.
 *
 * This test class verifies the following functionalities:
 * 1. Storing meter readings for a smart meter
 * 2. Retrieving meter readings for a specific smart meter
 * 3. Calculating and comparing prices for all available price plans
 * 4. Recommending the cheapest price plans for a given smart meter
 *
 * The tests use a TestRestTemplate to make HTTP requests to the application's endpoints
 * and verify the responses. They cover both successful scenarios and edge cases to
 * ensure the robustness of the API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Converts MeterReadings object to an HttpEntity with JSON content type.
     *
     * @param meterReadings The MeterReadings object to convert
     * @return HttpEntity containing the MeterReadings object with JSON headers
     */
    private static HttpEntity<MeterReadings> toHttpEntity(MeterReadings meterReadings) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(meterReadings, headers);
    }


    /**
     * Tests the storage of meter readings.
     * Verifies that the /readings/store endpoint returns a 200 OK status.
     */
    @Test
    public void shouldStoreReadings() {
        MeterReadings meterReadings =
                new MeterReadingsBuilder().generateElectricityReadings().build();
        HttpEntity entity = toHttpEntity(meterReadings);
        ResponseEntity response = restTemplate.postForEntity("/readings/store", entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Tests retrieval of meter readings for a specific meter ID.
     * Verifies that the /readings/read/{smartMeterId} endpoint returns the correct data.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() {
        String smartMeterId = "alice";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        populateReadingsForMeter(smartMeterId, data);
        ResponseEntity<ElectricityReading[]> response =
                restTemplate.getForEntity("/readings/read/" + smartMeterId, ElectricityReading[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Arrays.asList(response.getBody())).isEqualTo(data);
    }

    /**
     * Tests the calculation of prices for all price plans.
     * Verifies that the /price-plans/compare-all/{smartMeterId} endpoint returns correct price comparisons.
     */
    @Test
    public void shouldCalculateAllPrices() {
        String smartMeterId = "bob";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        populateReadingsForMeter(smartMeterId, data);
        ResponseEntity<CompareAllResponse> response =
                restTemplate.getForEntity("/price-plans/compare-all/" + smartMeterId, CompareAllResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(new CompareAllResponse(
                        Map.of("price-plan-0", 36000, "price-plan-1", 7200, "price-plan-2", 3600), null));
    }

    /**
     * Tests the recommendation of cheapest price plans.
     * Verifies that the /price-plans/recommend/{smartMeterId} endpoint returns the correct number of recommendations.
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void givenMeterIdAndLimitShouldReturnRecommendedCheapestPricePlans() {
        String smartMeterId = "jane";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        populateReadingsForMeter(smartMeterId, data);
        ResponseEntity<Map[]> response =
                restTemplate.getForEntity("/price-plans/recommend/" + smartMeterId + "?limit=2", Map[].class);
        assertThat(response.getBody()).containsExactly(Map.of("price-plan-2", 3600), Map.of("price-plan-1", 7200));
    }

    /**
     * Helper method to populate readings for a given meter.
     *
     * @param smartMeterId The ID of the smart meter
     * @param data The list of electricity readings to store
     */
    private void populateReadingsForMeter(String smartMeterId, List<ElectricityReading> data) {
        MeterReadings readings = new MeterReadings(smartMeterId, data);
        HttpEntity entity = toHttpEntity(readings);
        restTemplate.postForEntity("/readings/store", entity, String.class);
    }

    /**
     * Record class to represent the response from the compare-all endpoint.
     */
    record CompareAllResponse(Map<String, Integer> pricePlanComparisons, String pricePlanId) {}
}

