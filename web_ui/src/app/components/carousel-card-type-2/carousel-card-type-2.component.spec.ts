import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarouselCardType2Component } from './carousel-card-type-2.component';

describe('CardType2Component', () => {
  let component: CarouselCardType2Component;
  let fixture: ComponentFixture<CarouselCardType2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarouselCardType2Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CarouselCardType2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
