<template>
  <div class="manager-container">
    <div class="manager-header">
      <div class="manager-header-left">
        <img style="border-radius: 50%" src="@/assets/imgs/logo.png" alt="">
        <div class="title">动物园管理系统</div>
      </div>
      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item to="/manager/home">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="manager-header-right">
        <el-dropdown style="cursor: pointer;">
          <div style="padding-right: 20px; display: flex; align-items: center;">
            <img v-if="data.user.avatar" :src="data.user?.avatar" alt="" style="width: 40px; height: 40px; display: block; border-radius: 50%">
            <img v-else src="@/assets/imgs/avatar.png" alt="" style="width: 40px; height: 40px; display: block; border-radius: 50%">
            <span style="margin-left: 5px">{{ data.user?.name }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click.native="goToPerson">个人资料</el-dropdown-item>
              <el-dropdown-item @click.native="router.push('/manager/password')">修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div style="display: flex">
      <div class="manager-main-left">
        <el-menu
            :default-active="router.currentRoute.value.path"
            :default-openeds="['info', 'user', 'sys']"
            router
        >
          <el-menu-item index="/manager/home">
            <el-icon><home-filled /></el-icon><span>系统首页</span>
          </el-menu-item>
          <el-sub-menu index="user" >
            <template #title><el-icon><UserFilled /></el-icon><span>用户信息</span></template>
<!--            <el-menu-item index="/manager/admin">管理员</el-menu-item>-->
            <el-menu-item index="/manager/user">用户</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="info">
            <template #title><el-icon><Menu /></el-icon><span>信息管理</span></template>
							<el-menu-item index="/manager/type">动物分类</el-menu-item>
							<el-menu-item index="/manager/animal">动物信息</el-menu-item>
							<el-menu-item index="/manager/activity">活动</el-menu-item>
							<el-menu-item index="/manager/ticket">门票</el-menu-item>
							<el-menu-item index="/manager/order">订单</el-menu-item>
            <el-menu-item index="/manager/voucher">优惠券</el-menu-item>
<!--							<el-menu-item index="/manager/favor">用户收藏</el-menu-item>
							<el-menu-item index="/manager/look">用户浏览</el-menu-item>-->
							<el-menu-item index="/manager/comment">评论</el-menu-item>
<!--							<el-menu-item index="/manager/commentApprove">评论点赞</el-menu-item>-->
          </el-sub-menu>

          <el-sub-menu index="sys">
            <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
            <el-menu-item index="/manager/password">修改密码</el-menu-item>
            <el-menu-item @click.native="logout">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <div class="manager-main-right">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import {HomeFilled, UserFilled} from "@element-plus/icons-vue";
import {reactive} from "vue";
import router from "@/router";
import {ElMessage, ElMessageBox} from "element-plus";
import request from "@/utils/request.js";

const data = reactive({
  token:  JSON.parse(localStorage.getItem('xm-token') || '{}'),
  user: {}
})

// 加载用户数据
const load = () => {
  request.get( 'admin/me', ).then(res => {
    data.user = res.data
  })
}

const goToPerson = () => {
		router.push("/manager/pAdmin")
}
const logout = () => {
  ElMessageBox.confirm('您确定退出吗?', '确认', { type: 'warning' }).then(res => {
    ElMessage.success('退出成功')
    localStorage.removeItem('xm-token')
    request.post('/logout').then(res => {
      if (res.code === '200') {
        router.push('/login')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })

}

const updateUser = () => {
  //data.token= JSON.parse(localStorage.getItem('xm-token') || '{}');  // 重新获取下用户的token
  load();
}
load()
</script>

<style scoped>
@import '@/assets/css/manager.css';
</style>
