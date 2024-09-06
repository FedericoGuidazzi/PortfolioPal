import { Directive, HostListener } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Directive({
  selector: '[appGoogleSso]',
  standalone: true,
})
export class GoogleSsoDirective {
  constructor(private authService: AuthService) {}
  @HostListener('click')
  async onClick() {
    this.authService.loginWithGoogle();
    // do what you want with the credentials, for ex adding them to firestore...
  }
}
