import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UploadFileDialog } from '../upload-file-dialog/upload-file-dialog.component';

@Component({
  selector: 'app-upload-transaction',
  standalone: true,
  imports: [],
  templateUrl: './upload-transaction.component.html',
  styleUrl: './upload-transaction.component.css',
})
export class UploadTransactionComponent {
  constructor(private dialogUpdate: MatDialog) {}

  uploadData() {
    this.dialogUpdate.open(UploadFileDialog);
  }
}
