import { inject } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = async () => {
  const angularFireAuth = inject(AngularFireAuth);
  const router = inject(Router);
  const user = await angularFireAuth.currentUser;
  // coerce to boolean
  const isLoggedIn = !!user;

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
