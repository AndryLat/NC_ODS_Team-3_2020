import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketMessageHandler} from '../../services/socket-service/WebSocketMessageHandler';
import {WebSocketService} from '../../services/socket-service/WebSocketService';
import {Log} from '../../entity/Log';
import {RouteVariableNameConstants} from '../../constants/route-variable-names-constants';

@Component({
  selector: 'app-realtime-logs-component',
  templateUrl: './realtime-logs-component.component.html',
  styleUrls: ['./realtime-logs-component.component.css']
})
export class RealtimeLogsComponentComponent implements OnInit, OnDestroy, WebSocketMessageHandler {

  logs: Log[] = [];

  fileId: string;


  constructor(private webSocketService: WebSocketService) {
    this.fileId = localStorage.getItem(RouteVariableNameConstants.logFileToRealTimeVariableName);
    webSocketService.addHandler(this);
    webSocketService.addFileToListen(this.fileId);
  }

  ngOnInit(): void {
  }

  handleMessage(message) {
    this.logs.push(JSON.parse(message));
  }

  getLogs(): string {
    let res: string = '';

    this.logs.forEach(log => {
      res = res + log.text + '\n';
    });
    return res;
  }

  ngOnDestroy() {
    this.webSocketService.removeHandler(this);
  }
}
