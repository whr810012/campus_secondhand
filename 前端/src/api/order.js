import request from '@/api/request'

// 获取用户订单列表
export const getUserOrders = (params) => {
  return request({
    url: '/orders/my',
    method: 'get',
    params
  })
}

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 获取订单详情
export const getOrderById = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 支付订单
export const payOrder = (id, data) => {
  return request({
    url: `/orders/${id}/pay`,
    method: 'post',
    data
  })
}

// 确认收货
export const confirmReceipt = (id, userId) => {
  return request({
    url: `/orders/${id}/confirm`,
    method: 'post',
    params: { userId }
  })
}

// 取消订单
export const cancelOrder = (id, data) => {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'post',
    data
  })
}

// 发货
export const shipOrder = (id, sellerId) => {
  return request({
    url: `/orders/${id}/ship`,
    method: 'post',
    params: { sellerId }
  })
}

// 申请退款
export const requestRefund = (id, data) => {
  return request({
    url: `/orders/${id}/refund`,
    method: 'post',
    data
  })
}

// 评价订单
export const reviewOrder = (id, data) => {
  return request({
    url: `/orders/${id}/review`,
    method: 'post',
    data
  })
}

// 管理员获取所有订单
export const getAllOrders = (params = {}) => {
  return request({
    url: '/orders/admin',
    method: 'get',
    params
  })
}

// 删除订单
export const deleteOrder = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'delete'
  })
}