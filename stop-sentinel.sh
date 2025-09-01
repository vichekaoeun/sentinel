#!/bin/bash

# Sentinel Risk Management System - Stop Script
# This script stops all Sentinel services

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🛑 Stopping Sentinel Risk Management System...${NC}"

# Kill Spring Boot processes
echo "Stopping Spring Boot backend..."
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "mvn.*spring-boot" 2>/dev/null || true

# Kill React processes
echo "Stopping React frontend..."
pkill -f "react-scripts" 2>/dev/null || true
pkill -f "npm.*start" 2>/dev/null || true

# Kill processes on specific ports
echo "Cleaning up ports..."
lsof -ti:8080 | xargs kill -9 2>/dev/null || true
lsof -ti:3001 | xargs kill -9 2>/dev/null || true
lsof -ti:3000 | xargs kill -9 2>/dev/null || true

# Wait a moment for processes to fully stop
sleep 2

echo -e "${GREEN}✅ All Sentinel services stopped successfully!${NC}"
