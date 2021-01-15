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


  routeToLogs(objectId: bigint): void {
    this.router.navigateByUrl('/logs', {state: {objectId}});
  }

  deleteDirectory(objectId: bigint): void {
    this.http.delete(GlobalConstants.apiUrl + 'api/directory/delete/' + objectId).subscribe(() => {
      this.directories = this.directories.filter(item => item.objectId !== objectId);
    });
  }

  addDirectory(): void {
    const directory = this.insertForm.value;
    console.log(this.insertForm.value);
    this.http.post(GlobalConstants.apiUrl + 'api/directory/add', directory).subscribe(() => {
      console.log('Complete');
    });
    this.insertForm.reset({});
  }
}
