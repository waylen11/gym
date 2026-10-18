<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCoachDetail } from '../../api'

const route = useRoute()
const router = useRouter()
const coach = ref(null)

// 后端约定：sex 0=男，1=女
const genderText = (sex) => (sex === 0 ? '男' : '女')

getCoachDetail(route.params.id).then((data) => (coach.value = data))
</script>

<template>
  <div class="page coach-detail-page">
    <van-nav-bar title="教练详情" left-arrow fixed placeholder @click-left="router.back()" />

    <div v-if="coach" class="coach-panel">
      <div class="avatar">{{ coach.realName?.charAt(0) }}</div>
      <div class="info">
        <div class="name">{{ coach.realName }}</div>
        <div class="meta">{{ genderText(coach.sex) }} · {{ coach.age }} 岁</div>
      </div>
      <div class="member-count">
        <span class="num">{{ coach.memberCount }}</span>
        <span class="label">私教会员</span>
      </div>
    </div>

    <div class="section-title">执教课程</div>
    <van-empty v-if="coach && (!coach.courses || coach.courses.length === 0)" description="暂无课程" />

    <div v-for="c in coach?.courses || []" :key="c.courseId" class="card-box course-item">
      <div class="course-body">
        <div class="course-name">{{ c.courseName }}</div>
        <div v-if="c.description" class="course-desc">{{ c.description }}</div>
        <div class="course-meta">时长 {{ c.duration }} 分钟</div>
      </div>
      <div class="course-price">￥{{ c.sellingPrice }}</div>
    </div>
  </div>
</template>

<style scoped>
.coach-panel {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 12px;
  padding: 18px;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, #ff6b35 0%, #ff9a6c 100%);
  box-shadow: 0 8px 20px rgba(255, 107, 53, 0.35);
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.info {
  flex: 1;
}
.name {
  font-size: 18px;
  font-weight: 700;
}
.meta {
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.9;
}
.member-count {
  text-align: center;
}
.member-count .num {
  display: block;
  font-size: 22px;
  font-weight: 700;
}
.member-count .label {
  font-size: 12px;
  opacity: 0.9;
}
.section-title {
  margin: 16px 16px 0;
  font-size: 15px;
  font-weight: 600;
}
.course-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.course-body {
  flex: 1;
  min-width: 0;
}
.course-name {
  font-size: 15px;
  font-weight: 600;
}
.course-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
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
