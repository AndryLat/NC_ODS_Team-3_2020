import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthService} from "./AuthService";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
              private readonly authService: AuthService) {
  }

  intercept(req: HttpRequest<any>,
            next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken = this.authService.getToken();

    console.log("Intercepted")

    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization',
          idToken)
      });

      return next.handle(cloned);
    } else {
      return next.handle(req);
    }
  }
}
