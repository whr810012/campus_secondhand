import request from './request'

// 获取用户个人信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户个人信息
export const updateUserInfo = (data) => {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

// 获取指定用户的公开信息
export const getUserProfile = (userId) => {
  return request({
    url: `/user/profile/${userId}`,
    method: 'get'
  })
}

// 获取指定用户的商品列表
export const getUserProducts = (userId, params = {}) => {
  return request({
    url: `/user/${userId}/products`,
    method: 'get',
    params
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 上传头像
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 学生认证
export const submitVerification = (data) => {
  return request({
    url: '/user/verification',
    method: 'post',
    data
  })
}

// 获取认证状态
export const getVerificationStatus = () => {
  return request({
    url: '/user/verification/status',
    method: 'get'
  })
}

// 获取用户统计信息
export const getUserStats = () => {
  return request({
    url: '/user/stats',
    method: 'get'
  })
}