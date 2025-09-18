import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, logout, getCurrentUser } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref(null)
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const isVerified = computed(() => userInfo.value?.verifyStatus === 2)

  // 登录
  const loginAction = async (loginForm) => {
    loading.value = true
    try {
      const response = await login(loginForm)
      const { token: newToken, user } = response.data
      
      token.value = newToken
      userInfo.value = user
      setToken(newToken)
      
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const registerAction = async (registerForm) => {
    loading.value = true
    try {
      const response = await register(registerForm)
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    if (!token.value) return null
    
    try {
      const response = await getCurrentUser()
      userInfo.value = response.data
      return response.data
    } catch (error) {
      // Token可能已过期，清除本地存储
      logoutAction()
      throw error
    }
  }

  // 登出
  const logoutAction = async () => {
    try {
      if (token.value) {
        await logout()
      }
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      token.value = null
      userInfo.value = null
      removeToken()
    }
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
  }

  // 初始化用户信息
  const initUserInfo = async () => {
    if (token.value && !userInfo.value) {
      try {
        await getUserInfo()
      } catch (error) {
        console.error('初始化用户信息失败:', error)
      }
    }
  }

  return {
    token,
    userInfo,
    loading,
    isLoggedIn,
    isAdmin,
    isVerified,
    login: loginAction,
    loginAction,
    register: registerAction,
    registerAction,
    getUserInfo,
    logout: logoutAction,
    logoutAction,
    updateUserInfo,
    initUserInfo
  }
})