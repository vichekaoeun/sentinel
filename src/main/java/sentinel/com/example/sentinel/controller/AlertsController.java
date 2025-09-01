package sentinel.com.example.sentinel.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sentinel.com.example.sentinel.model.LimitBreach;
import sentinel.com.example.sentinel.service.AlertsComplianceService;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {
    private final AlertsComplianceService alertsService;

    public AlertsController(AlertsComplianceService alertsService) {
        this.alertsService = alertsService;
    }

    @GetMapping
    public List<LimitBreach> getActiveAlerts() {
        return alertsService.getActiveAlerts();
    }

    @PutMapping("/{breachId}/acknowledge")
    public void acknowledgeAlert(@PathVariable Long breachId) {
        alertsService.acknowledgeAlert(breachId);
    }
}
