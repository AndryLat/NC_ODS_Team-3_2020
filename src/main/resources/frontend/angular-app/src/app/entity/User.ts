import {EAVObject} from "./EAVObject";

export class User extends EAVObject{
  email: string;
  login: string;
  password: string;
  role: string;
}
