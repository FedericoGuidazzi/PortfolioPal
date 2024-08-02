import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private transactionUrl = `${environment.backendUrl}/transaction`;

  constructor(private http: HttpClient) {}

  modifyTransaction(id: Number, data: any): Observable<any> {
    return this.http.post(this.transactionUrl + '/update/' + id, data);
  }

  deleteTransaction(id: Number, data: any): Observable<any> {
    return this.http.delete(this.transactionUrl + '/delete/' + id, data);
  }

  getAllTransactionByPortfolioId(id: Number): Observable<any> {
    return this.http.get(this.transactionUrl + '/get-by-portfolio/' + id);
  }

  uploadTransaction(portfolioId: Number, data: any): Observable<any> {
    return this.http.post(this.transactionUrl + '/upload/' + portfolioId, data);
  }
}
