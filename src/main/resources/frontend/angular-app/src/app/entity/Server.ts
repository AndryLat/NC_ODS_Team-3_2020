import {BigInteger} from "@angular/compiler/src/i18n/big_integer";

export class Server {
  constructor(omega: string) {
    this.name = omega;
    this.objectId = 12;
  }

  objectId: number;
  name: string;
  parentId: number;
  ip: string;
  port: number;
  protocol: string;
  enabled: boolean;
  canConnect: boolean;
  login: string;
  password: string;
  lastAccessByUser: Date;
  lastAccessByJob: Date;
}
