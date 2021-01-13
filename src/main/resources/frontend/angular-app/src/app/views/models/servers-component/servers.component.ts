import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Server} from '../../../entity/Server';
import {GlobalConstants} from '../../../constants/global-constants';
import {AuthService} from "../../../services/AuthService";


@Component({
  selector: 'app-servers',
  templateUrl: './servers.component.html'
})
export class ServersComponent {
  insertForm: FormGroup;

  updateForm: FormGroup;

  servers: Server[] = [];

  protocols = [
    'SSH', 'FTP'
  ];

  testResult: boolean;

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder, ) {
    http.get<Server[]>(GlobalConstants.apiUrl + 'api/server/').subscribe(result => {// TODO: Change to all
      console.log(result);
      this.servers = result;
    });

    this.insertForm = this.fb.group({
      name: ['', Validators.required],
      ip: ['', Validators.required],
      port: ['', Validators.required],
      login: ['', Validators.required],
      password: ['', Validators.required],
      protocol: ['', Validators.required]
    });

    this.updateForm = this.fb.group({
      name: ['', Validators.required],
      port: ['', Validators.required],
      login: ['', Validators.required],
      password: ['', Validators.required],
      protocol: ['', Validators.required]
    });
  }
  isLogin(): boolean {
    return this.authService.isLoggedIn();
  }

  routeToDirectories(objectId: bigint): void {
    this.router.navigateByUrl('/directories', {state: {objectId}});
  }

  deleteServer(objectId: bigint): void {
    // TODO: Delete server
  }

  testConnection(): void{
    // TODO: Insert test post
    this.testResult = false;
  }

  addServer(): void {
    // TODO: Insert post
    console.log('Adding button work...');
    this.insertForm.reset({});
  }

  showSettings(server: Server): void {
    this.updateForm.controls.name.setValue(server.name);
    this.updateForm.controls.port.setValue(server.port);
    this.updateForm.controls.login.setValue(server.login);
    this.updateForm.controls.password.setValue(server.password);
    this.updateForm.controls.protocol.setValue(server.protocol);
  }

  updateServer(): void {
    // TODO: Insert update;
  }
}
