<template>
  <div class="content-audit">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>内容审核</h1>
      <p>审核商品信息和用户评论</p>
    </div>

    <!-- 审核统计 -->
    <div class="audit-stats">
      <div class="stat-item">
        <span class="stat-number">{{ stats.pendingProducts }}</span>
        <span class="stat-label">待审核商品</span>
      </div>
      <div class="stat-item">
        <span class="stat-number">{{ stats.pendingComments }}</span>
        <span class="stat-label">待审核评论</span>
      </div>
      <div class="stat-item">
        <span class="stat-number">{{ stats.todayAudited }}</span>
        <span class="stat-label">今日已审核</span>
      </div>
    </div>

    <!-- 审核选项卡 -->
    <div class="audit-tabs">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'products' }"
        @click="switchTab('products')"
      >
        商品审核 ({{ stats.pendingProducts }})
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'comments' }"
        @click="switchTab('comments')"
      >
        评论审核 ({{ stats.pendingComments }})
      </button>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <select v-model="filters.status" @change="fetchData">
          <option value="">全部状态</option>
          <option value="PENDING">待审核</option>
          <option value="APPROVED">已通过</option>
          <option value="REJECTED">已拒绝</option>
        </select>
        
        <select v-model="filters.category" @change="fetchData" v-if="activeTab === 'products'">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="搜索关键词" 
          @keyup.enter="fetchData"
        />
        
        <button @click="fetchData" class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
      
      <div class="filter-right">
        <button @click="batchApprove" class="batch-btn approve" :disabled="selectedItems.length === 0">
          <i class="fas fa-check"></i>
          批量通过
        </button>
        <button @click="batchReject" class="batch-btn reject" :disabled="selectedItems.length === 0">
          <i class="fas fa-times"></i>
          批量拒绝
        </button>
      </div>
    </div>

    <!-- 商品审核列表 -->
    <div v-if="activeTab === 'products'" class="audit-list">
      <div class="list-header">
        <label class="checkbox-all">
          <input 
            type="checkbox" 
            :checked="isAllSelected" 
            @change="toggleSelectAll"
          />
          全选
        </label>
        <span>共 {{ pagination.total }} 条记录</span>
      </div>
      
      <div class="product-list">
        <div 
          v-for="product in products" 
          :key="product.id" 
          class="product-item"
          :class="{ selected: selectedItems.includes(product.id) }"
        >
          <label class="item-checkbox">
            <input 
              type="checkbox" 
              :value="product.id" 
              v-model="selectedItems"
            />
          </label>
          
          <div class="product-images">
            <img 
              v-for="(image, index) in product.images.slice(0, 3)" 
              :key="index" 
              :src="image" 
              :alt="product.title"
              class="product-image"
            />
            <span v-if="product.images.length > 3" class="more-images">
              +{{ product.images.length - 3 }}
            </span>
          </div>
          
          <div class="product-info">
            <h3>{{ product.title }}</h3>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-meta">
              <span class="price">¥{{ product.price }}</span>
              <span class="category">{{ product.categoryName }}</span>
              <span class="seller">{{ product.sellerName }}</span>
              <span class="time">{{ formatTime(product.createdAt) }}</span>
            </div>
          </div>
          
          <div class="product-status">
            <span class="status" :class="product.auditStatus.toLowerCase()">
              {{ getStatusText(product.auditStatus) }}
            </span>
            <div class="audit-actions">
              <button 
                @click="approveItem(product.id)" 
                class="action-btn approve"
                :disabled="product.auditStatus !== 'PENDING'"
              >
                <i class="fas fa-check"></i>
                通过
              </button>
              <button 
                @click="rejectItem(product.id)" 
                class="action-btn reject"
                :disabled="product.auditStatus !== 'PENDING'"
              >
                <i class="fas fa-times"></i>
                拒绝
              </button>
              <button @click="viewProduct(product)" class="action-btn view">
                <i class="fas fa-eye"></i>
                详情
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论审核列表 -->
    <div v-if="activeTab === 'comments'" class="audit-list">
      <div class="list-header">
        <label class="checkbox-all">
          <input 
            type="checkbox" 
            :checked="isAllSelected" 
            @change="toggleSelectAll"
          />
          全选
        </label>
        <span>共 {{ pagination.total }} 条记录</span>
      </div>
      
      <div class="comment-list">
        <div 
          v-for="comment in comments" 
          :key="comment.id" 
          class="comment-item"
          :class="{ selected: selectedItems.includes(comment.id) }"
        >
          <label class="item-checkbox">
            <input 
              type="checkbox" 
              :value="comment.id" 
              v-model="selectedItems"
            />
          </label>
          
          <div class="comment-content">
            <div class="comment-header">
              <img :src="comment.userAvatar || '/default-avatar.png'" :alt="comment.userName" class="user-avatar" />
              <div class="user-info">
                <h4>{{ comment.userName }}</h4>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <span class="status" :class="comment.auditStatus.toLowerCase()">
                {{ getStatusText(comment.auditStatus) }}
              </span>
            </div>
            
            <div class="comment-text">
              <p>{{ comment.content }}</p>
              <div v-if="comment.images && comment.images.length > 0" class="comment-images">
                <img 
                  v-for="(image, index) in comment.images" 
                  :key="index" 
                  :src="image" 
                  alt="评论图片"
                  class="comment-image"
                />
              </div>
            </div>
            
            <div class="comment-target">
              <span>评论商品：</span>
              <a @click="viewProduct(comment.product)" class="product-link">
                {{ comment.productTitle }}
              </a>
            </div>
          </div>
          
          <div class="comment-actions">
            <button 
              @click="approveItem(comment.id)" 
              class="action-btn approve"
              :disabled="comment.auditStatus !== 'PENDING'"
            >
              <i class="fas fa-check"></i>
              通过
            </button>
            <button 
              @click="rejectItem(comment.id)" 
              class="action-btn reject"
              :disabled="comment.auditStatus !== 'PENDING'"
            >
              <i class="fas fa-times"></i>
              拒绝
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
    <div v-if="showProductModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>商品详情</h3>
          <button @click="closeModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedProduct">
          <div class="product-images-modal">
            <img 
              v-for="(image, index) in selectedProduct.images" 
              :key="index" 
              :src="image" 
              :alt="selectedProduct.title"
              class="modal-image"
            />
          </div>
          
          <div class="product-details-modal">
            <h4>{{ selectedProduct.title }}</h4>
            <p class="modal-price">¥{{ selectedProduct.price }}</p>
            <p class="modal-category">分类：{{ selectedProduct.categoryName }}</p>
            <p class="modal-condition">新旧程度：{{ selectedProduct.condition }}</p>
            <p class="modal-location">交易地点：{{ selectedProduct.location }}</p>
            <p class="modal-contact">联系方式：{{ selectedProduct.contact }}</p>
            <div class="modal-description">
              <h5>商品描述</h5>
              <p>{{ selectedProduct.description }}</p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="approveItem(selectedProduct.id)" class="modal-btn approve">
            通过审核
          </button>
          <button @click="rejectItem(selectedProduct.id)" class="modal-btn reject">
            拒绝审核
          </button>
        </div>
      </div>
    </div>

    <!-- 拒绝原因弹窗 -->
    <div v-if="showRejectModal" class="modal-overlay" @click="closeRejectModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>拒绝原因</h3>
          <button @click="closeRejectModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="reject-reasons">
            <label v-for="reason in rejectReasons" :key="reason" class="reason-option">
              <input 
                type="checkbox" 
                :value="reason" 
                v-model="selectedReasons"
              />
              <span>{{ reason }}</span>
            </label>
          </div>
          
          <div class="custom-reason">
            <label>其他原因：</label>
            <textarea 
              v-model="customReason" 
              placeholder="请输入具体的拒绝原因" 
              rows="3"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeRejectModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="confirmReject" class="modal-btn reject">
            确认拒绝
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
  name: 'ContentAudit',
  setup() {
    const activeTab = ref('products')
    const loading = ref(false)
    const products = ref([])
    const comments = ref([])
    const categories = ref([])
    const selectedItems = ref([])
    const showProductModal = ref(false)
    const showRejectModal = ref(false)
    const selectedProduct = ref(null)
    const rejectItemId = ref(null)
    const selectedReasons = ref([])
    const customReason = ref('')
    
    const stats = reactive({
      pendingProducts: 0,
      pendingComments: 0,
      todayAudited: 0
    })
    
    const filters = reactive({
      status: 'PENDING',
      category: '',
      keyword: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
      pages: 0
    })
    
    const rejectReasons = [
      '商品信息不真实',
      '图片与描述不符',
      '价格明显不合理',
      '涉嫌违规商品',
      '信息不完整',
      '重复发布',
      '内容违规'
    ]

    // 计算属性
    const isAllSelected = computed(() => {
      const currentList = activeTab.value === 'products' ? products.value : comments.value
      return currentList.length > 0 && selectedItems.value.length === currentList.length
    })

    // 切换选项卡
    const switchTab = (tab) => {
      activeTab.value = tab
      selectedItems.value = []
      pagination.current = 1
      fetchData()
    }

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/audit/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取分类列表
    const fetchCategories = async () => {
      try {
        const response = await api.get('/categories')
        categories.value = response.data
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    }

    // 获取数据
    const fetchData = async () => {
      loading.value = true
      try {
        const endpoint = activeTab.value === 'products' ? '/admin/audit/products' : '/admin/audit/comments'
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...filters
        }
        
        const response = await api.get(endpoint, { params })
        
        if (activeTab.value === 'products') {
          products.value = response.data.records
        } else {
          comments.value = response.data.records
        }
        
        pagination.total = response.data.total
        pagination.pages = response.data.pages
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 全选/取消全选
    const toggleSelectAll = () => {
      const currentList = activeTab.value === 'products' ? products.value : comments.value
      if (isAllSelected.value) {
        selectedItems.value = []
      } else {
        selectedItems.value = currentList.map(item => item.id)
      }
    }

    // 审核通过
    const approveItem = async (id) => {
      try {
        const endpoint = activeTab.value === 'products' ? '/admin/audit/products/approve' : '/admin/audit/comments/approve'
        await api.post(endpoint, { id })
        
        alert('审核通过成功')
        await fetchData()
        await fetchStats()
        closeModal()
      } catch (error) {
        console.error('审核通过失败:', error)
        alert('审核通过失败，请重试')
      }
    }

    // 审核拒绝
    const rejectItem = (id) => {
      rejectItemId.value = id
      selectedReasons.value = []
      customReason.value = ''
      showRejectModal.value = true
    }

    // 确认拒绝
    const confirmReject = async () => {
      if (selectedReasons.value.length === 0 && !customReason.value.trim()) {
        alert('请选择或填写拒绝原因')
        return
      }
      
      try {
        const endpoint = activeTab.value === 'products' ? '/admin/audit/products/reject' : '/admin/audit/comments/reject'
        const reasons = [...selectedReasons.value]
        if (customReason.value.trim()) {
          reasons.push(customReason.value.trim())
        }
        
        await api.post(endpoint, {
          id: rejectItemId.value,
          reason: reasons.join('; ')
        })
        
        alert('审核拒绝成功')
        await fetchData()
        await fetchStats()
        closeRejectModal()
        closeModal()
      } catch (error) {
        console.error('审核拒绝失败:', error)
        alert('审核拒绝失败，请重试')
      }
    }

    // 批量通过
    const batchApprove = async () => {
      if (selectedItems.value.length === 0) return
      
      if (!confirm(`确定要批量通过 ${selectedItems.value.length} 个项目吗？`)) return
      
      try {
        const endpoint = activeTab.value === 'products' ? '/admin/audit/products/batch-approve' : '/admin/audit/comments/batch-approve'
        await api.post(endpoint, { ids: selectedItems.value })
        
        alert('批量审核通过成功')
        selectedItems.value = []
        await fetchData()
        await fetchStats()
      } catch (error) {
        console.error('批量审核通过失败:', error)
        alert('批量审核通过失败，请重试')
      }
    }

    // 批量拒绝
    const batchReject = () => {
      if (selectedItems.value.length === 0) return
      
      rejectItemId.value = selectedItems.value
      selectedReasons.value = []
      customReason.value = ''
      showRejectModal.value = true
    }

    // 查看商品详情
    const viewProduct = (product) => {
      selectedProduct.value = product
      showProductModal.value = true
    }

    // 关闭弹窗
    const closeModal = () => {
      showProductModal.value = false
      selectedProduct.value = null
    }

    // 关闭拒绝弹窗
    const closeRejectModal = () => {
      showRejectModal.value = false
      rejectItemId.value = null
    }

    // 分页
    const changePage = (page) => {
      pagination.current = page
      fetchData()
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        PENDING: '待审核',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
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
      fetchData()
    })

    return {
      activeTab,
      loading,
      products,
      comments,
      categories,
      selectedItems,
      showProductModal,
      showRejectModal,
      selectedProduct,
      selectedReasons,
      customReason,
      rejectReasons,
      stats,
      filters,
      pagination,
      isAllSelected,
      switchTab,
      fetchData,
      toggleSelectAll,
      approveItem,
      rejectItem,
      confirmReject,
      batchApprove,
      batchReject,
      viewProduct,
      closeModal,
      closeRejectModal,
      changePage,
      getStatusText,
      formatTime
    }
  }
}
</script>

