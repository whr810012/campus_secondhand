import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 用户端布局
const UserLayout = () => import('@/layouts/UserLayout.vue')
// 管理员端布局
const AdminLayout = () => import('@/layouts/AdminLayout.vue')
// 认证页面布局
const AuthLayout = () => import('@/layouts/AuthLayout.vue')

// 用户端页面
const Home = () => import('@/views/user/Home.vue')
const ProductDetail = () => import('@/views/user/ProductDetail.vue')
const PublishProduct = () => import('@/views/user/PublishProduct.vue')
const Profile = () => import('@/views/user/Profile.vue')
const UserProfile = () => import('@/views/user/UserProfile.vue')
const Verification = () => import('@/views/user/Verification.vue')
const MyProducts = () => import('@/views/user/MyProducts.vue')
const MyOrders = () => import('@/views/user/MyOrders.vue')
const MyFavorites = () => import('@/views/user/MyFavorites.vue')
const Messages = () => import('@/views/user/Messages.vue')
const Chat = () => import('@/views/user/Chat.vue')
const OrderDetail = () => import('@/views/user/OrderDetail.vue')
const OrderConfirm = () => import('@/views/user/OrderConfirm.vue')

// 认证页面
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')


// 管理员端页面
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const AdminUsers = () => import('@/views/admin/Users.vue')
const AdminProducts = () => import('@/views/admin/Products.vue')
const AdminOrders = () => import('@/views/admin/Orders.vue')
const AdminCategories = () => import('@/views/admin/Categories.vue')
const AdminAnnouncements = () => import('@/views/admin/Announcements.vue')
const AdminLogs = () => import('@/views/admin/Logs.vue')
const AdminAudit = () => import('@/views/admin/Audit.vue')

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  // 重定向路由
  {
    path: '/register',
    redirect: '/auth/register'
  },
  {
    path: '/login',
    redirect: '/auth/login'
  },
  // 用户端路由
  {
    path: '/',
    component: UserLayout,
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: { title: '首页' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: ProductDetail,
        meta: { title: '商品详情' }
      },
      {
        path: 'publish',
        name: 'PublishProduct',
        component: PublishProduct,
        meta: { title: '发布商品', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: UserProfile,
        meta: { title: '用户详情' }
      },
      {
        path: 'verification',
        name: 'Verification',
        component: Verification,
        meta: { title: '学生认证', requiresAuth: true }
      },
      {
        path: 'my-products',
        name: 'MyProducts',
        component: MyProducts,
        meta: { title: '我的发布', requiresAuth: true }
      },
      {
        path: 'my-orders',
        name: 'MyOrders',
        component: MyOrders,
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'my-favorites',
        name: 'MyFavorites',
        component: MyFavorites,
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: Messages,
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: 'chat',
        name: 'Chat',
        component: Chat,
        meta: { title: '聊天', requiresAuth: true }
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: OrderConfirm,
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: OrderDetail,
        meta: { title: '订单详情', requiresAuth: true }
      }
    ]
  },
  // 认证路由
  {
    path: '/auth',
    component: AuthLayout,
    children: [
      {
        path: 'login',
        name: 'Login',
        component: Login,
        meta: { title: '登录' }
      },
      {
        path: 'register',
        name: 'Register',
        component: Register,
        meta: { title: '注册' }
      },

    ]
  },
  // 管理员端路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { title: '用户管理' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: AdminProducts,
        meta: { title: '商品管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: AdminOrders,
        meta: { title: '订单管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: AdminCategories,
        meta: { title: '分类管理' }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: AdminAnnouncements,
        meta: { title: '公告管理' }
      },
      {
        path: 'audit',
        name: 'AdminAudit',
        component: AdminAudit,
        meta: { title: '内容审核' }
      },
      {
        path: 'logs',
        name: 'AdminLogs',
        component: AdminLogs,
        meta: { title: '操作日志' }
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
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园二手交易平台`
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/auth/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/')
    return
  }
  
  next()
})

export default router