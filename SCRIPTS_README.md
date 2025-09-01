# Sentinel Risk Management System - Management Scripts

This directory contains scripts to easily manage the Sentinel Risk Management System.

## 🚀 Quick Start

### Start Everything (Recommended)
```bash
./start-sentinel.sh
```

This single command will:
- ✅ Clean up any existing processes
- ✅ Start the Spring Boot backend on port 8080
- ✅ Start the React frontend on port 3001
- ✅ Wait for both services to be ready
- ✅ Display access URLs and status

### Stop Everything
```bash
./stop-sentinel.sh
```

### Check Status
```bash
./status-sentinel.sh
```

## 📊 Access Points

Once started, you can access:

- **Dashboard**: http://localhost:3001
- **Backend API**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console

## 🔧 Manual Commands (if needed)

### Backend Only
```bash
cd /Users/vichekaoeun/Projects/sentinel/sentinel
mvn spring-boot:run
```

### Frontend Only
```bash
cd /Users/vichekaoeun/Projects/sentinel/sentinel/frontend
PORT=3001 npm start
```

## 📝 Logs

When using the start script, logs are saved to:
- `backend.log` - Spring Boot backend logs
- `frontend.log` - React frontend logs

View logs with:
```bash
tail -f backend.log    # Backend logs
tail -f frontend.log   # Frontend logs
```

## 🛠️ Troubleshooting

### Port Conflicts
The scripts automatically handle port conflicts by:
- Killing existing processes on ports 8080 and 3001
- Using port 3001 for React (since 3000 is often occupied by Grafana)

### Dependencies
- **Java 21+** and **Maven 3.6+** for backend
- **Node.js 16+** and **npm** for frontend
- **Kafka** (optional, for full functionality)

### Common Issues
1. **Port 8080 in use**: The script will automatically clean up
2. **Port 3000 in use**: React will use port 3001 instead
3. **Missing dependencies**: Run `npm install` in the frontend directory
4. **Kafka not running**: System will work but with limited functionality
