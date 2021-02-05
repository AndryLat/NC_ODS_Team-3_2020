import {Component} from '@angular/core';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../services/AuthService';
import {Config} from '../../entity/Config';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
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

  activityPollingPeriod: string = 'The time stamp in the connection check job. For example, every 300000 will check all IsEnabled = false servers.';
  changesPollingPeriod: string  = 'The time interval through which the log check job runs.';
  directoryActivityPeriod: string  = 'The maximum time a directory can not provide new logs.';
  serverActivityPeriod: string  = 'The maximum time a server can not provide new logs.';
  storageLogPeriod: string  = 'Time from the date of creation of logs after which they will be deleted.';

  public mask = [/[0-2]/,/[0-9]/,'/',/[0-3]/,/[0-9]/,'/',/[0-1]/,/[0-9]/,'/',/[0-2]/,/[0-9]/,/[0-9]/,/[0-9]/]

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder) {
    http.get<Config>(GlobalConstants.apiUrl + 'api/user/config').subscribe(result => {
      this.config = result;
      this.createForm(result);
    }, error => {
      this.msg = 'Something went wrong';
    });
  }

  setValues():void{
    this.insertForm.reset();
    this.insertForm.controls.activityPollingPeriod.setValue(this.config.activityPollingPeriod);
    this.insertForm.controls.changesPollingPeriod.setValue(this.config.changesPollingPeriod);
    this.insertForm.controls.directoryActivityPeriod.setValue(this.config.directoryActivityPeriod);
    this.insertForm.controls.serverActivityPeriod.setValue(this.getValidDate(this.config.serverActivityPeriod));
    this.insertForm.controls.directoryActivityPeriod.setValue(this.getValidDate(this.config.directoryActivityPeriod));
    this.insertForm.controls.storageLogPeriod.setValue(this.getValidDate(this.config.storageLogPeriod));
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

  private createForm(config: Config) {
    this.insertForm = this.fb.group({
      activityPollingPeriod: [config.activityPollingPeriod,[
        Validators.required,
        Validators.maxLength(32),
        Validators.min(0)
      ]],
      changesPollingPeriod: [config.changesPollingPeriod,[
        Validators.required,
        Validators.maxLength(32),
        Validators.min(0)
      ]],
      serverActivityPeriod: [this.getValidDate(config.serverActivityPeriod),[
        Validators.required,
        Validators.maxLength(13)]
      ],
      directoryActivityPeriod: [this.getValidDate(config.directoryActivityPeriod), [
        Validators.required,
        Validators.maxLength(13)]
      ],
      storageLogPeriod: [this.getValidDate(config.storageLogPeriod),[
        Validators.required,
        Validators.maxLength(13)]]
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
