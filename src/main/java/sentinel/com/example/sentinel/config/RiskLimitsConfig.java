package sentinel.com.example.sentinel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RiskLimitsConfig {

    @Value("${risk.position.limit}")
    private int positionLimit;

    @Value("${risk.daily.stoploss}")
    private double dailyStopLoss;

    @Value("${risk.counterparty.limit}")
    private double counterpartyLimit;

    @Value("${risk.concentration.max}")
    private double concentrationMax;

    public int positionLimit(String trader, String symbol) { return positionLimit; }
    public double dailyStopLoss(String trader) { return dailyStopLoss; }
    public double counterpartyLimit(String counterparty) { return counterpartyLimit; }
    public double concentrationMax() { return concentrationMax; }
}
