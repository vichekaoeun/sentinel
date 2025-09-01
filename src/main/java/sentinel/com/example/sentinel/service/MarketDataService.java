// This service is responsible for grabbing market data

package sentinel.com.example.sentinel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class MarketDataService {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${finnhub.api.key:demo}")
    private String apiKey;
    
    @Value("${finnhub.base.url:https://finnhub.io/api/v1}")
    private String baseUrl;
    
    // Price cache with timestamp for freshness
    private final Map<String, PriceData> priceCache = new ConcurrentHashMap<>();
    
    // Cache expiration time (15 minutes for free tier)
    private static final long CACHE_EXPIRY_MS = 60 * 60 * 1000;
    
    // Rate limiting for free tier (60 calls per minute)
    private static final int MAX_CALLS_PER_MINUTE = 50; // Leave some buffer
    private final Queue<Long> apiCallTimestamps = new ConcurrentLinkedQueue<>();



    public Optional<Double> getMarketPrice(String assetSymbol) {
        return getMarketPrice(assetSymbol, false);
    }

    public Optional<Double> getMarketPrice(String assetSymbol, boolean forceRefresh) {
        // Check cache first (unless forcing refresh)
        if (!forceRefresh) {
            PriceData cachedData = priceCache.get(assetSymbol);
            if (cachedData != null && !cachedData.isExpired()) {
                return Optional.of(cachedData.getPrice());
            }
        }
        
        // Check rate limiting
        if (!canMakeApiCall()) {
            logger.warn("Rate limit reached, using cached data for {}", assetSymbol);
            PriceData cachedData = priceCache.get(assetSymbol);
            if (cachedData != null) {
                return Optional.of(cachedData.getPrice());
            }
            return Optional.empty();
        }
        
        // Fetch fresh data from Finnhub
        try {
            String url = baseUrl + "/quote?symbol=" + assetSymbol + "&token=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JSONObject json = new JSONObject(response);
                if (json.has("c") && !json.isNull("c")) {
                    double currentPrice = json.getDouble("c");
                    double change = json.optDouble("d", 0.0);
                    double changePercent = json.optDouble("dp", 0.0);
                    long timestamp = json.optLong("t", System.currentTimeMillis());
                    
                    PriceData priceData = new PriceData(currentPrice, change, changePercent, timestamp);
                    priceCache.put(assetSymbol, priceData);
                    
                    // Record API call for rate limiting
                    recordApiCall();
                    
                    logger.info("Fetched live price for {}: ${} ({}%)", assetSymbol, currentPrice, changePercent);
                    return Optional.of(currentPrice);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching market price for {}: {}", assetSymbol, e.getMessage());
        }
        
        // Fallback to cached data if available (even if expired)
        PriceData cachedData = priceCache.get(assetSymbol);
        if (cachedData != null) {
            logger.warn("Using expired cached price for {}: ${}", assetSymbol, cachedData.getPrice());
            return Optional.of(cachedData.getPrice());
        }
        
        return Optional.empty();
    }
    
    // Rate limiting methods
    private boolean canMakeApiCall() {
        long currentTime = System.currentTimeMillis();
        long oneMinuteAgo = currentTime - 60000;
        
        // Remove old timestamps
        while (!apiCallTimestamps.isEmpty() && apiCallTimestamps.peek() < oneMinuteAgo) {
            apiCallTimestamps.poll();
        }
        
        return apiCallTimestamps.size() < MAX_CALLS_PER_MINUTE;
    }
    
    private void recordApiCall() {
        apiCallTimestamps.offer(System.currentTimeMillis());
    }
    
    public List<MarketQuote> getMarketQuotes(List<String> symbols) {
        return getMarketQuotes(symbols, false);
    }

    public List<MarketQuote> getMarketQuotes(List<String> symbols, boolean forceRefresh) {
        List<MarketQuote> quotes = new ArrayList<>();
        
        for (String symbol : symbols) {
            Optional<Double> price = getMarketPrice(symbol, forceRefresh);
            if (price.isPresent()) {
                PriceData priceData = priceCache.get(symbol);
                quotes.add(new MarketQuote(symbol, priceData));
            }
        }
        
        return quotes;
    }
    
    public Optional<MarketQuote> getMarketQuote(String symbol) {
        return getMarketQuote(symbol, false);
    }

    public Optional<MarketQuote> getMarketQuote(String symbol, boolean forceRefresh) {
        Optional<Double> price = getMarketPrice(symbol, forceRefresh);
        if (price.isPresent()) {
            PriceData priceData = priceCache.get(symbol);
            return Optional.of(new MarketQuote(symbol, priceData));
        }
        return Optional.empty();
    }
    
    // Get real-time trade data for a symbol
    public List<TradeEvent> getRecentTrades(String symbol) {
        try {
            String url = baseUrl + "/stock/trades?symbol=" + symbol + "&from=" + 
                        (System.currentTimeMillis() - 3600000) + "&token=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            
            List<TradeEvent> trades = new ArrayList<>();
            if (response != null) {
                JSONObject json = new JSONObject(response);
                if (json.has("data")) {
                    JSONArray data = json.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject trade = data.getJSONObject(i);
                        trades.add(new TradeEvent(
                            trade.getString("s"),
                            trade.getDouble("p"),
                            trade.getLong("t"),
                            trade.getInt("v"),
                            trade.opt("c")
                        ));
                    }
                }
            }
            return trades;
        } catch (Exception e) {
            logger.error("Error fetching recent trades for {}: {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }

    // Price data with timestamp for caching
    public static class PriceData {
        private final double price;
        private final double change;
        private final double changePercent;
        private final long timestamp;
        
        public PriceData(double price, double change, double changePercent, long timestamp) {
            this.price = price;
            this.change = change;
            this.changePercent = changePercent;
            this.timestamp = timestamp;
        }
        
        public double getPrice() { return price; }
        public double getChange() { return change; }
        public double getChangePercent() { return changePercent; }
        public long getTimestamp() { return timestamp; }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
        
        public String getFormattedChange() {
            String sign = change >= 0 ? "+" : "";
            return String.format("%s%.2f (%.2f%%)", sign, change, changePercent);
        }
    }
    
    // Market quote with price data
    public static class MarketQuote {
        private final String symbol;
        private final PriceData priceData;
        
        public MarketQuote(String symbol, PriceData priceData) {
            this.symbol = symbol;
            this.priceData = priceData;
        }
        
        public String getSymbol() { return symbol; }
        public PriceData getPriceData() { return priceData; }
        public double getPrice() { return priceData.getPrice(); }
        public double getChange() { return priceData.getChange(); }
        public double getChangePercent() { return priceData.getChangePercent(); }
        public String getFormattedChange() { return priceData.getFormattedChange(); }
        
        // Add getter for expired status
        public boolean isExpired() { return priceData.isExpired(); }
    }
    
    public static class TradeEvent {
        public final String symbol;
        public final double price;
        public final long timestamp;
        public final int volume;
        public final Object conditions;

        public TradeEvent(String symbol, double price, long timestamp, int volume, Object conditions) {
            this.symbol = symbol;
            this.price = price;
            this.timestamp = timestamp;
            this.volume = volume;
            this.conditions = conditions;
        }
    }

}
