import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import firebase from 'firebase/compat/app';
import { Observable, from, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private afAuth: AngularFireAuth) {
    this.setPersistence();
  }

  private async setPersistence() {
    try {
      await this.afAuth.setPersistence(firebase.auth.Auth.Persistence.SESSION);
      console.log('Persistenza impostata su SESSION');
    } catch (error) {
      console.error("Errore nell'impostazione della persistenza:", error);
    }
  }

  loginWithEmailAndPassword(data: any): Observable<any> {
    return from(
      this.afAuth.signInWithEmailAndPassword(data.email, data.password)
    );
  }

  logout(): Observable<any> {
    return from(this.afAuth.signOut());
  }

  signUpWithEmailAndPassword(data: any): Observable<any> {
    return from(
      this.afAuth.createUserWithEmailAndPassword(data.email, data.password)
    );
  }

  isAuthenticated(): Observable<boolean> {
    return this.afAuth.authState.pipe(map((user) => !!user));
  }
}
