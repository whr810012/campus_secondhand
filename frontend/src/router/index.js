import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 用户端页面
import Home from '@/views/user/Home.vue'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import ProductDetail from '@/views/user/ProductDetail.vue'
import PublishProduct from '@/views/user/PublishProduct.vue'
import Profile from '@/views/user/Profile.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/user/publish',
    name: 'PublishProduct',
    component: PublishProduct,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router