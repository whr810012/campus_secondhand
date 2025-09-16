<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/user/home">
            <img src="@/assets/images/logo.png" alt="青春校园" class="logo-img">
            <span class="logo-text">青春校园</span>
          </router-link>
        </div>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
          </el-input>
        </div>
        
        <!-- 用户菜单 -->
        <div class="user-menu">
          <el-dropdown v-if="isLoggedIn" @command="handleCommand">
            <span class="user-info">
              <el-avatar :src="userInfo?.avatar" :size="32">
                {{ userInfo?.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userInfo?.username || '用户' }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="my-products">
                <i class="el-icon-goods"></i> 我的商品
              </el-dropdown-item>
              <el-dropdown-item command="orders">
                <i class="el-icon-s-order"></i> 我的订单
              </el-dropdown-item>
              <el-dropdown-item command="address">
                <i class="el-icon-location"></i> 地址管理
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div v-else class="login-buttons">
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>
    
    <!-- 导航菜单 -->
    <el-menu
      :default-active="$route.path"
      mode="horizontal"
      class="nav-menu"
      router
    >
      <el-menu-item index="/user/home">
        <i class="el-icon-s-home"></i>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/user/shop">
        <i class="el-icon-shopping-bag-2"></i>
        <span>购物商城</span>
      </el-menu-item>
      <el-menu-item index="/user/publish">
        <i class="el-icon-plus"></i>
        <span>发布商品</span>
      </el-menu-item>
      <el-menu-item index="/user/my-products">
        <i class="el-icon-goods"></i>
        <span>我的商品</span>
      </el-menu-item>
      <el-menu-item index="/user/orders">
        <i class="el-icon-s-order"></i>
        <span>我的订单</span>
      </el-menu-item>
    </el-menu>
    
    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
    
    <!-- 底部 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 青春校园二手平台. All rights reserved.</p>
        <p>让闲置物品重新焕发价值</p>
      </div>
    </el-footer>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UserLayout',
  data() {
    return {
      searchKeyword: ''
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    isLoggedIn() {
      return this.userInfo && this.userInfo.id
    }
  },
  created() {
    // 检查登录状态并获取用户信息
    this.$store.dispatch('user/checkLogin')
  },
  methods: {
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push({
          path: '/user/shop',
          query: { keyword: this.searchKeyword }
        })
      }
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/user/profile')
          break
        case 'my-products':
          this.$router.push('/user/my-products')
          break
        case 'orders':
          this.$router.push('/user/orders')
          break
        case 'address':
          this.$router.push('/user/address')
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
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px !important;
  line-height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
}

.username {
  margin: 0 8px;
  color: #333;
}

.login-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.login-buttons .el-button--text {
  color: #666;
  font-size: 14px;
}

.login-buttons .el-button--text:hover {
  color: #409eff;
}

.nav-menu {
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.main-content {
  flex: 1;
  background: #f5f5f5;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.footer {
  background: #333;
  color: #fff;
  text-align: center;
  padding: 20px 0;
  height: auto !important;
}

.footer-content p {
  margin: 5px 0;
}
</style>