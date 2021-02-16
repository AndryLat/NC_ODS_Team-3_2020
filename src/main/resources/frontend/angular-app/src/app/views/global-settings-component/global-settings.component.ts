import {Component} from '@angular/core';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../services/AuthService';
import {Config} from '../../entity/Config';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import * as moment from 'moment';
import {faQuestionCircle, faSync} from "@fortawesome/free-solid-svg-icons";
import {AlertBarService} from "../../services/AlertBarService";


@Component({
  selector: 'app-global-settings',
  templateUrl: './global-settings.component.html'
})
export class GlobalSettingsComponent{

  insertForm: FormGroup;
  config: Config;

  tooltipIcon = faQuestionCircle;
  refreshIcon = faSync;

  activityPollingPeriod: string = 'The time stamp in the connection check job. For example, every 300000(ms) will check disabled servers.';
  changesPollingPeriod: string  = 'The time interval through which the log check job runs.';
  directoryActivityPeriod: string  = 'The maximum time a directory can not provide new logs.';
  serverActivityPeriod: string  = 'The maximum time a server can not provide new logs.';
  storageLogPeriod: string  = 'Time from the date of creation of logs after which they will be deleted.';

  public mask = [/([0][0-9])|([1][0-9])|([2][0-3])/,'/',/([0][0-9])|([1][0-9])|([2][0-8])/,'/',/([0][0-9])|([1][0-1])/,'/',/[0-2][0-9][0-9][0-9]/]

  constructor(private authService: AuthService,
              private http: HttpClient, private fb: FormBuilder,
              private alertBarService: AlertBarService) {
    this.http.get<Config>(GlobalConstants.apiUrl + 'api/user/config').subscribe(result => {
      this.config = result;
      this.createForm(result);
    }, error => {
      this.alertBarService.setErrorMessage('Something went wrong. Try again later.');
    });
  }

  ngOnDestroy(): void {
    this.alertBarService.resetMessage();
  }

  clickForUpdate(): void {
    let val = this.insertForm.value;
    if (val.activityPollingPeriod
      && val.directoryActivityPeriod
      && val.serverActivityPeriod
      && val.changesPollingPeriod
      && val.storageLogPeriod) {
      this.config = this.insertForm.value;
      let con = this.parseDate()
      this.updateConfig(con);
    }
  }

  updateConfig(config: Config): void {
    this.http
      .post(GlobalConstants.apiUrl + 'api/user/updateConfig', config, {observe: 'response'})
      .subscribe(res => {
        this.alertBarService.setConfirmMessage('Global configuration have been updated');
      }, error => {
        this.alertBarService.setErrorMessage('Something went wrong with update. Try again later.');
      });
  }

  validatorForNullDate = (control: FormControl) => {
    const condition = control.value;
    if (condition == '0000000000') {
      return {validatorForNullDate: 'Field cant be null'}
    }
    return null;
  }

  private createForm(config: Config) {
    this.insertForm = this.fb.group({
      activityPollingPeriod: [config.activityPollingPeriod,[
        Validators.required,
        Validators.max(9999999999999999),
        Validators.min(1)
      ]],
      changesPollingPeriod: [config.changesPollingPeriod,[
        Validators.required,
        Validators.max(9999999999999999),
        Validators.min(1)
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

  private parseDate():any{
    const conf = new Config();
    conf.objectId = this.config.objectId;
    conf.activityPollingPeriod = this.config.activityPollingPeriod;
    conf.changesPollingPeriod = this.config.changesPollingPeriod;
    conf.storageLogPeriod = this.config.storageLogPeriod;
    conf.directoryActivityPeriod = this.config.directoryActivityPeriod;
    conf.serverActivityPeriod = this.config.serverActivityPeriod;
    conf.storageLogPeriod = moment(this.parseDateForValid(conf.storageLogPeriod), "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    conf.serverActivityPeriod = moment(this.parseDateForValid(conf.serverActivityPeriod), "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    conf.directoryActivityPeriod = moment(this.parseDateForValid(conf.directoryActivityPeriod), "HH/DD/MM/yyyy" ).add(1970, 'year').format( "yyyy-MM-DD HH:mm:ss" );
    return conf;
  }

  private parseDateForValid(dateString: string):any{
    let hh = dateString.slice(0,2);
    let dd = dateString.slice(2,4);
    let mm = dateString.slice(4,6);
    let yyyy = dateString.slice(6,10);
    if(dd != '00' && mm != '00'){
      dd = moment(hh+'/'+dd+'/'+mm+'/'+yyyy, 'HH/DD/MM/yyyy').add(1, 'day').format('HH/DD/MM/yyyy').split('/').join('').slice(2,4);
      mm = moment(hh+'/'+dd+'/'+mm+'/'+yyyy, 'HH/DD/MM/yyyy').add(1, 'month').format('HH/DD/MM/yyyy').split('/').join('').slice(4,6);
      return hh + dd + mm + yyyy;
    }
    if(dd == '00' && mm == '00'){
      dd = '01';
      mm = '01';
      return hh + dd + mm + yyyy;
    }
    if(dd != '00' || mm != '00'){
      if(dd == '00'){
        dd = '01';
        mm = moment(hh+'/'+dd+'/'+mm+'/'+yyyy, 'HH/DD/MM/yyyy').add(1, 'month').format('HH/DD/MM/yyyy').split('/').join('').slice(4,6);
      }
      if(mm == '00'){
        mm = '01';
        dd = moment(hh+'/'+dd+'/'+mm+'/'+yyyy, 'HH/DD/MM/yyyy').add(1, 'day').format('HH/DD/MM/yyyy').split('/').join('').slice(2,4);
      }
      return hh + dd + mm + yyyy;
    }
  }

  private getValidDate(dateString: string): string {
    let dt = moment(dateString).subtract(1, 'hour').subtract(1970, 'year').format('HH/DD/MM/yyyy').split('/').join('').slice(0, -1) + '0';

    let hh = dt.slice(0,2);
    let dd = dt.slice(2,4);
    let mm = dt.slice(4,6);
    let yyyy = dt.slice(6,10);
    if(dd == '01'){
      dd = '00';
    }
    else {
      dd = moment(dateString).subtract(1, 'day').format('HH/DD/MM/yyyy').split('/').join('').slice(2,4);
    }
    if(mm == '01'){
      mm = '00';
    } else {
      mm = moment(dateString).subtract(1, 'month').format('HH/DD/MM/yyyy').split('/').join('').slice(4,6);
    }
    dt = hh+dd+mm+yyyy;
    return dt;
  }
}
