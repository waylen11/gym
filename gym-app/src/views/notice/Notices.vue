<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getNotices } from '../../api'

const router = useRouter()
const list = ref([])

getNotices().then((data) => (list.value = data || []))
</script>

<template>
  <div class="page notices-page">
    <van-nav-bar title="公告通知" left-arrow fixed placeholder @click-left="router.back()" />

    <van-empty v-if="list.length === 0" description="暂无公告" />

    <!-- 后端已按置顶排序：第一条即置顶活动公告，加"活动"标签 -->
    <div v-for="(n, i) in list" :key="n.id" class="card-box notice-item">
      <div class="notice-head">
        <van-tag v-if="i === 0" type="danger" class="top-tag">活动</van-tag>
        <span class="notice-title">{{ n.title }}</span>
      </div>
      <div class="notice-content">{{ n.content }}</div>
      <div class="notice-meta">
        <span>{{ n.publisher?.realName || '管理员' }}</span>
        <span>{{ n.publishTime }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notice-head {
  display: flex;
  align-items: center;
  gap: 6px;
}
.top-tag {
  flex-shrink: 0;
}
.notice-title {
  font-size: 15px;
  font-weight: 600;
}
.notice-content {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
.notice-meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>
