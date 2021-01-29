import {Injectable} from "@angular/core";
import {HttpClient} from '@angular/common/http';
import jwt_decode from "jwt-decode";
import {Subscription} from "rxjs";
import {User} from "../entity/User";
import {GlobalConstants} from "../constants/global-constants";
import {Router} from "@angular/router";

export const TOKEN_NAME:string = 'jwt_token';

@Injectable()
export class AuthService {

  private url:string = 'login';
  private currentUser:User;

  constructor(private http:HttpClient, private router:Router) {}

  getToken():string {
    return <string>localStorage.getItem(TOKEN_NAME);
  }

  setToken(token: string):void{
    if (token != null) {
      localStorage.setItem(TOKEN_NAME, token);
    }
  }

  getCurrentUser():User{
    if(this.currentUser === undefined){
      this.setCurrentUser()
    }
    return this.currentUser
  }

  setCurrentUser():void{
    const token:string = this.getToken()
    this.currentUser = new User();
    if(!this.isTokenExpired(token)){
      const decoded = this.getDecodedToken(token);
      this.currentUser.login = decoded.sub
      this.currentUser.role = decoded.Role
    }
  }

  getDecodedToken(token:string):JWTToken{
    return jwt_decode<JWTToken>(token);
  }

  getTokenExpirationTime(token:string):Date{
    const decoded = jwt_decode<JWTToken>(token);

    if(decoded.exp === undefined) return null;

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isTokenExpired(token?:string):boolean{
    if(!token) token = this.getToken();
    if (!token) return true;

    const date = this.getTokenExpirationTime(token);
    if(date === undefined){
      return false;
    }

    return !(date.valueOf()>new Date().valueOf());
  }

  login(login: string, password: string ):Subscription{
    return this.http
      .post(GlobalConstants.apiUrl + this.url ,{login, password},{observe: 'response'})
      .subscribe(res => this.setToken(res.headers.get('Authorization')));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_NAME);
    this.currentUser = undefined;
    this.router.navigate(['login']);
  }

  public isLoggedIn(): boolean {
    if (!(localStorage.getItem(TOKEN_NAME) === null)){
      if (!this.isTokenExpired()){
        return true;
      }
      console.log('Using expired token return false');
    }
    return false;
  }

  public isAdmin():boolean{
    const user = this.getCurrentUser()
    if(user === undefined){
      return false;
    }
    if(user.role == 'ADMIN'){
      return true
    }
    return false
  }
}

interface JWTToken {
  sub: string;
  Role:string,
  exp: number;
}
