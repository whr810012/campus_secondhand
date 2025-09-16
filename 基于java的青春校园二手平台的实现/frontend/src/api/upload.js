import request from '@/utils/request'

// 上传单个文件
export function uploadFile(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/api/upload/file',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传多个文件
export function uploadMultipleFiles(files, onUploadProgress) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  
  return request({
    url: '/api/upload/files',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传图片
export function uploadImage(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('image', file)
  
  return request({
    url: '/api/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传头像
export function uploadAvatar(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('avatar', file)
  
  return request({
    url: '/api/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传商品图片
export function uploadProductImages(files, onUploadProgress) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('productImages', file)
  })
  
  return request({
    url: '/api/upload/product-images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 上传分类图标
export function uploadCategoryIcon(file, onUploadProgress) {
  const formData = new FormData()
  formData.append('categoryIcon', file)
  
  return request({
    url: '/api/upload/category-icon',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 删除文件
export function deleteFile(fileUrl) {
  return request({
    url: '/api/upload/delete',
    method: 'delete',
    data: { fileUrl }
  })
}

// 批量删除文件
export function batchDeleteFiles(fileUrls) {
  return request({
    url: '/api/upload/batch-delete',
    method: 'delete',
    data: { fileUrls }
  })
}

// 获取文件信息
export function getFileInfo(fileUrl) {
  return request({
    url: '/api/upload/file-info',
    method: 'get',
    params: { fileUrl }
  })
}

// 获取上传配置
export function getUploadConfig() {
  return request({
    url: '/api/upload/config',
    method: 'get'
  })
}

// 检查文件是否存在
export function checkFileExists(fileUrl) {
  return request({
    url: '/api/upload/check-exists',
    method: 'get',
    params: { fileUrl }
  })
}

// 获取文件上传token（用于直传OSS等）
export function getUploadToken(fileType) {
  return request({
    url: '/api/upload/token',
    method: 'get',
    params: { fileType }
  })
}

// 压缩图片
export function compressImage(file, quality = 0.8) {
  const formData = new FormData()
  formData.append('image', file)
  formData.append('quality', quality)
  
  return request({
    url: '/api/upload/compress-image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 生成缩略图
export function generateThumbnail(imageUrl, width, height) {
  return request({
    url: '/api/upload/thumbnail',
    method: 'post',
    data: { imageUrl, width, height }
  })
}

// 获取上传历史
export function getUploadHistory(params) {
  return request({
    url: '/api/upload/history',
    method: 'get',
    params
  })
}

// 清理临时文件
export function cleanTempFiles() {
  return request({
    url: '/api/upload/clean-temp',
    method: 'post'
  })
}