<template>
  <div class="my-orders">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <h1>我的订单</h1>
        </div>
      </div>
    </div>

    <!-- 订单状态筛选 -->
    <div class="filter-section">
      <div class="container">
        <div class="filter-content">
          <el-radio-group v-model="statusFilter" @change="handleFilterChange">
            <el-radio-button label="all">全部订单</el-radio-button>
            <el-radio-button label="pending">待付款</el-radio-button>
            <el-radio-button label="paid">已付款</el-radio-button>
            <el-radio-button label="shipped">已发货</el-radio-button>
            <el-radio-button label="completed">已完成</el-radio-button>
            <el-radio-button label="cancelled">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-section">
      <div class="container">
        <div class="orders-list" v-loading="loading">
          <div 
            v-for="order in orders"
            :key="order.id"
            class="order-card"
          >
            <div class="order-header">
              <div class="order-info">
                <span class="order-number">订单号：{{ order.orderNumber }}</span>
                <span class="order-time">{{ formatTime(order.createdAt) }}</span>
              </div>
              <div class="order-status" :class="`status-${order.status}`">
                {{ getStatusText(order.status) }}
              </div>
            </div>
            
            <div class="order-content">
              <div class="product-info" @click="viewProduct(order.product.id)">
                <img :src="order.product.images[0]" :alt="order.product.title" class="product-image" />
                <div class="product-details">
                  <h3 class="product-title">{{ order.product.title }}</h3>
                  <p class="product-price">¥{{ order.product.price }}</p>
                  <p class="seller-info">卖家：{{ order.seller.nickname }}</p>
                </div>
              </div>
              
              <div class="order-amount">
                <div class="amount-info">
                  <span class="label">订单金额：</span>
                  <span class="amount">¥{{ order.totalAmount }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-actions">
              <template v-if="order.status === 'pending'">
                <el-button @click="cancelOrder(order)" size="small">取消订单</el-button>
                <el-button @click="payOrder(order)" type="primary" size="small">立即付款</el-button>
              </template>
              <template v-else-if="order.status === 'paid'">
                <el-button @click="contactSeller(order)" size="small">联系卖家</el-button>
              </template>
              <template v-else-if="order.status === 'shipped'">
                <el-button @click="confirmReceived(order)" type="primary" size="small">确认收货</el-button>
              </template>
              <template v-else-if="order.status === 'completed'">
                <el-button @click="rateOrder(order)" size="small" v-if="!order.isRated">评价订单</el-button>
                <el-button @click="viewRating(order)" size="small" v-else>查看评价</el-button>
              </template>
              <el-button @click="viewOrderDetail(order)" size="small">订单详情</el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && orders.length === 0" class="empty-state">
          <el-empty description="暂无订单">
            <el-button type="primary" @click="$router.push('/')">
              去逛逛
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px">
      <div class="payment-methods">
        <div class="payment-amount">
          <span>支付金额：</span>
          <span class="amount">¥{{ selectedOrder?.totalAmount }}</span>
        </div>
        <el-radio-group v-model="paymentMethod">
          <el-radio label="online" class="payment-option">
            <div class="payment-info">
              <el-icon><CreditCard /></el-icon>
              <span>在线支付</span>
            </div>
          </el-radio>
          <el-radio label="offline" class="payment-option">
            <div class="payment-info">
              <el-icon><Money /></el-icon>
              <span>线下支付</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment">确认支付</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog v-model="ratingDialogVisible" title="评价订单" width="500px">
      <div class="rating-form">
        <div class="rating-item">
          <label>商品评分：</label>
          <el-rate v-model="rating.productRating" :max="5" show-text />
        </div>
        <div class="rating-item">
          <label>卖家评分：</label>
          <el-rate v-model="rating.sellerRating" :max="5" show-text />
        </div>
        <div class="rating-item">
          <label>评价内容：</label>
          <el-input
            v-model="rating.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价..."
            maxlength="200"
            show-word-limit
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="ratingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRating">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CreditCard, Money } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()

