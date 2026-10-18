import axios from 'axios'
import { showToast } from 'vant'
import router from '../router'

const request = axios.create({ baseURL: '/api', timeout: 8000 })

// 请求拦截：自动带 token（后端约定直接放 token 字符串，不加 Bearer）
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = token
  return config
})

// 响应拦截：统一处理 {code, message, data}
// 后端约定 code !== 200 时 HTTP 状态也是 200，必须看业务 code
request.interceptors.response.use(
  (res) => {
    const { code, message, data } = res.data
    if (code === 200) return data // 直接给业务数据
    if (code === 401) {
      // token 失效 → 清除并跳登录
      localStorage.removeItem('token')
      router.push('/login')
      showToast('登录已过期，请重新登录')
      return Promise.reject(new Error(message))
    }
    // 400/500 → 后端 message 直接弹
    showToast(message)
    return Promise.reject(new Error(message))
  },
  (err) => {
    showToast('网络异常，请稍后重试')
    return Promise.reject(err)
  }
)

export default request
