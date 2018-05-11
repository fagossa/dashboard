import Vue from "vue";
import { Line, mixins } from 'vue-chartjs'

export default Vue.component('line-chart', {
  extends: Line,
  mixins: [mixins.reactiveProp],
  props: ['chartData', 'options'],
  mounted () {
    this.renderLineChart();
  },
  methods: {
    renderLineChart: function() {
      this.renderChart(this.chartData, this.options);
    }
  }
});
