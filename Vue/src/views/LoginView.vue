<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = 'ユーザー名とパスワードを入力してください'
    return
  }
  loading.value = true
  try {
    const { data } = await authApi.login({ username: username.value, password: password.value })
    auth.setAuth(data.token, data.username, data.userId)
    router.push({ name: 'mypage' })
  } catch (e: unknown) {
    const err = e as { response?: { data?: { error?: string } } }
    error.value = err.response?.data?.error ?? 'ログインに失敗しました'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>ログイン</h1>
      <form @submit.prevent="submit" class="auth-form">
        <div class="field">
          <label for="username">ユーザー名</label>
          <input id="username" v-model="username" type="text" autocomplete="username" />
        </div>
        <div class="field">
          <label for="password">パスワード</label>
          <input id="password" v-model="password" type="password" autocomplete="current-password" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading">ログイン</button>
      </form>
      <p class="link">
        <router-link to="/register">新規登録はこちら</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}
.auth-card {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 360px;
}
.auth-card h1 {
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}
.auth-form .field {
  margin-bottom: 1rem;
}
.auth-form label {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
  color: #555;
}
.auth-form input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.auth-form .error {
  color: #b91c1c;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}
.auth-form button {
  width: 100%;
  padding: 0.6rem;
  margin-top: 0.5rem;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
}
.auth-form button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.link {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
}
</style>
