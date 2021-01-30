import {AbstractControl, ValidatorFn} from '@angular/forms';

export function matchPattern(pattern: RegExp, errorName: string): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const match = !pattern.test(control.value);
    return match ? {matchPattern: {value: control.value, message: errorName}} : null;
  };
}
