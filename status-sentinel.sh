#!/bin/bash

# Sentinel Risk Management System - Status Script
# This script checks the status of all Sentinel services

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}📊 Sentinel Risk Management System Status${NC}"
echo "================================================"
echo ""

# Check Spring Boot Backend
echo -n "🔧 Spring Boot Backend: "
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}RUNNING${NC} (Port 8080)"
    echo "   URL: http://localhost:8080"
else
    echo -e "${RED}STOPPED${NC}"
fi

# Check React Frontend (prefer 3001, fallback to 3000)
echo -n "⚛️  React Frontend: "
if lsof -Pi :3001 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}RUNNING${NC} (Port 3001)"
    echo "   URL: http://localhost:3001"
elif lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    # Check if it's actually our React app or something else
    if lsof -Pi :3000 | grep -q "node.*react"; then
        echo -e "${GREEN}RUNNING${NC} (Port 3000)"
        echo "   URL: http://localhost:3000"
    else
        echo -e "${YELLOW}PORT 3000 OCCUPIED${NC} (by Grafana or other service)"
        echo "   React will use port 3001"
    fi
else
    echo -e "${RED}STOPPED${NC}"
fi

# Check Kafka
echo -n "📨 Kafka: "
if pgrep -f "kafka" >/dev/null; then
    echo -e "${GREEN}RUNNING${NC}"
else
    echo -e "${YELLOW}NOT RUNNING${NC} (Optional)"
fi

echo ""
echo -e "${BLUE}Quick Commands:${NC}"
echo "  Start:  ./start-sentinel.sh"
echo "  Stop:   ./stop-sentinel.sh"
echo "  Status: ./status-sentinel.sh"
