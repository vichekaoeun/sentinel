import React from 'react';
import { Wifi, WifiOff, Bell, Settings } from 'lucide-react';
import SentinelLogo from './SentinelLogo';

const Header = ({ isConnected }) => {
  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo and Title */}
          <div className="flex items-center">
            <div className="flex-shrink-0">
              {/* Sentinel Logo */}
              <div className="flex items-center">
                <div className="w-10 h-10 mr-3">
                  <SentinelLogo size={40} />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-900">
                    Sentinel
                  </h1>
                  <p className="text-sm text-gray-500">Risk Management System</p>
                </div>
              </div>
            </div>
          </div>

          {/* Connection Status and Actions */}
          <div className="flex items-center space-x-4">
            {/* Connection Status */}
            <div className="flex items-center space-x-2">
              {isConnected ? (
                <>
                  <Wifi className="h-5 w-5 text-green-500" />
                  <span className="text-sm text-green-600">Connected</span>
                </>
              ) : (
                <>
                  <WifiOff className="h-5 w-5 text-red-500" />
                  <span className="text-sm text-red-600">Disconnected</span>
                </>
              )}
            </div>

            {/* Notifications */}
            <button className="p-2 text-gray-400 hover:text-gray-500">
              <Bell className="h-5 w-5" />
            </button>

            {/* Settings */}
            <button className="p-2 text-gray-400 hover:text-gray-500">
              <Settings className="h-5 w-5" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
