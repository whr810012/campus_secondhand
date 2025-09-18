import Cookies from 'js-cookie'

const TOKEN_KEY = 'campus_secondhand_token'

// 获取token
export const getToken = () => {
  return Cookies.get(TOKEN_KEY)
}

// 设置token
export const setToken = (token) => {
  return Cookies.set(TOKEN_KEY, token, { expires: 7 }) // 7天过期
}

// 移除token
export const removeToken = () => {
  return Cookies.remove(TOKEN_KEY)
}