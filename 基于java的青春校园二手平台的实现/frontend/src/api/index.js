import request from '@/utils/request'

// 用户相关API
export const userApi = {
  // 用户注册
  register(data) {
    return request({
      url: '/api/auth/register',
      method: 'post',
      data
    })
  },
  
  // 用户登录
  login(data) {
    return request({
      url: '/api/auth/login',
      method: 'post',
      data
    })
  },
  
  // 获取用户信息
  getUserInfo() {
    return request({
      url: '/api/user/info',
      method: 'get'
    })
  },
  
  // 更新用户信息
  updateUserInfo(data) {
    return request({
      url: '/api/user/info',
      method: 'put',
      data
    })
  },
  
  // 修改密码
  changePassword(data) {
    return request({
      url: '/api/user/password',
      method: 'put',
      data
    })
  },
  
  // 上传头像
  uploadAvatar(data) {
    return request({
      url: '/api/user/avatar',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 检查用户名是否存在
  checkUsername(username) {
    return request({
      url: `/api/user/check-username/${username}`,
      method: 'get'
    })
  },
  
  // 检查邮箱是否存在
  checkEmail(email) {
    return request({
      url: `/api/user/check-email/${email}`,
      method: 'get'
    })
  }
}

// 商品相关API
export const productApi = {
  // 获取商品列表
  getProductList(params) {
    return request({
      url: '/api/products',
      method: 'get',
      params
    })
  },
  
  // 获取商品详情
  getProductDetail(id) {
    return request({
      url: `/api/products/${id}`,
      method: 'get'
    })
  },
  
  // 发布商品
  publishProduct(data) {
    return request({
      url: '/api/products',
      method: 'post',
      data
    })
  },
  
  // 更新商品
  updateProduct(id, data) {
    return request({
      url: `/api/products/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除商品
  deleteProduct(id) {
    return request({
      url: `/api/products/${id}`,
      method: 'delete'
    })
  },
  
  // 获取我的商品
  getMyProducts(params) {
    return request({
      url: '/api/products/my',
      method: 'get',
      params
    })
  },
  
  // 点赞商品
  likeProduct(id) {
    return request({
      url: `/api/products/${id}/like`,
      method: 'post'
    })
  },
  
  // 取消点赞
  unlikeProduct(id) {
    return request({
      url: `/api/products/${id}/like`,
      method: 'delete'
    })
  },
  
  // 上传商品图片
  uploadProductImages(data) {
    return request({
      url: '/api/products/upload-images',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 获取热门商品
  getHotProducts(params) {
    return request({
      url: '/api/products/hot',
      method: 'get',
      params
    })
  },
  
  // 获取最新商品
  getLatestProducts(params) {
    return request({
      url: '/api/products/latest',
      method: 'get',
      params
    })
  },
  
  // 获取推荐商品
  getRecommendedProducts(params) {
    return request({
      url: '/api/products/recommended',
      method: 'get',
      params
    })
  }
}

// 分类相关API
export const categoryApi = {
  // 获取分类列表
  getCategoryList() {
    return request({
      url: '/api/categories',
      method: 'get'
    })
  },
  
  // 获取分类树
  getCategoryTree() {
    return request({
      url: '/api/categories/tree',
      method: 'get'
    })
  }
}

// 订单相关API
export const orderApi = {
  // 创建订单
  createOrder(data) {
    return request({
      url: '/api/orders',
      method: 'post',
      data
    })
  },
  
  // 获取订单列表
  getOrderList(params) {
    return request({
      url: '/api/orders',
      method: 'get',
      params
    })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return request({
      url: `/api/orders/${id}`,
      method: 'get'
    })
  },
  
  // 取消订单
  cancelOrder(id, reason) {
    return request({
      url: `/api/orders/${id}/cancel`,
      method: 'put',
      data: { reason }
    })
  },
  
  // 确认收货
  confirmOrder(id) {
    return request({
      url: `/api/orders/${id}/confirm`,
      method: 'put'
    })
  },
  
  // 申请退款
  refundOrder(id, data) {
    return request({
      url: `/api/orders/${id}/refund`,
      method: 'put',
      data
    })
  }
}

// 地址相关API
export const addressApi = {
  // 获取地址列表
  getAddressList() {
    return request({
      url: '/api/addresses',
      method: 'get'
    })
  },
  
  // 添加地址
  addAddress(data) {
    return request({
      url: '/api/addresses',
      method: 'post',
      data
    })
  },
  
  // 更新地址
  updateAddress(id, data) {
    return request({
      url: `/api/addresses/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除地址
  deleteAddress(id) {
    return request({
      url: `/api/addresses/${id}`,
      method: 'delete'
    })
  },
  
  // 设置默认地址
  setDefaultAddress(id) {
    return request({
      url: `/api/addresses/${id}/default`,
      method: 'put'
    })
  }
}

// 公告相关API
export const announcementApi = {
  // 获取公告列表
  getAnnouncementList(params) {
    return request({
      url: '/api/announcements',
      method: 'get',
      params
    })
  },
  
  // 获取公告详情
  getAnnouncementDetail(id) {
    return request({
      url: `/api/announcements/${id}`,
      method: 'get'
    })
  }
}

// 管理员相关API
export const adminApi = {
  // 获取仪表板数据
  getDashboardData() {
    return request({
      url: '/api/admin/dashboard',
      method: 'get'
    })
  },
  
  // 用户管理
  getUserList(params) {
    return request({
      url: '/api/admin/users',
      method: 'get',
      params
    })
  },
  
  updateUserStatus(id, status) {
    return request({
      url: `/api/admin/users/${id}/status`,
      method: 'put',
      data: { status }
    })
  },
  
  deleteUser(id) {
    return request({
      url: `/api/admin/users/${id}`,
      method: 'delete'
    })
  },
  
  addAdmin(data) {
    return request({
      url: '/api/admin/users/admin',
      method: 'post',
      data
    })
  },
  
  // 商品管理
  getAdminProductList(params) {
    return request({
      url: '/api/admin/products',
      method: 'get',
      params
    })
  },
  
  approveProduct(id) {
    return request({
      url: `/api/admin/products/${id}/approve`,
      method: 'put'
    })
  },
  
  rejectProduct(id, reason) {
    return request({
      url: `/api/admin/products/${id}/reject`,
      method: 'put',
      data: { reason }
    })
  },
  
  updateProductStatus(id, status) {
    return request({
      url: `/api/admin/products/${id}/status`,
      method: 'put',
      data: { status }
    })
  },
  
  deleteAdminProduct(id) {
    return request({
      url: `/api/admin/products/${id}`,
      method: 'delete'
    })
  },
  
  // 订单管理
  getAdminOrderList(params) {
    return request({
      url: '/api/admin/orders',
      method: 'get',
      params
    })
  },
  
  cancelAdminOrder(id, reason) {
    return request({
      url: `/api/admin/orders/${id}/cancel`,
      method: 'put',
      data: { reason }
    })
  },
  
  processRefund(id, data) {
    return request({
      url: `/api/admin/orders/${id}/refund`,
      method: 'put',
      data
    })
  },
  
  exportOrders(params) {
    return request({
      url: '/api/admin/orders/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },
  
  // 分类管理
  getCategoryStats() {
    return request({
      url: '/api/admin/categories/stats',
      method: 'get'
    })
  },
  
  createCategory(data) {
    return request({
      url: '/api/admin/categories',
      method: 'post',
      data
    })
  },
  
  updateCategory(id, data) {
    return request({
      url: `/api/admin/categories/${id}`,
      method: 'put',
      data
    })
  },
  
  deleteCategory(id) {
    return request({
      url: `/api/admin/categories/${id}`,
      method: 'delete'
    })
  },
  
  updateCategoryStatus(id, status) {
    return request({
      url: `/api/admin/categories/${id}/status`,
      method: 'put',
      data: { status }
    })
  },
  
  updateCategorySort(id, direction) {
    return request({
      url: `/api/admin/categories/${id}/sort`,
      method: 'put',
      data: { direction }
    })
  },
  
  // 统计分析
  getCoreMetrics(params) {
    return request({
      url: '/api/admin/statistics/core-metrics',
      method: 'get',
      params
    })
  },
  
  getUserTrendData(params) {
    return request({
      url: '/api/admin/statistics/user-trend',
      method: 'get',
      params
    })
  },
  
  getOrderStatusData(params) {
    return request({
      url: '/api/admin/statistics/order-status',
      method: 'get',
      params
    })
  },
  
  getCategoryDistribution(params) {
    return request({
      url: '/api/admin/statistics/category-distribution',
      method: 'get',
      params
    })
  },
  
  getRevenueTrendData(params) {
    return request({
      url: '/api/admin/statistics/revenue-trend',
      method: 'get',
      params
    })
  },
  
  getUserStatistics(params) {
    return request({
      url: '/api/admin/statistics/users',
      method: 'get',
      params
    })
  },
  
  getProductStatistics(params) {
    return request({
      url: '/api/admin/statistics/products',
      method: 'get',
      params
    })
  },
  
  getOrderStatistics(params) {
    return request({
      url: '/api/admin/statistics/orders',
      method: 'get',
      params
    })
  },
  
  exportStatisticsData(params) {
    return request({
      url: '/api/admin/statistics/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  }
}

// 导出所有API
export default {
  userApi,
  productApi,
  categoryApi,
  orderApi,
  addressApi,
  announcementApi,
  adminApi
}