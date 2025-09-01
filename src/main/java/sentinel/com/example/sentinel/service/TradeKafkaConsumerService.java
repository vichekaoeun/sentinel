package sentinel.com.example.sentinel.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import sentinel.com.example.sentinel.model.Trade;

@Service
public class TradeKafkaConsumerService {

    private final PositionService positionService;
    private final RiskLimitService riskLimitService;

    public TradeKafkaConsumerService(PositionService positionService, RiskLimitService riskLimitService) {
        this.positionService = positionService;
        this.riskLimitService = riskLimitService;
    }

    @KafkaListener(topics = "trade-created", groupId = "risk-limit-service")
    public void onTradeCreated(Trade trade) {
        // Update position
        positionService.updatePosition(trade);
        
        // Process risk evaluation
        riskLimitService.processTradeForRisk(trade);
    }
}
