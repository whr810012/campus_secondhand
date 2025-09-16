import axios from 'axios'

// 获取平台概览统计
export function getPlatformOverview() {
  return axios({
    url: '/statistics/overview',
    method: 'get'
  })
}

// 获取用户统计数据
export function getUserStatistics(params) {
  return axios({
    url: '/statistics/users',
    method: 'get',
    params
  })
}

// 获取商品统计数据
export function getProductStatistics(params) {
  return axios({
    url: '/statistics/products',
    method: 'get',
    params
  })
}

// 获取订单统计数据
export function getOrderStatistics(params) {
  return axios({
    url: '/statistics/orders',
    method: 'get',
    params
  })
}

// 获取交易统计数据
export function getTransactionStatistics(params) {
  return axios({
    url: '/statistics/transactions',
    method: 'get',
    params
  })
}

// 获取分类统计数据
export function getCategoryStatistics() {
  return axios({
    url: '/statistics/categories',
    method: 'get'
  })
}

// 获取用户注册趋势
export function getUserRegistrationTrend(params) {
  return axios({
    url: '/statistics/user-registration-trend',
    method: 'get',
    params
  })
}

// 获取商品发布趋势
export function getProductPublishTrend(params) {
  return axios({
    url: '/statistics/product-publish-trend',
    method: 'get',
    params
  })
}

// 获取订单趋势
export function getOrderTrend(params) {
  return axios({
    url: '/statistics/order-trend',
    method: 'get',
    params
  })
}

// 获取交易金额趋势
export function getTransactionAmountTrend(params) {
  return axios({
    url: '/statistics/transaction-amount-trend',
    method: 'get',
    params
  })
}

// 获取热门分类排行
export function getHotCategoriesRanking(params) {
  return axios({
    url: '/statistics/hot-categories',
    method: 'get',
    params
  })
}

// 获取活跃用户排行
export function getActiveUsersRanking(params) {
  return axios({
    url: '/statistics/active-users',
    method: 'get',
    params
  })
}

// 获取热门商品排行
export function getHotProductsRanking(params) {
  return axios({
    url: '/statistics/hot-products',
    method: 'get',
    params
  })
}

// 获取地区分布统计
export function getRegionDistribution() {
  return axios({
    url: '/statistics/region-distribution',
    method: 'get'
  })
}

// 获取实时统计数据
export function getRealTimeStatistics() {
  return axios({
    url: '/statistics/realtime',
    method: 'get'
  })
}

// 导出统计报告
export function exportStatisticsReport(params) {
  return axios({
    url: '/statistics/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}