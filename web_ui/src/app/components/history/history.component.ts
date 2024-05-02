import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import {MatInputModule} from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { PortfolioAssets } from '../../pages/dashboard/dashboard.component';


export interface TransactionData {
  id: string;
  date: string;
  type: string;
  symbol: string;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [MatFormFieldModule, MatTableModule, MatSortModule, MatPaginatorModule, MatInputModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit{

  @Input() transactions: TransactionData[] = []; // Assuming transaction data structure
  displayedColumns: string[] = ['date', 'type', 'symbol', 'quantity', 'price'];
  dataSource = new MatTableDataSource<TransactionData>(this.transactions);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  constructor() {
  }

  ngOnInit(): void {
    this.dataSource.data = this.transactions;
  }

  uploadData() {
    //todo
    throw new Error('Method not implemented.');
  }

}
