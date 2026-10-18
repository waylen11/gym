<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { getMe, updateMe } from '../../api'

const router = useRouter()
const submitting = ref(false)
// sex: 0=男 1=女（与后端 User.sex 约定一致）
const form = ref({ realName: '', sex: 0, age: null, phone: '' })

// 回显当前资料
getMe().then((data) => {
  form.value = {
    realName: data.realName || '',
    sex: data.sex ?? 0,
    age: data.age ?? null,
    phone: data.phone || ''
  }
})

const rules = {
  realName: [{ required: true, message: '请输入昵称' }],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确' }
  ],
  age: [{ validator: (v) => v === null || v === '' || (Number(v) >= 1 && Number(v) <= 120), message: '年龄需在 1-120 之间' }]
}

// 只提交这 4 个字段，后端按非 null 字段更新
async function onSubmit() {
  submitting.value = true
  try {
    await updateMe({
      realName: form.value.realName,
      sex: form.value.sex,
      age: form.value.age === '' ? null : Number(form.value.age),
      phone: form.value.phone
    })
    showSuccessToast('保存成功')
    router.back()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page edit-page">
    <van-nav-bar title="编辑资料" left-arrow fixed placeholder @click-left="router.back()" />

    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="form.realName"
          name="realName"
          label="昵称"
          placeholder="请输入昵称"
          :rules="rules.realName"
          clearable
        />
        <van-field name="sex" label="性别">
          <template #input>
            <van-radio-group v-model="form.sex" direction="horizontal">
              <van-radio :name="0">男</van-radio>
              <van-radio :name="1">女</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
          v-model="form.age"
          name="age"
          label="年龄"
          type="digit"
          maxlength="3"
          placeholder="请输入年龄"
          :rules="rules.age"
        />
        <van-field
          v-model="form.phone"
          name="phone"
          label="手机号"
          type="tel"
          maxlength="11"
          placeholder="请输入手机号"
          :rules="rules.phone"
          clearable
        />
      </van-cell-group>

      <div class="submit-wrap">
        <van-button block round type="primary" native-type="submit" :loading="submitting">
          保存
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.submit-wrap {
  margin: 24px 16px;
}
</style>
