import {Component, Inject} from '@angular/core';
import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { TransactionData } from '../history/history.component';


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
  ],
})
export class UpdateTransactionDialog {
  constructor(
    public dialogRef: MatDialogRef<UpdateTransactionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: TransactionData,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
