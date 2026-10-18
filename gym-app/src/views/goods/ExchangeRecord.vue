<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getExchanges } from '../../api'

const router = useRouter()
const list = ref([])

getExchanges().then((data) => (list.value = data || []))
</script>

<template>
  <div class="page exchange-page">
    <van-nav-bar title="我的兑换记录" left-arrow fixed placeholder @click-left="router.back()" />

    <van-empty v-if="list.length === 0" description="暂无兑换记录" />

    <div v-for="r in list" :key="r.exchangeId" class="card-box record-item">
      <div class="record-head">
        <span class="name"><van-icon name="gift-o" /> {{ r.goodsName }}</span>
        <span class="cost">-{{ r.pointsCost }} 积分</span>
      </div>
      <div class="record-time">{{ r.createTime }}</div>
    </div>
  </div>
</template>

<style scoped>
.record-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
}
.record-head .name .van-icon {
  color: var(--van-primary-color);
  margin-right: 4px;
}
.cost {
  color: var(--van-primary-color);
}
.record-time {
  margin-top: 8px;
  font-size: 13px;
  color: #999;
}
</style>
