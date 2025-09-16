import axios from 'axios'

// 创建公告（管理员）
export function createNotice(data) {
  return axios({
    url: '/notice/create',
    method: 'post',
    data
  })
}

// 获取公告列表（管理员）
export function getNoticeList(params) {
  return axios({
    url: '/notice/admin/list',
    method: 'get',
    params
  })
}

// 获取启用的公告列表（用户端）
export function getEnabledNotices(params) {
  return axios({
    url: '/notice/enabled',
    method: 'get',
    params
  })
}

// 获取公告详情
export function getNoticeDetail(id) {
  return axios({
    url: `/notice/${id}`,
    method: 'get'
  })
}

// 更新公告信息
export function updateNotice(id, data) {
  return axios({
    url: `/notice/${id}`,
    method: 'put',
    data
  })
}

// 删除公告
export function deleteNotice(id) {
  return axios({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

// 启用/禁用公告
export function toggleNoticeStatus(id, status) {
  return axios({
    url: `/notice/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新公告排序
export function updateNoticeSort(id, sort) {
  return axios({
    url: `/notice/${id}/sort`,
    method: 'put',
    data: { sort }
  })
}

// 获取公告统计信息
export function getNoticeStats() {
  return axios({
    url: '/notice/stats',
    method: 'get'
  })
}

// 获取最新公告
export function getLatestNotices(limit = 5) {
  return axios({
    url: '/notice/latest',
    method: 'get',
    params: { limit }
  })
}

// 获取重要公告
export function getImportantNotices() {
  return axios({
    url: '/notice/important',
    method: 'get'
  })
}

// 获取系统公告
export function getSystemNotices() {
  return axios({
    url: '/notice/system',
    method: 'get'
  })
}

// 批量删除公告
export function batchDeleteNotices(ids) {
  return axios({
    url: '/notice/batch-delete',
    method: 'delete',
    data: { ids }
  })
}