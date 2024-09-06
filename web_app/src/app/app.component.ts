import { Component } from '@angular/core';
import { AuthService } from './utils/auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'PortfolioPal';

  public isAuthenticated!: boolean;
  private authSubscription!: Subscription;

  constructor(private authService: AuthService) {}
  ngOnInit() {
    this.authSubscription = this.authService
      .isAuthenticated()
      .subscribe((auth) => {
        this.isAuthenticated = auth;
        console.log('Stato di autenticazione:', auth);
      });
  }

  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
