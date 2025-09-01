import React, { useState } from 'react';
import { Users, TrendingUp, TrendingDown, RefreshCw } from 'lucide-react';

const PositionsCard = ({ positions, onRefresh }) => {
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

  const getPositionColor = (quantity) => {
    if (quantity > 0) return 'text-green-600';
    if (quantity < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  const getPositionIcon = (quantity) => {
    if (quantity > 0) return <TrendingUp className="h-4 w-4 text-green-500" />;
    if (quantity < 0) return <TrendingDown className="h-4 w-4 text-red-500" />;
    return <Users className="h-4 w-4 text-gray-500" />;
  };

  // Group positions by trader
  const positionsByTrader = positions.reduce((acc, position) => {
    if (!acc[position.trader]) {
      acc[position.trader] = [];
    }
    acc[position.trader].push(position);
    return acc;
  }, {});

  // Calculate total exposure by trader
  const traderExposure = Object.entries(positionsByTrader).map(([trader, traderPositions]) => {
    const totalExposure = traderPositions.reduce((sum, pos) => {
      // Estimate value based on quantity (this would be better with real market prices)
      const estimatedValue = Math.abs(pos.quantity) * 100; // Rough estimate
      return sum + estimatedValue;
    }, 0);

    return {
      trader,
      positions: traderPositions,
      totalExposure,
      positionCount: traderPositions.length
    };
  });

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900 flex items-center">
          <Users className="h-5 w-5 text-blue-500 mr-2" />
          Trader Positions
        </h2>
        <div className="flex items-center space-x-2">
          <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
            {positions.length} Positions
          </span>
          {onRefresh && (
            <button
              onClick={handleRefresh}
              disabled={isRefreshing}
              className={`p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 ${
                isRefreshing ? 'opacity-50 cursor-not-allowed' : ''
              }`}
              title="Refresh Positions"
            >
              <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
            </button>
          )}
        </div>
      </div>

      <div className="space-y-4 max-h-96 overflow-y-auto">
        {traderExposure.length === 0 ? (
          <div className="text-center py-8">
            <Users className="h-12 w-12 text-gray-400 mx-auto mb-2" />
            <p className="text-gray-500">No positions found</p>
          </div>
        ) : (
          traderExposure.map(({ trader, positions: traderPositions, totalExposure, positionCount }) => (
            <div key={trader} className="border rounded-lg p-4">
              <div className="flex items-center justify-between mb-3">
                <h3 className="font-medium text-gray-900">{trader}</h3>
                <div className="text-right">
                  <p className="text-sm text-gray-500">Total Exposure</p>
                  <p className="font-semibold text-gray-900">
                    {formatCurrency(totalExposure)}
                  </p>
                </div>
              </div>

              <div className="space-y-2">
                {traderPositions.slice(0, 5).map((position) => (
                  <div key={position.id} className="flex items-center justify-between text-sm">
                    <div className="flex items-center space-x-2">
                      {getPositionIcon(position.quantity)}
                      <span className="font-medium">{position.symbol}</span>
                    </div>
                    <span className={`font-semibold ${getPositionColor(position.quantity)}`}>
                      {position.quantity > 0 ? '+' : ''}{position.quantity.toLocaleString()}
                    </span>
                  </div>
                ))}
                
                {traderPositions.length > 5 && (
                  <div className="text-xs text-gray-500 text-center pt-2 border-t">
                    +{traderPositions.length - 5} more positions
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      <div className="mt-4 pt-4 border-t border-gray-200">
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-gray-500">Total Traders</p>
            <p className="font-semibold text-gray-900">{traderExposure.length}</p>
          </div>
          <div>
            <p className="text-gray-500">Total Positions</p>
            <p className="font-semibold text-gray-900">{positions.length}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PositionsCard;
