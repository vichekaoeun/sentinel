import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);

export const apiService = {
  // Health check
  getHealth: () => apiClient.get('/api/health'),

  // Alerts
  getAlerts: () => apiClient.get('/api/alerts'),
  acknowledgeAlert: (alertId) => apiClient.put(`/api/alerts/${alertId}/acknowledge`),

  // Positions
  getPositions: () => apiClient.get('/api/positions'),

  // Trades
  getTrades: () => apiClient.get('/trades'),
  createTrade: (tradeData) => apiClient.post('/trades', tradeData),

  // Live Trading
  getMarketOverview: () => apiClient.get('/api/live-trading/market-overview'),
  getMarketOverviewRefresh: () => apiClient.get('/api/live-trading/market-overview/refresh'),
  getLivePrice: (symbol) => apiClient.get(`/api/live-trading/price/${symbol}`),
  executeLiveTrade: (tradeData) => apiClient.post('/api/live-trading/execute', tradeData),
  getRecentTrades: (symbol) => apiClient.get(`/api/live-trading/trades/${symbol}`),
  getAvailableSymbols: () => apiClient.get('/api/live-trading/symbols'),
};

export default apiService;
