import {EAVObject} from './EAVObject';

export class LogFile extends EAVObject {
  fileName: string;
  lastUpdate: Date;
  lastRow: string;
  checked: boolean;
  fullText: boolean = false;
}
