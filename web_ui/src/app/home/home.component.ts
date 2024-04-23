import { Component } from '@angular/core';
import { CarouselComponent } from './carousel/carousel.component';
import { CollapsedCardComponent } from './collapsed-card/collapsed-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CarouselComponent, CollapsedCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  loggedIn: boolean = false;
  numberOfFaqs: number = 3;

  counter(i: number) {
    return new Array(i);
  }
}
