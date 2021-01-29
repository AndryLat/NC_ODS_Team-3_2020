import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {AuthService} from "../../../services/AuthService";
import {LogFile} from "../../../entity/LogFile";
import {DirectoryPage} from "../../../pageable/DirectoryPage";
import {RouteVariableNameConstants} from "../../../constants/route-variable-names-constants";
import {faRedoAlt, faSignInAlt, faStream, faTrashAlt} from '@fortawesome/free-solid-svg-icons';


@Component({
  selector: 'app-directories',
  templateUrl: './directories.component.html'
})
export class DirectoriesComponent implements OnInit {

  logsIcon = faStream;
  proceedIcon = faSignInAlt;
  deleteIcon = faTrashAlt;
  updateIcon = faRedoAlt;

  insertForm: FormGroup;
  searchForm: FormGroup;

  directories: Directory[] = [];

  dir:Directory;
  currentDir: Directory;

  files: LogFile[] = [];
  filesFromDir: LogFile[] = [];
  addingFiles: LogFile[];

  filesFromDB: LogFile[];
  filesFromServer: LogFile[];
  filesForUpdate: LogFile[] = [];

  flag:boolean = true;


  directoryPage: DirectoryPage;

  serverId: string;
  testResult: string;
  getResult: string;
  msg: string;

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
    this.serverId = localStorage.getItem(RouteVariableNameConstants.serverToDirectoryVariableName);
  }

  getDirectoriesFromPage(pageNumber: number): void {

    let params = new HttpParams()
      .set("parentId", this.serverId.toString())
      .set("page", pageNumber.toString());

    this.http.get<DirectoryPage>(GlobalConstants.apiUrl + 'api/directory/', {params}).subscribe(result => {
      console.log(result);
      this.directoryPage = result;
      this.directoryPage.number = this.directoryPage.number + 1;// In Spring pages start from 0.
      console.log(this.directoryPage);
    });
  }

  ngOnInit(): void {
    this.getDirectoriesFromPage(1);
  }


  routeToLogs(dir: Directory): void {
    localStorage.setItem(RouteVariableNameConstants.directoryToLogsVariableName,dir.objectId);
    localStorage.removeItem(RouteVariableNameConstants.logFileToLogsVariableName);
    this.updateDirectory(dir);
    this.router.navigateByUrl('/logs');
  }

  routeToLogFiles(objectId: string): void {
    localStorage.setItem(RouteVariableNameConstants.directoryToLogFilesVariableName,objectId);
    this.router.navigateByUrl('/logFiles');
  }
  updateDirectory(dir: Directory) {
    dir.lastExistenceCheck = new Date();
    this.http.put(GlobalConstants.apiUrl + 'api/directory/update', dir).subscribe()
  }

  deleteDirectory(objectId: string): void {
    this.http.delete(GlobalConstants.apiUrl + 'api/directory/delete/' + objectId).subscribe(() => {
      let deletedDirectory = this.directoryPage.content.find(deletedElement => deletedElement.objectId == objectId);
      let index = this.directoryPage.content.indexOf(deletedDirectory);
      this.directoryPage.content.splice(index, 1);
    });
  }

  addDirectory(): void {
    if(this.dir === undefined) this.testDirectory()
    console.log(this.insertForm.value);
    this.http.post<Directory>(GlobalConstants.apiUrl + 'api/directory/add', this.dir).subscribe(result => {
      this.currentDir = result;
      this.dir = undefined
      this.insertForm.reset({});
      this.msg = 'Directory added';
      console.log('Add Directory ', result);
      this.addFilesToDb()
    }, error=>{
        this.msg = 'Something went wrong with directory';
    });
  }

  addFilesToDb():void{
    if(this.files === undefined) {
      this.getFiles()
    }
    this.addFiles(this.currentDir, this.files)
  }

  testDirectory(): void{
    this.dir = new Directory();
    this.dir.parentId = this.serverId;
    this.dir.path = this.insertForm.value.path

    let params =  new HttpParams().set("directoryInString", JSON.stringify(this.dir));

    this.http.get<boolean>(GlobalConstants.apiUrl + 'api/directory/test', {params}).subscribe(result => {
      this.testResult = result ? "Connection established" : "Can't connect";
    }, error => {
      this.testResult = "Error with checking connection";
    })
  }

  getFiles():void{
    if(this.dir === undefined) this.testDirectory()

    let params =  new HttpParams().set("directoryInString", JSON.stringify(this.dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/directory/files', {params}).subscribe(result => {
      result.forEach(result => result.checked = true)
      this.filesFromDir = this.files = result;
    }, error => {
      this.getResult = 'Error with receiving files';
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
      }, error => {
        this.msg = 'Something went wrong with files';
      });
    }
  }

  getFilesFromDB(dir: Directory):void{
    let params =  new HttpParams().set("directoryInString", JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/filesDB', {params}).subscribe(result => {
      result.forEach(result => result.checked = true)
      this.filesFromDB = result
      console.log(this.filesFromDB)
    }, error => {
    })
  }

  getFilesFromServer(dir: Directory):void{
    let params =  new HttpParams().set("directoryInString", JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/directory/files', {params}).subscribe(result => {
      result.forEach(result => {
        result.checked = false
        result.parentId = dir.objectId
      })
      this.filesFromServer = result
      console.log(this.filesFromServer)
    }, error => {
      //this.Result = 'Error with receiving files';
    })
  }

  getFilesForUpdate(dir: Directory):void{
    //dir.lastExistenceCheck = null;
    const directory = new Directory()
    directory.objectId = dir.objectId
    directory.parentId = dir.parentId
    directory.path = dir.path
    if(this.filesFromDB === undefined) {this.getFilesFromDB(directory)}
    if(this.filesFromServer === undefined) {this.getFilesFromServer(directory)}
  }

  splitFilesServerBD(logFilesFromDB: LogFile[], logFilesFromServer: LogFile[]){
    if(logFilesFromDB && logFilesFromServer){
      logFilesFromServer.forEach(result =>{
        logFilesFromDB.forEach(res =>{
          if(result.fileName == res.fileName){
            if(res.checked){
              result.checked = true
            } else {
              result.checked = false
            }
          }
        });
        this.filesForUpdate.push(result)
      });
      console.log(this.filesForUpdate)
    }
  }

  closeUpdateFiles():void{
    this.filesFromDB = undefined
    this.filesFromServer = undefined
    this.filesForUpdate = []
  }

  updateFiles(){
    this.filesForUpdate.forEach(result => {
      this.filesFromDB.forEach(res =>{
        if(result.fileName == res.fileName){
          if(result.checked != res.checked){
            //delete result
            this.http.delete(GlobalConstants.apiUrl + 'api/logFile/delete/' + res.objectId).subscribe(() => {
              //
            });
          }else{result.checked = false;}
        }
      })
      if((result.checked == true)){
        //adding result
        this.http.post<LogFile>(GlobalConstants.apiUrl + 'api/logFile/file/add', result).subscribe(result => {
          console.log('Adding ',result);
        }, error => {
          //msg = 'Something went wrong with files';
        });
        //
      }
    })
  }

  changeStatusFile(name:String, files:LogFile[]){
    files.find(f => f.name == name).checked = !files.find(f => f.name == name).checked;
  }

  closeDir():void{
    this.dir = undefined
    this.addingFiles = undefined
    this.files = undefined
    this.insertForm.reset();
    this.searchForm.reset()
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
