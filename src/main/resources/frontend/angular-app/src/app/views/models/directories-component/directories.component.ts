import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {AuthService} from "../../../services/AuthService";
import {LogFile} from "../../../entity/LogFile";


@Component({
  selector: 'app-directories',
  templateUrl: './directories.component.html'
})
export class DirectoriesComponent implements OnInit {

  insertForm: FormGroup;

  searchForm: FormGroup;

  directories: Directory[] = [];

  dir:Directory;

  currentDir: Directory;

  files: LogFile[] = [];

  filesFromDir: LogFile[] = [];

  addingFiles: LogFile[];

  serverId: bigint;

  testResult: string;

  constructor(private authService: AuthService,
              private router: Router,
              private http: HttpClient,
              private fb: FormBuilder) {

    this.insertForm = this.fb.group({
      path: ['', Validators.required]
    });
    this.searchForm = this.fb.group({
      searchText: ['', Validators.required]
    });
    this.serverId = this.router.getCurrentNavigation().extras.state['objectId'];

  }

  ngOnInit(): void {
    this.directories.push(new Directory());

    let params = new HttpParams().set("parentId", this.serverId.toString())

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
    if(this.dir === undefined) this.testDirectory()
    console.log(this.insertForm.value);
    this.http.post<Directory>(GlobalConstants.apiUrl + 'api/directory/add', this.dir).subscribe(result => {
      this.currentDir = result;
      this.dir = undefined
      this.insertForm.reset({});
      console.log('Add Directory ', result);
      this.addFilesToBd()
    });
  }

  addFilesToBd():void{
    if(this.files === undefined) {
      this.getFiles()
    }else{
      this.addFiles(this.currentDir, this.files)
    }
  }

  testDirectory(): void{
    this.dir = new Directory();
    this.dir.parentId = this.serverId;
    this.dir.path = this.insertForm.value.path

    this.http.post<boolean>(GlobalConstants.apiUrl + 'api/directory/test', this.dir).subscribe(result => {
      this.testResult = result ? "Connection established" : "Cant connect";
    }, error => {
      this.testResult = "Error with checking connection";
    })
  }

  getFiles():void{
    if(this.dir === undefined) this.testDirectory()
    this.http.post<LogFile[]>(GlobalConstants.apiUrl + 'api/directory/files', this.dir).subscribe(result => {
      result.forEach(result => result.checked = true)
      this.filesFromDir = this.files = result;
    })
  }

  addFiles(dir: Directory, files:LogFile[]):void{
    files.forEach(result => result.parentId = dir.objectId);
    this.addingFiles = [];
    files.forEach(result => {if(result.checked) this.addingFiles.push(result)})
    if(this.addingFiles == []){
      console.log('AddingFiles is empty');
    } else{
      this.http.post<LogFile[]>(GlobalConstants.apiUrl + 'api/directory/files/add', this.addingFiles).subscribe(result => {
        console.log('Complete',this.addingFiles);
        this.addingFiles = undefined
        this.files = undefined
        this.ngOnInit()
      });
    }
  }

  changeStatusFile(name:String){
    this.files.find(f => f.name == name).checked = !this.files.find(f => f.name == name).checked;
  }

  search():void{
    const val = this.searchForm.value
    if(val.searchText){
      this.files = []
      this.filesFromDir.forEach(result => {
        if(result.fileName.includes(val.searchText)){
          this.files.push(result)
        }
      })
    }
  }
}
