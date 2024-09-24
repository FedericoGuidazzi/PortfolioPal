import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import Chart from 'chart.js/auto';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import {
  HistoryComponent,
  TransactionData,
} from '../../components/history/history.component';
import { AssetService } from '../../utils/api/asset/asset.service';
import { TransactionService } from '../../utils/api/transaction/transaction.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSortModule } from '@angular/material/sort';

export interface AssetData {
  name: string;
  currentValue: number;
  valueInPortfolio: number;
  sharesNumber: number;
  description: string;
  percetageInPortfolio: number;
  percentageWinLose: number;
  currency: string;
  totalCost: number;
  currentValuation: number;
  balance: number;
  averageCostPerShare: number;
}

interface AssetHistory {
  date: string;
  price: number;
}

@Component({
  selector: 'app-asset',
  standalone: true,
  imports: [
    /*LineChartComponent,*/ HistoryComponent,
    CardPortfolioValutationComponent,
    MatFormFieldModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    RouterLink,
  ],
  templateUrl: './asset.component.html',
  styleUrl: './asset.component.css',
})
export class AssetComponent {
  assetName: any;
  portfolioId: any;
  data?: PortfolioAmount;
  transactions: TransactionData[] = [];
  assetData?: AssetData;
  dataAsset: any = {};
  duration: string[] = ['1A', '5A', 'Max'];
  lineChart: any;
  assetHistory: AssetHistory[] = [];

  transactionDisplayedColumns: string[] = [
    'date',
    'type',
    'symbol',
    'quantity',
    'price',
    'currency',
  ];
  transactionDataSource = new MatTableDataSource<TransactionData>(
    this.transactions
  );

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private route: ActivatedRoute,
    private assetService: AssetService,
    private transactionService: TransactionService
  ) {}

  prepareData() {
    // get portfolioId from route
    this.route.paramMap.subscribe(
      (params) => (this.portfolioId = params.get('portfolioId'))
    );
    this.route.paramMap.subscribe(
      (params) => (this.assetName = params.get('assetName'))
    );

    // get asset data from API
    this.assetService.getAssetData(this.assetName, '1S').subscribe({
      next: (data: any) => {
        this.transactionService.getAssetAllocation(this.portfolioId).subscribe({
          next: (allocation: any) => {
            const asset = allocation.find(
              (asset: any) => asset['symbolId'] === this.assetName
            );

            const total = allocation.reduce(
              (acc: any, item: any) => acc + item.amount,
              0
            );

            let valueInPortfolio =
              asset['amount'] * data['prices'][data['prices'].length - 1];
            valueInPortfolio =
              valueInPortfolio <= 0
                ? parseFloat(valueInPortfolio.toFixed(5))
                : parseFloat(valueInPortfolio.toFixed(2));

            this.assetData = {
              name: data['symbol'],
              currentValue: data['prices'][data['prices'].length - 1],
              valueInPortfolio: valueInPortfolio,
              sharesNumber: allocation['shares'],
              description: data['description'],
              percetageInPortfolio: (asset['amount'] / total) * 100,
              percentageWinLose: allocation['percentage'],
              currency: data['currency'],
              totalCost: 0,
              currentValuation: 0,
              balance: 0,
              averageCostPerShare:
                data['prices'].reduce(
                  (tot: number, item: number) => tot + item,
                  0
                ) / data['prices'].length,
            };

            this.data = {
              assetName: this.assetName,
              currency: this.assetData.currency,
              amount:
                this.assetData.currentValue <= 0
                  ? parseFloat(this.assetData.currentValue.toFixed(5))
                  : parseFloat(this.assetData.currentValue.toFixed(2)),
              percentage: this.assetData.percentageWinLose,
            };

            this.createDoughnutChart(this.assetData);
          },
          error: (error) => {
            console.error('There was an error!', error);
          },
        });

        this.transactionService
          .getAllTransactionByPortfolioIdAndAssetId(
            this.portfolioId,
            this.assetName
          )
          .subscribe({
            next: (transactions: any) => {
              this.transactions = transactions.map((transaction: any) => {
                return {
                  id: transaction['id'],
                  date: transaction['date'],
                  type: transaction['type'],
                  symbol: transaction['symbolId'],
                  quantity: transaction['amount'],
                  price: transaction['price'],
                  currency: transaction['currency'],
                };
              });
              this.transactionDataSource.data = this.transactions;
            },
            error: (error) => {
              console.error('There was an error!', error);
            },
          });

        this.assetHistory = data['prices'].map(
          (price: number, index: number) => {
            return {
              date: data['dates'][index],
              price: price,
            };
          }
        );

        this.createLineChart(this.assetHistory);
      },
      error: (error) => {
        console.error('There was an error!', error);
      },
    });
  }

  createDoughnutChart(assetData: AssetData): void {
    //call API to get data
    const data = {
      labels: [],
      datasets: [
        {
          label: '',
          data: [
            assetData.percetageInPortfolio,
            100 - assetData.percetageInPortfolio,
          ],
          backgroundColor: ['rgb(255, 99, 132)'],
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
        cutout: '70%',
      },
    };

    const canvas = document.getElementById(
      'doughnutChartAsset'
    ) as HTMLCanvasElement;
    if (canvas) {
      new Chart(canvas, config);
    }
  }

  createLineChart(assetHistory: AssetHistory[]): void {
    this.dataAsset = {
      labels: assetHistory.map((item) => item.date),
      datasets: [
        {
          label: 'Valore Asset',
          data: assetHistory.map((item) => item.price),
        },
      ],
    };

    const config: any = {
      type: 'line',
      data: this.dataAsset,
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
      'lineChartAsset'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      this.lineChart = new Chart(ctx, config);
    }
  }

  ngAfterViewInit() {
    this.prepareData();
  }

  updateHistoryGraphView(value: string) {
    //gestire cambio di dati richiamando API
    const selectorActive = document.querySelectorAll('.active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selector = document.getElementById(value);
    selector?.classList.add('active');

    this.assetService.getAssetData(this.assetName, value).subscribe({
      next: (data: any) => {
        this.assetHistory = data['prices'].map(
          (price: number, index: number) => {
            return {
              date: data['dates'][index],
              price: price,
            };
          }
        );

        this.lineChart.data.labels = this.assetHistory.map((item) => item.date);
        this.lineChart.data.datasets[0].data = this.assetHistory.map(
          (item) => item.price
        );
        this.lineChart.update();
      },
      error: (error) => {
        console.error('There was an error!', error);
      },
    });
  }
}
