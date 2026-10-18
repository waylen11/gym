<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCardInfo } from '../../api'

const router = useRouter()
const card = ref(null)
const statusText = { 1: '正常', 0: '已过期' }

getCardInfo().then((data) => (card.value = data))
</script>

<template>
  <div class="page card-page">
    <van-nav-bar title="我的会员卡" left-arrow fixed placeholder @click-left="router.back()" />

    <!-- 卡信息 -->
    <div class="card-panel">
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
    </div>

    <van-button type="primary" block round class="renew-btn" @click="router.push('/card/buy')">
      续费 / 购买会员卡
    </van-button>

    <!-- 续费记录 -->
    <div class="record-title">续费记录</div>
    <van-empty v-if="card && (!card.renewRecords || card.renewRecords.length === 0)" description="暂无续费记录" />
    <div v-for="r in card?.renewRecords || []" :key="r.rechargeId" class="card-box record-item">
      <div class="record-head">
        <span class="type">{{ r.membertype?.typeName || '会员卡' }}</span>
        <span class="money">￥{{ r.money }}</span>
      </div>
      <div class="record-meta">充值时间：{{ r.rechargeTime }}</div>
      <div v-if="r.remark" class="record-meta">备注：{{ r.remark }}</div>
    </div>
  </div>
</template>

<style scoped>
.card-panel {
  margin: 12px;
  padding: 18px;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, #ff6b35 0%, #ff9a6c 100%);
  box-shadow: 0 8px 20px rgba(255, 107, 53, 0.35);
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
.renew-btn {
  width: calc(100% - 24px);
  margin: 4px 12px 0;
}
.record-title {
  margin: 20px 16px 8px;
  font-size: 15px;
  font-weight: 600;
}
.record-item .record-head {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
}
.record-item .money {
  color: var(--van-primary-color);
}
.record-item .record-meta {
  margin-top: 6px;
  font-size: 13px;
  color: #999;
}
</style>
