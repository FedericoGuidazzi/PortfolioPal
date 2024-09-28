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
import { AssetService } from '../../utils/api/asset/asset.service';
import { map, Observable, startWith } from 'rxjs';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

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
    MatAutocompleteModule,
  ],
})
export class UpdateTransactionDialog {
  tipologiaOptions: string[] = Object.values(TransactionTypes);
  transactions: TransactionData;
  mobile: boolean;

  inputValue: string = '';
  options: string[] = [];
  filteredOptions$!: Observable<string[]>;

  constructor(
    public dialogRef: MatDialogRef<UpdateTransactionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private assetService: AssetService
  ) {
    this.transactions = data.transactionData;
    this.mobile = data.mobile;
  }

  ngOnInit() {}

  onInputChange(inputValue: string) {
    if (inputValue.length === 0) {
      this.options = [];
    } else {
      this.generateNewOptions(inputValue);
    }
  }

  private generateNewOptions(inputValue: string): void {
    // Questa è una logica di esempio. Puoi personalizzarla in base alle tue esigenze.
    const upperInput = inputValue.toUpperCase();
    let options: string[] = [];
    this.assetService.searchAssets(upperInput).subscribe({
      next: (response: any) => {
        options = response as string[];
        this.options = options;
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
