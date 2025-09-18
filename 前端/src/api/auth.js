import request from './request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 发送注册验证码
export const sendRegisterCode = (phone) => {
  return request({
    url: '/auth/send-register-code',
    method: 'post',
    data: { phone }
  })
}

// 发送重置密码验证码
export const sendResetCode = (phone) => {
  return request({
    url: '/auth/send-reset-code',
    method: 'post',
    data: { phone }
  })
}

// 通用验证码发送接口
export const sendVerifyCode = (data) => {
  const { phone, type } = data
  const urlMap = {
    register: '/auth/send-register-code',
    reset: '/auth/send-reset-code',
    login: '/auth/send-login-code'
  }
  
  return request({
    url: urlMap[type] || '/auth/send-verify-code',
    method: 'post',
    data: { phone, type }
  })
}

// 重置密码
export const resetPassword = (data) => {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}

// 刷新Token
export const refreshToken = () => {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

// 退出登录
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// 更新用户信息
export const updateProfile = (data) => {
  return request({
    url: '/auth/profile',
    method: 'put',
    data
  })
}

// 学生身份认证申请
export const verifyStudent = (data) => {
  return request({
    url: '/auth/verify-student',
    method: 'post',
    data
  })
}

// 获取认证状态
export const getVerifyStatus = () => {
  return request({
    url: '/auth/verify-status',
    method: 'get'
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/auth/change-password',
    method: 'post',
    data
  })
}

// 获取学校列表
export const getSchools = () => {
  return request({
    url: '/schools',
    method: 'get'
  })
}

// 获取用户个人资料
export const getUserProfile = () => {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// 更新用户个人资料
export const updateUserProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

// 获取用户统计信息
export const getUserStats = () => {
  return request({
    url: '/user/stats',
    method: 'get'
  })
}

// 获取我的商品
export const getMyProducts = (params) => {
  return request({
    url: '/products/my',
    method: 'get',
    params
  })
}

// 获取我的订单
export const getMyOrders = (params) => {
  return request({
    url: '/orders/my',
    method: 'get',
    params
  })
}

// 获取我的收藏
export const getMyFavorites = (params) => {
  return request({
    url: '/favorites/user/' + (params?.userId || 'me'),
    method: 'get',
    params: {
      page: params?.page || 1,
      size: params?.size || 12
    }
  })
}