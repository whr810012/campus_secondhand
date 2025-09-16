import axios from 'axios'

// 发布商品
export function publishProduct(data) {
  return axios({
    url: '/product/publish',
    method: 'post',
    data
  })
}

// 获取商品列表（用户端）
export function getProductList(params) {
  return axios({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取商品列表（管理员端）
export function getProductListAdmin(params) {
  return axios({
    url: '/product/admin/list',
    method: 'get',
    params
  })
}

// 获取我的商品
export function getMyProducts(params) {
  return axios({
    url: '/product/my',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductDetail(id) {
  return axios({
    url: `/product/${id}`,
    method: 'get'
  })
}

// 更新商品信息
export function updateProduct(id, data) {
  return axios({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return axios({
    url: `/product/${id}`,
    method: 'delete'
  })
}

// 上架/下架商品
export function toggleProductStatus(id, status) {
  return axios({
    url: `/product/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 审核商品（管理员）
export function auditProduct(id, status, reason) {
  return axios({
    url: `/product/${id}/audit`,
    method: 'put',
    data: { status, reason }
  })
}

// 增加浏览次数
export function increaseViewCount(id) {
  return axios({
    url: `/product/${id}/view`,
    method: 'post'
  })
}

// 点赞/取消点赞
export function toggleLike(id) {
  return axios({
    url: `/product/${id}/like`,
    method: 'post'
  })
}

// 标记为已售出
export function markAsSold(id) {
  return axios({
    url: `/product/${id}/sold`,
    method: 'put'
  })
}

// 获取热门商品
export function getHotProducts(params) {
  return axios({
    url: '/product/hot',
    method: 'get',
    params
  })
}

// 获取最新商品
export function getLatestProducts(params) {
  return axios({
    url: '/product/latest',
    method: 'get',
    params
  })
}

// 获取推荐商品
export function getRecommendedProducts(params) {
  return axios({
    url: '/product/recommended',
    method: 'get',
    params
  })
}

// 搜索商品
export function searchProducts(params) {
  return axios({
    url: '/product/search',
    method: 'get',
    params
  })
}

// 获取商品统计信息（管理员）
export function getProductStats() {
  return axios({
    url: '/product/stats',
    method: 'get'
  })
}

// 上传商品图片
export function uploadProductImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return axios({
    url: '/file/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 批量上传商品图片
export function uploadProductImages(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  
  return axios({
    url: '/product/upload/images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 商品上架
export function onlineProduct(id) {
  return axios({
    url: `/product/${id}/online`,
    method: 'put'
  })
}

// 商品下架
export function offlineProduct(id) {
  return axios({
    url: `/product/${id}/offline`,
    method: 'put'
  })
}