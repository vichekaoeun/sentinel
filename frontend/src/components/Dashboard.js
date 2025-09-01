import React, { useState, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import { apiService } from '../services/apiService';
import Header from './Header';
import AlertsCard from './AlertsCard';
import PositionsCard from './PositionsCard';
import MarketDataCard from './MarketDataCard';
import TradesCard from './TradesCard';
import RiskMetricsCard from './RiskMetricsCard';
import SentinelLogo from './SentinelLogo';
import { Activity, TrendingUp, AlertTriangle, Users, DollarSign } from 'lucide-react';

const Dashboard = () => {
  const [alerts, setAlerts] = useState([]);
  const [positions, setPositions] = useState([]);
  const [marketData, setMarketData] = useState([]);
  const [recentTrades, setRecentTrades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const { isConnected, subscribe, unsubscribe } = useWebSocket();

  // Set page title
  useEffect(() => {
    document.title = 'Sentinel - Risk Management System';
  }, []);

  // Load initial data
  useEffect(() => {
    const loadInitialData = async () => {
      try {
        setLoading(true);
        const [alertsRes, positionsRes, marketRes, tradesRes] = await Promise.all([
          apiService.getAlerts(),
          apiService.getPositions(),
          apiService.getMarketOverview(),
          apiService.getTrades(),
        ]);

        setAlerts(alertsRes.data);
        setPositions(positionsRes.data);
        setMarketData(marketRes.data);
        setRecentTrades(tradesRes.data ? tradesRes.data.slice(0, 10) : []);
      } catch (err) {
        setError('Failed to load initial data');
        console.error('Error loading initial data:', err);
      } finally {
        setLoading(false);
      }
    };

    loadInitialData();
  }, []);

  // WebSocket subscriptions for real-time updates
  useEffect(() => {
    if (isConnected) {
      // Subscribe to real-time alerts
      subscribe('/topic/alerts', (newAlert) => {
        setAlerts(prev => [newAlert, ...prev]);
      });

      // Subscribe to real-time positions
      subscribe('/topic/positions', (updatedPositions) => {
        setPositions(updatedPositions);
      });

      // Subscribe to real-time trades
      subscribe('/topic/trades', (newTrade) => {
        setRecentTrades(prev => [newTrade, ...prev.slice(0, 9)]); // Keep only 10 most recent
      });

      return () => {
        unsubscribe('/topic/alerts');
        unsubscribe('/topic/positions');
        unsubscribe('/topic/trades');
      };
    }
  }, [isConnected, subscribe, unsubscribe]);

  // Automatic market data refresh every 30 seconds (since it's cached)
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const response = await apiService.getMarketOverview();
        setMarketData(response.data);
      } catch (err) {
        console.error('Error auto-refreshing market data:', err);
      }
    }, 30000); // Refresh every 30 seconds

    return () => clearInterval(interval);
  }, []);

  const handleAcknowledgeAlert = async (alertId) => {
    try {
      await apiService.acknowledgeAlert(alertId);
      setAlerts(prev => prev.filter(alert => alert.id !== alertId));
    } catch (err) {
      console.error('Error acknowledging alert:', err);
    }
  };

  const handleRefreshMarketData = async () => {
    try {
      const response = await apiService.getMarketOverviewRefresh();
      setMarketData(response.data);
    } catch (err) {
      console.error('Error refreshing market data:', err);
    }
  };

  const handleRefreshTrades = async () => {
    try {
      const response = await apiService.getTrades();
      setRecentTrades(response.data ? response.data.slice(0, 10) : []);
    } catch (err) {
      console.error('Error refreshing trades:', err);
    }
  };

  const handleRefreshAlerts = async () => {
    try {
      const response = await apiService.getAlerts();
      setAlerts(response.data);
    } catch (err) {
      console.error('Error refreshing alerts:', err);
    }
  };

  const handleRefreshPositions = async () => {
    try {
      const response = await apiService.getPositions();
      setPositions(response.data);
    } catch (err) {
      console.error('Error refreshing positions:', err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          {/* Loading Logo */}
          <div className="w-16 h-16 mx-auto mb-4">
            <SentinelLogo size={64} className="animate-pulse" />
          </div>
          <p className="text-gray-600">Loading Sentinel Dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <AlertTriangle className="h-12 w-12 text-danger-500 mx-auto" />
          <p className="mt-4 text-gray-600">{error}</p>
          <button 
            onClick={() => window.location.reload()} 
            className="mt-4 btn btn-primary"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header isConnected={isConnected} />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {/* Alerts Card */}
          <AlertsCard 
            alerts={alerts} 
            onAcknowledge={handleAcknowledgeAlert} 
            onRefresh={handleRefreshAlerts}
          />

          {/* Positions Card */}
          <PositionsCard positions={positions} onRefresh={handleRefreshPositions} />

          {/* Risk Metrics Card */}
          <RiskMetricsCard 
            alerts={alerts} 
            positions={positions} 
            onRefresh={() => {
              handleRefreshAlerts();
              handleRefreshPositions();
            }}
          />

          {/* Market Data Card */}
          <MarketDataCard 
            marketData={marketData} 
            onRefresh={handleRefreshMarketData} 
          />

          {/* Trades Card */}
          <TradesCard 
            trades={recentTrades} 
            onRefresh={handleRefreshTrades} 
          />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
