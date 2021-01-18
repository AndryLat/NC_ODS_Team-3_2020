import {LogLevel} from './list/LogLevel';

export class Log {
  objectId: bigint;
  text: string;
  level: LogLevel;
  creationDate: Date;
  checked?: boolean;
}
