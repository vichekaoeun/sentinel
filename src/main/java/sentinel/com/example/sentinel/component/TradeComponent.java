package sentinel.com.example.sentinel.model;

import org.springframework.stereotype.Component;

import sentinel.com.example.sentinel.model.Trade;
import java.time.Instant;
import java.util.UUID;

@Component
public class TradeComponent {
    
    public Trade createTrade(String trader, String symbol, int quantity, double price, String side, String counterparty) {
        Trade trade = new Trade();
        trade.setTrader(trader);
        trade.setSymbol(symbol);
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setSide(side);
        trade.setCounterparty(counterparty);
        trade.setTimestamp(Instant.now());
        trade.setTradeId(UUID.randomUUID().toString());
        return trade;
    }
    
    public boolean validateTrade(Trade trade) {
        if (trade.getTrader() == null || trade.getTrader().trim().isEmpty()) {
            return false;
        }
        if (trade.getSymbol() == null || trade.getSymbol().trim().isEmpty()) {
            return false;
        }
        if (trade.getQuantity() <= 0) {
            return false;
        }
        if (trade.getPrice() <= 0) {
            return false;
        }
        if (trade.getSide() == null || (!trade.getSide().equalsIgnoreCase("BUY") && !trade.getSide().equalsIgnoreCase("SELL"))) {
            return false;
        }
        return true;
    }
}
