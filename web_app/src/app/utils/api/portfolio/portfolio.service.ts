import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PortfolioService {
  private portfolioUrl = `${environment.backendUrl}/portfolio`;

  constructor(private http: HttpClient) {}

  createPortfolio(name: String): Observable<any> {
    return this.http.post(this.portfolioUrl + '/create', name);
  }

  getPortfolioById(id: number): Observable<any> {
    return this.http.get(this.portfolioUrl + '/get/' + id);
  }

  getPortfolioByUserId(): Observable<any> {
    return this.http.get(this.portfolioUrl + '/get/user');
  }

  getRanking(): Observable<any> {
    return this.http.get(this.portfolioUrl + '/ranking');
  }
}
