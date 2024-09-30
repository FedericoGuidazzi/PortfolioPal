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
        this.settings['sharePortfolio'].value = user.sharePortfolio;
        this.settings['sharePortfolio'].user_setting = user.sharePortfolio;
      },
      error: (error) => {
        console.error('Error getting user', error);
      },
    });
  }

  ngAfterViewInit() {
    const share = document.getElementById('sharePortfolio');
    share?.setAttribute(
      'checked',
      this.settings['sharePortfolio'].value.toString()
    );
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
