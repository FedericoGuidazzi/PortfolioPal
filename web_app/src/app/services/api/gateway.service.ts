// api.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class GatewayService {
  // backend url that returns the firebase user id of the current user
  private userUrl = `${environment.backendUrl}/user`;
  private transactionUrl = `${environment.backendUrl}/transaction`;

  constructor(private http: HttpClient) {}

  getUser(): Observable<any> {
    return this.http.get(this.userUrl + '/get/');
  }

  getUserById(data: String): Observable<any> {
    return this.http.get(this.userUrl + '/get/' + data);
  }

  createUser(data: any): Observable<any> {
    return this.http.post(this.userUrl + '/create/', data);
  }
}
