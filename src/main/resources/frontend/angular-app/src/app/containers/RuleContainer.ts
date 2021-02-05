import {LogLevel} from '../entity/list/LogLevel';

export class RuleContainer {

  text: string;
  dat1: Date;
  dat2: Date;
  levels: LogLevel[];
  sort: number;

  constructor(text: string, dat1: Date, dat2: Date, vSort: number) {
    this.text = text;
    this.dat1 = dat1;
    this.dat2 = dat2;
    this.levels = [];
    this.sort = vSort;
  }
}
