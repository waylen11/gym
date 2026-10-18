<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { getGoods, exchangeGoods } from '../../api'

const list = ref([])
const exchangingId = ref(null)

async function load() {
  list.value = (await getGoods()) || []
}
load()

// 积分兑换：成功 Toast 显示剩余积分；失败由拦截器自动弹后端 message
async function onExchange(goods) {
  if (exchangingId.value) return
  exchangingId.value = goods.goodsId
  try {
    const res = await exchangeGoods(goods.goodsId)
    showToast(`兑换成功，剩余 ${res.remainPoints} 积分`)
    load() // 库存可能变化，刷新列表
  } finally {
    exchangingId.value = null
  }
}
</script>

<template>
  <div class="page has-tabbar goods-page">
    <div class="goods-header">
      <div class="title">积分商城</div>
      <div class="sub">边运动边赚积分，好礼免费兑</div>
    </div>

    <van-empty v-if="list.length === 0" description="暂无在售商品" />

    <div v-for="g in list" :key="g.goodsId" class="card-box goods-item">
      <div class="goods-body">
        <div class="goods-name">
          {{ g.goodsName }}
          <span v-if="g.unit" class="goods-unit">（{{ g.unit }}）</span>
        </div>
        <div v-if="g.description" class="goods-desc">{{ g.description }}</div>
        <div class="goods-price-row">
          <span class="sell-price">￥{{ g.sellPrice }}</span>
          <span v-if="g.price && g.price !== g.sellPrice" class="origin-price">￥{{ g.price }}</span>
          <van-tag :type="g.stock > 0 ? 'success' : 'danger'" class="stock-tag">{{ g.stockText }}</van-tag>
        </div>
        <div v-if="g.pointsPrice > 0" class="points-row">
          <van-tag type="warning" plain>{{ g.pointsPrice }} 积分可兑换</van-tag>
        </div>
      </div>
      <van-button
        v-if="g.pointsPrice > 0"
        type="primary"
        size="small"
        round
        :disabled="g.stock <= 0"
        :loading="exchangingId === g.goodsId"
        loading-text="兑换中"
        @click="onExchange(g)"
      >
        积分兑换
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.goods-header {
  padding: 24px 16px 16px;
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
.goods-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.goods-body {
  flex: 1;
  min-width: 0;
}
.goods-name {
  font-size: 16px;
  font-weight: 600;
}
.goods-unit {
  font-size: 12px;
  color: #999;
  font-weight: 400;
}
.goods-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #999;
}
.goods-price-row {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.sell-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--van-primary-color);
}
.origin-price {
  font-size: 12px;
  color: #c8c9cc;
  text-decoration: line-through;
}
.points-row {
  margin-top: 8px;
}
</style>
