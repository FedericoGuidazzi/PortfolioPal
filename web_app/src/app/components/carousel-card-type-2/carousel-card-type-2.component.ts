import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  RankingTableComponent,
  RankElement,
} from '../ranking-table/ranking-table.component';
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';

@Component({
  selector: 'app-carousel-card-type-2',
  standalone: true,
  imports: [RouterLink, RankingTableComponent],
  templateUrl: './carousel-card-type-2.component.html',
  styleUrl: './carousel-card-type-2.component.css',
})
export class CarouselCardType2Component {}
