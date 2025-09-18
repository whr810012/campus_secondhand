<template>
  <div class="product-management-page">
    <div class="page-header">
      <h1>商品管理</h1>
      <p>管理平台商品信息和审核状态</p>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-card>
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="搜索">
            <el-input
              v-model="searchForm.keyword"
              placeholder="输入商品名称或卖家昵称"
              style="width: 300px"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select v-model="searchForm.categoryId" placeholder="选择分类" style="width: 150px" clearable>
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="选择状态" style="width: 120px" clearable>
              <el-option label="待审核" :value="0" />
              <el-option label="已上架" :value="1" />
              <el-option label="已下架" :value="2" />
              <el-option label="已售出" :value="3" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="价格区间">
            <el-input
              v-model="searchForm.minPrice"
              placeholder="最低价"
              style="width: 100px"
              type="number"
            />
            <span style="margin: 0 8px">-</span>
            <el-input
              v-model="searchForm.maxPrice"
              placeholder="最高价"
              style="width: 100px"
              type="number"
            />
          </el-form-item>
          
          <el-form-item label="发布时间">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 商品列表 -->
    <div class="table-section">
      <el-card>
        <div class="table-header">
          <div class="table-title">
            <h3>商品列表</h3>
            <span class="total-count">共 {{ pagination.total }} 个商品</span>
          </div>
          <div class="table-actions">
            <el-button type="success" :disabled="selectedProducts.length === 0" @click="batchApprove">
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button type="danger" :disabled="selectedProducts.length === 0" @click="batchReject">
              <el-icon><Close /></el-icon>
              批量下架
            </el-button>
            <el-button type="primary" @click="exportProducts">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
        
        <el-table
          v-loading="loading"
          :data="productList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="image" label="图片" width="80">
            <template #default="{ row }">
              <el-image
                :src="row.images?.[0]"
                fit="cover"
                style="width: 60px; height: 60px; border-radius: 8px"
                :preview-src-list="row.images"
              />
            </template>
          </el-table-column>
          
          <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
          
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="category" label="分类" width="120" />
          
          <el-table-column prop="seller" label="卖家" width="120" show-overflow-tooltip />
          
          <el-table-column prop="condition" label="成色" width="80">
            <template #default="{ row }">
              <el-tag :type="getConditionType(row.condition)" size="small">
                {{ getConditionText(row.condition) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="views" label="浏览量" width="80" />
          
          <el-table-column prop="createdAt" label="发布时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewProduct(row)">
                查看
              </el-button>
              <el-button
                v-if="row.status === 0"
                type="success"
                size="small"
                @click="approveProduct(row.id)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="warning"
                size="small"
                @click="offlineProduct(row.id)"
              >
                下架
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="success"
                size="small"
                @click="onlineProduct(row.id)"
              >
                上架
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="showProductDialog"
      title="商品详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentProduct" class="product-detail">
        <div class="product-images">
          <el-carousel height="300px" indicator-position="outside">
            <el-carousel-item v-for="(image, index) in currentProduct.images" :key="index">
              <el-image
                :src="image"
                fit="contain"
                style="width: 100%; height: 100%"
              />
            </el-carousel-item>
          </el-carousel>
        </div>
        
        <div class="product-info">
          <div class="product-header">
            <h3>{{ currentProduct.title }}</h3>
            <div class="product-tags">
              <el-tag :type="getStatusType(currentProduct.status)" size="small">
                {{ getStatusText(currentProduct.status) }}
              </el-tag>
              <el-tag :type="getConditionType(currentProduct.condition)" size="small">
                {{ getConditionText(currentProduct.condition) }}
              </el-tag>
            </div>
          </div>
          
          <div class="product-price">
            <span class="price">¥{{ currentProduct.price }}</span>
            <span v-if="currentProduct.originalPrice" class="original-price">
              原价：¥{{ currentProduct.originalPrice }}
            </span>
          </div>
          
          <el-divider />
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="分类">{{ currentProduct.category }}</el-descriptions-item>
            <el-descriptions-item label="卖家">{{ currentProduct.seller }}</el-descriptions-item>
            <el-descriptions-item label="交易方式">{{ currentProduct.tradeType }}</el-descriptions-item>
            <el-descriptions-item label="交易地点">{{ currentProduct.location || '-' }}</el-descriptions-item>
            <el-descriptions-item label="浏览量">{{ currentProduct.views }}</el-descriptions-item>
            <el-descriptions-item label="收藏数">{{ currentProduct.favorites || 0 }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{ formatDate(currentProduct.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDate(currentProduct.updatedAt) }}</el-descriptions-item>
          </el-descriptions>
          
          <div class="product-description">
            <h4>商品描述</h4>
            <div class="description-content" v-html="currentProduct.description"></div>
          </div>
          
          <div v-if="currentProduct.tags?.length" class="product-tags-section">
            <h4>商品标签</h4>
            <div class="tags-list">
              <el-tag v-for="tag in currentProduct.tags" :key="tag" size="small">
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showProductDialog = false">关闭</el-button>
        <el-button
          v-if="currentProduct?.status === 0"
          type="success"
          @click="approveProduct(currentProduct.id)"
        >
          审核通过
        </el-button>
        <el-button
          v-if="currentProduct?.status === 1"
          type="warning"
          @click="offlineProduct(currentProduct.id)"
        >
          下架商品
        </el-button>
        <el-button
          v-if="currentProduct?.status === 2"
          type="success"
          @click="onlineProduct(currentProduct.id)"
        >
          重新上架
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 批量操作确认对话框 -->
    <el-dialog
      v-model="showBatchDialog"
      :title="batchAction === 'approve' ? '批量审核通过' : '批量下架商品'"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="batch-content">
        <p>
          确定要{{ batchAction === 'approve' ? '审核通过' : '下架' }}以下 {{ selectedProducts.length }} 个商品吗？
        </p>
        <div class="selected-products">
          <div v-for="product in selectedProducts" :key="product.id" class="product-item">
            <el-image
              :src="product.images?.[0]"
              fit="cover"
              style="width: 40px; height: 40px; border-radius: 4px"
            />
            <span class="product-title">{{ product.title }}</span>
          </div>
        </div>
        <div v-if="batchAction === 'reject'" class="batch-reason">
          <el-form-item label="下架原因">
            <el-input
              v-model="batchReason"
              type="textarea"
              :rows="3"
              placeholder="请输入下架原因"
            />
          </el-form-item>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button
          :type="batchAction === 'approve' ? 'success' : 'danger'"
          @click="confirmBatchAction"
        >
          确定{{ batchAction === 'approve' ? '通过' : '下架' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const showProductDialog = ref(false)
const showBatchDialog = ref(false)
const currentProduct = ref(null)
const selectedProducts = ref([])
const batchAction = ref('')
const batchReason = ref('')

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: '',
  status: '',
  minPrice: '',
  maxPrice: '',
  dateRange: []
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 分类列表
const categories = ref([
  { id: 1, name: '数码产品' },
  { id: 2, name: '图书教材' },
  { id: 3, name: '生活用品' },
  { id: 4, name: '服装配饰' },
  { id: 5, name: '运动户外' },
  { id: 6, name: '其他' }
])

// 商品列表
const productList = ref([
  {
    id: 1,
    title: 'iPhone 14 Pro 256GB 深空黑色',
    price: 7999,
    originalPrice: 8999,
    images: ['/images/products/iphone1.jpg', '/images/products/iphone2.jpg'],
    category: '数码产品',
    seller: '张同学',
    condition: 9,
    status: 0,
    views: 156,
    favorites: 23,
    tradeType: '面交',
    location: '北京大学',
    description: '<p>全新未拆封iPhone 14 Pro，深空黑色256GB版本。</p><p>因为换了安卓手机所以出售，价格可小刀。</p>',
    tags: ['全新', '未拆封', '可小刀'],
    createdAt: '2024-01-15 10:30:00',
    updatedAt: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    title: 'MacBook Air M2 13英寸 8+256GB',
    price: 8999,
    originalPrice: 9999,
    images: ['/images/products/macbook1.jpg'],
    category: '数码产品',
    seller: '李同学',
    condition: 8,
    status: 1,
    views: 89,
    favorites: 15,
    tradeType: '快递',
    location: '',
    description: '<p>MacBook Air M2芯片，使用半年，9成新。</p><p>配件齐全，无磕碰，性能完好。</p>',
    tags: ['9成新', '配件齐全'],
    createdAt: '2024-01-14 16:20:00',
    updatedAt: '2024-01-14 16:20:00'
  },
  {
    id: 3,
    title: '小米13 Ultra 512GB 黑色',
    price: 5499,
    originalPrice: 5999,
    images: ['/images/products/xiaomi1.jpg'],
    category: '数码产品',
    seller: '王同学',
    condition: 7,
    status: 2,
    views: 45,
    favorites: 8,
    tradeType: '面交',
    location: '清华大学',
    description: '<p>小米13 Ultra，徕卡影像系统。</p><p>使用一年，有轻微使用痕迹，功能正常。</p>',
    tags: ['徕卡影像', '功能正常'],
    createdAt: '2024-01-13 14:15:00',
    updatedAt: '2024-01-15 09:20:00'
  }
])

// 搜索商品
const handleSearch = async () => {
  pagination.page = 1
  await fetchProducts()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    categoryId: '',
    status: '',
    minPrice: '',
    maxPrice: '',
    dateRange: []
  })
  handleSearch()
}

// 获取商品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    // 调用API获取商品列表
    // const response = await getProductList(params)
    // productList.value = response.data.list
    // pagination.total = response.data.total
    
    // 模拟数据
    pagination.total = 234
    
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 查看商品详情
const viewProduct = (product) => {
  currentProduct.value = product
  showProductDialog.value = true
}

// 审核通过商品
const approveProduct = async (productId) => {
  try {
    await ElMessageBox.confirm(
      '确定要审核通过该商品吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API审核通过
    // await approveProduct(productId)
    
    // 更新商品状态
    const product = productList.value.find(p => p.id === productId)
    if (product) {
      product.status = 1
    }
    
    // 如果是在详情对话框中操作，更新当前商品信息
    if (currentProduct.value && currentProduct.value.id === productId) {
      currentProduct.value.status = 1
    }
    
    ElMessage.success('商品审核通过')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

// 下架商品
const offlineProduct = async (productId) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入下架原因',
      '下架商品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入下架原因',
        inputValidator: (value) => {
          if (!value || !value.trim()) {
            return '请输入下架原因'
          }
          return true
        }
      }
    )
    
    // 调用API下架商品
    // await offlineProduct(productId, reason)
    
    // 更新商品状态
    const product = productList.value.find(p => p.id === productId)
    if (product) {
      product.status = 2
    }
    
    // 如果是在详情对话框中操作，更新当前商品信息
    if (currentProduct.value && currentProduct.value.id === productId) {
      currentProduct.value.status = 2
    }
    
    ElMessage.success('商品已下架')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败:', error)
      ElMessage.error('下架失败')
    }
  }
}

// 重新上架商品
const onlineProduct = async (productId) => {
  try {
    await ElMessageBox.confirm(
      '确定要重新上架该商品吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API重新上架
    // await onlineProduct(productId)
    
    // 更新商品状态
    const product = productList.value.find(p => p.id === productId)
    if (product) {
      product.status = 1
    }
    
    // 如果是在详情对话框中操作，更新当前商品信息
    if (currentProduct.value && currentProduct.value.id === productId) {
      currentProduct.value.status = 1
    }
    
    ElMessage.success('商品已重新上架')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('上架失败:', error)
      ElMessage.error('上架失败')
    }
  }
}

// 批量审核通过
const batchApprove = () => {
  batchAction.value = 'approve'
  showBatchDialog.value = true
}

// 批量下架
const batchReject = () => {
  batchAction.value = 'reject'
  batchReason.value = ''
  showBatchDialog.value = true
}

// 确认批量操作
const confirmBatchAction = async () => {
  if (batchAction.value === 'reject' && !batchReason.value.trim()) {
    ElMessage.warning('请输入下架原因')
    return
  }
  
  try {
    const productIds = selectedProducts.value.map(p => p.id)
    
    if (batchAction.value === 'approve') {
      // 调用批量审核通过API
      // await batchApproveProducts(productIds)
      
      // 更新商品状态
      selectedProducts.value.forEach(product => {
        product.status = 1
      })
      
      ElMessage.success(`成功审核通过 ${selectedProducts.value.length} 个商品`)
    } else {
      // 调用批量下架API
      // await batchOfflineProducts(productIds, batchReason.value)
      
      // 更新商品状态
      selectedProducts.value.forEach(product => {
        product.status = 2
      })
      
      ElMessage.success(`成功下架 ${selectedProducts.value.length} 个商品`)
    }
    
    showBatchDialog.value = false
    selectedProducts.value = []
    
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败')
  }
}

// 导出商品数据
const exportProducts = async () => {
  try {
    ElMessage.info('正在导出商品数据...')
    // 调用导出API
    // await exportProductData(searchForm)
    ElMessage.success('商品数据导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedProducts.value = selection
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchProducts()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchProducts()
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',  // 待审核
    1: 'success',  // 已上架
    2: 'danger',   // 已下架
    3: 'info'      // 已售出
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已上架',
    2: '已下架',
    3: '已售出'
  }
  return texts[status] || '未知'
}

// 获取成色类型
const getConditionType = (condition) => {
  if (condition >= 9) return 'success'
  if (condition >= 7) return 'warning'
  return 'danger'
}

// 获取成色文本
const getConditionText = (condition) => {
  return `${condition}成新`
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 组件挂载时获取数据
onMounted(() => {
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.product-management-page {
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    h1 {
      font-size: 28px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }
    
    p {
      font-size: 16px;
      color: var(--text-secondary);
      margin: 0;
    }
  }
  
  .search-section {
    margin-bottom: 24px;
    
    .search-form {
      .el-form-item {
        margin-bottom: 16px;
      }
    }
  }
  
  .table-section {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .table-title {
        display: flex;
        align-items: center;
        gap: 12px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
        
        .total-count {
          font-size: 14px;
          color: var(--text-secondary);
        }
      }
      
      .table-actions {
        display: flex;
        gap: 8px;
      }
    }
    
    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
  
  .price {
    font-weight: 600;
    color: var(--danger-color);
  }
}

// 商品详情对话框样式
.product-detail {
  .product-images {
    margin-bottom: 24px;
    
    :deep(.el-carousel__item) {
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-secondary);
    }
  }
  
  .product-info {
    .product-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
      
      h3 {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
        flex: 1;
      }
      
      .product-tags {
        display: flex;
        gap: 8px;
        flex-shrink: 0;
      }
    }
    
    .product-price {
      margin-bottom: 20px;
      
      .price {
        font-size: 24px;
        font-weight: 700;
        color: var(--danger-color);
      }
      
      .original-price {
        margin-left: 12px;
        font-size: 14px;
        color: var(--text-secondary);
        text-decoration: line-through;
      }
    }
    
    .product-description {
      margin-top: 20px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      .description-content {
        font-size: 14px;
        color: var(--text-secondary);
        line-height: 1.6;
        
        :deep(p) {
          margin: 8px 0;
        }
      }
    }
    
    .product-tags-section {
      margin-top: 20px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      .tags-list {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }
  }
}

// 批量操作对话框样式
.batch-content {
  .selected-products {
    max-height: 200px;
    overflow-y: auto;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 12px;
    margin: 16px 0;
    
    .product-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 8px 0;
      border-bottom: 1px solid var(--border-color-light);
      
      &:last-child {
        border-bottom: none;
      }
      
      .product-title {
        font-size: 14px;
        color: var(--text-primary);
      }
    }
  }
  
  .batch-reason {
    margin-top: 16px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-management-page {
    padding: 16px;
    
    .search-form {
      .el-form-item {
        display: block;
        margin-right: 0;
        
        .el-input,
        .el-select,
        .el-date-picker {
          width: 100% !important;
        }
      }
    }
    
    .table-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      
      .table-actions {
        width: 100%;
        justify-content: flex-start;
        flex-wrap: wrap;
      }
    }
  }
  
  .product-detail {
    .product-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background: var(--bg-secondary);
      color: var(--text-primary);
      font-weight: 600;
    }
  }
  
  .el-table__row {
    &:hover {
      background: var(--bg-secondary);
    }
  }
}

// 按钮样式优化
:deep(.el-button--small) {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}
</style>