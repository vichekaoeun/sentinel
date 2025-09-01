import React, { useState } from 'react';
import { Shield, AlertTriangle, TrendingUp, Activity, RefreshCw } from 'lucide-react';

const RiskMetricsCard = ({ alerts, positions, onRefresh }) => {
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    if (onRefresh) {
      setIsRefreshing(true);
      try {
        await onRefresh();
      } finally {
        setIsRefreshing(false);
      }
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const calculateRiskMetrics = () => {
    const criticalAlerts = alerts.filter(alert => alert.severity === 'CRITICAL').length;
    const highAlerts = alerts.filter(alert => alert.severity === 'HIGH').length;
    const mediumAlerts = alerts.filter(alert => alert.severity === 'MEDIUM').length;
    const lowAlerts = alerts.filter(alert => alert.severity === 'LOW').length;

    const totalExposure = positions.reduce((sum, position) => {
      // Estimate value based on quantity (this would be better with real market prices)
      const estimatedValue = Math.abs(position.quantity) * 100; // Rough estimate
      return sum + estimatedValue;
    }, 0);

    const uniqueTraders = new Set(positions.map(pos => pos.trader)).size;
    const uniqueSymbols = new Set(positions.map(pos => pos.symbol)).size;

    return {
      criticalAlerts,
      highAlerts,
      mediumAlerts,
      lowAlerts,
      totalAlerts: alerts.length,
      totalExposure,
      uniqueTraders,
      uniqueSymbols,
      totalPositions: positions.length
    };
  };

  const metrics = calculateRiskMetrics();

  const getRiskLevel = () => {
    if (metrics.criticalAlerts > 0) return { level: 'CRITICAL', color: 'text-red-600', bgColor: 'bg-red-50' };
    if (metrics.highAlerts > 0) return { level: 'HIGH', color: 'text-orange-600', bgColor: 'bg-orange-50' };
    if (metrics.mediumAlerts > 0) return { level: 'MEDIUM', color: 'text-yellow-600', bgColor: 'bg-yellow-50' };
    if (metrics.lowAlerts > 0) return { level: 'LOW', color: 'text-blue-600', bgColor: 'bg-blue-50' };
    return { level: 'SAFE', color: 'text-green-600', bgColor: 'bg-green-50' };
  };

  const riskLevel = getRiskLevel();

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 flex items-center">
          <Shield className="h-5 w-5 text-indigo-500 mr-2" />
          Risk Metrics
        </h2>
        <div className="flex items-center space-x-2">
          <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${riskLevel.bgColor} ${riskLevel.color}`}>
            {riskLevel.level}
          </span>
          {onRefresh && (
            <button
              onClick={handleRefresh}
              disabled={isRefreshing}
              className={`p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 ${
                isRefreshing ? 'opacity-50 cursor-not-allowed' : ''
              }`}
              title="Refresh Risk Metrics"
            >
              <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
            </button>
          )}
        </div>
      </div>

      {/* Risk Level Indicator */}
      <div className={`${riskLevel.bgColor} rounded-lg p-4 mb-4`}>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-gray-600">Current Risk Level</p>
            <p className={`text-xl font-bold ${riskLevel.color}`}>{riskLevel.level}</p>
          </div>
          <AlertTriangle className={`h-8 w-8 ${riskLevel.color}`} />
        </div>
      </div>

      {/* Alert Breakdown */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="text-center p-3 bg-red-50 rounded-lg">
          <p className="text-2xl font-bold text-red-600">{metrics.criticalAlerts}</p>
          <p className="text-xs text-red-600">Critical</p>
        </div>
        <div className="text-center p-3 bg-orange-50 rounded-lg">
          <p className="text-2xl font-bold text-orange-600">{metrics.highAlerts}</p>
          <p className="text-xs text-orange-600">High</p>
        </div>
        <div className="text-center p-3 bg-yellow-50 rounded-lg">
          <p className="text-2xl font-bold text-yellow-600">{metrics.mediumAlerts}</p>
          <p className="text-xs text-yellow-600">Medium</p>
        </div>
        <div className="text-center p-3 bg-blue-50 rounded-lg">
          <p className="text-2xl font-bold text-blue-600">{metrics.lowAlerts}</p>
          <p className="text-xs text-blue-600">Low</p>
        </div>
      </div>

      {/* Exposure Metrics */}
      <div className="space-y-3 mb-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Activity className="h-4 w-4 text-gray-500" />
            <span className="text-sm text-gray-600">Total Exposure</span>
          </div>
          <span className="font-semibold text-gray-900">
            {formatCurrency(metrics.totalExposure)}
          </span>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <TrendingUp className="h-4 w-4 text-gray-500" />
            <span className="text-sm text-gray-600">Active Traders</span>
          </div>
          <span className="font-semibold text-gray-900">{metrics.uniqueTraders}</span>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Shield className="h-4 w-4 text-gray-500" />
            <span className="text-sm text-gray-600">Trading Symbols</span>
          </div>
          <span className="font-semibold text-gray-900">{metrics.uniqueSymbols}</span>
        </div>
      </div>

      {/* Summary Stats */}
      <div className="pt-4 border-t border-gray-200">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-gray-500">Total Alerts</p>
            <p className="font-semibold text-gray-900">{metrics.totalAlerts}</p>
          </div>
          <div>
            <p className="text-gray-500">Total Positions</p>
            <p className="font-semibold text-gray-900">{metrics.totalPositions}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RiskMetricsCard;
