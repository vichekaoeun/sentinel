package sentinel.com.example.sentinel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import sentinel.com.example.sentinel.model.Trade;
import sentinel.com.example.sentinel.service.LiveTradingService;
import sentinel.com.example.sentinel.service.MarketDataService;
import sentinel.com.example.sentinel.service.MarketDataService.MarketQuote;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/live-trading")
public class LiveTradingController {
    
    @Autowired
    private LiveTradingService liveTradingService;
    
    @Autowired
    private MarketDataService marketDataService;
    
    // Get live market overview
    @GetMapping("/market-overview")
    public ResponseEntity<List<MarketQuote>> getMarketOverview() {
        return getMarketOverview(false);
    }

    // Get live market overview with optional force refresh
    @GetMapping("/market-overview/refresh")
    public ResponseEntity<List<MarketQuote>> getMarketOverviewWithRefresh() {
        return getMarketOverview(true);
    }

    private ResponseEntity<List<MarketQuote>> getMarketOverview(boolean forceRefresh) {
        try {
            List<MarketQuote> overview = marketDataService.getMarketQuotes(
                liveTradingService.getTradingSymbols(), forceRefresh
            );
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get live price for a specific symbol
    @GetMapping("/price/{symbol}")
    public ResponseEntity<Map<String, Object>> getLivePrice(@PathVariable String symbol) {
        try {
            var quoteOpt = marketDataService.getMarketQuote(symbol);
            if (quoteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            MarketQuote quote = quoteOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("symbol", quote.getSymbol());
            response.put("price", quote.getPrice());
            response.put("change", quote.getChange());
            response.put("changePercent", quote.getChangePercent());
            response.put("formattedChange", quote.getFormattedChange());
            response.put("timestamp", quote.getPriceData().getTimestamp());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Execute a live trade
    @PostMapping("/execute")
    public ResponseEntity<Trade> executeLiveTrade(@RequestBody Map<String, Object> tradeRequest) {
        try {
            String trader = (String) tradeRequest.get("trader");
            String symbol = (String) tradeRequest.get("symbol");
            Integer quantity = (Integer) tradeRequest.get("quantity");
            String side = (String) tradeRequest.get("side");
            String counterparty = (String) tradeRequest.get("counterparty");
            
            // Validate required fields
            if (trader == null || symbol == null || quantity == null || side == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate side
            if (!side.equalsIgnoreCase("BUY") && !side.equalsIgnoreCase("SELL")) {
                return ResponseEntity.badRequest().build();
            }
            
            // Execute the trade with live pricing
            Trade executedTrade = liveTradingService.executeLiveTrade(
                trader, symbol, quantity, side.toUpperCase(), counterparty
            );
            
            return ResponseEntity.ok(executedTrade);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get recent trades for a symbol
    @GetMapping("/trades/{symbol}")
    public ResponseEntity<List<MarketDataService.TradeEvent>> getRecentTrades(@PathVariable String symbol) {
        try {
            List<MarketDataService.TradeEvent> trades = marketDataService.getRecentTrades(symbol);
            return ResponseEntity.ok(trades);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get available trading symbols
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getAvailableSymbols() {
        List<String> symbols = List.of(
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
        return ResponseEntity.ok(symbols);
    }
}
