<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <img v-if="!isCollapse" src="@/assets/images/logo.png" alt="管理后台" class="logo-img">
          <span v-if="!isCollapse" class="logo-text">管理后台</span>
          <i v-else class="el-icon-s-grid logo-icon"></i>
        </div>
        
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :unique-opened="true"
          class="sidebar-menu"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-s-platform"></i>
            <span slot="title">仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/products">
            <i class="el-icon-goods"></i>
            <span slot="title">商品管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/orders">
            <i class="el-icon-s-order"></i>
            <span slot="title">订单管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/categories">
            <i class="el-icon-menu"></i>
            <span slot="title">分类管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/notices">
            <i class="el-icon-bell"></i>
            <span slot="title">公告管理</span>
          </el-menu-item>
          
          <el-menu-item v-if="userRole === 'super_admin'" index="/admin/users">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/statistics">
            <i class="el-icon-s-data"></i>
            <span slot="title">统计分析</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <!-- 顶部栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-button
              type="text"
              icon="el-icon-s-fold"
              @click="toggleCollapse"
              class="collapse-btn"
            ></el-button>
            
            <!-- 面包屑导航 -->
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path" :to="item.path">
                {{ item.name }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <!-- 通知 -->
            <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification">
              <el-button type="text" icon="el-icon-bell" @click="showNotifications"></el-button>
            </el-badge>
            
            <!-- 用户菜单 -->
            <el-dropdown @command="handleCommand" class="user-dropdown">
              <span class="user-info">
                <el-avatar :src="userInfo.avatar" :size="32">
                  {{ userInfo.username?.charAt(0) }}
                </el-avatar>
                <span class="username">{{ userInfo.username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人信息
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <i class="el-icon-setting"></i> 系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
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

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'AdminLayout',
  data() {
    return {
      isCollapse: false,
      notificationCount: 0
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'userRole']),
    breadcrumbList() {
      const route = this.$route
      const matched = route.matched.filter(item => item.meta && item.meta.title)
      return matched.map(item => ({
        path: item.path,
        name: item.meta.title
      }))
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    showNotifications() {
      this.$message.info('暂无新通知')
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('个人信息功能开发中')
          break
        case 'settings':
          this.$message.info('系统设置功能开发中')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/logout')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      })
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b3a4b;
  color: #fff;
}

.logo-img {
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  font-size: 16px;
  font-weight: bold;
}

.logo-icon {
  font-size: 24px;
}

.sidebar-menu {
  border: none;
  background: #304156;
}

.sidebar-menu .el-menu-item {
  color: #bfcbd9;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-menu-item.is-active {
  background: #409EFF !important;
  color: #fff;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px !important;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 18px;
  margin-right: 20px;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.notification {
  margin-right: 20px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
}

.username {
  margin: 0 8px;
  color: #333;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>