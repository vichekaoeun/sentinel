import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscriptions = new Map();
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 1000;
  }

  connect(onConnect, onError) {
    try {
      // Use SockJS + STOMP to match Spring Boot WebSocket config
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = Stomp.over(socket);
      
      // Disable debug logging
      this.stompClient.debug = () => {};
      
      this.stompClient.connect({}, 
        (frame) => {
          console.log('Connected to WebSocket via STOMP');
          this.connected = true;
          this.reconnectAttempts = 0;
          
          // Reestablish subscriptions after reconnection
          this.subscriptions.forEach((callback, destination) => {
            this.stompClient.subscribe(destination, (message) => {
              try {
                const data = JSON.parse(message.body);
                callback(data);
              } catch (error) {
                console.error('Error parsing WebSocket message:', error);
              }
            });
          });
          
          if (onConnect) onConnect();
        },
        (error) => {
          console.error('WebSocket connection error:', error);
          this.connected = false;
          if (onError) onError(error);
          this.attemptReconnect(onConnect, onError);
        }
      );

    } catch (error) {
      console.error('Error creating WebSocket connection:', error);
      if (onError) onError(error);
    }
  }

  attemptReconnect(onConnect, onError) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
      
      setTimeout(() => {
        this.connect(onConnect, onError);
      }, this.reconnectDelay * this.reconnectAttempts);
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  subscribe(destination, callback) {
    if (!this.connected || !this.stompClient) {
      console.warn('WebSocket not connected, storing subscription for later');
      this.subscriptions.set(destination, callback);
      return null;
    }

    // Store the subscription
    this.subscriptions.set(destination, callback);
    
    // Subscribe via STOMP
    const subscription = this.stompClient.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body);
        callback(data);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    });

    return subscription;
  }

  unsubscribe(destination) {
    this.subscriptions.delete(destination);
    // Note: STOMP subscriptions are automatically cleaned up when the client disconnects
  }

  send(destination, message) {
    if (this.connected && this.stompClient) {
      this.stompClient.send(destination, {}, JSON.stringify(message));
    } else {
      console.warn('WebSocket not connected, cannot send message');
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connected = false;
      this.subscriptions.clear();
    }
  }

  isConnected() {
    return this.connected;
  }
}

export default new WebSocketService();
