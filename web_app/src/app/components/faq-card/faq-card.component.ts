import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-faq-card',
  standalone: true,
  imports: [NgbCollapseModule, CommonModule],
  templateUrl: './faq-card.component.html',
  styleUrl: './faq-card.component.css',
})
export class FaqCardComponent {
  isCollapsed: boolean = true;
  @Input() index: number = 0;
  @Input() question: string = 'Placeholder question';
  @Input() answer: string =
    'Lorem ipsum dolor sit amet consectetur adipisicing elit. Quis eos maxime ad cum cumque quibusdam. Recusandae dolor odit facere iste optio deserunt, eveniet natus tempore doloribus, eligendi, unde commodi? Dignissimos! Ex numquam nemo repellendus voluptas ea optio id in? Repellat cum hic id qui magni labore necessitatibus quos cumque dignissimos, explicabo et quae dolorum. Nobis accusamus harum asperiores aliquam delectus!';

  rotated: boolean = false;

  toggleAndRotate() {
    this.isCollapsed = !this.isCollapsed;
    this.rotated = !this.rotated;
  }
}
