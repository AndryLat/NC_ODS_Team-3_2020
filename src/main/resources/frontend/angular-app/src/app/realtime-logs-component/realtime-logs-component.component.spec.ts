import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RealtimeLogsComponentComponent } from './realtime-logs-component.component';

describe('RealtimeLogsComponentComponent', () => {
  let component: RealtimeLogsComponentComponent;
  let fixture: ComponentFixture<RealtimeLogsComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RealtimeLogsComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RealtimeLogsComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
