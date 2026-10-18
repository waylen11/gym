<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCardInfo, getMe } from '../../api'

const router = useRouter()
const card = ref(null)
const me = ref(null)

// status：1 正常 / 0 过期（后端按剩余天数自动设置）
const statusText = { 1: '正常', 0: '已过期' }

getCardInfo().then((data) => (card.value = data))
getMe().then((data) => (me.value = data))

const entries = [
  { text: '我的预约', icon: 'calendar-o', to: '/appointment' },
  { text: '公告通知', icon: 'volume-o', to: '/notices' },
  { text: '失物招领', icon: 'search', to: '/losts' },
  { text: '我的私教', icon: 'friends-o', to: '/my-private' }
]
</script>

<template>
  <div class="page has-tabbar home-page">
    <div class="home-header">
      <div class="hi">Hi，{{ me?.realName || '健身达人' }}</div>
      <div class="sub">今天也要坚持锻炼哦</div>
    </div>

    <!-- 会员卡概览 -->
    <div class="card-panel" @click="router.push('/card')">
      <div class="card-top">
        <span class="card-name"><van-icon name="card" /> 会员卡</span>
        <van-tag v-if="card" :type="card.status === 1 ? 'success' : 'danger'">
          {{ statusText[card.status] || '未知' }}
        </van-tag>
      </div>
      <div class="card-days">
        <span class="num">{{ card?.remainingDays ?? '--' }}</span>
        <span class="unit">天</span>
        <span class="label">剩余天数</span>
      </div>
      <div class="card-expire">到期日：{{ card?.memberXufei || '--' }}</div>
      <div class="card-arrow"><van-icon name="arrow" /></div>
    </div>

    <!-- 醒目按钮 -->
    <div class="home-btns">
      <van-button type="primary" size="large" round icon="edit" @click="router.push('/sign')">
        每日签到
      </van-button>
      <van-button plain type="primary" size="large" round icon="card" @click="router.push('/card')">
        我的卡
      </van-button>
    </div>

    <!-- 常用入口 -->
    <van-grid :column-num="4" :border="false" class="entry-grid">
      <van-grid-item v-for="e in entries" :key="e.to" :text="e.text" @click="router.push(e.to)">
        <template #icon><van-icon :name="e.icon" /></template>
      </van-grid-item>
    </van-grid>
  </div>
</template>

<style scoped>
.home-header {
  padding: 24px 16px 8px;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8f5f 100%);
  color: #fff;
}
.hi {
  font-size: 20px;
  font-weight: 600;
}
.sub {
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.9;
}
.card-panel {
  position: relative;
  margin: 16px;
  padding: 18px;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, #ff6b35 0%, #ff9a6c 100%);
  box-shadow: 0 8px 20px rgba(255, 107, 53, 0.35);
  cursor: pointer;
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-name {
  font-size: 14px;
  font-weight: 600;
}
.card-days {
  margin-top: 14px;
}
.card-days .num {
  font-size: 40px;
  font-weight: 700;
  line-height: 1;
}
.card-days .unit {
  margin-left: 4px;
  font-size: 14px;
}
.card-days .label {
  margin-left: 12px;
  font-size: 13px;
  opacity: 0.9;
}
.card-expire {
  margin-top: 12px;
  font-size: 13px;
  opacity: 0.9;
}
.card-arrow {
  position: absolute;
  right: 16px;
  bottom: 16px;
  opacity: 0.9;
}
.home-btns {
  display: flex;
  gap: 12px;
  padding: 0 16px;
}
.home-btns .van-button {
  flex: 1;
}
.entry-grid {
  margin-top: 8px;
}
.entry-grid :deep(.van-grid-item__icon) {
  color: var(--van-primary-color);
  font-size: 26px;
}
</style>
