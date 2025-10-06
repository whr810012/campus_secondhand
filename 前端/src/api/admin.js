import request from './request'

/**
 * 管理员API接口
 */

// 获取仪表盘统计数据
export const getDashboardStats = () => {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

// 获取用户统计数据
export const getUserStats = (days = 30) => {
  return request({
    url: '/admin/stats/users',
    method: 'get',
    params: { days }
  })
}

// 获取商品统计数据
export const getProductStats = (days = 30) => {
  return request({
    url: '/admin/stats/products',
    method: 'get',
    params: { days }
  })
}

// 获取订单统计数据
export const getOrderStats = (days = 30) => {
  return request({
    url: '/admin/stats/orders',
    method: 'get',
    params: { days }
  })
}

// 获取交易统计数据
export const getTransactionStats = (days = 30) => {
  return request({
    url: '/admin/stats/transactions',
    method: 'get',
    params: { days }
  })
}

// 分页查询用户列表
export const getUserList = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 获取用户详细信息
export const getUserDetail = (userId) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}

// 分页查询商品列表
export const getProductList = (params) => {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

// 分页查询待审核商品
export const getPendingProducts = (params) => {
  return request({
    url: '/admin/products/pending',
    method: 'get',
    params
  })
}

// 审核商品通过
export const approveProduct = (productId, data) => {
  return request({
    url: `/admin/products/${productId}/approve`,
    method: 'post',
    data
  })
}

// 审核商品拒绝
export const rejectProduct = (productId, data) => {
  return request({
    url: `/admin/products/${productId}/reject`,
    method: 'post',
    params: {
      adminId: data.adminId,
      reason: data.reason
    }
  })
}

// 获取最新注册用户
export const getRecentUsers = (params = { page: 1, size: 5 }) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params: {
      ...params,
      sortBy: 'latest'
    }
  })
}

// 获取商品分类统计
export const getCategoryStats = () => {
  return request({
    url: '/admin/stats/categories',
    method: 'get'
  })
}

// 获取系统状态（模拟接口，实际可能需要后端实现）
export const getSystemStatus = () => {
  return request({
    url: '/admin/system/status',
    method: 'get'
  })
}

// 获取用户增长趋势数据
export const getUserGrowthTrend = (days = 7) => {
  return request({
    url: '/admin/stats/user-growth',
    method: 'get',
    params: { days }
  })
}

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/orders/admin',
    method: 'get',
    params
  })
}

// 封禁用户
export const banUser = (userId, data) => {
  return request({
    url: `/admin/users/${userId}/ban`,
    method: 'post',
    data
  })
}

// 解封用户
export const unbanUser = (userId, data) => {
  return request({
    url: `/admin/users/${userId}/unban`,
    method: 'post',
    data
  })
}

// 批量封禁用户
export const batchBanUsers = (data) => {
  return request({
    url: '/admin/users/batch-ban',
    method: 'post',
    data
  })
}

// 批量解封用户
export const batchUnbanUsers = (data) => {
  return request({
    url: '/admin/users/batch-unban',
    method: 'post',
    data
  })
}

// 删除用户
export const deleteUser = (userId, data) => {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete',
    data
  })
}

// 批量删除用户
export const batchDeleteUsers = (data) => {
  return request({
    url: '/admin/users/batch-delete',
    method: 'post',
    data
  })
}

// 获取待审核的学生身份认证列表
export const getPendingVerifications = (params) => {
  return request({
    url: '/admin/users/pending-verifications',
    method: 'get',
    params
  })
}

// 审核学生身份认证
export const verifyStudentIdentity = (userId, data) => {
  return request({
    url: `/admin/users/verifications/${userId}`,
    method: 'post',
    data
  })
}

// 批量认证学生身份
export const batchVerifyStudentIdentity = (data) => {
  return request({
    url: '/admin/users/verifications/batch',
    method: 'post',
    data
  })
}

// 根据ID获取图片
export const getImageById = (imageId) => {
  return request({
    url: `/upload/image/${imageId}`,
    method: 'get',
    responseType: 'blob' // 获取二进制数据
  })
}

// 导出用户数据
export const exportUsersData = () => {
  return request({
    url: '/admin/users/export',
    method: 'get',
    responseType: 'blob'
  })
}