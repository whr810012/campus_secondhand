import request from '@/utils/request'

// 创建支付订单
export function createPayment(data) {
  return request({
    url: '/api/payment/create',
    method: 'post',
    data
  })
}

// 获取支付方式列表
export function getPaymentMethods() {
  return request({
    url: '/api/payment/methods',
    method: 'get'
  })
}

// 支付宝支付
export function alipayPay(data) {
  return request({
    url: '/api/payment/alipay',
    method: 'post',
    data
  })
}

// 微信支付
export function wechatPay(data) {
  return request({
    url: '/api/payment/wechat',
    method: 'post',
    data
  })
}

// 银行卡支付
export function bankCardPay(data) {
  return request({
    url: '/api/payment/bank-card',
    method: 'post',
    data
  })
}

// 余额支付
export function balancePay(data) {
  return request({
    url: '/api/payment/balance',
    method: 'post',
    data
  })
}

// 查询支付状态
export function getPaymentStatus(paymentId) {
  return request({
    url: `/api/payment/${paymentId}/status`,
    method: 'get'
  })
}

// 取消支付
export function cancelPayment(paymentId) {
  return request({
    url: `/api/payment/${paymentId}/cancel`,
    method: 'post'
  })
}

// 申请退款
export function requestRefund(data) {
  return request({
    url: '/api/payment/refund',
    method: 'post',
    data
  })
}

// 查询退款状态
export function getRefundStatus(refundId) {
  return request({
    url: `/api/payment/refund/${refundId}/status`,
    method: 'get'
  })
}

// 获取支付记录
export function getPaymentHistory(params) {
  return request({
    url: '/api/payment/history',
    method: 'get',
    params
  })
}

// 获取退款记录
export function getRefundHistory(params) {
  return request({
    url: '/api/payment/refund-history',
    method: 'get',
    params
  })
}

// 获取用户余额
export function getUserBalance() {
  return request({
    url: '/api/payment/balance',
    method: 'get'
  })
}

// 充值余额
export function rechargeBalance(data) {
  return request({
    url: '/api/payment/recharge',
    method: 'post',
    data
  })
}

// 提现申请
export function withdrawBalance(data) {
  return request({
    url: '/api/payment/withdraw',
    method: 'post',
    data
  })
}

// 获取提现记录
export function getWithdrawHistory(params) {
  return request({
    url: '/api/payment/withdraw-history',
    method: 'get',
    params
  })
}

// 绑定银行卡
export function bindBankCard(data) {
  return request({
    url: '/api/payment/bind-card',
    method: 'post',
    data
  })
}

// 解绑银行卡
export function unbindBankCard(cardId) {
  return request({
    url: `/api/payment/unbind-card/${cardId}`,
    method: 'delete'
  })
}

// 获取绑定的银行卡列表
export function getBankCardList() {
  return request({
    url: '/api/payment/bank-cards',
    method: 'get'
  })
}

// 设置默认支付方式
export function setDefaultPaymentMethod(methodId) {
  return request({
    url: '/api/payment/default-method',
    method: 'put',
    data: { methodId }
  })
}

// 获取支付配置
export function getPaymentConfig() {
  return request({
    url: '/api/payment/config',
    method: 'get'
  })
}

// 验证支付密码
export function verifyPaymentPassword(password) {
  return request({
    url: '/api/payment/verify-password',
    method: 'post',
    data: { password }
  })
}

// 设置支付密码
export function setPaymentPassword(data) {
  return request({
    url: '/api/payment/set-password',
    method: 'post',
    data
  })
}

// 修改支付密码
export function changePaymentPassword(data) {
  return request({
    url: '/api/payment/change-password',
    method: 'put',
    data
  })
}

// 重置支付密码
export function resetPaymentPassword(data) {
  return request({
    url: '/api/payment/reset-password',
    method: 'post',
    data
  })
}

// 获取交易流水
export function getTransactionHistory(params) {
  return request({
    url: '/api/payment/transactions',
    method: 'get',
    params
  })
}

// 导出交易记录
export function exportTransactionHistory(params) {
  return request({
    url: '/api/payment/export-transactions',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 管理员相关API
// 获取所有支付记录
export function getAdminPaymentList(params) {
  return request({
    url: '/api/admin/payment/list',
    method: 'get',
    params
  })
}

// 处理退款申请
export function processRefund(refundId, data) {
  return request({
    url: `/api/admin/payment/refund/${refundId}/process`,
    method: 'put',
    data
  })
}

// 处理提现申请
export function processWithdraw(withdrawId, data) {
  return request({
    url: `/api/admin/payment/withdraw/${withdrawId}/process`,
    method: 'put',
    data
  })
}

// 获取支付统计
export function getPaymentStatistics(params) {
  return request({
    url: '/api/admin/payment/statistics',
    method: 'get',
    params
  })
}