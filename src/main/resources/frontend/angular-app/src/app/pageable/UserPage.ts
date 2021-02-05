import {User} from '../entity/User';

export class UserPage {
  content: User[];
  'last': boolean;
  'totalPages': number;
  'totalElements': number;
  'number': number;
  'size': number;
}
