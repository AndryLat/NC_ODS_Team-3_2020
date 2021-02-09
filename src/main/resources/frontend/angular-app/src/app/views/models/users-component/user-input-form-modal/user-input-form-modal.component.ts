import {Component, OnInit} from '@angular/core';
import {GlobalConstants} from "../../../../constants/global-constants";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {MatDialogRef} from "@angular/material/dialog";
import {matchPattern} from "../../../../services/validators/matchPatternValidator";

@Component({
  selector: 'app-user-input-form-modal',
  templateUrl: './user-input-form-modal.component.html'
})
export class UserInputFormModalComponent implements OnInit {

  inputError: string;
  localApi: string = GlobalConstants.apiUrl + 'api/user';
  insertForm: FormGroup;

  roles = [
    'USER', 'ADMIN'
  ];

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<UserInputFormModalComponent>) {
    this.insertForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(64)]],
      login: ['', [Validators.required, Validators.maxLength(64), matchPattern(/^[a-zA-Z0-9.-]+$/, 'Special characters is not allowed')]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
      role: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  getErrorByControlName(control: AbstractControl): string {
    let errors = control.errors;
    if (errors.max) {
      return 'Specified number is greater than max allowed: ' + errors.max.max;
    }
    if (errors.min) {
      return 'Specified number is lower than min allowed: ' + errors.min.min;
    }
    if (errors.maxlength) {
      return 'Maximum allowed length: ' + errors.maxlength.requiredLength;
    }
    if (errors.minlength) {
      return 'Minimum allowed length: ' + errors.minlength.requiredLength;
    }
    if (errors.required) {
      return 'This field is required';
    }
    if (errors.matchPattern) {
      return errors.matchPattern.message;
    }
    if (errors.email) {
      return 'Not valid email';
    }
  }

  closeDialog() {
    this.insertForm.reset();
    this.dialogRef.close();
  }

  saveDialogResult() {
    this.insertForm.markAllAsTouched();
    if (!this.insertForm.valid) {
      return;
    }
    const server = this.insertForm.value;

    this.http.post(this.localApi + '/create', server).subscribe(result => {
      this.insertForm.reset({});
      this.dialogRef.close(server);
    }, error => {
      this.inputError = 'Something gone wrong. Try again later';
    });
  }
}
