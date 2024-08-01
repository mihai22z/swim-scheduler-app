import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditClientsDialogComponent } from './edit-clients-dialog.component';

describe('EditClientsDialogComponent', () => {
  let component: EditClientsDialogComponent;
  let fixture: ComponentFixture<EditClientsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditClientsDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditClientsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
