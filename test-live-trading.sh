#!/bin/bash

# Test script for Live Trading with Real Market Data
# Make sure the application is running on localhost:8080

echo "🚀 Testing Sentinel Live Trading System"
echo "========================================"

# Test 1: Get live market overview
echo "📊 Getting live market overview..."
curl -X GET http://localhost:8080/api/live-trading/market-overview | jq '.[0:3]'

echo -e "\n\n"

# Test 2: Get live price for AAPL
echo "🍎 Getting live price for AAPL..."
curl -X GET http://localhost:8080/api/live-trading/price/AAPL | jq '.'

echo -e "\n\n"

# Test 3: Execute a live trade with real pricing
echo "💹 Executing live trade for AAPL..."
curl -X POST http://localhost:8080/api/live-trading/execute \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "alice",
    "symbol": "AAPL",
    "quantity": 100,
    "side": "BUY",
    "counterparty": "broker1"
  }' | jq '.'

echo -e "\n\n"

# Test 4: Get recent trades for AAPL
echo "📈 Getting recent trades for AAPL..."
curl -X GET http://localhost:8080/api/live-trading/trades/AAPL | jq '.[0:3]'

echo -e "\n\n"

# Test 5: Execute another live trade for MSFT
echo "💻 Executing live trade for MSFT..."
curl -X POST http://localhost:8080/api/live-trading/execute \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "bob",
    "symbol": "MSFT",
    "quantity": 50,
    "side": "SELL",
    "counterparty": "broker2"
  }' | jq '.'

echo -e "\n\n"

# Test 6: Check alerts (should show new trades)
echo "🚨 Checking for new alerts..."
curl -X GET http://localhost:8080/api/alerts | jq '.[0:3]'

echo -e "\n\n"

# Test 7: Check positions
echo "📊 Checking updated positions..."
curl -X GET http://localhost:8080/api/positions | jq '.'

echo -e "\n\n"
echo "✅ Live trading test completed! Check the dashboard at http://localhost:8080"
echo "🌐 The system is now using REAL market data from Finnhub!"
