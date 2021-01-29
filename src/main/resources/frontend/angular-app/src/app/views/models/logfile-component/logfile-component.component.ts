import { Component, OnInit } from '@angular/core';
import {LogFilePage} from "../../../pageable/LogFilePage";
import {LogFile} from "../../../entity/LogFile";
import {RouteVariableNameConstants} from "../../../constants/route-variable-names-constants";
import {Router} from "@angular/router";
import {HttpClient, HttpParams} from "@angular/common/http";
import {faEye, faSignInAlt, faStream, faTrashAlt} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-logfile-component',
  templateUrl: './logfile-component.component.html',
  styleUrls: ['./logfile-component.component.css']
})
export class LogfileComponentComponent implements OnInit {
  errorMessage: string;
  confirmMessage: string;
  logFilePage: LogFilePage;
  localApi: string = "api/logFile";

  logsIcon = faStream;
  proceedIcon = faSignInAlt;
  deleteIcon = faTrashAlt;
  realTimeIcon = faEye;

  directoryId: string;

  constructor(private router:Router,private http: HttpClient) {
    this.directoryId = localStorage.getItem(RouteVariableNameConstants.directoryToLogFilesVariableName);
  }

  ngOnInit(): void {
    this.getFilesFromPage(1);
  }

  routeToLogs(file: LogFile) {
    const objectId = file.objectId;
    localStorage.setItem(RouteVariableNameConstants.logFileToLogsVariableName,objectId);
    localStorage.removeItem(RouteVariableNameConstants.directoryToLogsVariableName);
    this.router.navigateByUrl('/logs');
  }

  deleteFile(objectId: string) {
    this.http.delete(this.localApi + "/delete/" + objectId).subscribe(result => {
      this.confirmMessage = "File deleted successfully";

      let changedServer = this.logFilePage.content.find(deletedElement => deletedElement.objectId === objectId);
      let index = this.logFilePage.content.indexOf(changedServer);

      this.logFilePage.content.splice(index, 1);
    }, error => {
      this.errorMessage = "Error with deleting file";
    })
  }

  getFilesFromPage(pageNumber: number) {
    let params = new HttpParams()
      .set("directoryId", this.directoryId)
      .set("page", pageNumber.toString());

    this.http.get<LogFilePage>(this.localApi+'/', {params}).subscribe(result => {
      console.log(result);
      this.logFilePage = result;
      this.logFilePage.number = this.logFilePage.number + 1;// In Spring pages start from 0.
      console.log(this.logFilePage);
    });
  }

  routeToRealtime(objectId: string) {
    localStorage.setItem(RouteVariableNameConstants.logFileToRealTimeVariableName,objectId);
    this.router.navigateByUrl('/realtime');
  }
}
