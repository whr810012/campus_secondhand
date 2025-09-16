<template>
  <div class="product-detail">
    <!-- 商品图片轮播 -->
    <div class="product-images">
      <el-carousel height="400px" indicator-position="outside">
        <el-carousel-item v-for="(image, index) in product.images" :key="index">
          <img :src="image" :alt="product.title" class="product-image" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <div class="product-header">
        <h1 class="product-title">{{ product.title }}</h1>
        <div class="product-price">¥{{ product.price }}</div>
      </div>

      <div class="product-meta">
        <el-tag type="info" size="small">{{ product.category }}</el-tag>
        <span class="product-condition">{{ product.condition }}</span>
        <span class="product-location">{{ product.location }}</span>
        <span class="product-time">{{ formatTime(product.createTime) }}</span>
      </div>

      <div class="product-description">
        <h3>商品描述</h3>
        <p>{{ product.description }}</p>
      </div>

      <!-- 卖家信息 -->
      <div class="seller-info">
        <h3>卖家信息</h3>
        <div class="seller-card">
          <el-avatar :src="seller.avatar" :size="50">{{ seller.nickname }}</el-avatar>
          <div class="seller-details">
            <div class="seller-name">{{ seller.nickname }}</div>
            <div class="seller-school">{{ seller.school }}</div>
            <div class="seller-rating">
              <el-rate v-model="seller.rating" disabled show-score />
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="product-actions">
        <el-button 
          type="primary" 
          size="large" 
          @click="handleBuy"
          :disabled="product.status !== 'available'"
        >
          立即购买
        </el-button>
        <el-button 
          size="large" 
          @click="handleFavorite"
          :icon="isFavorited ? 'Star' : 'StarFilled'"
        >
          {{ isFavorited ? '已收藏' : '收藏' }}
        </el-button>
        <el-button size="large" @click="handleContact">
          联系卖家
        </el-button>
      </div>
    </div>

    <!-- 商品评论 -->
    <div class="product-comments">
      <h3>商品评论</h3>
      <div v-if="comments.length === 0" class="no-comments">
        <el-empty description="暂无评论" />
      </div>
      <div v-else class="comments-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :src="comment.user.avatar" :size="40">{{ comment.user.nickname }}</el-avatar>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-user">{{ comment.user.nickname }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
          </div>
        </div>
      </div>

      <!-- 添加评论 -->
      <div v-if="userStore.isLoggedIn" class="add-comment">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="200"
          show-word-limit
        />
        <el-button type="primary" @click="handleAddComment" :loading="commentLoading">
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 购买确认对话框 -->
    <el-dialog v-model="buyDialogVisible" title="确认购买" width="400px">
      <div class="buy-confirm">
        <div class="buy-product">
          <img :src="product.images[0]" :alt="product.title" class="buy-product-image" />
          <div class="buy-product-info">
            <div class="buy-product-title">{{ product.title }}</div>
            <div class="buy-product-price">¥{{ product.price }}</div>
          </div>
        </div>
        <div class="buy-options">
          <el-radio-group v-model="paymentMethod">
            <el-radio label="online">在线支付</el-radio>
            <el-radio label="offline">线下交易</el-radio>
          </el-radio-group>
        </div>
        <div class="buy-note">
          <el-input
            v-model="buyNote"
            type="textarea"
            placeholder="给卖家留言（可选）"
            :rows="2"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBuy" :loading="buyLoading">
          确认购买
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const product = reactive({
  id: '',
  title: '',
  price: 0,
  description: '',
  images: [],
  category: '',
  condition: '',
  location: '',
  status: 'available',
  createTime: '',
  sellerId: ''
})

const seller = reactive({
  id: '',
  nickname: '',
  avatar: '',
  school: '',
  rating: 0
})

const comments = ref([])
const newComment = ref('')
const commentLoading = ref(false)
const isFavorited = ref(false)
const buyDialogVisible = ref(false)
const paymentMethod = ref('online')
const buyNote = ref('')
const buyLoading = ref(false)

// 计算属性
const productId = computed(() => route.params.id)

// 方法
const fetchProductDetail = async () => {
  try {
    const response = await api.get(`/products/${productId.value}`)
    Object.assign(product, response.data)
    
    // 获取卖家信息
    const sellerResponse = await api.get(`/users/${product.sellerId}`)
    Object.assign(seller, sellerResponse.data)
    
    // 检查是否已收藏
    if (userStore.isLoggedIn) {
      const favoriteResponse = await api.get(`/favorites/check/${productId.value}`)
      isFavorited.value = favoriteResponse.data
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    console.error('获取商品详情失败:', error)
  }
}

const fetchComments = async () => {
  try {
    const response = await api.get(`/products/${productId.value}/comments`)
    comments.value = response.data
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

const handleBuy = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (product.sellerId === userStore.user.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  
  buyDialogVisible.value = true
}

const confirmBuy = async () => {
  buyLoading.value = true
  try {
    const orderData = {
      productId: product.id,
      sellerId: product.sellerId,
      paymentMethod: paymentMethod.value,
      note: buyNote.value
    }
    
    const response = await api.post('/orders', orderData)
    ElMessage.success('订单创建成功')
    buyDialogVisible.value = false
    
    // 跳转到订单详情页
    router.push(`/user/orders/${response.data.id}`)
  } catch (error) {
    ElMessage.error('创建订单失败')
    console.error('创建订单失败:', error)
  } finally {
    buyLoading.value = false
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      await api.delete(`/favorites/${productId.value}`)
      ElMessage.success('取消收藏成功')
      isFavorited.value = false
    } else {
      await api.post('/favorites', { productId: productId.value })
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    ElMessage.error('操作失败')
    console.error('收藏操作失败:', error)
  }
}

const handleContact = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 跳转到聊天页面
  router.push(`/user/chat/${seller.id}`)
}

const handleAddComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  commentLoading.value = true
  try {
    const commentData = {
      productId: productId.value,
      content: newComment.value
    }
    
    await api.post('/comments', commentData)
    ElMessage.success('评论发表成功')
    newComment.value = ''
    
    // 重新获取评论列表
    await fetchComments()
  } catch (error) {
    ElMessage.error('发表评论失败')
    console.error('发表评论失败:', error)
  } finally {
    commentLoading.value = false
  }
}

const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  fetchProductDetail()
  fetchComments()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.product-images {
  position: sticky;
  top: 20px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 16px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.4;
}

.product-price {
  font-size: 28px;
  font-weight: 700;
  color: #e74c3c;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.product-condition,
.product-location,
.product-time {
  color: #666;
  font-size: 14px;
}

.product-description h3 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #333;
}

.product-description p {
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

.seller-info h3 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #333;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.seller-school {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.product-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.product-actions .el-button {
  flex: 1;
  min-width: 120px;
}

.product-comments {
  grid-column: 1 / -1;
  margin-top: 40px;
}

.product-comments h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.comments-list {
  margin-bottom: 24px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: 600;
  color: #333;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-text {
  color: #666;
  line-height: 1.5;
}

.add-comment {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.add-comment .el-button {
  align-self: flex-end;
}

.buy-confirm {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.buy-product {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.buy-product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.buy-product-info {
  flex: 1;
}

.buy-product-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.buy-product-price {
  color: #e74c3c;
  font-size: 18px;
  font-weight: 600;
}

.buy-options .el-radio-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

@media (max-width: 768px) {
  .product-detail {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 16px;
  }
  
  .product-images {
    position: static;
  }
  
  .product-actions {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 16px;
    background: white;
    border-top: 1px solid #eee;
    z-index: 100;
  }
  
  .product-info {
    padding-bottom: 80px;
  }
}
</style>