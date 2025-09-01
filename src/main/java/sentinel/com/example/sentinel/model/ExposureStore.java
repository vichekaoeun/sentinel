package sentinel.com.example.sentinel.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ExposureStore {
    // Map<counterparty, exposure>
    private final Map<String, Double> counterpartyExposure = new HashMap<>();
    // Map<symbol, market value>
    private final Map<String, Double> symbolValue = new HashMap<>();
    // Map<trader, total portfolio value>
    private final Map<String, Double> totalPortfolioValue = new HashMap<>();

    public void updateCounterparty(String counterparty, double exposure) {
        counterpartyExposure.merge(counterparty, exposure, Double::sum);
    }

    public double getCounterparty(String counterparty) {
        return counterpartyExposure.getOrDefault(counterparty, 0.0);
    }

    public void updateSymbolValue(String symbol, double price, int position) {
        symbolValue.put(symbol, Math.abs(position * price));
    }

    public double getSymbolValue(String symbol) {
        return symbolValue.getOrDefault(symbol, 0.0);
    }

    public void updateTotalPortfolioValue(String trader, double value) {
        totalPortfolioValue.put(trader, value);
    }

    public double getTotalPortfolioValue(String trader) {
        return totalPortfolioValue.getOrDefault(trader, 0.0);
    }
}
