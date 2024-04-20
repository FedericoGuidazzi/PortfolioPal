import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardType1Component } from './card-type-1.component';

describe('CardType1Component', () => {
  let component: CardType1Component;
  let fixture: ComponentFixture<CardType1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardType1Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CardType1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
