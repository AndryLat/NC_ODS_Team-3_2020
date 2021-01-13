import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {AuthService} from "../../../services/AuthService";


@Component({
  selector: 'app-directories',
  templateUrl: './directories.component.html'
})
export class DirectoriesComponent {

  insertForm: FormGroup;

  directories: Directory[] = [];

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder, ) {

    http.get<Directory>(GlobalConstants.apiUrl + 'directory/id/5').subscribe(result => {
      console.log(result);
      this.directories.push(result);
    });

    this.insertForm = this.fb.group({
      path: ['', Validators.required],
      mask: ['', Validators.required]
    });
  }
  isLogin(): boolean {
    return this.authService.isLoggedIn();
  }

  routeToLogs(objectId: bigint): void {
    this.router.navigateByUrl('/logs', {state: {objectId}});
  }

  deleteDirectory(objectId: bigint): void {
    // TODO: Delete server
  }

  addDirectory(): void {
    // TODO: Insert post
    console.log('Adding button work...');
    this.insertForm.reset({});
  }
}
