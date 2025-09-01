package sentinel.com.example.sentinel.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sentinel.com.example.sentinel.model.Position;
import sentinel.com.example.sentinel.model.Trade;
import sentinel.com.example.sentinel.repository.PositionRepository;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PositionService(PositionRepository positionRepository, SimpMessagingTemplate messagingTemplate) {
        this.positionRepository = positionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void updatePosition(Trade trade) {
        Position position = positionRepository
            .findByTraderAndSymbol(trade.getTrader(), trade.getSymbol())
            .orElseGet(() -> {
                Position newPosition = new Position();
                newPosition.setTrader(trade.getTrader());
                newPosition.setSymbol(trade.getSymbol());
                newPosition.setQuantity(0);
                return newPosition;
            });

        int delta = trade.getSide().equalsIgnoreCase("BUY") ? trade.getQuantity() : -trade.getQuantity();
        position.setQuantity(position.getQuantity() + delta);

        positionRepository.save(position);
        
        messagingTemplate.convertAndSend("/topic/positions", getAllPositions());
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }
}
