package sentinel.com.example.sentinel.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sentinel.com.example.sentinel.model.LimitBreach;

public interface LimitBreachRepository extends JpaRepository<LimitBreach, Long> {
    
    List<LimitBreach> findByTraderAndStatus(String trader, String status);
    
    List<LimitBreach> findByLimitTypeAndOccurredAtAfter(String limitType, Instant after);
    
    @Query("SELECT lb FROM LimitBreach lb WHERE lb.trader = :trader AND lb.occurredAt >= :startTime ORDER BY lb.occurredAt DESC")
    List<LimitBreach> findRecentBreachesForTrader(@Param("trader") String trader, @Param("startTime") Instant startTime);
    
    List<LimitBreach> findBySeverityAndStatus(String severity, String status);
    List<LimitBreach> findByStatus(String status);
}