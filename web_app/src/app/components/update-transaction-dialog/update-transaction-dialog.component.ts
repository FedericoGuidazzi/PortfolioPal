import { Component, Inject, Input } from '@angular/core';
import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  TransactionData,
  TransactionTypes,
} from '../history/history.component';
import { MatOption } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';

export interface DialogData {
  transactionData: TransactionData;
  mobile: boolean;
}

@Component({
  selector: 'update-transaction-dialog.',
  templateUrl: './update-transaction-dialog.component.html',
  styleUrl: './update-transaction-dialog.component.css',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatOption,
    MatSelectModule,
  ],
})
export class UpdateTransactionDialog {
  tipologiaOptions: string[] = Object.values(TransactionTypes);
  transactions: TransactionData;
  mobile: boolean;
  constructor(
    public dialogRef: MatDialogRef<UpdateTransactionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.transactions = data.transactionData;
    this.mobile = data.mobile;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
