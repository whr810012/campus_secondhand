<template>
  <div class="order-confirm-page">
    <div class="container">
      <div class="order-confirm" v-loading="loading">
        <!-- 页面头部 -->
        <div class="page-header">
          <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
          <h2>确认订单</h2>
        </div>

        <div class="order-content" v-if="product.id">
          <!-- 商品信息 -->
          <div class="product-section">
            <el-card>
              <template #header>
                <h3>商品信息</h3>
              </template>
              <div class="product-info">
                <img :src="getProductImage(product.images)" :alt="product.title" />
                <div class="product-details">
                  <h4 class="product-title">{{ product.title }}</h4>
                  <p class="product-description">{{ product.description }}</p>
                  <div class="product-meta">
                    <span class="product-category">{{ productCategory }}</span>
                    <span class="product-condition">{{ getConditionText(product.condition) }}</span>
                  </div>
                  <div class="price-info">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span class="original-price" v-if="product.originalPrice && product.originalPrice > product.price">
                      ¥{{ product.originalPrice }}
                    </span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 卖家信息 -->
          <div class="seller-section">
            <el-card>
              <template #header>
                <h3>卖家信息</h3>
              </template>
              <div class="seller-info">
                <el-avatar :size="50" :src="product.seller?.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="seller-details">
                  <h4>{{ product.seller?.nickname }}</h4>
                  <div class="seller-meta">
                    <span class="seller-school">{{ product.seller?.school }}</span>
                    <span class="seller-credit">信誉：{{ product.seller?.creditScore || 0 }}分</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 交易方式 -->
          <div class="trade-section">
            <el-card>
              <template #header>
                <h3>交易方式</h3>
              </template>
              <el-form :model="orderForm" label-width="100px">
                <el-form-item label="交易方式">
                  <el-radio-group v-model="orderForm.tradeType" @change="onTradeTypeChange">
                    <el-radio :label="1" :disabled="!canTradeOffline">线下交易</el-radio>
                    <el-radio :label="2" :disabled="!canTradeOnline">线上交易</el-radio>
                  </el-radio-group>
                  <div class="trade-type-desc">
                    <span v-if="orderForm.tradeType === 1" class="desc-text">线下交易：买卖双方约定地点当面交易</span>
                    <span v-if="orderForm.tradeType === 2" class="desc-text">线上交易：卖家发货，买家收货后确认</span>
                  </div>
                </el-form-item>
                
                <!-- 线下交易地点 -->
                <el-form-item label="交易地点" v-if="orderForm.tradeType === 1">
                  <el-input 
                    v-model="orderForm.tradeLocation" 
                    placeholder="请输入交易地点，如：学校图书馆门口" 
                  />
                </el-form-item>
                
                <!-- 支付方式 -->
                <el-form-item label="支付方式">
                  <el-radio-group v-model="orderForm.paymentMethod">
                    <el-radio label="online" :disabled="orderForm.tradeType === 1">线上支付</el-radio>
                    <el-radio label="offline">线下支付</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 收货信息 -->
          <div class="delivery-section" v-if="orderForm.tradeType === 2">
            <el-card>
              <template #header>
                <h3>收货信息</h3>
              </template>
              <el-form :model="orderForm" label-width="100px">
                <el-form-item label="收货人">
                  <el-input v-model="orderForm.recipient" placeholder="请输入收货人姓名" />
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="orderForm.phone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="收货地址">
                  <el-input 
                    v-model="orderForm.address" 
                    type="textarea" 
                    :rows="3" 
                    placeholder="请输入详细收货地址" 
                  />
                </el-form-item>
              </el-form>
            </el-card>
          </div>
          
          <!-- 备注信息 -->
          <div class="remark-section">
            <el-card>
              <template #header>
                <h3>备注信息</h3>
              </template>
              <el-form :model="orderForm" label-width="100px">
                <el-form-item label="备注">
                  <el-input 
                    v-model="orderForm.remark" 
                    type="textarea" 
                    :rows="2" 
                    placeholder="给卖家留言（选填）" 
                  />
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 订单总计 -->
          <div class="order-summary">
            <el-card>
              <template #header>
                <h3>订单总计</h3>
              </template>
              <div class="summary-item">
                <span class="label">商品价格：</span>
                <span class="value">¥{{ product.price }}</span>
              </div>
              <div class="summary-item">
                <span class="label">运费：</span>
                <span class="value">¥0.00</span>
              </div>
              <div class="summary-total">
                <span class="label">总计：</span>
                <span class="value total-price">¥{{ product.price }}</span>
              </div>
            </el-card>
          </div>

          <!-- 提交订单 -->
          <div class="submit-section">
            <el-button 
              type="primary" 
              size="large" 
              :loading="submitting" 
              @click="submitOrder"
              class="submit-btn"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User } from '@element-plus/icons-vue'
