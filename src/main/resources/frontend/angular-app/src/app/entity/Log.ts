import {LogLevel} from './list/LogLevel';

export class Log {
  constructor(omegaLog: string, CONFIG: LogLevel, date: Date) {
    this.text = omegaLog;
    this.level = CONFIG;
    this.creationDate = date;
  }

  objectId: bigint;
  text: string;
  level: LogLevel;
  creationDate: Date;
}
