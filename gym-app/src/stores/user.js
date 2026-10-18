import { defineStore } from 'pinia'

// token 以 localStorage 为准（Pinia 只做内存态，避免持久化问题）
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    info: null
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    logout() {
      this.token = ''
      this.info = null
      localStorage.removeItem('token')
    }
  }
})
