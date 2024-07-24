import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private userUrl = `${environment.backendUrl}/user`;

  constructor(private http: HttpClient) {}

  getUser(): Observable<any> {
    return this.http.get(this.userUrl + '/get');
  }

  getUserById(id: String): Observable<any> {
    return this.http.get(this.userUrl + '/get/' + id);
  }

  createUser(): Observable<any> {
    return this.http.post(this.userUrl + '/create', {});
  }

  updatePrivacy(data: any): void {
    this.http.put(this.userUrl + '/update-privacy', data);
  }

  updateCurrency(data: any): Observable<any> {
    return this.http.put(this.userUrl + '/update-currency', data);
  }
}
