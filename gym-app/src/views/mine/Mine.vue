<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { getMe, getSignStatus, logout } from '../../api'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const me = ref(null)
const points = ref(null)

getMe().then((data) => (me.value = data))
// /me 不返回积分，从签到状态接口取
getSignStatus().then((data) => (points.value = data.points))

const cells = [
  { text: '我的订单', icon: 'orders-o', to: '/orders' },
  { text: '消费记录', icon: 'bill-o', to: '/consumptions' },
  { text: '兑换记录', icon: 'gift-card-o', to: '/exchanges' }
]

async function onLogout() {
  try {
    await showConfirmDialog({ title: '提示', message: '确定退出登录吗？' })
  } catch {
    return
  }
  logout().catch(() => {}) // 后端注销 token，失败不阻塞本地退出
  userStore.logout()
  showSuccessToast('已退出登录')
  router.replace('/login')
}
</script>

<template>
  <div class="page has-tabbar mine-page">
    <div class="mine-header">
      <div class="user-row">
        <div class="avatar">{{ me?.realName?.charAt(0) || '会' }}</div>
        <div class="user-info">
          <div class="name">{{ me?.realName || '--' }}</div>
          <div class="phone">{{ me?.phone || '--' }}</div>
        </div>
      </div>

      <!-- 数据概览：剩余天数 / 积分 / 到期日 -->
      <div class="stat-row">
        <div class="stat">
          <span class="num">{{ me?.remainingDays ?? '--' }}</span>
          <span class="label">剩余天数</span>
        </div>
        <div class="stat">
          <span class="num">{{ points ?? '--' }}</span>
          <span class="label">积分</span>
        </div>
        <div class="stat">
          <span class="num date">{{ me?.memberXufei || '--' }}</span>
          <span class="label">到期日</span>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <van-cell-group inset class="cell-group">
      <van-cell
        v-for="c in cells"
        :key="c.to"
        :title="c.text"
        :icon="c.icon"
        is-link
        :to="c.to"
      />
    </van-cell-group>

    <van-cell-group inset class="cell-group">
      <van-cell title="我的私教课" icon="friends-o" is-link to="/my-private" />
      <van-cell title="我的预约" icon="calendar-o" is-link to="/appointment" />
      <van-cell title="编辑资料" icon="edit" is-link to="/mine/edit" />
    </van-cell-group>

    <div class="logout-wrap">
      <van-button block round plain type="danger" @click="onLogout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
.mine-header {
  padding: 28px 16px 20px;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8f5f 100%);
  color: #fff;
}
.user-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}
.name {
  font-size: 18px;
  font-weight: 700;
}
.phone {
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.9;
}
.stat-row {
  margin-top: 18px;
  display: flex;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 12px 0;
}
.stat {
  flex: 1;
  text-align: center;
}
.stat .num {
  display: block;
  font-size: 18px;
  font-weight: 700;
}
.stat .num.date {
  font-size: 14px;
  line-height: 18px;
}
.stat .label {
  font-size: 12px;
  opacity: 0.9;
}
.cell-group {
  margin-top: 12px;
}
.cell-group :deep(.van-icon) {
  color: var(--van-primary-color);
}
.logout-wrap {
  padding: 24px 16px;
}
</style>
