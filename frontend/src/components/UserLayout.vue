<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <h1>校园二手</h1>
          </div>
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品..."
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch" :icon="Search" />
              </template>
            </el-input>
          </div>
          <div class="user-actions">
            <el-button @click="$router.push('/user/publish')" type="primary">
              <el-icon><Plus /></el-icon>
              发布商品
            </el-button>
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.user?.avatar" />
                <span class="username">{{ userStore.user?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </header>

    <div class="layout-container">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-content">
          <div class="user-profile">
            <el-avatar :size="60" :src="userStore.user?.avatar" />
            <div class="user-info">
              <h3>{{ userStore.user?.nickname }}</h3>
              <p>{{ userStore.user?.email }}</p>
            </div>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/user/profile" class="nav-item">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </router-link>
            <router-link to="/user/products" class="nav-item">
              <el-icon><Goods /></el-icon>
              <span>我的发布</span>
            </router-link>
            <router-link to="/user/orders" class="nav-item">
              <el-icon><ShoppingBag /></el-icon>
              <span>我的订单</span>
            </router-link>
            <router-link to="/user/favorites" class="nav-item">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </router-link>

            <router-link to="/user/address" class="nav-item">
              <el-icon><Location /></el-icon>
              <span>收货地址</span>
            </router-link>
            <router-link to="/user/settings" class="nav-item">
              <el-icon><Setting /></el-icon>
              <span>设置</span>
            </router-link>
          </nav>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="main-content">
        <div class="content-wrapper">
          <!-- 面包屑导航 -->
          <el-breadcrumb class="breadcrumb" separator=">">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ getBreadcrumbTitle() }}</el-breadcrumb-item>
          </el-breadcrumb>
          
          <!-- 页面内容 -->
          <div class="page-content">
            <router-view />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Search,
  Plus,
  ArrowDown,
  User,
  Goods,
  ShoppingBag,
  Star,
  Location,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/',
      query: { search: searchKeyword.value.trim() }
    })
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}

const getBreadcrumbTitle = () => {
  const routeMap = {
    '/user/profile': '个人中心',
    '/user/products': '我的发布',
    '/user/orders': '我的订单',
    '/user/favorites': '我的收藏',
    '/user/messages': '消息中心',
    '/user/address': '收货地址',
    '/user/settings': '设置'
  }
  return routeMap[router.currentRoute.value.path] || '用户中心'
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  gap: 20px;
}

.logo {
  cursor: pointer;
}

.logo h1 {
  margin: 0;
  font-size: 20px;
  color: #409eff;
  font-weight: 600;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}

.layout-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  gap: 20px;
}

.sidebar {
  width: 240px;
  flex-shrink: 0;
}

.sidebar-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 100px;
}

.user-profile {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.user-profile .user-info {
  margin-top: 12px;
}

.user-profile h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #303133;
}

.user-profile p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 6px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s;
  font-size: 14px;
}

.nav-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.nav-item.router-link-active {
  background-color: #ecf5ff;
  color: #409eff;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.content-wrapper {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.breadcrumb {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fafafa;
}

.page-content {
  padding: 20px;
}

@media (max-width: 768px) {
  .layout-container {
    flex-direction: column;
    padding: 10px;
  }
  
  .sidebar {
    width: 100%;
    order: 2;
  }
  
  .sidebar-content {
    position: static;
  }
  
  .main-content {
    order: 1;
  }
  
  .header-content {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 0;
  }
  
  .search-bar {
    order: 3;
    max-width: none;
    width: 100%;
  }
}
</style>