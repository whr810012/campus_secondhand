import axios from 'axios'

// 创建订单
export function createOrder(data) {
  return axios({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 获取订单列表（管理员）
export function getOrderList(params) {
  return axios({
    url: '/order/admin/list',
    method: 'get',
    params
  })
}

// 获取我的订单
export function getMyOrders(params) {
  return axios({
    url: '/order/my',
    method: 'get',
    params
  })
}

// 根据订单号查询
export function getOrderByNumber(orderNumber) {
  return axios({
    url: `/order/number/${orderNumber}`,
    method: 'get'
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return axios({
    url: `/order/${id}`,
    method: 'get'
  })
}

// 确认订单
export function confirmOrder(id) {
  return axios({
    url: `/order/${id}/confirm`,
    method: 'put'
  })
}

// 取消订单
export function cancelOrder(id, reason) {
  return axios({
    url: `/order/${id}/cancel`,
    method: 'put',
    data: { reason }
  })
}

// 开始交易
export function startTransaction(id) {
  return axios({
    url: `/order/${id}/start`,
    method: 'put'
  })
}

// 完成订单
export function completeOrder(id) {
  return axios({
    url: `/order/${id}/complete`,
    method: 'put'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return axios({
    url: `/order/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 删除订单
export function deleteOrder(id) {
  return axios({
    url: `/order/${id}`,
    method: 'delete'
  })
}

// 获取订单统计信息（管理员）
export function getOrderStats() {
  return axios({
    url: '/order/stats',
    method: 'get'
  })
}

// 获取用户订单统计
export function getUserOrderStats() {
  return axios({
    url: '/order/user/stats',
    method: 'get'
  })
}

// 获取最近7天订单统计
export function getRecentOrderStats() {
  return axios({
    url: '/order/stats/recent',
    method: 'get'
  })
}

// 获取交易金额统计
export function getTransactionStats() {
  return axios({
    url: '/order/stats/transaction',
    method: 'get'
  })
}