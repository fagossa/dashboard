import Vue from "vue";
import { Line } from 'vue-chartjs'

export default Vue.component('line-chart', {
  extends: Line,
  props: ['data', 'options'],
  mounted () {
    this.renderLineChart();
  },
  computed: {
    chartData: function() {
      return this.data;
    }
  },
  watch: {
    data: function () {
      this.renderLineChart();
    }
  },
  methods: {
    renderLineChart: function() {
      this.renderChart(this.chartData, this.options);
    }
  }
});
