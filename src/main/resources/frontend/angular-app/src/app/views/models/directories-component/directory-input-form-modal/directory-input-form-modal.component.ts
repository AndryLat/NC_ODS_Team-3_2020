import {Component} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {RouteVariableNameConstants} from "../../../../constants/route-variable-names-constants";
import {Directory} from "../../../../entity/Directory";
import {GlobalConstants} from "../../../../constants/global-constants";
import {LogFile} from "../../../../entity/LogFile";
import {faCheck, faTimes} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-directory-input-form-modal',
  templateUrl: './directory-input-form-modal.component.html',
  styleUrls: ['./directory-input-form-modal.component.css']
})
export class DirectoryInputFormModalComponent{

  insertForm: FormGroup;
  searchForm: FormGroup;
  serverId: string;

  dir: Directory;
  testResult: string;
  testBool: boolean = false;

  files: LogFile[] = [];
  filesFromDir: LogFile[] = [];
  getResult: string;


  addingFiles: LogFile[];
  icon = faTimes;

  msg: string;
  currentDir: Directory;
  flag = false;

  constructor(private http: HttpClient, private fb: FormBuilder,
              private dialogRef: MatDialogRef<DirectoryInputFormModalComponent>,) {
    this.insertForm = this.fb.group({
      path: ['', Validators.required]
    });
    this.searchForm = this.fb.group({
      searchText: ['', Validators.required]
    });
    this.serverId = localStorage.getItem(RouteVariableNameConstants.serverToDirectoryVariableName);
  }

  testDirectory(): void {
    this.dir = new Directory();
    this.dir.parentId = this.serverId;
    this.dir.path = this.insertForm.value.path;

    let params = new HttpParams().set('directoryInString', JSON.stringify(this.dir));

    this.http.get<boolean>(GlobalConstants.apiUrl + 'api/directory/test', {params}).subscribe(result => {
      this.testResult = result ? 'Connection established' : 'Can\'t connect';
      this.testBool = result;
      this.dir = result ? this.dir : undefined;
    }, error => {
      this.dir = undefined;
      this.testBool = false;
      this.testResult = 'Error with checking connection';
    });
  }

  getFiles(): void {
    if (this.dir === undefined) {
      this.testDirectory();
    }
    if (this.dir !== undefined) {
      let params = new HttpParams().set('directoryInString', JSON.stringify(this.dir));

      this.http.get<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files', {params}).subscribe(result => {
        result.forEach(result => result.checked = true);
        this.filesFromDir = this.files = result;
        this.flag = true;
      }, error => {
        this.files = undefined;
        this.getResult = 'Error with receiving files';
      });
    }
  }

  search(): void {
    const val = this.searchForm.value;
    if(val.searchText == '' || val.searchText == null){
      this.files = this.filesFromDir;
    }
    if (val.searchText) {
      this.files = [];
      this.filesFromDir.forEach(result => {
        if (result.fileName.includes(val.searchText)) {
          this.files.push(result);
        }
      });
    }
  }

  addDirectory(): void {
    if (this.dir === undefined) {
      this.testDirectory();
    }
      this.http.post<Directory>(GlobalConstants.apiUrl + 'api/directory/add', this.dir).subscribe(result => {
        this.currentDir = result;
        this.dir = undefined;
        this.insertForm.reset({});
        this.flag = false;
        this.msg = 'Directory added';
        this.addFilesToDb();
      }, error => {
        this.msg = 'Something went wrong with directory';
      });
  }

  addFilesToDb(): void {
    if (this.files === undefined) {
      this.getFiles();
    }
    if (this.files !== undefined) {
      this.addFiles(this.currentDir, this.files);
    }
  }

  addFiles(dir: Directory, files: LogFile[]): void {
    files.forEach(result => result.parentId = dir.objectId);
    this.addingFiles = [];
    files.forEach(result => {
      if (result.checked) {
        this.addingFiles.push(result);
      }
    });
    if (this.addingFiles == []) {
      this.msg = 'AddingFiles is empty';
    } else {
      this.http.post<LogFile[]>(GlobalConstants.apiUrl + 'api/logFile/files/add', this.addingFiles).subscribe(result => {
        console.log('Complete', this.addingFiles);
        this.addingFiles = undefined;
        this.files = undefined;
        this.dialogRef.close(this.currentDir);
      }, error => {
        this.msg = 'Something went wrong with files';
      });
    }
  }

  changePath():void{
    this.dir = undefined;
  }

  changeStatusFile(name: string, files: LogFile[]) {
    files.find(f => f.name == name).checked = !files.find(f => f.name == name).checked;
  }

  checkAll(files: LogFile[]){
    if(files !== undefined && files != null){
      if(this.icon == faTimes){
        files.forEach(result => result.checked = false);
        this.icon = faCheck;
      } else {
        files.forEach(result => result.checked = true);
        this.icon = faTimes;
      }
    }
  }

  closeDir(): void {
    this.dir = undefined;
    this.currentDir = undefined;
    this.addingFiles = undefined;
    this.files = undefined;
    this.flag = false;
    this.insertForm.reset();
    this.searchForm.reset();
    this.dialogRef.close();
  }
}

