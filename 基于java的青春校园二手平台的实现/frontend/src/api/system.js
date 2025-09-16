import request from '@/utils/request'

// 获取系统配置
export function getSystemConfig() {
  return request({
    url: '/api/system/config',
    method: 'get'
  })
}

// 更新系统配置
export function updateSystemConfig(data) {
  return request({
    url: '/api/admin/system/config',
    method: 'put',
    data
  })
}

// 获取系统信息
export function getSystemInfo() {
  return request({
    url: '/api/admin/system/info',
    method: 'get'
  })
}

// 获取系统状态
export function getSystemStatus() {
  return request({
    url: '/api/admin/system/status',
    method: 'get'
  })
}

// 获取系统日志
export function getSystemLogs(params) {
  return request({
    url: '/api/admin/system/logs',
    method: 'get',
    params
  })
}

// 清理系统日志
export function clearSystemLogs(params) {
  return request({
    url: '/api/admin/system/logs/clear',
    method: 'delete',
    data: params
  })
}

// 导出系统日志
export function exportSystemLogs(params) {
  return request({
    url: '/api/admin/system/logs/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取操作日志
export function getOperationLogs(params) {
  return request({
    url: '/api/admin/system/operation-logs',
    method: 'get',
    params
  })
}

// 获取登录日志
export function getLoginLogs(params) {
  return request({
    url: '/api/admin/system/login-logs',
    method: 'get',
    params
  })
}

// 获取错误日志
export function getErrorLogs(params) {
  return request({
    url: '/api/admin/system/error-logs',
    method: 'get',
    params
  })
}

// 系统备份
export function createSystemBackup() {
  return request({
    url: '/api/admin/system/backup',
    method: 'post'
  })
}

// 获取备份列表
export function getBackupList(params) {
  return request({
    url: '/api/admin/system/backups',
    method: 'get',
    params
  })
}

// 恢复系统备份
export function restoreSystemBackup(backupId) {
  return request({
    url: `/api/admin/system/backups/${backupId}/restore`,
    method: 'post'
  })
}

// 删除系统备份
export function deleteSystemBackup(backupId) {
  return request({
    url: `/api/admin/system/backups/${backupId}`,
    method: 'delete'
  })
}

// 获取缓存信息
export function getCacheInfo() {
  return request({
    url: '/api/admin/system/cache/info',
    method: 'get'
  })
}

// 清理缓存
export function clearCache(cacheType) {
  return request({
    url: '/api/admin/system/cache/clear',
    method: 'delete',
    data: { cacheType }
  })
}

// 获取数据库信息
export function getDatabaseInfo() {
  return request({
    url: '/api/admin/system/database/info',
    method: 'get'
  })
}

// 优化数据库
export function optimizeDatabase() {
  return request({
    url: '/api/admin/system/database/optimize',
    method: 'post'
  })
}

// 获取服务器信息
export function getServerInfo() {
  return request({
    url: '/api/admin/system/server/info',
    method: 'get'
  })
}

// 获取系统监控数据
export function getSystemMonitor() {
  return request({
    url: '/api/admin/system/monitor',
    method: 'get'
  })
}

// 发送测试邮件
export function sendTestEmail(email) {
  return request({
    url: '/api/admin/system/test-email',
    method: 'post',
    data: { email }
  })
}

// 发送测试短信
export function sendTestSMS(phone) {
  return request({
    url: '/api/admin/system/test-sms',
    method: 'post',
    data: { phone }
  })
}

// 获取邮件配置
export function getEmailConfig() {
  return request({
    url: '/api/admin/system/email-config',
    method: 'get'
  })
}

// 更新邮件配置
export function updateEmailConfig(data) {
  return request({
    url: '/api/admin/system/email-config',
    method: 'put',
    data
  })
}

// 获取短信配置
export function getSMSConfig() {
  return request({
    url: '/api/admin/system/sms-config',
    method: 'get'
  })
}

// 更新短信配置
export function updateSMSConfig(data) {
  return request({
    url: '/api/admin/system/sms-config',
    method: 'put',
    data
  })
}

// 获取支付配置
export function getPaymentConfig() {
  return request({
    url: '/api/admin/system/payment-config',
    method: 'get'
  })
}

// 更新支付配置
export function updatePaymentConfig(data) {
  return request({
    url: '/api/admin/system/payment-config',
    method: 'put',
    data
  })
}

// 获取存储配置
export function getStorageConfig() {
  return request({
    url: '/api/admin/system/storage-config',
    method: 'get'
  })
}

// 更新存储配置
export function updateStorageConfig(data) {
  return request({
    url: '/api/admin/system/storage-config',
    method: 'put',
    data
  })
}

// 重启系统服务
export function restartSystemService(serviceName) {
  return request({
    url: '/api/admin/system/restart-service',
    method: 'post',
    data: { serviceName }
  })
}

// 获取系统版本信息
export function getVersionInfo() {
  return request({
    url: '/api/system/version',
    method: 'get'
  })
}

// 检查系统更新
export function checkSystemUpdate() {
  return request({
    url: '/api/admin/system/check-update',
    method: 'get'
  })
}