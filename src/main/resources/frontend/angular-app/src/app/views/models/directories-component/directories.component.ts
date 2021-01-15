import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {AuthService} from "../../../services/AuthService";


@Component({
  selector: 'app-directories',
  templateUrl: './directories.component.html'
})
export class DirectoriesComponent implements OnInit {

  insertForm: FormGroup;

  directories: Directory[] = [];

  serverId: string;

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder,
              private route: ActivatedRoute) {

    this.insertForm = this.fb.group({
      path: ['', Validators.required],
      mask: ['', Validators.required]
    });
    this.serverId = this.router.getCurrentNavigation().extras.state['objectId'];

  }

  ngOnInit(): void {
    this.directories.push(new Directory());

    let params = new HttpParams().set("parentId", this.serverId)

    this.http.get<Directory[]>(GlobalConstants.apiUrl + 'api/directory/', {params}).subscribe(result => {
      console.log(result);
      this.directories = result;
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
