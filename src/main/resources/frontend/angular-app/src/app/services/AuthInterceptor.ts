import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {finalize} from "rxjs/operators";
import {SpinnerOverlayService} from "./SpinerService";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly spinnerOverlayService: SpinnerOverlayService) {
  }

  intercept(req: HttpRequest<any>,
            next: HttpHandler): Observable<HttpEvent<any>> {
    //this.spinnerOverlayService.show();
    const idToken = localStorage.getItem('id_token');

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
