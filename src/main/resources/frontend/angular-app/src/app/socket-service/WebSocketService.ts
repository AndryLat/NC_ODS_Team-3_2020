import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {WebSocketMessageHandler} from "./WebSocketMessageHandler";
import {Injectable} from "@angular/core";

@Injectable()
export class WebSocketService {
  webSocketEndPoint: string = 'http://localhost:8081/ws';
  topic: string = "/events/logs";
  stompClient: any;
  handlers: WebSocketMessageHandler[] = [];

  constructor(){
  }

  addHandler(webSocketMessageHandler: WebSocketMessageHandler, fileId: string){
    this.handlers.push(webSocketMessageHandler);
    if(this.handlers.length>0){
      this._connect(fileId);
    }
  }
  removeHandler(webSocketMessageHandler: WebSocketMessageHandler){
    let deletedHandler = this.handlers.find(deletedHandler => deletedHandler == webSocketMessageHandler);
    let index = this.handlers.indexOf(deletedHandler);
    this.handlers.splice(index, 1);
    if(this.handlers.length<=0){
      this._disconnect();
    }
  }

  private _connect(fileId: string) {
    console.log("Initialize WebSocket Connection");
    let ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    const _this = this;
    _this.stompClient.connect({}, function (frame) {
      _this.stompClient.subscribe(_this.topic+"/"+fileId, function (sdkEvent) {
        _this.onMessageReceived(sdkEvent);
      });
    });
  };

  private _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  onMessageReceived(message) {
    this.handlers.forEach(handler=>{
      handler.handleMessage(message.body);
    })
  }
}
