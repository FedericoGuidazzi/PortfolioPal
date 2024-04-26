import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardType2Component } from './card-type-2.component';

describe('CardType2Component', () => {
  let component: CardType2Component;
  let fixture: ComponentFixture<CardType2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardType2Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CardType2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
