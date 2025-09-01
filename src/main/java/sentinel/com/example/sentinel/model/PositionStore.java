package sentinel.com.example.sentinel.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class PositionStore {
    // Map<trader+symbol, quantity>
    private final Map<String, Integer> positions = new ConcurrentHashMap<>();

    public void add(String trader, String symbol, int signedQuantity) {
        String key = trader + ":" + symbol;
        positions.merge(key, signedQuantity, Integer::sum);
    }

    public int get(String trader, String symbol) {
        String key = trader + ":" + symbol;
        return positions.getOrDefault(key, 0);
    }

    public Map<String, Integer> getAllPositions() {
        return new HashMap<>(positions);
    }

    public void clear() {
        positions.clear();
    }
}
