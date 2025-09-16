<template>
  <div class="order-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="back-btn" @click="$router.go(-1)">
            <el-icon><ArrowLeft /></el-icon>
          </div>
          <h1>订单详情</h1>
        </div>
      </div>
    </div>

    <div class="order-content" v-loading="loading">
      <div class="container" v-if="order">
        <!-- 订单状态 -->
        <div class="status-section">
          <div class="status-card">
            <div class="status-icon" :class="`status-${order.status}`">
              <el-icon v-if="order.status === 'pending'"><Clock /></el-icon>
              <el-icon v-else-if="order.status === 'paid'"><Money /></el-icon>
              <el-icon v-else-if="order.status === 'shipped'"><Box /></el-icon>
              <el-icon v-else-if="order.status === 'completed'"><Check /></el-icon>
              <el-icon v-else><Close /></el-icon>
            </div>
            <div class="status-info">
              <h2 class="status-title">{{ getStatusTitle(order.status) }}</h2>
              <p class="status-desc">{{ getStatusDesc(order.status) }}</p>
            </div>
          </div>
          
          <!-- 订单进度 -->
          <div class="progress-steps">
            <el-steps :active="getStepActive(order.status)" finish-status="success">
              <el-step title="订单创建" :description="formatTime(order.createdAt)" />
              <el-step title="付款完成" :description="order.paidAt ? formatTime(order.paidAt) : ''" />
              <el-step title="商品发货" :description="order.shippedAt ? formatTime(order.shippedAt) : ''" />
              <el-step title="确认收货" :description="order.completedAt ? formatTime(order.completedAt) : ''" />
            </el-steps>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="product-section">
          <div class="section-title">商品信息</div>
          <div class="product-card">
            <div class="product-info" @click="viewProduct">
              <img :src="order.product.images[0]" :alt="order.product.title" class="product-image" />
              <div class="product-details">
                <h3 class="product-title">{{ order.product.title }}</h3>
                <p class="product-desc">{{ order.product.description }}</p>
                <div class="product-meta">
                  <span class="price">¥{{ order.product.price }}</span>
                  <span class="quantity">数量：1</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 交易信息 -->
        <div class="trade-section">
          <div class="section-title">交易信息</div>
          <div class="trade-card">
            <div class="trade-item">
              <span class="label">订单编号：</span>
              <span class="value">{{ order.orderNumber }}</span>
              <el-button @click="copyOrderNumber" size="small" text>复制</el-button>
            </div>
            <div class="trade-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatTime(order.createdAt) }}</span>
            </div>
            <div class="trade-item" v-if="order.paidAt">
              <span class="label">付款时间：</span>
              <span class="value">{{ formatTime(order.paidAt) }}</span>
            </div>
            <div class="trade-item">
              <span class="label">支付方式：</span>
              <span class="value">{{ getPaymentMethodText(order.paymentMethod) }}</span>
            </div>
            <div class="trade-item total">
              <span class="label">订单金额：</span>
              <span class="value amount">¥{{ order.totalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section">
          <div class="section-title">卖家信息</div>
          <div class="seller-card">
            <div class="seller-info">
              <el-avatar :size="50" :src="order.seller.avatar" />
              <div class="seller-details">
                <h4 class="seller-name">{{ order.seller.nickname }}</h4>
                <div class="seller-meta">
                  <span class="rating">
                    <el-rate :model-value="order.seller.rating" disabled show-score />
                  </span>
                  <span class="sales">已售 {{ order.seller.salesCount }} 件</span>
                </div>
              </div>
            </div>
            <div class="seller-actions">
              <el-button @click="contactSeller" size="small">
                <el-icon><ChatDotRound /></el-icon>
                联系卖家
              </el-button>
            </div>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="address-section" v-if="order.deliveryAddress">
          <div class="section-title">收货信息</div>
          <div class="address-card">
            <div class="address-info">
              <div class="recipient">
                <span class="name">{{ order.deliveryAddress.recipientName }}</span>
                <span class="phone">{{ order.deliveryAddress.recipientPhone }}</span>
              </div>
              <div class="address">
                {{ order.deliveryAddress.fullAddress }}
              </div>
            </div>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="actions-section">
          <div class="actions-card">
            <template v-if="order.status === 'pending'">
              <el-button @click="cancelOrder" size="large">取消订单</el-button>
              <el-button @click="payOrder" type="primary" size="large">立即付款</el-button>
            </template>
            <template v-else-if="order.status === 'paid'">
              <el-button @click="contactSeller" size="large">联系卖家</el-button>
            </template>
            <template v-else-if="order.status === 'shipped'">
              <el-button @click="confirmReceived" type="primary" size="large">确认收货</el-button>
            </template>
            <template v-else-if="order.status === 'completed'">
              <el-button @click="rateOrder" size="large" v-if="!order.isRated">评价订单</el-button>
              <el-button @click="viewRating" size="large" v-else>查看评价</el-button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px">
      <div class="payment-methods">
        <div class="payment-amount">
          <span>支付金额：</span>
          <span class="amount">¥{{ order?.totalAmount }}</span>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Clock, Money, Box, Check, Close, 
  ChatDotRound, CreditCard 
} from '@element-plus/icons-vue'
import api from '@/api'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const order = ref(null)
const payDialogVisible = ref(false)
const paymentMethod = ref('online')

// 获取订单详情
const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const orderId = route.params.id
    const response = await api.get(`/user/orders/${orderId}`)
    if (response.data.code === 200) {
      order.value = response.data.data
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 获取状态标题
const getStatusTitle = (status) => {
  const statusMap = {
    pending: '等待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '交易完成',
    cancelled: '订单已取消'
  }
  return statusMap[status] || status
}

// 获取状态描述
const getStatusDesc = (status) => {
  const descMap = {
    pending: '请在24小时内完成付款',
    paid: '卖家正在准备发货',
    shipped: '商品已发货，请注意查收',
    completed: '交易已完成，感谢您的购买',
    cancelled: '订单已被取消'
  }
  return descMap[status] || ''
}

// 获取步骤进度
const getStepActive = (status) => {
  const stepMap = {
    pending: 1,
    paid: 2,
    shipped: 3,
    completed: 4,
    cancelled: 0
  }
  return stepMap[status] || 0
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    online: '在线支付',
    offline: '线下支付'
  }
  return methodMap[method] || method
}

// 复制订单号
const copyOrderNumber = async () => {
  try {
    await navigator.clipboard.writeText(order.value.orderNumber)
    ElMessage.success('订单号已复制')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 查看商品
const viewProduct = () => {
  router.push(`/products/${order.value.product.id}`)
}

// 联系卖家
const contactSeller = () => {
  router.push(`/chat/${order.value.seller.id}/${order.value.product.id}`)
}

// 取消订单
const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个订单吗？',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.put(`/user/orders/${order.value.id}/cancel`)
    ElMessage.success('订单已取消')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 支付订单
const payOrder = () => {
  payDialogVisible.value = true
}

// 确认支付
const confirmPayment = async () => {
  try {
    await api.post(`/user/orders/${order.value.id}/pay`, {
      paymentMethod: paymentMethod.value
    })
    
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    fetchOrderDetail()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

// 确认收货
const confirmReceived = async () => {
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
    
    await api.put(`/user/orders/${order.value.id}/confirm`)
    ElMessage.success('确认收货成功')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 评价订单
const rateOrder = () => {
  router.push(`/user/orders/${order.value.id}/rate`)
}

// 查看评价
const viewRating = () => {
  router.push(`/user/orders/${order.value.id}/rating`)
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
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
  fetchOrderDetail()
})
</script>

<style scoped>
.page-header {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
}

.header-content {
  display: flex;
  align-items: center;
  padding: 15px 0;
  gap: 15px;
}

.back-btn {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background: var(--fill-light);
}

.header-content h1 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.order-content {
  padding: 20px 0;
  background: #f5f5f5;
  min-height: calc(100vh - 80px);
}

.status-section,
.product-section,
.trade-section,
.seller-section,
.address-section,
.actions-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.status-card,
.product-card,
.trade-card,
.seller-card,
.address-card,
.actions-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.status-card {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.status-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
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

.status-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.status-desc {
  color: var(--text-secondary);
  margin: 0;
}

.progress-steps {
  margin-top: 20px;
}

.product-info {
  display: flex;
  gap: 15px;
  cursor: pointer;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.product-details {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.product-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: var(--danger-color);
}

.quantity {
  font-size: 14px;
  color: var(--text-secondary);
}

.trade-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-extra-light);
}

.trade-item:last-child {
  border-bottom: none;
}

.trade-item.total {
  font-size: 16px;
  font-weight: 600;
}

.label {
  color: var(--text-secondary);
}

.value {
  color: var(--text-primary);
}

.amount {
  color: var(--danger-color);
  font-size: 18px;
}

.seller-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.seller-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.seller-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
  color: var(--text-secondary);
}

.address-info .recipient {
  display: flex;
  gap: 15px;
  margin-bottom: 8px;
  font-weight: 500;
}

.address {
  color: var(--text-secondary);
}

.actions-card {
  display: flex;
  justify-content: center;
  gap: 15px;
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

@media (max-width: 768px) {
  .status-card {
    flex-direction: column;
    text-align: center;
  }
  
  .product-info {
    flex-direction: column;
  }
  
  .seller-card {
    flex-direction: column;
    gap: 15px;
  }
  
  .actions-card {
    flex-direction: column;
  }
}
</style>