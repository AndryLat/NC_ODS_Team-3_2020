import {Injectable} from '@angular/core';
import {AppComponent} from '../app.component';

export const TOKEN_NAME: string = 'jwt_token';

@Injectable()
export class AlertBarService {
  public topBar: AppComponent;

  constructor() {
  }

  public setErrorMessage(message: string): void {
    this.topBar.setConfirmMessage(undefined);
    this.topBar.setErrorMessage(message);
  }

  public setConfirmMessage(message: string): void {
    this.topBar.setErrorMessage(undefined);
    this.topBar.setConfirmMessage(message);
  }

  public resetMessage(): void {
    this.topBar.setErrorMessage(undefined);
    this.topBar.setConfirmMessage(undefined);
  }
}
