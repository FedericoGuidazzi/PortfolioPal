import { Component } from '@angular/core';
import {
  RankingTableComponent,
  User,
} from '../../components/ranking-table/ranking-table.component';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [RankingTableComponent],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css',
})
export class RankingComponent {
  ranking: User[] = [
    { pos: 1, name: 'Beach ball', score: 4 },
    { pos: 2, name: 'Towel', score: 5 },
    { pos: 3, name: 'Frisbee', score: 2 },
    { pos: 4, name: 'Sunscreen', score: 4 },
    { pos: 5, name: 'Cooler', score: 25 },
  ];
  current_user: User = { pos: 2, name: 'Towel', score: 5 };
}
