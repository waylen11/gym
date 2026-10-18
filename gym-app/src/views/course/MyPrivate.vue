<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyPrivate } from '../../api'

const router = useRouter()
const list = ref([])

getMyPrivate().then((data) => (list.value = data || []))
</script>

<template>
  <div class="page my-private-page">
    <van-nav-bar title="我的私教课" left-arrow fixed placeholder @click-left="router.back()" />

    <van-empty v-if="list.length === 0" description="暂无私教课记录（私教课由前台/管理端办理购买）" />

    <div v-for="r in list" :key="r.recordId" class="card-box private-item">
      <div class="head-row">
        <span class="course-name">{{ r.subject?.courseName }}</span>
        <span class="count">剩余 {{ r.count }} 次</span>
      </div>
      <div class="meta-row">
        <span><van-icon name="friends-o" /> 教练：{{ r.coach?.realName || '--' }}</span>
      </div>
      <div class="meta-row">
        <span><van-icon name="clock-o" /> 购课日期：{{ r.courseDate || '--' }}</span>
        <span class="price">实付 ￥{{ r.realprice }}</span>
      </div>
      <div v-if="r.remark" class="remark-row">打卡记录：{{ r.remark }}</div>
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
.count {
  font-size: 13px;
  color: var(--van-primary-color);
}
.meta-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
}
.meta-row .van-icon {
  color: var(--van-primary-color);
  margin-right: 2px;
}
.price {
  color: var(--van-primary-color);
  font-weight: 600;
}
.remark-row {
  margin-top: 8px;
  padding: 8px 10px;
  background: #fff7f3;
  border-radius: 8px;
  font-size: 12px;
  color: #999;
}
</style>
