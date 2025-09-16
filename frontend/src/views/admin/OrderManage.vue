<template>
  <div class="order-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>订单管理</h1>
      <p>管理平台订单和纠纷处理</p>
    </div>

    <!-- 订单统计 -->
    <div class="order-stats">
      <div class="stat-card">
        <div class="stat-icon total-icon">
          <i class="fas fa-shopping-cart"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalOrders }}</h3>
          <p>总订单数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon pending-icon">
          <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.pendingOrders }}</h3>
          <p>待处理订单</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon completed-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.completedOrders }}</h3>
          <p>已完成订单</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon dispute-icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.disputeOrders }}</h3>
          <p>纠纷订单</p>
        </div>
      </div>
    </div>

    <!-- 管理选项卡 -->
    <div class="manage-tabs">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'orders' }"
        @click="switchTab('orders')"
      >
        订单列表
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'disputes' }"
        @click="switchTab('disputes')"
      >
        纠纷处理 ({{ stats.disputeOrders }})
      </button>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <select v-model="filters.status" @change="fetchData">
          <option value="">全部状态</option>
          <option value="PENDING">待支付</option>
          <option value="PAID">已支付</option>
          <option value="SHIPPED">已发货</option>
          <option value="DELIVERED">已送达</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
          <option value="REFUNDED">已退款</option>
        </select>
        
        <select v-model="filters.paymentMethod" @change="fetchData" v-if="activeTab === 'orders'">
          <option value="">全部支付方式</option>
          <option value="ONLINE">线上支付</option>
          <option value="OFFLINE">线下支付</option>
        </select>
        
        <select v-model="filters.disputeStatus" @change="fetchData" v-if="activeTab === 'disputes'">
          <option value="">全部纠纷状态</option>
          <option value="PENDING">待处理</option>
          <option value="PROCESSING">处理中</option>
          <option value="RESOLVED">已解决</option>
          <option value="CLOSED">已关闭</option>
        </select>
        
        <input 
          type="date" 
          v-model="filters.startDate" 
          @change="fetchData"
        />
        <span>至</span>
        <input 
          type="date" 
          v-model="filters.endDate" 
          @change="fetchData"
        />
        
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="搜索订单号、商品、用户" 
          @keyup.enter="fetchData"
        />
        
        <button @click="fetchData" class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
      
      <div class="filter-right">
        <button @click="exportOrders" class="export-btn">
          <i class="fas fa-download"></i>
          导出数据
        </button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-if="activeTab === 'orders'" class="order-list">
      <div class="list-header">
        <span>共 {{ pagination.total }} 个订单</span>
      </div>
      
      <div class="orders-table">
        <table>
          <thead>
            <tr>
              <th>订单信息</th>
              <th>商品信息</th>
              <th>买家信息</th>
              <th>卖家信息</th>
              <th>金额</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id" class="order-row">
              <td>
                <div class="order-info">
                  <h4>{{ order.orderNo }}</h4>
                  <p class="payment-method">
                    <i class="fas" :class="order.paymentMethod === 'ONLINE' ? 'fa-credit-card' : 'fa-handshake'"></i>
                    {{ order.paymentMethod === 'ONLINE' ? '线上支付' : '线下支付' }}
                  </p>
                </div>
              </td>
              <td>
                <div class="product-info">
                  <img :src="order.product.images[0] || '/default-product.png'" :alt="order.product.title" class="product-image" />
                  <div class="product-details">
                    <h5>{{ order.product.title }}</h5>
                    <p>{{ order.product.category?.name }}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="user-info">
                  <img :src="order.buyer.avatar || '/default-avatar.png'" :alt="order.buyer.nickname" class="user-avatar" />
                  <div class="user-details">
                    <h5>{{ order.buyer.nickname }}</h5>
                    <p>{{ order.buyer.phone }}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="user-info">
                  <img :src="order.seller.avatar || '/default-avatar.png'" :alt="order.seller.nickname" class="user-avatar" />
                  <div class="user-details">
                    <h5>{{ order.seller.nickname }}</h5>
                    <p>{{ order.seller.phone }}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="amount-info">
                  <p class="total-amount">¥{{ order.totalAmount }}</p>
                  <p class="payment-time" v-if="order.paidAt">
                    {{ formatTime(order.paidAt) }}
                  </p>
                </div>
              </td>
              <td>
                <span class="order-status" :class="order.status.toLowerCase()">
                  {{ getOrderStatusText(order.status) }}
                </span>
              </td>
              <td>{{ formatTime(order.createdAt) }}</td>
              <td>
                <div class="order-actions">
                  <button @click="viewOrder(order)" class="action-btn view">
                    <i class="fas fa-eye"></i>
                  </button>
                  <button 
                    v-if="order.status === 'CANCELLED' || order.status === 'REFUNDED'" 
                    @click="deleteOrder(order)" 
                    class="action-btn delete"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 纠纷列表 -->
    <div v-if="activeTab === 'disputes'" class="dispute-list">
      <div class="list-header">
        <span>共 {{ pagination.total }} 个纠纷</span>
      </div>
      
      <div class="disputes-cards">
        <div 
          v-for="dispute in disputes" 
          :key="dispute.id" 
          class="dispute-card"
        >
          <div class="card-header">
            <div class="dispute-info">
              <h4>纠纷 #{{ dispute.id }}</h4>
              <p>订单号：{{ dispute.order.orderNo }}</p>
            </div>
            <span class="dispute-status" :class="dispute.status.toLowerCase()">
              {{ getDisputeStatusText(dispute.status) }}
            </span>
          </div>
          
          <div class="card-content">
            <div class="dispute-details">
              <div class="detail-row">
                <label>纠纷类型：</label>
                <span>{{ dispute.type }}</span>
              </div>
              <div class="detail-row">
                <label>申请人：</label>
                <span>{{ dispute.applicant.nickname }} ({{ dispute.applicant.phone }})</span>
              </div>
              <div class="detail-row">
                <label>被申请人：</label>
                <span>{{ dispute.respondent.nickname }} ({{ dispute.respondent.phone }})</span>
              </div>
              <div class="detail-row">
                <label>申请时间：</label>
                <span>{{ formatTime(dispute.createdAt) }}</span>
              </div>
              <div class="detail-row">
                <label>纠纷原因：</label>
                <span>{{ dispute.reason }}</span>
              </div>
            </div>
            
            <div class="product-summary">
              <h5>相关商品</h5>
              <div class="product-card">
                <img :src="dispute.order.product.images[0] || '/default-product.png'" :alt="dispute.order.product.title" class="product-image" />
                <div class="product-info">
                  <h6>{{ dispute.order.product.title }}</h6>
                  <p>¥{{ dispute.order.totalAmount }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="dispute-evidence" v-if="dispute.evidence && dispute.evidence.length > 0">
            <h5>证据材料</h5>
            <div class="evidence-grid">
              <div 
                v-for="(evidence, index) in dispute.evidence" 
                :key="index" 
                class="evidence-item"
                @click="previewImage(evidence)"
              >
                <img :src="evidence" alt="证据" />
                <div class="evidence-overlay">
                  <i class="fas fa-search-plus"></i>
                </div>
              </div>
            </div>
          </div>
          
          <div class="dispute-messages" v-if="dispute.messages && dispute.messages.length > 0">
            <h5>沟通记录</h5>
            <div class="messages-list">
              <div 
                v-for="message in dispute.messages" 
                :key="message.id" 
                class="message-item"
                :class="{ admin: message.isAdmin }"
              >
                <div class="message-header">
                  <span class="sender">{{ message.sender.nickname }}</span>
                  <span class="message-time">{{ formatTime(message.createdAt) }}</span>
                </div>
                <div class="message-content">{{ message.content }}</div>
              </div>
            </div>
          </div>
          
          <div class="card-actions" v-if="dispute.status === 'PENDING' || dispute.status === 'PROCESSING'">
            <button @click="processDispute(dispute)" class="action-btn process">
              <i class="fas fa-gavel"></i>
              处理纠纷
            </button>
            <button @click="closeDispute(dispute)" class="action-btn close">
              <i class="fas fa-times-circle"></i>
              关闭纠纷
            </button>
          </div>
          
          <div class="dispute-result" v-if="dispute.status === 'RESOLVED' || dispute.status === 'CLOSED'">
            <div class="result-info">
              <p><strong>处理结果：</strong>{{ getDisputeStatusText(dispute.status) }}</p>
              <p v-if="dispute.resolution"><strong>处理方案：</strong>{{ dispute.resolution }}</p>
              <p><strong>处理时间：</strong>{{ formatTime(dispute.updatedAt) }}</p>
            </div>
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

    <!-- 订单详情弹窗 -->
    <div v-if="showOrderModal" class="modal-overlay" @click="closeOrderModal">
      <div class="modal-content order-modal" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button @click="closeOrderModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedOrder">
          <div class="order-detail">
            <div class="detail-section">
              <h4>订单信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>订单号：</label>
                  <span>{{ selectedOrder.orderNo }}</span>
                </div>
                <div class="detail-item">
                  <label>订单状态：</label>
                  <span class="status" :class="selectedOrder.status.toLowerCase()">
                    {{ getOrderStatusText(selectedOrder.status) }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>支付方式：</label>
                  <span>{{ selectedOrder.paymentMethod === 'ONLINE' ? '线上支付' : '线下支付' }}</span>
                </div>
                <div class="detail-item">
                  <label>订单金额：</label>
                  <span class="amount">¥{{ selectedOrder.totalAmount }}</span>
                </div>
                <div class="detail-item">
                  <label>创建时间：</label>
                  <span>{{ formatTime(selectedOrder.createdAt) }}</span>
                </div>
                <div class="detail-item" v-if="selectedOrder.paidAt">
                  <label>支付时间：</label>
                  <span>{{ formatTime(selectedOrder.paidAt) }}</span>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4>商品信息</h4>
              <div class="product-detail">
                <img :src="selectedOrder.product.images[0] || '/default-product.png'" :alt="selectedOrder.product.title" class="product-image" />
                <div class="product-info">
                  <h5>{{ selectedOrder.product.title }}</h5>
                  <p>分类：{{ selectedOrder.product.category?.name }}</p>
                  <p>价格：¥{{ selectedOrder.product.price }}</p>
                  <p>交易地点：{{ selectedOrder.product.location }}</p>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4>交易双方</h4>
              <div class="users-info">
                <div class="user-card">
                  <h5>买家</h5>
                  <div class="user-detail">
                    <img :src="selectedOrder.buyer.avatar || '/default-avatar.png'" :alt="selectedOrder.buyer.nickname" class="user-avatar" />
                    <div class="user-info">
                      <p><strong>{{ selectedOrder.buyer.nickname }}</strong></p>
                      <p>{{ selectedOrder.buyer.phone }}</p>
                      <p v-if="selectedOrder.buyer.verified" class="verified">
                        <i class="fas fa-check-circle"></i>
                        已认证用户
                      </p>
                    </div>
                  </div>
                </div>
                
                <div class="user-card">
                  <h5>卖家</h5>
                  <div class="user-detail">
                    <img :src="selectedOrder.seller.avatar || '/default-avatar.png'" :alt="selectedOrder.seller.nickname" class="user-avatar" />
                    <div class="user-info">
                      <p><strong>{{ selectedOrder.seller.nickname }}</strong></p>
                      <p>{{ selectedOrder.seller.phone }}</p>
                      <p v-if="selectedOrder.seller.verified" class="verified">
                        <i class="fas fa-check-circle"></i>
                        已认证用户
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="detail-section" v-if="selectedOrder.timeline && selectedOrder.timeline.length > 0">
              <h4>订单时间线</h4>
              <div class="timeline">
                <div 
                  v-for="(item, index) in selectedOrder.timeline" 
                  :key="index" 
                  class="timeline-item"
                >
                  <div class="timeline-icon">
                    <i class="fas" :class="getTimelineIcon(item.status)"></i>
                  </div>
                  <div class="timeline-content">
                    <p>{{ item.description }}</p>
                    <span class="timeline-time">{{ formatTime(item.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 处理纠纷弹窗 -->
    <div v-if="showDisputeModal" class="modal-overlay" @click="closeDisputeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>处理纠纷</h3>
          <button @click="closeDisputeModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="dispute-resolution">
            <h5>处理方案</h5>
            <label v-for="solution in disputeSolutions" :key="solution" class="solution-option">
              <input 
                type="radio" 
                :value="solution" 
                v-model="selectedSolution"
              />
              <span>{{ solution }}</span>
            </label>
          </div>
          
          <div class="resolution-details">
            <label>处理说明：</label>
            <textarea 
              v-model="resolutionDetails" 
              placeholder="请详细说明处理方案和理由" 
              rows="4"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeDisputeModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="confirmResolution" class="modal-btn resolve">
            确认处理
          </button>
        </div>
      </div>
    </div>

    <!-- 图片预览弹窗 -->
    <div v-if="showImageModal" class="modal-overlay" @click="closeImageModal">
      <div class="image-modal" @click.stop>
        <button @click="closeImageModal" class="close-btn">
          <i class="fas fa-times"></i>
        </button>
        <img :src="previewImageUrl" alt="预览图片" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api'

export default {
  name: 'OrderManage',
  setup() {
    const activeTab = ref('orders')
    const loading = ref(false)
    const orders = ref([])
    const disputes = ref([])
    const selectedOrder = ref(null)
    const selectedDispute = ref(null)
    const showOrderModal = ref(false)
    const showDisputeModal = ref(false)
    const showImageModal = ref(false)
    const previewImageUrl = ref('')
    const selectedSolution = ref('')
    const resolutionDetails = ref('')
    
    const stats = reactive({
      totalOrders: 0,
      pendingOrders: 0,
      completedOrders: 0,
      disputeOrders: 0
    })
    
    const filters = reactive({
      status: '',
      paymentMethod: '',
      disputeStatus: '',
      startDate: '',
      endDate: '',
      keyword: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
      pages: 0
    })
    
    const disputeSolutions = [
      '支持买家，全额退款',
      '支持卖家，维持交易',
      '部分退款，协商解决',
      '重新发货，延长交易',
      '其他解决方案'
    ]

    // 切换选项卡
    const switchTab = (tab) => {
      activeTab.value = tab
      pagination.current = 1
      
      // 重置筛选条件
      Object.keys(filters).forEach(key => {
        if (key !== 'startDate' && key !== 'endDate' && key !== 'keyword') {
          filters[key] = ''
        }
      })
      
      fetchData()
    }

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/orders/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取数据
    const fetchData = async () => {
      loading.value = true
      try {
        const endpoint = activeTab.value === 'orders' ? '/admin/orders' : '/admin/disputes'
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...filters
        }
        
        const response = await api.get(endpoint, { params })
        
        if (activeTab.value === 'orders') {
          orders.value = response.data.records
        } else {
          disputes.value = response.data.records
        }
        
        pagination.total = response.data.total
        pagination.pages = response.data.pages
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 查看订单详情
    const viewOrder = async (order) => {
      try {
        const response = await api.get(`/admin/orders/${order.id}/detail`)
        selectedOrder.value = response.data
        showOrderModal.value = true
      } catch (error) {
        console.error('获取订单详情失败:', error)
        alert('获取订单详情失败')
      }
    }

    // 删除订单
    const deleteOrder = async (order) => {
      if (!confirm(`确定要删除订单 ${order.orderNo} 吗？此操作不可恢复！`)) return
      
      try {
        await api.delete(`/admin/orders/${order.id}`)
        alert('订单删除成功')
        await fetchData()
        await fetchStats()
      } catch (error) {
        console.error('删除订单失败:', error)
        alert('删除订单失败，请重试')
      }
    }

    // 处理纠纷
    const processDispute = (dispute) => {
      selectedDispute.value = dispute
      selectedSolution.value = ''
      resolutionDetails.value = ''
      showDisputeModal.value = true
    }

    // 确认处理纠纷
    const confirmResolution = async () => {
      if (!selectedSolution.value || !resolutionDetails.value.trim()) {
        alert('请选择处理方案并填写处理说明')
        return
      }
      
      try {
        await api.post('/admin/disputes/resolve', {
          disputeId: selectedDispute.value.id,
          solution: selectedSolution.value,
          details: resolutionDetails.value.trim()
        })
        
        alert('纠纷处理成功')
        await fetchData()
        await fetchStats()
        closeDisputeModal()
      } catch (error) {
        console.error('处理纠纷失败:', error)
        alert('处理纠纷失败，请重试')
      }
    }

    // 关闭纠纷
    const closeDispute = async (dispute) => {
      if (!confirm('确定要关闭这个纠纷吗？关闭后将无法再次处理。')) return
      
      try {
        await api.post('/admin/disputes/close', { disputeId: dispute.id })
        alert('纠纷关闭成功')
        await fetchData()
        await fetchStats()
      } catch (error) {
        console.error('关闭纠纷失败:', error)
        alert('关闭纠纷失败，请重试')
      }
    }

    // 导出订单数据
    const exportOrders = async () => {
      try {
        const endpoint = activeTab.value === 'orders' ? '/admin/orders/export' : '/admin/disputes/export'
        const response = await api.get(endpoint, {
          params: filters,
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${activeTab.value === 'orders' ? '订单' : '纠纷'}数据_${new Date().toISOString().slice(0, 10)}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
        alert('导出失败，请重试')
      }
    }

    // 预览图片
    const previewImage = (imageUrl) => {
      previewImageUrl.value = imageUrl
      showImageModal.value = true
    }

    // 关闭弹窗
    const closeOrderModal = () => {
      showOrderModal.value = false
      selectedOrder.value = null
    }

    const closeDisputeModal = () => {
      showDisputeModal.value = false
      selectedDispute.value = null
    }

    const closeImageModal = () => {
      showImageModal.value = false
      previewImageUrl.value = ''
    }

    // 分页
    const changePage = (page) => {
      pagination.current = page
      fetchData()
    }

    // 获取订单状态文本
    const getOrderStatusText = (status) => {
      const statusMap = {
        PENDING: '待支付',
        PAID: '已支付',
        SHIPPED: '已发货',
        DELIVERED: '已送达',
        COMPLETED: '已完成',
        CANCELLED: '已取消',
        REFUNDED: '已退款'
      }
      return statusMap[status] || status
    }

    // 获取纠纷状态文本
    const getDisputeStatusText = (status) => {
      const statusMap = {
        PENDING: '待处理',
        PROCESSING: '处理中',
        RESOLVED: '已解决',
        CLOSED: '已关闭'
      }
      return statusMap[status] || status
    }

    // 获取时间线图标
    const getTimelineIcon = (status) => {
      const icons = {
        PENDING: 'fa-clock',
        PAID: 'fa-credit-card',
        SHIPPED: 'fa-truck',
        DELIVERED: 'fa-check',
        COMPLETED: 'fa-check-circle',
        CANCELLED: 'fa-times-circle',
        REFUNDED: 'fa-undo'
      }
      return icons[status] || 'fa-info-circle'
    }

    // 格式化时间
    const formatTime = (time) => {
      return new Date(time).toLocaleString('zh-CN')
    }

    onMounted(() => {
      fetchStats()
      fetchData()
    })

    return {
      activeTab,
      loading,
      orders,
      disputes,
      selectedOrder,
      selectedDispute,
      showOrderModal,
      showDisputeModal,
      showImageModal,
      previewImageUrl,
      selectedSolution,
      resolutionDetails,
      disputeSolutions,
      stats,
      filters,
      pagination,
      switchTab,
      fetchData,
      viewOrder,
      deleteOrder,
      processDispute,
      confirmResolution,
      closeDispute,
      exportOrders,
      previewImage,
      closeOrderModal,
      closeDisputeModal,
      closeImageModal,
      changePage,
      getOrderStatusText,
      getDisputeStatusText,
      getTimelineIcon,
      formatTime
    }
  }
}
</script>

<style scoped>
.order-manage {
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

.order-stats {
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

.pending-icon {
  background: #ffc107;
}

.completed-icon {
  background: #28a745;
}

.dispute-icon {
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

.manage-tabs {
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
  flex-wrap: wrap;
}

.filter-left select,
.filter-left input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn,
.export-btn {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.export-btn {
  background: #6c757d;
}

.order-list,
.dispute-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.list-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  color: #666;
}

.orders-table {
  overflow-x: auto;
}

.orders-table table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th,
.orders-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.orders-table th {
  background: #f8f9fa;
  font-weight: 500;
  color: #333;
}

.order-row:hover {
  background-color: #f8f9fa;
}

.order-info h4 {
  margin: 0 0 0.25rem 0;
  font-size: 0.9rem;
  color: #333;
}

.payment-method {
  margin: 0;
  font-size: 0.8rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.product-info,
.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.product-image,
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.user-avatar {
  border-radius: 50%;
}

.product-details h5,
.user-details h5 {
  margin: 0 0 0.25rem 0;
  font-size: 0.9rem;
  color: #333;
}

.product-details p,
.user-details p {
  margin: 0;
  font-size: 0.8rem;
  color: #666;
}

.amount-info {
  text-align: right;
}

.total-amount {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  font-weight: 600;
  color: #dc3545;
}

.payment-time {
  margin: 0;
  font-size: 0.8rem;
  color: #666;
}

.order-status,
.dispute-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.order-status.pending {
  background: #fff3cd;
  color: #856404;
}

.order-status.paid {
  background: #d1ecf1;
  color: #0c5460;
}

.order-status.shipped {
  background: #d4edda;
  color: #155724;
}

.order-status.delivered {
  background: #d4edda;
  color: #155724;
}

.order-status.completed {
  background: #d4edda;
  color: #155724;
}

.order-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-status.refunded {
  background: #f8d7da;
  color: #721c24;
}

.dispute-status.pending {
  background: #fff3cd;
  color: #856404;
}

.dispute-status.processing {
  background: #d1ecf1;
  color: #0c5460;
}

.dispute-status.resolved {
  background: #d4edda;
  color: #155724;
}

.dispute-status.closed {
  background: #f8d7da;
  color: #721c24;
}

.order-actions {
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

.action-btn.view {
  background: #007bff;
  color: white;
}

.action-btn.delete {
  background: #dc3545;
  color: white;
}

.action-btn.process {
  background: #28a745;
  color: white;
}

.action-btn.close {
  background: #6c757d;
  color: white;
}

.disputes-cards {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dispute-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  background: #fafafa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.dispute-info h4 {
  margin: 0 0 0.25rem 0;
  color: #333;
}

.dispute-info p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.card-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  margin-bottom: 1rem;
}

.dispute-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-row {
  display: flex;
  gap: 0.5rem;
}

.detail-row label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.detail-row span {
  color: #666;
}

.product-summary h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.product-summary .product-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
}

.product-summary .product-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
}

.product-summary .product-info h6 {
  margin: 0 0 0.25rem 0;
  color: #333;
  font-size: 0.9rem;
}

.product-summary .product-info p {
  margin: 0;
  color: #dc3545;
  font-weight: 600;
}

.dispute-evidence,
.dispute-messages {
  margin-bottom: 1rem;
}

.dispute-evidence h5,
.dispute-messages h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.evidence-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 0.5rem;
}

.evidence-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.evidence-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.evidence-overlay {
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
  opacity: 0;
  transition: opacity 0.3s;
}

.evidence-item:hover .evidence-overlay {
  opacity: 1;
}

.messages-list {
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.message-item {
  padding: 0.75rem;
  background: white;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.message-item.admin {
  border-left-color: #28a745;
  background: #f8f9fa;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.sender {
  font-weight: 500;
  color: #333;
}

.message-time {
  color: #666;
  font-size: 0.8rem;
}

.message-content {
  color: #666;
  line-height: 1.5;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.dispute-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border-left: 4px solid #007bff;
}

.result-info p {
  margin: 0 0 0.5rem 0;
  color: #666;
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

.order-modal {
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

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.detail-item {
  display: flex;
  gap: 0.5rem;
}

.detail-item label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.detail-item span {
  color: #666;
}

.detail-item .status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.detail-item .amount {
  color: #dc3545;
  font-weight: 600;
}

.product-detail {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.product-detail .product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-detail .product-info h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.product-detail .product-info p {
  margin: 0 0 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.users-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.user-card {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.user-card h5 {
  margin: 0 0 1rem 0;
  color: #333;
  text-align: center;
}

.user-detail {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-detail .user-avatar {
  width: 60px;
  height: 60px;
}

.user-detail .user-info p {
  margin: 0 0 0.25rem 0;
  color: #666;
  font-size: 0.9rem;
}

.user-detail .verified {
  color: #28a745;
  font-weight: 500;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.timeline-item {
  display: flex;
  gap: 1rem;
}

.timeline-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
  padding-top: 0.5rem;
}

.timeline-content p {
  margin: 0 0 0.25rem 0;
  color: #333;
}

.timeline-time {
  color: #999;
  font-size: 0.8rem;
}

.dispute-resolution {
  margin-bottom: 1rem;
}

.dispute-resolution h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.solution-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  cursor: pointer;
}

.resolution-details label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.resolution-details textarea {
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

.modal-btn.resolve {
  background: #28a745;
  color: white;
}

.modal-btn.cancel {
  background: #6c757d;
  color: white;
}

.image-modal {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.image-modal img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-modal .close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .order-manage {
    padding: 1rem;
  }
  
  .order-stats {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-left {
    justify-content: center;
  }
  
  .orders-table {
    font-size: 0.8rem;
  }
  
  .card-content {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .users-info {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
}
</style>