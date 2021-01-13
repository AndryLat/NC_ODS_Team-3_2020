import {Component} from '@angular/core';
import {AuthService} from "./services/AuthService";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-app';
  constructor(private authService: AuthService) {
  }

  isAdmin(): boolean {
    return true;
  }

  isLoginned(): boolean {
    return this.authService.isLoggedIn();
  }
}
