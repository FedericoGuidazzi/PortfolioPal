import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatTableModule } from '@angular/material/table';

export interface User {
  pos: number;
  name: string;
  score: number;
}

@Component({
  selector: 'app-ranking-table',
  standalone: true,
  imports: [MatTableModule, CommonModule],
  templateUrl: './ranking-table.component.html',
  styleUrl: './ranking-table.component.css',
})
export class RankingTableComponent {
  displayedColumns: string[] = ['pos', 'name', 'score'];
  @Input() ranking: User[] = [];
  @Input() current_user: User | undefined;
  @Input() hover_disabled = false;
}
