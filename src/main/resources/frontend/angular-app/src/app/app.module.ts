import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {OverlaySpinnerComponent} from "./views/overlay-spinner/overlay-spinner.component";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {AuthInterceptor} from "./services/AuthInterceptor";
import {SpinnerOverlayService} from "./services/SpinerService";
import {AuthService} from "./services/AuthService";
import {AuthGuard} from "./services/AuthGuard";

@NgModule({
  declarations: [
    AppComponent,
    OverlaySpinnerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [AuthService,AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    SpinnerOverlayService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
