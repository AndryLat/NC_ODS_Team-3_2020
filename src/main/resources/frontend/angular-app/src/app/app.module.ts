import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {LoginComponent} from './views/models/login.component';
import {AppRoutingModule} from './app-routing.module';
import {MainviewComponent} from './views/models/mainview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UsersComponent} from './views/models/users.component';
import {ServersComponent} from './views/models/servers.component';
import {DirectoriesComponent} from './views/models/directories.component';
import {LogsComponent} from './views/models/logs.component';
import {UserSettingsComponent} from './views/user-settings.component';
import {AuthService} from "./services/AuthService";
import {AuthInterceptor} from "./services/AuthInterceptor";
import {AuthGuard} from "./services/AuthGuard";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainviewComponent,
    UsersComponent,
    ServersComponent,
    DirectoriesComponent,
    LogsComponent,
    UserSettingsComponent
  ],
  imports: [
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
    AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
     // localStorage.removeItem('id_token');
  }
}
