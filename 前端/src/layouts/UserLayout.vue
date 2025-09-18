<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <!-- Logo -->
          <div class="logo" @click="$router.push('/home')">
            <el-icon class="logo-icon"><ShoppingBag /></el-icon>
            <span class="logo-text">校园二手</span>
          </div>
          
          <!-- 搜索框 -->
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品..."
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          
          <!-- 用户操作区 -->
          <div class="user-actions">
            <template v-if="userStore.isLoggedIn">
              <!-- 发布商品 -->
              <el-button type="primary" @click="$router.push('/publish')">
                <el-icon><Plus /></el-icon>
                发布商品
              </el-button>
              
              <!-- 消息 -->
              <el-badge :value="unreadCount" :hidden="unreadCount === 0">
                <el-button circle @click="$router.push('/messages')">
                  <el-icon><Message /></el-icon>
                </el-button>
              </el-badge>
              
              <!-- 用户菜单 -->
              <el-dropdown @command="handleUserCommand">
                <div class="user-info">
                  <el-avatar :src="userStore.userInfo?.avatar" :size="32">
                    {{ userStore.userInfo?.nickname?.charAt(0) }}
                  </el-avatar>
                  <span class="username">{{ userStore.userInfo?.nickname }}</span>
                  <el-icon><ArrowDown /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="my-products">
                      <el-icon><Goods /></el-icon>
                      我的发布
                    </el-dropdown-item>
                    <el-dropdown-item command="my-orders">
                      <el-icon><List /></el-icon>
                      我的订单
                    </el-dropdown-item>
                    <el-dropdown-item command="my-favorites">
                      <el-icon><Star /></el-icon>
                      我的收藏
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            
            <template v-else>
              <el-button @click="$router.push('/auth/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/auth/register')">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 导航菜单 -->
    <nav class="nav-menu">
      <div class="container">
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          @select="handleMenuSelect"
          class="nav-menu-items"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-sub-menu index="categories">
            <template #title>分类</template>
            <el-menu-item
              v-for="category in categories"
              :key="category.id"
              :index="`/home?categoryId=${category.id}`"
            >
              {{ category.name }}
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </nav>
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view />
    </main>
    
    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>关于我们</h4>
            <p>校园二手交易平台致力于为在校学生提供安全、便捷的二手商品交易服务。</p>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <p>邮箱: campus-secondhand@example.com</p>
            <p>电话: 400-123-4567</p>
          </div>
          <div class="footer-section">
            <h4>友情链接</h4>
            <p>学校官网 | 学生会 | 社团联合会</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 校园二手交易平台. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCategories } from '@/api/product'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const categories = ref([])
const unreadCount = ref(0)

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/home',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  if (index.startsWith('/')) {
    router.push(index)
  }
}

// 处理用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-products':
      router.push('/my-products')
      break
    case 'my-orders':
      router.push('/my-orders')
      break
    case 'my-favorites':
      router.push('/my-favorites')
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

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data.filter(item => item.parentId === 0)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 初始化
onMounted(async () => {
  await fetchCategories()
  
  // 如果已登录，初始化用户信息
  if (userStore.isLoggedIn && !userStore.userInfo) {
    await userStore.initUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  
  .header-content {
    display: flex;
    align-items: center;
    height: 64px;
    gap: 20px;
  }
  
  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: var(--primary-color);
    font-weight: 600;
    font-size: 18px;
    
    .logo-icon {
      font-size: 24px;
    }
  }
  
  .search-box {
    flex: 1;
    max-width: 400px;
    
    .search-input {
      :deep(.el-input-group__append) {
        padding: 0;
        
        .el-button {
          margin: 0;
          border: none;
        }
      }
    }
  }
  
  .user-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .user-info {
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
      
      .username {
        font-size: 14px;
        color: var(--text-primary);
      }
    }
  }
}

.nav-menu {
  background: white;
  border-bottom: 1px solid var(--border-light);
  
  .nav-menu-items {
    border-bottom: none;
  }
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.footer {
  background: #2c3e50;
  color: white;
  padding: 40px 0 20px;
  
  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 30px;
    margin-bottom: 30px;
  }
  
  .footer-section {
    h4 {
      margin-bottom: 15px;
      font-size: 16px;
    }
    
    p {
      color: #bdc3c7;
      line-height: 1.6;
      margin-bottom: 8px;
    }
  }
  
  .footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #34495e;
    color: #bdc3c7;
  }
}

@media (max-width: 768px) {
  .header {
    .header-content {
      gap: 10px;
    }
    
    .logo {
      .logo-text {
        display: none;
      }
    }
    
    .search-box {
      max-width: 200px;
    }
    
    .user-actions {
      gap: 8px;
      
      .user-info {
        .username {
          display: none;
        }
      }
    }
  }
  
  .footer {
    .footer-content {
      grid-template-columns: 1fr;
      gap: 20px;
    }
  }
}
</style>