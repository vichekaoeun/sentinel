package sentinel.com.example.sentinel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import sentinel.com.example.sentinel.model.Trade;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private KafkaTemplate<String, Trade> tradeKafkaTemplate;
    
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "Sentinel Risk Management System");
        
        // Check Kafka connectivity
        try {
            // Create a simple trade for health check
            Trade healthTrade = new Trade();
            healthTrade.setTrader("health-check");
            healthTrade.setSymbol("HEALTH");
            healthTrade.setQuantity(1);
            healthTrade.setPrice(0.0);
            healthTrade.setSide("BUY");
            healthTrade.setTimestamp(java.time.Instant.now());
            healthTrade.setTradeId("health-check-" + System.currentTimeMillis());
            
            tradeKafkaTemplate.send("health-check", healthTrade);
            health.put("kafka", "UP");
        } catch (Exception e) {
            health.put("kafka", "DOWN");
            health.put("kafkaError", e.getMessage());
        }
        
        return health;
    }
}
