#!/bin/bash

echo "🚀 Starting Sentinel Risk Management System"
echo "=========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start Kafka and Zookeeper
echo "📦 Starting Kafka and Zookeeper..."
docker-compose up -d

# Wait for Kafka to be ready
echo "⏳ Waiting for Kafka to be ready..."
sleep 30

# Check if Kafka is running
echo "🔍 Checking Kafka status..."
if curl -s http://localhost:9092 > /dev/null 2>&1; then
    echo "✅ Kafka is running"
else
    echo "⚠️  Kafka might not be ready yet, but continuing..."
fi

# Build and start the application
echo "🔨 Building and starting the application..."
mvn clean install -DskipTests
mvn spring-boot:run &

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 20

# Check if application is running
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "✅ Application is running on http://localhost:8080"
    echo "🌐 Dashboard: http://localhost:8080"
    echo "🗄️  H2 Console: http://localhost:8080/h2-console"
    echo ""
    echo "🧪 To test the system, run: ./test-trades.sh"
    echo "🛑 To stop, press Ctrl+C and run: docker-compose down"
else
    echo "❌ Application failed to start. Check logs above."
    docker-compose down
    exit 1
fi

# Keep the script running
wait
