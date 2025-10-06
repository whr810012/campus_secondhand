<template>
  <div class="order-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>订单详情</h2>
      <div class="order-status">
        <el-tag :type="getStatusType(order.status)" size="large">
          {{ getStatusText(order.status) }}
        </el-tag>
      </div>
    </div>

    <div class="order-content" v-loading="loading">
      <div class="order-info-card" v-if="order.id">
        <!-- 订单基本信息 -->
        <div class="order-basic-info">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ order.order_number }}</span>
            <el-button size="small" text @click="copyOrderNumber">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
          <div class="info-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatTime(order.created_at) }}</span>
          </div>
          <div class="info-row" v-if="order.paid_at">
            <span class="label">支付时间：</span>
            <span class="value">{{ formatTime(order.paid_at) }}</span>
          </div>
          <div class="info-row" v-if="order.shipped_at">
            <span class="label">发货时间：</span>
            <span class="value">{{ formatTime(order.shipped_at) }}</span>
          </div>
          <div class="info-row" v-if="order.completed_at">
            <span class="label">完成时间：</span>
            <span class="value">{{ formatTime(order.completed_at) }}</span>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="product-info">
          <h3>商品信息</h3>
          <div class="product-card">
            <img :src="order.product?.images?.[0]" :alt="order.product?.title" />
            <div class="product-details">
              <h4 class="product-title">{{ order.product?.title }}</h4>
              <p class="product-description">{{ order.product?.description }}</p>
              <div class="product-meta">
                <span class="product-category">{{ order.product?.category?.name }}</span>
                <span class="product-condition">{{ getConditionText(order.product?.condition) }}</span>
              </div>
              <div class="price-info">
                <span class="current-price">¥{{ order.amount }}</span>
                <span class="original-price" v-if="order.product?.original_price > order.amount">
                  ¥{{ order.product?.original_price }}
                </span>
              </div>
            </div>
            <div class="product-actions">
              <el-button size="small" @click="viewProduct">
                查看商品
              </el-button>
            </div>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-info">
          <h3>卖家信息</h3>
          <div class="seller-card">
            <el-avatar :size="50" :src="order.seller?.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="seller-details">
              <h4>{{ order.seller?.nickname }}</h4>
              <div class="seller-meta">
                <el-rate v-model="order.seller?.rating" disabled show-score />
                <span class="seller-school">{{ order.seller?.school }}</span>
              </div>
            </div>
            <div class="seller-actions">
              <el-button size="small" @click="contactSeller">
                <el-icon><ChatDotRound /></el-icon>
                联系卖家
              </el-button>
            </div>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="delivery-info" v-if="order.delivery_address">
          <h3>收货信息</h3>
          <div class="address-card">
            <div class="address-header">
              <span class="recipient">{{ order.delivery_address.recipient }}</span>
              <span class="phone">{{ order.delivery_address.phone }}</span>
            </div>
            <div class="address-detail">
              {{ order.delivery_address.province }} {{ order.delivery_address.city }} 
              {{ order.delivery_address.district }} {{ order.delivery_address.detail }}
            </div>
            <div class="delivery-method">
              <el-tag size="small">{{ getDeliveryMethodText(order.delivery_method) }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 订单进度 -->
        <div class="order-progress">
          <h3>订单进度</h3>
          <el-steps :active="getProgressStep(order.status)" finish-status="success">
            <el-step title="订单创建" :description="formatTime(order.created_at)" />
            <el-step 
              title="等待支付" 
              :description="order.status === 'pending' ? '请及时完成支付' : ''"
            />
            <el-step 
              title="支付完成" 
              :description="order.paid_at ? formatTime(order.paid_at) : ''"
            />
            <el-step 
              title="卖家发货" 
              :description="order.shipped_at ? formatTime(order.shipped_at) : ''"
            />
            <el-step 
              title="交易完成" 
              :description="order.completed_at ? formatTime(order.completed_at) : ''"
            />
          </el-steps>
        </div>

        <!-- 订单操作 -->
        <div class="order-actions">
          <template v-if="order.status === 'pending'">
            <el-button type="primary" @click="payOrder" :loading="actionLoading">
              立即支付
            </el-button>
            <el-button @click="cancelOrder" :loading="actionLoading">
              取消订单
            </el-button>
          </template>
          
          <template v-else-if="order.status === 'paid'">
            <el-button @click="remindShipping" :loading="actionLoading">
              提醒发货
            </el-button>
            <el-button @click="requestRefund" :loading="actionLoading">
              申请退款
            </el-button>
          </template>
          
          <template v-else-if="order.status === 'shipped'">
            <el-button type="primary" @click="confirmReceived" :loading="actionLoading">
              确认收货
            </el-button>
            <el-button @click="requestRefund" :loading="actionLoading">
              申请退款
            </el-button>
          </template>
          
          <template v-else-if="order.status === 'completed'">
            <el-button @click="showReviewDialog" v-if="!order.review">
              评价订单
            </el-button>
            <el-button @click="viewReview" v-else>
              查看评价
            </el-button>
            <el-button @click="buyAgain">
              再次购买
            </el-button>
          </template>
          
          <template v-else-if="order.status === 'cancelled'">
            <el-button @click="buyAgain" v-if="order.product?.status === 'active'">
              再次购买
            </el-button>
          </template>
        </div>
      </div>
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text />
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
        <el-button type="primary" @click="submitReview" :loading="reviewLoading">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, 
  CopyDocument, 
  User, 
  ChatDotRound, 
  Plus 
} from '@element-plus/icons-vue'
import { 
  getOrderDetail, 
  payOrder as payOrderApi, 
  cancelOrder as cancelOrderApi,
  confirmReceipt as confirmReceivedApi,
  submitOrderReview
} from '@/api/order'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const actionLoading = ref(false)
const reviewLoading = ref(false)
const order = ref({})
const reviewDialogVisible = ref(false)

