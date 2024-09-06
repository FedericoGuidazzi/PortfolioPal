import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatTableModule } from '@angular/material/table';

export interface User {
  pos: number;
  name: string;
  score: number;
  id: string;
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
  @Input() currentUser: User | undefined;
  @Input() hoverDisabled = false;

  @Output() userSelection = new EventEmitter<User>();

  getSelectedUser(user: User) {
    this.userSelection.emit(user);
  }
}
