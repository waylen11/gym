<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { getOrders, payOrder } from '../../api'

const router = useRouter()
const list = ref([])
const payingId = ref(null)

async function load() {
  list.value = (await getOrders()) || []
}
load()

const tagType = { 0: 'warning', 1: 'success', 2: 'default' }

// 待支付订单可直接去支付（模拟支付）
async function onPay(order) {
  if (payingId.value) return
  payingId.value = order.orderId
  try {
    await payOrder(order.orderId)
    showSuccessToast('支付成功')
    load()
  } finally {
    payingId.value = null
  }
}
</script>

<template>
  <div class="page orders-page">
    <van-nav-bar title="我的订单" left-arrow fixed placeholder @click-left="router.back()" />

    <van-empty v-if="list.length === 0" description="暂无订单" />

    <div v-for="o in list" :key="o.orderId" class="card-box order-item">
      <div class="head-row">
        <span class="type">{{ o.typeName }}</span>
        <van-tag :type="tagType[o.status] || 'default'">{{ o.statusText }}</van-tag>
      </div>
      <div class="meta-row">订单号：{{ o.orderNo }}</div>
      <div class="meta-row">下单时间：{{ o.createTime }}</div>
      <div v-if="o.payTime" class="meta-row">支付时间：{{ o.payTime }}</div>
      <div class="foot-row">
        <span class="amount">￥{{ o.amount }}</span>
        <van-button
          v-if="o.status === 0"
          type="primary"
          size="small"
          round
          :loading="payingId === o.orderId"
          loading-text="支付中"
          @click="onPay(o)"
        >
          去支付
        </van-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
}
.meta-row {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
}
.foot-row {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.amount {
  font-size: 18px;
  font-weight: 700;
  color: var(--van-primary-color);
}
</style>