// 评价表单
const reviewForm = reactive({
  rating: 5,
  content: '',
  images: []
})

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true
    const response = await getOrderDetail(route.params.id)
    order.value = response.data || {}
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 复制订单号
const copyOrderNumber = async () => {
  try {
    await navigator.clipboard.writeText(order.value.order_number)
    ElMessage.success('订单号已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 查看商品
const viewProduct = () => {
  const routeData = router.resolve(`/product/${order.value.product.id}`)
  window.open(routeData.href, '_blank')
}

// 联系卖家
const contactSeller = () => {
  ElMessage.info('联系卖家功能开发中')
}

// 支付订单
const payOrder = async () => {
  try {
    actionLoading.value = true
    await payOrderApi(order.value.id, {})
    ElMessage.success('支付成功')
    fetchOrderDetail()
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    actionLoading.value = false
  }
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
    
    actionLoading.value = true
    await cancelOrderApi(order.value.id)
    ElMessage.success('订单已取消')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  } finally {
    actionLoading.value = false
  }
}

// 确认收货
const confirmReceived = async () => {
  try {
    if (!userStore.userInfo?.id) {
      ElMessage.error('用户信息获取失败，请重新登录')
      return
    }
    
    await ElMessageBox.confirm(
      '确定已收到商品吗？',
      '确认收货',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    actionLoading.value = true
    await confirmReceivedApi(order.value.id, userStore.userInfo.id)
    ElMessage.success('确认收货成功')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  } finally {
    actionLoading.value = false
  }
}

// 提醒发货
const remindShipping = () => {
  ElMessage.info('提醒发货功能开发中')
}

// 申请退款
const requestRefund = () => {
  ElMessage.info('申请退款功能开发中')
}

// 显示评价对话框
const showReviewDialog = () => {
  reviewDialogVisible.value = true
}

// 查看评价
const viewReview = () => {
  ElMessage.info('查看评价功能开发中')
}

// 再次购买
const buyAgain = () => {
  router.push(`/product/${order.value.product.id}`)
}

// 提交评价
const submitReview = async () => {
  try {
    reviewLoading.value = true
    await submitOrderReview(order.value.id, reviewForm)
    ElMessage.success('评价提交成功')
    reviewDialogVisible.value = false
    fetchOrderDetail()
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('提交评价失败')
  } finally {
    reviewLoading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    paid: 'info',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'danger',
    refunded: 'info'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return statusMap[status] || '未知状态'
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
  return conditionMap[condition] || '未知'
}

// 获取配送方式文本
const getDeliveryMethodText = (method) => {
  const methodMap = {
    pickup: '自提',
    delivery: '配送',
    express: '快递'
  }
  return methodMap[method] || '未知'
}

// 获取进度步骤
const getProgressStep = (status) => {
  const stepMap = {
    pending: 1,
    paid: 2,
    shipped: 3,
    completed: 4,
    cancelled: 0,
    refunded: 0
  }
  return stepMap[status] || 0
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  fetchOrderDetail()
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.order-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-color-lighter);
    
    h2 {
      margin: 0;
      color: var(--text-primary);
      flex: 1;
      text-align: center;
    }
    
    .order-status {
      min-width: 100px;
      text-align: right;
    }
  }
  
  .order-content {
    .order-info-card {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      box-shadow: var(--box-shadow-light);
      overflow: hidden;
      
      > div {
        padding: 24px;
        border-bottom: 1px solid var(--border-color-lighter);
        
        &:last-child {
          border-bottom: none;
        }
        
        h3 {
          margin: 0 0 16px 0;
          color: var(--text-primary);
          font-size: var(--font-size-large);
        }
      }
      
      .order-basic-info {
        .info-row {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .label {
            color: var(--text-secondary);
            min-width: 80px;
          }
          
          .value {
            color: var(--text-primary);
            flex: 1;
          }
        }
      }
      
      .product-info {
        .product-card {
          display: flex;
          gap: 16px;
          
          img {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: var(--border-radius-base);
            flex-shrink: 0;
          }
          
          .product-details {
            flex: 1;
            
            .product-title {
              font-size: var(--font-size-large);
              font-weight: var(--font-weight-primary);
              color: var(--text-primary);
              margin: 0 0 8px 0;
            }
            
            .product-description {
              color: var(--text-regular);
              margin: 0 0 12px 0;
              @include multi-line-ellipsis(2);
            }
            
            .product-meta {
              display: flex;
              gap: 12px;
              margin-bottom: 12px;
              
              span {
                font-size: var(--font-size-small);
                color: var(--text-secondary);
                padding: 2px 8px;
                background: var(--bg-secondary);
                border-radius: var(--border-radius-small);
              }
            }
            
            .price-info {
              .current-price {
                font-size: var(--font-size-extra-large);
                font-weight: var(--font-weight-primary);
                color: var(--danger-color);
              }
              
              .original-price {
                font-size: var(--font-size-medium);
                color: var(--text-secondary);
                text-decoration: line-through;
                margin-left: 8px;
              }
            }
          }
          
          .product-actions {
            display: flex;
            flex-direction: column;
            justify-content: center;
          }
        }
      }
      
      .seller-info {
        .seller-card {
          display: flex;
          gap: 16px;
          align-items: center;
          
          .seller-details {
            flex: 1;
            
            h4 {
              margin: 0 0 8px 0;
              color: var(--text-primary);
            }
            
            .seller-meta {
              display: flex;
              align-items: center;
              gap: 12px;
              
              .seller-school {
                color: var(--text-secondary);
                font-size: var(--font-size-small);
              }
            }
          }
        }
      }
      
      .delivery-info {
        .address-card {
          .address-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            
            .recipient {
              font-weight: var(--font-weight-primary);
              color: var(--text-primary);
            }
            
            .phone {
              color: var(--text-secondary);
            }
          }
          
          .address-detail {
            color: var(--text-regular);
            margin-bottom: 12px;
            line-height: 1.5;
          }
        }
      }
      
      .order-progress {
        .el-steps {
          margin-top: 16px;
        }
      }
      
      .order-actions {
        display: flex;
        gap: 12px;
        justify-content: flex-end;
        
        .el-button {
          min-width: 100px;
        }
      }
    }
  }
}

// 响应式设计
@include respond-to(md) {
  .order-detail {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      gap: 12px;
      
      h2 {
        text-align: center;
      }
    }
    
    .order-content {
      .order-info-card {
        > div {
          padding: 16px;
        }
        
        .product-info {
          .product-card {
            flex-direction: column;
            
            img {
              width: 100%;
              height: 200px;
            }
            
            .product-actions {
              flex-direction: row;
              justify-content: flex-start;
            }
          }
        }
        
        .seller-info {
          .seller-card {
            flex-direction: column;
            text-align: center;
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

@include respond-to(sm) {
  .order-detail {
    padding: 12px;
    
    .order-content {
      .order-info-card {
        > div {
          padding: 12px;
        }
        
        .order-basic-info {
          .info-row {
            flex-direction: column;
            align-items: flex-start;
            gap: 4px;
            
            .label {
              min-width: auto;
              font-weight: var(--font-weight-primary);
            }
          }
        }
      }
    }
  }
}
</style>