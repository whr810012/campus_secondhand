<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="sidebar">
        <div class="logo">
          <el-icon class="logo-icon"><Setting /></el-icon>
          <span class="logo-text">管理后台</span>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
          :collapse="isCollapse"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <template #title>商品管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/categories">
            <el-icon><Menu /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/audit">
            <el-icon><View /></el-icon>
            <template #title>内容审核</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-button
              text
              @click="toggleSidebar"
              class="collapse-btn"
            >
              <el-icon><Expand v-if="isCollapse" /><Fold v-else /></el-icon>
            </el-button>
            
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/admin' }">管理后台</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <!-- 返回前台 -->
            <el-button @click="$router.push('/home')">
              <el-icon><House /></el-icon>
              返回前台
            </el-button>
            
            <!-- 管理员信息 -->
            <el-dropdown @command="handleUserCommand">
              <div class="admin-info">
                <el-avatar :src="userStore.userInfo?.avatar" :size="32">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </el-avatar>
                <span class="admin-name">{{ userStore.userInfo?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主要内容区域 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 当前页面标题
const currentPageTitle = computed(() => {
  const titleMap = {
    '/admin/dashboard': '仪表盘',
    '/admin/users': '用户管理',
    '/admin/products': '商品管理',
    '/admin/orders': '订单管理',
    '/admin/categories': '分类管理',
    '/admin/audit': '内容审核',
    '/admin/announcements': '公告管理',
    '/admin/logs': '操作日志'
  }
  return titleMap[route.path] || '管理后台'
})

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  router.push(index)
}

// 处理用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logoutAction()
        ElMessage.success('退出登录成功')
        router.push('/home')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}

// 初始化
onMounted(async () => {
  // 检查管理员权限
  if (!userStore.isAdmin) {
    ElMessage.error('没有管理员权限')
    router.push('/home')
    return
  }
  
  // 如果用户信息不存在，获取用户信息
  if (!userStore.userInfo) {
    await userStore.initUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
}

.sidebar {
  background: #304156;
  
  .logo {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 20px;
    color: white;
    font-size: 18px;
    font-weight: 600;
    border-bottom: 1px solid #434a5a;
    
    .logo-icon {
      font-size: 24px;
      color: #409eff;
    }
  }
  
  .sidebar-menu {
    border-right: none;
    background: #304156;
    
    :deep(.el-menu-item) {
      color: #bfcbd9;
      
      &:hover {
        background-color: #434a5a;
        color: white;
      }
      
      &.is-active {
        background-color: #409eff;
        color: white;
      }
    }
    
    :deep(.el-icon) {
      color: inherit;
    }
  }
}

.header {
  background: white;
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .collapse-btn {
      font-size: 18px;
    }
    
    .breadcrumb {
      font-size: 14px;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .admin-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 8px;
      border-radius: 6px;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #f5f7fa;
      }
      
      .admin-name {
        font-size: 14px;
        color: var(--text-primary);
      }
    }
  }
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

// 响应式设计
@media (max-width: 768px) {
  .sidebar {
    width: 64px !important;
    
    .logo {
      justify-content: center;
      
      .logo-text {
        display: none;
      }
    }
  }
  
  .header {
    .header-left {
      .breadcrumb {
        display: none;
      }
    }
    
    .header-right {
      .admin-info {
        .admin-name {
          display: none;
        }
      }
    }
  }
  
  .main-content {
    padding: 10px;
  }
}
</style>