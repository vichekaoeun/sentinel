import React, { useState } from 'react';
import { AlertTriangle, X, CheckCircle, RefreshCw } from 'lucide-react';

const AlertsCard = ({ alerts, onAcknowledge, onRefresh }) => {
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

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'CRITICAL':
        return 'bg-red-50 border-red-200 text-red-800';
      case 'HIGH':
        return 'bg-orange-50 border-orange-200 text-orange-800';
      case 'MEDIUM':
        return 'bg-yellow-50 border-yellow-200 text-yellow-800';
      case 'LOW':
        return 'bg-blue-50 border-blue-200 text-blue-800';
      default:
        return 'bg-gray-50 border-gray-200 text-gray-800';
    }
  };

  const getSeverityIcon = (severity) => {
    switch (severity) {
      case 'CRITICAL':
        return <AlertTriangle className="h-4 w-4 text-red-500" />;
      case 'HIGH':
        return <AlertTriangle className="h-4 w-4 text-orange-500" />;
      case 'MEDIUM':
        return <AlertTriangle className="h-4 w-4 text-yellow-500" />;
      case 'LOW':
        return <AlertTriangle className="h-4 w-4 text-blue-500" />;
      default:
        return <AlertTriangle className="h-4 w-4 text-gray-500" />;
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

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 flex items-center">
          <AlertTriangle className="h-5 w-5 text-red-500 mr-2" />
          Risk Alerts
        </h2>
        <div className="flex items-center space-x-2">
          <span className="bg-red-100 text-red-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
            {alerts.length} Active
          </span>
          {onRefresh && (
            <button
              onClick={handleRefresh}
              disabled={isRefreshing}
              className={`p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 ${
                isRefreshing ? 'opacity-50 cursor-not-allowed' : ''
              }`}
              title="Refresh Alerts"
            >
              <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
            </button>
          )}
        </div>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {alerts.length === 0 ? (
          <div className="text-center py-8">
            <CheckCircle className="h-12 w-12 text-green-500 mx-auto mb-2" />
            <p className="text-gray-500">No active alerts</p>
          </div>
        ) : (
          alerts.slice(0, 10).map((alert) => (
            <div
              key={alert.id}
              className={`border rounded-lg p-3 ${getSeverityColor(alert.severity)}`}
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center space-x-2 mb-1">
                    {getSeverityIcon(alert.severity)}
                    <span className="text-sm font-medium">
                      {alert.limitType.replace(/_/g, ' ')}
                    </span>
                    <span className="text-xs bg-white bg-opacity-50 px-1.5 py-0.5 rounded">
                      {alert.severity}
                    </span>
                  </div>
                  
                  <div className="text-sm space-y-1">
                    <p><strong>Trader:</strong> {alert.trader}</p>
                    <p><strong>Symbol:</strong> {alert.symbol}</p>
                    <p><strong>Value:</strong> {formatCurrency(alert.actualValue)}</p>
                    <p><strong>Threshold:</strong> {formatCurrency(alert.threshold)}</p>
                    <p className="text-xs text-gray-600">
                      {formatDate(alert.occurredAt)}
                    </p>
                  </div>
                </div>
                
                <button
                  onClick={() => onAcknowledge(alert.id)}
                  className="text-gray-400 hover:text-gray-600 p-1"
                  title="Acknowledge Alert"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {alerts.length > 10 && (
        <div className="mt-4 text-center">
          <p className="text-sm text-gray-500">
            Showing 10 of {alerts.length} alerts
          </p>
        </div>
      )}
    </div>
  );
};

export default AlertsCard;
