import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Server} from '../../../entity/Server';
import {GlobalConstants} from '../../../constants/global-constants';
import {
  faCheck,
  faCogs,
  faFolderOpen,
  faPlus,
  faPowerOff,
  faQuestionCircle,
  faSync,
  faTimes,
  faTrashAlt
} from '@fortawesome/free-solid-svg-icons';
import {ServerPage} from '../../../pageable/ServerPage';
import {EAVObject} from '../../../entity/EAVObject';
import {RouteVariableNameConstants} from '../../../constants/route-variable-names-constants';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ServerInputFormModalComponent} from './server-input-form-modal/server-input-form-modal.component';
import {ServerUpdateFormModalComponent} from './server-update-form-modal/server-update-form-modal.component';
import {AlertBarService} from '../../../services/AlertBarService';

@Component({
  selector: 'app-servers',
  templateUrl: './servers.component.html'
})
export class ServersComponent implements OnInit, OnDestroy {
  refreshIcon = faSync;
  tooltipIcon = faQuestionCircle;
  plusIcon = faPlus;
  proceedIcon = faFolderOpen;
  settingIcon = faCogs;
  deleteIcon = faTrashAlt;
  switchIcon = faPowerOff;

  enabledIcon = faCheck;
  disabledIcon = faTimes;


  localApi: string = GlobalConstants.apiUrl + 'api/server';

  serverPage: ServerPage;

  constructor(private router: Router,
              private http: HttpClient,
              private dialog: MatDialog,
              private alertBarService: AlertBarService) {
  }

  ngOnDestroy(): void {
    this.alertBarService.resetMessage();
  }

  ngOnInit(): void {
    this.getServersFromPage(1);
  }

  routeToDirectories(server: Server): void {
    const objectId = server.objectId;
    let accessedServer = new Server();
    accessedServer.objectId = server.objectId;
    accessedServer.parentId = server.parentId;
    accessedServer.name = server.name;
    accessedServer.lastAccessByUser = new Date();
    accessedServer.objectTypeId = server.objectTypeId;
    console.log(accessedServer);

    this.http.put(this.localApi + '/updateLastAccessByUser', accessedServer).subscribe(result => {
    });
    localStorage.setItem(RouteVariableNameConstants.serverToDirectoryVariableName, objectId);
    this.router.navigateByUrl('/directories');
  }

  deleteServer(server: Server): void {
    this.http.delete(this.localApi + '/delete/' + server.objectId).subscribe(result => {
      this.alertBarService.setConfirmMessage('Server ' + server.name + ' deleted successfully');

      let changedServer = this.serverPage.content.find(deletedElement => deletedElement.objectId === server.objectId);
      let index = this.serverPage.content.indexOf(changedServer);

      this.serverPage.content.splice(index, 1);
    }, error => {
      this.alertBarService.setErrorMessage('Something gone wrong. Try again later.');
    });
  }

  getServersFromPage(pageNumber: number): void {

    let params = new HttpParams()
      .set('page', pageNumber.toString());

    this.http.get<ServerPage>(this.localApi + '/', {params}).subscribe(result => {
      console.log(result);
      this.serverPage = result;
      this.serverPage.number = this.serverPage.number + 1;// In Spring pages start from 0.
    }, error => {
      this.alertBarService.setErrorMessage('Can\'t get list of servers. Try again later.');
    });
  }

  getIndexByObjectIdOfObject(object: EAVObject): number {
    let changedServer = this.serverPage.content.find(changedElement => changedElement.objectId === object.objectId);
    return this.serverPage.content.indexOf(changedServer);
  }

  openInsertDialog() {

    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.width = '500px';
    dialogConfig.panelClass = 'custom-dialog-container';

    const dialogRef = this.dialog.open(ServerInputFormModalComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(data => {
      if (data !== undefined) {
        this.alertBarService.setConfirmMessage('Server ' + data['name'] + ' added successfully');
        this.getServersFromPage(1);
      }
    });
  }

  openUpdateDialog(server: Server) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.width = '500px';
    dialogConfig.panelClass = 'custom-dialog-container';
    dialogConfig.data = server;

    const dialogRef = this.dialog.open(ServerUpdateFormModalComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(data => {
      if (data !== undefined) {
        this.alertBarService.setConfirmMessage('Server ' + data['name'] + ' updated successfully');
        let index = this.getIndexByObjectIdOfObject(data);
        this.serverPage.content.splice(index, 1, data);
      }
    });
  }

  switchServer(server: Server) {
    let switchedServer = Object.assign({}, server);
    switchedServer.enabled = !server.enabled;
    switchedServer.connectable = switchedServer.enabled;
    this.http.put(this.localApi + '/update', switchedServer).subscribe(result => {
      this.alertBarService.setConfirmMessage('Server ' + server.name + (switchedServer.enabled ? ' enabled' : ' disabled'));
      server.enabled = switchedServer.enabled;
      server.connectable = switchedServer.connectable;
    }, error => {
      this.alertBarService.setErrorMessage('Cant' + (switchedServer.enabled ? ' enable ' : ' disable ') + server.name + '. Try again later');
    });
  }
}
