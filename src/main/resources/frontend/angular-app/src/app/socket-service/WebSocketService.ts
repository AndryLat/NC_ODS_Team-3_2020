import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {WebSocketMessageHandler} from './WebSocketMessageHandler';
import {Injectable} from '@angular/core';
import {AuthService} from '../services/AuthService';

@Injectable()
export class WebSocketService {
  webSocketEndPoint: string = 'http://localhost:8081/ws';
  topic: string = '/events/logs';
  stompClient: any;
  handlers: WebSocketMessageHandler[] = [];

  constructor(private authService: AuthService) {
  }

  addHandler(webSocketMessageHandler: WebSocketMessageHandler) {
    this.handlers.push(webSocketMessageHandler);
  }

  addFileToListen(fileId: string) {
    this._connect(fileId);
  }

  removeHandler(webSocketMessageHandler: WebSocketMessageHandler) {
    let deletedHandler = this.handlers.find(deletedHandler => deletedHandler == webSocketMessageHandler);
    let index = this.handlers.indexOf(deletedHandler);
    this.handlers.splice(index, 1);
    this._disconnect();
  }

  onMessageReceived(message) {
    this.handlers.forEach(handler => {
      handler.handleMessage(message.body);
    });
  }

  private _connect(fileId: string) {
    console.log('Initialize WebSocket Connection');
    let ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    const _this = this;
    _this.stompClient.connect({'Authorization': _this.authService.getToken()}, function(frame) {
      _this.stompClient.subscribe(_this.topic + '/' + fileId, function(sdkEvent) {
        _this.onMessageReceived(sdkEvent);
      });
    });
  }

  private _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log('Disconnected');
  }
}
