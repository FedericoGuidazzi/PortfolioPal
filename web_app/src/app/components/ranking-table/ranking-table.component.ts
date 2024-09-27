import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';

export interface RankElement {
  name: string;
  score: number | string;
  id: number;
  pos: number;
  disabled?: boolean;
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
  @Input() hoverDisabled = false;
  currentUser: RankElement | undefined;

  rankingDataSource = new MatTableDataSource<RankElement>([]);

  @Output() userSelection = new EventEmitter<RankElement>();

  onElementSelection(rank: RankElement) {
    this.userSelection.emit(rank);
  }

  constructor(private portfolioService: PortfolioService) {}

  ngAfterViewInit() {
    this.portfolioService.getRanking().subscribe((ranking) => {
      const elements: RankElement[] = [];
      for (let i = 0; i < ranking.length; i++) {
        elements.push({
          pos: i + 1,
          name: ranking[i].portfolioName,
          score: ranking[i].percentageValue,
          id: ranking[i].idPortfolio,
        });
      }

      if (elements.length < 5) {
        for (let i = elements.length; i < 5; i++) {
          elements.push({
            pos: i + 1,
            name: '',
            score: '',
            id: -1,
            disabled: true,
          });
        }
      }

      this.rankingDataSource.data = elements;

      if (!this.hoverDisabled) {
        this.portfolioService.getPortfolioByUserId().subscribe({
          next: (portfolio) => {
            this.currentUser = elements.find((el) => el.id === portfolio.id);
          },
          error: (err) => {
            // console.error(err);
          },
        });
      }
    });
  }
}
