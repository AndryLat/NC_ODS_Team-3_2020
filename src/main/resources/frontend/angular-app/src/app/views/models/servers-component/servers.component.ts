import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Server} from '../../../entity/Server';
import {GlobalConstants} from '../../../constants/global-constants';
import {AuthService} from "../../../services/AuthService";
import {faCheck, faCogs, faSignInAlt, faTimes, faTrashAlt} from '@fortawesome/free-solid-svg-icons';
import {matchPattern} from "../../../services/validators/matchPatternValidator";

@Component({
  selector: 'app-servers',
  templateUrl: './servers.component.html'
})
export class ServersComponent implements OnInit {
  proceedIcon = faSignInAlt;
  settingIcon = faCogs;
  deleteIcon = faTrashAlt;

  enabledIcon = faCheck;
  disabledIcon = faTimes;


  localApi: string = GlobalConstants.apiUrl + 'api/server'

  insertForm: FormGroup;
  updateForm: FormGroup;

  errorMessage: string;
  confirmMessage: string;
  inputError: string;

  servers: Server[] = [];

  protocols = [
    'SSH', 'FTP'
  ];

  testResult: string;

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder,) {

    this.insertForm = this.fb.group({
      name: ['', Validators.required],
      ip: ['', [Validators.required, matchPattern(/^[a-zA-Z0-9.]+$/, "Special characters is not allowed")]],
      port: ['', [Validators.required, Validators.min(0), Validators.max(65535), matchPattern(/[0-9]+/, "Only numbers allowed")]],
      login: ['', Validators.required],
      password: ['', Validators.required],
      protocol: ['', Validators.required]
    });

    this.updateForm = this.fb.group({
      objectId: [''],
      parentId: [''],
      name: ['', Validators.required],
      ip: [''],
      port: ['', [Validators.required, Validators.min(0), Validators.max(65535), matchPattern(/[0-9]+/, "Only numbers allowed")]],
      login: ['', Validators.required],
      password: ['', Validators.required],
      protocol: ['', Validators.required],
      enabled: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    let ser = new Server();
    ser.enabled = true;
    this.servers.push(ser);
    //return;
    this.http.get<Server[]>(this.localApi + '/').subscribe(result => {
      this.servers = result;
    }, error => {
      this.errorMessage = "Cant get list of servers";
    });
  }

  getErrorByControlName(control: AbstractControl): string {
    let errors = control.errors;
    console.log(errors);
    if (errors.max) {
      return 'Specified number is greater than max allowed: ' + errors.max.max;
    }
    if (errors.min) {
      return 'Specified number is lower than min allowed: ' + errors.min.min;
    }
    if (errors.required) {
      return 'This field is required';
    }
    if (errors.matchPattern) {
      return errors.matchPattern.message;
    }
  }


  routeToDirectories(server: Server): void {
    const objectId = server.objectId;
    this.updateServer(server);
    this.router.navigateByUrl('/directories', {state: {objectId}});
  }

  deleteServer(objectId: bigint): void {
    this.http.delete(this.localApi + "/delete/" + objectId).subscribe(result => {
      this.confirmMessage = "Server deleted successful";

      let changedServer = this.servers.find(this.findIndexToUpdate);
      let index = this.servers.indexOf(changedServer);

      this.servers.splice(index, 1);
    }, error => {
      this.errorMessage = "Error with deleting server";
    })
  }

  testConnection(): void {
    this.http.post<boolean>(this.localApi + '/testConnection', this.insertForm.value).subscribe(result => {
      this.testResult = result ? "Connection established" : "Cant connect";
    }, error => {
      this.testResult = "Error with checking connection";
    })
  }

  addServer(): void {
    const server = this.insertForm.value;

    if (!this.insertForm.valid) {
      return;
    }
    this.http.post(this.localApi + "/add", server).subscribe(result => {
      this.confirmMessage = "Server added";
      server['objectId'] = result;
      this.servers.push(server);
      this.insertForm.reset({});
    }, error => {
      this.inputError = "Cant add server";
    })
  }

  showSettings(server: Server): void {
    this.updateForm.controls.objectId.setValue(server.objectId);
    this.updateForm.controls.parentId.setValue(server.parentId);
    this.updateForm.controls.name.setValue(server.name);
    this.updateForm.controls.ip.setValue(server.ip);
    this.updateForm.controls.port.setValue(server.port);
    this.updateForm.controls.login.setValue(server.login);
    this.updateForm.controls.password.setValue(server.password);
    this.updateForm.controls.protocol.setValue(server.protocol);
    this.updateForm.controls.enabled.setValue(server.enabled);
  }

  updateServerByForm(): void {
    if (!this.updateForm.valid) {
      return;
    }
    const server = this.updateForm.value;

    this.http.put(this.localApi + "/update", server).subscribe(result => {
      this.confirmMessage = "Server updated";

      let changedServer = this.servers.find(this.findIndexToUpdate);
      let index = this.servers.indexOf(changedServer);

      this.servers.splice(index, 1, server);

    }, error => {
      alert("Cant update server");
    })
  }

  updateServer(server: Server) {
    server.lastAccessByUser = new Date();
    this.http.put(this.localApi + "/update", server).subscribe(result => {
    }, error => {
    })
  }

  private findIndexToUpdate(newItem) {
    return newItem.id === this;
  }

}
