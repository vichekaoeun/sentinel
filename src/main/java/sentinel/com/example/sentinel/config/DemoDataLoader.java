package sentinel.com.example.sentinel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import sentinel.com.example.sentinel.model.Trade;
import sentinel.com.example.sentinel.service.TradeService;

import java.time.Instant;
import java.util.UUID;

@Component
public class DemoDataLoader implements CommandLineRunner {

    @Autowired
    private TradeService tradeService;

    @Override
    public void run(String... args) throws Exception {
        // Load some demo trades to get started
        loadDemoTrades();
    }

    private void loadDemoTrades() {
        try {
            // Demo trade 1: Small AAPL buy
            Trade trade1 = new Trade();
            trade1.setTrader("alice");
            trade1.setSymbol("AAPL");
            trade1.setQuantity(100);
            trade1.setPrice(150.50);
            trade1.setSide("BUY");
            trade1.setCounterparty("broker1");
            trade1.setTimestamp(Instant.now());
            trade1.setTradeId(UUID.randomUUID().toString());
            tradeService.createTrade(trade1);

            // Demo trade 2: MSFT sell
            Trade trade2 = new Trade();
            trade2.setTrader("bob");
            trade2.setSymbol("MSFT");
            trade2.setQuantity(50);
            trade2.setPrice(300.25);
            trade2.setSide("SELL");
            trade2.setCounterparty("broker2");
            trade2.setTimestamp(Instant.now());
            trade2.setTradeId(UUID.randomUUID().toString());
            tradeService.createTrade(trade2);

            // Demo trade 3: Large GOOGL buy (should trigger limits)
            Trade trade3 = new Trade();
            trade3.setTrader("alice");
            trade3.setSymbol("GOOGL");
            trade3.setQuantity(2000);
            trade3.setPrice(2800.00);
            trade3.setSide("BUY");
            trade3.setCounterparty("broker1");
            trade3.setTimestamp(Instant.now());
            trade3.setTradeId(UUID.randomUUID().toString());
            tradeService.createTrade(trade3);

            System.out.println("✅ Demo trades loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading demo trades: " + e.getMessage());
        }
    }
}
