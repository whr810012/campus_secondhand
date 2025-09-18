<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <i class="fas fa-graduation-cap"></i>
          <span v-if="!sidebarCollapsed">校园二手管理</span>
        </div>
        <button class="collapse-btn" @click="toggleSidebar">
          <i class="fas" :class="sidebarCollapsed ? 'fa-chevron-right' : 'fa-chevron-left'"></i>
        </button>
      </div>
      
      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/dashboard" class="nav-link" active-class="active">
              <i class="fas fa-tachometer-alt"></i>
              <span v-if="!sidebarCollapsed">仪表盘</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/content-audit" class="nav-link" active-class="active">
              <i class="fas fa-search"></i>
              <span v-if="!sidebarCollapsed">内容审核</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/user-manage" class="nav-link" active-class="active">
              <i class="fas fa-users"></i>
              <span v-if="!sidebarCollapsed">用户管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/product-manage" class="nav-link" active-class="active">
              <i class="fas fa-box"></i>
              <span v-if="!sidebarCollapsed">商品管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/order-manage" class="nav-link" active-class="active">
              <i class="fas fa-shopping-cart"></i>
              <span v-if="!sidebarCollapsed">订单管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/category-announcement" class="nav-link" active-class="active">
              <i class="fas fa-tags"></i>
              <span v-if="!sidebarCollapsed">分类公告</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/operation-log" class="nav-link" active-class="active">
              <i class="fas fa-history"></i>
              <span v-if="!sidebarCollapsed">操作日志</span>
            </router-link>
          </li>
        </ul>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info" v-if="!sidebarCollapsed">
          <div class="user-avatar">
            <i class="fas fa-user-shield"></i>
          </div>
          <div class="user-details">
            <div class="user-name">{{ userStore.userInfo?.nickname || '管理员' }}</div>
            <div class="user-role">系统管理员</div>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout" :title="sidebarCollapsed ? '退出登录' : ''">
          <i class="fas fa-sign-out-alt"></i>
          <span v-if="!sidebarCollapsed">退出登录</span>
        </button>
      </div>
    </aside>
    
    <!-- 主内容区域 -->
    <main class="main-content" :class="{ expanded: sidebarCollapsed }">
      <header class="main-header">
        <div class="breadcrumb">
          <span class="breadcrumb-item">管理后台</span>
          <i class="fas fa-chevron-right"></i>
          <span class="breadcrumb-item current">{{ getCurrentPageTitle() }}</span>
        </div>
        <div class="header-actions">
          <button class="action-btn" @click="$router.push('/')" title="返回前台">
            <i class="fas fa-home"></i>
          </button>
        </div>
      </header>
      
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

export default {
  name: 'AdminLayout',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const sidebarCollapsed = ref(false)
    
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }
    
    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }
    
    const getCurrentPageTitle = () => {
      const routeMap = {
        '/admin/dashboard': '仪表盘',
        '/admin/content-audit': '内容审核',
        '/admin/user-manage': '用户管理',
        '/admin/product-manage': '商品管理',
        '/admin/order-manage': '订单管理',
        '/admin/category-announcement': '分类公告',
        '/admin/operation-log': '操作日志'
      }
      return routeMap[router.currentRoute.value.path] || '管理后台'
    }
    
    return {
      userStore,
      sidebarCollapsed,
      toggleSidebar,
      handleLogout,
      getCurrentPageTitle
    }
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  background: #2c3e50;
  color: white;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 1000;
}

.sidebar.collapsed {
  width: 70px;
}

.sidebar-header {
  padding: 1rem;
  border-bottom: 1px solid #34495e;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
}

.logo i {
  font-size: 1.5rem;
  color: #3498db;
}

.collapse-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background 0.2s;
}

.collapse-btn:hover {
  background: #34495e;
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
}

.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 0.25rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #bdc3c7;
  text-decoration: none;
  transition: all 0.2s;
  border-radius: 0;
}

.nav-link:hover {
  background: #34495e;
  color: white;
}

.nav-link.active {
  background: #3498db;
  color: white;
  border-right: 3px solid #2980b9;
}

.nav-link i {
  font-size: 1.1rem;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid #34495e;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: #34495e;
  border-radius: 8px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: #3498db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-details {
  flex: 1;
}

.user-name {
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.user-role {
  font-size: 0.8rem;
  color: #bdc3c7;
}

.logout-btn {
  width: 100%;
  background: #e74c3c;
  border: none;
  color: white;
  padding: 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: #c0392b;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  margin-left: 260px;
  transition: margin-left 0.3s ease;
  display: flex;
  flex-direction: column;
}

.main-content.expanded {
  margin-left: 70px;
}

.main-header {
  background: white;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6c757d;
}

.breadcrumb-item.current {
  color: #2c3e50;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  color: #6c757d;
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.content-wrapper {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    width: 260px;
  }
  
  .sidebar.collapsed {
    transform: translateX(-190px);
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .main-content.expanded {
    margin-left: 0;
  }
}
</style>