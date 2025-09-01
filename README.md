# 🚨 Sentinel Risk Management System

A comprehensive trade capture and risk management system built with Spring Boot, Kafka, and React.

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React         │    │   Spring Boot   │    │   Kafka         │
│   Dashboard     │◄──►│   Backend       │◄──►│   Message Bus   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   H2 Database   │
                       │   (In-Memory)   │
                       └─────────────────┘
```

## 🚀 Features

- **Trade Capture Service**: REST API to record trades
- **Risk & Limits Engine**: Real-time exposure and PnL calculation
- **Alerts Service**: Store and query risk alerts
- **Real-time Dashboard**: WebSocket-powered React frontend
- **Position Management**: Track trader positions across symbols
- **Limit Monitoring**: Position, PnL, counterparty, and concentration limits

## 🛠️ Prerequisites

- Java 21+
- Maven 3.6+
- Kafka (local or Docker)

## 📦 Setup

### 1. Start Kafka

Using Docker:
```bash
docker run -p 9092:9092 apache/kafka:3.6.0
```

Or using local Kafka installation:
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

### 2. Build and Run

```bash
# Navigate to project directory
cd sentinel

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the Dashboard

Open your browser and navigate to:
- **Dashboard**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

## 🧪 Testing

### Test the System

1. **Start the application** (see Setup above)
2. **Run the test script**:
   ```bash
   chmod +x test-trades.sh
   ./test-trades.sh
   ```

### Manual Testing

#### Create a Trade
```bash
curl -X POST http://localhost:8080/trades \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "alice",
    "symbol": "AAPL",
    "quantity": 100,
    "price": 150.50,
    "side": "BUY",
    "counterparty": "broker1"
  }'
```

#### Check Alerts
```bash
curl http://localhost:8080/api/alerts
```

#### Check Positions
```bash
curl http://localhost:8080/api/positions
```

## 📊 API Endpoints

### Trade Management
- `POST /trades` - Create a new trade
- `GET /trades` - List all trades

### Risk & Alerts
- `GET /api/alerts` - Get all alerts
- `PUT /api/alerts/{id}/acknowledge` - Acknowledge an alert

### Positions
- `GET /api/positions` - Get all positions

## 🔧 Configuration

### Risk Limits (application.properties)
```properties
risk.position.limit=1000          # Max position size
risk.daily.stoploss=-100000      # Daily stop loss threshold
risk.counterparty.limit=10000000 # Max counterparty exposure
risk.concentration.max=0.4       # Max symbol concentration
```

### Kafka Topics
- `trade-created` - New trades published here
- `limit-breached` - Risk limit breaches published here

## 🏛️ Data Models

### Trade
- `id`, `trader`, `symbol`, `quantity`, `price`
- `side` (BUY/SELL), `timestamp`, `tradeId`, `counterparty`

### Position
- `id`, `trader`, `symbol`, `quantity`

### LimitBreach
- `id`, `breachId`, `limitType`, `trader`, `symbol`
- `actualValue`, `threshold`, `status`, `severity`

## 🔄 Data Flow

1. **Trade Creation**: Trade posted to `/trades` endpoint
2. **Event Publishing**: `TradeCreated` event published to Kafka
3. **Risk Processing**: Risk engine processes trade and calculates exposures
4. **Limit Checking**: System checks against configured risk limits
5. **Alert Generation**: Limit breaches published as `LimitBreached` events
6. **Real-time Updates**: Dashboard receives alerts via WebSocket

## 🎯 Risk Limit Types

- **Position Limit**: Maximum position size per symbol
- **PnL Stop Loss**: Daily loss threshold
- **Counterparty Exposure**: Maximum exposure to any counterparty
- **Concentration Limit**: Maximum percentage in any single symbol

## 🚨 Alert Severity Levels

- **LOW**: Minor limit exceedance
- **MEDIUM**: Moderate limit exceedance
- **HIGH**: Significant limit exceedance
- **CRITICAL**: Severe limit exceedance

## 🛡️ Security Notes

- This is a demo system - not production ready
- H2 database is in-memory and resets on restart
- No authentication/authorization implemented
- Kafka topics are not secured

## 🔍 Troubleshooting

### Common Issues

1. **Kafka Connection Failed**
   - Ensure Kafka is running on localhost:9092
   - Check if Kafka topics exist

2. **WebSocket Connection Failed**
   - Verify application is running on port 8080
   - Check browser console for errors

3. **No Alerts Generated**
   - Verify risk limits are configured correctly
   - Check if trades are being processed

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/spring.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is for educational/demo purposes.

## 🆘 Support

For issues or questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify configuration settings
4. Ensure all prerequisites are met

## ✨ Real-Time Features

Sentinel provides **automatic real-time updates** without requiring manual refresh:

### 🔄 Automatic Updates
- **Trades**: New trades appear instantly via WebSocket
- **Positions**: Position changes update immediately when trades execute
- **Risk Alerts**: New alerts appear in real-time when limits are breached
- **Market Data**: Refreshes automatically every 30 seconds
- **Risk Metrics**: Recalculates automatically when alerts/positions change

### 🎯 WebSocket Integration
- **STOMP over SockJS**: Full-duplex communication with Spring Boot
- **Auto-reconnection**: Handles connection drops gracefully
- **Topic subscriptions**: `/topic/trades`, `/topic/positions`, `/topic/alerts`
- **Real-time status**: Header shows connection status (Connected/Disconnected)

### 🔧 Manual Refresh (Optional)
Each dashboard section still includes refresh buttons (↻) for manual updates:
- Force fresh market data (bypasses cache)
- Refresh specific sections on demand
- Visual feedback with spinning icons

## 🏃‍♂️ Quick Start
