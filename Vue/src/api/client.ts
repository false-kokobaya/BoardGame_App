import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

/**
 * /api 向けの axios インスタンス。
 * リクエストにJWTを付与し、401時はログイン画面へリダイレクトする。
 */
const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  // FormData のときは Content-Type を送らない（ブラウザが boundary 付き multipart を設定する）
  if (config.data instanceof FormData) {
    delete config.headers['Content-Type']
  }
  return config
})

client.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err.response?.status === 401) {
      const auth = useAuthStore()
      auth.clearAuth()
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default client
