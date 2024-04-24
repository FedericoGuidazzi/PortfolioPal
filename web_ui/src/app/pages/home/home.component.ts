import { Component, Input, ViewChild } from '@angular/core';
import { CollapsedCardComponent } from '../../components/collapsed-card/collapsed-card.component';
import {
  NgbCarousel,
  NgbCarouselModule,
  NgbSlideEvent,
} from '@ng-bootstrap/ng-bootstrap';
import { CardType1Component } from '../../components/card-type-1/card-type-1.component';
import { CardType2Component } from '../../components/card-type-2/card-type-2.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    NgbCarouselModule,
    CollapsedCardComponent,
    CardType1Component,
    CardType2Component,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  loggedIn: boolean = false;
  numberOfFaqs: number = 3;

  counter(i: number) {
    return new Array(i);
  }

  paused = false;
  pauseOnIndicator = false;

  protected interval: number = 10000;
  protected pauseOnHover: boolean = true;
  protected pauseOnFocus: boolean = true;

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
