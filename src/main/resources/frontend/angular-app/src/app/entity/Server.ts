export class Server {
  objectId: bigint;
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
