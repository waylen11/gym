<script setup>
import { ref } from 'vue'
import { showToast, showSuccessToast } from 'vant'
import { getSignStatus, doSign, getSignRank } from '../../api'
import { getLocation } from '../../utils/map'

const status = ref(null)
const rank = ref({})
const signing = ref(false)

async function loadAll() {
  status.value = await getSignStatus()
  rank.value = await getSignRank()
}
loadAll()

// 签到：先定位拿经纬度，再调签到接口
// 接口报错由拦截器统一弹出（如"不在健身房附近"），这里只处理定位失败
async function onSign() {
  if (signing.value) return
  signing.value = true
  try {
    const loc = await getLocation()
    const res = await doSign(loc.lat, loc.lng)
    showSuccessToast(res.message || '签到成功')
    await loadAll()
  } catch (e) {
    if (!e.request) showToast(e.message || '定位失败，请开启定位权限')
  } finally {
    signing.value = false
  }
}

// 排行前三名徽章颜色
const topColors = { 1: '#ffb800', 2: '#c0c4cc', 3: '#e08a4e' }
</script>

<template>
  <div class="page has-tabbar sign-page">
    <div class="sign-header">
      <div class="title">每日签到</div>
      <div class="sub">坚持签到，积分好礼拿不停</div>
    </div>

    <!-- 签到状态卡片 -->
    <div class="card-box status-card">
      <div class="days-row">
        <div class="days">
          <span class="num">{{ status?.continuousDays ?? '--' }}</span>
          <span class="label">连续签到（天）</span>
        </div>
        <div class="points">
          <span class="num">{{ status?.points ?? '--' }}</span>
          <span class="label">当前积分</span>
        </div>
      </div>
      <div class="month-row">
        本月已签到 {{ status?.monthCount ?? 0 }} 次
      </div>
      <div v-if="status && !status.signed && status.nextRewardRemain > 0" class="reward-tip">
        再签 {{ status.nextRewardRemain }} 天可领周签到奖励（+50积分）
      </div>

      <van-button
        type="primary"
        round
        block
        size="large"
        class="sign-btn"
        :disabled="status?.signed"
        :loading="signing"
        loading-text="签到中..."
        @click="onSign"
      >
        {{ status?.signed ? '今日已签到' : '签 到' }}
      </van-button>
    </div>

    <!-- 签到排行榜 -->
    <div class="rank-title">签到排行榜（本月 Top10）</div>
    <div class="card-box rank-card">
      <van-empty
        v-if="!rank.rankList || rank.rankList.length === 0"
        image="search"
        description="本月暂无签到记录"
      />
      <div v-for="r in rank.rankList || []" :key="r.memberId" class="rank-item">
        <span class="rank-no" :style="topColors[r.rank] ? { color: topColors[r.rank] } : {}">
          {{ r.rank <= 3 ? '🏆' : '' }} {{ r.rank }}
        </span>
        <span class="rank-name">{{ r.realName }}</span>
        <span class="rank-count">{{ r.count }} 天</span>
      </div>
      <!-- 我的名次 -->
      <div v-if="rank.myRank" class="rank-item my-rank">
        <span class="rank-no">{{ rank.myRank.rank }}</span>
        <span class="rank-name">{{ rank.myRank.realName }}（我）</span>
        <span class="rank-count">{{ rank.myRank.count }} 天</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sign-header {
  padding: 24px 16px 8px;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8f5f 100%);
  color: #fff;
}
.title {
  font-size: 20px;
  font-weight: 600;
}
.sub {
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.9;
}
.status-card {
  margin-top: 12px;
}
.days-row {
  display: flex;
  text-align: center;
}
.days-row > div {
  flex: 1;
}
.days-row .num {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: var(--van-primary-color);
  line-height: 1.2;
}
.days-row .label {
  font-size: 12px;
  color: #999;
}
.month-row {
  margin-top: 12px;
  text-align: center;
  font-size: 13px;
  color: #666;
}
.reward-tip {
  margin-top: 8px;
  text-align: center;
  font-size: 13px;
  color: var(--van-primary-color);
}
.sign-btn {
  margin-top: 16px;
}
.rank-title {
  margin: 16px 16px 0;
  font-size: 15px;
  font-weight: 600;
}
.rank-card {
  padding: 8px 16px;
}
.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 14px;
}
.rank-item:last-child {
  border-bottom: none;
}
.rank-no {
  width: 40px;
  font-weight: 700;
  color: #666;
}
.rank-name {
  flex: 1;
}
.rank-count {
  color: #999;
  font-size: 13px;
}
.my-rank {
  background: #fff7f3;
  margin: 0 -16px;
  padding-left: 16px;
  padding-right: 16px;
}
</style>
