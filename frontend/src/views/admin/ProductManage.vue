<template>
  <div class="product-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>商品管理</h1>
      <p>管理平台商品和违规处理</p>
    </div>

    <!-- 商品统计 -->
    <div class="product-stats">
      <div class="stat-card">
        <div class="stat-icon total-icon">
          <i class="fas fa-box"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalProducts }}</h3>
          <p>总商品数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon active-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.activeProducts }}</h3>
          <p>在售商品</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon sold-icon">
          <i class="fas fa-handshake"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.soldProducts }}</h3>
          <p>已售商品</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon banned-icon">
          <i class="fas fa-ban"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.bannedProducts }}</h3>
          <p>违规下架</p>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <select v-model="filters.status" @change="fetchProducts">
          <option value="">全部状态</option>
          <option value="ACTIVE">在售</option>
          <option value="SOLD">已售</option>
          <option value="BANNED">违规下架</option>
          <option value="DRAFT">草稿</option>
        </select>
        
        <select v-model="filters.categoryId" @change="fetchProducts">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        
        <select v-model="filters.priceRange" @change="fetchProducts">
          <option value="">全部价格</option>
          <option value="0-50">0-50元</option>
          <option value="50-100">50-100元</option>
          <option value="100-300">100-300元</option>
          <option value="300-500">300-500元</option>
          <option value="500+">500元以上</option>
        </select>
        
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="搜索商品标题、描述、用户" 
          @keyup.enter="fetchProducts"
        />
        
        <button @click="fetchProducts" class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
      
      <div class="filter-right">
        <button @click="batchAction('ban')" class="batch-btn ban" :disabled="selectedProducts.length === 0">
          <i class="fas fa-ban"></i>
          批量下架
        </button>
        <button @click="batchAction('restore')" class="batch-btn restore" :disabled="selectedProducts.length === 0">
          <i class="fas fa-undo"></i>
          批量恢复
        </button>
        <button @click="exportProducts" class="export-btn">
          <i class="fas fa-download"></i>
          导出数据
        </button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div class="list-header">
        <label class="select-all">
          <input 
            type="checkbox" 
            :checked="isAllSelected" 
            @change="toggleSelectAll"
          />
          <span>全选</span>
        </label>
        <span>共 {{ pagination.total }} 个商品</span>
      </div>
      
      <div class="products-grid">
        <div 
          v-for="product in products" 
          :key="product.id" 
          class="product-card"
          :class="{ selected: selectedProducts.includes(product.id) }"
        >
          <div class="card-header">
            <label class="product-select">
              <input 
                type="checkbox" 
                :value="product.id" 
                v-model="selectedProducts"
              />
            </label>
            <span class="product-status" :class="product.status.toLowerCase()">
              {{ getStatusText(product.status) }}
            </span>
          </div>
          
          <div class="product-image" @click="viewProduct(product)">
            <img :src="product.images[0] || '/default-product.png'" :alt="product.title" />
            <div class="image-overlay">
              <i class="fas fa-eye"></i>
            </div>
          </div>
          
          <div class="product-info">
            <h4 class="product-title" @click="viewProduct(product)">{{ product.title }}</h4>
            <p class="product-price">¥{{ product.price }}</p>
            <p class="product-category">{{ product.category?.name }}</p>
            
            <div class="seller-info">
              <img :src="product.seller.avatar || '/default-avatar.png'" :alt="product.seller.nickname" class="seller-avatar" />
              <div class="seller-details">
                <span class="seller-name">{{ product.seller.nickname }}</span>
                <span class="seller-phone">{{ product.seller.phone }}</span>
              </div>
            </div>
            
            <div class="product-meta">
              <span class="publish-time">
                <i class="fas fa-clock"></i>
                {{ formatTime(product.createdAt) }}
              </span>
              <span class="view-count">
                <i class="fas fa-eye"></i>
                {{ product.viewCount || 0 }}
              </span>
            </div>
          </div>
          
          <div class="product-actions">
            <button @click="viewProduct(product)" class="action-btn view">
              <i class="fas fa-eye"></i>
              查看
            </button>
            <button 
              v-if="product.status === 'ACTIVE'" 
              @click="banProduct(product)" 
              class="action-btn ban"
            >
              <i class="fas fa-ban"></i>
              下架
            </button>
            <button 
              v-if="product.status === 'BANNED'" 
              @click="restoreProduct(product)" 
              class="action-btn restore"
            >
              <i class="fas fa-undo"></i>
              恢复
            </button>
            <button @click="deleteProduct(product)" class="action-btn delete">
              <i class="fas fa-trash"></i>
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button 
        @click="changePage(pagination.current - 1)" 
        :disabled="pagination.current <= 1"
        class="page-btn"
      >
        上一页
      </button>
      
      <span class="page-info">
        第 {{ pagination.current }} 页，共 {{ pagination.pages }} 页
      </span>
      
      <button 
        @click="changePage(pagination.current + 1)" 
        :disabled="pagination.current >= pagination.pages"
        class="page-btn"
      >
        下一页
      </button>
    </div>

    <!-- 商品详情弹窗 -->
    <div v-if="showProductModal" class="modal-overlay" @click="closeProductModal">
      <div class="modal-content product-modal" @click.stop>
        <div class="modal-header">
          <h3>商品详情</h3>
          <button @click="closeProductModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedProduct">
          <div class="product-detail">
            <div class="product-images">
              <div class="main-image">
                <img :src="currentImage" :alt="selectedProduct.title" />
              </div>
              <div class="image-thumbnails" v-if="selectedProduct.images.length > 1">
                <img 
                  v-for="(image, index) in selectedProduct.images" 
                  :key="index" 
                  :src="image" 
                  :alt="selectedProduct.title"
                  :class="{ active: currentImage === image }"
                  @click="currentImage = image"
                />
              </div>
            </div>
            
            <div class="product-details">
              <h2>{{ selectedProduct.title }}</h2>
              <p class="price">¥{{ selectedProduct.price }}</p>
              
              <div class="detail-row">
                <label>分类：</label>
                <span>{{ selectedProduct.category?.name }}</span>
              </div>
              
              <div class="detail-row">
                <label>新旧程度：</label>
                <span>{{ selectedProduct.condition }}</span>
              </div>
              
              <div class="detail-row">
                <label>交易地点：</label>
                <span>{{ selectedProduct.location }}</span>
              </div>
              
              <div class="detail-row">
                <label>状态：</label>
                <span class="status" :class="selectedProduct.status.toLowerCase()">
                  {{ getStatusText(selectedProduct.status) }}
                </span>
              </div>
              
              <div class="detail-row">
                <label>发布时间：</label>
                <span>{{ formatTime(selectedProduct.createdAt) }}</span>
              </div>
              
              <div class="detail-row">
                <label>浏览次数：</label>
                <span>{{ selectedProduct.viewCount || 0 }}</span>
              </div>
              
              <div class="seller-section">
                <h4>卖家信息</h4>
                <div class="seller-card">
                  <img :src="selectedProduct.seller.avatar || '/default-avatar.png'" :alt="selectedProduct.seller.nickname" class="seller-avatar" />
                  <div class="seller-info">
                    <h5>{{ selectedProduct.seller.nickname }}</h5>
                    <p>{{ selectedProduct.seller.phone }}</p>
                    <p v-if="selectedProduct.seller.verified" class="verified">
                      <i class="fas fa-check-circle"></i>
                      已认证用户
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="product-description">
            <h4>商品描述</h4>
            <div class="description-content" v-html="selectedProduct.description"></div>
          </div>
          
          <div class="product-reports" v-if="selectedProduct.reports && selectedProduct.reports.length > 0">
            <h4>举报记录</h4>
            <div class="reports-list">
              <div 
                v-for="report in selectedProduct.reports" 
                :key="report.id" 
                class="report-item"
              >
                <div class="report-header">
                  <span class="reporter">{{ report.reporter.nickname }}</span>
                  <span class="report-time">{{ formatTime(report.createdAt) }}</span>
                </div>
                <div class="report-content">
                  <p><strong>举报类型：</strong>{{ report.type }}</p>
                  <p><strong>举报原因：</strong>{{ report.reason }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button 
            v-if="selectedProduct && selectedProduct.status === 'ACTIVE'" 
            @click="banProduct(selectedProduct)" 
            class="modal-btn ban"
          >
            <i class="fas fa-ban"></i>
            下架商品
          </button>
          <button 
            v-if="selectedProduct && selectedProduct.status === 'BANNED'" 
            @click="restoreProduct(selectedProduct)" 
            class="modal-btn restore"
          >
            <i class="fas fa-undo"></i>
            恢复商品
          </button>
          <button @click="closeProductModal" class="modal-btn cancel">
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- 下架原因弹窗 -->
    <div v-if="showBanModal" class="modal-overlay" @click="closeBanModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>下架商品</h3>
          <button @click="closeBanModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="ban-reasons">
            <h5>下架原因</h5>
            <label v-for="reason in banReasons" :key="reason" class="reason-option">
              <input 
                type="checkbox" 
                :value="reason" 
                v-model="selectedBanReasons"
              />
              <span>{{ reason }}</span>
            </label>
          </div>
          
          <div class="custom-reason">
            <label>其他原因：</label>
            <textarea 
              v-model="customBanReason" 
              placeholder="请输入具体的下架原因" 
              rows="3"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeBanModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="confirmBan" class="modal-btn ban">
            确认下架
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api'

export default {
  name: 'ProductManage',
  setup() {
    const loading = ref(false)
    const products = ref([])
    const categories = ref([])
    const selectedProducts = ref([])
    const selectedProduct = ref(null)
    const currentImage = ref('')
    const showProductModal = ref(false)
    const showBanModal = ref(false)
    const banProductIds = ref([])
    const selectedBanReasons = ref([])
    const customBanReason = ref('')
    
    const stats = reactive({
      totalProducts: 0,
      activeProducts: 0,
      soldProducts: 0,
      bannedProducts: 0
    })
    
    const filters = reactive({
      status: '',
      categoryId: '',
      priceRange: '',
      keyword: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 12,
      total: 0,
      pages: 0
    })
    
    const banReasons = [
      '商品信息虚假',
      '价格异常',
      '图片不符',
      '违禁物品',
      '重复发布',
      '恶意刷屏',
      '涉嫌诈骗',
      '其他违规'
    ]

    // 计算属性
    const isAllSelected = computed(() => {
      return products.value.length > 0 && selectedProducts.value.length === products.value.length
    })

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/products/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取分类
    const fetchCategories = async () => {
      try {
        const response = await api.get('/categories')
        categories.value = response.data
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    }

    // 获取商品列表
    const fetchProducts = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...filters
        }
        
        const response = await api.get('/admin/products', { params })
        products.value = response.data.records
        pagination.total = response.data.total
        pagination.pages = response.data.pages
        
        // 清空选择
        selectedProducts.value = []
      } catch (error) {
        console.error('获取商品列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 查看商品详情
    const viewProduct = async (product) => {
      try {
        const response = await api.get(`/admin/products/${product.id}/detail`)
        selectedProduct.value = response.data
        currentImage.value = response.data.images[0] || '/default-product.png'
        showProductModal.value = true
      } catch (error) {
        console.error('获取商品详情失败:', error)
        alert('获取商品详情失败')
      }
    }

    // 下架商品
    const banProduct = (product) => {
      banProductIds.value = Array.isArray(product) ? product : [product.id]
      selectedBanReasons.value = []
      customBanReason.value = ''
      showBanModal.value = true
    }

    // 确认下架
    const confirmBan = async () => {
      if (selectedBanReasons.value.length === 0 && !customBanReason.value.trim()) {
        alert('请选择或填写下架原因')
        return
      }
      
      try {
        const reasons = [...selectedBanReasons.value]
        if (customBanReason.value.trim()) {
          reasons.push(customBanReason.value.trim())
        }
        
        await api.post('/admin/products/ban', {
          productIds: banProductIds.value,
          reason: reasons.join('; ')
        })
        
        alert('商品下架成功')
        await fetchProducts()
        await fetchStats()
        closeBanModal()
        closeProductModal()
      } catch (error) {
        console.error('下架商品失败:', error)
        alert('下架商品失败，请重试')
      }
    }

    // 恢复商品
    const restoreProduct = async (product) => {
      const productIds = Array.isArray(product) ? product : [product.id]
      
      if (!confirm('确定要恢复这些商品吗？')) return
      
      try {
        await api.post('/admin/products/restore', { productIds })
        alert('商品恢复成功')
        await fetchProducts()
        await fetchStats()
        closeProductModal()
      } catch (error) {
        console.error('恢复商品失败:', error)
        alert('恢复商品失败，请重试')
      }
    }

    // 删除商品
    const deleteProduct = async (product) => {
      if (!confirm(`确定要删除商品 "${product.title}" 吗？此操作不可恢复！`)) return
      
      try {
        await api.delete(`/admin/products/${product.id}`)
        alert('商品删除成功')
        await fetchProducts()
        await fetchStats()
      } catch (error) {
        console.error('删除商品失败:', error)
        alert('删除商品失败，请重试')
      }
    }

    // 批量操作
    const batchAction = (action) => {
      if (selectedProducts.value.length === 0) {
        alert('请先选择商品')
        return
      }
      
      if (action === 'ban') {
        banProduct(selectedProducts.value)
      } else if (action === 'restore') {
        restoreProduct(selectedProducts.value)
      }
    }

    // 全选/取消全选
    const toggleSelectAll = () => {
      if (isAllSelected.value) {
        selectedProducts.value = []
      } else {
        selectedProducts.value = products.value.map(p => p.id)
      }
    }

    // 导出商品数据
    const exportProducts = async () => {
      try {
        const response = await api.get('/admin/products/export', {
          params: filters,
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `商品数据_${new Date().toISOString().slice(0, 10)}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
        alert('导出失败，请重试')
      }
    }

    // 关闭弹窗
    const closeProductModal = () => {
      showProductModal.value = false
      selectedProduct.value = null
      currentImage.value = ''
    }

    const closeBanModal = () => {
      showBanModal.value = false
      banProductIds.value = []
      selectedBanReasons.value = []
      customBanReason.value = ''
    }

    // 分页
    const changePage = (page) => {
      pagination.current = page
      fetchProducts()
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        ACTIVE: '在售',
        SOLD: '已售',
        BANNED: '违规下架',
        DRAFT: '草稿'
      }
      return statusMap[status] || status
    }

    // 格式化时间
    const formatTime = (time) => {
      return new Date(time).toLocaleString('zh-CN')
    }

    onMounted(() => {
      fetchStats()
      fetchCategories()
      fetchProducts()
    })

    return {
      loading,
      products,
      categories,
      selectedProducts,
      selectedProduct,
      currentImage,
      showProductModal,
      showBanModal,
      selectedBanReasons,
      customBanReason,
      banReasons,
      stats,
      filters,
      pagination,
      isAllSelected,
      fetchProducts,
      viewProduct,
      banProduct,
      confirmBan,
      restoreProduct,
      deleteProduct,
      batchAction,
      toggleSelectAll,
      exportProducts,
      closeProductModal,
      closeBanModal,
      changePage,
      getStatusText,
      formatTime
    }
  }
}
</script>

<style scoped>
.product-manage {
  padding: 2rem;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.product-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.total-icon {
  background: #007bff;
}

.active-icon {
  background: #28a745;
}

.sold-icon {
  background: #17a2b8;
}

.banned-icon {
  background: #dc3545;
}

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-left {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.filter-left select,
.filter-left input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filter-right {
  display: flex;
  gap: 0.5rem;
}

.search-btn,
.batch-btn,
.export-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.search-btn {
  background: #007bff;
  color: white;
}

.batch-btn.ban {
  background: #dc3545;
  color: white;
}

.batch-btn.restore {
  background: #28a745;
  color: white;
}

.batch-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.export-btn {
  background: #6c757d;
  color: white;
}

.product-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
  padding: 1rem;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  background: white;
}

.product-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-card.selected {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: #f8f9fa;
}

.product-select {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.product-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.product-status.active {
  background: #d4edda;
  color: #155724;
}

.product-status.sold {
  background: #d1ecf1;
  color: #0c5460;
}

.product-status.banned {
  background: #f8d7da;
  color: #721c24;
}

.product-status.draft {
  background: #fff3cd;
  color: #856404;
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-image:hover .image-overlay {
  opacity: 1;
}

.product-info {
  padding: 1rem;
}

.product-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-title:hover {
  color: #007bff;
}

.product-price {
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #dc3545;
}

.product-category {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 0.9rem;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.seller-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.seller-details {
  flex: 1;
}

.seller-name {
  display: block;
  font-size: 0.9rem;
  color: #333;
  font-weight: 500;
}

.seller-phone {
  display: block;
  font-size: 0.8rem;
  color: #666;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #666;
}

.product-meta span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.product-actions {
  display: flex;
  gap: 0.25rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-top: 1px solid #eee;
}

.action-btn {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  transition: all 0.3s;
}

.action-btn.view {
  background: #007bff;
  color: white;
}

.action-btn.ban {
  background: #dc3545;
  color: white;
}

.action-btn.restore {
  background: #28a745;
  color: white;
}

.action-btn.delete {
  background: #6c757d;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.product-modal {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
}

.modal-body {
  padding: 1rem;
}

.product-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.product-images {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.main-image img {
  width: 100%;
  border-radius: 8px;
}

.image-thumbnails {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
}

.image-thumbnails img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.image-thumbnails img.active,
.image-thumbnails img:hover {
  opacity: 1;
}

.product-details h2 {
  margin: 0 0 1rem 0;
  color: #333;
}

.price {
  font-size: 1.5rem;
  font-weight: 600;
  color: #dc3545;
  margin-bottom: 1rem;
}

.detail-row {
  display: flex;
  margin-bottom: 0.75rem;
}

.detail-row label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.detail-row span {
  color: #666;
}

.detail-row .status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.seller-section {
  margin-top: 2rem;
}

.seller-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.seller-card .seller-avatar {
  width: 60px;
  height: 60px;
}

.seller-card .seller-info h5 {
  margin: 0 0 0.25rem 0;
  color: #333;
}

.seller-card .seller-info p {
  margin: 0 0 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.seller-card .verified {
  color: #28a745;
  font-weight: 500;
}

.product-description {
  margin-bottom: 2rem;
}

.product-description h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.description-content {
  line-height: 1.6;
  color: #666;
}

.product-reports h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.reports-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.report-item {
  padding: 1rem;
  background: #fff3cd;
  border-radius: 8px;
  border-left: 4px solid #ffc107;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.reporter {
  font-weight: 500;
  color: #333;
}

.report-time {
  color: #666;
  font-size: 0.9rem;
}

.report-content p {
  margin: 0 0 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.ban-reasons {
  margin-bottom: 1rem;
}

.ban-reasons h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.reason-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  cursor: pointer;
}

.custom-reason label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.custom-reason textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem;
  border-top: 1px solid #eee;
}

.modal-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.modal-btn.ban {
  background: #dc3545;
  color: white;
}

.modal-btn.restore {
  background: #28a745;
  color: white;
}

.modal-btn.cancel {
  background: #6c757d;
  color: white;
}

@media (max-width: 768px) {
  .product-manage {
    padding: 1rem;
  }
  
  .product-stats {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
  
  .filter-right {
    justify-content: center;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .product-detail {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
}
</style>