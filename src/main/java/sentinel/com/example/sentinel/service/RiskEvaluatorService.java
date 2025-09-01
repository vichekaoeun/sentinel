package sentinel.com.example.sentinel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sentinel.com.example.sentinel.component.LimitCheckComponent;
import sentinel.com.example.sentinel.component.ProcessedTradeDeduper;
import sentinel.com.example.sentinel.model.CostBasisStore;
import sentinel.com.example.sentinel.model.ExposureStore;
import sentinel.com.example.sentinel.model.LimitBreach;
import sentinel.com.example.sentinel.model.PositionStore;
import sentinel.com.example.sentinel.model.PnLStore;
import sentinel.com.example.sentinel.model.Trade;

@Service
public class RiskEvaluatorService {
    private final PositionStore positions;
    private final CostBasisStore costBasis;
    private final PnLStore pnlStore;
    private final ExposureStore exposureStore;
    private final LimitCheckComponent limitChecker;
    private final MarketPriceProviderService priceProvider;
    private final ProcessedTradeDeduper deduper;
    
    public RiskEvaluatorService(PositionStore positions, CostBasisStore costBasis, 
                               PnLStore pnlStore, ExposureStore exposureStore,
                               LimitCheckComponent limitChecker, MarketPriceProviderService priceProvider,
                               ProcessedTradeDeduper deduper) {
        this.positions = positions;
        this.costBasis = costBasis;
        this.pnlStore = pnlStore;
        this.exposureStore = exposureStore;
        this.limitChecker = limitChecker;
        this.priceProvider = priceProvider;
        this.deduper = deduper;
    }

    public List<LimitBreach> processTrade(Trade t) {
        if (!deduper.markIfNew(t.getTradeId())) {
            return List.of();
        }

        int signedQty = t.getSide().equalsIgnoreCase("BUY") ? t.getQuantity() : -t.getQuantity();
        positions.add(t.getTrader(), t.getSymbol(), signedQty);

        costBasis.update(t.getTrader(), t.getSymbol(), t.getSide(), t.getQuantity(), t.getPrice());

        double lastPrice = priceProvider.getLastPrice(t.getSymbol());
        pnlStore.update(t.getTrader(), calculatePnL(t, lastPrice));

        exposureStore.updateSymbolValue(t.getSymbol(), lastPrice, positions.get(t.getTrader(), t.getSymbol()));
        if (t.getCounterparty() != null) {
            exposureStore.updateCounterparty(t.getCounterparty(), Math.abs(signedQty * lastPrice));
        }

        // Calculate total portfolio value for concentration checks
        double totalPortfolioValue = calculateTotalPortfolioValue(t.getTrader());
        exposureStore.updateTotalPortfolioValue(t.getTrader(), totalPortfolioValue);

        return limitChecker.runAll(t, positions, pnlStore, exposureStore)
                          .stream()
                          .map(LimitBreach::new)
                          .collect(Collectors.toList());
    }

    private double calculatePnL(Trade trade, double currentPrice) {
        double avgCost = costBasis.getAvgCost(trade.getTrader(), trade.getSymbol());
        int position = positions.get(trade.getTrader(), trade.getSymbol());
        return position * (currentPrice - avgCost);
    }

    private double calculateTotalPortfolioValue(String trader) {
        return positions.getAllPositions().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(trader + ":"))
                .mapToDouble(entry -> {
                    String symbol = entry.getKey().split(":")[1];
                    int quantity = entry.getValue();
                    double price = priceProvider.getLastPrice(symbol);
                    return Math.abs(quantity * price);
                })
                .sum();
    }
}