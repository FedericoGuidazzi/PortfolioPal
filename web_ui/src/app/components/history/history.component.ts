import { Component, Input, OnInit } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import {MatInputModule} from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';


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

  @Input() transactions: any[] = []; // Assuming transaction data structure
  displayedColumns: string[] = ['date', 'type', 'symbol', 'quantity', 'price'];

  constructor() {
  }

  ngOnInit(): void {

  }

  uploadData() {
    //todo
    throw new Error('Method not implemented.');
  }

}
