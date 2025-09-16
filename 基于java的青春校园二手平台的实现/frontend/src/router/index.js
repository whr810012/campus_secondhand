import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/user/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  // 用户端路由
  {
    path: '/user',
    component: () => import('@/layouts/UserLayout.vue'),
    meta: { requiresAuth: true, role: 'user' },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue')
      },
      {
        path: 'shop',
        name: 'Shop',
        component: () => import('@/views/user/Shop.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/user/ProductDetail.vue')
      },
      {
        path: 'my-products',
        name: 'MyProducts',
        component: () => import('@/views/user/MyProducts.vue')
      },
      {
        path: 'publish',
        name: 'PublishProduct',
        component: () => import('@/views/user/PublishProduct.vue')
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue')
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'address',
        name: 'UserAddress',
        component: () => import('@/views/user/Address.vue')
      }
    ]
  },
  // 管理员端路由
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { role: 'super_admin' }
      },
      {
        path: 'products',
        name: 'ProductManagement',
        component: () => import('@/views/admin/ProductManagement.vue')
      },
      {
        path: 'orders',
        name: 'OrderManagement',
        component: () => import('@/views/admin/OrderManagement.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('@/views/admin/CategoryManagement.vue')
      },
      {
        path: 'notices',
        name: 'NoticeManagement',
        component: () => import('@/views/admin/NoticeManagement.vue')
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue')
      }
    ]
  },
  {
    path: '*',
    name: '404',
    component: () => import('@/views/404.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.getters.token
  const userRole = store.getters.userRole
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }
    
    // 角色权限检查
    if (to.meta.role) {
      if (to.meta.role === 'super_admin' && userRole !== 'super_admin') {
        next('/admin/dashboard')
        return
      }
      if (to.meta.role === 'admin' && !['admin', 'super_admin'].includes(userRole)) {
        next('/user/home')
        return
      }
      if (to.meta.role === 'user' && ['admin', 'super_admin'].includes(userRole)) {
        next('/admin/dashboard')
        return
      }
    }
  }
  
  // 已登录用户访问登录/注册页面
  if (token && (to.path === '/login' || to.path === '/register')) {
    if (['admin', 'super_admin'].includes(userRole)) {
      next('/admin/dashboard')
    } else {
      next('/user/home')
    }
    return
  }
  
  next()
})

export default router