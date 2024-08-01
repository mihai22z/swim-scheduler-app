import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RescheduleLessonDialogComponent } from './reschedule-lesson-dialog.component';

describe('RescheduleLessonDialogComponent', () => {
  let component: RescheduleLessonDialogComponent;
  let fixture: ComponentFixture<RescheduleLessonDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RescheduleLessonDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RescheduleLessonDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
