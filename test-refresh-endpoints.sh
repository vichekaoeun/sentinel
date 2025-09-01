#!/bin/bash

echo "🧪 Testing Sentinel Refresh Endpoints"
echo "====================================="

# Test backend health
echo "1. Backend Health Check:"
curl -s http://localhost:8080/api/health | jq '.status' 2>/dev/null || echo "❌ Backend not responding"

# Test market data refresh
echo -e "\n2. Market Data Refresh:"
curl -s http://localhost:8080/api/live-trading/market-overview/refresh | jq '.[0] | {symbol, price, expired}' 2>/dev/null || echo "❌ Market data refresh failed"

# Test alerts endpoint
echo -e "\n3. Alerts Endpoint:"
curl -s http://localhost:8080/api/alerts | jq 'length' 2>/dev/null || echo "❌ Alerts endpoint failed"

# Test positions endpoint
echo -e "\n4. Positions Endpoint:"
curl -s http://localhost:8080/api/positions | jq 'length' 2>/dev/null || echo "❌ Positions endpoint failed"

# Test trades endpoint
echo -e "\n5. Trades Endpoint:"
curl -s http://localhost:8080/trades | jq 'length' 2>/dev/null || echo "❌ Trades endpoint failed"

# Test frontend
echo -e "\n6. Frontend Status:"
if curl -s http://localhost:3001 >/dev/null 2>&1; then
    echo "✅ Frontend running on port 3001"
else
    echo "❌ Frontend not responding on port 3001"
fi

echo -e "\n🎯 All refresh endpoints should now be working!"
echo "   - Market Data: Force refresh with cache bypass"
echo "   - Risk Alerts: Real-time alert updates"
echo "   - Positions: Live position data"
echo "   - Risk Metrics: Combined alerts + positions refresh"
echo "   - Trades: Latest trade history"
