import request from '@/utils/request'

// 获取公告列表
export function getAnnouncementList(params) {
  return request({
    url: '/api/announcements',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'get'
  })
}

// 获取最新公告
export function getLatestAnnouncements(params) {
  return request({
    url: '/api/announcements/latest',
    method: 'get',
    params
  })
}

// 获取重要公告
export function getImportantAnnouncements(params) {
  return request({
    url: '/api/announcements/important',
    method: 'get',
    params
  })
}

// 标记公告为已读
export function markAnnouncementAsRead(id) {
  return request({
    url: `/api/announcements/${id}/read`,
    method: 'post'
  })
}

// 获取未读公告数量
export function getUnreadAnnouncementCount() {
  return request({
    url: '/api/announcements/unread-count',
    method: 'get'
  })
}

// 搜索公告
export function searchAnnouncements(params) {
  return request({
    url: '/api/announcements/search',
    method: 'get',
    params
  })
}

// 管理员相关API
// 获取管理员公告列表
export function getAdminAnnouncementList(params) {
  return request({
    url: '/api/admin/announcements',
    method: 'get',
    params
  })
}

// 创建公告
export function createAnnouncement(data) {
  return request({
    url: '/api/admin/announcements',
    method: 'post',
    data
  })
}

// 更新公告
export function updateAnnouncement(id, data) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'put',
    data
  })
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}`,
    method: 'delete'
  })
}

// 发布公告
export function publishAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/publish`,
    method: 'put'
  })
}

// 取消发布公告
export function unpublishAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/unpublish`,
    method: 'put'
  })
}

// 置顶公告
export function pinAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/pin`,
    method: 'put'
  })
}

// 取消置顶公告
export function unpinAnnouncement(id) {
  return request({
    url: `/api/admin/announcements/${id}/unpin`,
    method: 'put'
  })
}

// 批量删除公告
export function batchDeleteAnnouncements(ids) {
  return request({
    url: '/api/admin/announcements/batch-delete',
    method: 'delete',
    data: { ids }
  })
}

// 批量发布公告
export function batchPublishAnnouncements(ids) {
  return request({
    url: '/api/admin/announcements/batch-publish',
    method: 'put',
    data: { ids }
  })
}

// 获取公告统计
export function getAnnouncementStats() {
  return request({
    url: '/api/admin/announcements/stats',
    method: 'get'
  })
}

// 获取公告阅读统计
export function getAnnouncementReadStats(id) {
  return request({
    url: `/api/admin/announcements/${id}/read-stats`,
    method: 'get'
  })
}