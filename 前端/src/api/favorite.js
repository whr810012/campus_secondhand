import request from './request'

// 添加收藏
export const addToFavorites = (productId) => {
  return request({
    url: '/favorites',
    method: 'post',
    data: { productId }
  })
}

// 取消收藏
export const removeFromFavorites = (productId) => {
  return request({
    url: '/favorites',
    method: 'delete',
    params: { productId }
  })
}

// 获取用户收藏列表
export const getUserFavorites = (params) => {
  return request({
    url: '/favorites/user/' + (params?.userId || 'me'),
    method: 'get',
    params
  })
}

// 删除收藏（通过收藏ID）
export const removeFavorite = (favoriteId) => {
  return request({
    url: `/favorites/${favoriteId}`,
    method: 'delete'
  })
}

// 批量删除收藏
export const batchRemoveFavorites = (favoriteIds) => {
  return request({
    url: '/favorites/batch',
    method: 'delete',
    data: { favoriteIds }
  })
}

// 检查是否已收藏
export const checkFavorite = (productId) => {
  return request({
    url: '/favorites/check',
    method: 'get',
    params: { productId }
  })
}

// 获取商品收藏数
export const getProductFavoriteCount = (productId) => {
  return request({
    url: `/favorites/product/${productId}/count`,
    method: 'get'
  })
}

// 获取用户收藏数
export const getUserFavoriteCount = (userId) => {
  return request({
    url: `/favorites/user/${userId}/count`,
    method: 'get'
  })
}

// 获取当前用户收藏列表
export const getMyFavorites = (params = {}) => {
  return request({
    url: '/favorites/user/me',
    method: 'get',
    params
  })
}