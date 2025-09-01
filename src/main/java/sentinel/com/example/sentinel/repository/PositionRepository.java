package sentinel.com.example.sentinel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sentinel.com.example.sentinel.model.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByTraderAndSymbol(String trader, String symbol);
}
