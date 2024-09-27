import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneratePortfolioDialogComponent } from './generate-portfolio-dialog.component';

describe('GeneratePortfolioDialogComponent', () => {
  let component: GeneratePortfolioDialogComponent;
  let fixture: ComponentFixture<GeneratePortfolioDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneratePortfolioDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GeneratePortfolioDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
