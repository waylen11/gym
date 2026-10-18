import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/login/Login.vue'), meta: { title: '登录' } },
  { path: '/', name: 'home', component: () => import('../views/home/Home.vue'), meta: { title: '首页' } },
  { path: '/card', name: 'card', component: () => import('../views/card/Card.vue'), meta: { title: '我的会员卡' } },
  { path: '/card/buy', name: 'cardBuy', component: () => import('../views/card/CardBuy.vue'), meta: { title: '购买/续费会员卡' } },
  { path: '/goods', name: 'goods', component: () => import('../views/goods/Goods.vue'), meta: { title: '积分商城' } },
  { path: '/exchanges', name: 'exchanges', component: () => import('../views/goods/ExchangeRecord.vue'), meta: { title: '我的兑换记录' } },
  { path: '/sign', name: 'sign', component: () => import('../views/sign/Sign.vue'), meta: { title: '每日签到' } },
  { path: '/courses', name: 'courses', component: () => import('../views/course/Courses.vue'), meta: { title: '课程列表' } },
  { path: '/coach', name: 'coach', component: () => import('../views/course/CoachList.vue'), meta: { title: '教练列表' } },
  { path: '/coach/:id', name: 'coachDetail', component: () => import('../views/course/CoachDetail.vue'), meta: { title: '教练详情' } },
  { path: '/my-private', name: 'myPrivate', component: () => import('../views/course/MyPrivate.vue'), meta: { title: '我的私教课' } },
  { path: '/appointment', name: 'appointment', component: () => import('../views/appointment/Appointment.vue'), meta: { title: '我的预约' } },
  { path: '/notices', name: 'notices', component: () => import('../views/notice/Notices.vue'), meta: { title: '公告通知' } },
  { path: '/losts', name: 'losts', component: () => import('../views/lost/LostList.vue'), meta: { title: '失物招领' } },
  { path: '/mine', name: 'mine', component: () => import('../views/mine/Mine.vue'), meta: { title: '我的' } },
  { path: '/mine/edit', name: 'profileEdit', component: () => import('../views/mine/ProfileEdit.vue'), meta: { title: '编辑资料' } },
  { path: '/orders', name: 'orders', component: () => import('../views/mine/Orders.vue'), meta: { title: '我的订单' } },
  { path: '/consumptions', name: 'consumptions', component: () => import('../views/mine/Consumptions.vue'), meta: { title: '消费记录' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 登录守卫：除 /login 外，无 token 一律跳登录
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return { path: '/login', replace: true }
  if (to.path === '/login' && token) return { path: '/', replace: true }
  document.title = to.meta?.title ? `${to.meta.title} · 活力健身` : '活力健身会员端'
  return true
})

export default router
