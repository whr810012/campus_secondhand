import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import product from './modules/product'
import order from './modules/order'
import category from './modules/category'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    product,
    order,
    category
  },
  state: {
    loading: false
  },
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading
    }
  },
  actions: {
    setLoading({ commit }, loading) {
      commit('SET_LOADING', loading)
    }
  },
  getters: {
    loading: state => state.loading,
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    userRole: state => state.user.userInfo?.role || 'user'
  }
})