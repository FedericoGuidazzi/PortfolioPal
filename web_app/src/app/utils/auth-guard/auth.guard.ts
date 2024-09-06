import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { firstValueFrom } from 'rxjs';

export const authGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const isLoggedIn = await firstValueFrom(authService.isAuthenticated());

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
