import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Chart } from 'chart.js';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import {
  HistoryComponent,
  TransactionData,
} from '../../components/history/history.component';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';
import { PortfolioGenerationComponent } from '../../components/portfolio-generation/portfolio-generation.component';
import { UpdateTransactionDialog } from '../../components/update-transaction-dialog/update-transaction-dialog.component';
import { UploadFileDialog } from '../../components/upload-file-dialog/upload-file-dialog.component';
import { HistoryService } from '../../utils/api/portfolio/history.service';
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';
import { TransactionService } from '../../utils/api/transaction/transaction.service';
import { Location } from '@angular/common';
import { UploadTransactionComponent } from '../../components/upload-transaction/upload-transaction.component';

export interface PortfolioAssets {
  symbolId: string;
  percPortfolio: number;
}

export interface HistoryItem {
  countervail: number;
  date: string;
  id: number;
  investedAmount: number;
  percentageValue: number;
  portfolioId: number;
  withdrawnAmount: number;
}

interface AssetQty {
  symbolId: string;
  amount: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    LineChartComponent,
    HistoryComponent,
    CardPortfolioValutationComponent,
    PortfolioGenerationComponent,
    UploadTransactionComponent,
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
  portfolioInfo!: PortfolioAmount;
  assets: PortfolioAssets[] = [];
  assetDisplayedColumns: string[] = ['symbol', 'portfolioPercentage'];
  assetDataSource = new MatTableDataSource<PortfolioAssets>(this.assets);
  @ViewChild('assetPaginator') assetPaginator!: MatPaginator;

  transactions: TransactionData[] = [];
  transactionDisplayedColumns: string[] = [
    'date',
    'type',
    'symbol',
    'quantity',
    'price',
    'currency',
    'actions',
  ];
  transactionDataSource = new MatTableDataSource<TransactionData>(
    this.transactions
  );
  @ViewChild('transactionPaginator') transactionPaginator!: MatPaginator;

  duration: string[] = ['1A', '5A', 'Max'];
  lineChart: any;
  doughnutChart: any;

  @Input() hideHistory: boolean = false;
  @Input() colView: boolean = false;
  @Input() assetRowDisabled: boolean = false;

  existPortfolio: boolean = false;
  transactionDataEmpty: boolean = true;

  @ViewChild('portfolioContainer') portfolioContainer!: ElementRef;
  @ViewChild('portfolioGenerationContainer')
  portfolioGenerationContainer!: ElementRef;
  @ViewChild('uploadTransactionContainer')
  uploadTransactionContainer!: ElementRef;

  portfolioId: any;

  constructor(
    private historyService: HistoryService,
    private portfolioService: PortfolioService,
    private transactionService: TransactionService,
    private dialogUpdate: MatDialog,
    private dialogUpload: MatDialog,
    private location: Location
  ) {}

