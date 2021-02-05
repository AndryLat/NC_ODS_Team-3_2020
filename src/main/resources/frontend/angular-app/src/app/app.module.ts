import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {LoginComponent} from './views/login-component/login.component';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UsersComponent} from './views/models/users-component/users.component';
import {ServersComponent} from './views/models/servers-component/servers.component';
import {DirectoriesComponent} from './views/models/directories-component/directories.component';
import {LogsComponent} from './views/models/logs-component/logs.component';
import {UserSettingsComponent} from './views/user-settings-component/user-settings.component';
import {PasswordRecoveryComponent} from './views/password-recovery-component/password-recovery.component';
import {PasswordSettingComponent} from './views/password-setting-component/password-setting.component';
import {GlobalSettingsComponent} from './views/global-settings-component/global-settings.component';
import {AuthService} from './services/AuthService';
import {AuthInterceptor} from './services/AuthInterceptor';
import {AuthGuard} from './services/AuthGuard';
import {SpinnerService} from './services/overlay-spinner/SpinerService';
import {OverlaySpinnerComponent} from './services/overlay-spinner/overlay-spinner.component';
import {OverlayModule} from '@angular/cdk/overlay';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {NgxPaginationModule} from 'ngx-pagination';
import {WebSocketService} from './socket-service/WebSocketService';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RealtimeLogsComponentComponent} from './views/realtime-logs-component/realtime-logs-component.component';
import {LogfileComponentComponent} from './views/models/logfile-component/logfile-component.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatButtonModule} from '@angular/material/button';
import {MatNativeDateModule} from '@angular/material/core';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {NgxMatDatetimePickerModule, NgxMatNativeDateModule, NgxMatTimepickerModule} from '@angular-material-components/datetime-picker';
import {ErrorCheckInputComponent} from './services/inputs/error-check-input/error-check-input.component';
import {ErrorCheckSelectComponent} from './services/inputs/error-check-select/error-check-select.component';
import {ServerInputFormModalComponent} from './views/models/servers-component/server-input-form-modal/server-input-form-modal.component';
import {ServerUpdateFormModalComponent} from './views/models/servers-component/server-update-form-modal/server-update-form-modal.component';
import {MatDialogModule} from '@angular/material/dialog';
import {AlertBarService} from './services/AlertBarService';
import {DirectoryInputFormModalComponent} from "./views/models/directories-component/directory-input-form-modal/directory-input-form-modal.component";
import {DirectoryLogFileUpdateFormModalComponent} from "./views/models/directories-component/log-file-update-form-modal/log-file-update-form-modal.component";
import {MaskedInputComponent} from './views/global-settings-component/date-mask-input-component/date-mask-input.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UsersComponent,
    ServersComponent,
    DirectoriesComponent,
    LogsComponent,
    UserSettingsComponent,
    OverlaySpinnerComponent,
    PasswordRecoveryComponent,
    PasswordSettingComponent,
    GlobalSettingsComponent,
    RealtimeLogsComponentComponent,
    LogfileComponentComponent,
    ErrorCheckInputComponent,
    ErrorCheckSelectComponent,
    ErrorCheckSelectComponent,
    ServerInputFormModalComponent,
    ServerUpdateFormModalComponent,
    DirectoryInputFormModalComponent,
    DirectoryLogFileUpdateFormModalComponent,
    MaskedInputComponent
  ],
  imports: [
    OverlayModule,
    BrowserModule,
    BrowserAnimationsModule,
    MatTooltipModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule, ReactiveFormsModule,
    FontAwesomeModule,
    NgxPaginationModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    NgxMatTimepickerModule,
    NgxMatDatetimePickerModule,
    NgxMatNativeDateModule,
    MatDialogModule
  ],
  providers: [
    AuthService,
    BrowserAnimationsModule,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    SpinnerService,
    AuthGuard,
    WebSocketService,
    AlertBarService],
  bootstrap: [AppComponent],
  entryComponents: [ServerInputFormModalComponent,
    ServerUpdateFormModalComponent,
    DirectoryInputFormModalComponent,
    DirectoryLogFileUpdateFormModalComponent]
})
export class AppModule {
  constructor() {
  }
}
