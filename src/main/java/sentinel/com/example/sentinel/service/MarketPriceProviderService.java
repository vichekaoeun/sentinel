package sentinel.com.example.sentinel.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class MarketPriceProviderService {
    private final Map<String, Double> priceCache = new ConcurrentHashMap<>();
    private final MarketDataService marketDataService;

    public MarketPriceProviderService(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public double getLastPrice(String symbol) {
        Double cachedPrice = priceCache.get(symbol);
        if (cachedPrice != null) {
            return cachedPrice;
        }

        return marketDataService.getMarketPrice(symbol).orElse(0.0);
    }

    public void updatePrice(String symbol, double price) {
        priceCache.put(symbol, price);
    }

    public void clearCache() {
        priceCache.clear();
    }
}
