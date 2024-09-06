import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateTransactionDialog } from './update-transaction-dialog.component';

describe('UpdateTransactionDialog', () => {
  let component: UpdateTransactionDialog;
  let fixture: ComponentFixture<UpdateTransactionDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateTransactionDialog]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UpdateTransactionDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
