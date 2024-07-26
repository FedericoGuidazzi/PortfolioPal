import { Component, Input, ViewChild } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Chart } from 'chart.js';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import {
  RankingTableComponent,
  User,
} from '../../components/ranking-table/ranking-table.component';
import { PortfolioAssets } from '../dashboard/dashboard.component';
import { UserService } from '../../utils/api/user/user.service';

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
  displayedColumns: string[] = ['symbol', 'portfolioPercentage', 'percentage'];
  dataSource = new MatTableDataSource<PortfolioAssets>([]);
  duration: string[] = ['1S', '1A', '5A', 'Max'];
  lineChart: any;
  portfolioInfo!: PortfolioAmount;
  ranking: User[] = [];
  currentUser!: User;

  @Input() assetRowDisabled: boolean = false;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.createLineChart();
    this.createDoughnutChart();
  }

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.ranking = [
      { pos: 1, name: 'Beach ball', score: 4, id: '1' },
      { pos: 2, name: 'Towel', score: 5, id: '2' },
      { pos: 3, name: 'Frisbee', score: 2, id: '3' },
      { pos: 4, name: 'Sunscreen', score: 4, id: '4' },
      { pos: 5, name: 'Cooler', score: 25, id: '5' },
    ];

    this.currentUser = this.ranking[3];

    // call portfolio API to get data
    this.portfolioInfo = {
      assetName: null,
      currency: 'EUR',
      amount: 3,
      percentage: 45,
    };

    //call API to get data
    this.dataSource.data = [
      { symbol: 'AAPL', percPortfolio: 3, percentage: 3 },
      { symbol: 'GOOGL', percPortfolio: 3, percentage: 5 },
      { symbol: 'AAPL', percPortfolio: 3, percentage: 3 },
      { symbol: 'GOOGL', percPortfolio: 3, percentage: 5 },
      { symbol: 'AAPL', percPortfolio: 3, percentage: 3 },
      { symbol: 'GOOGL', percPortfolio: 3, percentage: 5 },
      { symbol: 'AAPL', percPortfolio: 3, percentage: 3 },
      { symbol: 'GOOGL', percPortfolio: 3, percentage: 5 },
    ];
  }

  createDoughnutChart(): void {
    //call API to get data
    const data = {
      labels: ['Red', 'Blue', 'Yellow'],
      datasets: [
        {
          label: 'My First Dataset',
          data: [300, 50, 100],
          backgroundColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 205, 86)',
          ],
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

    const canvas = document.getElementById(
      'userDoughnutChart'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      new Chart(ctx, config);
    }
  }

  createLineChart(): void {
    const portfolioGrowth = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'Valore Portfolio',
          data: [100, 120, 130, 110, 150, 160, 140],
        },
        {
          label: 'Liquidità Inserita',
          data: [50, 50, 50, 50, 50, 50, 50],
        },
      ],
    };

    const config: any = {
      type: 'line',
      data: portfolioGrowth,
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

    const canvas = document.getElementById(
      'userLineChart'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      this.lineChart = new Chart(ctx, config);
    }
  }

  updateUserView(id: string) {
    // update selector indicators
    const selectorActive = document.querySelectorAll('.active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selector = document.querySelector('.selectors>div:first-child');
    if (selector) {
      selector.classList.add('active');
    }

    //call API to get data
    this.userService.getUserById(id).subscribe({
      next: (user) => {
        const userName = document.getElementById('userName');
        if (userName) {
          userName.textContent = user;
        }
      },
      error: (error) => {
        console.error('Error getting user', error);
      },
    });

    const portfolioGrowth = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'Valore Portfolio',
          data: [100, 120, 130, 110, 150, 160, 140],
        },
        {
          label: 'Liquidità Inserita',
          data: [50, 50, 50, 50, 50, 50, 50],
        },
      ],
    };

    this.lineChart.data.labels = portfolioGrowth.labels;
    this.lineChart.data.datasets[0].data = portfolioGrowth.datasets[0].data;
    this.lineChart.data.datasets[1].data = portfolioGrowth.datasets[1].data;
    this.lineChart.update();
  }

  updateLineChart(value: string) {
    //gestire cambio di dati richiamando API
    const selectorActive = document.querySelectorAll('.active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selector = document.getElementById(value);
    selector?.classList.add('active');

    switch (value) {
      case '1S':
        //fare chiamata api per prendere i dati rispetto alla scadenza desiderata
        this.lineChart.data.labels = [
          'asdfsdf',
          'afsfdsfds',
          'afsdfsdf',
          'afsfdsf',
          'asdfdsfd',
          'asfsdfsd',
          'asfsdfd',
        ];
        this.lineChart.data.datasets[0].data = [10, 12, 13, 11, 15, 16, 14];
        this.lineChart.data.datasets[1].data = [5, 5, 10, 5, 5, 5, 5];
        this.lineChart.update();
        break;
      case '1A':
        break;
      case '5A':
        break;
      case 'Max':
        break;
    }
  }

  onUserSelection(user: User) {
    console.log('User selected: ' + user.name);
    this.updateUserView(user.id);
  }
}
