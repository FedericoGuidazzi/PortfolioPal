import { Component } from '@angular/core';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';
import { HistoryComponent, TransactionData } from '../../components/history/history.component';
import { CardPortfolioValutationComponent, PortfolioAmount } from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { Chart } from 'chart.js';
import { config } from 'rxjs';

export interface PortfolioAssets{
  symbol: string,
  percentage: number
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LineChartComponent, HistoryComponent, CardPortfolioValutationComponent, MatFormFieldModule, MatTableModule, MatSortModule, MatPaginatorModule, MatInputModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

  transactions: TransactionData[] = [];
  data?: PortfolioAmount;
  assets: PortfolioAssets[] = [];
  displayedColumns: string[] = ['symbol', 'percentage'];

  constructor() {
    //use API to get data regarding transactions
    this.transactions = [
      { id: '', date: '', type: 'Compra', symbol: 'AAPL', quantity: 10, price: 150 },
      { id: '', date: '', type: 'Vendita', symbol: 'GOOGL', quantity: 5, price: 250 }
    ];

    this.data = {
      currency :"",
      amount: 3,
      percentage: 45
    }

    this.assets = [
      {symbol: 'AAPL', percentage: 3},
      {symbol: 'GOOGL', percentage: 5}
    ]

    const data = {
      labels: [
        'Red',
        'Blue',
        'Yellow'
      ],
      datasets: [{
        label: 'My First Dataset',
        data: [300, 50, 100],
        backgroundColor: [
          'rgb(255, 99, 132)',
          'rgb(54, 162, 235)',
          'rgb(255, 205, 86)'
        ],
        hoverOffset: 4
      }]
    };

    const config:any = {
      type: 'doughnut',
      data: data,
    };

    const canvas = document.getElementById('lineChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx){
      new Chart(ctx, config);
    }
  }
}
