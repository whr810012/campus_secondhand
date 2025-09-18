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

// 布局组件
import AdminLayout from '@/components/AdminLayout.vue'
import UserLayout from '@/components/UserLayout.vue'

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
  // 用户端路由
  {
    path: '/user',
    component: UserLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'products',
        name: 'MyProducts',
        component: MyProducts
      },
      {
        path: 'orders',
        name: 'MyOrders',
        component: MyOrders
      },
      {
        path: 'favorites',
        name: 'MyFavorites',
        component: MyFavorites
      },
      {
        path: 'publish',
        name: 'PublishProduct',
        component: PublishProduct
      },

      {
        path: 'chat/:userId',
        name: 'Chat',
        component: Chat
      },
      {
        path: 'rating/:orderId',
        name: 'Rating',
        component: Rating
      },
      {
        path: 'order/:orderId',
        name: 'OrderDetail',
        component: OrderDetail
      },
      {
        path: '',
        redirect: 'profile'
      }
    ]
  },
  // 独立的聊天路由（支持产品ID参数）
  {
    path: '/chat/:userId/:productId?',
    name: 'ChatWithProduct',
    component: Chat,
    meta: { requiresAuth: true }
  },
  // 管理员端路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: 'content-audit',
        name: 'ContentAudit',
        component: ContentAudit
      },
      {
        path: 'user-manage',
        name: 'UserManage',
        component: UserManage
      },
      {
        path: 'product-manage',
        name: 'ProductManage',
        component: ProductManage
      },
      {
        path: 'order-manage',
        name: 'OrderManage',
        component: OrderManage
      },
      {
        path: 'category-announcement',
        name: 'CategoryAnnouncement',
        component: CategoryAnnouncement
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        component: OperationLog
      },
      {
        path: '',
        redirect: 'dashboard'
      }
    ]
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