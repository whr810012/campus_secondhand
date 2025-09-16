import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 刷新token
export function refreshToken() {
  return request({
    url: '/api/auth/refresh',
    method: 'post'
  })
}

// 检查用户名是否存在
export function checkUsername(username) {
  return request({
    url: `/api/auth/check-username/${username}`,
    method: 'get'
  })
}

// 检查邮箱是否存在
export function checkEmail(email) {
  return request({
    url: `/api/auth/check-email/${email}`,
    method: 'get'
  })
}

// 发送验证码
export function sendVerificationCode(email) {
  return request({
    url: '/api/auth/send-code',
    method: 'post',
    data: { email }
  })
}

// 验证邮箱验证码
export function verifyCode(email, code) {
  return request({
    url: '/api/auth/verify-code',
    method: 'post',
    data: { email, code }
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/api/auth/reset-password',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/auth/user-info',
    method: 'get'
  })
}