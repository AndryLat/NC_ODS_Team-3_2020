import {LogLevel} from '../entity/list/LogLevel';

export class RuleContainer {

  text: string;
  dat1: Date;
  dat2: Date;
  levels: LogLevel[];
  sort: number;

  constructor() {
    this.levels = [];
  }
}
