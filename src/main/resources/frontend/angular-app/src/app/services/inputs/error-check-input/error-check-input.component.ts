import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-error-check-input',
  templateUrl: './error-check-input.component.html'
})
export class ErrorCheckInputComponent implements OnInit {

  inputName: string;
  @Input() controlName: string;
  @Input() type: string;
  @Input() parentFormGroup: FormGroup;
  @Input() errorCheckFunction: (control: AbstractControl) => string;
  @Input() labelText: string;
  @Input() placeholder: string;
  @Input() value: string;
  @Input() passwordHideCheck: boolean = false;
  isPasswordHide: boolean = true;


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
