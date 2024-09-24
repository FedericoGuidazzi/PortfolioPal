import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import {
  ActivatedRoute,
  NavigationEnd,
  Router,
  RouterLink,
} from '@angular/router';
import { Chart } from 'chart.js';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import {
  RankElement,
  RankingTableComponent,
} from '../../components/ranking-table/ranking-table.component';
import { UserService } from '../../utils/api/user/user.service';
import { HistoryItem, PortfolioAssets } from '../dashboard/dashboard.component';
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';
import { TransactionService } from '../../utils/api/transaction/transaction.service';
import { HistoryService } from '../../utils/api/portfolio/history.service';
import { Location } from '@angular/common';

interface AssetQty {
  symbolId: string;
  amount: number;
}

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [
    RankingTableComponent,
    CardPortfolioValutationComponent,
    MatFormFieldModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatInputModule,
    MatFormFieldModule,
    MatInputModule,
    RouterLink,
  ],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css',
})
export class RankingComponent {
  duration: string[] = ['1A', '5A', 'Max'];
  lineChart: any;
  doughnutChart: any;

  portfolioInfo!: PortfolioAmount;
  currentUser!: RankElement;

  assets: PortfolioAssets[] = [];
  assetDisplayedColumns: string[] = [
    'symbol',
    'portfolioPercentage',
    'percentage',
  ];
  assetDataSource = new MatTableDataSource<PortfolioAssets>(this.assets);
  @ViewChild(MatPaginator) assetPaginator!: MatPaginator;

  @ViewChild('portfolioContainer') portfolioContainer!: ElementRef;
  @ViewChild('noPortfolioContainer') noPortfolioContainer!: ElementRef;

  assetRowDisabled: boolean = true;

  noElementSelected = true;
  portfolioId!: any;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private transactionService: TransactionService,
    private historyService: HistoryService,
    private portfolioService: PortfolioService,
    private location: Location
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(
      (params) => (this.portfolioId = params.get('portfolioId'))
    );

    if (this.portfolioId) {
      this.noElementSelected = false;
    }
  }

  ngAfterViewInit() {
    this.assetDataSource.paginator = this.assetPaginator;
    if (this.portfolioId) {
      this.updatePortfolioView(this.portfolioId, '');
    }
  }

  @ViewChild('userDoughnutChart') assetDoughnutContainer!: ElementRef;
  createDoughnutChart(): void {
    const data = {
      labels: this.assetDataSource.data.map((item) => item.symbolId + '%'),
      datasets: [
        {
          data: this.assetDataSource.data.map((item) => item.percPortfolio),
          hoverOffset: 4,
        },
      ],
    };

    const config: any = {
      type: 'doughnut',
      data: data,
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: false,
          },
        },
        maintainAspectRatio: false,
        cutout: '75%',
      },
    };

    this.assetDoughnutContainer.nativeElement.innerHTML =
      '<canvas class="h-100 w-100" id="userDoughnutChart"></canvas>';
    const canvas = document.getElementById(
      'userDoughnutChart'
    ) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }
    if (this.doughnutChart) {
      this.doughnutChart.destroy();
    }
    this.doughnutChart = new Chart(canvas, config);
  }

  @ViewChild('userHistoryGraphContainer') historyGraphContainer!: ElementRef;
  createLineChart(labels: any, countervails: any, investedAmounts: any): void {
    const config: any = {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Valore Portfolio',
            data: countervails,
          },
          {
            label: 'Liquidità Inserita',
            data: investedAmounts,
          },
        ],
      },
      options: {
        scales: {
          x: {
            grid: {
              display: false,
            },
          },
          y: {
            grid: {
              display: false,
            },
          },
        },
        responsive: true,
        plugins: {
          legend: {
            display: false,
          },
          title: {
            display: false,
          },
        },
        maintainAspectRatio: false,
      },
    };

    this.historyGraphContainer.nativeElement.innerHTML =
      '<canvas class="h-100 w-100" id="userHistoryLineChart"></canvas>';
    const canvas = document.getElementById(
      'userHistoryLineChart'
    ) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }

    if (this.lineChart) {
      this.lineChart.destroy();
    }
    this.lineChart = new Chart(canvas, config);
  }

  updatePortfolioView(id: number, name: string) {
    // update selector indicators
    const selectorActive = document.querySelectorAll('.selectors > .active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selectors = document.querySelectorAll('.selectors>div');
    if (selectors.length > 0) {
      selectors[0].classList.add('active');
    }

    const portfolioName = document.getElementById('portfolio_name');
    if (portfolioName) {
      portfolioName.textContent = name == '' ? 'Anonymous' : name;
    }

    if (this.portfolioContainer.nativeElement.classList.contains('d-none')) {
      this.noPortfolioContainer.nativeElement.classList.add('d-none');
      this.portfolioContainer.nativeElement.classList.remove('d-none');
    }

    // Request asset allocation
    this.transactionService.getAssetAllocation(id).subscribe({
      next: (data: AssetQty[]) => {
        const total = data.reduce((acc, item) => acc + item.amount, 0);
        this.assets = data.map((item) => {
          return {
            symbolId: item.symbolId,
            percPortfolio: (item.amount / total) * 100,
            percentage: 0,
          };
        });
        this.assetDataSource.data = this.assets;

        this.createDoughnutChart();
      },
      error: (error) => {
        console.error('Error fetching asset allocation', error);
      },
    });

    // Request past week history
    this.historyService.getPortfolioHistoryById(id, '1S').subscribe({
      next: (data: HistoryItem[]) => {
        this.portfolioInfo = {
          assetName: null,
          currency: 'EUR',
          amount: data[0].countervail,
          percentage: data[0].percentageValue,
        };

        this.createLineChart(
          data.map((item) => item.date),
          data.map((item) => item.countervail),
          data.map((item) => item.investedAmount)
        );
      },
      error: (error: any) => {
        console.error('Error fetching history', error);
      },
    });
  }

  updateHistoryGraphView(value: string) {
    const selectorActive = document.querySelectorAll('.active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selector = document.getElementById(value);
    selector?.classList.add('active');

    this.historyService.getPortfolioHistoryById(1, value).subscribe({
      next: (data: HistoryItem[]) => {
        const labels = data.map((item) => item.date);
        const countervail = data.map((item) => item.countervail);
        const investedAmount = data.map((item) => item.investedAmount);

        this.lineChart.data.labels = labels;
        this.lineChart.data.datasets = [
          {
            label: 'Valore Portfolio',
            data: countervail,
          },
          {
            label: 'Liquidità Inserita',
            data: investedAmount,
          },
        ];

        this.lineChart.update();
      },
      error: (error: any) => {
        console.error('Error fetching history', error);
      },
    });
  }

  onElementSelection(rank: RankElement) {
    this.location.go(`/ranking/${rank.id}`);
    this.updatePortfolioView(rank.id, rank.name);
  }
}
