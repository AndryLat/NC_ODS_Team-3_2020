import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../constants/global-constants';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {User} from '../../entity/User';
import {MustMatch} from "../../services/validators/must-match.validator";

@Component({
  selector: 'app-password-setting',
  templateUrl: './password-setting.component.html'
})
export class PasswordSettingComponent implements OnInit {

  form: FormGroup;
  user: User;
  id: string;
  token: string;
  private url: string = 'api/user/changePassword';
  submitted: boolean = false;
  badLink: boolean = false;
  passChangeSuccess: boolean;


  constructor(private fb: FormBuilder, private http: HttpClient, private actRoute: ActivatedRoute) {
    this.user = new User();
    this.form = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
      confirmPassword: ['', Validators.required],
    }, {
      validator: MustMatch('newPassword', 'confirmPassword'),
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
            this.badLink = true;
            throw new Error(res.statusText);
          }
        }
      )
      .then(res => res.text()
      )
      .then(text => {
        this.user.login = text;
      });
  }

  onSubmit() {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.updatePassword();
  }

  get f() {
    return this.form.controls;
  }

  updatePassword(): void {
    this.user.password = this.form.value.newPassword;
    this.http
      .put(GlobalConstants.apiUrl + 'api/user/updatePassword', this.user, {observe: 'response'})
      .subscribe(result => {
          this.passChangeSuccess = (result.status == 204);
        },
        error => {
          this.passChangeSuccess = false;
        });
  }
}
