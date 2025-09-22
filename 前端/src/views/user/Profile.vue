<template>
  <div class="profile-page">
    <div class="container">
      <!-- 个人信息卡片 -->
      <el-card class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :src="userInfo.avatar" :size="80">
              {{ userInfo.nickname?.charAt(0) }}
            </el-avatar>
          </div>
          
          <div class="user-info">
            <h2>{{ userInfo.nickname }}</h2>
            <div class="user-meta">
              <el-tag v-if="userInfo.verifyStatus === 2" type="success" size="small">
                <el-icon><CircleCheck /></el-icon>
                已认证学生
              </el-tag>
              <el-tag v-else-if="userInfo.verifyStatus === 1" type="warning" size="small">
                <el-icon><Warning /></el-icon>
                认证审核中
              </el-tag>
              <el-tag v-else-if="userInfo.verifyStatus === 3" type="danger" size="small">
                <el-icon><Warning /></el-icon>
                认证失败
              </el-tag>
              <el-tag v-else type="info" size="small">
                <el-icon><Warning /></el-icon>
                未认证
              </el-tag>
              <span class="join-date">加入时间：{{ formatDate(userInfo.createdAt) }}</span>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ userStats.productCount }}</span>
                <span class="stat-label">发布商品</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userStats.orderCount }}</span>
                <span class="stat-label">交易订单</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userStats.favoriteCount }}</span>
                <span class="stat-label">收藏商品</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.creditScore || 0 }}</span>
                <span class="stat-label">信誉积分</span>
              </div>
            </div>
          </div>
          
          <div class="profile-actions">
            <el-button type="primary" @click="showEditDialog = true">
              编辑资料
            </el-button>
            <el-button v-if="userInfo.verifyStatus !== 2" type="warning" @click="goToVerification">
              学生认证
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 功能导航 -->
      <div class="function-nav">
        <el-card
          v-for="item in functionItems"
          :key="item.key"
          class="function-item"
          :class="{ active: activeTab === item.key }"
          @click="activeTab = item.key"
        >
          <div class="function-content">
            <el-icon class="function-icon" :size="24">
              <component :is="item.icon" />
            </el-icon>
            <div class="function-info">
              <h4>{{ item.title }}</h4>
              <p>{{ item.description }}</p>
            </div>
            <div class="function-count">
              {{ item.count }}
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 我的发布 -->
        <div v-if="activeTab === 'products'" class="tab-content">
          <div class="content-header">
            <h3>我的发布</h3>
            <el-button type="primary" @click="$router.push('/publish')">
              发布新商品
            </el-button>
          </div>
          
          <div class="product-filters">
            <el-radio-group v-model="productFilter" @change="fetchMyProducts">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="pending">待审核</el-radio-button>
              <el-radio-button label="available">在售</el-radio-button>
              <el-radio-button label="reserved">已预定</el-radio-button>
              <el-radio-button label="sold">已售出</el-radio-button>
              <el-radio-button label="unavailable">已下架</el-radio-button>
            </el-radio-group>
          </div>
          
          <div v-loading="loading.products" class="products-list">
            <div
              v-for="product in myProducts"
              :key="product.id"
              class="product-item"
            >
              <div class="product-image">
                <img :src="getProductImage(product.images)" :alt="product.title" />
              </div>
              
              <div class="product-info">
                <h4>{{ product.title }}</h4>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-meta">
                  <div class="price-info">
                    <span class="price">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">原价：¥{{ product.originalPrice }}</span>
                  </div>
                  <el-tag :type="getStatusType(product.status, product.auditStatus)" size="small">
                    {{ getStatusText(product.status, product.auditStatus) }}
                  </el-tag>
                </div>
                <div class="product-stats">
                  <span>浏览：{{ product.viewCount }}</span>
                  <span>收藏：{{ product.favoriteCount }}</span>
                  <span>成色：{{ getConditionText(product.condition) }}</span>
                  <span>交易：{{ getTradeTypeText(product.tradeType) }}</span>
                  <span>发布：{{ formatDate(product.createdAt) }}</span>
                </div>
              </div>
              
              <div class="product-actions">
                <el-button size="small" @click="viewProduct(product.id)">
                  查看
                </el-button>
                <el-button size="small" type="primary" @click="editProduct(product.id)">
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="handleDeleteProduct(product.id)">
                  删除
                </el-button>
              </div>
            </div>
            
            <div v-if="!loading.products && myProducts.length === 0" class="empty-state">
              <el-icon class="empty-icon"><Box /></el-icon>
              <p>暂无发布的商品</p>
              <el-button type="primary" @click="$router.push('/publish')">
                发布第一个商品
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 我的订单 -->
        <div v-if="activeTab === 'orders'" class="tab-content">
          <div class="content-header">
            <h3>我的订单</h3>
          </div>
          
          <div class="order-filters">
            <el-radio-group v-model="orderFilter" @change="fetchMyOrders">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="pending">待付款</el-radio-button>
              <el-radio-button label="paid">已付款</el-radio-button>
              <el-radio-button label="shipped">已发货</el-radio-button>
              <el-radio-button label="delivered">已送达</el-radio-button>
              <el-radio-button label="completed">已完成</el-radio-button>
              <el-radio-button label="cancelled">已取消</el-radio-button>
              <el-radio-button label="refunded">已退款</el-radio-button>
            </el-radio-group>
          </div>
          
          <div v-loading="loading.orders" class="orders-list">
            <div
              v-for="order in myOrders"
              :key="order.id"
              class="order-item"
            >
              <div class="order-header">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <el-tag :type="getOrderStatusType(order.status)" size="small">
                  {{ getOrderStatusText(order.status) }}
                </el-tag>
              </div>
              
              <div class="order-content">
                <div class="product-info">
                  <img :src="getProductImage(order.product?.images)" :alt="order.product?.title" />
                  <div class="product-details">
                    <h4>{{ order.product?.title }}</h4>
                    <p>{{ order.product?.description }}</p>
                    <div class="price-info">
                      <span class="price">¥{{ order.totalAmount }}</span>
                      <span class="quantity">x{{ order.quantity }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="order-actions">
                  <el-button size="small" @click="viewOrderDetail(order.id)">
                    查看详情
                  </el-button>
                  <el-button
                    v-if="order.status === 'pending'"
                    size="small"
                    type="primary"
                    @click="payOrder(order.id)"
                  >
                    立即付款
                  </el-button>
                  <el-button
                    v-if="order.status === 'shipped'"
                    size="small"
                    type="success"
                    @click="confirmReceive(order.id)"
                  >
                    确认收货
                  </el-button>
                  <el-button
                    v-if="order.status === 'completed'"
                    size="small"
                    @click="rateOrder(order.id)"
                  >
                    评价
                  </el-button>
                </div>
              </div>
              
              <div class="order-footer">
                <span class="order-time">下单时间：{{ formatDate(order.createdAt) }}</span>
              </div>
            </div>
            
            <div v-if="!loading.orders && myOrders.length === 0" class="empty-state">
              <el-icon class="empty-icon"><ShoppingCart /></el-icon>
              <p>暂无订单记录</p>
            </div>
          </div>
        </div>
        
        <!-- 我的收藏 -->
        <div v-if="activeTab === 'favorites'" class="tab-content">
          <div class="content-header">
            <h3>我的收藏</h3>
            <div class="header-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="$router.push('/user/favorites')"
              >
                查看全部
              </el-button>
            </div>
          </div>
          
          <div v-loading="loading.favorites" class="favorites-grid">
            <div
              v-for="item in myFavorites"
              :key="item.id"
              class="favorite-item"
            >
              <div class="product-image" @click="viewProduct(item.id)">
                <img :src="getProductImage(item.images)" :alt="item.title" />
                <div class="product-status" v-if="item.status !== 'available'">
                  {{ getStatusText(item.status, item.auditStatus) }}
                </div>
                <div class="favorite-actions" @click.stop>
                  <el-button
                    size="small"
                    type="danger"
                    circle
                    @click="removeFavoriteItem(item.id)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              
              <div class="product-info">
                <h4 class="product-title" @click="viewProduct(item.id)">{{ item.title }}</h4>
                <div class="product-meta">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                    ¥{{ item.originalPrice }}
                  </span>
                </div>
                <div class="product-details">
                  <span class="condition">{{ getConditionText(item.condition) }}</span>
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ item.viewCount || 0 }}
                  </span>
                  <span class="favorites">
                    <el-icon><Star /></el-icon>
                    {{ item.favoriteCount || 0 }}
                  </span>
                </div>
                <div class="favorite-time">收藏于 {{ formatDate(item.createdAt) }}</div>
              </div>
            </div>
            
            <div v-if="!loading.favorites && myFavorites.length === 0" class="empty-state">
              <el-icon class="empty-icon"><Star /></el-icon>
              <p>暂无收藏的商品</p>
              <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
            </div>
          </div>
        </div>
        
        <!-- 账户设置 -->
        <div v-if="activeTab === 'settings'" class="tab-content">
          <div class="content-header">
            <h3>账户设置</h3>
          </div>
          
          <div class="settings-list">
            <el-card class="setting-item" @click="showPasswordDialog = true">
              <div class="setting-content">
                <el-icon class="setting-icon"><Lock /></el-icon>
                <div class="setting-info">
                  <h4>修改密码</h4>
                  <p>定期修改密码，保护账户安全</p>
                </div>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </el-card>
            
            <el-card class="setting-item" @click="showPhoneDialog = true">
              <div class="setting-content">
                <el-icon class="setting-icon"><Phone /></el-icon>
                <div class="setting-info">
                  <h4>修改手机号</h4>
                  <p>当前手机号：{{ maskPhone(userInfo.phone) }}</p>
                </div>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </el-card>
            
            <el-card class="setting-item" @click="logout">
              <div class="setting-content">
                <el-icon class="setting-icon"><SwitchButton /></el-icon>
                <div class="setting-info">
                  <h4>退出登录</h4>
                  <p>退出当前账户</p>
                </div>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑资料" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>

      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateProfile">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 更换头像对话框 -->
    <el-dialog v-model="showAvatarDialog" title="更换头像" width="400px">
      <div class="avatar-upload">
        <el-upload
          :action="uploadAction"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <el-avatar :src="previewAvatar || userInfo.avatar" :size="120">
            {{ userInfo.nickname?.charAt(0) }}
          </el-avatar>
          <div class="upload-tip">点击上传新头像</div>
        </el-upload>
      </div>
      
      <template #footer>
        <el-button @click="showAvatarDialog = false">取消</el-button>
        <el-button type="primary" @click="updateAvatar">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, Warning, Delete, Star, View } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getUserProfile,
  updateUserProfile,
  getUserStats,
  getMyProducts,
  getMyOrders,
  getMyFavorites,
  changePassword as changePasswordApi
} from '@/api/auth'
import { deleteProduct } from '@/api/product'
import { removeFromFavorites } from '@/api/favorite'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('products')
const showEditDialog = ref(false)
const showAvatarDialog = ref(false)
const showPasswordDialog = ref(false)
const showPhoneDialog = ref(false)
const previewAvatar = ref('')

