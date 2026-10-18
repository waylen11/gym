<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const TABS = ['/', '/goods', '/sign', '/courses', '/mine']
const showTabbar = computed(() => TABS.includes(route.path))
</script>

<template>
  <router-view />
  <van-tabbar v-if="showTabbar" route safe-area-inset-bottom>
    <van-tabbar-item replace to="/" icon="wap-home-o">首页</van-tabbar-item>
    <van-tabbar-item replace to="/goods" icon="gift-o">商品</van-tabbar-item>
    <!-- 中间凸起签到按钮 -->
    <van-tabbar-item replace to="/sign">
      <template #icon="{ active }">
        <div class="sign-fab" :class="{ active }">
          <van-icon name="edit" />
        </div>
      </template>
      签到
    </van-tabbar-item>
    <van-tabbar-item replace to="/courses" icon="certificate">课程</van-tabbar-item>
    <van-tabbar-item replace to="/mine" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
.sign-fab {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: #dcdfe6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transform: translateY(-16px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
  transition: background 0.2s;
}
.sign-fab.active {
  background: var(--van-primary-color);
  box-shadow: 0 6px 12px rgba(255, 107, 53, 0.45);
}
</style>
