import Vue from 'vue';
import CommitChart from './CommitChart'

export const Board = Vue.component('board', {
  data: () => ({
    stockData: {
      labels: [],
      datasets: []
    },
    chartOptions: {
      responsive: true,
      maintainAspectRatio: false,
      title: {
        display: true,
        text: 'Current board'
      },
      tooltips: {
        mode: 'index',
        intersect: false,
      },
      hover: {
        mode: 'nearest',
        intersect: true
      },
      scales: {
        xAxes: [{
          display: true,
          scaleLabel: {
            display: true,
            labelString: 'Month'
          }
        }],
        yAxes: [{
          display: true,
          scaleLabel: {
            display: true,
            labelString: 'Value'
          }
        }]
      }
    }
  }),
  template: `\
    <div class="board-scene">\
      Loading events
      <line-chart
        :chartData="stockData"
        :options="chartOptions"
        :width="800"
        :height="400"
        >
      </line-chart>
    </div>\
  `,
  created: function() {
    console.log("Receiving events...");
    this.setupStream();
  },
  methods: {
    setupStream: function() {
      let es = new EventSource('http://localhost:8080/board/events');

      let buildScore = function(s) {
        return {
          label: s.name,
          backgroundColor: s.background,
          borderColor: s.background,
          data: s.billed.map(m => m.value),
          fill: false
        }
      };

      es.addEventListener('message', event => {
        if (this.isJsonString(event.data)) {
          let data = JSON.parse(event.data);
          this.stockData = {
            labels: data.labels.map(label => label.name),
            datasets: data.scores.map(score => buildScore(score))
          };
        }
      }, false);

      es.addEventListener('error', event => {
        if (event.readyState == EventSource.CLOSED) {
          console.log('Event was closed');
        }
      }, false);
    },
    isJsonString: function(str) {
      try {
          JSON.parse(str);
      } catch (e) {
          return false;
      }
      return true;
    }
  }
});