  ngAfterViewInit(): void {
    this.portfolioService.getPortfolioByUserId().subscribe({
      next: (data) => {
        if (data == null) {
          this.portfolioGenerationContainer.nativeElement.classList.remove(
            'd-none'
          );
          return;
        }

        this.existPortfolio = true;
        this.portfolioGenerationContainer.nativeElement.classList.add('d-none');

        this.assetDataSource.paginator = this.assetPaginator;
        this.transactionDataSource.paginator = this.transactionPaginator;

        this.location.go('/dashboard/' + data.id);
        this.portfolioId = data.id;

        // Request all transactions
        this.transactionService
          .getAllTransactionByPortfolioId(data.id)
          .subscribe({
            next: (data) => {
              if (data.length == 0) {
                this.uploadTransactionContainer.nativeElement.classList.remove(
                  'd-none'
                );
                return;
              }
              this.portfolioContainer.nativeElement.classList.remove('d-none');

              this.transactions = this.hideHistory
                ? []
                : data.map(
                    (item: {
                      id: any;
                      date: any;
                      type: any;
                      symbolId: any;
                      amount: any;
                      price: any;
                      currency: any;
                    }) => {
                      return {
                        id: item.id,
                        date: item.date,
                        type: item.type,
                        symbol: item.symbolId,
                        quantity: item.amount,
                        price: item.price,
                        currency: item.currency,
                      };
                    }
                  );
              this.transactionDataSource.data = this.transactions;
            },
            error: (error) => {
              console.error('Error fetching transactions', error);
            },
          });

        // Request asset allocation
        this.transactionService.getAssetAllocation(data.id).subscribe({
          next: (data: AssetQty[]) => {
            const total = data.reduce((acc, item) => acc + item.amount, 0);
            this.assets = data.map((item) => {
              let perc = (item.amount / total) * 100;
              perc = parseFloat(perc.toFixed(2));
              return {
                symbolId: item.symbolId,
                percPortfolio: perc,
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
        this.historyService.getPortfolioHistoryById(data.id, '1S').subscribe({
          next: (data: HistoryItem[]) => {
            if (data.length == 0) {
              // create empty chart
              this.portfolioInfo = {
                assetName: null,
                currency: 'EUR',
                amount: 0,
                percentage: 0,
              };

              this.createLineChart([], [], []);

              return;
            }

            let amount = data[data.length - 1].countervail;
            let percentage = data[data.length - 1].percentageValue;

            this.portfolioInfo = {
              assetName: null,
              currency: 'EUR',
              amount:
                amount <= 0
                  ? parseFloat(amount.toFixed(5))
                  : parseFloat(amount.toFixed(2)),
              percentage: parseFloat(percentage.toFixed(2)),
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
      },
      error: (error) => {
        this.portfolioGenerationContainer.nativeElement.classList.remove(
          'd-none'
        );
        console.error('Error fetching portfolio', error);
      },
    });
  }

  @ViewChild('assetDoughnutContainer') assetDoughnutContainer!: ElementRef;
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
      '<canvas class="h-100 w-100" id="doughnutChart"></canvas>';
    const canvas = document.getElementById(
      'doughnutChart'
    ) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }
    if (this.doughnutChart) {
      this.doughnutChart.destroy();
    }
    this.doughnutChart = new Chart(canvas, config);
  }

  @ViewChild('historyGraphContainer') historyGraphContainer!: ElementRef;
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
      '<canvas class="h-100 w-100" id="historyLineChart"></canvas>';
    const canvas = document.getElementById(
      'historyLineChart'
    ) as HTMLCanvasElement;
    if (!canvas) {
      return;
    }

    if (this.lineChart) {
      this.lineChart.destroy();
    }
    this.lineChart = new Chart(canvas, config);
  }

  updateHistoryGraphView(value: string) {
    const selectorActive = document.querySelectorAll('.active');
    selectorActive.forEach(function (selected) {
      selected.classList.remove('active');
    });

    let selector = document.getElementById(value);
    selector?.classList.add('active');

    this.historyService
      .getPortfolioHistoryById(this.portfolioId, value)
      .subscribe({
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

  // TRANSACTION METHODS
  updateTransactionList(transactions: TransactionData[]) {
    this.transactionDataSource.data = transactions;
  }

  uploadData() {
    this.openDialogUpload(this.portfolioId);
  }

  updateData(id: number) {
    //Method to update data
    this.openDialogUpdate(id, this.portfolioId);
  }

  deleteData(id: number) {
    //call API to delete record
    this.transactionService
      .deleteTransaction(id, this.portfolioId)
      .subscribe((data) => {
        this.refreshPage();
      });
  }

  updateDeleteDataMobile(id: number) {
    const portfolioId = this.portfolioId;

    const foundTransaction = this.transactions.find(
      (transaction) => transaction.id === id
    );
    const transaction = {
      id: foundTransaction?.id,
      date: foundTransaction?.date,
      type: foundTransaction?.type,
      symbol: foundTransaction?.symbol,
      quantity: foundTransaction?.quantity,
      price: foundTransaction?.price,
      currency: foundTransaction?.currency,
    };
    const dialogRef = this.dialogUpdate.open(UpdateTransactionDialog, {
      data: { transactionData: transaction, mobile: true },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result.length == 1) {
          this.transactionService
            .deleteTransaction(result[0], portfolioId)
            .subscribe((data) => {
              this.refreshPage();
            });
        }

        //if something has changed update the row calling the API and refresh the page
        const id = result[0];
        const date = result[1];
        const type = result[2];
        const symbol = result[3];
        const quantity = result[4];
        const price = result[5];
        const currency = result[6];

        if (
          date !== foundTransaction?.date ||
          type !== foundTransaction?.type ||
          symbol !== foundTransaction?.symbol ||
          quantity !== foundTransaction?.quantity ||
          price !== foundTransaction?.price ||
          currency !== foundTransaction?.currency
        ) {
          this.transactionService
            .modifyTransaction(id, {
              date: date == '' || date == null ? foundTransaction?.date : date,
              type: type == '' || type == null ? foundTransaction?.type : type,
              symbolId:
                symbol == '' || symbol == null
                  ? foundTransaction?.symbol
                  : symbol,
              amount:
                quantity == '' || quantity == null
                  ? foundTransaction?.quantity
                  : quantity,
              price:
                price == '' || price == null ? foundTransaction?.price : price,
              currency:
                currency == '' || currency == null
                  ? foundTransaction?.currency
                  : currency,
              portfolioId: portfolioId,
            })
            .subscribe((data) => {
              console.log('Transaction updated');
              this.refreshPage();
            });
        }
      }
    });
  }

  openDialogUpload(portfolioId: any): void {
    this.dialogUpdate.open(UploadFileDialog, {
      data: { portfolioId },
    });
  }

  openDialogUpdate(id: number, portfolioId: number): void {
    const foundTransaction = this.transactions.find(
      (transaction) => transaction.id === id
    );
    const transaction = {
      id: foundTransaction?.id,
      date: foundTransaction?.date,
      type: foundTransaction?.type,
      symbol: foundTransaction?.symbol,
      quantity: foundTransaction?.quantity,
      price: foundTransaction?.price,
      currency: foundTransaction?.currency,
    };
    const dialogRef = this.dialogUpdate.open(UpdateTransactionDialog, {
      data: { transactionData: transaction, mobile: false },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        //if something has changed update the row calling the API and refresh the page
        const id = result[0];
        const date = result[1];
        const type = result[2];
        const symbol = result[3];
        const quantity = result[4];
        const price = result[5];
        const currency = result[6];

        if (
          date !== foundTransaction?.date ||
          type !== foundTransaction?.type ||
          symbol !== foundTransaction?.symbol ||
          quantity !== foundTransaction?.quantity ||
          price !== foundTransaction?.price ||
          currency !== foundTransaction?.currency
        ) {
          this.transactionService
            .modifyTransaction(id, {
              date: date == '' || date == null ? foundTransaction?.date : date,
              type: type == '' || type == null ? foundTransaction?.type : type,
              symbolId:
                symbol == '' || symbol == null
                  ? foundTransaction?.symbol
                  : symbol,
              amount:
                quantity == '' || quantity == null
                  ? foundTransaction?.quantity
                  : quantity,
              price:
                price == '' || price == null ? foundTransaction?.price : price,
              currency:
                currency == '' || currency == null
                  ? foundTransaction?.currency
                  : currency,
              portfolioId: portfolioId,
            })
            .subscribe((data) => {
              console.log('Transaction updated');
              this.refreshPage();
            });
        }
      }
    });
  }

  refreshPage() {
    window.location.reload();
  }
}
