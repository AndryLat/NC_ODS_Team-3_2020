import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {GlobalConstants} from '../../../constants/global-constants';
import {LogLevel} from '../../../entity/list/LogLevel';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../services/AuthService';
import {faTrashAlt} from '@fortawesome/free-solid-svg-icons';
import {RuleContainer} from '../../../containers/RuleContainer';
import {LogPage} from '../../../pageable/LogPage';
import {RouteVariableNameConstants} from "../../../constants/route-variable-names-constants";
import {Log} from "../../../entity/Log";

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {
  deleteIcon = faTrashAlt;
  rule: RuleContainer;

  parentType: string;
  parentId: string;
  msg: string;
  date1: Date;
  date2: Date;
  logPage: LogPage;
  operationForm: FormGroup;
  localApi: string = GlobalConstants.apiUrl + 'api/log';

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder) {
    this.operationForm = this.fb.group({
      text: ['', Validators.maxLength(2000)],
      vSort: ['0'],
      dat1: [''],
      dat2: [''],
      date: ['']
    });
    for (let level of this.keys()) {
      this.operationForm.addControl(level, this.fb.control(''));
    }

    let logFileId = localStorage.getItem(RouteVariableNameConstants.logFileToLogsVariableName);
    if (logFileId != null) {
      this.parentId = logFileId;
      this.parentType = 'logFile';
    } else {
      this.parentId = localStorage.getItem(RouteVariableNameConstants.directoryToLogsVariableName);
      this.parentType = 'directory';
    }

    this.rule = new RuleContainer();
  }

  get f() {
    return this.operationForm.controls;
  }

  ngOnInit(): void {
    this.setNewRule();
  }

  clearDate2(event) {
    event.stopPropagation();
    this.date2 = null;
  }

  clearDate1(event) {
    event.stopPropagation();
    this.date1 = null;
  }

  deleteLog(objectId: string): void {
    this.http.delete(this.localApi + '/delete/' + objectId).subscribe(result => {
      this.msg = 'Log successfully deleted';
    }, error => {
      this.msg = 'Something went wrong during deleting logs';
    });
    let changedServer = this.logPage.content.find(deletedLog => deletedLog.objectId == objectId);
    let index = this.logPage.content.indexOf(changedServer);
    this.logPage.content.splice(index, 1);
  }

  checkAllCheckBox(ev) {
    this.logPage?.content.forEach(x => x.checked = ev.target.checked);
  }

  isAllCheckBoxChecked() {
    return this.logPage?.content.every(p => p.checked);
  }

  deleteSelectedLogs(): void {
    const selectedProducts = this.logPage?.content.filter(product => product.checked).map(p => p.objectId);
    if (selectedProducts && selectedProducts.length > 0) {
      this.http.delete(this.localApi + '/deletes/' + selectedProducts)
        .subscribe(result => {
            this.msg = 'Logs successfully deleted';
            for (let i = 0; i < selectedProducts.length; i++) {
              let changedServer = this.logPage.content.find(deletedLog => deletedLog.objectId == selectedProducts[i]);
              let index = this.logPage.content.indexOf(changedServer);
              this.logPage.content.splice(index, 1);
            }
          }, error => {
            this.msg = 'Something went wrong during deleting logs';
          }
        );
    } else {
      this.msg = 'You must select at least one log';
    }
  }

  getLogsByRule(pageNumber: number, ruleChange: boolean): void {

    let params = new HttpParams()
      .set('rule', JSON.stringify(this.rule))
      .set(this.parentType + "Id", this.parentId)
      .set('page', pageNumber.toString());

    this.http.get<LogPage>(this.localApi + '/' + this.parentType, {params}).subscribe(result => {
      if ((this.logPage === undefined && result.approximate) || (pageNumber == this.logPage?.totalPages)) {
        this.http.get<number>(this.localApi + '/' + this.parentType + '/' + 'count', {params}).subscribe(result => {
          console.log("Get accurate count: " + result);
          this.logPage.approximate = false;
          this.logPage.totalElements = result;
          this.logPage.totalPages = result/this.logPage.size;
        })
      }
      if ((result.approximate && this.logPage !== undefined && !this.logPage.approximate) && (!ruleChange)) {
        result.totalElements = this.logPage.totalElements;
        result.approximate = this.logPage.approximate;
      }
      this.logPage = result;
      this.logPage.number = this.logPage.number + 1;// In Spring pages start from 0.
      console.log(this.logPage);
    });
  }

  keys(): Array<string> {
    let keys = Object.keys(LogLevel);
    return keys.slice(keys.length / 2);
  }

  setNewRule() {
    this.rule.text = this.operationForm.controls['text'].value;
    this.rule.dat1 = this.operationForm.controls['dat1'].value;
    this.rule.dat2 = this.operationForm.controls['dat2'].value;
    this.rule.sort = this.operationForm.controls['vSort'].value;

    this.rule.levels = [];
    for (let level of this.keys()) {
      if (this.operationForm.controls[level].value) {
        this.rule.levels.push(LogLevel[level] - 13);
      }
    }
    this.getLogsByRule(1, true);
  }

  getTextWithDelimiter(log: Log): string {

    let logWithEnter = log.text.split('\n').join('<br/>');
    logWithEnter = logWithEnter.substr(0, !log.fullText ? 90 : logWithEnter.length);
    return logWithEnter;
  }
}
