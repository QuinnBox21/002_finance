import axios from '../utils/axios'

export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  password: string
  email: string
}

export interface AuthResponse {
  token: string
  user: {
    id: string
    username: string
    email: string
  }
}

export const login = async (data: LoginData): Promise<AuthResponse> => {
  try {
    const response = await axios.post('/auth/login', data)
    return response.data
  } catch (error) {
    console.error('登录失败:', error)
    throw error
  }
}

export const register = async (data: RegisterData): Promise<void> => {
  try {
    const response = await axios.post('/auth/register', data)
    return response.data
  } catch (error) {
    console.error('注册失败:', error)
    throw error
  }
}

export const logout = async (): Promise<void> => {
  try {
    await axios.post('/auth/logout')
  } catch (error) {
    console.error('登出失败:', error)
    throw error
  }
} 