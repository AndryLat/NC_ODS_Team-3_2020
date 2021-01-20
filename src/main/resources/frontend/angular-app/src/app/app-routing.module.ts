import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './views/login-component/login.component';
import {UsersComponent} from './views/models/users-component/users.component';
import {ServersComponent} from './views/models/servers-component/servers.component';
import {DirectoriesComponent} from './views/models/directories-component/directories.component';
import {LogsComponent} from './views/models/logs-component/logs.component';
import {UserSettingsComponent} from './views/user-settings-component/user-settings.component';
import {PasswordRecoveryComponent} from './views/password-recovery-component/password-recovery.component';
import {AuthGuard} from "./services/AuthGuard";

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'users', component: UsersComponent, canActivate: [AuthGuard]},
  {path: 'servers', component: ServersComponent, canActivate: [AuthGuard]},
  {path: '', redirectTo: 'servers', pathMatch: 'full', canActivate: [AuthGuard]},
  {path: 'directories', component: DirectoriesComponent, canActivate: [AuthGuard]},
  {path: 'logs', component: LogsComponent, canActivate: [AuthGuard]},
  {path: 'settings', component: UserSettingsComponent, canActivate: [AuthGuard]},
  {path: 'resetPassword', component: PasswordRecoveryComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
