import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GeneratePortfolioDialogComponent } from '../generate-portfolio-dialog/generate-portfolio-dialog.component';

@Component({
  selector: 'app-portfolio-generation',
  standalone: true,
  imports: [],
  templateUrl: './portfolio-generation.component.html',
  styleUrl: './portfolio-generation.component.css',
})
export class PortfolioGenerationComponent {
  constructor(private dialogUpdate: MatDialog) {}

  uploadData() {
    this.dialogUpdate.open(GeneratePortfolioDialogComponent);
  }
}
