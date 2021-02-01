import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../../entity/User';
import {AuthService} from '../../services/AuthService';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {GlobalConstants} from '../../constants/global-constants';
import {MustMatch} from "../../services/validators/must-match.validator";


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html'
})
export class UserSettingsComponent {

  user: User;
  form: FormGroup;
  deletePressed: boolean = false;
  passChangeSuccess: boolean;
  wrongPassword: boolean = false;
  submitted: boolean = false;
  samePasswords: boolean = false;

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
      confirmPassword: ['']
    }, {
      validator: MustMatch('newPassword', 'confirmPassword'),
    });
    this.user = new User();
    this.http.get<User>('api/user/getInfo').subscribe(result => {
      this.user = result;
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.form.value.oldPassword == this.form.value.newPassword) {
      this.samePasswords = true;
      return;
    }
    this.samePasswords = false;
    if (this.form.invalid) {
      return;
    }
    this.updatePassword();
  }

  get f() {
    return this.form.controls;
  }

  updatePassword() {
    let userCheck: User = new User();
    userCheck.login = this.user.login;
    userCheck.password = this.form.value.oldPassword;
    this.http.post('api/user/checkPassword', userCheck, {observe: 'response'}).subscribe(res => {
      if (res.body as boolean) {
        this.wrongPassword = false;
        this.user.password = this.form.value.newPassword;
        this.http.put(GlobalConstants.apiUrl + 'api/user/updatePassword', this.user, {observe: 'response'}).subscribe(result => {
          this.passChangeSuccess = (result.status == 204);
        });
      } else {
        this.wrongPassword = true;
      }
    });
  }

  deletePres() {
    this.deletePressed = true;
  }

  deleteNo() {
    this.deletePressed = false;
  }

  deleteYes() {
    this.http.delete('api/user/delete/' + this.user.objectId).subscribe(res => {
      console.log(res);
      this.authService.logout();
      this.router.navigateByUrl('/');
    });
  }
}
