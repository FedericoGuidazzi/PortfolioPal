import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardPortfolioValutationComponent } from './card-portfolio-valutation.component';

describe('CardPortfolioValutationComponent', () => {
  let component: CardPortfolioValutationComponent;
  let fixture: ComponentFixture<CardPortfolioValutationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardPortfolioValutationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CardPortfolioValutationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
