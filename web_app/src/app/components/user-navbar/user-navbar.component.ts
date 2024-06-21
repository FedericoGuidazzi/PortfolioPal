import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { RouterLink } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-user-navbar',
  standalone: true,
  imports: [MatSlideToggleModule, RouterLink],
  templateUrl: './user-navbar.component.html',
  styleUrl: './user-navbar.component.css',
})
export class UserNavbarComponent {
  settings = [
    {
      name: 'Lingua',
      icon_loc: '/assets/icons/ic-language.svg',
      value: 'Spanish',
      type: 'select',
    },
    {
      name: 'Tema scuro',
      icon_loc: '/assets/icons/moon.svg',
      value: true,
      type: 'toggle',
    },
    {
      name: 'Notifiche',
      icon_loc: '/assets/icons/notification.svg',
      value: true,
      type: 'toggle',
    },
    {
      name: 'Share',
      icon_loc: '/assets/icons/ic-share.svg',
      value: true,
      type: 'toggle',
    },
  ];

  supportedLanguages: string[] = ['Italiano', 'English', 'Spanish', 'French'];

  constructor(public location: Location) {}

  @ViewChild('language') language!: ElementRef;
  onSelected(): void {
    this.settings[0].value = this.language.nativeElement.value;
  }

  onToggle(index: number): void {
    this.settings[index].value = !this.settings[index].value;
  }
}
