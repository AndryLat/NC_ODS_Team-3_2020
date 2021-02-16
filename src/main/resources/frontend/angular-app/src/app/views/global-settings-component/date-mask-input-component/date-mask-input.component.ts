import {Component, ElementRef, forwardRef, Input, OnInit, ViewChild, ViewEncapsulation} from "@angular/core";
import {ControlValueAccessor, FormControl, FormGroup, NG_VALUE_ACCESSOR} from "@angular/forms";

@Component({
  selector: 'md-input',
  templateUrl: './date-mask-input.component.html',
  encapsulation: ViewEncapsulation.None,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => MaskedInputComponent),
      multi: true,
    }
  ]
})

export class MaskedInputComponent implements ControlValueAccessor, OnInit {

  @ViewChild('mdInputEl', {static: true}) public mdInputEl: ElementRef;

  @Input() mask: any[];

  secondDescription: string = 'hh - hours(00-23)         \n' +
                              'dd - days(00-28)          \n' +
                              'mm - months(00-11)        \n' +
                              'yyyy - years(0000-2999)';

  maskAdditional: any[] = [/[0-2]/,/[0-9]/,'/',/[0-3]/,/[0-9]/,'/',/[0-1]/,/[0-9]/,'/',/[0-2]/,/[0-9]/,/[0-9]/,/[0-9]/];

  @Input() title: string;

  @Input() description: string;

  @Input() icon;

  @Input() parentFormGroup: FormGroup;

  @Input() controlName: string;

  public mdInput = new FormControl();

  private _previousValue: string = '';

  private _previousPlaceholder: string = '';

  private _maxInputValue: number;

  private _maxInputValueAdd: number;

  private _currentCursorPosition: number;

  private readonly _placeholderChar: string = '0';

  public ngOnInit(): void {
    this._maxInputValue = this.mask.length;
    this._maxInputValueAdd = this.maskAdditional.length;
    this.mdInput.valueChanges
      .subscribe((value: string) => {
          if (!value || value === this._previousValue) {
            return;
          }
          this._currentCursorPosition = this.mdInputEl.nativeElement.selectionEnd;

          const placeholder = this._convertMaskToPlaceholder();

          const placeholderAdditional = this._convertMaskAdditionalToPlaceholder();

          const values = this._conformValue(value, placeholder, placeholderAdditional);
          const adjustedCursorPosition = this._getCursorPosition(value, placeholderAdditional, values.conformed);
          this.mdInputEl.nativeElement.value = values.conformed;
          this.mdInputEl.nativeElement.setSelectionRange(
            adjustedCursorPosition,
            adjustedCursorPosition,
            'none');

          this._onChange(values.cleaned);

          this._previousValue = values.conformed;
          this._previousPlaceholder = placeholder;
        },
        (err) => console.warn(err)
      );
  }

  public writeValue(value: string): void {
    if(value != null){
      this._currentCursorPosition = this.mdInputEl.nativeElement.selectionEnd;
      const placeholder = this._convertMaskToPlaceholder();
      const placeholderAdditional = this._convertMaskAdditionalToPlaceholder();
      let values = this._conformValue(value, placeholder, placeholderAdditional);
      this.mdInputEl.nativeElement.value = values.conformed;
      this.mdInput.setValue(values.conformed);
    }
  }

  public registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  private _onChange: Function = (_: any) => {
  }

  private _onTouched: Function = (_: any) => {
  }

  private _convertMaskToPlaceholder(): string {
    return this.mask.map((char) => {
      return (char instanceof RegExp) ? this._placeholderChar : char;
    }).join('');
  }

  private _convertMaskAdditionalToPlaceholder(): string {
    return this.maskAdditional.map((char) => {
      return (char instanceof RegExp) ? this._placeholderChar : char;
    }).join('');
  }

