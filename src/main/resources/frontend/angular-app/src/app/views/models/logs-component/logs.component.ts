import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {GlobalConstants} from '../../../constants/global-constants';
import {LogLevel} from '../../../entity/list/LogLevel';
import {FormBuilder, FormGroup} from '@angular/forms';
import {AuthService} from "../../../services/AuthService";
import {faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {RuleContainer} from "../../../containers/RuleContainer";
import {LogPage} from "../../../pageable/LogPage";


@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html'
})
export class LogsComponent implements OnInit {
  deleteIcon = faTrashAlt;
  rule: RuleContainer;
  directoryId: string;

  logPage: LogPage;

  operationForm: FormGroup;

  localApi: string = GlobalConstants.apiUrl + 'api/log';

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder) {
    this.operationForm = this.fb.group({
      text: [''],
      vSort: [''],
      dat1: [''],
      dat2: ['']
    });
    for (let level of this.keys()) {
      this.operationForm.addControl(level, this.fb.control(''));
    }

    this.directoryId = this.router.getCurrentNavigation().extras.state['objectId'].toString();

  }

  ngOnInit(): void {
    this.rule = new RuleContainer("", null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    this.getLogsByRule(1);
  }

  deleteLog(objectId: bigint): void {
    this.http.delete(this.localApi + "/delete/" + objectId).subscribe(result => {
    }, error => {
    })
  }

  getLogsByRule(pageNumber: number): void {
    console.log(this.directoryId);

    console.log(this.rule);

    this.rule.text = this.operationForm.controls['text'].value;
    this.rule.dat1 = this.operationForm.controls['dat1'].value;
    this.rule.dat2 = this.operationForm.controls['dat2'].value;
    this.rule.sort = this.operationForm.controls['vSort'].value;

    for (let level of this.keys()) {
      if (this.operationForm.controls[level].value) {
        this.rule[level.toLowerCase()] = 1;
      } else {
        this.rule[level.toLowerCase()] = 0;
      }
    }

    console.log(this.rule)
    let params = new HttpParams()
      .set("rule", JSON.stringify(this.rule))
      .set("directoryId", this.directoryId)
      .set("page", pageNumber.toString());

    this.http.get<LogPage>(this.localApi+'/', {params}).subscribe(result => {
      console.log(result);
      this.logPage = result;
      this.logPage.number = this.logPage.number + 1;// In Spring pages start from 0.
      console.log(this.logPage);
    });
  }

  keys(): Array<string> {
    let keys = Object.keys(LogLevel);
    return keys.slice(keys.length / 2);
  }

  formatLevel(level: LogLevel): string {
    return LogLevel[level];
  }
}
