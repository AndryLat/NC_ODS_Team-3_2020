import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {AuthService} from "../../../services/AuthService";
import {BigInteger} from "@angular/compiler/src/i18n/big_integer";


@Component({
  selector: 'app-directories',
  templateUrl: './directories.component.html'
})
export class DirectoriesComponent {

  insertForm: FormGroup;

  directories: Directory[] = [];

  constructor(private authService: AuthService, private router: Router, private http: HttpClient, private fb: FormBuilder, private route: ActivatedRoute) {

    this.directories.push({objectId: BigInteger.zero(), path: "aw", enabled: true, lastExistenceCheck: new Date()})

    const parentId = router.getCurrentNavigation().extras.state['objectId'];

    console.log(parentId);

    let params = new HttpParams().set("parentId", parentId)

    http.get<Directory[]>(GlobalConstants.apiUrl + 'api/directory/', {params}).subscribe(result => {
      console.log(result);
      this.directories = result;
    });

    this.insertForm = this.fb.group({
      path: ['', Validators.required],
      mask: ['', Validators.required]
    });
  }

  routeToLogs(objectId: bigint): void {
    this.router.navigateByUrl('/logs', {state: {objectId}});
  }

  deleteDirectory(objectId: BigInteger): void {
    // TODO: Delete server
    this.http.delete(GlobalConstants.apiUrl + 'api/directory/delete/' + objectId).subscribe(() => {
      this.directories = this.directories.filter(item => item.objectId !== objectId);
    });
  }

  addDirectory(): void {
    // TODO: Insert post
    const directory = this.insertForm.value;
    console.log(this.insertForm.value);
    this.http.post(GlobalConstants.apiUrl + 'api/directory/add', directory).subscribe(() => {
      console.log('Complete');
    });
    this.insertForm.reset({});
  }
}
