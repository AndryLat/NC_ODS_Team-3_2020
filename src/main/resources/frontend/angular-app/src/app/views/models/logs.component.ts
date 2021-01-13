import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../../constants/global-constants';
import {Log} from '../../entity/Log';
import {LogLevel} from '../../entity/list/LogLevel';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from "../../services/AuthService";


@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html'
})
export class LogsComponent {
  sort = 'date';

  logs: Log[] = [];

  operationForm: FormGroup;

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder) {
    http.get<Log>(GlobalConstants.apiUrl + 'log/id/7').subscribe(result => {
      console.log(result);
      this.logs.push(result);
    });

    this.logs.push(new Log('OmegaLog', LogLevel.CONFIG, new Date()));

    this.operationForm = this.fb.group({
      search: [''],
      sort: [''],
      dateFiltr: [''],
      startDate: [''],
      endDate: [''],
      levelFiltr: [''],
      logLevel: ['']
    });

  }
  isLogin(): boolean {
    return this.authService.isLoggedIn();
  }

  deleteLog(objectId: bigint): void {
    // TODO: Delete server
  }

  keys(): Array<string> {
    var keys = Object.keys(LogLevel);
    return keys.slice(keys.length / 2);
  }

  formatLevel(level: LogLevel): string {
    return LogLevel[level];
  }
}
