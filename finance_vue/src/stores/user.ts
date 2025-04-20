import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login as loginApi, register as registerApi, logout as logoutApi, type AuthResponse } from '../api/auth'
import router from "@/router";

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  
  const isAuthenticated = computed(() => !!token.value)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUsername = (name: string) => {
    username.value = name
    localStorage.setItem('username', name)
  }

  const login = async (loginData: { username: string; password: string }) => {
    try {
      const { token: newToken, user } = await loginApi(loginData)
      setToken(newToken)
      setUsername(user.username)
      ElMessage.success('登录成功')
      return true
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '登录失败')
      return false
    }
  }

  const register = async (registerData: { username: string; password: string; email: string }) => {
    try {
      await registerApi(registerData)
      ElMessage.success('注册成功')
      return true
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '注册失败')
      return false
    }
  }

  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      token.value = ''
      username.value = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      await router.push('/login')
      ElMessage.success('已退出登录')
    }
  }

  return {
    token,
    username,
    isAuthenticated,
    login,
    register,
    logout
  }
})