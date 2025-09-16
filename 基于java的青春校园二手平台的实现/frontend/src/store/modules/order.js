import { getOrderList, getMyOrders, createOrder, updateOrderStatus, getOrderDetail } from '@/api/order'

const state = {
  orderList: [],
  myBuyOrders: [],
  mySellOrders: [],
  currentOrder: null,
  pagination: {
    current: 1,
    size: 10,
    total: 0
  },
  orderStats: {
    totalOrders: 0,
    pendingOrders: 0,
    completedOrders: 0,
    cancelledOrders: 0
  }
}

const mutations = {
  SET_ORDER_LIST(state, { list, pagination }) {
    state.orderList = list
    state.pagination = { ...state.pagination, ...pagination }
  },
  SET_MY_BUY_ORDERS(state, orders) {
    state.myBuyOrders = orders
  },
  SET_MY_SELL_ORDERS(state, orders) {
    state.mySellOrders = orders
  },
  SET_CURRENT_ORDER(state, order) {
    state.currentOrder = order
  },
  SET_ORDER_STATS(state, stats) {
    state.orderStats = { ...state.orderStats, ...stats }
  },
  UPDATE_ORDER_IN_LIST(state, updatedOrder) {
    const updateInArray = (array) => {
      const index = array.findIndex(o => o.id === updatedOrder.id)
      if (index !== -1) {
        array.splice(index, 1, updatedOrder)
      }
    }
    updateInArray(state.orderList)
    updateInArray(state.myBuyOrders)
    updateInArray(state.mySellOrders)
  }
}

const actions = {
  // 获取订单列表（管理员）
  async getOrderList({ commit, state }, params = {}) {
    try {
      const queryParams = {
        current: state.pagination.current,
        size: state.pagination.size,
        ...params
      }
      const response = await getOrderList(queryParams)
      if (response.data.code === 200) {
        const { records, current, size, total } = response.data.data
        commit('SET_ORDER_LIST', {
          list: records,
          pagination: { current, size, total }
        })
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取订单列表失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取我的购买订单
  async getMyBuyOrders({ commit }, params = {}) {
    try {
      const response = await getMyOrders({ type: 'buy', ...params })
      if (response.data.code === 200) {
        commit('SET_MY_BUY_ORDERS', response.data.data.records)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取购买订单失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取我的销售订单
  async getMySellOrders({ commit }, params = {}) {
    try {
      const response = await getMyOrders({ type: 'sell', ...params })
      if (response.data.code === 200) {
        commit('SET_MY_SELL_ORDERS', response.data.data.records)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取销售订单失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 获取订单详情
  async getOrderDetail({ commit }, orderId) {
    try {
      const response = await getOrderDetail(orderId)
      if (response.data.code === 200) {
        commit('SET_CURRENT_ORDER', response.data.data)
        return { success: true, data: response.data.data }
      }
    } catch (error) {
      console.error('获取订单详情失败:', error)
      return { success: false, message: error.message }
    }
  },

  // 创建订单
  async createOrder({ dispatch }, orderData) {
    try {
      const response = await createOrder(orderData)
      if (response.data.code === 200) {
        // 创建成功后刷新订单列表
        dispatch('getMyBuyOrders')
        return { success: true, data: response.data.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '创建订单失败' }
    }
  },

  // 更新订单状态
  async updateOrderStatus({ commit }, { orderId, status }) {
    try {
      const response = await updateOrderStatus(orderId, status)
      if (response.data.code === 200) {
        commit('UPDATE_ORDER_IN_LIST', response.data.data)
        return { success: true, data: response.data.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '更新订单状态失败' }
    }
  },

  // 设置分页
  setPagination({ commit, dispatch }, pagination) {
    commit('SET_ORDER_LIST', { list: [], pagination })
    dispatch('getOrderList')
  }
}

const getters = {
  orderList: state => state.orderList,
  myBuyOrders: state => state.myBuyOrders,
  mySellOrders: state => state.mySellOrders,
  currentOrder: state => state.currentOrder,
  pagination: state => state.pagination,
  orderStats: state => state.orderStats
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}