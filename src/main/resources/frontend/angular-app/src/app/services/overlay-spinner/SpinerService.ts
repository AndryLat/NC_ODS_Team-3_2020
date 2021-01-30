import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {Injectable} from '@angular/core';
import {OverlaySpinnerComponent} from './overlay-spinner.component';
import {defer, NEVER} from 'rxjs';
import {finalize, share} from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class SpinnerService {
  public readonly spinner$ = defer(() => {
    this.show();
    return NEVER.pipe(
      finalize(() => {
        this.hide();
      })
    );
  }).pipe(share());
  private overlayRef: OverlayRef = undefined;

  constructor(private overlay: Overlay) {
  }

  public show(): void {
    Promise.resolve(null).then(() => {
      if (!this.overlayRef) {
        this.overlayRef = this.overlay.create({
          positionStrategy: this.overlay
            .position()
            .global()
            .centerHorizontally()
            .centerVertically(),
          hasBackdrop: true,
        });
        this.overlayRef.attach(new ComponentPortal(OverlaySpinnerComponent));
      }
    });
  }

  public hide(): void {
    if (this.overlayRef) {
      this.overlayRef.detach();
      this.overlayRef = undefined;
    }
  }
}
