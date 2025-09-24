<template>
  <div class="products-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">商品管理</h1>
        <p class="page-description">管理平台所有商品信息，包括审核、上下架等操作</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="exportProducts">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">总商品数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon active">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.active }}</div>
              <div class="stat-label">已上架</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon rejected">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.rejected }}</div>
              <div class="stat-label">已下架</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filters" inline>
          <el-form-item label="商品名称">
            <el-input
              v-model="filters.name"
              placeholder="请输入商品名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="商品状态">
            <el-select v-model="filters.status" placeholder="请选择状态" clearable style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="pending" />
              <el-option label="已上架" value="active" />
              <el-option label="已下架" value="inactive" />
              <el-option label="已售出" value="sold" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select v-model="filters.category" placeholder="请选择分类" clearable style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="数码电子" value="electronics" />
              <el-option label="图书教材" value="books" />
              <el-option label="生活用品" value="daily" />
              <el-option label="服装配饰" value="clothing" />
              <el-option label="运动户外" value="sports" />
              <el-option label="其他" value="others" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格范围">
            <el-input
              v-model="filters.minPrice"
              placeholder="最低价"
              type="number"
              style="width: 100px"
            />
            <span style="margin: 0 8px">-</span>
            <el-input
              v-model="filters.maxPrice"
              placeholder="最高价"
              type="number"
              style="width: 100px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchProducts">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilters">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedProducts.length > 0">
      <el-alert
        :title="`已选择 ${selectedProducts.length} 个商品`"
        type="info"
        show-icon
        :closable="false"
      >
        <template #default>
          <div class="batch-buttons">
            <el-button size="small" @click="batchApprove">批量审核通过</el-button>
            <el-button size="small" @click="batchReject">批量审核拒绝</el-button>
            <el-button size="small" @click="batchOffline">批量下架</el-button>
            <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
          </div>
        </template>
      </el-alert>
    </div>

    <!-- 商品列表 -->
    <div class="products-table">
      <el-card>
        <el-table
          :data="products"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  :src="row.images[0]"
                  :preview-src-list="row.images"
                  class="product-image"
                  fit="cover"
                />
                <div class="product-details">
                  <div class="product-name">{{ row.name }}</div>
                  <div class="product-desc">{{ row.description }}</div>
                  <div class="product-meta">
                    <el-tag size="small">{{ getCategoryName(row.category) }}</el-tag>
                    <span class="product-id">ID: {{ row.id }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" prop="price" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="卖家" width="120">
            <template #default="{ row }">
              <div class="seller-info">
                <div class="seller-name">{{ row.seller.name }}</div>
                <div class="seller-contact">{{ row.seller.phone }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" prop="createdAt" width="120">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="浏览/收藏" width="100">
            <template #default="{ row }">
              <div class="stats">
                <div>{{ row.views }} 浏览</div>
                <div>{{ row.favorites }} 收藏</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button size="small" @click="viewProduct(row)">查看</el-button>
                <el-button
                  v-if="row.status === 'pending'"
                  size="small"
                  type="success"
                  @click="approveProduct(row)"
                >
                  审核通过
                </el-button>
                <el-button
                  v-if="row.status === 'pending'"
                  size="small"
                  type="warning"
                  @click="rejectProduct(row)"
                >
                  审核拒绝
                </el-button>
                <el-button
                  v-if="row.status === 'active'"
                  size="small"
                  type="warning"
                  @click="offlineProduct(row)"
                >
                  下架
                </el-button>
                <el-button
                  v-if="row.status === 'inactive'"
                  size="small"
                  type="success"
                  @click="onlineProduct(row)"
                >
                  上架
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteProduct(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      title="商品详情"
      width="800px"
      :before-close="closeProductDetail"
    >
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="product-images">
              <el-carousel height="300px">
                <el-carousel-item v-for="image in selectedProduct.images" :key="image">
                  <el-image :src="image" fit="cover" style="width: 100%; height: 100%" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="product-info-detail">
              <h3>{{ selectedProduct.name }}</h3>
              <div class="price-info">
                <span class="current-price">¥{{ selectedProduct.price }}</span>
                <span v-if="selectedProduct.originalPrice" class="original-price">
                  ¥{{ selectedProduct.originalPrice }}
                </span>
              </div>
              <div class="product-tags">
                <el-tag>{{ getCategoryName(selectedProduct.category) }}</el-tag>
                <el-tag :type="getStatusType(selectedProduct.status)">
                  {{ getStatusText(selectedProduct.status) }}
                </el-tag>
              </div>
              <div class="product-stats">
                <div class="stat-item">
                  <span class="label">浏览次数：</span>
                  <span class="value">{{ selectedProduct.views }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">收藏次数：</span>
                  <span class="value">{{ selectedProduct.favorites }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">发布时间：</span>
                  <span class="value">{{ formatDate(selectedProduct.createdAt) }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="product-description">
          <h4>商品描述</h4>
          <p>{{ selectedProduct.description }}</p>
        </div>
        <div class="seller-info-detail">
          <h4>卖家信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="label">姓名：</span>
                <span class="value">{{ selectedProduct.seller.name }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ selectedProduct.seller.phone }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">学号：</span>
                <span class="value">{{ selectedProduct.seller.studentId }}</span>
              </div>
              <div class="info-item">
                <span class="label">学院：</span>
                <span class="value">{{ selectedProduct.seller.college }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeProductDetail">关闭</el-button>
          <el-button
            v-if="selectedProduct?.status === 'pending'"
            type="success"
            @click="approveProduct(selectedProduct)"
          >
            审核通过
          </el-button>
          <el-button
            v-if="selectedProduct?.status === 'pending'"
            type="warning"
            @click="rejectProduct(selectedProduct)"
          >
            审核拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Search,
  Refresh,
  Box,
  Clock,
  Check,
  Close
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const products = ref([])
const selectedProducts = ref([])
const productDetailVisible = ref(false)
const selectedProduct = ref(null)

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  active: 0,
  rejected: 0
})

// 筛选条件
const filters = reactive({
  name: '',
  status: '',
  category: '',
  minPrice: '',
  maxPrice: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 分类映射
const categoryMap = {
  electronics: '数码电子',
  books: '图书教材',
  daily: '生活用品',
  clothing: '服装配饰',
  sports: '运动户外',
  others: '其他'
}

// 状态映射
const statusMap = {
  pending: '待审核',
  active: '已上架',
  inactive: '已下架',
  sold: '已售出'
}

// 获取分类名称
const getCategoryName = (category) => {
  return categoryMap[category] || category
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    active: 'success',
    inactive: 'info',
    sold: 'success'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    const mockProducts = [
      {
        id: 1,
        name: 'iPhone 13 Pro',
        description: '9成新，无磕碰，功能正常，配件齐全',
        price: 6800,
        originalPrice: 8999,
        category: 'electronics',
        status: 'pending',
        images: ['/api/placeholder/300/300'],
        seller: {
          name: '张三',
          phone: '138****1234',
          studentId: '2021001',
          college: '计算机学院'
        },
        views: 156,
        favorites: 23,
        createdAt: '2024-01-15T10:30:00Z'
      },
      {
        id: 2,
        name: '高等数学教材',
        description: '同济版高等数学上下册，笔记齐全',
        price: 45,
        originalPrice: 89,
        category: 'books',
        status: 'active',
        images: ['/api/placeholder/300/300'],
        seller: {
          name: '李四',
          phone: '139****5678',
          studentId: '2021002',
          college: '数学学院'
        },
        views: 89,
        favorites: 12,
        createdAt: '2024-01-14T15:20:00Z'
      }
    ]
    
    products.value = mockProducts
    pagination.total = mockProducts.length
    
    // 更新统计数据
    updateStats()
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.total = products.value.length
  stats.pending = products.value.filter(p => p.status === 'pending').length
  stats.active = products.value.filter(p => p.status === 'active').length
  stats.rejected = products.value.filter(p => p.status === 'inactive').length
}

// 搜索商品
const searchProducts = () => {
  pagination.page = 1
  fetchProducts()
}

// 重置筛选条件
const resetFilters = () => {
  Object.keys(filters).forEach(key => {
    filters[key] = ''
  })
  searchProducts()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedProducts.value = selection
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchProducts()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchProducts()
}

// 查看商品详情
const viewProduct = (product) => {
  selectedProduct.value = product
  productDetailVisible.value = true
}

// 关闭商品详情
const closeProductDetail = () => {
  productDetailVisible.value = false
  selectedProduct.value = null
}

// 审核通过
const approveProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认审核通过该商品？', '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    product.status = 'active'
    ElMessage.success('审核通过成功')
    updateStats()
    
    if (productDetailVisible.value) {
      closeProductDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核通过失败')
    }
  }
}

// 审核拒绝
const rejectProduct = async (product) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    product.status = 'inactive'
    product.rejectReason = reason
    ElMessage.success('审核拒绝成功')
    updateStats()
    
    if (productDetailVisible.value) {
      closeProductDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核拒绝失败')
    }
  }
}

// 下架商品
const offlineProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认下架该商品？', '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    product.status = 'inactive'
    ElMessage.success('下架成功')
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

// 上架商品
const onlineProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认上架该商品？', '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    product.status = 'active'
    ElMessage.success('上架成功')
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('上架失败')
    }
  }
}

