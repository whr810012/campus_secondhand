import request from '@/utils/request'

// 获取通知列表
export function getNotificationList(params) {
  return request({
    url: '/api/notifications',
    method: 'get',
    params
  })
}

// 获取通知详情
export function getNotificationDetail(id) {
  return request({
    url: `/api/notifications/${id}`,
    method: 'get'
  })
}

// 标记通知为已读
export function markNotificationAsRead(id) {
  return request({
    url: `/api/notifications/${id}/read`,
    method: 'put'
  })
}

// 批量标记通知为已读
export function batchMarkNotificationsAsRead(ids) {
  return request({
    url: '/api/notifications/batch-read',
    method: 'put',
    data: { ids }
  })
}

// 标记所有通知为已读
export function markAllNotificationsAsRead() {
  return request({
    url: '/api/notifications/read-all',
    method: 'put'
  })
}

// 删除通知
export function deleteNotification(id) {
  return request({
    url: `/api/notifications/${id}`,
    method: 'delete'
  })
}

// 批量删除通知
export function batchDeleteNotifications(ids) {
  return request({
    url: '/api/notifications/batch-delete',
    method: 'delete',
    data: { ids }
  })
}

// 清空所有通知
export function clearAllNotifications() {
  return request({
    url: '/api/notifications/clear-all',
    method: 'delete'
  })
}

// 获取未读通知数量
export function getUnreadNotificationCount() {
  return request({
    url: '/api/notifications/unread-count',
    method: 'get'
  })
}

// 获取通知统计
export function getNotificationStats() {
  return request({
    url: '/api/notifications/stats',
    method: 'get'
  })
}

// 获取通知设置
export function getNotificationSettings() {
  return request({
    url: '/api/notifications/settings',
    method: 'get'
  })
}

// 更新通知设置
export function updateNotificationSettings(data) {
  return request({
    url: '/api/notifications/settings',
    method: 'put',
    data
  })
}

// 订阅通知类型
export function subscribeNotificationType(type) {
  return request({
    url: '/api/notifications/subscribe',
    method: 'post',
    data: { type }
  })
}

// 取消订阅通知类型
export function unsubscribeNotificationType(type) {
  return request({
    url: '/api/notifications/unsubscribe',
    method: 'post',
    data: { type }
  })
}

// 获取订阅的通知类型
export function getSubscribedNotificationTypes() {
  return request({
    url: '/api/notifications/subscribed-types',
    method: 'get'
  })
}

// 测试推送通知
export function testPushNotification(data) {
  return request({
    url: '/api/notifications/test-push',
    method: 'post',
    data
  })
}

// 获取系统通知
export function getSystemNotifications(params) {
  return request({
    url: '/api/notifications/system',
    method: 'get',
    params
  })
}

// 获取订单通知
export function getOrderNotifications(params) {
  return request({
    url: '/api/notifications/orders',
    method: 'get',
    params
  })
}

// 获取商品通知
export function getProductNotifications(params) {
  return request({
    url: '/api/notifications/products',
    method: 'get',
    params
  })
}

// 获取聊天通知
export function getChatNotifications(params) {
  return request({
    url: '/api/notifications/chat',
    method: 'get',
    params
  })
}

// 获取关注通知
export function getFollowNotifications(params) {
  return request({
    url: '/api/notifications/follow',
    method: 'get',
    params
  })
}

// 管理员相关API
// 发送系统通知
export function sendSystemNotification(data) {
  return request({
    url: '/api/admin/notifications/system/send',
    method: 'post',
    data
  })
}

// 发送批量通知
export function sendBatchNotification(data) {
  return request({
    url: '/api/admin/notifications/batch-send',
    method: 'post',
    data
  })
}

// 获取所有用户通知
export function getAdminNotificationList(params) {
  return request({
    url: '/api/admin/notifications/list',
    method: 'get',
    params
  })
}

// 获取通知模板列表
export function getNotificationTemplates(params) {
  return request({
    url: '/api/admin/notifications/templates',
    method: 'get',
    params
  })
}

// 创建通知模板
export function createNotificationTemplate(data) {
  return request({
    url: '/api/admin/notifications/templates',
    method: 'post',
    data
  })
}

// 更新通知模板
export function updateNotificationTemplate(id, data) {
  return request({
    url: `/api/admin/notifications/templates/${id}`,
    method: 'put',
    data
  })
}

// 删除通知模板
export function deleteNotificationTemplate(id) {
  return request({
    url: `/api/admin/notifications/templates/${id}`,
    method: 'delete'
  })
}

// 获取推送统计
export function getPushStatistics(params) {
  return request({
    url: '/api/admin/notifications/push-statistics',
    method: 'get',
    params
  })
}

// 获取通知发送记录
export function getNotificationSendHistory(params) {
  return request({
    url: '/api/admin/notifications/send-history',
    method: 'get',
    params
  })
}

// 重新发送失败的通知
export function resendFailedNotifications(ids) {
  return request({
    url: '/api/admin/notifications/resend-failed',
    method: 'post',
    data: { ids }
  })
}

// 获取推送设备列表
export function getPushDeviceList(params) {
  return request({
    url: '/api/admin/notifications/push-devices',
    method: 'get',
    params
  })
}

// 删除推送设备
export function deletePushDevice(deviceId) {
  return request({
    url: `/api/admin/notifications/push-devices/${deviceId}`,
    method: 'delete'
  })
}