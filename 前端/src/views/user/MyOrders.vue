<template>
  <div class="my-orders">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的订单</h2>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待付款" name="pending" />
        <el-tab-pane label="已付款" name="paid" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>
      

    </div>

    <!-- 订单列表 -->
    <div class="orders-container" v-loading="loading">
      <div class="order-card" v-for="order in orders" :key="order.id">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNo }}</span>
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
              <span class="label">订单金额：</span>
              <span class="value price">¥{{ order.amount }}</span>
            </div>
            <div class="detail-item">
              <span class="label">支付方式：</span>
              <span class="value">{{ getPaymentMethodText(order.paymentMethod) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">交易方式：</span>
              <span class="value">{{ getTradeTypeText(order.tradeType) }}</span>
            </div>
            <div class="detail-item" v-if="order.tradeLocation">
              <span class="label">交易地点：</span>
              <span class="value">{{ order.tradeLocation }}</span>
            </div>
            <div class="detail-item" v-if="order.remark">
              <span class="label">备注：</span>
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
          
          <el-button @click="contactSeller(order)">
            联系卖家
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
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
const activeTab = ref('all')

// 评价对话框
const reviewDialogVisible = ref(false)
const currentOrder = ref(null)
const reviewForm = reactive({
  rating: 5,
  content: '',
  images: []
})

// 获取订单列表
const fetchOrders = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: activeTab.value === 'all' ? '' : activeTab.value
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

// 标签页切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  fetchOrders()
}



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
  window.open(`/order/${order.id}`, '_blank')
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
    await ElMessageBox.confirm(
      `确定要取消订单"${order.orderNo}"吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cancelOrderApi(order.id)
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

// 联系卖家
const contactSeller = (order) => {
  // 跳转到消息页面或打开聊天窗口
  ElMessage.info('联系卖家功能开发中')
}



// 获取商品状态文本
const getConditionText = (condition) => {
  const conditionMap = {
    1: '全新',
    2: '几乎全新',
    3: '轻微使用痕迹',
    4: '明显使用痕迹',
    5: '重度使用'
  }
  return conditionMap[condition] || '未知'
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
    1: '面交',
    2: '邮寄',
    3: '自提'
  }
  return typeMap[type] || '未知'
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    pending: 'status-pending',
    paid: 'status-paid',
    completed: 'status-completed',
    cancelled: 'status-cancelled'
  }
  return statusMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待付款',
    paid: '已付款',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 组件挂载
onMounted(() => {
  fetchOrders()
})
</script>

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

// 响应式设计
@include respond-to(md) {
  .my-orders {
    padding: 16px;
    

    
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
            
            img,
            .placeholder-image {
              align-self: center;
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
}
</style>