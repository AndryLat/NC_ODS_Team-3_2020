export class RuleContainer {


  constructor(text: string, dat1: Date, dat2: Date, vSevere: number, vWarning: number, vInfo: number, vConfig: number, vFine: number, vFiner: number, vFinest: number, vDebug: number, vTrace: number, vError: number, vFatal: number, vSort: number) {
    this.text = text;
    this.dat1 = dat1;
    this.dat2 = dat2;
    this.severe = vSevere;
    this.warning = vWarning;
    this.info = vInfo;
    this.config = vConfig;
    this.fine = vFine;
    this.finer = vFiner;
    this.finest = vFinest;
    this.debug = vDebug;
    this.trace = vTrace;
    this.error = vError;
    this.fatal = vFatal;
    this.sort = vSort;
  }

  text:string;
  dat1:Date;
  dat2:Date;
  severe:number;
  warning:number;
  info:number;
  config:number;
  fine:number;
  finer:number;
  finest:number;
  debug:number;
  trace:number;
  error:number;
  fatal:number;
  sort:number;
}
