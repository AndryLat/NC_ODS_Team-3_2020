import {Component, Inject, OnInit} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {FormBuilder} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {LogFile} from "../../../../entity/LogFile";
import {GlobalConstants} from "../../../../constants/global-constants";
import {Directory} from "../../../../entity/Directory";
import {faCheck, faTimes} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-log-file-update-form-modal',
  templateUrl: './log-file-update-form-modal.component.html',
  styleUrls: ['./log-file-update-form-modal.component.css']
})
export class DirectoryLogFileUpdateFormModalComponent implements OnInit{

  currentDirectory: Directory;
  filesFromDB: LogFile[];
  filesFromServer: LogFile[];
  filesForUpdate: LogFile[];
  errMes:string;
  flag:boolean = false;
  icon = faTimes;

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<DirectoryLogFileUpdateFormModalComponent>,
              @Inject(MAT_DIALOG_DATA) dir){
    this.currentDirectory = dir;
  }

  ngOnInit(){
    this.getFilesForUpdate(this.currentDirectory);
  }

  getFilesFromDB(objectId: String): void {
    this.filesFromDB = [];
    let params = new HttpParams().set('objectId', JSON.stringify(objectId));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files/database',{params}).subscribe(result => {
      result.forEach(res => res.checked = true);
      this.filesFromDB = result;
      if(this.filesFromDB && this.filesFromServer){
        this.splitFilesServerBD(this.filesFromDB, this.filesFromServer);
      }
    }, error => {
      this.errMes = 'Error with receiving files from db';
    });
  }

  getFilesFromServer(dir: Directory): void {
    this.filesFromServer = [];
    let params = new HttpParams().set('directoryInString', JSON.stringify(dir));

    this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files', {params}).subscribe(result => {
      if(result.length == 0){
        this.errMes = 'Error with receiving files from server';
      }
      result.forEach(res => {
        res.checked = false;
        res.parentId = dir.objectId;
      });
      this.filesFromServer = result;
      if(this.filesFromDB && this.filesFromServer){
        this.splitFilesServerBD(this.filesFromDB, this.filesFromServer);
      }
    }, error => {
      this.errMes = 'Error with receiving files from server';
    });
  }

  getFilesForUpdate(dir: Directory): void {
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
            result.checked = res.checked;
          }
        });
        this.filesForUpdate.push(result);
        this.flag = true;
      });
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
        }, error => {
          this.errMes = 'Something went wrong with files';
        });
        //
      }
    });
    this.dialogRef.close(this.currentDirectory);
  }

  changeStatusFile(name: string, files: LogFile[]) {
    files.find(f => f.name == name).checked = !files.find(f => f.name == name).checked;
  }

  checkAll(){
    if(this.filesForUpdate !== undefined && this.filesForUpdate != null){
      if(this.icon == faTimes){
        this.filesForUpdate.forEach(result => result.checked = false);
        this.icon = faCheck;
      } else {
        this.filesForUpdate.forEach(result => result.checked = true);
        this.icon = faTimes;
      }
    }
  }

}
