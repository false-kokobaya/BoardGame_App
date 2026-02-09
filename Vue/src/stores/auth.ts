import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

const TOKEN_KEY = 'boardgame_token'
const USER_KEY = 'boardgame_user'

/**
 * 認証状態（トークン・ユーザー名・ID）を保持するストア。
 * localStorage と同期する。
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const username = ref<string | null>(localStorage.getItem(USER_KEY))
  const storedUserId = localStorage.getItem('boardgame_userId')
  const userId = ref<number | null>(storedUserId ? parseInt(storedUserId, 10) : null)

  const isAuthenticated = computed(() => !!token.value)

  /** ログイン成功時にトークン・ユーザー名・IDを保存する */
  function setAuth(t: string, u: string, id: number) {
    token.value = t
    username.value = u
    userId.value = id
    localStorage.setItem(TOKEN_KEY, t)
    localStorage.setItem(USER_KEY, u)
    localStorage.setItem('boardgame_userId', String(id))
  }

  /** ログアウト時に認証情報をクリアする */
  function clearAuth() {
    token.value = null
    username.value = null
    userId.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem('boardgame_userId')
  }

  return {
    token,
    username,
    userId,
    isAuthenticated,
    setAuth,
    clearAuth,
  }
})
