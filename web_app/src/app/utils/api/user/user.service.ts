import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { PortfolioService } from '../portfolio/portfolio.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private userUrl = `${environment.backendUrl}/user`;

  constructor(
    private http: HttpClient,
    private portfolioService: PortfolioService
  ) {}

  getUser(): Observable<any> {
    return this.http.get(this.userUrl + '/get');
  }

  getUserById(id: String): Observable<any> {
    return this.http.get(this.userUrl + '/get-name/' + id);
  }

  createUser(): Observable<any> {
    return this.http.post(this.userUrl + '/create', {});
  }

  updatePrivacy(data: boolean): Observable<any> {
    return this.http.put(this.userUrl + '/update-privacy', data);
  }

  updateCurrency(data: string): Observable<any> {
    return this.http.put(this.userUrl + '/update-currency', data);
  }
}
