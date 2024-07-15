import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { Observable, from } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private angularFireAuth: AngularFireAuth) {}

  loginWithEmailAndPassword(data: any): Observable<any> {
    return from(
      this.angularFireAuth.signInWithEmailAndPassword(data.email, data.password)
    );
  }

  logout(): Observable<any> {
    return from(this.angularFireAuth.signOut());
  }

  signUpWithEmailAndPassword(data: any): Observable<any> {
    return from(
      this.angularFireAuth.createUserWithEmailAndPassword(
        data.email,
        data.password
      )
    );
  }

  
}
