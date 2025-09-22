<template>
  <div class="my-products">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的发布</h2>
      <el-button type="primary" @click="$router.push('/publish')">
        <el-icon><Plus /></el-icon>
        发布商品
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="在售" name="selling" />
        <el-tab-pane label="已售出" name="sold" />
        <el-tab-pane label="已下架" name="offline" />
        <el-tab-pane label="审核中" name="pending" />
      </el-tabs>
      
      <div class="filter-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索我的商品"
          style="width: 200px"
          clearable
          @change="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px" @change="handleSort">
          <el-option label="发布时间" value="created_at" />
          <el-option label="更新时间" value="updated_at" />
          <el-option label="浏览量" value="views" />
          <el-option label="价格" value="price" />
        </el-select>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-container">
      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id">
          <div class="product-card">
            <div class="product-image">
              <img :src="product.images[0] || '/placeholder.jpg'" :alt="product.title" />
              <div class="product-status" :class="getStatusClass(product.status)">
                {{ getStatusText(product.status) }}
              </div>
              <div class="product-actions">
                <el-button size="small" @click="viewProduct(product)">
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button size="small" @click="editProduct(product)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button size="small" type="danger" @click="deleteProduct(product)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            
            <div class="product-info">
              <h4 class="product-title">{{ product.title }}</h4>
              <div class="product-price">¥{{ product.price }}</div>
              <div class="product-meta">
                <span class="views">
                  <el-icon><View /></el-icon>
                  {{ product.views }}
                </span>
                <span class="created-time">{{ formatTime(product.created_at) }}</span>
              </div>
            </div>
            
            <div class="product-operations">
              <el-button 
                v-if="product.status === 'selling'" 
                size="small" 
                @click="toggleStatus(product, 'offline')"
              >
                下架
              </el-button>
              <el-button 
                v-if="product.status === 'offline'" 
                size="small" 
                type="primary" 
                @click="toggleStatus(product, 'selling')"
              >
                上架
              </el-button>
              <el-button 
                v-if="product.status === 'selling'" 
                size="small" 
                type="success" 
                @click="markAsSold(product)"
              >
                标记已售
              </el-button>
              <el-button size="small" @click="promoteProduct(product)">
                置顶
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 空状态 -->
      <el-empty v-if="!loading && products.length === 0" description="暂无商品">
        <el-button type="primary" @click="$router.push('/publish')">
          发布第一个商品
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, View, Edit, Delete } from '@element-plus/icons-vue'
import { getUserProducts, updateProductStatus, deleteProduct as deleteProductApi, promoteProduct as promoteProductApi } from '@/api/product'
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const products = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const activeTab = ref('all')
const searchKeyword = ref('')
const sortBy = ref('created_at')

// 获取商品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: activeTab.value === 'all' ? undefined : activeTab.value,
      keyword: searchKeyword.value,
      sort_by: sortBy.value,
      sort_order: 'desc'
    }
    
    const response = await getUserProducts(params)
    // MyBatis Plus Page对象结构：records为数据列表
    products.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  fetchProducts()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

// 排序
const handleSort = () => {
  currentPage.value = 1
  fetchProducts()
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchProducts()
}

// 查看商品
const viewProduct = (product) => {
  window.open(`/product/${product.id}`, '_blank')
}

// 编辑商品
const editProduct = (product) => {
  // 跳转到编辑页面
  window.open(`/publish?id=${product.id}`, '_blank')
}

// 删除商品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteProductApi(product.id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除商品失败')
    }
  }
}

// 切换商品状态
const toggleStatus = async (product, status) => {
  try {
    await updateProductStatus(product.id, { status })
    ElMessage.success(status === 'selling' ? '上架成功' : '下架成功')
    fetchProducts()
  } catch (error) {
    console.error('更新商品状态失败:', error)
    ElMessage.error('操作失败')
  }
}

// 标记为已售
const markAsSold = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要将"${product.title}"标记为已售出吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await updateProductStatus(product.id, { status: 'sold' })
    ElMessage.success('标记成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 置顶商品
const promoteProduct = async (product) => {
  try {
    await promoteProductApi(product.id)
    ElMessage.success('置顶成功')
    fetchProducts()
  } catch (error) {
    console.error('置顶失败:', error)
    ElMessage.error('置顶失败')
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    selling: 'status-selling',
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
    selling: '在售',
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
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.my-products {
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
  }
  
  .filter-bar {
    background: var(--bg-primary);
    padding: 16px;
    border-radius: var(--border-radius-large);
    margin-bottom: 20px;
    box-shadow: var(--box-shadow-light);
    
    .el-tabs {
      margin-bottom: 16px;
    }
    
    .filter-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
  
  .products-container {
    .product-card {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      overflow: hidden;
      box-shadow: var(--box-shadow-light);
      transition: var(--transition-all);
      margin-bottom: 20px;
      
      &:hover {
        box-shadow: var(--box-shadow-dark);
        transform: translateY(-2px);
      }
      
      .product-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: var(--transition-all);
        }
        
        .product-status {
          position: absolute;
          top: 8px;
          left: 8px;
          padding: 4px 8px;
          border-radius: var(--border-radius-base);
          font-size: var(--font-size-small);
          color: white;
          
          &.status-selling {
            background: var(--success-color);
          }
          
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
        
        .product-actions {
          position: absolute;
          top: 8px;
          right: 8px;
          display: flex;
          gap: 4px;
          opacity: 0;
          transition: var(--transition-all);
          
          .el-button {
            padding: 4px;
            min-height: auto;
          }
        }
        
        &:hover .product-actions {
          opacity: 1;
        }
      }
      
      .product-info {
        padding: 12px;
        
        .product-title {
          font-size: var(--font-size-medium);
          font-weight: var(--font-weight-primary);
          color: var(--text-primary);
          margin: 0 0 8px 0;
          @include text-ellipsis;
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
          font-size: var(--font-size-small);
          color: var(--text-secondary);
          
          .views {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
      
      .product-operations {
        padding: 0 12px 12px;
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        
        .el-button {
          flex: 1;
          min-width: 60px;
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
  .my-products {
    padding: 16px;
    
    .filter-bar {
      .filter-actions {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
        
        .el-input,
        .el-select {
          width: 100% !important;
        }
      }
    }
  }
}

@include respond-to(sm) {
  .my-products {
    padding: 12px;
    
    .page-header {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
      
      h2 {
        text-align: center;
      }
    }
    
    .products-container {
      .product-card {
        .product-operations {
          .el-button {
            font-size: var(--font-size-small);
          }
        }
      }
    }
  }
}
</style>