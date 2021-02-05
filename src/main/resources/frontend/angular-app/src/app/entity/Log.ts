import {LogLevel} from './list/LogLevel';
import {EAVObject} from "./EAVObject";

export class Log extends EAVObject{
  objectId: bigint;
  text: string;
  level: LogLevel;
  creationDate: Date;
  checked?: boolean;
}
