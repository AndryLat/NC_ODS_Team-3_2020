import {EAVObject} from './EAVObject';

export class Directory extends EAVObject {
  path: string;
  enabled: boolean;
  lastExistenceCheck: Date;
  connectable: boolean;
}
