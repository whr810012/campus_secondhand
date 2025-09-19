<template>
  <div class="user-profile-page">
    <div class="container">
      <!-- 用户信息卡片 -->
      <el-card class="user-info-card" v-loading="loading">
        <div class="user-header">
          <div class="avatar-section">
            <el-avatar :src="userInfo.avatar" :size="80">
              {{ userInfo.nickname?.charAt(0) }}
            </el-avatar>
          </div>
          
          <div class="user-details">
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
              <el-tag v-else type="info" size="small">
                <el-icon><User /></el-icon>
                未认证
              </el-tag>
              
              <span class="join-time">加入时间：{{ formatDate(userInfo.createdAt) }}</span>
            </div>
            
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.productCount || 0 }}</span>
                <span class="stat-label">发布商品</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.tradeCount || 0 }}</span>
                <span class="stat-label">交易次数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userInfo.creditScore || 0 }}</span>
                <span class="stat-label">信用评分</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 用户商品列表 -->
      <el-card class="products-card">
        <template #header>
          <div class="card-header">
            <span>TA的商品</span>
            <el-tag>{{ products.length }} 件商品</el-tag>
          </div>
        </template>
        
        <div v-if="productsLoading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="products.length === 0" class="empty-container">
          <el-empty description="暂无商品" />
        </div>
        
        <div v-else class="products-grid">
          <div 
            v-for="product in products" 
            :key="product.id" 
            class="product-item"
            @click="viewProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.images?.[0] || '/images/placeholder.jpg'" :alt="product.title" />
              <div class="product-status" v-if="product.status !== 1">
                <el-tag v-if="product.status === 0" type="warning" size="small">待审核</el-tag>
                <el-tag v-else-if="product.status === 2" type="success" size="small">已售出</el-tag>
                <el-tag v-else-if="product.status === 3" type="info" size="small">已下架</el-tag>
              </div>
            </div>
            
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-time">{{ formatDate(product.createdAt) }}</p>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, Warning, User } from '@element-plus/icons-vue'
import { getUserProfile, getUserProducts } from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const productsLoading = ref(true)
const userInfo = ref({})
const products = ref([])

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    loading.value = true
    const userId = route.params.id
    const response = await getUserProfile(userId)
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 获取用户商品
const fetchUserProducts = async () => {
  try {
    productsLoading.value = true
    const userId = route.params.id
    const response = await getUserProducts(userId)
    products.value = response.data.records || []
  } catch (error) {
    console.error('获取用户商品失败:', error)
    ElMessage.error('获取用户商品失败')
  } finally {
    productsLoading.value = false
  }
}

// 查看商品详情
const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchUserInfo()
  fetchUserProducts()
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.user-info-card {
  margin-bottom: 20px;
}

.user-header {
  display: flex;
  gap: 20px;
}

.avatar-section {
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.user-details h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.join-time {
  color: #666;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.user-bio {
  color: #666;
  line-height: 1.6;
}

.products-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container,
.empty-container {
  padding: 40px 0;
  text-align: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.product-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-image {
  position: relative;
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-status {
  position: absolute;
  top: 8px;
  right: 8px;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
  margin: 0 0 8px 0;
}

.product-time {
  font-size: 12px;
  color: #999;
  margin: 0;
}

@media (max-width: 768px) {
  .user-header {
    flex-direction: column;
    text-align: center;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
}
</style>