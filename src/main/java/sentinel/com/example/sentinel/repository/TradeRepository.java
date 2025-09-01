package sentinel.com.example.sentinel.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import sentinel.com.example.sentinel.model.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    
}
