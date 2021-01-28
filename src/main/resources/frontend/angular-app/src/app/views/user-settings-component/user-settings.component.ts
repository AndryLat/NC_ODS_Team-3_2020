import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../entity/User';
import {AuthService} from "../../services/AuthService";
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html'
})
export class UserSettingsComponent {

  user: User;
  form: FormGroup;
  deletePressed: boolean = false;
  httpClient: HttpClient;

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    });
    this.user = new User();
    this.httpClient = http;
    this.httpClient.get<User>("api/user/getInfo").subscribe(result => {
      this.user = result;
    });
  }

  deletePres() {
    this.deletePressed = true;
  }

  deleteNo() {
    this.deletePressed = false;
  }

  deleteYes() {
    this.httpClient.delete("api/user/delete/" + this.user.objectId).subscribe(res => {
      console.log(res);
      this.authService.logout();
      this.router.navigateByUrl('/');
    })
  }
}
