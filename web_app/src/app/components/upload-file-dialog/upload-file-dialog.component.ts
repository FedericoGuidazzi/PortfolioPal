import { HttpClientModule } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TransactionService } from '../../utils/api/transaction/transaction.service';
import { ActivatedRoute, Router } from '@angular/router';

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
    private dialogRef: MatDialogRef<UploadFileDialog>,
    private transactService: TransactionService,
    private route: Router,
    @Inject(MAT_DIALOG_DATA) private data: { portfolioId: any }
  ) {
    this.portfolioId = data.portfolioId;
  }

  portfolioId: any;

  ngOnInit() {}

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
    formData.append('file', file, file.name);
    //call the API to send the file and then refresh the page
    this.transactService
      .uploadTransaction(this.portfolioId, formData)
      .subscribe({
        next: (data) => {
          this.route.navigate(['/dashboard/' + this.portfolioId]);
          this.dialogRef.close();
        },
        error: (error) => {
          this.dialogRef.close();
        },
      });
  }
}
