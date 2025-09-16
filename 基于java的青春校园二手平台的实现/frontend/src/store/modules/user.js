import Cookies from 'js-cookie'
import { login, register, getUserInfo, updateUserInfo } from '@/api/user'

const state = {
  token: Cookies.get('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo')) || null
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    if (token) {
      Cookies.set('token', token, { expires: 7 })
    } else {
      Cookies.remove('token')
    }
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    if (userInfo) {
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } else {
      localStorage.removeItem('userInfo')
    }
  }
}

const actions = {
  // 登录
  async login({ commit }, loginForm) {
    try {
      const response = await login(loginForm)
      if (response.data.code === 200) {
        const { token, user } = response.data.data
        commit('SET_TOKEN', token)
        commit('SET_USER_INFO', user)
        return { success: true, data: response.data.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  },

  // 注册
  async register({ commit }, registerForm) {
    try {
      const response = await register(registerForm)
      if (response.data.code === 200) {
        return { success: true, message: '注册成功' }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  },

  // 获取用户信息
  async getUserInfo({ commit, state }) {
    if (!state.token) return
    try {
      const response = await getUserInfo()
      if (response.data.code === 200) {
        commit('SET_USER_INFO', response.data.data)
        return response.data.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  },

  // 更新用户信息
  async updateUserInfo({ commit }, userInfo) {
    try {
      const response = await updateUserInfo(userInfo)
      if (response.data.code === 200) {
        commit('SET_USER_INFO', response.data.data)
        return { success: true, message: '更新成功' }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '更新失败' }
    }
  },

  // 登出
  logout({ commit }) {
    commit('SET_TOKEN', '')
    commit('SET_USER_INFO', null)
  },

  // 检查登录状态
  checkLogin({ commit, state }) {
    if (state.token && !state.userInfo) {
      // 有token但没有用户信息，重新获取
      this.dispatch('user/getUserInfo')
    }
  }
}

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  isLoggedIn: state => !!state.token,
  userRole: state => state.userInfo?.role || 'user',
  userId: state => state.userInfo?.id
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}