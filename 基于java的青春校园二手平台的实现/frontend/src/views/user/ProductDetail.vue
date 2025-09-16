<template>
  <div class="product-detail-container">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>
    
    <div v-else-if="product" class="product-detail">
      <!-- 商品基本信息 -->
      <div class="product-main">
        <div class="product-images">
          <div class="main-image">
            <img :src="currentImage" :alt="product.title" />
            <div class="image-actions">
              <el-button
                type="primary"
                icon="el-icon-zoom-in"
                circle
                @click="previewImage"
              />
            </div>
          </div>
          
          <div class="image-thumbnails" v-if="product.images && product.images.length > 1">
            <div
              v-for="(image, index) in product.images"
              :key="index"
              class="thumbnail"
              :class="{ active: currentImage === image }"
              @click="currentImage = image"
            >
              <img :src="image" :alt="`商品图片${index + 1}`" />
            </div>
          </div>
        </div>
        
        <div class="product-info">
          <div class="product-header">
            <h1 class="product-title">{{ product.title }}</h1>
            <div class="product-status">
              <el-tag
                :type="getStatusType(product.status)"
                size="medium"
              >
                {{ getStatusText(product.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="product-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ product.price }}</span>
            <span class="price-unit">/件</span>
          </div>
          
          <div class="product-stats">
            <div class="stat-item">
              <i class="el-icon-view"></i>
              <span>浏览 {{ product.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <i class="el-icon-star-on"></i>
              <span>点赞 {{ product.likeCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <i class="el-icon-time"></i>
              <span>{{ formatTime(product.createTime) }}</span>
            </div>
          </div>
          
          <div class="product-category" v-if="product.categoryName">
            <span class="label">分类：</span>
            <el-tag type="info">{{ product.categoryName }}</el-tag>
          </div>
          
          <div class="product-description">
            <h3>商品描述</h3>
            <p>{{ product.description }}</p>
          </div>
          
          <div class="product-actions" v-if="product.status === 'APPROVED'">
            <el-button
              type="primary"
              size="large"
              icon="el-icon-shopping-cart-2"
              @click="handleBuy"
              :disabled="product.sellerId === currentUserId"
            >
              立即购买
            </el-button>
            
            <el-button
              :type="isLiked ? 'danger' : 'default'"
              size="large"
              :icon="isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
              @click="toggleLike"
            >
              {{ isLiked ? '已点赞' : '点赞' }}
            </el-button>
            
            <el-button
              size="large"
              icon="el-icon-chat-dot-round"
              @click="contactSeller"
            >
              联系卖家
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 卖家信息 -->
      <div class="seller-info-section">
        <h3>卖家信息</h3>
        <div class="seller-card">
          <div class="seller-avatar">
            <el-avatar :size="60" :src="product.sellerAvatar">
              {{ product.sellerName ? product.sellerName.charAt(0) : 'U' }}
            </el-avatar>
          </div>
          
          <div class="seller-details">
            <h4 class="seller-name">{{ product.sellerName || '匿名用户' }}</h4>
            <div class="seller-stats">
              <span class="stat">商品 {{ sellerStats.productCount || 0 }}</span>
              <span class="stat">成交 {{ sellerStats.orderCount || 0 }}</span>
              <span class="stat">信誉 {{ sellerStats.rating || '暂无' }}</span>
            </div>
            <div class="seller-join-time">
              加入时间：{{ formatTime(product.sellerJoinTime) }}
            </div>
          </div>
          
          <div class="seller-actions">
            <el-button
              type="primary"
              @click="viewSellerProducts"
            >
              查看TA的商品
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 推荐商品 -->
      <div class="recommended-section" v-if="recommendedProducts.length > 0">
        <h3>相关推荐</h3>
        <div class="recommended-grid">
          <product-card
            v-for="product in recommendedProducts"
            :key="product.id"
            :product="product"
            @click="goToProduct(product.id)"
          />
        </div>
      </div>
    </div>
    
    <!-- 商品不存在 -->
    <div v-else class="not-found">
      <el-empty description="商品不存在或已下架">
        <el-button type="primary" @click="$router.go(-1)">返回上页</el-button>
      </el-empty>
    </div>
    
    <!-- 图片预览 -->
    <el-dialog
      :visible.sync="imagePreviewVisible"
      width="80%"
      center
      :show-close="false"
      class="image-preview-dialog"
    >
      <img :src="currentImage" style="width: 100%; max-height: 70vh; object-fit: contain;" />
    </el-dialog>
    
    <!-- 购买确认对话框 -->
    <el-dialog
      title="确认购买"
      :visible.sync="buyDialogVisible"
      width="500px"
      center
    >
      <div class="buy-confirm">
        <div class="product-summary">
          <img :src="product.imageUrl" class="summary-image" />
          <div class="summary-info">
            <h4>{{ product.title }}</h4>
            <div class="summary-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
          </div>
        </div>
        
        <div class="buy-options">
          <div class="option-item">
            <span class="option-label">交易方式：</span>
            <el-radio-group v-model="buyForm.tradeType">
              <el-radio label="ONLINE">线上交易</el-radio>
              <el-radio label="OFFLINE">线下交易</el-radio>
            </el-radio-group>
          </div>
          
          <div class="option-item" v-if="buyForm.tradeType === 'ONLINE'">
            <span class="option-label">收货地址：</span>
            <el-select
              v-model="buyForm.addressId"
              placeholder="请选择收货地址"
              style="width: 100%"
            >
              <el-option
                v-for="address in addresses"
                :key="address.id"
                :label="`${address.receiverName} ${address.phone} ${address.address}`"
                :value="address.id"
              />
            </el-select>
            <el-link type="primary" @click="manageAddress">管理地址</el-link>
          </div>
          
          <div class="option-item">
            <span class="option-label">备注信息：</span>
            <el-input
              v-model="buyForm.remark"
              type="textarea"
              :rows="3"
              placeholder="给卖家留言..."
              maxlength="200"
              show-word-limit
            />
          </div>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBuy" :loading="buyLoading">
          确认购买
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ProductCard from '@/components/ProductCard.vue'
import { getProductDetail, likeProduct, unlikeProduct, getRecommendedProducts } from '@/api/product'
import { createOrder } from '@/api/order'
import { getUserAddresses } from '@/api/address'
import { mapGetters } from 'vuex'
import moment from 'moment'

export default {
  name: 'ProductDetail',
  components: {
    ProductCard
  },
  data() {
    return {
      loading: true,
      product: null,
      currentImage: '',
      isLiked: false,
      sellerStats: {},
      recommendedProducts: [],
      imagePreviewVisible: false,
      buyDialogVisible: false,
      buyLoading: false,
      addresses: [],
      buyForm: {
        tradeType: 'ONLINE',
        addressId: null,
        remark: ''
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userInfo']),
    currentUserId() {
      return this.userInfo?.id
    }
  },
  created() {
    this.loadProductDetail()
  },
  watch: {
    '$route.params.id': {
      handler() {
        this.loadProductDetail()
      },
      immediate: false
    }
  },
  methods: {
    async loadProductDetail() {
      this.loading = true
      try {
        const productId = this.$route.params.id
        const res = await getProductDetail(productId)
        
        if (res.data.code === 200) {
          this.product = res.data.data
          this.currentImage = this.product.imageUrl || '/images/default-product.jpg'
          this.isLiked = this.product.isLiked || false
          
          // 加载推荐商品
          this.loadRecommendedProducts()
        } else {
          this.product = null
        }
      } catch (error) {
        console.error('加载商品详情失败:', error)
        this.product = null
      } finally {
        this.loading = false
      }
    },
    
    async loadRecommendedProducts() {
      try {
        const res = await getRecommendedProducts({
          productId: this.product.id,
          categoryId: this.product.categoryId,
          size: 4
        })
        
        if (res.data.code === 200) {
          this.recommendedProducts = res.data.data || []
        }
      } catch (error) {
        console.error('加载推荐商品失败:', error)
      }
    },
    
    async loadAddresses() {
      try {
        const res = await getUserAddresses()
        if (res.data.code === 200) {
          this.addresses = res.data.data || []
          // 设置默认地址
          const defaultAddress = this.addresses.find(addr => addr.isDefault)
          if (defaultAddress) {
            this.buyForm.addressId = defaultAddress.id
          }
        }
      } catch (error) {
        console.error('加载地址失败:', error)
      }
    },
    
    previewImage() {
      this.imagePreviewVisible = true
    },
    
    async toggleLike() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        if (this.isLiked) {
          const res = await unlikeProduct(this.product.id)
          if (res.data.code === 200) {
            this.isLiked = false
            this.product.likeCount = Math.max(0, (this.product.likeCount || 0) - 1)
            this.$message.success('取消点赞成功')
          }
        } else {
          const res = await likeProduct(this.product.id)
          if (res.data.code === 200) {
            this.isLiked = true
            this.product.likeCount = (this.product.likeCount || 0) + 1
            this.$message.success('点赞成功')
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    handleBuy() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      if (this.product.sellerId === this.currentUserId) {
        this.$message.warning('不能购买自己的商品')
        return
      }
      
      this.buyDialogVisible = true
      this.loadAddresses()
    },
    
    async confirmBuy() {
      if (this.buyForm.tradeType === 'ONLINE' && !this.buyForm.addressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      
      this.buyLoading = true
      try {
        const orderData = {
          productId: this.product.id,
          tradeType: this.buyForm.tradeType,
          remark: this.buyForm.remark
        }
        
        if (this.buyForm.tradeType === 'ONLINE') {
          orderData.addressId = this.buyForm.addressId
        }
        
        const res = await createOrder(orderData)
        if (res.data.code === 200) {
          this.$message.success('订单创建成功')
          this.buyDialogVisible = false
          this.$router.push(`/user/order/${res.data.data.id}`)
        } else {
          this.$message.error(res.data.message || '创建订单失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        this.$message.error('创建订单失败，请重试')
      } finally {
        this.buyLoading = false
      }
    },
    
    contactSeller() {
      this.$message.info('联系卖家功能开发中...')
    },
    
    viewSellerProducts() {
      this.$router.push({
        path: '/user/shop',
        query: { sellerId: this.product.sellerId }
      })
    },
    
    manageAddress() {
      this.$router.push('/user/address')
    },
    
    goToProduct(productId) {
      this.$router.push(`/user/product/${productId}`)
    },
    
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'SOLD': 'info',
        'OFFLINE': 'info'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'PENDING': '待审核',
        'APPROVED': '在售',
        'REJECTED': '已拒绝',
        'SOLD': '已售出',
        'OFFLINE': '已下架'
      }
      return textMap[status] || status
    },
    
    formatTime(time) {
      if (!time) return ''
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 商品主要信息 */
.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-images {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.main-image {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 10px;
  overflow: hidden;
  background: #f5f5f5;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 15px;
  right: 15px;
  opacity: 0;
  transition: opacity 0.3s;
}

.main-image:hover .image-actions {
  opacity: 1;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;
  flex-shrink: 0;
}

.thumbnail.active {
  border-color: #409EFF;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 15px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.price-symbol {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 500;
}

.price-value {
  font-size: 36px;
  color: #e74c3c;
  font-weight: 700;
}

.price-unit {
  font-size: 16px;
  color: #999;
}

.product-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.stat-item i {
  font-size: 16px;
  color: #409EFF;
}

.product-category {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-size: 14px;
  color: #666;
}

.product-description {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.product-description h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px 0;
}

.product-description p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.product-actions {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

/* 卖家信息 */
.seller-info-section {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.seller-info-section h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.seller-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 5px;
}

.stat {
  font-size: 13px;
  color: #666;
}

.seller-join-time {
  font-size: 12px;
  color: #999;
}

/* 推荐商品 */
.recommended-section {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recommended-section h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
}

.recommended-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

/* 购买确认对话框 */
.buy-confirm {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-summary {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.summary-info {
  flex: 1;
}

.summary-info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px 0;
}

.summary-price {
  display: flex;
  align-items: baseline;
  gap: 3px;
}

.summary-price .price-symbol {
  font-size: 16px;
  color: #e74c3c;
}

.summary-price .price-value {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 600;
}

.buy-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.option-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 空状态 */
.not-found {
  background: white;
  border-radius: 10px;
  padding: 60px 30px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail-container {
    padding: 10px;
  }
  
  .product-main {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .main-image {
    height: 300px;
  }
  
  .product-title {
    font-size: 20px;
  }
  
  .price-value {
    font-size: 28px;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .seller-card {
    flex-direction: column;
    text-align: center;
  }
  
  .recommended-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .recommended-grid {
    grid-template-columns: 1fr;
  }
}
</style>