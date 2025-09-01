package sentinel.com.example.sentinel.model;

import java.time.Instant;

public class Limit {
    private String breachId;        
    private String limitType;  
    private String trader;          
    private String symbol;        
    private String counterparty;   
    private double actualValue;      
    private double threshold;        
    private Long tradeId;            
    private Instant occurredAt;      

    public Limit() {}

    public Limit(String breachId, String limitType, String trader, String symbol, String counterparty,
                       double actualValue, double threshold, Long tradeId, Instant occurredAt) {
        this.breachId = breachId;
        this.limitType = limitType;
        this.trader = trader;
        this.symbol = symbol;
        this.counterparty = counterparty;
        this.actualValue = actualValue;
        this.threshold = threshold;
        this.tradeId = tradeId;
        this.occurredAt = occurredAt;
    }

    public static Limit positionLimit(Trade t, int actual, int threshold) {
        return new Limit(
            java.util.UUID.randomUUID().toString(),
            "POSITION_LIMIT",
            t.getTrader(),
            t.getSymbol(),
            null, // counterparty not relevant for position limit
            actual,
            threshold,
            t.getId(),
            Instant.now()
        );
    }

    public static Limit pnlStopLoss(Trade t, double actual, double threshold) {
        return new Limit(
            java.util.UUID.randomUUID().toString(),
            "PNL_STOP_LOSS",
            t.getTrader(),
            t.getSymbol(),
            null,
            actual,
            threshold,
            t.getId(),
            Instant.now()
        );
    }

    public static Limit counterpartyExposure(Trade t, double actual, double threshold) {
        return new Limit(
            java.util.UUID.randomUUID().toString(),
            "COUNTERPARTY_EXPOSURE",
            t.getTrader(),
            t.getSymbol(),
            t.getCounterparty(),
            actual,
            threshold,
            t.getId(),
            Instant.now()
        );
    }

    public static Limit concentration(Trade t, double actual, double threshold) {
        return new Limit(
            java.util.UUID.randomUUID().toString(),
            "CONCENTRATION_LIMIT",
            t.getTrader(),
            t.getSymbol(),
            null,
            actual,
            threshold,
            t.getId(),
            Instant.now()
        );
    }

    // Getters and setters

    public String getBreachId() { return breachId; }
    public void setBreachId(String breachId) { this.breachId = breachId; }

    public String getLimitType() { return limitType; }
    public void setLimitType(String limitType) { this.limitType = limitType; }

    public String getTrader() { return trader; }
    public void setTrader(String trader) { this.trader = trader; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }

    public double getActualValue() { return actualValue; }
    public void setActualValue(double actualValue) { this.actualValue = actualValue; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
