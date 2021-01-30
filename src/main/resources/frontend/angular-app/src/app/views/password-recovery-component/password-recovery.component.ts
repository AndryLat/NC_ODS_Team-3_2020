import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';


@Component({
  selector: 'app-password-recovery',
  templateUrl: './password-recovery.component.html'
})
export class PasswordRecoveryComponent {

  form: FormGroup;
  private url: string = 'api/user/resetPassword';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      login: ['', Validators.required]
    });
  }

  send(): void {
    const login = this.form.value.login;
    this.http
      .post(GlobalConstants.apiUrl + this.url, login, {observe: 'response'})
      .subscribe(res => {
        console.log(res);
      });
    alert('Check your email :)');
  }
}
