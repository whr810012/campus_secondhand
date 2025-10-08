<template>
  <div class="my-orders">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的订单</h2>
    </div>



    <!-- 订单列表 -->
    <div class="orders-container" v-loading="loading">
      <div class="order-card" v-for="order in orders" :key="order.id">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号: {{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        
        <div class="order-content">
          <div class="product-info" @click="viewProduct(order.product)" v-if="order.product">
            <div class="product-details">
              <h4 class="product-title">{{ order.product.title }}</h4>
              <p class="product-desc">{{ order.product.description }}</p>
              <div class="product-meta">
                <span class="product-price">¥{{ order.product.price }}</span>
                <span class="product-condition">{{ getConditionText(order.product.condition) }}</span>
              </div>
            </div>
          </div>
          <div class="product-placeholder" v-else>
            <div class="product-details">
              <h4 class="product-title">商品信息不可用</h4>
              <p class="product-desc">商品ID: {{ order.productId }}</p>
              <div class="product-price">¥{{ order.amount }}</div>
            </div>
          </div>
          
          <div class="order-details">
            <div class="detail-item">
              <span class="label">订单金额:</span>
              <span class="value price">¥{{ order.amount }}</span>
            </div>
            <div class="detail-item">
              <span class="label">支付方式:</span>
              <span class="value">{{ getPaymentMethodText(order.paymentMethod) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">交易方式:</span>
              <span class="value">{{ getTradeTypeText(order.tradeType) }}</span>
            </div>
            <div class="detail-item" v-if="order.tradeLocation">
              <span class="label">交易地点:</span>
              <span class="value">{{ order.tradeLocation }}</span>
            </div>
            <div class="detail-item" v-if="order.remark">
              <span class="label">备注:</span>
              <span class="value">{{ order.remark }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-actions">
          <el-button 
            v-if="order.status === 'pending'" 
            type="primary" 
            @click="payOrder(order)"
          >
            立即付款
          </el-button>
          
          <el-button 
            v-if="order.status === 'paid'" 
            type="success" 
            @click="confirmReceipt(order)"
          >
            确认收货
          </el-button>
          
          <el-button 
            v-if="['pending', 'paid'].includes(order.status)" 
            @click="cancelOrder(order)"
          >
            取消订单
          </el-button>
          
          <el-button 
            v-if="order.status === 'completed' && !order.buyerRated" 
            @click="reviewOrder(order)"
          >
            评价
          </el-button>
          
          <el-button @click="viewOrderDetail(order)">
            查看详情
          </el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单">
        <el-button type="primary" @click="$router.push('/home')">
          去逛逛
        </el-button>
      </el-empty>
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="订单评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="reviewForm.images"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>

</div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="showOrderDetailDialog" title="订单详情" width="600px">
      <div class="order-detail-content">
        <!-- 订单基本信息 -->
        <el-card class="order-info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>订单信息</span>
            </div>
          </template>
          <div class="order-info">
            <div class="info-row">
              <span class="label">订单号:</span>
              <span class="value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">订单状态:</span>
              <el-tag :type="getStatusClass(selectedOrder.status)" size="small">
                {{ getStatusText(selectedOrder.status) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">订单金额:</span>
              <span class="value price">¥{{ selectedOrder.amount }}</span>
            </div>
            <div class="info-row">
              <span class="label">下单时间:</span>
              <span class="value">{{ formatTime(selectedOrder.createdAt) }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.paymentTime">
              <span class="label">支付时间:</span>
              <span class="value">{{ formatTime(selectedOrder.paymentTime) }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.shippedTime">
              <span class="label">发货时间:</span>
              <span class="value">{{ formatTime(selectedOrder.shippedTime) }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.completedTime">
              <span class="label">完成时间:</span>
              <span class="value">{{ formatTime(selectedOrder.completedTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">支付方式:</span>
              <span class="value">{{ getPaymentMethodText(selectedOrder.paymentMethod) }}</span>
            </div>
            <div class="info-row">
              <span class="label">交易方式:</span>
              <span class="value">{{ getTradeTypeText(selectedOrder.tradeType) }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.tradeLocation">
              <span class="label">交易地点:</span>
              <span class="value">{{ selectedOrder.tradeLocation }}</span>
            </div>
            <div class="info-row" v-if="selectedOrder.remark">
              <span class="label">备注:</span>
              <span class="value">{{ selectedOrder.remark }}</span>
            </div>
          </div>
        </el-card>

        <!-- 商品信息 -->
        <el-card class="product-info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>商品信息</span>
            </div>
          </template>
          <div class="product-detail" v-if="selectedOrder.product">
            <div class="product-image">
              <img v-if="selectedOrder.product.imageList && selectedOrder.product.imageList.length > 0" 
                   :src="selectedOrder.product.imageList[0]" 
                   :alt="selectedOrder.product.title" />
              <div v-else class="placeholder-image">
                <el-icon><Picture /></el-icon>
              </div>
            </div>
            <div class="product-info">
              <h4>{{ selectedOrder.product.title }}</h4>
              <p class="product-desc">{{ selectedOrder.product.description }}</p>
              <div class="product-meta">
                <div class="meta-item">
                  <span class="label">价格:</span>
                  <span class="value price">¥{{ selectedOrder.product.price }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">成色:</span>
                  <span class="value">{{ getConditionText(selectedOrder.product.condition) }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">交易方式:</span>
                  <span class="value">{{ getTradeTypeText(selectedOrder.product.tradeType) }}</span>
                </div>
                <div class="meta-item" v-if="selectedOrder.product.location">
                  <span class="label">交易地点:</span>
                  <span class="value">{{ selectedOrder.product.location }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="product-placeholder">
            <div class="placeholder-image">
              <el-icon><Picture /></el-icon>
            </div>
            <div class="product-info">
              <h4>商品信息不可用</h4>
              <p class="product-desc">商品ID: {{ selectedOrder.productId }}</p>
              <div class="product-meta">
                <div class="meta-item">
                  <span class="label">价格:</span>
                  <span class="value price">¥{{ selectedOrder.amount }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <template #footer>
        <el-button @click="showOrderDetailDialog = false">关闭</el-button>
        <el-button
          v-if="selectedOrder.status === 'pending'"
          type="primary"
          @click="payOrder(selectedOrder)"
        >
          立即付款
        </el-button>
        <el-button
          v-if="selectedOrder.status === 'paid'"
          type="success"
          @click="confirmReceipt(selectedOrder)"
        >
          确认收货
        </el-button>
        <el-button
          v-if="['pending', 'paid'].includes(selectedOrder.status)"
          @click="cancelOrder(selectedOrder)"
        >
          取消订单
        </el-button>
        <el-button
          v-if="selectedOrder.status === 'completed' && !selectedOrder.buyerRated"
          @click="reviewOrder(selectedOrder)"
        >
          评价
        </el-button>
      </template>
    </el-dialog>
  <!-- </div> -->

</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getUserOrders, payOrder as payOrderApi, cancelOrder as cancelOrderApi, confirmReceipt as confirmReceiptApi, reviewOrder as reviewOrderApi } from '@/api/order'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 评价对话框
const reviewDialogVisible = ref(false)
const currentOrder = ref(null)
const reviewForm = reactive({
  rating: 5,
  content: '',
  images: []
})

// 订单详情对话框
const showOrderDetailDialog = ref(false)
const selectedOrder = ref({})

// 获取订单列表
const fetchOrders = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const response = await getUserOrders(params)
    // MyBatis Plus Page对象结构：records为数据列表，total为总数
    // 根据接口返回的实际数据结构进行处理
    const responseData = response.data
    orders.value = responseData.records || []
    total.value = responseData.total || 0
    
    console.log('订单数据:', orders.value)
    console.log('总数:', total.value)
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 页面挂载时立即获取订单
onMounted(() => {
  fetchOrders()
})



// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

// 查看商品
const viewProduct = (product) => {
  window.open(`/product/${product.id}`, '_blank')
}

// 查看订单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showOrderDetailDialog.value = true
}

// 付款
const payOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要支付订单"${order.orderNo}"吗？`,
      '确认支付',
      {
        confirmButtonText: '确定支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await payOrderApi(order.id, {})
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消原因',
      '取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '请输入取消原因'
      }
    )
    
    if (!userStore.userInfo?.id) {
      ElMessage.error('用户信息获取失败，请重新登录')
      return
    }
    
    await cancelOrderApi(order.id, {
      userId: userStore.userInfo.id,
      reason: reason
    })
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 确认收货
const confirmReceipt = async (order) => {
  try {
    if (!userStore.userInfo?.id) {
      ElMessage.error('用户信息获取失败，请重新登录')
      return
    }
    
    await ElMessageBox.confirm(
      `确定已收到商品"${order.product?.title || '该商品'}"吗？`,
      '确认收货',
      {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await confirmReceiptApi(order.id, userStore.userInfo.id)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  }
}

// 评价订单
const reviewOrder = (order) => {
  currentOrder.value = order
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.images = []
  reviewDialogVisible.value = true
}

// 提交评价
const submitReview = async () => {
  try {
    if (!reviewForm.content.trim()) {
      ElMessage.warning('请输入评价内容')
      return
    }
    
    await reviewOrderApi(currentOrder.value.id, {
      rating: reviewForm.rating,
      content: reviewForm.content,
      images: reviewForm.images.map(file => file.url).filter(Boolean)
    })
    
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    console.error('评价失败:', error)
    ElMessage.error('评价失败')
  }
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'online': '在线支付',
    'offline': '线下支付',
    'cash': '现金支付'
  }
  return methodMap[method] || '未知'
}

// 获取交易方式文本
const getTradeTypeText = (type) => {
  const typeMap = {
    1: '仅线下',
    2: '仅线上', 
    3: '线上线下均可'
  }
  return typeMap[type] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return statusMap[status] || status
}

// 获取状态样式类
const getStatusClass = (status) => {
  return `status-${status}`
}

// 获取成色文本
const getConditionText = (condition) => {
  const conditionMap = {
    new: '全新',
    like_new: '几乎全新',
    good: '良好',
    fair: '一般',
    poor: '较差'
  }
  return conditionMap[condition] || condition
}

// 初始化
onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.my-orders {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.filter-bar {
  margin-bottom: 24px;
}

.orders-container {
  .order-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 16px;
    overflow: hidden;
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: #f8f9fa;
      border-bottom: 1px solid #eee;
      
      .order-info {
        .order-no {
          font-weight: 600;
          color: var(--text-primary);
          margin-right: 16px;
        }
        
        .order-time {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .order-status {
        .status-tag {
          padding: 4px 12px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: 500;
          
          &.pending {
            background: #fff7e6;
            color: #fa8c16;
          }
          
          &.paid {
            background: #e6f7ff;
            color: #1890ff;
          }
          
          &.shipped {
            background: #f6ffed;
            color: #52c41a;
          }
          
          &.completed {
            background: #f6ffed;
            color: #52c41a;
          }
          
          &.cancelled {
            background: #fff2f0;
            color: #ff4d4f;
          }
        }
      }
    }
    
    .order-content {
      padding: 20px;
      
      .product-info {
        display: flex;
        margin-bottom: 16px;
        
        .product-image {
          width: 80px;
          height: 80px;
          border-radius: 8px;
          object-fit: cover;
          margin-right: 16px;
        }
        
        .product-details {
          flex: 1;
          
          .product-title {
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .product-desc {
            color: var(--text-secondary);
            font-size: 14px;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .product-price {
            font-size: 18px;
            font-weight: 600;
            color: #ff4d4f;
          }
        }
      }
      
      .order-details {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 12px;
        margin-bottom: 16px;
        
        .detail-item {
          display: flex;
          align-items: center;
          
          .label {
            color: var(--text-secondary);
            margin-right: 8px;
            min-width: 80px;
          }
          
          .value {
            color: var(--text-primary);
            font-weight: 500;
          }
        }
      }
      
      .order-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        
        .el-button {
          min-width: 80px;
        }
      }
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}


</style>



<style lang="scss" scoped>
.my-orders {
  padding: 20px;
  
  .page-header {
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
    

  }
  
  .orders-container {
    .order-card {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      box-shadow: var(--box-shadow-light);
      margin-bottom: 16px;
      overflow: hidden;
      transition: var(--transition-all);
      
      &:hover {
        box-shadow: var(--box-shadow-dark);
      }
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        background: var(--bg-secondary);
        border-bottom: 1px solid var(--border-color-lighter);
        
        .order-info {
          display: flex;
          gap: 16px;
          align-items: center;
          
          .order-number {
            font-weight: var(--font-weight-primary);
            color: var(--text-primary);
          }
          
          .order-time {
            color: var(--text-secondary);
            font-size: var(--font-size-small);
          }
        }
        
        .order-status {
          padding: 4px 12px;
          border-radius: var(--border-radius-base);
          font-size: var(--font-size-small);
          font-weight: var(--font-weight-primary);
          
          &.status-pending {
            background: #fff7e6;
            color: var(--warning-color);
          }
          
          &.status-paid {
            background: #e6f7ff;
            color: var(--primary-color);
          }
          
          &.status-completed {
            background: #f6ffed;
            color: var(--success-color);
          }
          
          &.status-cancelled {
            background: #fff2f0;
            color: var(--danger-color);
          }
        }
      }
      
      .order-content {
        padding: 20px;
        
        .product-info {
          display: flex;
          margin-bottom: 16px;
          cursor: pointer;
          transition: var(--transition-all);
          
          &:hover {
            background: var(--bg-secondary);
          }
          
          .product-image {
            width: 80px;
            height: 80px;
            border-radius: var(--border-radius-base);
            overflow: hidden;
            margin-right: 16px;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
          
          .product-details {
            flex: 1;
            
            .product-title {
              font-size: var(--font-size-medium);
              font-weight: var(--font-weight-primary);
              color: var(--text-primary);
              margin: 0 0 8px 0;
              @include text-ellipsis;
            }
            
            .product-desc {
              color: var(--text-secondary);
              font-size: var(--font-size-small);
              margin: 0 0 8px 0;
              @include multi-line-ellipsis(2);
            }
            
            .product-meta {
              display: flex;
              align-items: center;
              gap: 12px;
              
              .product-price {
                font-size: var(--font-size-large);
                font-weight: var(--font-weight-primary);
                color: var(--danger-color);
              }
              
              .product-condition {
                font-size: var(--font-size-small);
                color: var(--text-secondary);
                background: var(--bg-secondary);
                padding: 2px 8px;
                border-radius: var(--border-radius-small);
              }
            }
          }
        }
        
        .product-placeholder {
          display: flex;
          margin-bottom: 16px;
          
          .placeholder-image {
            width: 80px;
            height: 80px;
            background: var(--bg-secondary);
            border-radius: var(--border-radius-base);
            margin-right: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--text-secondary);
          }
          
          .product-details {
            flex: 1;
            
            .product-title {
              font-size: var(--font-size-medium);
              font-weight: var(--font-weight-primary);
              color: var(--text-primary);
              margin: 0 0 8px 0;
            }
            
            .product-desc {
              color: var(--text-secondary);
              font-size: var(--font-size-small);
              margin: 0 0 8px 0;
            }
            
            .product-price {
              font-size: var(--font-size-large);
              font-weight: var(--font-weight-primary);
              color: var(--danger-color);
            }
          }
        }
        
        .order-details {
          .detail-item {
            display: flex;
            margin-bottom: 8px;
            
            .label {
              width: 80px;
              color: var(--text-secondary);
              font-size: var(--font-size-small);
            }
            
            .value {
              flex: 1;
              color: var(--text-primary);
              font-size: var(--font-size-small);
              
              &.price {
                color: var(--danger-color);
                font-weight: var(--font-weight-primary);
              }
            }
          }
        }
      }
      
      .order-actions {
        padding: 16px 20px;
        border-top: 1px solid var(--border-color-lighter);
        display: flex;
        gap: 8px;
        justify-content: flex-end;
        flex-wrap: wrap;
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }
}

/* 订单详情对话框样式 */
.order-detail-content {
  .order-info-card,
  .product-info-card {
    margin-bottom: 16px;
    
    .card-header {
      font-weight: 600;
      color: var(--text-primary);
    }
  }
  
  .order-info {
    .info-row {
      display: flex;
      margin-bottom: 12px;
      
      .label {
        width: 100px;
        color: var(--text-secondary);
        font-size: 14px;
      }
      
      .value {
        flex: 1;
        color: var(--text-primary);
        
        &.price {
          color: var(--danger-color);
          font-weight: 600;
        }
      }
    }
  }
  
  .product-detail,
  .product-placeholder {
    display: flex;
    gap: 16px;
    
    .product-image,
    .placeholder-image {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .placeholder-image {
      background: var(--bg-secondary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--text-secondary);
      font-size: 24px;
    }
    
    .product-info {
      flex: 1;
      
      h4 {
        margin: 0 0 8px 0;
        color: var(--text-primary);
      }
      
      .product-desc {
        color: var(--text-secondary);
        margin: 0 0 12px 0;
        font-size: 14px;
      }
      
      .product-meta {
        .meta-item {
          display: flex;
          margin-bottom: 6px;
          
          .label {
            width: 80px;
            color: var(--text-secondary);
            font-size: 14px;
          }
          
          .value {
            color: var(--text-primary);
            
            &.price {
              color: var(--danger-color);
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

/* 响应式设计 */
@include respond-to(md) {
  .my-orders {
    padding: 16px;
    
    .filter-bar {
      padding: 12px;
    }
    
    .orders-container {
      .order-card {
        .order-header {
          flex-direction: column;
          align-items: stretch;
          gap: 8px;
          
          .order-info {
            justify-content: space-between;
          }
          
          .order-status {
            align-self: flex-end;
          }
        }
        

        
        .order-actions {
          justify-content: center;
          
          .el-button {
            flex: 1;
            min-width: 80px;
          }
        }
      }
    }
  }
}

@include respond-to(sm) {
  .my-orders {
    padding: 12px;
    
    .orders-container {
      .order-card {
        .order-content {
          padding: 16px;
          
          .product-info,
          .product-placeholder {
            flex-direction: column;
            text-align: center;
            
            .product-image,
            .placeholder-image {
              align-self: center;
              margin-right: 0;
              margin-bottom: 12px;
            }
          }
          
          .order-details {
            .detail-item {
              flex-direction: column;
              
              .label {
                width: auto;
                margin-bottom: 4px;
              }
            }
          }
        }
        
        .order-actions {
          flex-direction: column;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
  
  .order-detail-content {
    .product-detail,
    .product-placeholder {
      flex-direction: column;
      text-align: center;
      
      .product-image,
      .placeholder-image {
        align-self: center;
        margin-bottom: 12px;
      }
    }
  }
}
</style>