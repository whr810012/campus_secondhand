<template>
  <div class="product-detail-page">
    <div class="container">
      <!-- 错误状态 -->
      <div v-if="error && !loading" class="error-state">
        <el-result
          icon="error"
          title="加载失败"
          :sub-title="error"
        >
          <template #extra>
            <el-button type="primary" @click="fetchProductDetail">重新加载</el-button>
          </template>
        </el-result>
      </div>

      <div v-else v-loading="loading" class="product-detail">
        <!-- 面包屑导航 -->
        <el-breadcrumb class="breadcrumb" separator=">">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="product.category">
            {{ product.category.name }}
          </el-breadcrumb-item>
          <el-breadcrumb-item>商品详情</el-breadcrumb-item>
        </el-breadcrumb>
        
        <div class="product-content">
          <!-- 左侧图片区域 -->
          <div class="product-images">
            <div class="main-image">
              <el-image
                :src="currentImage || getProductImage(product)"
                :alt="product.title"
                fit="cover"
                :preview-src-list="productImages"
                :initial-index="productImages.indexOf(currentImage)"
                preview-teleported
                loading="lazy"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>图片加载失败</span>
                  </div>
                </template>
              </el-image>
              <div v-if="product.isTop" class="top-badge">置顶</div>
              <div v-if="product.isRecommend" class="recommend-badge">推荐</div>
            </div>
            
            <div v-if="productImages.length > 1" class="image-thumbnails">
              <div
                  v-for="(image, index) in productImages"
                  :key="index"
                  class="thumbnail"
                  :class="{ active: currentImage === image }"
                  @click="currentImage = image"
                >
                  <el-image
                    :src="image"
                    :alt="`商品图片${index + 1}`"
                    fit="cover"
                    loading="lazy"
                  >
                    <template #error>
                      <div class="thumbnail-error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                </div>
            </div>
          </div>
          
          <!-- 右侧商品信息 -->
          <div class="product-info">
            <div class="product-header">
              <h1 class="product-title">{{ product.title }}</h1>
              <div class="product-actions">
                <el-button
                  :icon="isFavorited ? 'StarFilled' : 'Star'"
                  :type="isFavorited ? 'danger' : 'default'"
                  @click="toggleFavorite"
                >
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
                <el-button icon="Share">分享</el-button>
              </div>
            </div>
            
            <div class="price-section">
              <div class="current-price">¥{{ product.price }}</div>
              <div v-if="product.originalPrice" class="original-price">
                原价：¥{{ product.originalPrice }}
              </div>
            </div>
            
            <div class="product-meta">
              <div class="meta-item">
                <span class="label">成色：</span>
                <el-tag :type="getConditionType(product.condition)">
                  {{ getConditionText(product.condition) }}
                </el-tag>
              </div>
              
              <div class="meta-item">
                <span class="label">分类：</span>
                <span class="value">{{ categoryName }}</span>
              </div>
              
              <div class="meta-item">
                <span class="label">发布时间：</span>
                <span class="value">{{ formatDate(product.createdAt) }}</span>
              </div>
              
              <div class="meta-item">
                <span class="label">浏览量：</span>
                <span class="value">{{ product.viewCount }}</span>
              </div>
              
              <div class="meta-item">
                <span class="label">收藏量：</span>
                <span class="value">{{ product.favoriteCount }}</span>
              </div>
            </div>
            
            <div v-if="productTags.length > 0" class="product-tags">
              <span class="label">标签：</span>
              <el-tag
                v-for="tag in productTags"
                :key="tag"
                size="small"
                type="info"
              >
                {{ tag }}
              </el-tag>
            </div>
            
            <!-- 卖家信息 -->
            <div class="seller-info">
              <div class="seller-header">
                <el-avatar :src="product.seller?.avatar" :size="40">
                  {{ product.seller?.nickname?.charAt(0) }}
                </el-avatar>
                <div class="seller-details">
                  <div class="seller-name">{{ product.seller?.nickname }}</div>
                  <div class="seller-stats">
                    <span>信誉：{{ product.seller?.creditScore || 0 }}分</span>
                    <span>成交：{{ product.seller?.dealCount || 0 }}笔</span>
                  </div>
                </div>
              </div>
              
              <div class="seller-actions">
                <el-button type="primary" @click="handleContact">
                  联系卖家
                </el-button>
                <el-button @click="viewSellerProfile">
                  查看主页
                </el-button>
              </div>
            </div>
            
            <!-- 购买操作 -->
            <div v-if="currentStatus === 1" class="purchase-actions">
              <el-button
                type="primary"
                size="large"
                class="buy-button"
                @click="handlePurchase"
              >
                立即购买
              </el-button>
              <el-button
                size="large"
                class="cart-button"
                @click="handleAddToCart"
              >
                加入购物车
              </el-button>
            </div>
            
            <div v-else class="status-info">
              <el-alert
                :title="getStatusText(product.status)"
                :type="getStatusType(product.status)"
                show-icon
                :closable="false"
              />
            </div>
          </div>
        </div>
        
        <!-- 商品详情描述 -->
        <div class="product-description">
          <el-card>
            <template #header>
              <h3>商品详情</h3>
            </template>
            <div class="description-content" v-html="product.description"></div>
          </el-card>
        </div>
        
        <!-- 相关推荐 -->
        <div v-if="relatedProducts.length > 0" class="related-products">
          <el-card>
            <template #header>
              <h3>相关推荐</h3>
            </template>
            <div class="related-grid">
              <div
                v-for="item in relatedProducts"
                :key="item.id"
                class="related-item"
                @click="goToProduct(item.id)"
              >
                <img :src="getProductImage(item.images)" :alt="item.title" />
                <div class="item-info">
                  <h4>{{ item.title }}</h4>
                  <div class="item-price">¥{{ item.price }}</div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
    
    <!-- 联系卖家对话框 -->
    <el-dialog
      v-model="contactDialogVisible"
      title="联系卖家"
      width="500px"
    >
      <el-form :model="contactForm" label-width="80px">
        <el-form-item label="留言内容">
          <el-input
            v-model="contactForm.message"
            type="textarea"
            :rows="4"
            placeholder="请输入您想对卖家说的话..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendContactMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getProductDetail, getRelatedProducts } from '@/api/product'
