import { Component, Input, ViewChild } from '@angular/core';
import {
  NgbCarousel,
  NgbCarouselModule,
  NgbSlideEvent,
  NgbSlideEventSource,
} from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { CardType1Component } from './card-type-1/card-type-1.component';
import { CardType2Component } from './card-type-2/card-type-2.component';

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [
    NgbCarouselModule,
    FormsModule,
    CardType1Component,
    CardType2Component,
  ],
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.css',
})
export class CarouselComponent {
  paused = false;
  pauseOnIndicator = false;

  @Input() interval: number = 5000;
  @Input() pauseOnHover: boolean = true;
  @Input() pauseOnFocus: boolean = true;

  @ViewChild('carousel', { static: true })
  carousel!: NgbCarousel;

  togglePaused() {
    if (this.paused) {
      this.carousel.cycle();
    } else {
      this.carousel.pause();
    }
    this.paused = !this.paused;
  }

  onSlide(slideEvent: NgbSlideEvent) {
    if (slideEvent.paused) {
      this.togglePaused();
    }
  }
}
