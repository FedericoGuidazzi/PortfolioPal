import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  RankingTableComponent,
  User,
} from '../ranking-table/ranking-table.component';

@Component({
  selector: 'app-card-type-2',
  standalone: true,
  imports: [RouterLink, RankingTableComponent],
  templateUrl: './card-type-2.component.html',
  styleUrl: './card-type-2.component.css',
})
export class CardType2Component {
  ranking: User[] = [
    { pos: 1, name: 'Beach ball', score: 4 },
    { pos: 2, name: 'Towel', score: 5 },
    { pos: 3, name: 'Frisbee', score: 2 },
    { pos: 4, name: 'Sunscreen', score: 4 },
    { pos: 5, name: 'Cooler', score: 25 },
  ];
}
