package uk.tw.energy.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final Map<String, String> smartMeterToPricePlanAccounts;

    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    /**
     * Retrieves the price plan ID associated with a given smart meter.
     *
     * @param smartMeterId The ID of the smart meter
     * @return The price plan ID associated with the smart meter
     */
    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
