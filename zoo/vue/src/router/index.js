import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login'},
    {
      path: '/manager',
      name: 'Manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/manager/login',
      children: [
        { path: 'password', meta: { name: '修改密码' }, component: () => import('@/views/manager/Password.vue')},
        { path: 'home', meta: { name: '系统首页' }, component: () => import('@/views/manager/Home.vue')},
		{ path: 'admin', meta: { name: '管理员' }, component: () => import("@/views/manager/Admin.vue") },
		{ path: 'user', meta: { name: '用户' }, component: () => import("@/views/manager/User.vue") },
		{ path: 'type', meta: { name: '动物分类' }, component: () => import("@/views/manager/Type.vue") },
		{ path: 'animal', meta: { name: '动物信息' }, component: () => import("@/views/manager/Animal.vue") },
		{ path: 'activity', meta: { name: '活动' }, component: () => import("@/views/manager/Activity.vue") },
		{ path: 'ticket', meta: { name: '门票' }, component: () => import("@/views/manager/Ticket.vue") },
        { path: 'voucher', meta: { name: '优惠券' }, component: () => import("@/views/manager/Voucher.vue") },
		{ path: 'order', meta: { name: '订单' }, component: () => import("@/views/manager/Order.vue") },
		{ path: 'favor', meta: { name: '用户收藏' }, component: () => import("@/views/manager/Favor.vue") },
		{ path: 'look', meta: { name: '用户浏览' }, component: () => import("@/views/manager/Look.vue") },
		{ path: 'comment', meta: { name: '评论' }, component: () => import("@/views/manager/Comment.vue") },
		{ path: 'commentApprove', meta: { name: '评论点赞' }, component: () => import("@/views/manager/CommentApprove.vue") },
		{ path: 'pAdmin', meta: { name: '个人信息' }, component: () => import("@/views/manager/pAdmin.vue")  },

      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue')},

    { path: '/404', component: () => import('@/views/404.vue')},
    { path: '/:pathMatch(.*)', redirect: '/404', hidden: true }
  ]
})

export default router
