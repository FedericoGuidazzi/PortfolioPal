import { Component, Input, ViewChild } from '@angular/core';
import { FaqCardComponent } from '../../components/faq-card/faq-card.component';
import {
  NgbCarousel,
  NgbCarouselModule,
  NgbSlideEvent,
} from '@ng-bootstrap/ng-bootstrap';
import { CarouselCardType1Component } from '../../components/carousel-card-type-1/carousel-card-type-1.component';
import { CarouselCardType2Component } from '../../components/carousel-card-type-2/carousel-card-type-2.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    NgbCarouselModule,
    FaqCardComponent,
    CarouselCardType1Component,
    CarouselCardType2Component,
  ],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css',
})
export class LandingPageComponent {
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
