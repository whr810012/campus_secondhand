<template>
  <div class="my-products">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <h1>我的发布</h1>
          <el-button type="primary" @click="$router.push('/user/publish')">
            <el-icon><Plus /></el-icon>
            发布新商品
          </el-button>
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
            <el-radio-button label="reserved">已预定</el-radio-button>
            <el-radio-button label="unavailable">已下架</el-radio-button>
          </el-radio-group>
          
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索我的商品..."
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

    <!-- 商品列表 -->
    <div class="products-section">
      <div class="container">
        <div class="products-grid" v-loading="loading">
          <div 
            v-for="product in products"
            :key="product.id"
            class="product-card"
          >
            <div class="product-image-container">
              <img :src="product.images[0]" :alt="product.title" class="product-image" />
              <div class="product-status" :class="`status-${product.status}`">
                {{ getStatusText(product.status) }}
              </div>
            </div>
            
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
              <div class="product-meta">
                <span class="views">浏览 {{ product.viewCount }}</span>
                <span class="favorites">收藏 {{ product.favoriteCount }}</span>
                <span class="time">{{ formatTime(product.createdAt) }}</span>
              </div>
              
              <div class="product-actions">
                <el-button size="small" @click="viewProduct(product.id)">
                  查看详情
                </el-button>
                <el-button size="small" @click="editProduct(product.id)" v-if="product.status === 'available'">
                  编辑
                </el-button>
                <el-dropdown @command="(command) => handleProductAction(command, product)">
                  <el-button size="small">
                    更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="toggle" v-if="product.status === 'available'">
                        下架商品
                      </el-dropdown-item>
                      <el-dropdown-item command="toggle" v-if="product.status === 'unavailable'">
                        重新上架
                      </el-dropdown-item>
                      <el-dropdown-item command="sold" v-if="product.status === 'available'">
                        标记为已售出
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" divided>
                        删除商品
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
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
          <el-empty description="暂无商品">
            <el-button type="primary" @click="$router.push('/user/publish')">
              发布第一个商品
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
import { Search, Plus, ArrowDown } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()

// 响应式数据
const statusFilter = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const products = ref([])

// 获取我的商品列表
const fetchMyProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === 'all' ? undefined : statusFilter.value,
      keyword: searchKeyword.value
    }
    
    const response = await api.get('/user/products', { params })
    if (response.data.code === 200) {
      products.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  fetchMyProducts()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchMyProducts()
}

// 分页变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchMyProducts()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchMyProducts()
}

// 查看商品详情
const viewProduct = (productId) => {
  router.push(`/products/${productId}`)
}

// 编辑商品
const editProduct = (productId) => {
  router.push(`/user/publish?edit=${productId}`)
}

// 商品操作
const handleProductAction = async (command, product) => {
  switch (command) {
    case 'toggle':
      await toggleProductStatus(product)
      break
    case 'sold':
      await markAsSold(product)
      break
    case 'delete':
      await deleteProduct(product)
      break
  }
}

// 切换商品状态
const toggleProductStatus = async (product) => {
  try {
    const newStatus = product.status === 'available' ? 'unavailable' : 'available'
    const action = newStatus === 'available' ? '上架' : '下架'
    
    await ElMessageBox.confirm(
      `确定要${action}商品「${product.title}」吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.put(`/user/products/${product.id}/status`, { status: newStatus })
    product.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 标记为已售出
const markAsSold = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要将商品「${product.title}」标记为已售出吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.put(`/user/products/${product.id}/status`, { status: 'sold' })
    product.status = 'sold'
    ElMessage.success('标记成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除商品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品「${product.title}」吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await api.delete(`/user/products/${product.id}`)
    ElMessage.success('删除成功')
    fetchMyProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    available: '在售',
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
  fetchMyProducts()
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

.products-section {
  padding: 20px 0;
}

.products-grid {
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
}

.product-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
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
}

.product-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.status-available {
  background: var(--success-color);
}

.status-sold {
  background: var(--info-color);
}

.status-reserved {
  background: var(--warning-color);
}

.status-unavailable {
  background: var(--danger-color);
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
  margin-bottom: 15px;
}

.product-actions {
  display: flex;
  gap: 8px;
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
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
}
</style>