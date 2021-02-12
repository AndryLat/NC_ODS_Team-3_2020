import {Component, Inject} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {FormBuilder} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {LogFile} from "../../../../entity/LogFile";
import {GlobalConstants} from "../../../../constants/global-constants";
import {Directory} from "../../../../entity/Directory";

@Component({
  selector: 'app-log-file-update-form-modal',
  templateUrl: './log-file-update-form-modal.component.html',
  styleUrls: ['./log-file-update-form-modal.component.css']
})
export class DirectoryLogFileUpdateFormModalComponent{

  currentDirectory: Directory;

  filesFromDB: LogFile[];
  filesFromServer: LogFile[];
  filesForUpdate: LogFile[] = [];
  errMes:string;
  flag:boolean = false;

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<DirectoryLogFileUpdateFormModalComponent>,
              @Inject(MAT_DIALOG_DATA) dir) {
    this.currentDirectory = dir;
    this.getFilesForUpdate(this.currentDirectory);
  }

  ngAfterContentChecked(): void{
    this.splitFilesServerBD(this.filesFromDB, this.filesFromServer);
  }

  getFilesFromDB(objectId: String): void {
    let params = new HttpParams().set('objectId', JSON.stringify(objectId));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files/database',{params}).subscribe(result => {
      result.forEach(result => result.checked = true);
      this.filesFromDB = result;
      console.log(this.filesFromDB);
    }, error => {
      this.errMes = 'Error with receiving files from db';
    });
  }

  getFilesFromServer(dir: Directory): void {
    let params = new HttpParams().set('directoryInString', JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files', {params}).subscribe(result => {
      result.forEach(result => {
        result.checked = false;
        result.parentId = dir.objectId;
      });
      this.filesFromServer = result;
      console.log(this.filesFromServer);
    }, error => {
      this.errMes = 'Error with receiving files from server';
    });
  }

  getFilesForUpdate(dir: Directory): void {
    //dir.lastExistenceCheck = null;
    const directory = new Directory();
    directory.objectId = dir.objectId;
    directory.parentId = dir.parentId;
    directory.path = dir.path;
    if (this.filesFromDB === undefined) {
      this.getFilesFromDB(directory.objectId);
    }
    if (this.filesFromServer === undefined) {
      this.getFilesFromServer(directory);
    }
  }

  splitFilesServerBD(logFilesFromDB: LogFile[], logFilesFromServer: LogFile[]) {
    this.filesForUpdate = [];
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
        this.flag = true;
      });
      console.log(this.filesForUpdate);
    }
  }

  closeUpdateFiles(): void {
    this.filesFromDB = undefined;
    this.filesFromServer = undefined;
    this.filesForUpdate = [];
    this.dialogRef.close();
    this.flag = false;
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
          this.errMes = 'Something went wrong with files';
          //msg = 'Something went wrong with files';
        });
        //
      }
    });
    this.dialogRef.close(this.currentDirectory);
  }

  changeStatusFile(name: string, files: LogFile[]) {
    files.find(f => f.name == name).checked = !files.find(f => f.name == name).checked;
  }

}
