import axios from 'axios'

// 添加地址
export function addAddress(data) {
  return axios({
    url: '/address/add',
    method: 'post',
    data
  })
}

// 获取用户所有地址
export function getUserAddresses() {
  return axios({
    url: '/address/list',
    method: 'get'
  })
}

// 获取默认地址
export function getDefaultAddress() {
  return axios({
    url: '/address/default',
    method: 'get'
  })
}

// 获取地址详情
export function getAddressDetail(id) {
  return axios({
    url: `/address/${id}`,
    method: 'get'
  })
}

// 更新地址
export function updateAddress(id, data) {
  return axios({
    url: `/address/${id}`,
    method: 'put',
    data
  })
}

// 删除地址
export function deleteAddress(id) {
  return axios({
    url: `/address/${id}`,
    method: 'delete'
  })
}

// 设置默认地址
export function setDefaultAddress(id) {
  return axios({
    url: `/address/${id}/default`,
    method: 'put'
  })
}

// 获取地址数量
export function getAddressCount() {
  return axios({
    url: '/address/count',
    method: 'get'
  })
}

// 批量删除地址
export function batchDeleteAddresses(ids) {
  return axios({
    url: '/address/batch-delete',
    method: 'delete',
    data: { ids }
  })
}