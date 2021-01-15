import {Injectable} from "@angular/core";
import {HttpClient} from '@angular/common/http';
import jwt_decode from "jwt-decode";
import {Subscription} from "rxjs";

export const TOKEN_NAME: string = 'jwt_token';
export const API_URL: string = 'http://localhost:8081/'; //temporally

@Injectable()
export class AuthService {

  private url: string = 'login';

  constructor(private http: HttpClient) {
  }

  getToken(): string {
    return <string>localStorage.getItem(TOKEN_NAME);
  }

  setToken(token: string | null): void {
    if (token != null) {
      localStorage.setItem(TOKEN_NAME, token);
    }
  }

  getTokenExpirationTime(token: string): Date {
    const decoded = jwt_decode<JWTToken>(token);
    console.log(decoded);

    if (decoded.exp === undefined) return null;

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isTokenExpired(token?: string): boolean {
    if (!token) token = this.getToken();
    if (!token) return true;

    const date = this.getTokenExpirationTime(token);
    if (date === undefined) {
      return false;
    }

    return !(date.valueOf() > new Date().valueOf());
  }

  login(login: string, password: string): Subscription {
    return this.http
      .post(API_URL + this.url, {login, password, role: 'USER'}, {observe: 'response'})
      .subscribe(res => this.setToken(res.headers.get('Authorization')));
  }

  public isLoggedIn(): boolean {
    if (!(localStorage.getItem(TOKEN_NAME) === null)) {
      if (!this.isTokenExpired()) {
        return true;
      }
      console.log('Using expired token return false');
    }
    return false;
  }
}

interface JWTToken {
  name: string;
  role: string,
  exp: number;
}
