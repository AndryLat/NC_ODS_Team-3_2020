import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {User} from '../../../entity/User';
import {UserPage} from '../../../pageable/UserPage';
import {GlobalConstants} from '../../../constants/global-constants';
import {Router} from '@angular/router';
import {AuthService} from "../../../services/AuthService";

@Component({
  selector: 'app-users-component',
  templateUrl: './users.component.html'
})
export class UsersComponent {
  form: FormGroup;

  users: User[];

  roles = [
    'USER', 'ADMIN'
  ];

  constructor(private authService: AuthService, private http: HttpClient, private fb: FormBuilder, private router: Router){
    http.get<UserPage>(GlobalConstants.apiUrl + 'api/user/').subscribe(result => {
      this.users = result.content;
    });

    this.form = this.fb.group({
      email: ['', Validators.required],
      login: ['', Validators.required],
      password: ['', Validators.required],
      role: ['', Validators.required]
    });
  }
  deleteUser(id: string): void{
    this.http.delete(GlobalConstants.apiUrl + 'api/user/delete/' + id).subscribe(() => {
      this.users = this.users.filter(item => item.objectId !== id);
    });
  }

  addUser(): void {
    const user = this.form.value;
    console.log(this.form.value);
    this.http.post(GlobalConstants.apiUrl + 'api/user/create', user).subscribe(() => {
      console.log('Complete');
    });
  }
}