const productFilter = ref('all')
const orderFilter = ref('all')

const loading = reactive({
  products: false,
  orders: false,
  favorites: false
})

const userInfo = ref({})
const userStats = ref({})
const myProducts = ref([])
const myOrders = ref([])
const myFavorites = ref([])

const editForm = reactive({
  nickname: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 功能导航项
const functionItems = computed(() => [
  {
    key: 'products',
    title: '我的发布',
    description: '管理我发布的商品',
    icon: 'Box',
    count: userStats.value.productCount || 0
  },
  {
    key: 'orders',
    title: '我的订单',
    description: '查看购买记录',
    icon: 'ShoppingCart',
    count: userStats.value.orderCount || 0
  },
  {
    key: 'favorites',
    title: '我的收藏',
    description: '收藏的商品',
    icon: 'Star',
    count: userStats.value.favoriteCount || 0
  },
  {
    key: 'settings',
    title: '账户设置',
    description: '个人信息设置',
    icon: 'Setting',
    count: ''
  }
])

const uploadAction = '/api/upload/avatar'
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const response = await getUserProfile()
    userInfo.value = response.data
    editForm.nickname = userInfo.value.nickname
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取用户统计
const fetchUserStats = async () => {
  try {
    const response = await getUserStats()
    userStats.value = response.data
  } catch (error) {
    console.error('获取用户统计失败:', error)
  }
}

// 获取我的商品
const fetchMyProducts = async () => {
  loading.products = true
  try {
    let params = {}
    if (productFilter.value !== 'all') {
      if (productFilter.value === 'pending') {
        // 待审核状态使用audit_status参数，数据库中0表示待审核
        params.audit_status = 0
      } else {
        // 其他状态使用status参数
        params.status = productFilter.value
      }
    }
    const response = await getMyProducts(params)
    myProducts.value = response.data.records || []
  } catch (error) {
    console.error('获取我的商品失败:', error)
  } finally {
    loading.products = false
  }
}

// 获取我的订单
const fetchMyOrders = async () => {
  loading.orders = true
  try {
    const params = orderFilter.value === 'all' ? {} : { status: orderFilter.value }
    const response = await getMyOrders(params)
    myOrders.value = response.data.records || []
  } catch (error) {
    console.error('获取我的订单失败:', error)
  } finally {
    loading.orders = false
  }
}

// 获取我的收藏
const fetchMyFavorites = async () => {
  loading.favorites = true
  try {
    const response = await getMyFavorites({ page: 1, size: 12 })
    // 处理返回的数据结构
    if (response.data && response.data.records) {
      myFavorites.value = response.data.records
    } else if (response.data && Array.isArray(response.data)) {
      myFavorites.value = response.data
    } else {
      myFavorites.value = []
    }
    console.log('收藏数据:', response.data)
  } catch (error) {
    console.error('获取我的收藏失败:', error)
    ElMessage.error('获取收藏列表失败')
    myFavorites.value = []
  } finally {
    loading.favorites = false
  }
}

// 工具函数
const getProductImage = (images) => {
  if (images && images.length > 0) {
    return images[0]
  }
  return 'https://via.placeholder.com/200x150/f5f5f5/cccccc?text=暂无图片'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const getStatusText = (status, auditStatus) => {
  // 优先显示审核状态
  if (auditStatus === 0) {
    return '待审核'
  }
  if (auditStatus === 2) {
    return '审核不通过'
  }
  
  // 审核通过后显示商品状态
  const statusMap = {
    'available': '在售',
    'reserved': '已预定', 
    'sold': '已售出',
    'unavailable': '已下架'
  }
  
  return statusMap[status] || '未知'
}

const getStatusType = (status, auditStatus) => {
  // 优先显示审核状态
  if (auditStatus === 0) {
    return 'warning'
  }
  if (auditStatus === 2) {
    return 'danger'
  }
  
  // 审核通过后显示商品状态
  const typeMap = {
    'available': 'success',
    'reserved': 'primary',
    'sold': 'info', 
    'unavailable': 'warning'
  }
  
  return typeMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const statusMap = {
    'pending': '待付款',
    'paid': '已付款',
    'shipped': '已发货',
    'delivered': '已送达',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunded': '已退款'
  }
  return statusMap[status] || '未知'
}

const getOrderStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'paid': 'primary',
    'shipped': 'info',
    'delivered': 'primary',
    'completed': 'success',
    'cancelled': 'danger',
    'refunded': 'warning'
  }
  return typeMap[status] || 'info'
}

// 商品成色映射
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

// 交易方式映射
const getTradeTypeText = (tradeType) => {
  const tradeTypeMap = {
    1: '仅线下',
    2: '仅线上',
    3: '线上线下均可'
  }
  return tradeTypeMap[tradeType] || '未知'
}

// 事件处理
const updateProfile = async () => {
  try {
    await updateUserProfile(editForm)
    ElMessage.success('资料更新成功')
    showEditDialog.value = false
    await fetchUserProfile()
  } catch (error) {
    console.error('更新资料失败:', error)
    ElMessage.error('更新资料失败')
  }
}

const handleAvatarSuccess = (response) => {
  previewAvatar.value = response.data.url
  ElMessage.success('头像上传成功')
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const updateAvatar = async () => {
  if (!previewAvatar.value) {
    ElMessage.warning('请先上传头像')
    return
  }
  
  try {
    await updateUserProfile({ avatar: previewAvatar.value })
    ElMessage.success('头像更新成功')
    showAvatarDialog.value = false
    previewAvatar.value = ''
    await fetchUserProfile()
  } catch (error) {
    console.error('更新头像失败:', error)
    ElMessage.error('更新头像失败')
  }
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await changePasswordApi(passwordForm)
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  }
}

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}

