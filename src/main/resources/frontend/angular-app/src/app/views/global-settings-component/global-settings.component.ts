import {Component} from '@angular/core';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../services/AuthService';
import {Config} from '../../entity/Config';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import * as moment from 'moment';
import {faQuestionCircle, faSync} from "@fortawesome/free-solid-svg-icons";


@Component({
  selector: 'app-global-settings',
  templateUrl: './global-settings.component.html'
})
export class GlobalSettingsComponent{

  insertForm: FormGroup;
  config: Config;
  msg: string;

  tooltipIcon = faQuestionCircle;
  refreshIcon = faSync;

  activityPollingPeriod: string = 'The time stamp in the connection check job. For example, every 300000(ms) will check disabled servers.';
  changesPollingPeriod: string  = 'The time interval through which the log check job runs.';
  directoryActivityPeriod: string  = 'The maximum time a directory can not provide new logs.';
  serverActivityPeriod: string  = 'The maximum time a server can not provide new logs.';
  storageLogPeriod: string  = 'Time from the date of creation of logs after which they will be deleted.';

  //public mask = [/[0-2]/,/[0-9]/,'/',/[0-3]/,/[0-9]/,'/',/[0-1]/,/[0-9]/,'/',/[0-2]/,/[0-9]/,/[0-9]/,/[0-9]/]
  public mask = [/([0-1][0-9])|([2][0-4])/,'/',/([0-2][0-9])|([3][0-1])/,'/',/([0][0-9])|([1][0-2])/,'/',/[0-2][0-9][0-9][0-9]/]
  //public mask = [/[0-2]/,/[0-9]/,'/',/(0[1-9]|1[0-9]|2[0-9]|3[01])/,'/',/(0[1-9]|1[012])/,'/',/[0-9]{4}/]

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder) {
  }

  ngOnInit():void{
    this.http.get<Config>(GlobalConstants.apiUrl + 'api/user/config').subscribe(result => {
      this.config = result;
      this.createForm(result);
    }, error => {
      this.msg = 'Something went wrong';
    });
  }

  setValues():void{
    this.insertForm.reset();
    this.ngOnInit();
  }

  clickForUpdate(): void {
    const val = this.insertForm.value;

    if (val.activityPollingPeriod
      && val.directoryActivityPeriod
      && val.serverActivityPeriod
      && val.changesPollingPeriod
      && val.storageLogPeriod) {
      const con = this.parseDate(val)
      console.log(con);
      this.updateConfig(con);
    }
  }

  updateConfig(config: Config): void {
    this.http
      .post(GlobalConstants.apiUrl + 'api/user/updateConfig', config, {observe: 'response'})
      .subscribe(res => {
        this.msg = 'Global configuration have been updated';
      }, error => {
        this.msg = 'Something went wrong with update';
      });
  }

  validatorForNullDate = (control: FormControl) => {
    const condition = control.value;
    console.log(condition)
    if (condition == '0000000000') {
      return {validatorForNullDate: 'Field cant be null'}
    }
    return null;
  }

  private createForm(config: Config) {
    this.insertForm = this.fb.group({
      activityPollingPeriod: [config.activityPollingPeriod,[
        Validators.required,
        Validators.maxLength(16),
        Validators.min(0)
      ]],
      changesPollingPeriod: [config.changesPollingPeriod,[
        Validators.required,
        Validators.maxLength(16),
        Validators.min(0)
      ]],
      serverActivityPeriod: [this.getValidDate(config.serverActivityPeriod),[
        Validators.required,
        this.validatorForNullDate]
      ],
      directoryActivityPeriod: [this.getValidDate(config.directoryActivityPeriod), [
        Validators.required,
        this.validatorForNullDate]
      ],
      storageLogPeriod: [this.getValidDate(config.storageLogPeriod),[
        Validators.required,
        this.validatorForNullDate]]
    });
  }

  private parseDate(config:Config):any{
    config.storageLogPeriod = moment(config.storageLogPeriod, "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    config.serverActivityPeriod = moment(config.serverActivityPeriod, "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    config.directoryActivityPeriod = moment(config.directoryActivityPeriod, "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    return config;
  }

  private getValidDate(date: string): string {
    console.log(date);
    const dt = moment(date).subtract(1, 'hour').subtract(1970, 'year').format('HH/DD/MM/yyyy').split('/').join('').slice(0, -1) + '0';// parse for valid timezone
    console.log(dt);
    return dt;
  }
}
