import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class HistoryService {
  private portfolioUrl = `${environment.backendUrl}/portfolio-history`;

  constructor(private http: HttpClient) {}

  getPortfolioHistoryById(id: Number, duration: String): any {
    return this.http.get(
      this.portfolioUrl + '/' + id + '?duration=' + duration
    );
  }
}
