import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {Injectable} from '@angular/core';
import {OverlaySpinnerComponent} from "../views/overlay-spinner/overlay-spinner.component";

@Injectable({
  providedIn: 'root',
})
export class SpinnerOverlayService {
  private isShowing: boolean = false;

  constructor(private overlay: Overlay, private overlayRef: OverlayRef) {
  }

  public show(): void {
    Promise.resolve(null).then(() => {
      this.overlayRef = this.overlay.create({
        positionStrategy: this.overlay
          .position()
          .global()
          .centerHorizontally()
          .centerVertically(),
        hasBackdrop: true,
      });
      this.overlayRef.attach(new ComponentPortal(OverlaySpinnerComponent));
      this.isShowing = true;
    });
  }

  public hide(): void {
    this.overlayRef.detach();
    this.isShowing = false;
  }
}
