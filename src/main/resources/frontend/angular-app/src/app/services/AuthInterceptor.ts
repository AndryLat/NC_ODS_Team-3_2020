import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthService} from "./AuthService";
import {finalize} from "rxjs/operators";
import {SpinnerService} from "./overlay-spinner/SpinerService";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly spinnerOverlayService: SpinnerService,
              private readonly authService: AuthService) {
  }

  intercept(req: HttpRequest<any>,
            next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken = this.authService.getToken();

    this.spinnerOverlayService.show();

    console.log("Intercepted")

    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization',
          idToken)
      });

      return next.handle(cloned).pipe(finalize(() => this.spinnerOverlayService.hide()));
    } else {
      return next.handle(req).pipe(finalize(() => this.spinnerOverlayService.hide()));
    }
  }
}
