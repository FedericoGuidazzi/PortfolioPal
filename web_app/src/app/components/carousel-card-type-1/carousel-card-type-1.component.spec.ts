import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarouselCardType1Component } from './carousel-card-type-1.component';

describe('CarouselCardType1Component', () => {
  let component: CarouselCardType1Component;
  let fixture: ComponentFixture<CarouselCardType1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarouselCardType1Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CarouselCardType1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
