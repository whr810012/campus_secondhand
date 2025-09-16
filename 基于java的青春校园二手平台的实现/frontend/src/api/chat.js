import request from '@/utils/request'

// 获取聊天列表
export function getChatList(params) {
  return request({
    url: '/api/chat/list',
    method: 'get',
    params
  })
}

// 获取聊天详情
export function getChatDetail(chatId) {
  return request({
    url: `/api/chat/${chatId}`,
    method: 'get'
  })
}

// 创建聊天
export function createChat(data) {
  return request({
    url: '/api/chat/create',
    method: 'post',
    data
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/api/chat/send-message',
    method: 'post',
    data
  })
}

// 获取消息历史
export function getMessageHistory(chatId, params) {
  return request({
    url: `/api/chat/${chatId}/messages`,
    method: 'get',
    params
  })
}

// 标记消息为已读
export function markMessageAsRead(messageId) {
  return request({
    url: `/api/chat/messages/${messageId}/read`,
    method: 'put'
  })
}

// 批量标记消息为已读
export function batchMarkMessagesAsRead(messageIds) {
  return request({
    url: '/api/chat/messages/batch-read',
    method: 'put',
    data: { messageIds }
  })
}

// 删除消息
export function deleteMessage(messageId) {
  return request({
    url: `/api/chat/messages/${messageId}`,
    method: 'delete'
  })
}

// 撤回消息
export function recallMessage(messageId) {
  return request({
    url: `/api/chat/messages/${messageId}/recall`,
    method: 'put'
  })
}

// 获取未读消息数量
export function getUnreadMessageCount() {
  return request({
    url: '/api/chat/unread-count',
    method: 'get'
  })
}

// 搜索消息
export function searchMessages(params) {
  return request({
    url: '/api/chat/search-messages',
    method: 'get',
    params
  })
}

// 上传聊天文件
export function uploadChatFile(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/api/chat/upload-file',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传聊天图片
export function uploadChatImage(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('image', file)
  
  return request({
    url: '/api/chat/upload-image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 删除聊天
export function deleteChat(chatId) {
  return request({
    url: `/api/chat/${chatId}`,
    method: 'delete'
  })
}

// 清空聊天记录
export function clearChatHistory(chatId) {
  return request({
    url: `/api/chat/${chatId}/clear`,
    method: 'delete'
  })
}

// 置顶聊天
export function pinChat(chatId) {
  return request({
    url: `/api/chat/${chatId}/pin`,
    method: 'put'
  })
}

// 取消置顶聊天
export function unpinChat(chatId) {
  return request({
    url: `/api/chat/${chatId}/unpin`,
    method: 'put'
  })
}

// 静音聊天
export function muteChat(chatId) {
  return request({
    url: `/api/chat/${chatId}/mute`,
    method: 'put'
  })
}

// 取消静音聊天
export function unmuteChat(chatId) {
  return request({
    url: `/api/chat/${chatId}/unmute`,
    method: 'put'
  })
}

// 拉黑用户
export function blockUser(userId) {
  return request({
    url: `/api/chat/block-user/${userId}`,
    method: 'post'
  })
}

// 取消拉黑用户
export function unblockUser(userId) {
  return request({
    url: `/api/chat/unblock-user/${userId}`,
    method: 'delete'
  })
}

// 获取黑名单列表
export function getBlockedUserList(params) {
  return request({
    url: '/api/chat/blocked-users',
    method: 'get',
    params
  })
}

// 举报消息
export function reportMessage(data) {
  return request({
    url: '/api/chat/report-message',
    method: 'post',
    data
  })
}

// 获取在线状态
export function getOnlineStatus(userIds) {
  return request({
    url: '/api/chat/online-status',
    method: 'post',
    data: { userIds }
  })
}

// 设置在线状态
export function setOnlineStatus(status) {
  return request({
    url: '/api/chat/set-online-status',
    method: 'put',
    data: { status }
  })
}

// 获取聊天设置
export function getChatSettings() {
  return request({
    url: '/api/chat/settings',
    method: 'get'
  })
}

// 更新聊天设置
export function updateChatSettings(data) {
  return request({
    url: '/api/chat/settings',
    method: 'put',
    data
  })
}

// 管理员相关API
// 获取所有聊天记录
export function getAdminChatList(params) {
  return request({
    url: '/api/admin/chat/list',
    method: 'get',
    params
  })
}

// 获取举报消息列表
export function getReportedMessages(params) {
  return request({
    url: '/api/admin/chat/reported-messages',
    method: 'get',
    params
  })
}

// 处理举报消息
export function processReportedMessage(reportId, data) {
  return request({
    url: `/api/admin/chat/reported-messages/${reportId}/process`,
    method: 'put',
    data
  })
}

// 删除违规消息
export function deleteViolationMessage(messageId) {
  return request({
    url: `/api/admin/chat/messages/${messageId}/delete`,
    method: 'delete'
  })
}

// 获取聊天统计
export function getChatStatistics(params) {
  return request({
    url: '/api/admin/chat/statistics',
    method: 'get',
    params
  })
}