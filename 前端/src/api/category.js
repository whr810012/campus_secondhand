import request from './request'

// 获取分类列表
export const getCategories = () => {
  return request({
    url: '/categories',
    method: 'get'
  })
}

// 获取分类详情
export const getCategoryById = (id) => {
  return request({
    url: `/categories/${id}`,
    method: 'get'
  })
}

// 管理员分类API

// 分页获取分类列表（管理员）
export const getCategoriesPage = (params) => {
  return request({
    url: '/admin/categories',
    method: 'get',
    params
  })
}

// 获取所有分类（管理员）
export const getAllCategories = () => {
  return request({
    url: '/admin/categories/all',
    method: 'get'
  })
}

// 获取分类树结构（管理员）
export const getCategoryTree = () => {
  return request({
    url: '/admin/categories/tree',
    method: 'get'
  })
}

// 创建分类（管理员）
export const createCategory = (data) => {
  return request({
    url: '/admin/categories',
    method: 'post',
    data
  })
}

// 更新分类（管理员）
export const updateCategory = (id, data) => {
  return request({
    url: `/admin/categories/${id}`,
    method: 'put',
    data
  })
}

// 删除分类（管理员）
export const deleteCategory = (id) => {
  return request({
    url: `/admin/categories/${id}`,
    method: 'delete'
  })
}

// 批量删除分类（管理员）
export const batchDeleteCategories = (ids) => {
  return request({
    url: '/admin/categories/batch',
    method: 'delete',
    data: ids
  })
}

// 更新分类状态（管理员）
export const updateCategoryStatus = (id, status) => {
  return request({
    url: `/admin/categories/${id}/status`,
    method: 'put',
    params: { status }
  })
}