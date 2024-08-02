import { CommonModule, Location } from '@angular/common';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { UserService } from '../../utils/api/user/user.service';
import { AuthService } from '../../utils/auth/auth.service';

interface BaseSetting {
  name: string;
  value: boolean | string;
  icon_loc: string;
  disabled: boolean;
  type: 'toggle' | 'select';
}

interface ToggleSetting extends BaseSetting {
  type: 'toggle';
  value: boolean;
}

interface SelectSetting extends BaseSetting {
  type: 'select';
  value: string;
  supportedField: string[];
}

type Setting = ToggleSetting | SelectSetting;

interface Settings {
  [key: string]: Setting;
}
@Component({
  selector: 'app-user-navbar',
  standalone: true,
  imports: [MatSlideToggleModule, RouterLink, CommonModule, RouterModule],
  templateUrl: './user-navbar.component.html',
  styleUrl: './user-navbar.component.css',
})
export class UserNavbarComponent {
  supportedLanguages: string[] = ['Italiano', 'English', 'Spanish', 'French'];
  supportedCurrencies: string[] = ['EUR', 'USD', 'GBP', 'JPY', 'CNY'];

  settings: Settings = {
    language: {
      name: 'lingua',
      value: 'Italiano',
      icon_loc: '/assets/icons/ic-language.svg',
      disabled: true,
      supportedField: this.supportedLanguages,
      type: 'select',
    },
    currency: {
      name: 'valuta',
      value: 'EUR',
      icon_loc: '/assets/icons/ic-currency-exchange.svg',
      disabled: false,
      supportedField: this.supportedCurrencies,
      type: 'select',
    },
    dark_theme: {
      name: 'tema scuro',
      value: false,
      icon_loc: '/assets/icons/moon.svg',
      disabled: true,
      type: 'toggle',
    },
    notifications: {
      name: 'notifiche',
      value: false,
      icon_loc: '/assets/icons/notification.svg',
      disabled: true,
      type: 'toggle',
    },
    sharePortfolio: {
      name: 'share',
      value: true,
      icon_loc: '/assets/icons/ic-share.svg',
      disabled: false,
      type: 'toggle',
    },
  };

  constructor(
    public location: Location,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.userService.getUser().subscribe({
      next: (user) => {
        this.settings['currency'].value = user.currency;
        this.settings['share'].value = user.sharePortfolio;
      },
      error: (error) => {
        // console.error('Error getting user', error);
      },
    });
  }

  @ViewChild('language') language!: ElementRef;
  onSelectedLanguage(): void {
    this.settings['language'].value = this.language.nativeElement.value;
  }

  @ViewChild('currency') currency!: ElementRef;
  onSelectedCurrency(): void {
    this.userService
      .updateCurrency({ currency: this.language.nativeElement.value })
      .subscribe({
        next: () => {
          console.log('Currency updated');
          this.settings['currency'].value = this.language.nativeElement.value;
        },
        error: (error) => {
          this.currency.nativeElement.value =
            this.settings['currency']?.value || '';
          console.error('Error updating currency', error);
        },
      });
  }

  onToggle(key: string): void {
    if (this.settings.hasOwnProperty(key)) {
      this.settings[key].value = !this.settings[key].value;
      if (key === 'sharePortfolio') {
        this.userService.updatePrivacy({
          key: this.settings[key].value,
        });
      }
    }
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['']);
      },
      error: (error) => {
        console.error('Error logging out', error);
      },
    });
  }
}