<style scoped>
.content-audit {
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

.audit-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.stat-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 600;
  color: #007bff;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.audit-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
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

.search-btn {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.filter-right {
  display: flex;
  gap: 0.5rem;
}

.batch-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.batch-btn.approve {
  background: #28a745;
  color: white;
}

.batch-btn.reject {
  background: #dc3545;
  color: white;
}

.batch-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.audit-list {
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

.checkbox-all {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.product-list,
.comment-list {
  padding: 0;
}

.product-item,
.comment-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  transition: background-color 0.3s;
}

.product-item:hover,
.comment-item:hover {
  background-color: #f8f9fa;
}

.product-item.selected,
.comment-item.selected {
  background-color: #e3f2fd;
}

.item-checkbox {
  cursor: pointer;
}

.product-images {
  display: flex;
  gap: 0.5rem;
  position: relative;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.more-images {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.25rem;
  border-radius: 4px;
  font-size: 0.7rem;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
}

.product-desc {
  margin: 0 0 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
}

.price {
  color: #dc3545;
  font-weight: 600;
}

.category,
.seller,
.time {
  color: #666;
}

.product-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status.pending {
  background: #fff3cd;
  color: #856404;
}

.status.approved {
  background: #d4edda;
  color: #155724;
}

.status.rejected {
  background: #f8d7da;
  color: #721c24;
}

.audit-actions {
  display: flex;
  gap: 0.25rem;
}

.action-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s;
}

.action-btn.approve {
  background: #28a745;
  color: white;
}

.action-btn.reject {
  background: #dc3545;
  color: white;
}

.action-btn.view {
  background: #007bff;
  color: white;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info h4 {
  margin: 0;
  font-size: 0.9rem;
  color: #333;
}

.comment-time {
  font-size: 0.8rem;
  color: #666;
}

.comment-text {
  margin-bottom: 0.5rem;
}

.comment-text p {
  margin: 0 0 0.5rem 0;
  color: #333;
  line-height: 1.5;
}

.comment-images {
  display: flex;
  gap: 0.5rem;
}

.comment-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.comment-target {
  font-size: 0.8rem;
  color: #666;
}

.product-link {
  color: #007bff;
  cursor: pointer;
  text-decoration: underline;
}

.comment-actions {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
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

.product-images-modal {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.modal-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.product-details-modal h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.modal-price {
  font-size: 1.2rem;
  color: #dc3545;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.modal-category,
.modal-condition,
.modal-location,
.modal-contact {
  margin: 0 0 0.5rem 0;
  color: #666;
}

.modal-description {
  margin-top: 1rem;
}

.modal-description h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.modal-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.reject-reasons {
  margin-bottom: 1rem;
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
}

.modal-btn.approve {
  background: #28a745;
  color: white;
}

.modal-btn.reject {
  background: #dc3545;
  color: white;
}

.modal-btn.cancel {
  background: #6c757d;
  color: white;
}

@media (max-width: 768px) {
  .content-audit {
    padding: 1rem;
  }
  
  .audit-stats {
    flex-direction: column;
    gap: 1rem;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
  
  .product-item,
  .comment-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
}
</style>