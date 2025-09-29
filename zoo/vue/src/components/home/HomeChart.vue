<template>
  <div id="home-chart" style="width:100%;height:300px;"></div>
</template>
<script>
import * as echarts from 'echarts';
import request from "@/utils/request.js";
export default {
  name: "HomeChart",
  async mounted() {
    await this.getChart();
    this.homeChart();
  },
  data(){
    return{
      dataList: [],
    }

  },

  methods: {
    async getChart(){
      await request.get('order/getTicketStats').then(res=>{
        this.dataList=res.data;
      })
    },
    homeChart() {
      // 基于准备好的dom，初始化echarts实例
      var myChart = echarts.init(document.getElementById("home-chart"));

      var stats = this.dataList;

      // 指定图表的配置项和数据
      var option = {
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: ["销售金额", "售票量"]
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        xAxis: {
          data: stats.map(item => item.month)
        },
        yAxis: [
          {
            name:'销售金额(元)',
            type:'value',

          },
          {
            name:'售票量(张)',
            type:'value',
            axisLabel:{
              formatter:'{value}'
            }

          }
        ],
        series: [
          {
            name: "销售金额",
            type: "line",
            smooth:true,
            itemStyle:{
              color:'#EE4000',
            },
            data: stats.map(item => item.totalAmount),

          },
          {
            name: "售票量",
            type: "bar",
            yAxisIndex:1,
            barWidth:30,
            itemStyle:{
              color:'#B0E2FF',
              barBorderRadius:[5,5,0,0]
            },
            emphasis:{
              focus:'series'
            },

            data: stats.map(item => item.count)
          },

        ]
      };
      // // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
      //根据窗口的大小变动图表
      window.onresize = function () {
        myChart.resize();
      };
    }
  }
};
</script>
<style lang="scss" scoped>
#home-chart {

  background: #ffffff;
  margin-bottom: 10px;

}
</style>
