#!/bin/bash

echo "🚀 Testing Sentinel Real-Time Updates"
echo "====================================="

# Wait for backend to be ready
echo "1. Waiting for backend to be ready..."
sleep 5

# Test creating a trade (this should trigger real-time updates)
echo -e "\n2. Creating a test trade..."
TRADE_RESPONSE=$(curl -s -X POST http://localhost:8080/trades \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "TestTrader",
    "symbol": "AAPL",
    "side": "BUY",
    "quantity": 100,
    "price": 150.00,
    "counterparty": "TestBank"
  }')

echo "Trade created: $TRADE_RESPONSE"

# Test creating another trade
echo -e "\n3. Creating another test trade..."
TRADE_RESPONSE2=$(curl -s -X POST http://localhost:8080/trades \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "TestTrader2",
    "symbol": "GOOGL",
    "side": "SELL",
    "quantity": 50,
    "price": 2800.00,
    "counterparty": "TestBank2"
  }')

echo "Trade created: $TRADE_RESPONSE2"

# Check current positions
echo -e "\n4. Checking updated positions..."
curl -s http://localhost:8080/api/positions | jq '.[] | {trader, symbol, quantity}' 2>/dev/null || echo "No positions found"

# Check current trades
echo -e "\n5. Checking updated trades..."
curl -s http://localhost:8080/trades | jq '.[] | {trader, symbol, side, quantity}' 2>/dev/null || echo "No trades found"

echo -e "\n🎯 Real-time updates should now be visible in your dashboard!"
echo "   - New trades should appear immediately"
echo "   - Positions should update automatically"
echo "   - Market data refreshes every 30 seconds"
echo "   - No manual refresh needed!"
