import {Directory} from "../entity/Directory";

export class DirectoryPage {
  content: Directory[];
  'last': boolean;
  'totalPages': number;
  'totalElements': number;
  'number': number;
  'size': number;
}
