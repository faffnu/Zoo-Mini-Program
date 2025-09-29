<template>
  <div>
    <div class="card" style="height: 50px; font-size: 18px;">欢迎进入动物园管理系统</div>
  </div>

  <div style="margin-top: 10px">
    <el-row :gutter="10">
      <el-col :span="12">
          <home-card
              :title="title1"
              :titleUnit="titleUnit1"
              :content="1"
              style="width: 100%;height: 400px">
          </home-card>
      </el-col>

      <el-col :span="12">
        <home-card
            :title="title2"
            :titleUnit="titleUnit2"
            :content="2"
            style="width: 100%;height: 400px">
        </home-card>
      </el-col>
    </el-row>
    <el-row :gutter="10" style="margin-top: 10px;margin-left: auto">
        <home-card
            :title="title3"
            :titleUnit="titleUnit3"
            :content="3"
            style="width: 100%;height: 600px">
        </home-card>
    </el-row>
  </div>
</template>

<script setup>

  import {reactive} from "vue";
  import HomeCard from "@/components/home/HomeCard.vue";

  const title1 = '月度销售统计';
  const title2 = '门票销售统计';
  const title3 = '热门评论展示';
  const titleUnit1 = '订单管理';
  const titleUnit2 = '门票管理';
  const titleUnit3 = '评论管理';






</script>

