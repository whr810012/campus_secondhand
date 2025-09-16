import request from '@/utils/request'

// 获取仪表板数据
export function getDashboardData() {
  return request({
    url: '/api/admin/dashboard',
    method: 'get'
  })
}

// 用户管理
export function getUserList(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

export function getUserDetail(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'get'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/api/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  })
}

export function batchDeleteUsers(ids) {
  return request({
    url: '/api/admin/users/batch-delete',
    method: 'delete',
    data: { ids }
  })
}

export function addAdmin(data) {
  return request({
    url: '/api/admin/users/admin',
    method: 'post',
    data
  })
}

export function resetUserPassword(id) {
  return request({
    url: `/api/admin/users/${id}/reset-password`,
    method: 'put'
  })
}

// 商品管理
export function getAdminProductList(params) {
  return request({
    url: '/api/admin/products',
    method: 'get',
    params
  })
}

export function getAdminProductDetail(id) {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'get'
  })
}

export function approveProduct(id) {
  return request({
    url: `/api/admin/products/${id}/approve`,
    method: 'put'
  })
}

export function rejectProduct(id, reason) {
  return request({
    url: `/api/admin/products/${id}/reject`,
    method: 'put',
    data: { reason }
  })
}

export function updateProductStatus(id, status) {
  return request({
    url: `/api/admin/products/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function deleteAdminProduct(id) {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'delete'
  })
}

export function batchApproveProducts(ids) {
  return request({
    url: '/api/admin/products/batch-approve',
    method: 'put',
    data: { ids }
  })
}

export function batchRejectProducts(ids, reason) {
  return request({
    url: '/api/admin/products/batch-reject',
    method: 'put',
    data: { ids, reason }
  })
}

export function batchDeleteProducts(ids) {
  return request({
    url: '/api/admin/products/batch-delete',
    method: 'delete',
    data: { ids }
  })
}

// 订单管理
export function getAdminOrderList(params) {
  return request({
    url: '/api/admin/orders',
    method: 'get',
    params
  })
}

export function getAdminOrderDetail(id) {
  return request({
    url: `/api/admin/orders/${id}`,
    method: 'get'
  })
}

export function cancelAdminOrder(id, reason) {
  return request({
    url: `/api/admin/orders/${id}/cancel`,
    method: 'put',
    data: { reason }
  })
}

export function processRefund(id, data) {
  return request({
    url: `/api/admin/orders/${id}/refund`,
    method: 'put',
    data
  })
}

export function exportOrders(params) {
  return request({
    url: '/api/admin/orders/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function batchCancelOrders(ids, reason) {
  return request({
    url: '/api/admin/orders/batch-cancel',
    method: 'put',
    data: { ids, reason }
  })
}

// 分类管理
export function getCategoryStats() {
  return request({
    url: '/api/admin/categories/stats',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/api/admin/categories',
    method: 'post',
    data
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'delete'
  })
}

export function updateCategoryStatus(id, status) {
  return request({
    url: `/api/admin/categories/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function updateCategorySort(id, direction) {
  return request({
    url: `/api/admin/categories/${id}/sort`,
    method: 'put',
    data: { direction }
  })
}

// 系统设置
export function getSystemSettings() {
  return request({
    url: '/api/admin/settings',
    method: 'get'
  })
}

export function updateSystemSettings(data) {
  return request({
    url: '/api/admin/settings',
    method: 'put',
    data
  })
}

// 日志管理
export function getSystemLogs(params) {
  return request({
    url: '/api/admin/logs',
    method: 'get',
    params
  })
}

export function clearSystemLogs(params) {
  return request({
    url: '/api/admin/logs/clear',
    method: 'delete',
    data: params
  })
}

// 公告管理
export function getAdminAnnouncementList(params) {
  return request({
    url: '/api/admin/announcements',
    method: 'get',
    params
  })
}

export function createAnnouncement(data) {
  return request({
    url: '/api/admin/announcements',
    method: 'post',
    data
  })
}

export function updateAnnouncement(id, data) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'put',
    data
  })
}

export function deleteAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'delete'
  })
}

export function publishAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/publish`,
    method: 'put'
  })
}

export function unpublishAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/unpublish`,
    method: 'put'
  })
}