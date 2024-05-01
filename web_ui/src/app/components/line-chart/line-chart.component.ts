import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css'
})
export class LineChartComponent implements OnInit {

  data: any = {};
  duration: string[]= ['1D', '1S', '1A', 'Max'];
  constructor() { }

  ngOnInit(): void {
    this.createLineChart();
  }

  createLineChart(): void {
    const data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [{
        label: 'My Portfolio',
        data: [100, 120, 130, 110, 150, 160, 140],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
      }]
    };

    const config:any = {
      type: 'line',
      data: data,
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'top',
          },
          title: {
            display: true,
            text: 'Chart.js Line Chart'
          }
        }
      },
    };

    const canvas = document.getElementById('lineChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx){
      new Chart(ctx, config);
    }
    
  }

  updateData(value: string) {
    //gestire cambio di dati richiamando API

    switch(value){
      case "1D":
        break;
      case "1S":
        break;
      case "1A":
        break;
      case "Max":
        break;
    }
    
    this.data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [{
        label: 'My Portfolio',
        data: [100, 120, 130, 110, 150, 160, 140],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
      }]
    };
  }
}