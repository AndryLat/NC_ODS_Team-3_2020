import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketMessageHandler} from '../../services/socket-service/WebSocketMessageHandler';
import {WebSocketService} from '../../services/socket-service/WebSocketService';
import {Log} from '../../entity/Log';
import {RouteVariableNameConstants} from '../../constants/route-variable-names-constants';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';

@Component({
  selector: 'app-realtime-logs-component',
  templateUrl: './realtime-logs-component.component.html',
  styleUrls: ['./realtime-logs-component.component.css']
})
export class RealtimeLogsComponentComponent implements OnInit, OnDestroy, WebSocketMessageHandler {

  logs: Log[] = [];

  fileId: string;
  isAutoscroll = false;

  constructor(private webSocketService: WebSocketService, private sanitizer: DomSanitizer, private router: Router) {
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

  saveLogsToFile(){
    const content = this.getLogs();
    const blob = new Blob([content], {type: 'text/plain'});
    return this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
  }

  ngOnDestroy() {
    this.webSocketService.removeHandler(this);
  }

  getCurrentDate(): string {
    return new Date().valueOf().toString();
  }

  navigateToFiles() {
    this.router.navigateByUrl('/logFiles');
  }
}
