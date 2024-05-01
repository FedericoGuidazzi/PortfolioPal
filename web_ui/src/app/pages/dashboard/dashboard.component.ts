import { Component } from '@angular/core';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';
import { HistoryComponent } from '../../components/history/history.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LineChartComponent, HistoryComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
