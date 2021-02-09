import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PaginationControlsComponent} from "ngx-pagination";

@Component({
  selector: 'app-pagination-controls',
  templateUrl: './approximate-pagination-controls.html'
})
export class ApproximatePaginationControls extends PaginationControlsComponent implements OnInit {

  @Input() approximate: boolean = false;
  @Input() id: string;
  @Input() maxSize: number;
  @Output() pageChange: EventEmitter<number>;
  @Output() pageBoundsCorrection: EventEmitter<number>;

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

}
