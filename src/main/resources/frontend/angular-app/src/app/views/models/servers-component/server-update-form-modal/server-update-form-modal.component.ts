import {Component, Inject, OnInit} from '@angular/core';
import {GlobalConstants} from '../../../../constants/global-constants';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {matchPattern} from '../../../../services/validators/matchPatternValidator';
import {Server} from '../../../../entity/Server';

@Component({
  selector: 'app-server-update-form-modal',
  templateUrl: './server-update-form-modal.component.html'
})
export class ServerUpdateFormModalComponent implements OnInit {
  inputError: string;
  localApi: string = GlobalConstants.apiUrl + 'api/server';
  updateForm: FormGroup;
  currentServer: Server;

  protocols = [
    'SSH', 'FTP'
  ];

  testResultMessage: string;
  testResult: boolean;

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<ServerUpdateFormModalComponent>,
              @Inject(MAT_DIALOG_DATA) data) {
    this.currentServer = data;

    this.updateForm = this.fb.group({
      objectId: [''],//hidden
      parentId: [''],//hidden
      lastAccessByJob: [''],//hidden
      name: ['', Validators.required],
      ip: [''],//hidden
      port: ['', [Validators.required, Validators.min(0), Validators.max(65535), matchPattern(/[0-9]+/, 'Only numbers allowed')]],
      login: ['', Validators.required],
      password: ['', Validators.required],
      protocol: ['', Validators.required],
      enabled: ['', Validators.required]
    });
    console.log(this.currentServer);
    this.showSettings(this.currentServer);
  }

  ngOnInit(): void {
  }

  showSettings(server: Server): void {
    this.updateForm.controls.objectId.setValue(server.objectId);
    this.updateForm.controls.parentId.setValue(server.parentId);
    this.updateForm.controls.lastAccessByJob.setValue(server.lastAccessByUser);
    this.updateForm.controls.name.setValue(server.name);
    this.updateForm.controls.ip.setValue(server.ip);
    this.updateForm.controls.port.setValue(server.port);
    this.updateForm.controls.login.setValue(server.login);
    this.updateForm.controls.password.setValue(server.password);
    this.updateForm.controls.protocol.setValue(server.protocol);
    this.updateForm.controls.enabled.setValue(server.enabled);
  }

  testConnection(): void {
    this.updateForm.markAllAsTouched();
    if (!this.updateForm.valid) {
      return;
    }
    this.http.post<boolean>(this.localApi + '/testConnection', this.updateForm.value).subscribe(result => {
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
      return 'Max allowed length: ' + errors.maxlength.requiredLength;
    }
    if (errors.required) {
      return 'This field is required';
    }
    if (errors.matchPattern) {
      return errors.matchPattern.message;
    }
  }

  closeDialog() {
    this.updateForm.reset();
    this.testResultMessage = null;
    this.dialogRef.close();
  }

  saveDialogResult() {
    this.updateForm.markAllAsTouched();

    if (!this.updateForm.valid) {
      return;
    }
    console.log(this.updateForm.value);
    const server = this.updateForm.value;
    if (server.enabled) {
      server.connectable = true;
    }

    this.http.put(this.localApi + '/update', server).subscribe(result => {
      this.dialogRef.close(server);
    }, error => {
      this.inputError = 'Something gone wrong. Try again later';
    });
  }
}
