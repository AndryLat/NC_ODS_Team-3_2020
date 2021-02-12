import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../../../entity/User';
import {UserPage} from '../../../pageable/UserPage';
import {GlobalConstants} from '../../../constants/global-constants';
import {Router} from "@angular/router";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {AlertBarService} from "../../../services/AlertBarService";
import {faArrowUp, faPlus, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {UserInputFormModalComponent} from "./user-input-form-modal/user-input-form-modal.component";

@Component({
  selector: 'app-users-component',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit, OnDestroy {
  plusIcon = faPlus;
  deleteIcon = faTrashAlt;
  promoteIcon = faArrowUp;
  localApi: string = GlobalConstants.apiUrl + 'api/user';

  userPage: UserPage;

  roles = [
    'USER', 'ADMIN'
  ];

  constructor(private router: Router,
              private http: HttpClient,
              private dialog: MatDialog,
              private alertBarService: AlertBarService) {
  }

  ngOnInit(): void {
    this.getUsersFromPage(1);
  }

  ngOnDestroy() {
    this.alertBarService.resetMessage();
  }

  deleteUser(user: User): void {
    this.http.delete(GlobalConstants.apiUrl + 'api/user/delete/' + user.objectId).subscribe(() => {
      this.alertBarService.setConfirmMessage('User ' + user.login + ' deleted successfully');

      let changedServer = this.userPage.content.find(deletedElement => deletedElement.objectId === user.objectId);
      let index = this.userPage.content.indexOf(changedServer);

      this.userPage.content.splice(index, 1);
    }, error => {
      this.alertBarService.setErrorMessage('Something gone wrong. Try again later.');
    });
  }

  getUsersFromPage(pageNumber: number): void {

    let params = new HttpParams()
      .set('page', pageNumber.toString());

    this.http.get<UserPage>(this.localApi + '/', {params}).subscribe(result => {
      console.log(result);
      this.userPage = result;
      this.userPage.number = this.userPage.number + 1;// In Spring pages start from 0.
    }, error => {
      this.alertBarService.setErrorMessage('Can\'t get list of users. Try again later.');
    });
  }

  openInsertDialog() {

    const dialogConfig = new MatDialogConfig();

    dialogConfig.autoFocus = true;
    dialogConfig.width = '500px';
    dialogConfig.panelClass = 'custom-dialog-container';

    const dialogRef = this.dialog.open(UserInputFormModalComponent, dialogConfig);
    dialogRef.afterClosed().subscribe(data => {
      if (data !== undefined) {
        this.alertBarService.setConfirmMessage('User ' + data['login'] + ' added successfully');
        this.getUsersFromPage(1);
      }
    });
  }

  promoteUser(user: User) {
    let promotingUser = new User();
    promotingUser.objectId = user.objectId
    promotingUser.name = user.name;
    promotingUser.objectTypeId = user.objectTypeId;
    promotingUser.role = 'ADMIN';
    this.http.put(this.localApi + '/promoteToAdmin', promotingUser).subscribe(result => {
        user.role = 'ADMIN';
        this.alertBarService.setConfirmMessage('User ' + user.login + ' promoted to admin');
      },
      error => {
        this.alertBarService.setErrorMessage('Error occurs while promoting ' + user.login + '. Try again later.');
      });
  }
}
