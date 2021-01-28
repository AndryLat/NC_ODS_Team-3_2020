import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {User} from "../../entity/User";

@Component({
  selector: 'app-password-setting',
  templateUrl: './password-setting.component.html'
})
export class PasswordSettingComponent {

  form: FormGroup;
  private url: string = 'api/user/changePassword';
  user: User;
  differentPasswords: boolean = false;


  constructor(private fb: FormBuilder, private http: HttpClient, private actRoute: ActivatedRoute) {
    this.user = new User();
    this.form = this.fb.group({
      newPassword: ['', Validators.required],
      repeatNewPassword: ['', Validators.required]
    });
    const id: string = this.actRoute.snapshot.params.id;
    const token: string = this.actRoute.snapshot.params.token;
    this.http.get(GlobalConstants.apiUrl + this.url + "?id=" + id + "&token=" + token)
      .subscribe(res => {
        console.log(res);
        this.user.login = (res as string);
      });
  }

  updatePassword(): void {
    if (this.form.value.newPassword != null && this.form.value.newPassword == this.form.value.repeatNewPassword) {
      this.differentPasswords = false;
      this.user.password = this.form.value.newPassword;
      this.http
        .put(GlobalConstants.apiUrl + "api/user/updatePassword", this.user, {observe: 'response'})
        .subscribe(res => {
          console.log(res);
        });
    } else this.differentPasswords = true;
  }
}
