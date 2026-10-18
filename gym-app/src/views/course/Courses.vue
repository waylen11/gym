<script setup>
import { ref } from 'vue'
import { getCourses } from '../../api'

const list = ref([])

getCourses().then((data) => (list.value = data || []))
</script>

<template>
  <div class="page has-tabbar courses-page">
    <div class="page-header">
      <div class="title">课程列表</div>
      <div class="sub">选一门喜欢的课，动起来</div>
    </div>

    <van-empty v-if="list.length === 0" description="暂无课程" />

    <div v-for="(c, i) in list" :key="i" class="card-box course-item">
      <div class="course-icon"><van-icon name="certificate" /></div>
      <div class="course-body">
        <div class="course-name">{{ c.courseName }}</div>
        <div class="course-meta">时长：{{ c.duration }}</div>
      </div>
      <div class="course-price">￥{{ c.sellingPrice }}</div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  padding: 24px 16px 12px;
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
.course-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.course-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #fff3ec;
  color: var(--van-primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.course-body {
  flex: 1;
  min-width: 0;
}
.course-name {
  font-size: 15px;
  font-weight: 600;
}
.course-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
.course-price {
  font-size: 17px;
  font-weight: 700;
  color: var(--van-primary-color);
}
</style>
