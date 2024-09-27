import { HttpClient, HttpParams } from '@angular/common/http';
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
    return this.http.put(this.transactionUrl + '/update/' + id, data);
  }

  deleteTransaction(id: Number, portfolioId: any): Observable<any> {
    const params = new HttpParams().set('portfolioId', portfolioId.toString());
    return this.http.delete(this.transactionUrl + '/delete/' + id, { params });
  }

  getAllTransactionByPortfolioId(id: Number): Observable<any> {
    return this.http.get(this.transactionUrl + '/get-by-portfolio/' + id);
  }

  uploadTransaction(portfolioId: number, data: any): Observable<any> {
    return this.http.post(this.transactionUrl + '/upload/' + portfolioId, data);
  }

  getAssetAllocation(portfolioId: Number): Observable<any> {
    return this.http.get(
      this.transactionUrl + '/get-by-portfolio/' + portfolioId + '/assets-qty'
    );
  }

  getAllTransactionByPortfolioIdAndAssetId(
    id: Number,
    assetId: Number
  ): Observable<any> {
    return this.http.get(
      this.transactionUrl + '/get-by-portfolio/' + id + '/asset/' + assetId
    );
  }
}
