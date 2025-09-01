package sentinel.com.example.sentinel.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import sentinel.com.example.sentinel.model.LimitBreach;
import sentinel.com.example.sentinel.repository.LimitBreachRepository;

@Service
public class AlertsComplianceService {
    private final LimitBreachRepository limitBreachRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertsComplianceService(LimitBreachRepository limitBreachRepository,
                                  SimpMessagingTemplate messagingTemplate) {
        this.limitBreachRepository = limitBreachRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "limit-breached", groupId = "alerts-service")
    public void onLimitBreached(LimitBreach breach) {
        // Store the breach (if not already stored)
        LimitBreach savedBreach = limitBreachRepository.save(breach);
        
        // Push via WebSocket to dashboard
        messagingTemplate.convertAndSend("/topic/alerts", savedBreach);
        
        // Send email/SMS for critical breaches
        if ("CRITICAL".equals(breach.getSeverity())) {
            sendCriticalAlert(breach);
        }
    }

    private void sendCriticalAlert(LimitBreach breach) {
        // Implement email/SMS notification logic
        System.out.println("CRITICAL ALERT: " + breach.getLimitType() + 
                          " for trader " + breach.getTrader());
    }

    public List<LimitBreach> getActiveAlerts() {
        return limitBreachRepository.findByStatus("NEW");
    }

    public void acknowledgeAlert(Long breachId) {
        limitBreachRepository.findById(breachId).ifPresent(breach -> {
            breach.setStatus("ACKNOWLEDGED");
            limitBreachRepository.save(breach);
        });
    }
}