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
  submitted: boolean = false;
  sendSuccess: boolean = false;
  badLogin: boolean = false;
  smthWrong: boolean = false;


  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.form = this.fb.group({
      login: ['', Validators.required]
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.send();
  }

  get f() {
    return this.form.controls;
  }

  send(): void {
    this.http
      .post(GlobalConstants.apiUrl + this.url, this.form.value.login, {observe: 'response'})
      .subscribe(res => {
          this.badLogin = false;
          this.sendSuccess = true;
        },
        error => {
          if (error.status == 400){
          this.badLogin = true;
          this.sendSuccess = false;
          this.smthWrong = false;
          } else {
          this.smthWrong = true;
          this.sendSuccess = false;
          this.badLogin = false;
          }
        });
  }
}
