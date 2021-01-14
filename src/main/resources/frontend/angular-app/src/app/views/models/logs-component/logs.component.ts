import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {GlobalConstants} from '../../../constants/global-constants';
import {Log} from '../../../entity/Log';
import {LogLevel} from '../../../entity/list/LogLevel';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from "../../../services/AuthService";
import {faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {RuleContainer} from "../../../containers/RuleContainer";
import {LogPage} from "../../../pageable/LogPage";


@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html'
})
export class LogsComponent {
  deleteIcon = faTrashAlt;
  rule: RuleContainer;
  directoryId: string;
  currentPage: number;

  logs: Log[] = [];

  operationForm: FormGroup;

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder) {

    this.currentPage = 0;
    this.rule = new RuleContainer("",new Date(0),new Date(),0,0,0,0,0,0,0,0,0,0,0,0);

    this.directoryId = router.getCurrentNavigation().extras.state['objectId'].toString();

    this.logs.push(new Log('OmegaLog', LogLevel.CONFIG, new Date()));

    this.operationForm = this.fb.group({
      text: [''],
      vSort: [''],
      dat1: [''],
      dat2: ['']
    });
    for(let level of this.keys()) {
      this.operationForm.addControl(level,this.fb.control(''));
    }
    this.getLogsByRule(this.currentPage);

  }

  deleteLog(objectId: bigint): void {
    // TODO: Delete server
  }

  getLogsByRule(pageNumber: number): void {
    console.log(this.directoryId);

    console.log(this.rule);

    this.rule.text = this.operationForm.controls['text'].value;
    this.rule.dat1 = this.operationForm.controls['dat1'].value;
    this.rule.dat2 = this.operationForm.controls['dat2'].value;
    this.rule.sort = this.operationForm.controls['vSort'].value;

    for(let level of this.keys()) {
      if(this.operationForm.controls[level].value){
        this.rule[level.toLowerCase()] = 1;
      }else{
        this.rule[level.toLowerCase()] = 0;
      }
    }

    console.log(this.rule)
    let params = new HttpParams()
      .set("rule",JSON.stringify(this.rule))
      .set("directoryId", this.directoryId)
      .set("page",this.currentPage.toString());

    this.http.get<LogPage>(GlobalConstants.apiUrl + 'api/log/',{params}).subscribe(result => {
      console.log(result);
      this.logs = result.content;
    });
  }

  keys(): Array<string> {
    let keys = Object.keys(LogLevel);
    return keys.slice(keys.length / 2);
  }

  formatLevel(level: LogLevel): string {
    return LogLevel[level];
  }

  getLogsFromPage(event: number) {
    this.currentPage = event;
    this.getLogsByRule(this.currentPage);
  }
}
