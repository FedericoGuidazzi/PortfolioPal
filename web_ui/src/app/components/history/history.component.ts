import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit{
  transactions: any[] = []; // Assuming transaction data structure
  
  ngOnInit(): void {
    // Fetch transactions data from API or service
    this.getTransactions();
  }

  getTransactions() {
    // Call API or service to get transaction data
    // Assuming transactions is an array of objects with date, type, symbol, quantity, and price properties
    this.transactions = [
      { date: new Date(), type: 'Compra', symbol: 'AAPL', quantity: 10, price: 150 },
      { date: new Date(), type: 'Vendita', symbol: 'GOOGL', quantity: 5, price: 250 },
      // Add more transaction data as needed
    ];
  }
}
