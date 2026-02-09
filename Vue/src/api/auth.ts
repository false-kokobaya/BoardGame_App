import axios from 'axios'

const base = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

/** ユーザー登録APIのリクエスト */
export interface RegisterRequest {
  username: string
  email: string
  password: string
}

/** ログインAPIのリクエスト */
export interface LoginRequest {
  username: string
  password: string
}

/** 認証成功時のレスポンス（トークン・ユーザー名・ID） */
export interface AuthResponse {
  token: string
  username: string
  userId: number
}

/** 認証API（登録・ログイン） */
export const authApi = {
  /** 新規ユーザーを登録する */
  register(data: RegisterRequest) {
    return base.post<AuthResponse>('/auth/register', data)
  },
  /** ログインしトークンを取得する */
  login(data: LoginRequest) {
    return base.post<AuthResponse>('/auth/login', data)
  },
}
