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

// 创建分类（管理员）
export const createCategory = (data) => {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

// 更新分类（管理员）
export const updateCategory = (id, data) => {
  return request({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

// 删除分类（管理员）
export const deleteCategory = (id) => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}