  private _conformValue(value: string, placeholder: string, placeholderAdditional: string): { conformed: string, cleaned: string } {
    const editDistance = value.length - this._previousValue.length;
    console.log('editDistance : '+editDistance)
    console.log('placeholderAdditional : '+placeholderAdditional)
    const isAddition = editDistance > 0;
    const indexOfFirstChange = this._currentCursorPosition + (isAddition ? -editDistance : 0);
    console.log('indexOfFirstChange : '+ indexOfFirstChange)
    const indexOfLastChange = indexOfFirstChange + Math.abs(editDistance);
    console.log('indexOfLastChange : '+ indexOfLastChange)

    if (!isAddition) {
      let compensatingPlaceholderChars = '';

      for (let i = indexOfFirstChange; i < indexOfLastChange; i++) {
        compensatingPlaceholderChars += placeholderAdditional[i];
      }

      value =
        (value.slice(0, indexOfFirstChange) +
          compensatingPlaceholderChars +
          value.slice(indexOfFirstChange, value.length)
        );
    }

    let prevValue = value;

    if(isAddition){
      prevValue = value.slice(0, indexOfFirstChange)+value.slice(indexOfLastChange, value.length);
    }

    if(isNaN(Number(((prevValue.slice(0, 1))))) || (prevValue.slice(0, 1) == ' ')){
      prevValue = '0' + prevValue.slice(1, 13);
      value = prevValue;
    }

    console.log('prevValue: '+prevValue);
    let flag : boolean = false;
    if (isAddition && value.length > 13 ) {
      const prevVal = value.slice(0, indexOfFirstChange)+value.slice(indexOfLastChange, value.length);
      const addVal = value.slice(indexOfFirstChange, indexOfLastChange);
      const delVal = prevVal.slice(indexOfFirstChange, indexOfLastChange);
      const result = prevVal.slice(0, indexOfFirstChange)+addVal+prevVal.slice(indexOfLastChange, value.length);
      flag = true;
      value = result;
    }

    console.log('Value : '+ value)
    const valueArray = value.split('');
    const valueArr = (valueArray.length== 10) ? [ valueArray[0] + valueArray[1],
      valueArray[2] + valueArray[3],
      valueArray[4] + valueArray[5],
      valueArray[6] + valueArray[7] + valueArray[8] + valueArray[9]]
      :
      [ valueArray[0] + valueArray[1], valueArray[2],
        valueArray[3] + valueArray[4], valueArray[5],
        valueArray[6] + valueArray[7], valueArray[8],
        valueArray[9] + valueArray[10] + valueArray[11] + valueArray[12]];

    const valueArrayPrev = prevValue.split('');
    const valueArrPrev = (valueArrayPrev.length==10) ? [valueArrayPrev[0] + valueArrayPrev[1],
    valueArrayPrev[2] + valueArrayPrev[3],
    valueArrayPrev[4] + valueArrayPrev[5],
    valueArrayPrev[6] + valueArrayPrev[7] + valueArrayPrev[8] + valueArrayPrev[9]]
      :
      [ valueArrayPrev[0] + valueArrayPrev[1], valueArrayPrev[2],
        valueArrayPrev[3] + valueArrayPrev[4], valueArrayPrev[5],
        valueArrayPrev[6] + valueArrayPrev[7], valueArrayPrev[8],
        valueArrayPrev[9] + valueArrayPrev[10] + valueArrayPrev[11] + valueArrayPrev[12]];

    for (let i = value.length - 1; i >= 0; i--) {
      let char = value[i];
      if (char !== this._placeholderChar) {
        const shouldOffset = i >= indexOfFirstChange &&
          this._previousValue.length === this._maxInputValue;
        if (char === placeholder[(shouldOffset) ? i - editDistance : i]) {
          valueArr.splice(i, 1);
          valueArrPrev.splice(i, 1);
        }
      }
    }

    let conformedValue = '';
    let cleanedValue = '';

    placeholderLoop: for (let i = 0; i < placeholder.length; i++) {
      const charInPlaceholder = placeholder[i];
      if (charInPlaceholder === this._placeholderChar) {
        if (valueArr.length > 0) {
          while (valueArr.length > 0) {
            let valueChar = valueArr.shift();
            let prevChar = valueArrPrev.shift();

            if (valueChar === this._placeholderChar) {
              conformedValue += this._placeholderChar;
              continue placeholderLoop;
            } else if (this.mask[i].test(valueChar)) {
              conformedValue += valueChar;
              cleanedValue += valueChar;

              continue placeholderLoop;
            }
            else {
              if (flag && this.mask[i].test(prevChar)) {
                conformedValue += prevChar;
                cleanedValue += prevChar;

                continue placeholderLoop;
              }
            }
          }
        }

        conformedValue += placeholder.substr(i, placeholder.length);
        break;
      } else {
        conformedValue += charInPlaceholder;
      }
    }

    return {conformed: conformedValue, cleaned: cleanedValue};
  }

