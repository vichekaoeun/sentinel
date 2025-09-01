package sentinel.com.example.sentinel.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CostBasisStore {
    // Map<trader:symbol, CostBasisData>
    private final Map<String, CostBasisData> costBasis = new ConcurrentHashMap<>();

    public void update(String trader, String symbol, String side, int quantity, double price) {
        String key = trader + ":" + symbol;
        costBasis.compute(key, (k, existing) -> {
            if (existing == null) {
                existing = new CostBasisData();
            }
            return existing.update(side, quantity, price);
        });
    }

    public double getAvgCost(String trader, String symbol) {
        String key = trader + ":" + symbol;
        CostBasisData data = costBasis.get(key);
        return data != null ? data.getAverageCost() : 0.0;
    }

    private static class CostBasisData {
        private double totalCost = 0.0;
        private int totalQuantity = 0;

        public CostBasisData update(String side, int quantity, double price) {
            if ("BUY".equalsIgnoreCase(side)) {
                totalCost += quantity * price;
                totalQuantity += quantity;
            } else { // SELL
                // FIFO basis - reduce average cost proportionally
                if (totalQuantity > 0) {
                    double avgCost = getAverageCost();
                    totalCost -= Math.min(quantity, totalQuantity) * avgCost;
                    totalQuantity -= Math.min(quantity, totalQuantity);
                }
            }
            return this;
        }

        public double getAverageCost() {
            return totalQuantity > 0 ? totalCost / totalQuantity : 0.0;
        }
    }
}
