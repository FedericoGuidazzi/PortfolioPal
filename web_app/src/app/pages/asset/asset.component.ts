import { Component, Input } from '@angular/core';
import {
  CardPortfolioValutationComponent,
  PortfolioAmount,
} from '../../components/card-portfolio-valutation/card-portfolio-valutation.component';
import {
  HistoryComponent,
  TransactionData,
} from '../../components/history/history.component';
import Chart from 'chart.js/auto';
import { ActivatedRoute } from '@angular/router';
import { LineChartComponent } from '../../components/line-chart/line-chart.component';

export interface AssetData {
  name: string;
  currentValue: number;
  valueInPortfolio: number;
  sharesNumber: number;
  description: string;
  percetageInPortfolio: number;
  percentageWinLose: number;
  currency: string;
  totalCost: number;
  currentValuation: number;
  balance: number;
  averageCostPerShare: number;
}

@Component({
  selector: 'app-asset',
  standalone: true,
  imports: [
    /*LineChartComponent,*/ HistoryComponent,
    CardPortfolioValutationComponent,
  ],
  templateUrl: './asset.component.html',
  styleUrl: './asset.component.css',
})
export class AssetComponent {
  @Input() assetName?: any;
  data?: PortfolioAmount;
  transactions: TransactionData[] = [];
  assetData?: AssetData;
  dataAsset: any = {};
  duration: string[] = ['1A', '5A', 'Max'];
  lineChart: any;

  constructor(private route: ActivatedRoute) {}

  prepareData() {
    this.assetData = {
      name: 'name',
      currentValue: 3,
      valueInPortfolio: 3,
      sharesNumber: 3,
      description:
        'orem Ipsum è un testo segnaposto utilizzato nel settore della tipografia e della stampa. Lorem Ipsum è considerato il testo segnaposto standard sin dal sedicesimo secolo, quando un anonimo tipografo prese una cassetta di caratteri e li assemblò per preparare un testo campione. È sopravvissuto non solo a più di cinque secoli, ma anche al passaggio alla videoimpaginazione, pervenendoci sostanzialmente inalterato. Fu reso popolare, negli anni ’60, con la diffusione dei fogli di caratteri trasferibili “Letraset”, che contenevano passaggi del Lorem Ipsum, e più recentemente da software di impaginazione come Aldus PageMaker, che includeva versioni del Lorem Ipsum.',
      percetageInPortfolio: 3,
      percentageWinLose: 3,
      currency: '$',
      totalCost: 3,
      currentValuation: 3,
      balance: 3,
      averageCostPerShare: 3,
    };

    this.transactions = [
      {
        id: 1,
        date: 'wjebf',
        type: 'Acquisto',
        symbol: 'AAPL',
        quantity: 10,
        price: 150,
        currency: '$',
      },
      {
        id: 2,
        date: 'jbnwefkjn',
        type: 'Vendita',
        symbol: 'GOOGL',
        quantity: 5,
        price: 250,
        currency: '$',
      },
      {
        id: 3,
        date: 'wjebf',
        type: 'Acquisto',
        symbol: 'AAPL',
        quantity: 10,
        price: 150,
        currency: '$',
      },
      {
        id: 4,
        date: 'jbnwefkjn',
        type: 'Vendita',
        symbol: 'GOOGL',
        quantity: 5,
        price: 250,
        currency: '$',
      },
      {
        id: 5,
        date: 'wjebf',
        type: 'Acquisto',
        symbol: 'AAPL',
        quantity: 10,
        price: 150,
        currency: '$',
      },
      {
        id: 6,
        date: 'jbnwefkjn',
        type: 'Vendita',
        symbol: 'GOOGL',
        quantity: 5,
        price: 250,
        currency: '$',
      },
      {
        id: 7,
        date: 'wjebf',
        type: 'Acquisto',
        symbol: 'AAPL',
        quantity: 10,
        price: 150,
        currency: '$',
      },
      {
        id: 8,
        date: 'jbnwefkjn',
        type: 'Vendita',
        symbol: 'GOOGL',
        quantity: 5,
        price: 250,
        currency: '$',
      },
    ];

    this.data = {
      assetName: this.assetName,
      currency: this.assetData?.currency,
      amount: this.assetData?.currentValue,
      percentage: this.assetData?.percentageWinLose,
    };
  }

  createDoughnutChart(): void {
    //call API to get data
    const data = {
      labels: ['Red', 'Blue', 'Yellow'],
      datasets: [
        {
          label: 'My First Dataset',
          data: [300, 50, 100],
          backgroundColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 205, 86)',
          ],
          hoverOffset: 4,
        },
      ],
    };

    const config: any = {
      type: 'doughnut',
      data: data,
      options: {
        responsive: true,
        plugins: {
          legend: {
            display: false,
          },
        },
        maintainAspectRatio: false,
      },
    };

    const canvas = document.getElementById(
      'doughnutChartAsset'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      new Chart(ctx, config);
    }
  }

  ngOnDestroy() {
    this.lineChart.destroy();
  }

  ngOnInit() {
    this.route.paramMap.subscribe(
      (params) => (this.assetName = params.get('assetName'))
    );
    this.prepareData();
  }

  ngAfterViewInit() {
    this.createLineChart();
    this.createDoughnutChart();
  }

  createLineChart(): void {
    this.dataAsset = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'Valore Portfolio',
          data: [100, 120, 130, 110, 150, 160, 140],
        },
      ],
    };

    const config: any = {
      type: 'line',
      data: this.dataAsset,
      options: {
        scales: {
          x: {
            grid: {
              display: false,
            },
          },
          y: {
            grid: {
              display: false,
            },
          },
        },
        responsive: true,
        plugins: {
          legend: {
            display: false,
          },
          title: {
            display: false,
          },
        },
        maintainAspectRatio: false,
      },
    };

    const canvas = document.getElementById(
      'lineChartAsset'
    ) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      this.lineChart = new Chart(ctx, config);
    }
  }

  updateData(value: string) {
    //gestire cambio di dati richiamando API
    const selectorSelected = document.querySelectorAll('.selectorSelected');
    selectorSelected.forEach(function (selected) {
      selected.classList.remove('selectorSelected');
    });

    let selector = document.getElementById(value);
    selector?.classList.add('selectorSelected');

    switch (value) {
      case '1S':
        //fare chiamata api per prendere i dati rispetto alla scadenza desiderata
        this.lineChart.data.labels = [
          'asdfsdf',
          'afsfdsfds',
          'afsdfsdf',
          'afsfdsf',
          'asdfdsfd',
          'asfsdfsd',
          'asfsdfd',
        ];
        this.lineChart.data.datasets[0].data = [10, 12, 13, 11, 15, 16, 14];
        this.lineChart.update();
        break;
      case '1A':
        break;
      case '5A':
        break;
      case 'Max':
        break;
    }
  }
}