import { getProductDetail, getCategories } from '@/api/product'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const product = ref({})
const categories = ref([])
const productCategory = ref('')

const orderForm = reactive({
  recipient: '',
  phone: '',
  address: '',
  remark: '',
  tradeType: 2, // 默认线上交易
  tradeLocation: '',
  paymentMethod: 'online' // 默认线上支付
})

// 交易方式相关的计算属性
const canTradeOffline = computed(() => {
  return product.value.tradeType === 1 || product.value.tradeType === 3
})

const canTradeOnline = computed(() => {
  return product.value.tradeType === 2 || product.value.tradeType === 3
})

// 交易方式变化处理
const onTradeTypeChange = (value) => {
  if (value === 1) {
    // 线下交易，支付方式默认为线下支付
    orderForm.paymentMethod = 'offline'
    // 清空收货信息
    orderForm.recipient = ''
    orderForm.phone = ''
    orderForm.address = ''
  } else if (value === 2) {
    // 线上交易，支付方式默认为线上支付
    orderForm.paymentMethod = 'online'
    // 清空交易地点
    orderForm.tradeLocation = ''
  }
}

// 初始化交易方式
const initTradeType = () => {
  if (product.value.tradeType === 1) {
    // 仅支持线下交易
    orderForm.tradeType = 1
    orderForm.paymentMethod = 'offline'
  } else if (product.value.tradeType === 2) {
    // 仅支持线上交易
    orderForm.tradeType = 2
    orderForm.paymentMethod = 'online'
  } else {
    // 线上线下均可，默认线上交易
    orderForm.tradeType = 2
    orderForm.paymentMethod = 'online'
  }
}

// 获取分类信息
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data
    // 根据categoryId找到对应的分类名称
    const category = categories.value.find(cat => cat.id === product.value.categoryId)
    productCategory.value = category?.name || '未知分类'
  } catch (error) {
    console.error('获取分类失败:', error)
    productCategory.value = '未知分类'
  }
}