// 删除商品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认删除该商品？删除后无法恢复！', '确认删除', {
      type: 'error'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const index = products.value.findIndex(p => p.id === product.id)
    if (index > -1) {
      products.value.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量操作
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(`确认批量审核通过 ${selectedProducts.value.length} 个商品？`, '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedProducts.value.forEach(product => {
      if (product.status === 'pending') {
        product.status = 'active'
      }
    })
    
    ElMessage.success('批量审核通过成功')
    selectedProducts.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量审核通过失败')
    }
  }
}

const batchReject = async () => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '批量审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedProducts.value.forEach(product => {
      if (product.status === 'pending') {
        product.status = 'inactive'
        product.rejectReason = reason
      }
    })
    
    ElMessage.success('批量审核拒绝成功')
    selectedProducts.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量审核拒绝失败')
    }
  }
}

const batchOffline = async () => {
  try {
    await ElMessageBox.confirm(`确认批量下架 ${selectedProducts.value.length} 个商品？`, '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedProducts.value.forEach(product => {
      if (product.status === 'active') {
        product.status = 'inactive'
      }
    })
    
    ElMessage.success('批量下架成功')
    selectedProducts.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确认批量删除 ${selectedProducts.value.length} 个商品？删除后无法恢复！`,
      '确认删除',
      { type: 'error' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const idsToDelete = selectedProducts.value.map(p => p.id)
    products.value = products.value.filter(p => !idsToDelete.includes(p.id))
    
    ElMessage.success('批量删除成功')
    selectedProducts.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出数据
const exportProducts = async () => {
  try {
    ElMessage.info('正在导出数据...')
    
    // 模拟导出
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 初始化
onMounted(() => {
  fetchProducts()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.products-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      .page-title {
        margin: 0 0 8px 0;
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }

      .page-description {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .stats-cards {
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-2px);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;
        color: white;

        &.total {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.pending {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.active {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.rejected {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .stat-content {
        .stat-number {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }

  .filter-section {
    margin-bottom: 20px;
  }

  .batch-actions {
    margin-bottom: 20px;

    .batch-buttons {
      margin-top: 10px;

      .el-button {
        margin-right: 10px;
      }
    }
  }

  .products-table {
    margin-bottom: 20px;

    .product-info {
      display: flex;
      align-items: center;

      .product-image {
        width: 60px;
        height: 60px;
        border-radius: 6px;
        margin-right: 12px;
        flex-shrink: 0;
      }

      .product-details {
        flex: 1;
        min-width: 0;

        .product-name {
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .product-desc {
          font-size: 12px;
          color: #909399;
          margin-bottom: 6px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .product-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .product-id {
            font-size: 12px;
            color: #c0c4cc;
          }
        }
      }
    }

    .price {
      font-weight: 600;
      color: #e6a23c;
      font-size: 16px;
    }

    .seller-info {
      .seller-name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 2px;
      }

      .seller-contact {
        font-size: 12px;
        color: #909399;
      }
    }

    .stats {
      font-size: 12px;
      color: #909399;
      line-height: 1.4;
    }

    .action-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;

      .el-button {
        padding: 4px 8px;
        font-size: 12px;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .product-detail {
    .product-images {
      .el-carousel {
        border-radius: 8px;
        overflow: hidden;
      }
    }

    .product-info-detail {
      h3 {
        margin: 0 0 16px 0;
        font-size: 20px;
        color: #303133;
      }

      .price-info {
        margin-bottom: 16px;

        .current-price {
          font-size: 24px;
          font-weight: 600;
          color: #e6a23c;
          margin-right: 12px;
        }

        .original-price {
          font-size: 16px;
          color: #c0c4cc;
          text-decoration: line-through;
        }
      }

      .product-tags {
        margin-bottom: 16px;

        .el-tag {
          margin-right: 8px;
        }
      }

      .product-stats {
        .stat-item {
          display: flex;
          margin-bottom: 8px;

          .label {
            width: 80px;
            color: #909399;
            font-size: 14px;
          }

          .value {
            color: #303133;
            font-size: 14px;
          }
        }
      }
    }

    .product-description {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;

      h4 {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #303133;
      }

      p {
        margin: 0;
        color: #606266;
        line-height: 1.6;
      }
    }

    .seller-info-detail {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;

      h4 {
        margin: 0 0 16px 0;
        font-size: 16px;
        color: #303133;
      }

      .info-item {
        display: flex;
        margin-bottom: 8px;

        .label {
          width: 80px;
          color: #909399;
          font-size: 14px;
        }

        .value {
          color: #303133;
          font-size: 14px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .products-management {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .stats-cards {
      .el-col {
        margin-bottom: 10px;
      }
    }

    .filter-section {
      .el-form {
        .el-form-item {
          width: 100%;
          margin-bottom: 16px;
        }
      }
    }

    .products-table {
      .action-buttons {
        flex-direction: column;

        .el-button {
          width: 100%;
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style>