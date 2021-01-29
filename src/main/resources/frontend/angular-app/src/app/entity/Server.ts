import {EAVObject} from "./EAVObject";

export class Server extends EAVObject{
  ip: string;
  port: number;
  protocol: string;
  enabled: boolean;
  connectable: boolean;
  login: string;
  password: string;
  lastAccessByUser: Date;
  lastAccessByJob: Date;
}
