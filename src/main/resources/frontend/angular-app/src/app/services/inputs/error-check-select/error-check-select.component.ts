import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-error-check-select',
  templateUrl: './error-check-select.component.html'
})
export class ErrorCheckSelectComponent implements OnInit {

  inputName: string;
  @Input() controlName: string;
  @Input() parentFormGroup: FormGroup;
  @Input() errorCheckFunction: (control: AbstractControl) => string;
  @Input() labelText: string;
  @Input() values: string[];


  constructor() {
  }

  ngOnInit(): void {
    this.inputName = this.controlName + 'Input';
  }

  containErrors(): boolean {
    return this.parentFormGroup.controls[this.controlName].invalid
      && (this.parentFormGroup.controls[this.controlName].dirty
        || this.parentFormGroup.controls[this.controlName].touched);
  }

  getErrorByControlName(control: AbstractControl): string {
    return this.errorCheckFunction(control);
  }
}