const goToVerification = () => {
  router.push('/verification')
}

const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const editProduct = (productId) => {
  router.push(`/publish?id=${productId}`)
}

const handleDeleteProduct = (productId) => {
  if (!productId) {
    ElMessage.error('商品ID无效')
    return
  }
  
  ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
    console.log('userStore', userStore)
    
      await deleteProduct(productId, userStore.userInfo.id)
      ElMessage.success('删除成功')
      fetchMyProducts()
    } catch (error) {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  })
}

const viewOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const payOrder = (orderId) => {
  router.push(`/order/pay/${orderId}`)
}

const confirmReceive = (orderId) => {
  ElMessageBox.confirm('确定已收到商品吗？', '确认收货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // TODO: 确认收货
    ElMessage.success('确认收货成功')
    fetchMyOrders()
  })
}

const rateOrder = (orderId) => {
  router.push(`/order/rate/${orderId}`)
}

const removeFavoriteItem = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await removeFromFavorites(productId)
    ElMessage.success('取消收藏成功')
    await fetchMyFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchUserProfile(),
    fetchUserStats()
  ])
  
  // 根据当前标签加载对应数据
  if (activeTab.value === 'products') {
    fetchMyProducts()
  } else if (activeTab.value === 'orders') {
    fetchMyOrders()
  } else if (activeTab.value === 'favorites') {
    fetchMyFavorites()
  }
})