import { addToFavorites, removeFromFavorites } from '@/api/favorite'
import { sendMessage } from '@/api/message'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const product = ref({})
const relatedProducts = ref([])
const currentImage = ref('')
const imageLoading = ref(false)
const error = ref(null)
const isFavorited = ref(false)
const contactDialogVisible = ref(false)

const contactForm = reactive({
  message: ''
})

// 计算属性：获取商品图片列表
const productImages = computed(() => {
  return product.value?.imageList || product.value?.images || []
})

// 计算属性：获取当前商品状态数值
const currentStatus = computed(() => {
  return getStatusFromString(product.value?.status)
})

// 计算属性：获取商品标签
const productTags = computed(() => {
  return product.value?.tagList || product.value?.tags || []
})

// 计算属性：获取商品分类名称
const categoryName = computed(() => {
  return product.value?.category?.name || '未分类'
})

// 获取商品详情
const fetchProductDetail = async () => {
  loading.value = true
  error.value = null
  try {
    const productId = route.params.id
    
    if (!productId) {
      throw new Error('商品ID不存在')
    }
    
    const response = await getProductDetail(productId)
    
    if (!response) {
      throw new Error('获取商品详情失败')
    }
    
    // 检查响应状态，200表示成功
    if (response.code && response.code !== 200) {
      throw new Error(response.message || '获取商品详情失败')
    }
    
    // 根据API返回的数据结构进行数据映射
    const rawData = response.data
    
    if (!rawData) {
      throw new Error('商品数据不存在')
    }
    
    product.value = {
      ...rawData,
      // 映射图片数据
      images: rawData.imageList || rawData.images || [],
      // 映射分类信息
      category: rawData.category || { name: '未分类' },
      // 映射卖家信息
      seller: rawData.seller || {
        id: rawData.sellerId,
        nickname: '卖家',
        avatar: '',
        creditScore: 0,
        dealCount: 0
      },
      // 映射标签
      tags: rawData.tagList || rawData.tags || [],
      // 确保状态为数字类型
      status: getStatusFromString(rawData.status)
    }
    
    // 设置当前显示的图片
    const images = rawData.imageList || rawData.images || []
    if (images.length > 0) {
      currentImage.value = images[0]
    }
    
    // 获取相关推荐
    fetchRelatedProducts()
  } catch (err) {
    console.error('获取商品详情失败:', err)
    error.value = err.message || '获取商品详情失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 获取相关推荐
const fetchRelatedProducts = async () => {
  try {
    const response = await getRelatedProducts(product.value.id)
    relatedProducts.value = response.data || []
  } catch (error) {
    console.error('获取相关推荐失败:', error)
  }
}

// 获取商品图片
const getProductImage = (images) => {
  // 处理不同的图片数据结构
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

// 获取成色类型
const getConditionType = (condition) => {
  if (condition <= 2) return 'success'
  if (condition <= 3) return 'warning'
  return 'danger'
}

// 将字符串状态转换为数字状态
const getStatusFromString = (status) => {
  const statusMap = {
    'pending': 0,
    'active': 1,
    'sold': 2,
    'inactive': 3,
    'rejected': 4
  }
  return typeof status === 'string' ? statusMap[status.toLowerCase()] ?? 0 : status
}

// 获取状态文本
const getStatusText = (status) => {
  const numStatus = typeof status === 'string' ? getStatusFromString(status) : status
  const statusMap = {
    0: '审核中',
    1: '在售',
    2: '已售出',
    3: '已下架',
    4: '审核不通过'
  }
  return statusMap[numStatus] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  const numStatus = typeof status === 'string' ? getStatusFromString(status) : status
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'error'
  }
  return typeMap[numStatus] || 'info'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      await removeFromFavorites(product.value.id)
      isFavorited.value = false
      product.value.favoriteCount--
      ElMessage.success('取消收藏成功')
    } else {
      await addToFavorites(product.value.id)
      isFavorited.value = true
      product.value.favoriteCount++
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 联系卖家
const handleContact = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (product.value.seller?.id === userStore.userInfo?.id) {
    ElMessage.warning('不能联系自己')
    return
  }
  
  contactDialogVisible.value = true
}

// 发送消息
const sendContactMessage = async () => {
  if (!contactForm.message.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  try {
    const messageData = {
      senderId: userStore.userInfo.id,
      receiverId: product.value.seller.id,
      productId: product.value.id,
      content: contactForm.message,
      type: 'text'
    }
    
    await sendMessage(messageData)
    ElMessage.success('消息发送成功')
    contactDialogVisible.value = false
    contactForm.message = ''
    
    // 询问是否跳转到聊天页面
    ElMessageBox.confirm(
      '消息发送成功，是否前往聊天页面继续对话？',
      '提示',
      {
        confirmButtonText: '前往聊天',
        cancelButtonText: '稍后再说',
        type: 'success'
      }
    ).then(() => {
      router.push('/chat')
    }).catch(() => {
      // 用户选择稍后再说，不做任何操作
    })
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

// 查看卖家主页
const viewSellerProfile = () => {
  router.push(`/user/${product.value.seller?.id}`)
}

// 立即购买
const handlePurchase = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (product.value.seller?.id === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  
  // TODO: 跳转到订单确认页面
  router.push(`/order/confirm?productId=${product.value.id}`)
}

// 加入购物车
const handleAddToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (product.value.seller?.id === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己的商品')
    return
  }
  
  // TODO: 调用加入购物车API
  ElMessage.success('已加入购物车')
}

// 跳转到其他商品
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 初始化
onMounted(() => {
  fetchProductDetail()
})
</script>

<style lang="scss" scoped>
.product-detail-page {
  .product-detail {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  .breadcrumb {
    margin-bottom: 20px;
  }
  
  .product-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 40px;
    margin-bottom: 40px;
    
    .product-images {
      .main-image {
        position: relative;
        margin-bottom: 16px;
        border-radius: 12px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 400px;
          object-fit: cover;
          transition: transform 0.3s ease;
          cursor: zoom-in;
          
          &:hover {
            transform: scale(1.02);
          }
        }
        
        .top-badge,
        .recommend-badge {
          position: absolute;
          top: 12px;
          padding: 6px 12px;
          border-radius: 6px;
          font-size: 12px;
          color: white;
          font-weight: 500;
        }
        
        .top-badge {
          right: 12px;
          background: var(--warning-color);
        }
        
        .recommend-badge {
          left: 12px;
          background: var(--success-color);
        }
      }
      
      .image-thumbnails {
        display: flex;
        gap: 8px;
        overflow-x: auto;
        
        .thumbnail {
          flex-shrink: 0;
          width: 80px;
          height: 80px;
          border-radius: 8px;
          overflow: hidden;
          cursor: pointer;
          border: 2px solid transparent;
          transition: border-color 0.3s ease;
          
          &.active {
            border-color: var(--primary-color);
          }
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }
    }
    
    .product-info {
      .product-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 20px;
        
        .product-title {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-primary);
          line-height: 1.4;
          flex: 1;
          margin-right: 20px;
        }
        
        .product-actions {
          display: flex;
          gap: 8px;
        }
      }
      
      .price-section {
        margin-bottom: 24px;
        
        .current-price {
          font-size: 32px;
          font-weight: 600;
          color: var(--danger-color);
          margin-bottom: 8px;
        }
        
        .original-price {
          font-size: 16px;
          color: var(--text-placeholder);
          text-decoration: line-through;
        }
      }
      
      .product-meta {
        margin-bottom: 20px;
        
        .meta-item {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          
          .label {
            width: 80px;
            color: var(--text-secondary);
            font-size: 14px;
          }
          
          .value {
            color: var(--text-primary);
            font-size: 14px;
          }
        }
      }
      
      .product-tags {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 24px;
        
        .label {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .seller-info {
        background: var(--bg-secondary);
        padding: 20px;
        border-radius: 12px;
        margin-bottom: 24px;
        
        .seller-header {
          display: flex;
          align-items: center;
          margin-bottom: 16px;
          
          .seller-details {
            margin-left: 12px;
            flex: 1;
            
            .seller-name {
              font-size: 16px;
              font-weight: 500;
              color: var(--text-primary);
              margin-bottom: 4px;
            }
            
            .seller-stats {
              display: flex;
              gap: 16px;
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
        
        .seller-actions {
          display: flex;
          gap: 12px;
        }
      }
      
      .purchase-actions {
        display: flex;
        gap: 12px;
        
        .buy-button,
        .cart-button {
          flex: 1;
        }
      }
      
      .status-info {
        margin-top: 20px;
      }
    }
  }
  
  .product-description {
    margin-bottom: 40px;
    
    .description-content {
      line-height: 1.6;
      color: var(--text-primary);
      
      :deep(img) {
        max-width: 100%;
        height: auto;
        border-radius: 8px;
        margin: 16px 0;
      }
    }
  }
  
  .related-products {
    .related-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;
      
      .related-item {
        cursor: pointer;
        border-radius: 8px;
        overflow: hidden;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
        }
        
        img {
          width: 100%;
          height: 150px;
          object-fit: cover;
        }
        
        .item-info {
          padding: 12px;
          
          h4 {
            font-size: 14px;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .item-price {
            font-size: 16px;
            font-weight: 600;
            color: var(--danger-color);
          }
        }
        
        .image-error {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 100%;
          color: #909399;
          background: #f5f7fa;
          
          .el-icon {
            font-size: 48px;
            margin-bottom: 8px;
          }
          
          span {
            font-size: 14px;
          }
        }
      }
      
      .image-thumbnails {
        .thumbnail {
          .thumbnail-error {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            color: #c0c4cc;
            background: #f5f7fa;
            
            .el-icon {
              font-size: 20px;
            }
          }
        }
      }
    }
    
    .error-state {
      margin: 40px 0;
      text-align: center;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-detail-page {
    .product-detail {
      padding: 15px;
      margin: 10px;
      border-radius: 6px;
    }
    
    .product-content {
      grid-template-columns: 1fr;
      gap: 20px;
      
      .product-images {
        .main-image {
          img {
            height: 300px;
          }
        }
      }
      
      .product-info {
        .product-header {
          flex-direction: column;
          align-items: flex-start;
          gap: 16px;
          
          .product-title {
            margin-right: 0;
          }
        }
        
        .seller-info {
          .seller-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;
          }
          
          .seller-actions {
            width: 100%;
            
            .el-button {
              flex: 1;
            }
          }
        }
        
        .purchase-actions {
          flex-direction: column;
        }
      }
    }
    
    .related-products {
      .related-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      }
    }
  }
}
</style>