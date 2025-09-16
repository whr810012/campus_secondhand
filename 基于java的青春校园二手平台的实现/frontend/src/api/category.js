import axios from 'axios'

// 创建分类（管理员）
export function createCategory(data) {
  return axios({
    url: '/category/create',
    method: 'post',
    data
  })
}

// 获取分类列表（管理员）
export function getCategoryList(params) {
  return axios({
    url: '/category/admin/list',
    method: 'get',
    params
  })
}

// 获取启用的分类列表（用户端）
export function getEnabledCategories() {
  return axios({
    url: '/category/enabled',
    method: 'get'
  })
}

// 获取分类详情
export function getCategoryDetail(id) {
  return axios({
    url: `/category/${id}`,
    method: 'get'
  })
}

// 更新分类信息
export function updateCategory(id, data) {
  return axios({
    url: `/category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return axios({
    url: `/category/${id}`,
    method: 'delete'
  })
}

// 启用/禁用分类
export function toggleCategoryStatus(id, status) {
  return axios({
    url: `/category/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新分类排序
export function updateCategorySort(id, sort) {
  return axios({
    url: `/category/${id}/sort`,
    method: 'put',
    data: { sort }
  })
}

// 检查分类名称是否存在
export function checkCategoryName(name, id) {
  return axios({
    url: '/category/check-name',
    method: 'get',
    params: { name, id }
  })
}

// 检查分类是否有关联商品
export function checkCategoryProducts(id) {
  return axios({
    url: `/category/${id}/check-products`,
    method: 'get'
  })
}

// 获取分类统计信息
export function getCategoryStats() {
  return axios({
    url: '/category/stats',
    method: 'get'
  })
}

// 获取分类商品数量统计
export function getCategoryProductCount() {
  return axios({
    url: '/category/product-count',
    method: 'get'
  })
}