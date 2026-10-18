<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getConsumptions } from '../../api'

const router = useRouter()
const list = ref([])

getConsumptions().then((data) => (list.value = data || []))
</script>

<template>
  <div class="page consume-page">
    <van-nav-bar title="消费记录" left-arrow fixed placeholder @click-left="router.back()" />

    <van-empty v-if="list.length === 0" description="暂无消费记录" />

    <div v-for="r in list" :key="r.id" class="card-box consume-item">
      <div class="head-row">
        <span class="name"><van-icon name="shopping-cart-o" /> {{ r.goods?.goodsName || '商品' }}</span>
        <span class="total">￥{{ r.totalPrice }}</span>
      </div>
      <div class="meta-row">
        <span>￥{{ r.goods?.price }} × {{ r.count }}</span>
        <span>{{ r.createTime }}</span>
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
.head-row .van-icon {
  color: var(--van-primary-color);
  margin-right: 4px;
}
.total {
  color: var(--van-primary-color);
  font-size: 17px;
}
.meta-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>
