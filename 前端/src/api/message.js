import request from './request'

// 发送消息
export const sendMessage = (data) => {
  return request({
    url: '/messages',
    method: 'post',
    data
  })
}

// 分页查询聊天记录
export const getChatMessages = (params) => {
  return request({
    url: '/messages/chat',
    method: 'get',
    params
  })
}

// 获取用户聊天列表
export const getChatList = (userId, params) => {
  return request({
    url: `/messages/chats/${userId}`,
    method: 'get',
    params
  })
}

// 标记消息为已读
export const markMessageAsRead = (messageId, userId) => {
  return request({
    url: `/messages/${messageId}/read`,
    method: 'put',
    params: { userId }
  })
}

// 批量标记聊天消息为已读
export const markChatAsRead = (data) => {
  return request({
    url: '/messages/chat/read',
    method: 'put',
    data
  })
}

// 获取未读消息数量
export const getUnreadCount = (userId) => {
  return request({
    url: `/messages/unread/${userId}`,
    method: 'get'
  })
}

// 删除消息
export const deleteMessage = (messageId, userId) => {
  return request({
    url: `/messages/${messageId}`,
    method: 'delete',
    params: { userId }
  })
}

// 清空所有消息
export const clearAllMessages = (userId) => {
  return request({
    url: `/messages/clear/${userId}`,
    method: 'delete'
  })
}

// 获取用户消息列表
export const getUserMessages = (params) => {
  return request({
    url: '/messages/user',
    method: 'get',
    params
  })
}

// 标记所有消息为已读
export const markAllMessagesAsRead = (userId) => {
  return request({
    url: `/messages/read-all/${userId}`,
    method: 'put'
  })
}