// 获取商品详情
const fetchProductDetail = async () => {
  try {
    loading.value = true
    const productId = route.query.productId
    
    if (!productId) {
      ElMessage.error('商品ID不存在')
      router.go(-1)
      return
    }
    
    const response = await getProductDetail(productId)
    
    // 响应拦截器已处理成功状态，直接使用data
    const rawData = response.data
    
    // 确保seller对象存在
    product.value = {
      ...rawData,
      seller: rawData.seller || {
        id: rawData.sellerId,
        nickname: '卖家',
        avatar: '',
        creditScore: 0,
        dealCount: 0
      }
    }
    
    // 初始化交易方式
    initTradeType()
    
    // 获取分类信息
    await fetchCategories()
    
    // 检查商品状态
    if (product.value.status !== 'available' && product.value.status !== 1) {
      ElMessage.error('商品已下架或不可购买')
      router.go(-1)
      return
    }
    
    // 检查是否是自己的商品
    if (product.value.seller?.id === userStore.userInfo?.id) {
      ElMessage.error('不能购买自己的商品')
      router.go(-1)
      return
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
    router.go(-1)
  } finally {
    loading.value = false
  }
}

// 提交订单
const submitOrder = async () => {
  // 表单验证
  if (orderForm.tradeType === 1) {
    // 线下交易验证
    if (!orderForm.tradeLocation.trim()) {
      ElMessage.warning('请输入交易地点')
      return
    }
  } else if (orderForm.tradeType === 2) {
    // 线上交易验证
    if (!orderForm.recipient.trim()) {
      ElMessage.warning('请输入收货人姓名')
      return
    }
    
    if (!orderForm.phone.trim()) {
      ElMessage.warning('请输入联系电话')
      return
    }
    
    if (!orderForm.address.trim()) {
      ElMessage.warning('请输入收货地址')
      return
    }
  }
  
  try {
    submitting.value = true
    
    const orderData = {
      productId: product.value.id,
      sellerId: product.value.seller.id,
      amount: product.value.price,
      tradeType: orderForm.tradeType,
      paymentMethod: orderForm.paymentMethod,
      remark: orderForm.remark
    }
    
    // 根据交易方式添加相应字段
    if (orderForm.tradeType === 1) {
      // 线下交易
      orderData.tradeLocation = orderForm.tradeLocation
    } else if (orderForm.tradeType === 2) {
      // 线上交易
      orderData.receiverName = orderForm.recipient
      orderData.receiverPhone = orderForm.phone
      orderData.deliveryAddress = orderForm.address
    }
    
    const response = await createOrder(orderData)
    
    // 响应拦截器已处理成功状态，直接使用data
    ElMessage.success('订单创建成功')
    // 跳转到订单详情页面
    router.push(`/order/${response.data.id}`)
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 获取商品图片
const getProductImage = (images) => {
  const imageList = images?.imageList || images?.images || images || []
  if (imageList && imageList.length > 0) {
    return imageList[0]
  }
  return 'https://via.placeholder.com/300x200/f5f5f5/cccccc?text=暂无图片'
}

// 获取成色文本
const getConditionText = (condition) => {
  const conditionMap = {
    1: '全新',
    2: '几乎全新',
    3: '轻微使用痕迹',
    4: '明显使用痕迹',
    5: '重度使用痕迹'
  }
  return conditionMap[condition] || '未知'
}

// 初始化
onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  fetchProductDetail()
})
</script>

<style lang="scss" scoped>
.order-confirm-page {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .order-confirm {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 24px;
  }
  
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #ebeef5;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }
  
  .order-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .product-section,
  .seller-section,
  .trade-section,
  .delivery-section,
  .remark-section,
  .order-summary {
    .el-card {
      border-radius: 8px;
    }
  }
  
  .trade-section {
    .trade-type-desc {
      margin-top: 8px;
      
      .desc-text {
        font-size: 12px;
        color: #909399;
        line-height: 1.4;
      }
    }
    
    .el-radio-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .el-radio {
        margin-right: 0;
        margin-bottom: 0;
      }
    }
  }
  
  .product-info {
    display: flex;
    gap: 16px;
    
    img {
      width: 120px;
      height: 120px;
      object-fit: cover;
      border-radius: 8px;
    }
    
    .product-details {
      flex: 1;
      
      .product-title {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
      
      .product-description {
        margin: 0 0 12px 0;
        color: #606266;
        font-size: 14px;
        line-height: 1.4;
      }
      
      .product-meta {
        display: flex;
        gap: 12px;
        margin-bottom: 12px;
        
        span {
          padding: 4px 8px;
          background: #f5f7fa;
          border-radius: 4px;
          font-size: 12px;
          color: #606266;
        }
      }
      
      .price-info {
        .current-price {
          font-size: 20px;
          font-weight: 600;
          color: #e6a23c;
        }
        
        .original-price {
          margin-left: 8px;
          font-size: 14px;
          color: #909399;
          text-decoration: line-through;
        }
      }
    }
  }
  
  .seller-info {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .seller-details {
      h4 {
        margin: 0 0 4px 0;
        font-size: 16px;
        color: #303133;
      }
      
      .seller-meta {
        display: flex;
        gap: 12px;
        font-size: 14px;
        color: #606266;
      }
    }
  }
  
  .summary-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    
    .label {
      color: #606266;
    }
    
    .value {
      color: #303133;
      font-weight: 500;
    }
  }
  
  .summary-total {
    display: flex;
    justify-content: space-between;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
    font-size: 18px;
    font-weight: 600;
    
    .total-price {
      color: #e6a23c;
    }
  }
  
  .submit-section {
    text-align: center;
    padding-top: 20px;
    
    .submit-btn {
      width: 200px;
      height: 48px;
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  .order-confirm-page {
    .container {
      padding: 10px;
    }
    
    .product-info {
      flex-direction: column;
      
      img {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>