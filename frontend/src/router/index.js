import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 用户端页面
import Home from '@/views/user/Home.vue'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import ProductDetail from '@/views/user/ProductDetail.vue'
import PublishProduct from '@/views/user/PublishProduct.vue'
import Rating from '@/views/user/Rating.vue'
import Profile from '@/views/user/Profile.vue'
import MyProducts from '@/views/user/MyProducts.vue'
import MyOrders from '@/views/user/MyOrders.vue'
import MyFavorites from '@/views/user/MyFavorites.vue'
import Chat from '@/views/user/Chat.vue'
import OrderDetail from '@/views/user/OrderDetail.vue'

// 管理员端页面
import Dashboard from '@/views/admin/Dashboard.vue'
import ContentAudit from '@/views/admin/ContentAudit.vue'
import UserManage from '@/views/admin/UserManage.vue'
import ProductManage from '@/views/admin/ProductManage.vue'
import OrderManage from '@/views/admin/OrderManage.vue'
import CategoryAnnouncement from '@/views/admin/CategoryAnnouncement.vue'
import OperationLog from '@/views/admin/OperationLog.vue'

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
    path: '/user/rating/:orderId',
    name: 'Rating',
    component: Rating,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/products',
    name: 'MyProducts',
    component: MyProducts,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/orders',
    name: 'MyOrders',
    component: MyOrders,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/favorites',
    name: 'MyFavorites',
    component: MyFavorites,
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/:userId/:productId?',
    name: 'Chat',
    component: Chat,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/orders/:id',
    name: 'OrderDetail',
    component: OrderDetail,
    meta: { requiresAuth: true }
  },
  // 管理员端路由
  {
    path: '/admin/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/content-audit',
    name: 'ContentAudit',
    component: ContentAudit,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/user-manage',
    name: 'UserManage',
    component: UserManage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/product-manage',
    name: 'ProductManage',
    component: ProductManage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/order-manage',
    name: 'OrderManage',
    component: OrderManage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/category-announcement',
    name: 'CategoryAnnouncement',
    component: CategoryAnnouncement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/operation-log',
    name: 'OperationLog',
    component: OperationLog,
    meta: { requiresAuth: true, requiresAdmin: true }
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