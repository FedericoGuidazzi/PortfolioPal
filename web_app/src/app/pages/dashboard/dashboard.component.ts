import { Component, Input, ViewChild } from '@angular/core';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';
import {
  HistoryComponent,
  TransactionData,
} from '../../components/history/history.component';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Chart } from 'chart.js';
import { RouterLink } from '@angular/router';

export interface PortfolioAssets {
  symbol: string;
  percPortfolio: number;
  percentage: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    LineChartComponent,
    HistoryComponent,
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
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  transactions: TransactionData[] = [];
  data?: PortfolioAmount;
  assets: PortfolioAssets[] = [];
  displayedColumns: string[] = ['symbol', 'portfolioPercentage', 'percentage'];
  dataSource = new MatTableDataSource<PortfolioAssets>(this.assets);
  duration: string[] = ['1A', '5A', 'Max'];
  lineChart: any;
  portfolioData: any = {};

  @Input() hideHistory: boolean = false;
  @Input() colView: boolean = false;
  @Input() assetRowDisabled: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  constructor() {
    //use API to get data regarding transactions
    this.transactions = !this.hideHistory
      ? [
          {
            id: 1,
            date: 'wjebf',
            type: 'Acquisto',
            symbol: 'AAPL',
            quantity: 10,
            price: 150,
            currency: '$',
          },
          {
            id: 2,
            date: 'jbnwefkjn',
            type: 'Vendita',
            symbol: 'GOOGL',
            quantity: 5,
            price: 250,
            currency: '$',
          },
          {
            id: 3,
            date: 'wjebf',
            type: 'Acquisto',
            symbol: 'AAPL',
            quantity: 10,
            price: 150,
            currency: '$',
          },
          {
            id: 4,
            date: 'jbnwefkjn',
            type: 'Vendita',
            symbol: 'GOOGL',
            quantity: 5,
            price: 250,
            currency: '$',
          },
          {
            id: 5,
            date: 'wjebf',
            type: 'Acquisto',
            symbol: 'AAPL',
            quantity: 10,
            price: 150,
            currency: '$',
          },
          {
            id: 6,
            date: 'jbnwefkjn',
            type: 'Vendita',
            symbol: 'GOOGL',
            quantity: 5,
            price: 250,
            currency: '$',
          },
          {
            id: 7,
            date: 'wjebf',
            type: 'Acquisto',
            symbol: 'AAPL',
            quantity: 10,
            price: 150,
            currency: '$',
          },
          {
            id: 8,
            date: 'jbnwefkjn',
            type: 'Vendita',
            symbol: 'GOOGL',
            quantity: 5,
            price: 250,
            currency: '$',
          },
        ]
      : [];

    this.data = {
      assetName: null,
      currency: 'EUR',
      amount: 3,
      percentage: 45,
    };
  }

  ngOnInit(): void {
    this.createLineChart();
    this.createDoughnutChart();
    this.createPaginator();
  }

  createPaginator() {
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
      },
    };

    const canvas = document.getElementById(
      'doughnutChart'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      new Chart(ctx, config);
    }
  }

  createLineChart(): void {
    this.portfolioData = {
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
      data: this.portfolioData,
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

    const canvas = document.getElementById('lineChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      this.lineChart = new Chart(ctx, config);
    }
  }

  updateData(value: string) {
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
}
