import { Component, Input, OnInit } from '@angular/core';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css'
})
export class LineChartComponent implements OnInit {
  @Input() asset?: any;
  data: any = {};
  duration: string[]= ['1S', '1A', '5A', 'Max'];
  lineChart: any;
  constructor() { }

  ngOnInit(): void {
    this.createLineChart();
  }

  ngAfterViewInit(){
    let selector = document.getElementById("1S");
    selector?.classList.add("selectorSelected");
    selector?.classList.add("rounded");
  }

  createLineChart(): void {
    
    //chiamare l'API (potrebbero essere sia i dati riguardanti l'asset singolo che quelli riguardanti il portfolio totale)
    if(this.asset){
      //dati riguardanti il singolo asset
      this.data = {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [
          {
            label: 'Valore Portfolio',
            data: [100, 120, 130, 110, 150, 160, 140],
          }
        ]
      };
    } else {
      // dati riguardanti l'intero portfolio
      this.data = {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [
          {
            label: 'Valore Portfolio',
            data: [100, 120, 130, 110, 150, 160, 140],
          },
          {
            label: 'Liquidità Inserita',
            data: [50, 50, 50, 50, 50, 50, 50],
          }
        ]
      };
    }
    

    const config:any = {
      type: 'line',
      data: this.data,
      options: {
        scales: {
          x: {
            grid: {
              display: false
            }
          },
          y: {
            grid: {
              display: false
            }
          }
        },
        responsive: true,
        plugins: {
          legend: {
            display: false
          },
          title: {
            display: false
          }
        },
        maintainAspectRatio: false
      },
    };

    const canvas = document.getElementById('lineChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx){
      this.lineChart = new Chart(ctx, config);
    }
    
  }

  updateData(value: string) {
    //gestire cambio di dati richiamando API
    const selectorSelected = document.querySelectorAll(".selectorSelected");
    selectorSelected.forEach(function(selected) {
      selected.classList.remove("selectorSelected");
    });
    
    let selector = document.getElementById(value);
    selector?.classList.add("selectorSelected");
    selector?.classList.add("rounded");
    
    switch(value){
      case "1S":
        //fare chiamata api per prendere i dati rispetto alla scadenza desiderata
        this.lineChart.data.labels = ["asdfsdf", "afsfdsfds", "afsdfsdf", "afsfdsf", "asdfdsfd", "asfsdfsd", "asfsdfd"];
        this.lineChart.data.datasets[0].data = [10, 12, 13, 11, 15, 16, 14];
        this.lineChart.data.datasets[1].data = [5, 5, 10, 5, 5, 5, 5];
        this.lineChart.update();
        break;
      case "1A":
        break;
      case "5A":
        break;
      case "Max":
        break;
    }
  }
}