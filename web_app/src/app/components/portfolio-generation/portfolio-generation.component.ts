import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UploadFileDialog } from '../upload-file-dialog/upload-file-dialog.component';

@Component({
  selector: 'app-portfolio-generation',
  standalone: true,
  imports: [],
  templateUrl: './portfolio-generation.component.html',
  styleUrl: './portfolio-generation.component.css'
})
export class PortfolioGenerationComponent {

  constructor(public dialogUpdate: MatDialog, public dialogUpload: MatDialog) {
  }

  uploadData() {
    this.openDialogUpload();
  }

  openDialogUpload() {
    this.dialogUpdate.open(UploadFileDialog);
  }

}
