<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCoachList } from '../../api'

const router = useRouter()
const list = ref([])

getCoachList().then((data) => (list.value = data || []))

// 后端约定：sex 0=男，1=女
const genderText = (sex) => (sex === 0 ? '男' : '女')
</script>

<template>
  <div class="page has-tabbar coach-page">
    <div class="page-header">
      <div class="title">教练团队</div>
      <div class="sub">专业教练，一对一指导</div>
    </div>

    <van-empty v-if="list.length === 0" description="暂无教练" />

    <div v-for="c in list" :key="c.id" class="card-box coach-item" @click="router.push(`/coach/${c.id}`)">
      <div class="avatar">{{ c.realName?.charAt(0) }}</div>
      <div class="coach-body">
        <div class="coach-name">{{ c.realName }}</div>
        <div class="coach-meta">{{ genderText(c.sex) }} · {{ c.age }} 岁 · 带教 {{ c.coachTeach ?? 0 }} 人</div>
      </div>
      <van-icon name="arrow" color="#c8c9cc" />
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
.coach-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b35, #ff9a6c);
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.coach-body {
  flex: 1;
  min-width: 0;
}
.coach-name {
  font-size: 15px;
  font-weight: 600;
}
.coach-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
</style>
