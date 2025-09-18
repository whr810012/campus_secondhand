import request from '@/api/request'

// 获取学校列表
export function getSchools() {
  return request({
    url: '/schools',
    method: 'get'
  })
}

// 获取学校详情
export function getSchoolById(id) {
  return request({
    url: `/schools/${id}`,
    method: 'get'
  })
}

// 搜索学校
export function searchSchools(keyword) {
  return request({
    url: '/schools/search',
    method: 'get',
    params: {
      name: keyword
    }
  })
}