// 监听标签切换
watch(activeTab, (newTab) => {
  if (newTab === 'products' && myProducts.value.length === 0) {
    fetchMyProducts()
  } else if (newTab === 'orders' && myOrders.value.length === 0) {
    fetchMyOrders()
  } else if (newTab === 'favorites' && myFavorites.value.length === 0) {
    fetchMyFavorites()
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
  .profile-card {
    margin-bottom: 24px;
    
    .profile-header {
      display: flex;
      align-items: center;
      gap: 24px;
      
      .avatar-section {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
      }
      
      .user-info {
        flex: 1;
        
        h2 {
          font-size: 24px;
          font-weight: 600;
          margin-bottom: 8px;
        }
        
        .user-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 16px;
          
          .join-date {
            font-size: 14px;
            color: var(--text-secondary);
          }
        }
        
        .user-stats {
          display: flex;
          gap: 32px;
          
          .stat-item {
            text-align: center;
            
            .stat-value {
              display: block;
              font-size: 20px;
              font-weight: 600;
              color: var(--primary-color);
            }
            
            .stat-label {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
      }
      
      .profile-actions {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }
  }
  
  .function-nav {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
    margin-bottom: 24px;
    
    .function-item {
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      &.active {
        border-color: var(--primary-color);
        
        .function-icon {
          color: var(--primary-color);
        }
      }
      
      .function-content {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .function-icon {
          color: var(--text-secondary);
        }
        
        .function-info {
          flex: 1;
          
          h4 {
            font-size: 16px;
            font-weight: 500;
            margin-bottom: 4px;
          }
          
          p {
            font-size: 12px;
            color: var(--text-secondary);
            margin: 0;
          }
        }
        
        .function-count {
          font-size: 18px;
          font-weight: 600;
          color: var(--primary-color);
        }
      }
    }
  }
  
  .content-area {
    .tab-content {
      .content-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          margin: 0;
        }
        
        .header-actions {
          display: flex;
          gap: 8px;
        }
      }
      
      .product-filters,
      .order-filters {
        margin-bottom: 20px;
      }
      
      .products-list {
        .product-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 16px;
          background: white;
          border-radius: 8px;
          margin-bottom: 12px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          
          .product-image {
            img {
              width: 80px;
              height: 80px;
              object-fit: cover;
              border-radius: 8px;
            }
          }
          
          .product-info {
            flex: 1;
            
            h4 {
              font-size: 16px;
              margin-bottom: 8px;
            }
            
            .product-desc {
              font-size: 14px;
              color: var(--text-secondary);
              margin-bottom: 8px;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }
            
            .product-meta {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 8px;
              
              .price-info {
                display: flex;
                flex-direction: column;
                gap: 4px;
                
                .price {
                  font-size: 16px;
                  font-weight: 600;
                  color: var(--danger-color);
                }
                
                .original-price {
                  font-size: 14px;
                  color: var(--text-secondary);
                  text-decoration: line-through;
                }
              }
            }
            
            .product-stats {
              display: flex;
              gap: 16px;
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
          
          .product-actions {
            display: flex;
            gap: 8px;
          }
        }
      }
      
      .orders-list {
        .order-item {
          background: white;
          border-radius: 8px;
          margin-bottom: 16px;
          overflow: hidden;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          
          .order-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px;
            background: var(--bg-secondary);
            border-bottom: 1px solid var(--border-color);
            
            .order-no {
              font-size: 14px;
              color: var(--text-secondary);
            }
          }
          
          .order-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px;
            
            .product-info {
              display: flex;
              gap: 12px;
              flex: 1;
              
              img {
                width: 60px;
                height: 60px;
                object-fit: cover;
                border-radius: 6px;
              }
              
              .product-details {
                h4 {
                  font-size: 14px;
                  margin-bottom: 4px;
                }
                
                p {
                  font-size: 12px;
                  color: var(--text-secondary);
                  margin-bottom: 8px;
                }
                
                .price-info {
                  .price {
                    font-size: 16px;
                    font-weight: 600;
                    color: var(--danger-color);
                  }
                  
                  .quantity {
                    margin-left: 8px;
                    font-size: 12px;
                    color: var(--text-secondary);
                  }
                }
              }
            }
            
            .order-actions {
              display: flex;
              gap: 8px;
            }
          }
          
          .order-footer {
            padding: 12px 16px;
            background: var(--bg-secondary);
            border-top: 1px solid var(--border-color);
            
            .order-time {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
      }
      
      .favorites-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 20px;
        
        .favorite-item {
          background: white;
          border-radius: 12px;
          overflow: hidden;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
          }
          
          .product-image {
            position: relative;
            cursor: pointer;
            
            img {
              width: 100%;
              height: 160px;
              object-fit: cover;
              transition: transform 0.3s ease;
            }
            
            &:hover img {
              transform: scale(1.05);
            }
            
            .product-status {
              position: absolute;
              top: 8px;
              left: 8px;
              background: rgba(0, 0, 0, 0.7);
              color: white;
              padding: 4px 8px;
              border-radius: 4px;
              font-size: 12px;
            }
            
            .favorite-actions {
              position: absolute;
              top: 8px;
              right: 8px;
              opacity: 0;
              transition: opacity 0.3s ease;
            }
            
            &:hover .favorite-actions {
              opacity: 1;
            }
          }
          
          .product-info {
            padding: 16px;
            
            .product-title {
              font-size: 15px;
              font-weight: 600;
              margin-bottom: 8px;
              cursor: pointer;
              color: var(--text-primary);
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              line-height: 1.4;
              
              &:hover {
                color: var(--primary-color);
              }
            }
            
            .product-meta {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-bottom: 8px;
              
              .price {
                font-size: 18px;
                font-weight: 700;
                color: var(--danger-color);
              }
              
              .original-price {
                font-size: 14px;
                color: var(--text-secondary);
                text-decoration: line-through;
              }
            }
            
            .product-details {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 8px;
              font-size: 12px;
              color: var(--text-secondary);
              
              .condition {
                background: var(--bg-secondary);
                padding: 2px 6px;
                border-radius: 4px;
              }
              
              .views, .favorites {
                display: flex;
                align-items: center;
                gap: 2px;
              }
            }
            
            .favorite-time {
              font-size: 12px;
              color: var(--text-placeholder);
            }
          }
        }
      }
      
      .settings-list {
        .setting-item {
          margin-bottom: 12px;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &:hover {
            transform: translateX(4px);
          }
          
          .setting-content {
            display: flex;
            align-items: center;
            gap: 16px;
            
            .setting-icon {
              font-size: 20px;
              color: var(--text-secondary);
            }
            
            .setting-info {
              flex: 1;
              
              h4 {
                font-size: 16px;
                margin-bottom: 4px;
              }
              
              p {
                font-size: 12px;
                color: var(--text-secondary);
                margin: 0;
              }
            }
            
            .arrow-icon {
              color: var(--text-placeholder);
            }
          }
        }
      }
      
      .empty-state {
        text-align: center;
        padding: 60px 20px;
        
        .empty-icon {
          font-size: 48px;
          color: var(--text-placeholder);
          margin-bottom: 16px;
        }
        
        p {
          font-size: 16px;
          color: var(--text-secondary);
          margin-bottom: 20px;
        }
      }
    }
  }
  
  .avatar-upload {
    text-align: center;
    
    .upload-tip {
      margin-top: 12px;
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-page {
    .profile-card {
      .profile-header {
        flex-direction: column;
        text-align: center;
        
        .user-stats {
          justify-content: center;
        }
      }
    }
    
    .function-nav {
      grid-template-columns: 1fr;
    }
    
    .content-area {
      .products-list {
        .product-item {
          flex-direction: column;
          align-items: flex-start;
          
          .product-actions {
            width: 100%;
            justify-content: flex-end;
          }
        }
      }
      
      .orders-list {
        .order-item {
          .order-content {
            flex-direction: column;
            align-items: flex-start;
            gap: 16px;
            
            .order-actions {
              width: 100%;
              justify-content: flex-end;
            }
          }
        }
      }
      
      .favorites-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      }
    }
  }
}
</style>