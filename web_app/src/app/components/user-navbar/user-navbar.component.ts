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
  updated: boolean;
  user_setting: boolean | string;
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
      updated: false,
      user_setting: 'Italiano',
    },
    currency: {
      name: 'valuta',
      value: 'USD',
      icon_loc: '/assets/icons/ic-currency-exchange.svg',
      disabled: true,
      supportedField: this.supportedCurrencies,
      type: 'select',
      updated: false,
      user_setting: 'USD',
    },
    dark_theme: {
      name: 'tema scuro',
      value: false,
      icon_loc: '/assets/icons/moon.svg',
      disabled: true,
      type: 'toggle',
      updated: false,
      user_setting: false,
    },
    notifications: {
      name: 'notifiche',
      value: false,
      icon_loc: '/assets/icons/notification.svg',
      disabled: true,
      type: 'toggle',
      updated: false,
      user_setting: false,
    },
    sharePortfolio: {
      name: 'share',
      value: false,
      icon_loc: '/assets/icons/ic-share.svg',
      disabled: false,
      type: 'toggle',
      updated: false,
      user_setting: false,
    },
  };

  constructor(
    public location: Location,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  async ngOnInit() {
    await this.userService.getUser().subscribe({
      next: (user) => {
        this.settings['currency'].value = user.favouriteCurrency;
        this.settings['sharePortfolio'].value = user.sharePortfolio;

        this.settings['currency'].user_setting = user.favouriteCurrency;
        this.settings['sharePortfolio'].user_setting = user.sharePortfolio;
      },
      error: (error) => {
        // console.error('Error getting user', error);
      },
    });
  }

  ngAfterViewInit() {
    this.currency.nativeElement.value = this.settings['currency'].value;
    const share = document.getElementById('sharePortfolio');
    share?.setAttribute(
      'checked',
      this.settings['sharePortfolio'].value.toString()
    );
  }

  @ViewChild('language') language!: ElementRef;
  @ViewChild('currency') currency!: ElementRef;

  onSelectedLanguage(): void {
    this.settings['language'].value = this.language.nativeElement.value;
    this.settings['language'].updated =
      this.settings['language'].value !==
      this.settings['language'].user_setting;

    this.showSaveButton();
  }

  onSelectedCurrency(): void {
    this.settings['currency'].value = this.currency.nativeElement.value;
    this.settings['currency'].updated =
      this.settings['currency'].value !==
      this.settings['currency'].user_setting;

    this.showSaveButton();
  }

  onToggle(key: string): void {
    this.settings[key].value = !this.settings[key].value;
    this.settings[key].updated =
      this.settings[key].value !== this.settings[key].user_setting;

    this.showSaveButton();
  }

  showSaveButton(): void {
    let isChanged: boolean = false;
    for (const key in this.settings) {
      if (this.settings[key].updated) {
        isChanged = true;
      }
    }
    if (isChanged) {
      document
        .getElementById('modify_preferencies')
        ?.classList.remove('d-none');
    } else {
      document.getElementById('modify_preferencies')?.classList.add('d-none');
    }
  }

  onSave() {
    if (this.settings['sharePortfolio'].updated) {
      this.userService
        .updatePrivacy(this.settings['sharePortfolio'].value as boolean)
        .subscribe({
          next: () => {
            this.settings['sharePortfolio'].user_setting =
              this.settings['sharePortfolio'].value;
            this.settings['sharePortfolio'].updated = false;
          },
          error: (error) => {
            console.error('Error updating privacy', error);
          },
        });
    }

    for (const key in this.settings) {
      if (this.settings[key].updated) {
        this.settings[key].updated = false;
      }
    }
    document.getElementById('modify_preferencies')?.classList.add('d-none');
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