  private _getCursorPosition(value: string, placeholder: string, conformedValue: string): number {
    if (this._currentCursorPosition === 0) {
      return 0;
    }

    const editLength = value.length - this._previousValue.length;
    const isAddition = editLength > 0;
    const isFirstValue = this._previousValue.length === 0;
    const isPartialMultiCharEdit = editLength > 1 && !isAddition && !isFirstValue;

    if (isPartialMultiCharEdit) {
      return this._currentCursorPosition;
    }

    const possiblyHasRejectedChar = isAddition && (
      this._previousValue === conformedValue ||
      conformedValue === placeholder);

    let startingSearchIndex = 0;
    let trackRightCharacter;
    let targetChar;

    if (possiblyHasRejectedChar) {
      startingSearchIndex = this._currentCursorPosition - editLength;
    } else {
      const normalizedConformedValue = conformedValue.toLowerCase();
      const normalizedValue = value.toLowerCase();
      const leftHalfChars = normalizedValue.substr(0, this._currentCursorPosition).split('');
      const intersection = leftHalfChars.filter((char) => normalizedConformedValue.indexOf(char) !== -1);
      targetChar = intersection[intersection.length - 1];

      const leftMaskChars = placeholder
        .substr(0, intersection.length)
        .split('')
        .filter((char) => char !== this._placeholderChar)
        .length;

      const targetIsMaskMovingLeft = (
        placeholder[intersection.length - 1] !== undefined &&
        placeholder[intersection.length - 2] !== undefined &&
        placeholder[intersection.length - 1] !== this._placeholderChar &&
        placeholder[intersection.length - 1] !== placeholder[intersection.length - 1] &&
        placeholder[intersection.length - 1] === placeholder[intersection.length - 2]
      );

      if (!isAddition &&
        targetIsMaskMovingLeft &&
        leftMaskChars > 0 &&
        placeholder.indexOf(targetChar) > -1 &&
        value[this._currentCursorPosition] !== undefined) {
        trackRightCharacter = true;
        targetChar = value[this._currentCursorPosition];
      }

      const countTargetCharInIntersection = intersection.filter((char) => char === targetChar).length;

      const countTargetCharInPlaceholder = placeholder
        .substr(0, placeholder.indexOf(this._placeholderChar))
        .split('')
        .filter((char, index) => (
          char === targetChar &&
          value[index] !== char
        )).length;

      const requiredNumberOfMatches =
        (countTargetCharInPlaceholder + countTargetCharInIntersection + (trackRightCharacter ? 1 : 0));

      let numberOfEncounteredMatches = 0;
      for (let i = 0; i < conformedValue.length; i++) {
        const conformedValueChar = normalizedConformedValue[i];

        startingSearchIndex = i + 1;

        if (conformedValueChar === targetChar) {
          numberOfEncounteredMatches++;
        }

        if (numberOfEncounteredMatches >= requiredNumberOfMatches) {
          break;
        }
      }
    }

    if (isAddition) {
      let lastPlaceholderChar = startingSearchIndex;
      for (let i = startingSearchIndex; i <= placeholder.length; i++) {
        if (placeholder[i] === this._placeholderChar) {
          lastPlaceholderChar = i;
        }

        if (placeholder[i] === this._placeholderChar || i === placeholder.length) {
          return lastPlaceholderChar;
        }
      }
    } else {
      if (trackRightCharacter) {
        for (let i = startingSearchIndex - 1; i >= 0; i--) {
          if (
            conformedValue[i] === targetChar ||
            i === 0
          ) {
            return i;
          }
        }
      } else {
        for (let i = startingSearchIndex; i >= 0; i--) {
          if (placeholder[i - 1] === this._placeholderChar || i === 0) {
            return i;
          }
        }
      }
    }
  }
}
