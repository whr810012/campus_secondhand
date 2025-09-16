import axios from 'axios'

// 用户注册
export function register(data) {
  return axios({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 用户登录
export function login(data) {
  return axios({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return axios({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return axios({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return axios({
    url: '/user/change-password',
    method: 'put',
    data
  })
}

// 获取用户列表（管理员）
export function getUserList(params) {
  return axios({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情（管理员）
export function getUserDetail(id) {
  return axios({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 更新用户状态（管理员）
export function updateUserStatus(id, status) {
  return axios({
    url: `/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新用户角色（管理员）
export function updateUserRole(id, role) {
  return axios({
    url: `/user/${id}/role`,
    method: 'put',
    data: { role }
  })
}

// 重置用户密码（管理员）
export function resetUserPassword(id) {
  return axios({
    url: `/user/${id}/reset-password`,
    method: 'put'
  })
}

// 删除用户（管理员）
export function deleteUser(id) {
  return axios({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 获取用户统计信息（管理员）
export function getUserStats() {
  return axios({
    url: '/user/stats',
    method: 'get'
  })
}

// 检查用户名是否存在
export function checkUsername(username) {
  return axios({
    url: '/user/check-username',
    method: 'get',
    params: { username }
  })
}

// 检查邮箱是否存在
export function checkEmail(email) {
  return axios({
    url: '/user/check-email',
    method: 'get',
    params: { email }
  })
}

// 创建管理员账户
export function createAdmin(data) {
  return axios({
    url: '/user/admin/create',
    method: 'post',
    data
  })
}