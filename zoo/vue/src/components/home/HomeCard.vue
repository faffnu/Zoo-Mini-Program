<template>
  <el-card class="box-card">
    <div slot="header" class="header">
      <span><h3>{{title}}</h3></span>
      <span>
        <el-tag size="default" style="font-size: 15px;cursor: pointer;" @click="switchover()">{{ titleUnit }}</el-tag>
      </span>
    </div>
    <hr style="margin-top: 15px;margin-bottom: 15px">

    <div v-if="content===1" class="content">
      <home-chart></home-chart>
    </div>
    <div v-if="content===2" class="content">
      <home-pie></home-pie>
    </div>
    <div v-if="content===3" class="content">
      <home-comment></home-comment>
    </div>

  </el-card>
</template>
<script>

import router from "@/router/index.js";
import HomePie from "@/components/home/HomePie.vue";
import HomeChart from "@/components/home/HomeChart.vue";
import HomeComment from "@/components/home/HomeComment.vue";

export default {
  components: {HomeComment, HomeChart, HomePie},
  props: [
    "title",
    "titleUnit",
    "content",
    "contentUnit",
    "bottomTitle",
    "bottomContent",
    "bottomIcon"
  ],
  methods:{

    switchover(){
      if(this.titleUnit==='订单管理'){
        router.push("/manager/order");
      }else if (this.titleUnit==='门票管理'){
        router.push("/manager/ticket");
      } else if (this.titleUnit==='评论管理'){
        router.push("/manager/comment");
      }
    },

  }
};
</script>
<style lang="scss" scoped>
.box-card {
  margin-right: 10px;

  .header {
    height: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .content {
    font-size: 30px;
    font-weight: bold;
    color: #666;
    text-align: center;
    height: 300px;



    .unit {
      font-size: 16px;
    }
  }

  .bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
  }
}


</style>
