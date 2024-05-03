import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

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
    HttpClientModule
  ],
})
export class UploadFileDialog {

  constructor(
    public dialogRef: MatDialogRef<UploadFileDialog>, private http:HttpClient
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  fileChange(e:any){
    let fileList: FileList = e.target.files;

    if (fileList.length < 1) {
      return;
    }
    
    let file: File = fileList[0];
    let formData:FormData = new FormData();
    formData.append('uploadFile', file, file.name);

    //call the API to send the file and then refresh the page

    /*this.http.post(`${this.apiEndPoint}`, formData)
        .map(res => res.json())
        .catch(error => Observable.throw(error))
        .subscribe(
            data => console.log('success'),
            error => console.log(error)
        );*/
    
    window.location.reload();
  }
}

