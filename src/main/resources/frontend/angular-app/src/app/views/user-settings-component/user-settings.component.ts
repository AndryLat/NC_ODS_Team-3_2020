import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../entity/User';
import {AuthService} from "../../services/AuthService";


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html'
})
export class UserSettingsComponent {

  user: User;

  constructor(private authService: AuthService, private http: HttpClient) {
    http.get<User>('api/user/2').subscribe(result => {
      this.user = result;
    });
  }
}
