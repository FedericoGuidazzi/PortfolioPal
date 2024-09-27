import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadTransactionComponent } from './upload-transaction.component';

describe('UploadTransactionComponent', () => {
  let component: UploadTransactionComponent;
  let fixture: ComponentFixture<UploadTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadTransactionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UploadTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
