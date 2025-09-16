<template>
  <div class="my-favorites">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <h1>我的收藏</h1>
          <div class="header-actions">
            <el-button @click="toggleSelectMode" v-if="favorites.length > 0">
              {{ selectMode ? '取消' : '管理收藏' }}
            </el-button>
            <el-button 
              v-if="selectMode && selectedItems.length > 0" 
              @click="batchRemove" 
              type="danger"
            >
              删除选中 ({{ selectedItems.length }})
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选选项 -->
    <div class="filter-section">
      <div class="container">
        <div class="filter-content">
          <el-radio-group v-model="statusFilter" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="available">在售</el-radio-button>
            <el-radio-button label="sold">已售出</el-radio-button>
            <el-radio-button label="unavailable">已下架</el-radio-button>
          </el-radio-group>
          
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索收藏的商品..."
              @keyup.enter="handleSearch"
              clearable
            >
              <template #append>
                <el-button @click="handleSearch" :icon="Search" />
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="favorites-section">
      <div class="container">
        <div class="favorites-grid" v-loading="loading">
          <div 
            v-for="favorite in favorites"
            :key="favorite.id"
            class="favorite-card"
            :class="{ selected: selectedItems.includes(favorite.id) }"
          >
            <!-- 选择框 -->
            <div class="select-checkbox" v-if="selectMode">
              <el-checkbox 
                :model-value="selectedItems.includes(favorite.id)"
                @change="toggleSelect(favorite.id)"
              />
            </div>
            
            <div class="product-content" @click="viewProduct(favorite.product.id)">
              <div class="product-image-container">
                <img :src="favorite.product.images[0]" :alt="favorite.product.title" class="product-image" />
                <div class="product-status" v-if="favorite.product.status !== 'available'">
                  {{ getStatusText(favorite.product.status) }}
                </div>
              </div>
              
              <div class="product-info">
                <h3 class="product-title">{{ favorite.product.title }}</h3>
                <p class="product-price">¥{{ favorite.product.price }}</p>
                <div class="product-meta">
                  <span class="seller">{{ favorite.product.seller.nickname }}</span>
                  <span class="time">收藏于 {{ formatTime(favorite.createdAt) }}</span>
                </div>
              </div>
            </div>
            
            <div class="favorite-actions" v-if="!selectMode">
              <el-button 
                size="small" 
                @click.stop="removeFavorite(favorite)"
                type="danger"
                :icon="Delete"
              >
                取消收藏
              </el-button>
              <el-button 
                size="small" 
                @click.stop="buyNow(favorite.product)"
                type="primary"
                v-if="favorite.product.status === 'available'"
              >
                立即购买
              </el-button>
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
        <div v-if="!loading && favorites.length === 0" class="empty-state">
          <el-empty description="暂无收藏">
            <el-button type="primary" @click="$router.push('/')">
              去逛逛
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()

// 响应式数据
const statusFilter = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const favorites = ref([])

// 选择模式
const selectMode = ref(false)
const selectedItems = ref([])

// 获取收藏列表
const fetchFavorites = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === 'all' ? undefined : statusFilter.value,
      keyword: searchKeyword.value
    }
    
    const response = await api.get('/user/favorites', { params })
    if (response.data.code === 200) {
      favorites.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 分页变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFavorites()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFavorites()
}

// 查看商品详情
const viewProduct = (productId) => {
  router.push(`/products/${productId}`)
}

// 切换选择模式
const toggleSelectMode = () => {
  selectMode.value = !selectMode.value
  selectedItems.value = []
}

// 切换选择
const toggleSelect = (favoriteId) => {
  const index = selectedItems.value.indexOf(favoriteId)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(favoriteId)
  }
}

// 取消收藏
const removeFavorite = async (favorite) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏「${favorite.product.title}」吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.delete(`/user/favorites/${favorite.id}`)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 批量删除
const batchRemove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏选中的 ${selectedItems.value.length} 个商品吗？`,
      '确认批量操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.delete('/user/favorites/batch', {
      data: { favoriteIds: selectedItems.value }
    })
    
    ElMessage.success('批量取消收藏成功')
    selectedItems.value = []
    selectMode.value = false
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

// 立即购买
const buyNow = (product) => {
  // 跳转到购买页面或显示购买对话框
  router.push(`/products/${product.id}?action=buy`)
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
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 初始化
onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.page-header {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.header-content h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  background: #fff;
  padding: 20px 0;
  border-bottom: 1px solid var(--border-light);
}

.filter-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-box {
  width: 300px;
}

.favorites-section {
  padding: 20px 0;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.favorite-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
}

.favorite-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.favorite-card.selected {
  border: 2px solid var(--primary-color);
}

.select-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  padding: 4px;
}

.product-content {
  cursor: pointer;
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

.favorite-card:hover .product-image {
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
  font-size: 18px;
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
}

.favorite-actions {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  padding: 15px;
  border-top: 1px solid var(--border-light);
  background: var(--fill-light);
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
  .filter-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .favorite-actions {
    flex-direction: column;
  }
}
</style>