import React, { useState } from 'react';
import { DollarSign, ArrowUp, ArrowDown, RefreshCw } from 'lucide-react';

const TradesCard = ({ trades, onRefresh }) => {
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

  const getSideColor = (side) => {
    return side === 'BUY' ? 'text-green-600' : 'text-red-600';
  };

  const getSideIcon = (side) => {
    return side === 'BUY' ? 
      <ArrowUp className="h-4 w-4 text-green-500" /> : 
      <ArrowDown className="h-4 w-4 text-red-500" />;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const calculateTradeValue = (trade) => {
    return trade.quantity * trade.price;
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 flex items-center">
          <DollarSign className="h-5 w-5 text-purple-500 mr-2" />
          Recent Trades
        </h2>
        <button
          onClick={handleRefresh}
          disabled={isRefreshing}
          className={`p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 ${
            isRefreshing ? 'opacity-50 cursor-not-allowed' : ''
          }`}
          title="Refresh Trades"
        >
          <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
        </button>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {trades.length === 0 ? (
          <div className="text-center py-8">
            <DollarSign className="h-12 w-12 text-gray-400 mx-auto mb-2" />
            <p className="text-gray-500">No recent trades</p>
          </div>
        ) : (
          trades.map((trade) => (
            <div key={trade.id} className="border rounded-lg p-4">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center space-x-2">
                  {getSideIcon(trade.side)}
                  <span className="font-semibold text-gray-900">{trade.symbol}</span>
                  <span className={`text-sm font-medium ${getSideColor(trade.side)}`}>
                    {trade.side}
                  </span>
                </div>
                <span className="text-lg font-bold text-gray-900">
                  {formatCurrency(trade.price)}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-4 text-sm mb-2">
                <div>
                  <p className="text-gray-500">Trader</p>
                  <p className="font-medium text-gray-900">{trade.trader}</p>
                </div>
                <div>
                  <p className="text-gray-500">Quantity</p>
                  <p className="font-medium text-gray-900">
                    {trade.quantity.toLocaleString()}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 text-sm mb-2">
                <div>
                  <p className="text-gray-500">Value</p>
                  <p className="font-semibold text-gray-900">
                    {formatCurrency(calculateTradeValue(trade))}
                  </p>
                </div>
                <div>
                  <p className="text-gray-500">Counterparty</p>
                  <p className="font-medium text-gray-900">{trade.counterparty || 'N/A'}</p>
                </div>
              </div>

              <div className="text-xs text-gray-500 border-t pt-2">
                {formatDate(trade.timestamp)}
              </div>
            </div>
          ))
        )}
      </div>

      <div className="mt-4 pt-4 border-t border-gray-200">
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-gray-500">Total Trades</p>
            <p className="font-semibold text-gray-900">{trades.length}</p>
          </div>
          <div>
            <p className="text-gray-500">Buy Orders</p>
            <p className="font-semibold text-green-600">
              {trades.filter(t => t.side === 'BUY').length}
            </p>
          </div>
          <div>
            <p className="text-gray-500">Sell Orders</p>
            <p className="font-semibold text-red-600">
              {trades.filter(t => t.side === 'SELL').length}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TradesCard;
