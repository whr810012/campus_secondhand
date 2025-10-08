import request from './request'

// 公告管理API（管理员专用）

/**
 * 分页查询公告列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.type - 公告类型
 * @param {number} params.status - 公告状态
 */
export function getAnnouncementPage(params) {
  return request({
    url: '/api/admin/announcements',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取公告详情
 * @param {number} id - 公告ID
 */
export function getAnnouncementById(id) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'get'
  })
}

/**
 * 创建公告
 * @param {Object} data - 公告数据
 */
export function createAnnouncement(data) {
  return request({
    url: '/api/admin/announcements',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 * @param {number} id - 公告ID
 * @param {Object} data - 公告数据
 */
export function updateAnnouncement(id, data) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告
 * @param {number} id - 公告ID
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除公告
 * @param {Array} ids - 公告ID数组
 */
export function batchDeleteAnnouncements(ids) {
  return request({
    url: '/api/admin/announcements/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 发布公告
 * @param {number} id - 公告ID
 */
export function publishAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/publish`,
    method: 'put'
  })
}

/**
 * 下线公告
 * @param {number} id - 公告ID
 */
export function offlineAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/offline`,
    method: 'put'
  })
}

/**
 * 批量发布公告
 * @param {Array} ids - 公告ID数组
 */
export function batchPublishAnnouncements(ids) {
  return request({
    url: '/api/admin/announcements/batch/publish',
    method: 'put',
    data: ids
  })
}

/**
 * 批量下线公告
 * @param {Array} ids - 公告ID数组
 */
export function batchOfflineAnnouncements(ids) {
  return request({
    url: '/api/admin/announcements/batch/offline',
    method: 'put',
    data: ids
  })
}

/**
 * 增加浏览次数
 * @param {number} id - 公告ID
 */
export function incrementViewCount(id) {
  return request({
    url: `/api/admin/announcements/${id}/view`,
    method: 'put'
  })
}

/**
 * 获取公告统计信息
 */
export function getAnnouncementStats() {
  return request({
    url: '/api/admin/announcements/stats',
    method: 'get'
  })
}

// 用户端公告API（公开接口）

/**
 * 获取已发布的公告列表（用户端）
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页大小
 * @param {string} params.type - 公告类型
 */
export function getPublishedAnnouncements(params) {
  return request({
    url: '/announcements',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取公告详情（用户端）
 * @param {number} id - 公告ID
 */
export function getPublishedAnnouncementById(id) {
  return request({
    url: `/announcements/${id}`,
    method: 'get'
  })
}