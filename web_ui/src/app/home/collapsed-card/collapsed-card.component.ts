import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-collapsed-card',
  standalone: true,
  imports: [NgbCollapseModule, CommonModule],
  templateUrl: './collapsed-card.component.html',
  styleUrl: './collapsed-card.component.css',
})
export class CollapsedCardComponent {
  isCollapsed: boolean = true;
  isActive: boolean = !this.isCollapsed;
  @Input() index: number | undefined;
  @Input() question: string | undefined;
  @Input() answer: string | undefined;

  rotated: boolean = false;

  toggleAndRotate() {
    this.isCollapsed = !this.isCollapsed;
    this.isActive = !this.isActive;
    this.rotated = !this.rotated;
  }
}
