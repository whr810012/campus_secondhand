import { getCategoryList, getEnabledCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'

const state = {
  categoryList: [],
  enabledCategories: [],
  currentCategory: null,
  pagination: {
    current: 1,
    size: 10,
    total: 0
  }
}

const mutations = {
  SET_CATEGORY_LIST(state, { list, pagination }) {
    state.categoryList = list
    if (pagination) {
      state.pagination = { ...state.pagination, ...pagination }
    }
  },
  SET_ENABLED_CATEGORIES(state, categories) {
    state.enabledCategories = categories
  },
  SET_CURRENT_CATEGORY(state, category) {
    state.currentCategory = category
  },
  ADD_CATEGORY(state, category) {
    state.categoryList.unshift(category)
    if (category.status === 1) {
      state.enabledCategories.unshift(category)
    }
  },
  UPDATE_CATEGORY_IN_LIST(state, updatedCategory) {
    const index = state.categoryList.findIndex(c => c.id === updatedCategory.id)
    if (index !== -1) {
      state.categoryList.splice(index, 1, updatedCategory)
    }
    const enabledIndex = state.enabledCategories.findIndex(c => c.id === updatedCategory.id)
    if (updatedCategory.status === 1) {
      if (enabledIndex === -1) {
        state.enabledCategories.push(updatedCategory)
      } else {
        state.enabledCategories.splice(enabledIndex, 1, updatedCategory)
      }
    } else if (enabledIndex !== -1) {
      state.enabledCategories.splice(enabledIndex, 1)
    }
  },
  REMOVE_CATEGORY_FROM_LIST(state, categoryId) {
    state.categoryList = state.categoryList.filter(c => c.id !== categoryId)
    state.enabledCategories = state.enabledCategories.filter(c => c.id !== categoryId)
  }
}

const actions = {
  // 获取分类列表（管理员）
  async getCategoryList({ commit, state }, params = {}) {
    try {
      const queryParams = {
        current: state.pagination.current,
        size: state.pagination.size,
        ...params
      }
      const response = await getCategoryList(queryParams)
      if (response.data.code === 200) {
        const { records, current, size, total } = response.data.data
        commit('SET_CATEGORY_LIST', {
          list: records,
          pagination: { current, size, total }
        })
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取分类列表失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取启用的分类列表（用户端）
  async getEnabledCategories({ commit }) {
    try {
      const response = await getEnabledCategories()
      if (response.data.code === 200) {
        commit('SET_ENABLED_CATEGORIES', response.data.data)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取启用分类失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 创建分类
  async createCategory({ commit }, categoryData) {
    try {
      const response = await createCategory(categoryData)
      if (response.data.code === 200) {
        commit('ADD_CATEGORY', response.data.data)
        return { success: true, data: response.data.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '创建分类失败' }
    }
  },

  // 更新分类
  async updateCategory({ commit }, { id, categoryData }) {
    try {
      const response = await updateCategory(id, categoryData)
      if (response.data.code === 200) {
        commit('UPDATE_CATEGORY_IN_LIST', response.data.data)
        return { success: true, data: response.data.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '更新分类失败' }
    }
  },

  // 删除分类
  async deleteCategory({ commit }, categoryId) {
    try {
      const response = await deleteCategory(categoryId)
      if (response.data.code === 200) {
        commit('REMOVE_CATEGORY_FROM_LIST', categoryId)
        return { success: true }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '删除分类失败' }
    }
  },

  // 设置分页
  setPagination({ commit, dispatch }, pagination) {
    commit('SET_CATEGORY_LIST', { list: [], pagination })
    dispatch('getCategoryList')
  }
}

const getters = {
  categoryList: state => state.categoryList,
  enabledCategories: state => state.enabledCategories,
  currentCategory: state => state.currentCategory,
  pagination: state => state.pagination,
  categoryOptions: state => state.enabledCategories.map(cat => ({
    label: cat.name,
    value: cat.id
  }))
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}