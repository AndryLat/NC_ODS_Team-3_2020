import {Component} from '@angular/core';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {AuthService} from "../../services/AuthService";
import {Config} from "../../entity/Config";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DatePipe} from '@angular/common';
import {faQuestion} from "@fortawesome/free-solid-svg-icons/faQuestion";


@Component({
  selector: 'app-global-settings',
  templateUrl: './global-settings.component.html',
  providers:[DatePipe]
})
export class GlobalSettingsComponent{

  insertForm: FormGroup;
  config: Config;
  msg: string;

  qIcon = faQuestion;

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder, private datePipe : DatePipe) {
    http.get<Config>(GlobalConstants.apiUrl + 'api/user/config').subscribe(result => {
      this.config = result;
      this.createForm(result);
    }, error => {
      this.msg = 'Something went wrong';
    });
  }

  private createForm(config:Config){
  this.insertForm = this.fb.group({
    activityPollingPeriod: [config.activityPollingPeriod, Validators.required],
    changesPollingPeriod: [config.changesPollingPeriod, Validators.required],
    serverActivityPeriod: [this.getValidDate(config.serverActivityPeriod), Validators.required],
    directoryActivityPeriod: [this.getValidDate(config.directoryActivityPeriod), Validators.required],
    storageLogPeriod: [this.getValidDate(config.storageLogPeriod), Validators.required]
  });
  }

  private getValidDate(date:string):string{
    const dt = this.datePipe.transform(date, 'yyyy-MM-dd')
    console.log(dt)
    return dt;
  }

  clickForUpdate(): void{
    const val = this.insertForm.value;

    if (val.activityPollingPeriod
      && val.directoryActivityPeriod
      && val.serverActivityPeriod
      && val.changesPollingPeriod
      && val.storageLogPeriod) {
      this.updateConfig(val)
    }
  }

  updateConfig(config: Config): void{
    this.http
      .post(GlobalConstants.apiUrl + 'api/user/updateConfig' ,config,{observe: 'response'})
      .subscribe(res => {
      this.msg = 'Global configuration have been updated';
      }, error => {
      this.msg = 'Something went wrong with update';
    });
  }
}
