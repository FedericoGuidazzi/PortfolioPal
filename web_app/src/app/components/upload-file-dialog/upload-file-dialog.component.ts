import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TransactionService } from '../../utils/api/transaction/transaction.service';

@Component({
  selector: 'app-upload-file-dialog',
  templateUrl: './upload-file-dialog.component.html',
  styleUrl: './upload-file-dialog.component.css',
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
    HttpClientModule,
  ],
})
export class UploadFileDialog {
  constructor(
    public dialogRef: MatDialogRef<UploadFileDialog>,
    private transactService: TransactionService
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  fileChange(e: any) {
    let fileList: FileList = e.target.files;

    if (fileList.length < 1) {
      return;
    }

    let file: File = fileList[0];
    let formData: FormData = new FormData();
    formData.append('uploadFile', file, file.name);

    //call the API to send the file and then refresh the page
    this.transactService
      .uploadTransaction(1, formData.get('uploadFile'))
      .subscribe({
        next: (data) => {
          console.log(data);
        },
        error: (error) => {
          console.log(error);
        },
      });

    window.location.reload();
  }
}
