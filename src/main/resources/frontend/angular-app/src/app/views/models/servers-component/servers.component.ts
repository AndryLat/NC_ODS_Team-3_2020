import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Server} from '../../../entity/Server';
import {GlobalConstants} from '../../../constants/global-constants';
import {AuthService} from "../../../services/AuthService";
import {faCheck, faCogs, faSignInAlt, faTimes, faTrashAlt} from '@fortawesome/free-solid-svg-icons';
import {matchPattern} from "../../../services/validators/matchPatternValidator";
import {ServerPage} from "../../../pageable/ServerPage";
import {EAVObject} from "../../../entity/EAVObject";
import {RouteVariableNameConstants} from "../../../constants/route-variable-names-constants";

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

  serverPage: ServerPage;

  protocols = [
    'SSH', 'FTP'
  ];

  testResult: string;

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder,) {

    this.insertForm = this.fb.group({
      name: ['', [Validators.required,Validators.maxLength(64)]],
      ip: ['', [Validators.required, matchPattern(/^[a-zA-Z0-9.]+$/, "Special characters is not allowed"),Validators.maxLength(128)]],
      port: ['', [Validators.required, Validators.min(0), Validators.max(65535), matchPattern(/[0-9]+/, "Only numbers allowed"),Validators.maxLength(5)]],
      login: ['', [Validators.required,Validators.maxLength(64)]],
      password: ['', [Validators.required,Validators.maxLength(128)]],
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
    this.getServersFromPage(1);
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
    if(errors.maxLength){
      return 'Max allowed length: ' + errors.maxLength.requiredLength;
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
    localStorage.setItem(RouteVariableNameConstants.serverToDirectoryVariableName,objectId);
    this.router.navigateByUrl('/directories');
  }

  deleteServer(objectId: string): void {
    this.http.delete(this.localApi + "/delete/" + objectId).subscribe(result => {
      this.confirmMessage = "Server deleted successfully";

      let changedServer = this.serverPage.content.find(deletedElement => deletedElement.objectId === objectId);
      let index = this.serverPage.content.indexOf(changedServer);

      this.serverPage.content.splice(index, 1);
    }, error => {
      this.errorMessage = "Error with deleting server";
    })
  }

  testConnection(): void {
    this.http.post<boolean>(this.localApi + '/testConnection', this.insertForm.value).subscribe(result => {
      this.testResult = result ? "Connection established" : "Can't connect to server";
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
      this.getServersFromPage(1);
      this.insertForm.reset({});
    }, error => {
      this.inputError = "Сan't add server";
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

      let index = this.getIndexByObjectIdOfObject(server);
      this.serverPage.content.splice(index, 1, server);

    }, error => {
      alert("Сan't update server");
    })
  }

  updateServer(server: Server) {
    server.lastAccessByUser = new Date();
    this.http.put(this.localApi + "/update", server).subscribe(result => {
    }, error => {
    })
  }

  getServersFromPage(pageNumber: number): void {

    let params = new HttpParams()
      .set("page", pageNumber.toString());

    this.http.get<ServerPage>(this.localApi+'/', {params}).subscribe(result => {
      console.log(result);
      this.serverPage = result;
      this.serverPage.number = this.serverPage.number + 1;// In Spring pages start from 0.
      console.log(this.serverPage);
    });
  }

  getIndexByObjectIdOfObject(object: EAVObject): number{
    let changedServer = this.serverPage.content.find(changedElement => changedElement.objectId === object.objectId);
    return this.serverPage.content.indexOf(changedServer);
  }
}
