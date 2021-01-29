import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogfileComponentComponent } from './logfile-component.component';

describe('LogfileComponentComponent', () => {
  let component: LogfileComponentComponent;
  let fixture: ComponentFixture<LogfileComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LogfileComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LogfileComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