// 响应式数据
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const orders = ref([])

// 支付相关
const payDialogVisible = ref(false)
const selectedOrder = ref(null)
const paymentMethod = ref('online')

// 评价相关
const ratingDialogVisible = ref(false)
const rating = ref({
  productRating: 5,
  sellerRating: 5,
  comment: ''
})

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === 'all' ? undefined : statusFilter.value
    }
    
    const response = await api.get('/user/orders', { params })
    if (response.data.code === 200) {
      orders.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  currentPage.value = 1
  fetchOrders()
}

// 分页变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

// 查看商品详情
const viewProduct = (productId) => {
  router.push(`/products/${productId}`)
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单「${order.orderNumber}」吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.put(`/user/orders/${order.id}/cancel`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 支付订单
const payOrder = (order) => {
  selectedOrder.value = order
  payDialogVisible.value = true
}

// 确认支付
const confirmPayment = async () => {
  try {
    await api.post(`/user/orders/${selectedOrder.value.id}/pay`, {
      paymentMethod: paymentMethod.value
    })
    
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

// 联系卖家
const contactSeller = (order) => {
  // 跳转到聊天页面或显示联系方式
  ElMessage.info('联系卖家功能开发中...')
}

// 确认收货
const confirmReceived = async (order) => {
  try {
    await ElMessageBox.confirm(
      '确定已收到商品吗？确认后订单将完成。',
      '确认收货',
      {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await api.put(`/user/orders/${order.id}/confirm`)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 评价订单
const rateOrder = (order) => {
  selectedOrder.value = order
  rating.value = {
    productRating: 5,
    sellerRating: 5,
    comment: ''
  }
  ratingDialogVisible.value = true
}

// 提交评价
const submitRating = async () => {
  try {
    await api.post(`/user/orders/${selectedOrder.value.id}/rate`, rating.value)
    ElMessage.success('评价提交成功')
    ratingDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('评价提交失败')
  }
}

// 查看评价
const viewRating = (order) => {
  ElMessage.info('查看评价功能开发中...')
}

// 查看订单详情
const viewOrderDetail = (order) => {
  router.push(`/user/orders/${order.id}`)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 初始化
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.page-header {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
}

.header-content {
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

.orders-section {
  padding: 20px 0;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: var(--fill-light);
  border-bottom: 1px solid var(--border-light);
}

.order-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--text-regular);
}

.order-number {
  font-weight: 500;
  color: var(--text-primary);
}

.order-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status-paid {
  background: #e6f7ff;
  color: #1890ff;
}

.status-shipped {
  background: #f6ffed;
  color: #52c41a;
}

.status-completed {
  background: #f6ffed;
  color: #52c41a;
}

.status-cancelled {
  background: #fff2f0;
  color: #ff4d4f;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.product-info {
  display: flex;
  gap: 15px;
  cursor: pointer;
  flex: 1;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

.product-details {
  flex: 1;
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
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: var(--danger-color);
  margin-bottom: 5px;
}

.seller-info {
  font-size: 14px;
  color: var(--text-secondary);
}

.order-amount {
  text-align: right;
}

.amount-info {
  font-size: 14px;
}

.amount {
  font-size: 18px;
  font-weight: 600;
  color: var(--danger-color);
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
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

.payment-methods {
  padding: 20px 0;
}

.payment-amount {
  text-align: center;
  font-size: 18px;
  margin-bottom: 20px;
}

.payment-amount .amount {
  font-weight: 600;
  color: var(--danger-color);
}

.payment-option {
  display: block;
  width: 100%;
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid var(--border-light);
  border-radius: 6px;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: var(--primary-color);
}

.payment-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-form {
  padding: 20px 0;
}

.rating-item {
  margin-bottom: 20px;
}

.rating-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .order-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .order-amount {
    text-align: left;
    width: 100%;
  }
  
  .order-actions {
    flex-wrap: wrap;
  }
  
  .order-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>