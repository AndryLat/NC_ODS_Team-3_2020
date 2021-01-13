import { Component } from '@angular/core';
import {AuthService} from "../../services/AuthService";

@Component({
  selector: 'app-mainview',
  templateUrl: './mainview.component.html'
})
export class MainviewComponent {
  title: "Log"

  constructor(private authService: AuthService) {
  }
  isLogin(): boolean {
    return this.authService.isLoggedIn();
  }
}
