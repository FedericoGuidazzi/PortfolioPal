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
import { PortfolioService } from '../../utils/api/portfolio/portfolio.service';
import { TransactionService } from '../../utils/api/transaction/transaction.service';
import { UserService } from '../../utils/api/user/user.service';

interface Portfolio {
  name: string;
  share: boolean;
}

@Component({
  selector: 'app-generate-portfolio-dialog',
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
  templateUrl: './generate-portfolio-dialog.component.html',
  styleUrl: './generate-portfolio-dialog.component.css',
})
export class GeneratePortfolioDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<GeneratePortfolioDialogComponent>,
    private transactService: TransactionService,
    private portfolioService: PortfolioService,
    private userService: UserService
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
    formData.append('file', file, file.name);

    //call the API to send the file and then refresh the page
    this.userService.getUser().subscribe({
      next: (data) => {
        const portfolioForm: Portfolio = {
          name: data.name,
          share: data.sharePortfolio,
        };

        this.portfolioService.createPortfolio(portfolioForm).subscribe({
          next: (data) => {
            console.log(data);
            this.transactService
              .uploadTransaction(data.id, formData)
              .subscribe({
                next: (data) => {
                  window.location.reload();
                },
                error: (error) => {
                  console.error('There was an error!', error);
                  window.location.reload();
                },
              });
          },
        });
      },
      error: (error) => {
        console.error('There was an error!', error);
      },
    });
  }
}
