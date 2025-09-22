<template>
  <div class="my-favorites">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的收藏</h2>
      <div class="header-actions">
        <el-button @click="clearAll" :disabled="favorites.length === 0">
          清空收藏
        </el-button>
        <el-button type="primary" @click="batchRemove" :disabled="selectedIds.length === 0">
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索收藏的商品"
          style="width: 250px"
          clearable
          @change="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="categoryFilter" placeholder="商品分类" style="width: 150px" @change="handleCategoryChange">
          <el-option label="全部分类" value="" />
          <el-option 
            v-for="category in categories" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id"
          />
        </el-select>
        
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px" @change="handleSort">
          <el-option label="收藏时间" value="created_at" />
          <el-option label="商品价格" value="price" />
          <el-option label="浏览量" value="views" />
        </el-select>
      </div>
      
      <div class="filter-right">
        <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        <span class="selected-count">已选择 {{ selectedIds.length }} 项</span>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="favorites-container" v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="favorite in favorites" :key="favorite.id">
          <div class="favorite-card" :class="{ selected: selectedIds.includes(favorite.id) }">
            <div class="card-header">
              <el-checkbox 
                :model-value="selectedIds.includes(favorite.id)"
                @change="handleItemSelect(favorite.id, $event)"
              />
              <el-button 
                size="small" 
                type="danger" 
                text 
                @click="removeFavorite(favorite)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            
            <div class="product-image" @click="viewProduct(favorite.product)">
              <img :src="favorite.product.images[0] || '/placeholder.jpg'" :alt="favorite.product.title" />
              <div class="product-status" v-if="favorite.product.status !== 'selling'" :class="getStatusClass(favorite.product.status)">
                {{ getStatusText(favorite.product.status) }}
              </div>
            </div>
            
            <div class="product-info">
              <h4 class="product-title" @click="viewProduct(favorite.product)">{{ favorite.product.title }}</h4>
              <div class="product-price">¥{{ favorite.product.price }}</div>
              <div class="product-meta">
                <span class="category">{{ favorite.product.category?.name }}</span>
                <span class="views">
                  <el-icon><View /></el-icon>
                  {{ favorite.product.views }}
                </span>
              </div>
              <div class="favorite-time">收藏于 {{ formatTime(favorite.created_at) }}</div>
            </div>
            
            <div class="product-actions">
              <el-button size="small" type="primary" @click="viewProduct(favorite.product)">
                查看详情
              </el-button>
              <el-button 
                size="small" 
                @click="contactSeller(favorite.product)"
                :disabled="favorite.product.status !== 'selling'"
              >
                联系卖家
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 空状态 -->
      <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏">
        <el-button type="primary" @click="$router.push('/home')">
          去逛逛
        </el-button>
      </el-empty>
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 48]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, View } from '@element-plus/icons-vue'
import { getUserFavorites, removeFavorite as removeFavoriteApi, batchRemoveFavorites } from '@/api/favorite'
import { getCategories } from '@/api/category'
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const favorites = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const categoryFilter = ref('')
const sortBy = ref('created_at')
const selectedIds = ref([])
const selectAll = ref(false)

// 监听全选状态
watch(() => favorites.value.length, () => {
  if (selectedIds.value.length === favorites.value.length && favorites.value.length > 0) {
    selectAll.value = true
  } else {
    selectAll.value = false
  }
})

// 获取收藏列表
const fetchFavorites = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      category_id: categoryFilter.value,
      sort_by: sortBy.value,
      sort_order: 'desc'
    }
    
    const response = await getUserFavorites(params)
    // MyBatis Plus Page对象结构：records为数据列表
    favorites.value = response.data.records || []
    total.value = response.data.total || 0
    
    // 清空选择状态
    selectedIds.value = []
    selectAll.value = false
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 分类筛选
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 排序
const handleSort = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFavorites()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchFavorites()
}

// 全选/取消全选
const handleSelectAll = (checked) => {
  if (checked) {
    selectedIds.value = favorites.value.map(item => item.id)
  } else {
    selectedIds.value = []
  }
}

// 单项选择
const handleItemSelect = (id, checked) => {
  if (checked) {
    selectedIds.value.push(id)
  } else {
    const index = selectedIds.value.indexOf(id)
    if (index > -1) {
      selectedIds.value.splice(index, 1)
    }
  }
}

// 查看商品
const viewProduct = (product) => {
  window.open(`/product/${product.id}`, '_blank')
}

