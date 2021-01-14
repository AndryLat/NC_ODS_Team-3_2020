import {BigInteger} from "@angular/compiler/src/i18n/big_integer";

export class Directory {
  objectId: BigInteger;
  path: string;
  enabled: boolean;
  lastExistenceCheck: Date;
}
