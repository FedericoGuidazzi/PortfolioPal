import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import firebase from 'firebase/compat/app';
import { Observable, from, map } from 'rxjs';
import { UserService } from '../api/user/user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private afAuth: AngularFireAuth,
    private userService: UserService
  ) {
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

  loginWithGoogle(): Observable<any> {
    const provider = new firebase.auth.GoogleAuthProvider();
    return from(this.afAuth.signInWithPopup(provider));
  }

  loginWithEmailAndPassword(data: any): Observable<any> {
    return from(
      this.afAuth.signInWithEmailAndPassword(data.email, data.password)
    );
  }

  logout(): Observable<any> {
    return from(this.afAuth.signOut());
  }

  async signUpWithEmailAndPassword(data: any): Promise<Observable<any>> {
    const creds = await this.afAuth.createUserWithEmailAndPassword(
      data.email,
      data.password
    );
    return from(this.userService.createUser());
  }

  async signUpWithGoogle(): Promise<Observable<any>> {
    const provider = new firebase.auth.GoogleAuthProvider();
    const creds = await this.afAuth.signInWithPopup(provider);
    return from(this.userService.createUser());
  }

  isAuthenticated(): Observable<boolean> {
    return this.afAuth.authState.pipe(map((user) => !!user));
  }
}
