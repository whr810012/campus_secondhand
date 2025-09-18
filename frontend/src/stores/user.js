import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role?.toLowerCase() === 'admin')

  // 登录
  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      const { token: newToken, user: userData } = response.data
      
      token.value = newToken
      user.value = userData
      localStorage.setItem('token', newToken)
      
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '登录失败' }
    }
  }

  // 注册
  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData)
      return { success: true, message: '注册成功，请登录' }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '注册失败' }
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    if (!token.value) return
    
    try {
      const response = await api.get('/user/profile')
      user.value = response.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      logout()
    }
  }

  // 更新用户信息
  const updateProfile = async (profileData) => {
    try {
      const response = await api.put('/user/profile', profileData)
      user.value = { ...user.value, ...response.data }
      return { success: true, message: '更新成功' }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '更新失败' }
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchUserInfo,
    updateProfile
  }
})