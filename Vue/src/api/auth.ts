import axios from 'axios'

const base = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface AuthResponse {
  token: string
  username: string
  userId: number
}

export const authApi = {
  register(data: RegisterRequest) {
    return base.post<AuthResponse>('/auth/register', data)
  },
  login(data: LoginRequest) {
    return base.post<AuthResponse>('/auth/login', data)
  },
}
