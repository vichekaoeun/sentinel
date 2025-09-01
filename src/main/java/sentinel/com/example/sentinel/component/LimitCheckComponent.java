package sentinel.com.example.sentinel.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import sentinel.com.example.sentinel.config.RiskLimitsConfig;
import sentinel.com.example.sentinel.model.ExposureStore;
import sentinel.com.example.sentinel.model.Limit;
import sentinel.com.example.sentinel.model.PnLStore;
import sentinel.com.example.sentinel.model.PositionStore;
import sentinel.com.example.sentinel.model.Trade;

@Component
public class LimitCheckComponent {
    private final RiskLimitsConfig limits;

    public LimitCheckComponent(RiskLimitsConfig limits) {
        this.limits = limits;
    }

    public List<Limit> runAll(Trade t, PositionStore pos, PnLStore pnl, ExposureStore exp) {
        List<Limit> out = new ArrayList<>();

        int position = pos.get(t.getTrader(), t.getSymbol());
        if (Math.abs(position) > limits.positionLimit(t.getTrader(), t.getSymbol())) {
            out.add(Limit.positionLimit(t, position, limits.positionLimit(t.getTrader(), t.getSymbol())));
        }

        double dailyPnl = pnl.getDaily(t.getTrader());
        if (dailyPnl <= limits.dailyStopLoss(t.getTrader())) {
            out.add(Limit.pnlStopLoss(t, dailyPnl, limits.dailyStopLoss(t.getTrader())));
        }

        if (t.getCounterparty() != null) {
            double cpExposure = exp.getCounterparty(t.getCounterparty());
            if (cpExposure > limits.counterpartyLimit(t.getCounterparty())) {
                out.add(Limit.counterpartyExposure(t, cpExposure, limits.counterpartyLimit(t.getCounterparty())));
            }
        }

        double symbolValue = exp.getSymbolValue(t.getSymbol());
        double totalValue = exp.getTotalPortfolioValue(t.getTrader());
        if (totalValue > 0) {
            double concentration = symbolValue / totalValue;
            if (concentration > limits.concentrationMax()) {
                out.add(Limit.concentration(t, concentration, limits.concentrationMax()));
            }
        }
        return out;
    }
}