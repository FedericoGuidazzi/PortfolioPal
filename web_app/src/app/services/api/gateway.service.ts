// api.service.ts
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class ApiService {
  // backend url that returns the firebase user id of the current user
  userIdUrl = `${environment.backendUrl}/userid`;
  constructor(private http: HttpClient) {}
  getUserId(): Observable<any> {
    return this.http.get(this.userIdUrl);
  }
}