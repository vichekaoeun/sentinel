import { useState, useEffect, useCallback } from 'react';
import websocketService from '../services/websocketService';

export const useWebSocket = () => {
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState(null);

  const connect = useCallback(() => {
    websocketService.connect(
      () => {
        setIsConnected(true);
        setError(null);
      },
      (error) => {
        setIsConnected(false);
        setError(error);
      }
    );
  }, []);

  const disconnect = useCallback(() => {
    websocketService.disconnect();
    setIsConnected(false);
  }, []);

  const subscribe = useCallback((destination, callback) => {
    return websocketService.subscribe(destination, callback);
  }, []);

  const unsubscribe = useCallback((destination) => {
    websocketService.unsubscribe(destination);
  }, []);

  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    isConnected,
    error,
    connect,
    disconnect,
    subscribe,
    unsubscribe,
  };
};
