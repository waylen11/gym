<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast, showToast } from 'vant'
import { getAppointments, cancelAppointment, createAppointment, getMyPrivate } from '../../api'

const router = useRouter()

// status：0 已预约 / 1 已打卡 / 2 已取消
const tabs = [
  { label: '已预约', value: 0 },
  { label: '已打卡', value: 1 },
  { label: '已取消', value: 2 }
]
const activeTab = ref(0)
const list = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    list.value = (await getAppointments(activeTab.value)) || []
  } finally {
    loading.value = false
  }
}
load()

const tagType = { 0: 'primary', 1: 'success', 2: 'default' }

// 取消预约
async function onCancel(item) {
  try {
    await showConfirmDialog({ title: '提示', message: `确定取消 ${item.appointmentDate} 的预约吗？` })
  } catch {
    return
  }
  await cancelAppointment(item.appointmentId)
  showSuccessToast('已取消')
  load()
}

// ===== 新建预约（选自己的私教课记录 + 日期）=====
const showCreate = ref(false)
const showCalendar = ref(false)
const myPrivates = ref([])
const picked = ref(null)
const dateText = ref('')
const remark = ref('')
const submitting = ref(false)

// 日历可选范围：今天 ~ 90 天内
const minDate = new Date()
const maxDate = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate() + 90)

async function openCreate() {
  picked.value = null
  dateText.value = ''
  remark.value = ''
  myPrivates.value = ((await getMyPrivate()) || []).filter((r) => r.count > 0)
  if (myPrivates.value.length === 0) {
    return showToast('暂无可预约的私教课（私教课由前台/管理端办理购买）')
  }
  showCreate.value = true
}

function onDateConfirm(date) {
  const p = (n) => String(n).padStart(2, '0')
  dateText.value = `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())}`
  showCalendar.value = false
}

async function onSubmit() {
  if (!picked.value) return showToast('请选择要预约的私教课')
  if (!dateText.value) return showToast('请选择预约日期')
  submitting.value = true
  try {
    // 后端 AppointmentCreateDTO：{recordId, appointmentDate, remark}
    await createAppointment({
      recordId: picked.value.recordId,
      appointmentDate: dateText.value,
      remark: remark.value || undefined
    })
    showSuccessToast('预约成功')
    showCreate.value = false
    activeTab.value = 0
    load()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page appointment-page">
    <van-nav-bar title="我的预约" left-arrow fixed placeholder @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" color="#ff6b35" @change="load">
      <van-tab v-for="t in tabs" :key="t.value" :title="t.label" />
    </van-tabs>

    <van-loading v-if="loading && list.length === 0" class="loading" vertical>加载中...</van-loading>
    <van-empty v-else-if="list.length === 0" description="暂无预约记录" />

    <div v-for="item in list" :key="item.appointmentId" class="card-box appt-item">
      <div class="head-row">
        <span class="course-name">{{ item.subject?.courseName }}</span>
        <van-tag :type="tagType[item.status] || 'default'">{{ item.statusText }}</van-tag>
      </div>
      <div class="meta-row">
        <span><van-icon name="friends-o" /> 教练：{{ item.coach?.realName || '--' }}</span>
      </div>
      <div class="meta-row">
        <span><van-icon name="calendar-o" /> {{ item.appointmentDate }}</span>
      </div>
      <div v-if="item.checkinTime" class="meta-row">
        <span><van-icon name="clock-o" /> 打卡时间：{{ item.checkinTime }}</span>
      </div>
      <div v-if="item.remark" class="meta-row remark">备注：{{ item.remark }}</div>

      <div v-if="item.status === 0" class="btn-row">
        <van-button size="small" round plain type="danger" @click="onCancel(item)">取消预约</van-button>
      </div>
    </div>

    <!-- 新建预约按钮 -->
    <van-button type="primary" round block size="large" class="create-btn" icon="plus" @click="openCreate">
      新建预约
    </van-button>

    <!-- 创建预约弹窗 -->
    <van-popup v-model:show="showCreate" round position="bottom" style="padding: 20px 16px">
      <div class="popup-title">新建预约</div>

      <div class="field-label">选择私教课</div>
      <div
        v-for="r in myPrivates"
        :key="r.recordId"
        class="pick-item"
        :class="{ picked: picked?.recordId === r.recordId }"
        @click="picked = r"
      >
        <div>
          <div class="pick-name">{{ r.subject?.courseName }}</div>
          <div class="pick-meta">{{ r.coach?.realName }} · 剩余 {{ r.count }} 次</div>
        </div>
        <van-icon
          :name="picked?.recordId === r.recordId ? 'checked' : 'circle'"
          :color="picked?.recordId === r.recordId ? 'var(--van-primary-color)' : '#dcdee0'"
          size="20"
        />
      </div>

      <div class="field-label">预约日期</div>
      <van-field
        :model-value="dateText"
        placeholder="点击选择日期"
        readonly
        is-link
        @click="showCalendar = true"
      />

      <div class="field-label">备注（可选）</div>
      <van-field
        v-model="remark"
        type="textarea"
        rows="2"
        maxlength="50"
        placeholder="有什么想对教练说的吗"
        show-word-limit
      />

      <van-button type="primary" round block class="submit-btn" :loading="submitting" @click="onSubmit">
        提交预约
      </van-button>
    </van-popup>

    <!-- 日期选择 -->
    <van-calendar v-model:show="showCalendar" :min-date="minDate" :max-date="maxDate" @confirm="onDateConfirm" />
  </div>
</template>

<style scoped>
.loading {
  margin-top: 40vh;
}
.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
}
.meta-row {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
}
.meta-row .van-icon {
  color: var(--van-primary-color);
  margin-right: 2px;
}
.meta-row.remark {
  color: #999;
}
.btn-row {
  margin-top: 10px;
  text-align: right;
}
.create-btn {
  width: calc(100% - 24px);
  margin: 8px 12px 0;
}
.popup-title {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}
.field-label {
  margin: 12px 0 6px;
  font-size: 13px;
  color: #999;
}
.pick-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 8px;
}
.pick-item.picked {
  border-color: var(--van-primary-color);
  background: #fff7f3;
}
.pick-name {
  font-size: 14px;
  font-weight: 600;
}
.pick-meta {
  margin-top: 2px;
  font-size: 12px;
  color: #999;
}
.submit-btn {
  margin-top: 16px;
}
</style>
