import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkdayDetailComponent } from './workday-detail.component';

describe('WorkdayDetailComponent', () => {
  let component: WorkdayDetailComponent;
  let fixture: ComponentFixture<WorkdayDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkdayDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(WorkdayDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
