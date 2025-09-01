package sentinel.com.example.sentinel.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import sentinel.com.example.sentinel.model.Trade;
import sentinel.com.example.sentinel.repository.TradeRepository;
import sentinel.com.example.sentinel.service.TradeKafkaProducerService;

import java.util.List;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeKafkaProducerService tradeKafkaProducer;
    private final SimpMessagingTemplate messagingTemplate;

    public TradeService(TradeRepository tradeRepository, 
                       TradeKafkaProducerService tradeKafkaProducer,
                       SimpMessagingTemplate messagingTemplate) {
        this.tradeRepository = tradeRepository;
        this.tradeKafkaProducer = tradeKafkaProducer;
        this.messagingTemplate = messagingTemplate;
    }

    public Trade createTrade(Trade trade) {
        Trade savedTrade = tradeRepository.save(trade);
        tradeKafkaProducer.publishTradeCreated(savedTrade);
        
        messagingTemplate.convertAndSend("/topic/trades", savedTrade);
        
        return savedTrade;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
