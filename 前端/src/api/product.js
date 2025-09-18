import request from './request'

// 分页查询商品列表
export const getProducts = (params) => {
  return request({
    url: '/products',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

// 发布商品
export const createProduct = (data) => {
  return request({
    url: '/products',
    method: 'post',
    data
  })
}

// 更新商品信息
export const updateProduct = (id, data) => {
  return request({
    url: `/products/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id, userId) => {
  return request({
    url: `/products/${id}`,
    method: 'delete',
    params: { userId }
  })
}

// 获取用户发布的商品
export const getUserProducts = (userId, params) => {
  return request({
    url: `/products/user/${userId}`,
    method: 'get',
    params
  })
}

// 商品置顶
export const topProduct = (id, data) => {
  return request({
    url: `/products/${id}/top`,
    method: 'post',
    data
  })
}

// 增加商品浏览量
export const addProductView = (id) => {
  return request({
    url: `/products/${id}/view`,
    method: 'post'
  })
}

// 获取分类列表
export const getCategories = () => {
  return request({
    url: '/categories',
    method: 'get'
  })
}

// 获取分类树结构
export const getCategoryTree = () => {
  return request({
    url: '/categories/tree',
    method: 'get'
  })
}

// 根据父分类ID获取子分类
export const getCategoriesByParent = (parentId) => {
  return request({
    url: `/categories/parent/${parentId}`,
    method: 'get'
  })
}