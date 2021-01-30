import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {User} from '../../entity/User';

@Component({
  selector: 'app-password-setting',
  templateUrl: './password-setting.component.html'
})
export class PasswordSettingComponent implements OnInit {

  form: FormGroup;
  user: User;
  id: string;
  token: string;
  differentPasswords: boolean = false;
  private url: string = 'api/user/changePassword';

  constructor(private fb: FormBuilder, private http: HttpClient, private actRoute: ActivatedRoute) {
    this.user = new User();
    this.form = this.fb.group({
      newPassword: ['', Validators.required],
      repeatNewPassword: ['', Validators.required]
    });
    this.id = this.actRoute.snapshot.params.id;
    this.token = this.actRoute.snapshot.params.token;
  }

  ngOnInit(): void {
    fetch(GlobalConstants.apiUrl + this.url + '?id=' + this.id + '&token=' + this.token)
      .then(res => {
          if (res.ok) {
            return res;
          } else {
            throw new Error(res.statusText);
          }
        }
      )
      .then(res => res.text()
      )
      .then(text => {
        console.log(text);
        this.user.login = text;
      });
  }

  updatePassword(): void {
    if (this.form.value.newPassword != null && this.form.value.newPassword == this.form.value.repeatNewPassword) {
      this.differentPasswords = false;
      const val = this.form.value;
      this.user.password = val.newPassword;
      console.log(this.user);
      this.http
        .put(GlobalConstants.apiUrl + 'api/user/updatePassword', this.user, {observe: 'response'})
        .subscribe(res => {
          console.log(res);
        });
    } else {
      this.differentPasswords = true;
    }
  }
}
