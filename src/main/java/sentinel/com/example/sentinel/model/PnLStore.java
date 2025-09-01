package sentinel.com.example.sentinel.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PnLStore {
    // Map<trader, daily PnL>
    private final Map<String, Double> dailyPnl = new HashMap<>();

    public void update(String trader, double realizedPnl) {
        dailyPnl.merge(trader, realizedPnl, Double::sum);
    }

    public double getDaily(String trader) {
        return dailyPnl.getOrDefault(trader, 0.0);
    }

    public void resetDaily() {
        dailyPnl.clear();
    }
}