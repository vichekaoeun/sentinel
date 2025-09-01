package sentinel.com.example.sentinel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sentinel.com.example.sentinel.model.Trade;
import sentinel.com.example.sentinel.service.MarketDataService.MarketQuote;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Random;

@Service
public class LiveTradingService {
    private static final Logger logger = LoggerFactory.getLogger(LiveTradingService.class);
    
    @Autowired
    private TradeService tradeService;
    
    @Autowired
    private MarketDataService marketDataService;
    
    private final Random random = new Random();
    
    // Popular symbols for live trading
    private final List<String> TRADING_SYMBOLS = List.of(
        // Tech Giants
        "AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA", "AMD",
        
        // Financial
        "JPM", "BAC", "WFC", "GS", "MS", "C", "BLK", "AXP",
        
        // Healthcare
        "JNJ", "PFE", "UNH", "ABBV", "MRK", "TMO", "ABT", "DHR",
        
        // Consumer
        "KO", "PG", "WMT", "HD", "MCD", "DIS", "NKE", "SBUX",
        
        // Energy
        "XOM", "CVX", "COP", "EOG", "SLB", "PSX", "VLO", "MPC",
        
        // Industrials
        "BA", "CAT", "GE", "MMM", "HON", "UPS", "FDX", "RTX",
        
        // ETFs
        "SPY", "QQQ", "IWM", "VTI", "VOO", "VEA", "VWO", "BND",
        
        // Crypto
        "BTC", "ETH", "ADA", "DOT", "LINK", "UNI", "LTC", "BCH",
        
        // Commodities
        "GOLD", "SLV", "USO", "UNG", "DBA", "DBC", "GLD", "SLV",
        
        // International
        "BABA", "TSM", "ASML", "NFLX", "PYPL", "CRM", "ADBE", "ORCL"
    );
    
    @Scheduled(fixedRate = 30000)
    public void simulateLiveTrading() {
        try {
            // Pick a random symbol
            String symbol = TRADING_SYMBOLS.get(random.nextInt(TRADING_SYMBOLS.size()));
            
            // Get live market data
            var quoteOpt = marketDataService.getMarketQuote(symbol);
            if (quoteOpt.isEmpty()) {
                logger.warn("Could not fetch live data for {}", symbol);
                return;
            }
            
            MarketQuote quote = quoteOpt.get();
            double currentPrice = quote.getPrice();
            
            // Generate a realistic trade
            Trade trade = generateRealisticTrade(symbol, currentPrice);
            
            // Execute the trade
            Trade executedTrade = tradeService.createTrade(trade);
            
            logger.info("Executed live trade: {} {} {} shares of {} at ${}", 
                trade.getTrader(), trade.getSide(), trade.getQuantity(), 
                symbol, currentPrice);
                
        } catch (Exception e) {
            logger.error("Error in live trading simulation: {}", e.getMessage());
        }
    }
    
    private Trade generateRealisticTrade(String symbol, double currentPrice) {
        Trade trade = new Trade();
        
        // Random trader
        String[] traders = {"alice", "bob", "charlie", "diana", "eve"};
        trade.setTrader(traders[random.nextInt(traders.length)]);
        
        trade.setSymbol(symbol);
        
        // Realistic quantity (1-1000 shares)
        trade.setQuantity(random.nextInt(1000) + 1);
        
        // Price with small variation (±1%)
        double priceVariation = currentPrice * (0.99 + random.nextDouble() * 0.02);
        trade.setPrice(Math.round(priceVariation * 100.0) / 100.0);
        
        // Random side
        trade.setSide(random.nextBoolean() ? "BUY" : "SELL");
        
        // Random counterparty
        String[] counterparties = {"broker1", "broker2", "broker3", "broker4"};
        trade.setCounterparty(counterparties[random.nextInt(counterparties.length)]);
        
        trade.setTimestamp(Instant.now());
        trade.setTradeId(UUID.randomUUID().toString());
        
        return trade;
    }
    
    // Manual trade execution with live pricing
    public Trade executeLiveTrade(String trader, String symbol, int quantity, String side, String counterparty) {
        try {
            // Get live market price
            var quoteOpt = marketDataService.getMarketQuote(symbol);
            if (quoteOpt.isEmpty()) {
                throw new RuntimeException("Could not fetch live price for " + symbol);
            }
            
            MarketQuote quote = quoteOpt.get();
            double livePrice = quote.getPrice();
            
            // Create trade with live price
            Trade trade = new Trade();
            trade.setTrader(trader);
            trade.setSymbol(symbol);
            trade.setQuantity(quantity);
            trade.setPrice(livePrice);
            trade.setSide(side);
            trade.setCounterparty(counterparty);
            trade.setTimestamp(Instant.now());
            trade.setTradeId(UUID.randomUUID().toString());
            
            // Execute the trade
            Trade executedTrade = tradeService.createTrade(trade);
            
            logger.info("Executed manual live trade: {} {} {} shares of {} at ${}", 
                trader, side, quantity, symbol, livePrice);
                
            return executedTrade;
            
        } catch (Exception e) {
            logger.error("Error executing live trade: {}", e.getMessage());
            throw new RuntimeException("Failed to execute live trade", e);
        }
    }
    
    // Get live market overview
    public List<MarketQuote> getLiveMarketOverview() {
        return marketDataService.getMarketQuotes(TRADING_SYMBOLS);
    }

    // Get trading symbols
    public List<String> getTradingSymbols() {
        return TRADING_SYMBOLS;
    }
}
