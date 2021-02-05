import {LogFile} from '../entity/LogFile';

export class LogFilePage {
  content: LogFile[];
  'last': boolean;
  'totalPages': number;
  'totalElements': number;
  'number': number;
  'size': number;
}
