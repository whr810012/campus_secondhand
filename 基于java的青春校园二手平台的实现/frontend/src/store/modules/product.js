import { getProductList, getProductDetail, searchProducts, getMyProducts } from '@/api/product'

const state = {
  productList: [],
  myProducts: [],
  currentProduct: null,
  searchResults: [],
  pagination: {
    current: 1,
    size: 12,
    total: 0
  },
  searchKeyword: '',
  selectedCategory: null,
  sortBy: 'createTime',
  sortOrder: 'desc'
}

const mutations = {
  SET_PRODUCT_LIST(state, { list, pagination }) {
    state.productList = list
    state.pagination = { ...state.pagination, ...pagination }
  },
  SET_MY_PRODUCTS(state, products) {
    state.myProducts = products
  },
  SET_CURRENT_PRODUCT(state, product) {
    state.currentProduct = product
  },
  SET_SEARCH_RESULTS(state, { list, pagination }) {
    state.searchResults = list
    state.pagination = { ...state.pagination, ...pagination }
  },
  SET_SEARCH_KEYWORD(state, keyword) {
    state.searchKeyword = keyword
  },
  SET_SELECTED_CATEGORY(state, category) {
    state.selectedCategory = category
  },
  SET_SORT_OPTIONS(state, { sortBy, sortOrder }) {
    state.sortBy = sortBy
    state.sortOrder = sortOrder
  },
  UPDATE_PRODUCT_IN_LIST(state, updatedProduct) {
    const index = state.productList.findIndex(p => p.id === updatedProduct.id)
    if (index !== -1) {
      state.productList.splice(index, 1, updatedProduct)
    }
    const myIndex = state.myProducts.findIndex(p => p.id === updatedProduct.id)
    if (myIndex !== -1) {
      state.myProducts.splice(myIndex, 1, updatedProduct)
    }
  },
  REMOVE_PRODUCT_FROM_LIST(state, productId) {
    state.productList = state.productList.filter(p => p.id !== productId)
    state.myProducts = state.myProducts.filter(p => p.id !== productId)
  }
}

const actions = {
  // 获取商品列表
  async getProductList({ commit, state }, params = {}) {
    try {
      const queryParams = {
        current: state.pagination.current,
        size: state.pagination.size,
        sortBy: state.sortBy,
        sortOrder: state.sortOrder,
        categoryId: state.selectedCategory?.id,
        ...params
      }
      const response = await getProductList(queryParams)
      if (response.data.code === 200) {
        const { records, current, size, total } = response.data.data
        commit('SET_PRODUCT_LIST', {
          list: records,
          pagination: { current, size, total }
        })
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取商品列表失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取我的商品
  async getMyProducts({ commit }, params = {}) {
    try {
      const response = await getMyProducts(params)
      if (response.data.code === 200) {
        commit('SET_MY_PRODUCTS', response.data.data.records)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取我的商品失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取商品详情
  async getProductDetail({ commit }, productId) {
    try {
      const response = await getProductDetail(productId)
      if (response.data.code === 200) {
        commit('SET_CURRENT_PRODUCT', response.data.data)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取商品详情失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 搜索商品
  async searchProducts({ commit, state }, keyword) {
    try {
      commit('SET_SEARCH_KEYWORD', keyword)
      const params = {
        keyword,
        current: state.pagination.current,
        size: state.pagination.size,
        sortBy: state.sortBy,
        sortOrder: state.sortOrder
      }
      const response = await searchProducts(params)
      if (response.data.code === 200) {
        const { records, current, size, total } = response.data.data
        commit('SET_SEARCH_RESULTS', {
          list: records,
          pagination: { current, size, total }
        })
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('搜索商品失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 设置分页
  setPagination({ commit, dispatch }, pagination) {
    commit('SET_PRODUCT_LIST', { list: [], pagination })
    dispatch('getProductList')
  },

  // 设置分类筛选
  setCategory({ commit, dispatch }, category) {
    commit('SET_SELECTED_CATEGORY', category)
    dispatch('getProductList')
  },

  // 设置排序
  setSortOptions({ commit, dispatch }, { sortBy, sortOrder }) {
    commit('SET_SORT_OPTIONS', { sortBy, sortOrder })
    dispatch('getProductList')
  },

  // 更新商品状态
  updateProductInList({ commit }, product) {
    commit('UPDATE_PRODUCT_IN_LIST', product)
  },

  // 从列表中移除商品
  removeProductFromList({ commit }, productId) {
    commit('REMOVE_PRODUCT_FROM_LIST', productId)
  }
}

const getters = {
  productList: state => state.productList,
  myProducts: state => state.myProducts,
  currentProduct: state => state.currentProduct,
  searchResults: state => state.searchResults,
  pagination: state => state.pagination,
  searchKeyword: state => state.searchKeyword,
  selectedCategory: state => state.selectedCategory,
  sortOptions: state => ({ sortBy: state.sortBy, sortOrder: state.sortOrder })
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}