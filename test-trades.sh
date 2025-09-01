#!/bin/bash

# Test script for Sentinel Trade Capture System
# Make sure the application is running on localhost:8080

echo "🚀 Testing Sentinel Trade Capture System"
echo "========================================"

# Test 1: Create a BUY trade
echo "📈 Creating BUY trade for AAPL..."
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

echo -e "\n\n"

# Test 2: Create a SELL trade
echo "📉 Creating SELL trade for MSFT..."
curl -X POST http://localhost:8080/trades \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "bob",
    "symbol": "MSFT",
    "quantity": 50,
    "price": 300.25,
    "side": "SELL",
    "counterparty": "broker2"
  }'

echo -e "\n\n"

# Test 3: Create another BUY trade (should trigger position limit)
echo "🚨 Creating large BUY trade for GOOGL (should trigger limits)..."
curl -X POST http://localhost:8080/trades \
  -H "Content-Type: application/json" \
  -d '{
    "trader": "alice",
    "symbol": "GOOGL",
    "quantity": 2000,
    "price": 2800.00,
    "side": "BUY",
    "counterparty": "broker1"
  }'

echo -e "\n\n"

# Test 4: Check alerts
echo "🔍 Checking for alerts..."
curl -X GET http://localhost:8080/api/alerts

echo -e "\n\n"

# Test 5: Check positions
echo "📊 Checking positions..."
curl -X GET http://localhost:8080/api/positions

echo -e "\n\n"
echo "✅ Test completed! Check the dashboard at http://localhost:8080"
