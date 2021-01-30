import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../entity/User';
import {AuthService} from '../../services/AuthService';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {GlobalConstants} from '../../constants/global-constants';


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html'
})
export class UserSettingsComponent {

  user: User;
  form: FormGroup;
  httpClient: HttpClient;
  deletePressed: boolean = false;
  differentPasswords: boolean = false;
  passChangeSuccess: boolean;

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    });
    this.user = new User();
    this.httpClient = http;
    this.httpClient.get<User>('api/user/getInfo').subscribe(result => {
      this.user = result;
    });
  }

  updatePassword() {
    if (this.form.value.newPassword != null && this.form.value.newPassword == this.form.value.repeatPassword) {
      this.differentPasswords = false;
      let userCheck: User = new User();
      userCheck.login = this.user.login;
      userCheck.password = this.form.value.oldPassword;
      this.httpClient.post('api/user/checkPassword', userCheck, {observe: 'response'}).subscribe(res => {
        if (res.body as boolean) {
          this.user.password = this.form.value.newPassword;
          this.httpClient.put(GlobalConstants.apiUrl + 'api/user/updatePassword', this.user, {observe: 'response'}).subscribe(result => {
            this.passChangeSuccess = (result.status == 204);
          });
        }
      });

    } else {
      this.differentPasswords = true;
    }
  }

  deletePres() {
    this.deletePressed = true;
  }

  deleteNo() {
    this.deletePressed = false;
  }

  deleteYes() {
    this.httpClient.delete('api/user/delete/' + this.user.objectId).subscribe(res => {
      console.log(res);
      this.authService.logout();
      this.router.navigateByUrl('/');
    });
  }
}
