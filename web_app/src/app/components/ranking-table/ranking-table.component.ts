import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import {
  MatTable,
  MatTableDataSource,
  MatTableModule,
} from '@angular/material/table';
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';
import { HistoryService } from '../../utils/api/portfolio/history.service';

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

  @ViewChild(MatTable) table: MatTable<RankElement> | undefined;

  onElementSelection(rank: RankElement) {
    this.userSelection.emit(rank);
  }

  constructor(private portfolioService: PortfolioService) {}

  ngAfterViewInit() {
    this.portfolioService.getRanking().subscribe((ranking) => {
      const elements: RankElement[] = [];
      let displayedElements: RankElement[] = [];
      for (let i = 0; i < ranking.length; i++) {
        const score = ranking[i].percentageValue;
        elements.push({
          pos: i + 1,
          name: ranking[i].portfolioName,
          score:
            score <= 0
              ? parseFloat(score.toFixed(5))
              : parseFloat(score.toFixed(2)),
          id: ranking[i].idPortfolio,
        });
      }

      displayedElements = elements.slice(0, 5);

      if (!this.hoverDisabled) {
        this.portfolioService.getPortfolioByUserId().subscribe({
          next: (portfolio) => {
            this.currentUser = elements.find((el) => el.id == portfolio.id);

            if (displayedElements.length < 5) {
              for (let i = displayedElements.length; i < 5; i++) {
                displayedElements.push({
                  pos: i + 1,
                  name: '',
                  score: '',
                  id: -1,
                  disabled: true,
                });
              }
            }
            if (this.currentUser && this.currentUser.pos > 5) {
              displayedElements.push(this.currentUser);
            }

            this.rankingDataSource = new MatTableDataSource<RankElement>(
              displayedElements
            );
          },
          error: (err) => {
            // console.error(err);
          },
        });
      }
    });
  }
}
