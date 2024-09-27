import { Component, Input } from '@angular/core';

export interface PortfolioAmount{
  assetName: any,
  currency: string,
  amount: number,
  percentage: number
}

@Component({
  selector: 'app-card-portfolio-valutation',
  standalone: true,
  imports: [],
  templateUrl: './card-portfolio-valutation.component.html',
  styleUrl: './card-portfolio-valutation.component.css'
})
export class CardPortfolioValutationComponent {
 @Input() data?: PortfolioAmount;

 constructor() {
  
 }
}
