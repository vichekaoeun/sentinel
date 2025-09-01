#!/bin/bash

# Sentinel Risk Management System - Complete Startup Script
# This script starts both the Spring Boot backend and React frontend

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if a port is available
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 1  # Port is in use
    else
        return 0  # Port is available
    fi
}

# Function to kill processes on specific ports
kill_port() {
    local port=$1
    local pids=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pids" ]; then
        print_status "Killing processes on port $port..."
        echo $pids | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for service at $url..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" >/dev/null 2>&1; then
            print_success "Service at $url is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "Service at $url failed to start within $((max_attempts * 2)) seconds"
    return 1
}

# Cleanup function
cleanup() {
    print_status "Shutting down services..."
    
    # Kill backend
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
    fi
    
    # Kill frontend
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
    fi
    
    # Kill any remaining processes on our ports
    kill_port 8080
    kill_port 3001
    
    print_success "Cleanup completed"
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Main script
main() {
    echo "🚨 Sentinel Risk Management System"
    echo "=================================="
    echo ""
    
    # Get the project root directory
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    PROJECT_ROOT="$SCRIPT_DIR"
    
    print_status "Project root: $PROJECT_ROOT"
    
    # Check if we're in the right directory
    if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
        print_error "pom.xml not found. Please run this script from the project root directory."
        exit 1
    fi
    
    # Clean up any existing processes
    print_status "Cleaning up existing processes..."
    kill_port 8080
    kill_port 3001
    
    # Check if Kafka is running (optional)
    if ! pgrep -f "kafka" >/dev/null; then
        print_warning "Kafka doesn't appear to be running. The system may not work properly."
        print_status "To start Kafka with Docker: docker run -p 9092:9092 apache/kafka:3.6.0"
    else
        print_success "Kafka is running"
    fi
    
    # Start Spring Boot Backend
    print_status "Starting Spring Boot backend..."
    cd "$PROJECT_ROOT"
    
    # Check if port 8080 is available
    if ! check_port 8080; then
        print_error "Port 8080 is already in use. Please free up the port and try again."
        exit 1
    fi
    
    # Start backend in background
    mvn spring-boot:run > backend.log 2>&1 &
    BACKEND_PID=$!
    
    print_status "Backend started with PID: $BACKEND_PID"
    
    # Wait for backend to be ready
    if ! wait_for_service "http://localhost:8080/api/health"; then
        print_error "Backend failed to start properly"
        cleanup
        exit 1
    fi
    
    print_success "Backend is running on http://localhost:8080"
    
    # Start React Frontend
    print_status "Starting React frontend..."
    cd "$PROJECT_ROOT/frontend"
    
    # Check if frontend dependencies are installed
    if [ ! -d "node_modules" ]; then
        print_status "Installing frontend dependencies..."
        npm install
    fi
    
    # Check if port 3001 is available
    if ! check_port 3001; then
        print_error "Port 3001 is already in use. Please free up the port and try again."
        cleanup
        exit 1
    fi
    
    # Start frontend in background
    PORT=3001 npm start > frontend.log 2>&1 &
    FRONTEND_PID=$!
    
    print_status "Frontend started with PID: $FRONTEND_PID"
    
    # Wait for frontend to be ready
    if ! wait_for_service "http://localhost:3001"; then
        print_error "Frontend failed to start properly"
        cleanup
        exit 1
    fi
    
    print_success "Frontend is running on http://localhost:3001"
    
    # Display final status
    echo ""
    echo "🎉 Sentinel Risk Management System is ready!"
    echo "============================================="
    echo ""
    echo "📊 Dashboard:     http://localhost:3001"
    echo "🔧 Backend API:    http://localhost:8080"
    echo "🗄️  H2 Console:    http://localhost:8080/h2-console"
    echo ""
    echo "📝 Logs:"
    echo "   Backend:  tail -f $PROJECT_ROOT/backend.log"
    echo "   Frontend: tail -f $PROJECT_ROOT/frontend.log"
    echo ""
    echo "🛑 To stop all services, press Ctrl+C"
    echo ""
    
    # Keep the script running
    while true; do
        sleep 10
        
        # Check if processes are still running
        if ! kill -0 $BACKEND_PID 2>/dev/null; then
            print_error "Backend process died unexpectedly"
            cleanup
            exit 1
        fi
        
        if ! kill -0 $FRONTEND_PID 2>/dev/null; then
            print_error "Frontend process died unexpectedly"
            cleanup
            exit 1
        fi
    done
}

# Run the main function
main "$@"
