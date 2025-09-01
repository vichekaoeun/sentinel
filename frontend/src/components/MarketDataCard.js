import React, { useState } from 'react';
import { TrendingUp, TrendingDown, RefreshCw } from 'lucide-react';

const MarketDataCard = ({ marketData, onRefresh }) => {
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    try {
      await onRefresh();
    } finally {
      setIsRefreshing(false);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  const getChangeColor = (change) => {
    if (change > 0) return 'text-green-600';
    if (change < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  const getChangeIcon = (change) => {
    if (change > 0) return <TrendingUp className="h-4 w-4 text-green-500" />;
    if (change < 0) return <TrendingDown className="h-4 w-4 text-red-500" />;
    return null;
  };

  const formatChange = (change, changePercent) => {
    const sign = change >= 0 ? '+' : '';
    return `${sign}${change.toFixed(2)} (${sign}${changePercent.toFixed(2)}%)`;
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 flex items-center">
          <TrendingUp className="h-5 w-5 text-green-500 mr-2" />
          Market Data
        </h2>
        <button
          onClick={handleRefresh}
          disabled={isRefreshing}
          className={`p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 ${
            isRefreshing ? 'opacity-50 cursor-not-allowed' : ''
          }`}
          title="Refresh Market Data"
        >
          <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
        </button>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {marketData.length === 0 ? (
          <div className="text-center py-8">
            <TrendingUp className="h-12 w-12 text-gray-400 mx-auto mb-2" />
            <p className="text-gray-500">No market data available</p>
          </div>
        ) : (
          marketData.map((quote) => (
            <div key={quote.symbol} className="border rounded-lg p-4">
              <div className="flex items-center justify-between mb-2">
                <h3 className="font-semibold text-gray-900">{quote.symbol}</h3>
                <span className="text-lg font-bold text-gray-900">
                  {formatCurrency(quote.price)}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  {getChangeIcon(quote.change)}
                  <span className={`text-sm font-medium ${getChangeColor(quote.change)}`}>
                    {formatChange(quote.change, quote.changePercent)}
                  </span>
                </div>

                <div className="text-xs text-gray-500">
                  {quote.expired ? (
                    <span className="text-gray-400">Cached</span>
                  ) : (
                    <span className="text-green-600">Live</span>
                  )}
                </div>
              </div>

              {quote.priceData?.timestamp && (
                <div className="mt-2 text-xs text-gray-500">
                  Last updated: {new Date(quote.priceData.timestamp * 1000).toLocaleTimeString()}
                </div>
              )}
            </div>
          ))
        )}
      </div>

      <div className="mt-4 pt-4 border-t border-gray-200">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-gray-500">Symbols</p>
            <p className="font-semibold text-gray-900">{marketData.length}</p>
          </div>
          <div>
            <p className="text-gray-500">Status</p>
            <p className="font-semibold text-green-600">Live</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MarketDataCard;
