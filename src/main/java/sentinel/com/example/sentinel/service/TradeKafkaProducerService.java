package sentinel.com.example.sentinel.service;

import org.springframework.stereotype.Service;
import sentinel.com.example.sentinel.model.Trade;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class TradeKafkaProducerService {
    private final KafkaTemplate<String, Trade> kafkaTemplate;
    public TradeKafkaProducerService(KafkaTemplate<String, Trade> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTradeCreated(Trade trade) {
        kafkaTemplate.send("trade-created", trade);
    }
}
