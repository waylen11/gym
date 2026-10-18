<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { getCardTypes, createOrder, payOrder } from '../../api'

const router = useRouter()
const types = ref([])
const selected = ref(null)
const paying = ref(false)

getCardTypes().then((data) => (types.value = data || []))

// 三步闭环：选卡 → 下单 → 模拟支付
async function onBuy() {
  if (!selected.value || paying.value) return
  paying.value = true
  try {
    // 1. 创建订单
    const order = await createOrder(selected.value.id)
    // 2. 模拟支付确认弹窗
    try {
      await showConfirmDialog({
        title: '模拟支付',
        message: `确认支付 ${order.amount ?? selected.value.price} 元购买「${order.typeName || selected.value.typeName}」？`
      })
    } catch {
      return // 用户取消支付
    }
    // 3. 支付
    await payOrder(order.orderId)
    showSuccessToast('支付成功')
    router.replace('/card')
  } finally {
    paying.value = false
  }
}
</script>

<template>
  <div class="page buy-page">
    <van-nav-bar title="购买 / 续费会员卡" left-arrow fixed placeholder @click-left="router.back()" />

    <div class="type-title">选择卡类型</div>
    <div
      v-for="t in types"
      :key="t.id"
      class="card-box type-item"
      :class="{ selected: selected?.id === t.id }"
      @click="selected = t"
    >
      <div class="type-head">
        <span class="type-name">{{ t.typeName }}</span>
        <van-icon
          :name="selected?.id === t.id ? 'checked' : 'circle'"
          :color="selected?.id === t.id ? 'var(--van-primary-color)' : '#dcdee0'"
          size="22"
        />
      </div>
      <div class="type-desc">有效期 {{ t.validDays }} 天</div>
      <div v-if="t.description" class="type-desc">{{ t.description }}</div>
      <div class="type-price">￥{{ t.price }}</div>
    </div>

    <!-- 底部结算栏 -->
    <div class="pay-bar safe-area-inset-bottom">
      <div class="total">
        合计：<span class="amount">￥{{ selected?.price ?? '0.00' }}</span>
      </div>
      <van-button type="primary" round :disabled="!selected" :loading="paying" @click="onBuy">
        确认购买
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.type-title {
  margin: 14px 16px 8px;
  font-size: 15px;
  font-weight: 600;
}
.type-item {
  border: 2px solid transparent;
  transition: border-color 0.2s;
}
.type-item.selected {
  border-color: var(--van-primary-color);
}
.type-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.type-name {
  font-size: 16px;
  font-weight: 600;
}
.type-desc {
  margin-top: 6px;
  font-size: 13px;
  color: #999;
}
.type-price {
  margin-top: 8px;
  font-size: 20px;
  font-weight: 700;
  color: var(--van-primary-color);
}
.pay-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}
.pay-bar .amount {
  font-size: 20px;
  font-weight: 700;
  color: var(--van-primary-color);
}
.buy-page {
  padding-bottom: 80px;
}
</style>
