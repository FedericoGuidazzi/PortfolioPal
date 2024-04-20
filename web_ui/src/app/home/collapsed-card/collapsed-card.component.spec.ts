import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollapsedCardComponent } from './collapsed-card.component';

describe('CollapsedCardComponent', () => {
  let component: CollapsedCardComponent;
  let fixture: ComponentFixture<CollapsedCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CollapsedCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CollapsedCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
