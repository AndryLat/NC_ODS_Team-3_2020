import {Log} from '../entity/Log';

export class LogPage {
  content: Log[];
  'last': boolean;
  'totalPages': number;
  'totalElements': number;
  'number': number;
  'size': number;
}
