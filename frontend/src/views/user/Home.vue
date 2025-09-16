<template>
  <div class="home">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo">
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
            <template v-if="userStore.isLoggedIn">
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
                    <el-dropdown-item command="products">我的发布</el-dropdown-item>
                    <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                    <el-dropdown-item command="favorites">收藏夹</el-dropdown-item>
                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button @click="$router.push('/login')">登录</el-button>
              <el-button @click="$router.push('/register')" type="primary">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- 分类导航 -->
    <nav class="category-nav">
      <div class="container">
        <div class="categories">
          <div 
            class="category-item"
            :class="{ active: selectedCategory === null }"
            @click="selectCategory(null)"
          >
            全部
          </div>
          <div 
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            :class="{ active: selectedCategory === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>
      </div>
    </nav>

    <!-- 商品列表 -->
    <main class="main-content">
      <div class="container">
        <div class="content-header">
          <div class="sort-options">
            <el-radio-group v-model="sortBy" @change="handleSortChange">
              <el-radio-button label="latest">最新发布</el-radio-button>
              <el-radio-button label="price_asc">价格从低到高</el-radio-button>
              <el-radio-button label="price_desc">价格从高到低</el-radio-button>
              <el-radio-button label="popular">最受欢迎</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <div class="product-grid" v-loading="loading">
          <div 
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="viewProduct(product.id)"
          >
            <div class="product-image-container">
              <img :src="product.images[0]" :alt="product.title" class="product-image" />
              <div class="product-status" v-if="product.status !== 'available'">
                {{ getStatusText(product.status) }}
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
              <div class="product-meta">
                <span class="seller">{{ product.seller.nickname }}</span>
                <span class="time">{{ formatTime(product.createdAt) }}</span>
              </div>
              <div class="product-actions">
                <el-button 
                  size="small" 
                  @click.stop="toggleFavorite(product)"
                  :type="product.isFavorited ? 'danger' : 'default'"
                >
                  <el-icon><Star /></el-icon>
                  {{ product.isFavorited ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[12, 24, 48]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && products.length === 0" class="empty-state">
          <el-empty description="暂无商品" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Plus, ArrowDown, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const searchKeyword = ref('')
const selectedCategory = ref(null)
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const products = ref([])
const categories = ref([])

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await api.get('/categories')
    // 后端返回格式: {code: 200, message: '操作成功', data: [...]}
    if (response.data && response.data.code === 200) {
      categories.value = response.data.data
    } else {
      throw new Error(response.data?.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.warning('获取分类列表失败')
  }
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: selectedCategory.value,
      sortBy: sortBy.value
    }
    
    const response = await api.get('/products', { params })
    products.value = response.data.data.records
    total.value = response.data.data.total
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索商品
const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  fetchProducts()
}

// 排序变化
const handleSortChange = () => {
  currentPage.value = 1
  fetchProducts()
}

// 分页变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchProducts()
}

// 查看商品详情
const viewProduct = (productId) => {
  router.push(`/products/${productId}`)
}

// 切换收藏
const toggleFavorite = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (product.isFavorited) {
      await api.delete(`/favorites/${product.id}`)
      product.isFavorited = false
      ElMessage.success('取消收藏成功')
    } else {
      await api.post('/favorites', { productId: product.id })
      product.isFavorited = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 用户操作
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'products':
      router.push('/user/products')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('退出登录成功')
      break
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    sold: '已售出',
    reserved: '已预定',
    unavailable: '已下架'
  }
  return statusMap[status] || status
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return `${Math.floor(diff / 86400000)}天前`
  }
}

// 初始化
onMounted(() => {
  fetchCategories()
  fetchProducts()
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
  }
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  padding: 15px 0;
  gap: 20px;
}

.logo h1 {
  color: var(--primary-color);
  font-size: 24px;
  font-weight: 600;
}

.search-bar {
  flex: 1;
  max-width: 500px;
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
  background-color: var(--border-extra-light);
}

.username {
  font-size: 14px;
  color: var(--text-primary);
}

.category-nav {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
}

.categories {
  display: flex;
  gap: 30px;
  padding: 15px 0;
  overflow-x: auto;
}

.category-item {
  white-space: nowrap;
  cursor: pointer;
  padding: 8px 0;
  color: var(--text-regular);
  font-weight: 500;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.category-item:hover,
.category-item.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.main-content {
  padding: 20px 0;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.product-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-status {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-price {
  font-size: 20px;
  font-weight: 600;
  color: var(--danger-color);
  margin-bottom: 10px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.product-actions {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-bar {
    order: 3;
    max-width: none;
  }
  
  .user-actions {
    order: 2;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }
  
  .categories {
    gap: 20px;
  }
}
</style>