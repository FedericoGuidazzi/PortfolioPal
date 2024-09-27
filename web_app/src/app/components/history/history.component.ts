import { Component, Input, ViewChild } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { UpdateTransactionDialog } from '../../components/update-transaction-dialog/update-transaction-dialog.component';
import { UploadFileDialog } from '../upload-file-dialog/upload-file-dialog.component';
import { RouterLink } from '@angular/router';

export enum TransactionTypes {
  ACQUISTO = 'Acquisto',
  VENDITA = 'Vendita',
}

export interface TransactionData {
  id: number;
  date: string;
  type: 'Acquisto' | 'Vendita';
  symbol: string;
  quantity: number;
  price: number;
  currency: string;
}

@Component({
  selector: 'app-history',
  standalone: true,
  templateUrl: './history.component.html',
  styleUrl: './history.component.css',
  imports: [
    MatFormFieldModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    RouterLink,
  ],
})
export class HistoryComponent {
  @Input() transactions: TransactionData[] = []; // Assuming transaction data structure
  displayedColumns: string[] = [
    'date',
    'type',
    'symbol',
    'quantity',
    'price',
    'currency',
    'actions',
  ];
  dataSource = new MatTableDataSource<TransactionData>(this.transactions);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  constructor(public dialogUpdate: MatDialog, public dialogUpload: MatDialog) {}

  ngOnInit(): void {
    this.dataSource.data = this.transactions;
  }

  uploadData() {
    this.openDialogUpload();
  }

  updateDataDialog(id: number) {
    this.openDialogUpdate(id);
  }

  deleteDataDialog(id: number) {
    //call API to delete record
    this.deleteData(id);
  }

  updateData(id: number) {
    //Method to update data
    this.openDialogUpdate(id);
  }

  deleteData(id: number) {
    //call API to delete record
    this.refreshPage();
  }

  updateDeleteDataMobile(id: number) {
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

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        //if something has changed update the row calling the API and refresh the page
        this.updateData(id);
        this.refreshPage();
      }
    });
  }

  openDialogUpload() {
    this.dialogUpdate.open(UploadFileDialog);
  }

  openDialogUpdate(id: number): void {
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

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        //if something has changed update the row calling the API and refresh the page
        this.updateData(id);
        this.refreshPage();
      }
    });
  }

  refreshPage() {
    window.location.reload();
  }
}
