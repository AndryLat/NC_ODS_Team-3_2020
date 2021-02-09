import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../../../../constants/global-constants';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {matchPattern} from '../../../../services/validators/matchPatternValidator';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-server-input-form-modal',
  templateUrl: './server-input-form-modal.component.html'
})
export class ServerInputFormModalComponent implements OnInit {

  inputError: string;
  localApi: string = GlobalConstants.apiUrl + 'api/server';
  insertForm: FormGroup;

  protocols = [
    'SSH', 'FTP'
  ];

  testResultMessage: string = undefined;
  testResult: boolean;

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<ServerInputFormModalComponent>) {
    this.insertForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(64)]],
      ip: ['', [Validators.required, matchPattern(/^[a-zA-Z0-9.-]+$/, 'Special characters is not allowed'), Validators.maxLength(128)]],
      port: ['', [Validators.required, Validators.min(0), Validators.max(65535), matchPattern(/[0-9]+/, 'Only numbers allowed'), Validators.maxLength(5)]],
      login: ['', [Validators.required, Validators.maxLength(64)]],
      password: ['', [Validators.required, Validators.maxLength(128)]],
      protocol: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  testConnection(): void {
    this.insertForm.markAllAsTouched();
    if (!this.insertForm.valid) {
      return;
    }
    this.http.post<boolean>(this.localApi + '/testConnection', this.insertForm.value).subscribe(result => {
      this.testResult = result;
      this.testResultMessage = result ? 'Connection established' : 'Can\'t connect to server';
    }, error => {
      this.testResult = false;
      this.testResultMessage = 'Some errors occurs when checking';
    });
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
    if (errors.required) {
      return 'This field is required';
    }
    if (errors.matchPattern) {
      return errors.matchPattern.message;
    }
  }

  closeDialog() {
    this.insertForm.reset();
    this.testResultMessage = null;
    this.dialogRef.close();
  }

  saveDialogResult() {
    this.insertForm.markAllAsTouched();
    if (!this.insertForm.valid) {
      return;
    }
    const server = this.insertForm.value;

    this.http.post(this.localApi + '/add', server).subscribe(result => {
      this.insertForm.reset({});
      this.dialogRef.close(server);
    }, error => {
      this.inputError = 'Something gone wrong. Try again later';
    });
  }
}
