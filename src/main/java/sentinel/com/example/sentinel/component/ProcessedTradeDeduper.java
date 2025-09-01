package sentinel.com.example.sentinel.component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ProcessedTradeDeduper {
    private final Set<String> processedTradeIds = ConcurrentHashMap.newKeySet();

    public boolean markIfNew(String tradeId) {
        if (tradeId == null || tradeId.trim().isEmpty()) {
            return false; // Invalid trade ID
        }
        return processedTradeIds.add(tradeId);
    }

    public void clear() {
        processedTradeIds.clear();
    }

    public int size() {
        return processedTradeIds.size();
    }
}
