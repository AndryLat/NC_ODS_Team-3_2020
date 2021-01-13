import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {LoginComponent} from './views/login-component/login.component';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UsersComponent} from './views/models/users-component/users.component';
import {ServersComponent} from './views/models/servers-component/servers.component';
import {DirectoriesComponent} from './views/models/directories-component/directories.component';
import {LogsComponent} from './views/models/logs-component/logs.component';
import {UserSettingsComponent} from './views/user-settings-component/user-settings.component';
import {AuthService} from "./services/AuthService";
import {AuthInterceptor} from "./services/AuthInterceptor";
import {AuthGuard} from "./services/AuthGuard";
import {SpinnerService} from "./services/overlay-spinner/SpinerService";
import {OverlaySpinnerComponent} from "./services/overlay-spinner/overlay-spinner.component";
import { OverlayModule } from '@angular/cdk/overlay';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UsersComponent,
    ServersComponent,
    DirectoriesComponent,
    LogsComponent,
    UserSettingsComponent,
    OverlaySpinnerComponent
  ],
  imports: [
    OverlayModule,
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule, ReactiveFormsModule
  ],
  providers: [
    AuthService,
    {provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true},
    SpinnerService,
    AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
     // localStorage.removeItem('id_token');
  }
}
