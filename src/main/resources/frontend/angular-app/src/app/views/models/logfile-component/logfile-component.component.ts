import {Component, OnInit} from '@angular/core';
import {LogFilePage} from '../../../pageable/LogFilePage';
import {LogFile} from '../../../entity/LogFile';
import {RouteVariableNameConstants} from '../../../constants/route-variable-names-constants';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {
  faEye,
  faPlus,
  faSignInAlt,
  faStream,
  faTrashAlt
} from '@fortawesome/free-solid-svg-icons';
import {GlobalConstants} from '../../../constants/global-constants';
import {Directory} from '../../../entity/Directory';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-logfile-component',
  templateUrl: './logfile-component.component.html'
})
export class LogfileComponentComponent implements OnInit {
  errorMessage: string;
  confirmMessage: string;
  logFilePage: LogFilePage;
  localApi: string = 'api/logFile';

  plusIcon = faPlus;
  logsIcon = faStream;
  proceedIcon = faSignInAlt;
  deleteIcon = faTrashAlt;
  realTimeIcon = faEye;

  searchForm: FormGroup;
  insertForm: FormGroup;


  directory: Directory;

  files: LogFile[] = [];
  filesFromDir: LogFile[] = [];

  filesFromDB: LogFile[];
  filesFromServer: LogFile[];
  filesForUpdate: LogFile[] = [];

  getResult: string;

  constructor(private router: Router, private http: HttpClient) {
    this.directory = JSON.parse(localStorage.getItem(RouteVariableNameConstants.directoryToLogFilesVariableName));
  }

  ngOnInit(): void {
    this.getFilesFromPage(1);
  }

  routeToLogs(file: LogFile) {
    const objectId = file.objectId;
    localStorage.setItem(RouteVariableNameConstants.logFileToLogsVariableName, objectId);
    localStorage.removeItem(RouteVariableNameConstants.directoryToLogsVariableName);
    this.router.navigateByUrl('/logs');
  }

  deleteFile(objectId: string) {
    this.http.delete(this.localApi + '/delete/' + objectId).subscribe(result => {
      this.confirmMessage = 'File deleted successfully';

      let changedServer = this.logFilePage.content.find(deletedElement => deletedElement.objectId === objectId);
      let index = this.logFilePage.content.indexOf(changedServer);

      this.logFilePage.content.splice(index, 1);
    }, error => {
      this.errorMessage = 'Error with deleting file';
    });
  }

  getFilesFromPage(pageNumber: number) {
    let params = new HttpParams()
      .set('directoryId', this.directory.objectId)
      .set('page', pageNumber.toString());

    this.http.get<LogFilePage>(this.localApi + '/', {params}).subscribe(result => {
      console.log(result);
      this.logFilePage = result;
      this.logFilePage.number = this.logFilePage.number + 1;// In Spring pages start from 0.
      console.log(this.logFilePage);
    });
  }

  routeToRealtime(objectId: string) {
    localStorage.setItem(RouteVariableNameConstants.logFileToRealTimeVariableName, objectId);
    this.router.navigateByUrl('/realtime');
  }

  getFiles(): void {
    let params = new HttpParams().set('directoryInString', JSON.stringify(this.directory));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files', {params}).subscribe(result => {
      result.forEach(result => result.checked = true);
      this.filesFromDir = this.files = result;
    }, error => {
      this.getResult = 'Error with receiving files';
    });
  }

  search(): void {
    const val = this.searchForm.value;
    if (val.searchText) {
      this.files = [];
      this.filesFromDir.forEach(result => {
        if (result.fileName.includes(val.searchText)) {
          this.files.push(result);
        }
      });
    }
  }

  closeUpdateFiles(): void {
    this.filesFromDB = undefined;
    this.filesFromServer = undefined;
    this.filesForUpdate = [];
  }

  splitFilesServerBD(logFilesFromDB: LogFile[], logFilesFromServer: LogFile[]) {
    if (logFilesFromDB && logFilesFromServer) {
      logFilesFromServer.forEach(result => {
        logFilesFromDB.forEach(res => {
          if (result.fileName == res.fileName) {
            if (res.checked) {
              result.checked = true;
            } else {
              result.checked = false;
            }
          }
        });
        this.filesForUpdate.push(result);
      });
      console.log(this.filesForUpdate);
    }
  }

  updateFiles() {
    this.filesForUpdate.forEach(result => {
      this.filesFromDB.forEach(res => {
        if (result.fileName == res.fileName) {
          if (result.checked != res.checked) {
            //delete result
            this.http.delete(GlobalConstants.apiUrl + 'api/logFile/delete/' + res.objectId).subscribe(() => {
              //
            });
          } else {
            result.checked = false;
          }
        }
      });
      if ((result.checked == true)) {
        //adding result
        this.http.post<LogFile>(GlobalConstants.apiUrl + 'api/logFile/file/add', result).subscribe(result => {
          console.log('Adding ', result);
        }, error => {
          //msg = 'Something went wrong with files';
        });
        //
      }
    });
  }

  getFilesForUpdate(): void {
    const directory = new Directory();
    directory.objectId = this.directory.objectId;
    directory.parentId = this.directory.parentId;
    directory.path = this.directory.path;
    if (this.filesFromDB === undefined) {
      this.getFilesFromDB(directory);
    }
    if (this.filesFromServer === undefined) {
      this.getFilesFromServer(directory);
    }
  }

  getFilesFromServer(dir: Directory): void {
    let params = new HttpParams().set('directoryInString', JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/directory/files', {params}).subscribe(result => {
      result.forEach(result => {
        result.checked = false;
        result.parentId = dir.objectId;
      });
      this.filesFromServer = result;
      console.log(this.filesFromServer);
    }, error => {
      //this.Result = 'Error with receiving files';
    });
  }

  getFilesFromDB(dir: Directory): void {
    let params = new HttpParams().set('directoryInString', JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/filesDB', {params}).subscribe(result => {
      result.forEach(result => result.checked = true);
      this.filesFromDB = result;
      console.log(this.filesFromDB);
    }, error => {
    });
  }

  changeStatusFile(name: string, files: LogFile[]) {
    files.find(f => f.name == name).checked = !files.find(f => f.name == name).checked;
  }
}
