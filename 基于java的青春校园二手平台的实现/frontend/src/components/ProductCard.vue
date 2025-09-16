<template>
  <div class="product-card" @click="$emit('click')">
    <div class="product-image">
      <img :src="product.imageUrl || '/images/default-product.jpg'" :alt="product.title" />
      <div class="product-status" v-if="product.status">
        <el-tag
          :type="getStatusType(product.status)"
          size="mini"
        >
          {{ getStatusText(product.status) }}
        </el-tag>
      </div>
      <div class="product-actions">
        <el-button
          type="primary"
          icon="el-icon-view"
          size="mini"
          circle
          @click.stop="viewProduct"
        />
        <el-button
          type="danger"
          :icon="isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
          size="mini"
          circle
          @click.stop="toggleLike"
        />
      </div>
    </div>
    
    <div class="product-info">
      <h4 class="product-title">{{ product.title }}</h4>
      <p class="product-description">{{ product.description }}</p>
      
      <div class="product-meta">
        <div class="product-price">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ product.price }}</span>
        </div>
        <div class="product-stats">
          <span class="stat-item">
            <i class="el-icon-view"></i>
            {{ product.viewCount || 0 }}
          </span>
          <span class="stat-item">
            <i class="el-icon-star-on"></i>
            {{ product.likeCount || 0 }}
          </span>
        </div>
      </div>
      
      <div class="product-footer">
        <div class="seller-info">
          <el-avatar :size="20" :src="product.sellerAvatar">
            {{ product.sellerName ? product.sellerName.charAt(0) : 'U' }}
          </el-avatar>
          <span class="seller-name">{{ product.sellerName || '匿名用户' }}</span>
        </div>
        <div class="product-time">
          {{ formatTime(product.createTime) }}
        </div>
      </div>
      
      <div class="product-category" v-if="product.categoryName">
        <el-tag size="mini" type="info">{{ product.categoryName }}</el-tag>
      </div>
    </div>
  </div>
</template>

<script>
import { likeProduct, unlikeProduct } from '@/api/product'
import moment from 'moment'

export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isLiked: false
    }
  },
  created() {
    this.isLiked = this.product.isLiked || false
  },
  methods: {
    viewProduct() {
      this.$router.push(`/user/product/${this.product.id}`)
    },
    
    async toggleLike() {
      try {
        if (this.isLiked) {
          const res = await unlikeProduct(this.product.id)
          if (res.data.code === 200) {
            this.isLiked = false
            this.$emit('like-changed', {
              productId: this.product.id,
              isLiked: false,
              likeCount: Math.max(0, (this.product.likeCount || 0) - 1)
            })
            this.$message.success('取消点赞成功')
          }
        } else {
          const res = await likeProduct(this.product.id)
          if (res.data.code === 200) {
            this.isLiked = true
            this.$emit('like-changed', {
              productId: this.product.id,
              isLiked: true,
              likeCount: (this.product.likeCount || 0) + 1
            })
            this.$message.success('点赞成功')
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error)
        this.$message.error('操作失败，请重试')
      }
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
        'APPROVED': '已上架',
        'REJECTED': '已拒绝',
        'SOLD': '已售出',
        'OFFLINE': '已下架'
      }
      return textMap[status] || status
    },
    
    formatTime(time) {
      if (!time) return ''
      return moment(time).fromNow()
    }
  }
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-status {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
}

.product-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-actions {
  opacity: 1;
}

.product-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-description {
  font-size: 13px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 14px;
  color: #e74c3c;
  font-weight: 500;
}

.price-value {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 700;
  margin-left: 2px;
}

.product-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.stat-item i {
  margin-right: 3px;
  font-size: 14px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seller-name {
  font-size: 12px;
  color: #666;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-time {
  font-size: 11px;
  color: #999;
}

.product-category {
  margin-top: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-image {
    height: 150px;
  }
  
  .product-title {
    font-size: 14px;
  }
  
  .product-description {
    font-size: 12px;
  }
  
  .price-value {
    font-size: 18px;
  }
}

/* 加载状态 */
.product-card.loading {
  pointer-events: none;
}

.product-card.loading .product-image {
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-card.loading .product-info {
  background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 50%, #f5f5f5 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>