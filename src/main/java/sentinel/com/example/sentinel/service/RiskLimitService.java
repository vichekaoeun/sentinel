package sentinel.com.example.sentinel.service;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import sentinel.com.example.sentinel.model.LimitBreach;
import sentinel.com.example.sentinel.model.Trade;

@Service
public class RiskLimitService {
    private final RiskEvaluatorService riskEvaluator;
    private final KafkaTemplate<String, LimitBreach> limitBreachKafkaTemplate;

    public RiskLimitService(RiskEvaluatorService riskEvaluator, 
                           KafkaTemplate<String, LimitBreach> limitBreachKafkaTemplate) {
        this.riskEvaluator = riskEvaluator;
        this.limitBreachKafkaTemplate = limitBreachKafkaTemplate;
    }

    public void processTradeForRisk(Trade trade) {
        List<LimitBreach> breaches = riskEvaluator.processTrade(trade);
        
        // Publish each breach to Kafka
        for (LimitBreach breach : breaches) {
            limitBreachKafkaTemplate.send("limit-breached", breach);
        }
    }
}