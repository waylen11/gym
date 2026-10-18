<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLosts } from '../../api'

const router = useRouter()
const list = ref([])
const loading = ref(false)

// status 可选：0 未认领 / 1 已认领 / 不传=全部（后端默认未认领在前）
const tabs = [
  { label: '全部', value: null },
  { label: '未认领', value: 0 },
  { label: '已认领', value: 1 }
]
const activeTab = ref(0)

async function load() {
  loading.value = true
  try {
    const status = tabs[activeTab.value].value
    list.value = (await getLosts(status)) || []
  } finally {
    loading.value = false
  }
}
load()
</script>

<template>
  <div class="page lost-page">
    <van-nav-bar title="失物招领" left-arrow fixed placeholder @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" color="#ff6b35" @change="load">
      <van-tab v-for="t in tabs" :key="t.label" :title="t.label" />
    </van-tabs>

    <van-loading v-if="loading && list.length === 0" class="loading" vertical>加载中...</van-loading>
    <van-empty v-else-if="list.length === 0" description="暂无失物招领信息" />

    <div v-for="l in list" :key="l.lostId" class="card-box lost-item">
      <van-image
        v-if="l.image"
        :src="l.image"
        width="72"
        height="72"
        radius="8"
        fit="cover"
        class="lost-img"
      />
      <div class="lost-body">
        <div class="head-row">
          <span class="name">{{ l.itemName }}</span>
          <van-tag :type="l.status === 0 ? 'primary' : 'success'">{{ l.statusText }}</van-tag>
        </div>
        <div v-if="l.description" class="desc">{{ l.description }}</div>
        <div class="meta">
          <span v-if="l.address"><van-icon name="location-o" /> {{ l.address }}</span>
        </div>
        <div class="meta">
          <span><van-icon name="clock-o" /> 拾取时间：{{ l.foundDate }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loading {
  margin-top: 40vh;
}
.lost-item {
  display: flex;
  gap: 12px;
}
.lost-body {
  flex: 1;
  min-width: 0;
}
.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.name {
  font-size: 15px;
  font-weight: 600;
}
.desc {
  margin-top: 4px;
  font-size: 13px;
  color: #666;
}
.meta {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
.meta .van-icon {
  color: var(--van-primary-color);
  margin-right: 2px;
}
</style>
