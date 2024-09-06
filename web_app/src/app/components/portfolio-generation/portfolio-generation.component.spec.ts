import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioGenerationComponent } from './portfolio-generation.component';

describe('PortfolioGenerationComponent', () => {
  let component: PortfolioGenerationComponent;
  let fixture: ComponentFixture<PortfolioGenerationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PortfolioGenerationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PortfolioGenerationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
