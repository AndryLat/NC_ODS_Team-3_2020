import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketMessageHandler} from '../../socket-service/WebSocketMessageHandler';
import {WebSocketService} from '../../socket-service/WebSocketService';
import {Log} from '../../entity/Log';
import {RouteVariableNameConstants} from '../../constants/route-variable-names-constants';

@Component({
  selector: 'app-realtime-logs-component',
  templateUrl: './realtime-logs-component.component.html',
  styleUrls: ['./realtime-logs-component.component.css']
})
export class RealtimeLogsComponentComponent implements OnInit, OnDestroy, WebSocketMessageHandler {

  logs: Log[] = [];

  objectId: string;


  constructor(private webSocketService: WebSocketService) {
    this.objectId = localStorage.getItem(RouteVariableNameConstants.logFileToRealTimeVariableName);
    webSocketService.addHandler(this);
    webSocketService.addFileToListen(this.objectId);
  }

  ngOnInit(): void {
    let newLog = new Log();
    newLog.text = 'Test log from ts';
    this.logs.push(newLog);
  }

  handleMessage(message) {
    this.logs.push(JSON.parse(message));
  }

  getLogs(): string {
    let res: string = '';

    this.logs.forEach(log => {
      res = res + log.text + '\n';
    });
    console.log(res);

    return res;
  }

  ngOnDestroy() {
    this.webSocketService.removeHandler(this);
  }
}
