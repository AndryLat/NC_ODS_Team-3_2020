import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthService} from './AuthService';
import {finalize} from 'rxjs/operators';
import {SpinnerService} from './overlay-spinner/SpinerService';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private ignoreOverlayRequests = [];

  constructor(private readonly spinnerOverlayService: SpinnerService,
              private readonly authService: AuthService) {
    this.ignoreOverlayRequests.push('api/log/directory/count');
    this.ignoreOverlayRequests.push('api/log/logFile/count');
  }

  intercept(req: HttpRequest<any>,
            next: HttpHandler): Observable<HttpEvent<any>> {
    for (let request of this.ignoreOverlayRequests) {
      if (req.url.includes(request)) {
        return this.interceptWithoutOverlay(req, next);
      } else {
        return this.interceptWithOverlay(req, next);
      }
    }
  }


  private interceptWithOverlay(req: HttpRequest<any>,
                               next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken = this.authService.getToken();
    const spinnerSubscription = this.spinnerOverlayService.spinner$.subscribe();

    if (idToken) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization',
          idToken)
      });

      return next.handle(cloned).pipe(finalize(() => spinnerSubscription.unsubscribe()));
    } else {
      return next.handle(req).pipe(finalize(() => spinnerSubscription.unsubscribe()));
    }
  }

  private interceptWithoutOverlay(req: HttpRequest<any>,
                                  next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken = this.authService.getToken();

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
