import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from "../../services/AuthService";
import {GlobalConstants} from "../../constants/global-constants";
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  form: FormGroup;
  badLogin: boolean = false;
  private url: string = 'login';


  constructor(private http: HttpClient,
              private fb: FormBuilder,
              private authService: AuthService,
              private router: Router) {
    if (authService.isLoggedIn()) {
      this.router.navigateByUrl("/");
    }
    this.form = this.fb.group({
      login: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  login(): void {
    const login = this.form.value.login;
    const password = this.form.value.password;

    if (login && password) {
      this.http
        .post(GlobalConstants.apiUrl + this.url, {login, password}, {observe: 'response'})
        .subscribe(res => {
            this.badLogin = false;
            this.authService.setToken(res.headers.get('Authorization'))
            this.router.navigateByUrl('/');
          },
          error => {
            this.badLogin = (error.status == 401);
          });
    }


  }
}
