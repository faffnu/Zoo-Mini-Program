<template>
  <div id="home-pie" style="width:100%;height:300px;"></div>
</template>

<script>
import request from "@/utils/request.js";
import * as echarts from 'echarts';
export default {
  name: "HomePie",
  async mounted() {
    await this.getPie();
    this.homePie();
  },
  data(){
    return{
      dataList: [],
    }
  },
  methods: {
    async getPie(){
      await request.get('order/getPie').then(res=>{
        this.dataList=res.data?.pieChartData;
      })

    },
    homePie() {
      // 基于准备好的dom，初始化echarts实例
      var myPie = echarts.init(document.getElementById("home-pie"));
      var data = this.dataList;
      // 指定图表的配置项和数据
      var option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '',
            type: 'pie',
            center:['50%','50%'],
            radius: '80%',
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      // // 使用刚指定的配置项和数据显示图表。
      myPie.setOption(option);
      //根据窗口的大小变动图表
      window.onresize = function () {
        myPie.resize();
      };


    }
  }
}
</script>

<style scoped>
#home-pie {

  background: #ffffff;
  margin-bottom: 10px;

}
</style>