import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GeneratePortfolioDialogComponent } from '../generate-portfolio-dialog/generate-portfolio-dialog.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-portfolio-generation',
  standalone: true,
  imports: [],
  templateUrl: './portfolio-generation.component.html',
  styleUrl: './portfolio-generation.component.css',
})
export class PortfolioGenerationComponent {
  constructor(private dialogUpdate: MatDialog, private router: Router) {}

  uploadData() {
    const dialogRef = this.dialogUpdate.open(GeneratePortfolioDialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      console.log('The dialog was closed with result: ' + result);
      this.router.navigate(['/dashboard/' + result]);
    });
  }
}
