import {Server} from "../entity/Server";

export class ServerPage {
  content: Server[];
  'last': boolean;
  'totalPages': number;
  'totalElements': number;
  'number': number;
  'size': number;
}
