<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { sendCode, login } from '../../api'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const phone = ref('')
const code = ref('')
const counting = ref(0)
let timer = null

const validPhone = () => /^1\d{10}$/.test(phone.value)

async function onSendCode() {
  if (!validPhone()) return showToast('请输入正确的11位手机号')
  if (counting.value > 0) return
  await sendCode(phone.value)
  showToast('验证码已发送')
  // 后端 60s 限发，按钮同步倒计时
  counting.value = 60
  timer = setInterval(() => {
    counting.value--
    if (counting.value <= 0) clearInterval(timer)
  }, 1000)
}

async function onLogin() {
  if (!validPhone()) return showToast('请输入正确的11位手机号')
  if (!code.value) return showToast('请输入验证码')
  // 登录成功返回的 data 就是 token 字符串
  const token = await login(phone.value, code.value)
  userStore.setToken(token)
  router.replace('/')
}

onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div class="login-page">
    <div class="login-head">
      <div class="logo"><van-icon name="fire-o" /></div>
      <h1>活力健身</h1>
      <p>会员端 · 运动让生活更有活力</p>
    </div>

    <div class="login-form">
      <van-field
        v-model="phone"
        type="tel"
        maxlength="11"
        label="手机号"
        placeholder="请输入手机号"
        clearable
      />
      <van-field v-model="code" type="digit" maxlength="6" label="验证码" placeholder="请输入验证码">
        <template #button>
          <van-button size="small" type="primary" :disabled="counting > 0" @click="onSendCode">
            {{ counting > 0 ? `${counting}s后重发` : '获取验证码' }}
          </van-button>
        </template>
      </van-field>
      <van-button type="primary" block round size="large" class="login-btn" @click="onLogin">
        登 录
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  box-sizing: border-box;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8f5f 30%, #f6f7f9 62%);
  padding: 0 24px;
}
.login-head {
  padding: 72px 0 40px;
  text-align: center;
  color: #fff;
}
.login-head .logo {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38px;
}
.login-head h1 {
  margin: 0 0 6px;
  font-size: 24px;
}
.login-head p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
}
.login-form {
  background: #fff;
  border-radius: 16px;
  padding: 20px 12px 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.login-btn {
  margin-top: 20px;
}
</style>
