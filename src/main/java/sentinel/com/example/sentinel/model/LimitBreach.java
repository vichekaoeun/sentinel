package sentinel.com.example.sentinel.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LimitBreach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String breachId;
    private String limitType;
    private String trader;
    private String symbol;
    private String counterparty;
    private double actualValue;
    private double threshold;
    private Long tradeId;
    private Instant occurredAt;
    private String status; // NEW, ACKNOWLEDGED, RESOLVED
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    // Constructors
    public LimitBreach() {}

    public LimitBreach(Limit limit) {
        this.breachId = limit.getBreachId();
        this.limitType = limit.getLimitType();
        this.trader = limit.getTrader();
        this.symbol = limit.getSymbol();
        this.counterparty = limit.getCounterparty();
        this.actualValue = limit.getActualValue();
        this.threshold = limit.getThreshold();
        this.tradeId = limit.getTradeId();
        this.occurredAt = limit.getOccurredAt();
        this.status = "NEW";
        this.severity = determineSeverity(limit);
    }

    private String determineSeverity(Limit limit) {
        double exceedanceRatio = Math.abs(limit.getActualValue() / limit.getThreshold());
        if (exceedanceRatio > 2.0) return "CRITICAL";
        if (exceedanceRatio > 1.5) return "HIGH";
        if (exceedanceRatio > 1.2) return "MEDIUM";
        return "LOW";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
