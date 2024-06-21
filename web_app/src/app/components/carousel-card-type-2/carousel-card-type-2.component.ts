import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  RankingTableComponent,
  User,
} from '../ranking-table/ranking-table.component';

@Component({
  selector: 'app-carousel-card-type-2',
  standalone: true,
  imports: [RouterLink, RankingTableComponent],
  templateUrl: './carousel-card-type-2.component.html',
  styleUrl: './carousel-card-type-2.component.css',
})
export class CarouselCardType2Component {
  ranking: User[] = [
    { pos: 1, name: 'Beach ball', score: 4 },
    { pos: 2, name: 'Towel', score: 5 },
    { pos: 3, name: 'Frisbee', score: 2 },
    { pos: 4, name: 'Sunscreen', score: 4 },
  ];
}