// 联系卖家
const contactSeller = (product) => {
  ElMessage.info('联系卖家功能开发中')
}

// 移除单个收藏
const removeFavorite = async (favorite) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏"${favorite.product.title}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await removeFavoriteApi(favorite.id)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 批量删除
const batchRemove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个收藏吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await batchRemoveFavorites(selectedIds.value)
    ElMessage.success('批量删除成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 清空收藏
const clearAll = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有收藏吗？此操作不可恢复。',
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const allIds = favorites.value.map(item => item.id)
    await batchRemoveFavorites(allIds)
    ElMessage.success('清空成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空收藏失败:', error)
      ElMessage.error('清空收藏失败')
    }
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    sold: 'status-sold',
    offline: 'status-offline',
    pending: 'status-pending',
    rejected: 'status-rejected'
  }
  return statusMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    sold: '已售',
    offline: '下架',
    pending: '审核中',
    rejected: '审核未通过'
  }
  return statusMap[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 组件挂载
onMounted(() => {
  fetchFavorites()
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.my-favorites {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .filter-bar {
    background: var(--bg-primary);
    padding: 16px;
    border-radius: var(--border-radius-large);
    margin-bottom: 20px;
    box-shadow: var(--box-shadow-light);
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .filter-left {
      display: flex;
      gap: 12px;
      align-items: center;
    }
    
    .filter-right {
      display: flex;
      gap: 12px;
      align-items: center;
      
      .selected-count {
        color: var(--text-secondary);
        font-size: var(--font-size-small);
      }
    }
  }
  
  .favorites-container {
    .favorite-card {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      overflow: hidden;
      box-shadow: var(--box-shadow-light);
      transition: var(--transition-all);
      margin-bottom: 20px;
      border: 2px solid transparent;
      
      &:hover {
        box-shadow: var(--box-shadow-dark);
        transform: translateY(-2px);
      }
      
      &.selected {
        border-color: var(--primary-color);
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 12px;
        background: var(--bg-secondary);
      }
      
      .product-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        cursor: pointer;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: var(--transition-all);
        }
        
        &:hover img {
          transform: scale(1.05);
        }
        
        .product-status {
          position: absolute;
          top: 8px;
          left: 8px;
          padding: 4px 8px;
          border-radius: var(--border-radius-base);
          font-size: var(--font-size-small);
          color: white;
          
          &.status-sold {
            background: var(--info-color);
          }
          
          &.status-offline {
            background: var(--warning-color);
          }
          
          &.status-pending {
            background: var(--primary-color);
          }
          
          &.status-rejected {
            background: var(--danger-color);
          }
        }
      }
      
      .product-info {
        padding: 12px;
        
        .product-title {
          font-size: var(--font-size-medium);
          font-weight: var(--font-weight-primary);
          color: var(--text-primary);
          margin: 0 0 8px 0;
          cursor: pointer;
          transition: var(--transition-color);
          @include text-ellipsis;
          
          &:hover {
            color: var(--primary-color);
          }
        }
        
        .product-price {
          font-size: var(--font-size-large);
          font-weight: var(--font-weight-primary);
          color: var(--danger-color);
          margin-bottom: 8px;
        }
        
        .product-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          font-size: var(--font-size-small);
          color: var(--text-secondary);
          
          .category {
            background: var(--bg-tertiary);
            padding: 2px 6px;
            border-radius: var(--border-radius-small);
          }
          
          .views {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
        
        .favorite-time {
          font-size: var(--font-size-extra-small);
          color: var(--text-placeholder);
        }
      }
      
      .product-actions {
        padding: 0 12px 12px;
        display: flex;
        gap: 8px;
        
        .el-button {
          flex: 1;
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }
}

// 响应式设计
@include respond-to(md) {
  .my-favorites {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
      
      h2 {
        text-align: center;
      }
      
      .header-actions {
        justify-content: center;
      }
    }
    
    .filter-bar {
      flex-direction: column;
      gap: 12px;
      
      .filter-left {
        flex-wrap: wrap;
        justify-content: center;
        
        .el-input,
        .el-select {
          width: 100% !important;
          max-width: 200px;
        }
      }
      
      .filter-right {
        justify-content: center;
      }
    }
  }
}

@include respond-to(sm) {
  .my-favorites {
    padding: 12px;
    
    .filter-bar {
      .filter-left {
        flex-direction: column;
        
        .el-input,
        .el-select {
          max-width: none;
        }
      }
    }
    
    .favorites-container {
      .favorite-card {
        .product-actions {
          flex-direction: column;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>