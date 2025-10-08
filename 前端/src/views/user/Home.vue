<template>
  <div class="home-page">
    <div class="container">

      
      <!-- 管理员入口 -->
      <div v-if="userStore.isAdmin" class="admin-entrance">
        <el-card class="admin-card">
          <div class="admin-content">
            <div class="admin-info">
              <el-icon class="admin-icon"><Setting /></el-icon>
              <div class="admin-text">
                <h3>管理员专区</h3>
                <p>进入管理后台，管理平台内容和用户</p>
              </div>
            </div>
            <el-button type="primary" size="large" @click="goToAdmin">
              进入管理后台
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 公告展示区域 -->
      <div v-if="announcements.length > 0" class="announcements-section">
        <div class="section-header">
          <h3>
            <el-icon><Bell /></el-icon>
            系统公告
          </h3>
          <div class="carousel-controls">
          <span class="carousel-indicator">{{ currentSlide + 1 }} / {{ announcements.length }}</span>
          <div class="carousel-nav">
            <el-button 
              :icon="ArrowLeft" 
              size="small" 
              circle 
              @click="prevSlide"
              :disabled="announcements.length <= 1"
            />
            <el-button 
              :icon="ArrowRight" 
              size="small" 
              circle 
              @click="nextSlide"
              :disabled="announcements.length <= 1"
            />
          </div>
        </div>
        </div>
        
        <div class="announcements-carousel">
          <div class="carousel-container" 
               @mouseenter="stopAutoSlide" 
               @mouseleave="startAutoSlide">
            <div class="announcement-slide"
                 v-for="(announcement, index) in announcements"
                 :key="announcement.id"
                 :class="{ 
                   urgent: announcement.type === 'urgent',
                   active: index === currentSlide 
                 }"
                 @click="showAnnouncementDetail(announcement)"
            >
              <div class="announcement-header">
                <div class="announcement-title">
                  <el-icon v-if="announcement.type === 'urgent'" class="urgent-icon"><Warning /></el-icon>
                  <span>{{ announcement.title }}</span>
                  <el-tag :type="getAnnouncementTypeTag(announcement.type)" size="small">
                    {{ getAnnouncementTypeLabel(announcement.type) }}
                  </el-tag>
                </div>
                <div class="announcement-time">
                  {{ formatDate(announcement.publishTime || announcement.createdAt) }}
                </div>
              </div>
              <div class="announcement-content">
                {{ announcement.content }}
              </div>
            </div>
          </div>
          
          <!-- 轮播指示器 -->
          <div v-if="announcements.length > 1" class="carousel-dots">
            <span
              v-for="(announcement, index) in announcements"
              :key="`dot-${announcement.id}`"
              class="dot"
              :class="{ active: index === currentSlide }"
              @click="goToSlide(index)"
            ></span>
          </div>
        </div>
      </div>
      

      
      <!-- 商品列表 -->
      <div class="products-section">
        <div class="section-header">
          <h3>商品列表</h3>
          <span class="product-count">共 {{ pagination.total }} 件商品</span>
        </div>
        
        <div v-loading="loading" class="products-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goToProductDetail(product.id)"
          >
            <div class="product-image">
              <img :src="getProductImage(product.images)" :alt="product.title" />
              <div v-if="product.isTop" class="top-badge">置顶</div>
              <div v-if="product.isRecommend" class="recommend-badge">推荐</div>
            </div>
            
            <div class="product-info">
              <h4 class="product-title">{{ product.title }}</h4>
              <p class="product-description">{{ product.description }}</p>
              
              <div class="product-meta">
                <div class="price-info">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                </div>
                
                <div class="product-stats">
                  <span class="view-count">
                    <el-icon><View /></el-icon>
                    {{ product.viewCount }}
                  </span>
                  <span class="favorite-count">
                    <el-icon><Star /></el-icon>
                    {{ product.favoriteCount }}
                  </span>
                </div>
              </div>
              
              <div class="product-footer">
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="!loading && products.length === 0" class="empty-container">
          <el-icon class="empty-icon"><Box /></el-icon>
          <p class="empty-text">暂无商品</p>
        </div>
        
        <!-- 分页 -->
        <div v-if="pagination.total > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="showAnnouncementDialog"
      title="公告详情"
      width="600px"
    >
      <div v-if="selectedAnnouncement" class="announcement-detail">
        <div class="detail-header">
          <h2 class="detail-title">{{ selectedAnnouncement.title }}</h2>
          <div class="detail-meta">
            <el-tag :type="getAnnouncementTypeTag(selectedAnnouncement.type)" size="small">
              {{ getAnnouncementTypeLabel(selectedAnnouncement.type) }}
            </el-tag>
            <span class="detail-time">
              {{ formatDate(selectedAnnouncement.publishTime || selectedAnnouncement.createdAt) }}
            </span>
          </div>
        </div>
        <div class="detail-content">
          {{ selectedAnnouncement.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElDialog } from 'element-plus'
import { Setting, View, Star, Box, Bell, Warning, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getCategories, getProducts } from '@/api/product'
import { getPublishedAnnouncements, getPublishedAnnouncementById } from '@/api/announcement'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const announcements = ref([])
const showAnnouncementDialog = ref(false)
const selectedAnnouncement = ref(null)
const currentAnnouncementIndex = ref(0)
const autoPlayTimer = ref(null)
const currentSlide = ref(0)
const slideInterval = ref(null)



// 分页信息
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await getProducts(params)
    products.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data.filter(item => item.parentId === 0)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const response = await getPublishedAnnouncements({
      current: 1,
      size: 5
    })
    if (response.code === 200) {
      announcements.value = response.data.records || []
      // 如果有公告数据，启动自动轮播
      if (announcements.value.length > 1) {
        startAutoSlide()
      }
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
  }
}

// 轮播图控制方法
const nextSlide = () => {
  if (announcements.value.length > 0) {
    currentSlide.value = (currentSlide.value + 1) % announcements.value.length
  }
}

const prevSlide = () => {
  if (announcements.value.length > 0) {
    currentSlide.value = currentSlide.value === 0 
      ? announcements.value.length - 1 
      : currentSlide.value - 1
  }
}

const goToSlide = (index) => {
  currentSlide.value = index
  // 重新启动自动轮播
  stopAutoSlide()
  startAutoSlide()
}

// 自动轮播控制
const startAutoSlide = () => {
  if (announcements.value.length > 1) {
    slideInterval.value = setInterval(() => {
      nextSlide()
    }, 4000) // 每4秒切换一次
  }
}

const stopAutoSlide = () => {
  if (slideInterval.value) {
    clearInterval(slideInterval.value)
    slideInterval.value = null
  }
}

// 获取商品图片
const getProductImage = (images) => {
  if (images && images.length > 0) {
    return images[0]
  }
  return 'https://via.placeholder.com/300x200/f5f5f5/cccccc?text=暂无图片'
}

// 跳转到商品详情
const goToProductDetail = (productId) => {
  router.push(`/product/${productId}`)
}

// 跳转到管理后台
const goToAdmin = () => {
  router.push('/admin/dashboard')
}

// 显示公告详情
const showAnnouncementDetail = (announcement) => {
  selectedAnnouncement.value = announcement
  showAnnouncementDialog.value = true
}

// 获取公告类型标签
const getAnnouncementTypeTag = (type) => {
  const types = {
    normal: 'info',
    important: 'warning',
    urgent: 'danger'
  }
  return types[type] || 'info'
}

// 获取公告类型标签文本
const getAnnouncementTypeLabel = (type) => {
  const labels = {
    normal: '普通',
    important: '重要',
    urgent: '紧急'
  }
  return labels[type] || type
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 轮播图控制函数
const nextAnnouncement = () => {
  if (announcements.value.length > 1) {
    currentAnnouncementIndex.value = (currentAnnouncementIndex.value + 1) % announcements.value.length
  }
}

const prevAnnouncement = () => {
  if (announcements.value.length > 1) {
    currentAnnouncementIndex.value = currentAnnouncementIndex.value === 0 
      ? announcements.value.length - 1 
      : currentAnnouncementIndex.value - 1
  }
}

const goToAnnouncement = (index) => {
  currentAnnouncementIndex.value = index
  resetAutoPlay()
}

// 自动播放控制
const startAutoPlay = () => {
  if (announcements.value.length > 1) {
    autoPlayTimer.value = setInterval(() => {
      nextAnnouncement()
    }, 5000) // 5秒自动切换
  }
}

const stopAutoPlay = () => {
  if (autoPlayTimer.value) {
    clearInterval(autoPlayTimer.value)
    autoPlayTimer.value = null
  }
}

const resetAutoPlay = () => {
  stopAutoPlay()
  startAutoPlay()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchProducts()
}

// 处理每页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchProducts()
}

// 初始化
onMounted(async () => {
  await fetchCategories()
  await fetchAnnouncements()
  await fetchProducts()
  
  // 启动自动播放
  startAutoPlay()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopAutoPlay()
  stopAutoSlide()
})
</script>

<style lang="scss" scoped>
.home-page {

  
  .admin-entrance {
    margin: 20px 0;
    
    .admin-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 12px;
      
      :deep(.el-card__body) {
        padding: 24px;
      }
      
      .admin-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .admin-info {
          display: flex;
          align-items: center;
          gap: 16px;
          
          .admin-icon {
            font-size: 32px;
            color: white;
            background: rgba(255, 255, 255, 0.2);
            padding: 12px;
            border-radius: 50%;
          }
          
          .admin-text {
            h3 {
              color: white;
              font-size: 18px;
              font-weight: 600;
              margin: 0 0 4px 0;
            }
            
            p {
              color: rgba(255, 255, 255, 0.8);
              font-size: 14px;
              margin: 0;
            }
          }
        }
        
        .el-button {
          background: white;
          color: #667eea;
          border: none;
          font-weight: 600;
          
          &:hover {
            background: rgba(255, 255, 255, 0.9);
            color: #667eea;
          }
        }
      }
    }
  }
  
  // 公告轮播图样式
  .announcements-section {
    margin: 20px 0;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    
    .section-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        color: white;
        font-size: 16px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 8px;
        
        .el-icon {
          font-size: 18px;
        }
      }
      
      .carousel-controls {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .el-button {
          background: rgba(255, 255, 255, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.3);
          color: white;
          
          &:hover:not(:disabled) {
            background: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.5);
          }
          
          &:disabled {
            background: rgba(255, 255, 255, 0.1);
            border-color: rgba(255, 255, 255, 0.1);
            color: rgba(255, 255, 255, 0.5);
          }
        }
        
        .carousel-indicator {
          color: white;
          font-size: 12px;
          font-weight: 500;
          min-width: 40px;
          text-align: center;
        }
      }
    }
    
    .announcements-carousel {
      position: relative;
      overflow: hidden;
      
      .carousel-container {
        position: relative;
        width: 100%;
        min-height: 120px; /* 设置最小高度确保容器有空间 */
        
        .announcement-slide {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          padding: 20px;
          cursor: pointer;
          transition: opacity 0.5s cubic-bezier(0.4, 0, 0.2, 1), visibility 0.5s cubic-bezier(0.4, 0, 0.2, 1);
          opacity: 0;
          visibility: hidden;
          box-sizing: border-box;
          
          &.active {
            opacity: 1;
            visibility: visible;
            z-index: 1;
          }
          
          &:hover {
            background: linear-gradient(90deg, #f8f9ff 0%, #ffffff 100%);
            
            &::before {
              opacity: 1;
            }
          }
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 4px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            opacity: 0;
            transition: opacity 0.3s ease;
          }
          
          // 紧急公告特殊样式
          &.urgent {
            background: linear-gradient(90deg, #fef0f0 0%, #ffffff 100%);
            
            &::before {
              background: #f56c6c;
              opacity: 1;
            }
            
            &:hover {
              background: linear-gradient(90deg, #fde2e2 0%, #ffffff 100%);
            }
            
            .urgent-icon {
              color: #f56c6c;
              animation: pulse 2s infinite;
            }
          }
          
          .announcement-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 12px;
            gap: 12px;
            
            .announcement-title {
              flex: 1;
              display: flex;
              align-items: center;
              gap: 8px;
              
              span {
                font-size: 16px;
                font-weight: 600;
                color: #303133;
                line-height: 1.4;
              }
              
              .el-tag {
                flex-shrink: 0;
              }
            }
            
            .announcement-time {
              font-size: 12px;
              color: #909399;
              white-space: nowrap;
              flex-shrink: 0;
            }
          }
          
          .announcement-content {
            font-size: 14px;
            color: #606266;
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
      
      .carousel-dots {
        display: flex;
        justify-content: center;
        gap: 8px;
        padding: 16px 20px 20px;
        
        .dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: #dcdfe6;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &:hover {
            background: #c0c4cc;
            transform: scale(1.2);
          }
          
          &.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            transform: scale(1.3);
          }
        }
      }
    }
  }

  .products-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
      }
      
      .product-count {
        color: var(--text-secondary);
        font-size: 14px;
      }
    }
    
    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
      
      .product-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        cursor: pointer;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }
        
        .product-image {
          position: relative;
          height: 200px;
          overflow: hidden;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s ease;
          }
          
          &:hover img {
            transform: scale(1.05);
          }
          
          .top-badge,
          .recommend-badge {
            position: absolute;
            top: 8px;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            color: white;
            font-weight: 500;
          }
          
          .top-badge {
            right: 8px;
            background: var(--warning-color);
          }
          
          .recommend-badge {
            left: 8px;
            background: var(--success-color);
          }
        }
        
        .product-info {
          padding: 16px;
          
          .product-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .product-description {
            font-size: 14px;
            color: var(--text-secondary);
            margin-bottom: 12px;
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
            
            .price-info {
              .current-price {
                font-size: 18px;
                font-weight: 600;
                color: var(--danger-color);
              }
              
              .original-price {
                font-size: 14px;
                color: var(--text-placeholder);
                text-decoration: line-through;
                margin-left: 8px;
              }
            }
            
            .product-stats {
              display: flex;
              gap: 12px;
              font-size: 12px;
              color: var(--text-secondary);
              
              span {
                display: flex;
                align-items: center;
                gap: 4px;
              }
            }
          }
          
          .product-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            .seller-info {
              display: flex;
              align-items: center;
              gap: 8px;
              
              .seller-name {
                font-size: 12px;
                color: var(--text-secondary);
              }
            }
            
            .product-tags {
              display: flex;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .home-page {
    .products-section {
      .products-grid {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 16px;
      }
    }
  }
}

@media (max-width: 480px) {
  .home-page {
    .products-section {
      .products-grid {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>



/* 公告详情对话框样式 */
.announcement-detail {
  padding: 10px 0;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-time {
  font-size: 13px;
  color: #909399;
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* 响应式优化 */
@media (max-width: 768px) {
  .announcements-section {
    margin: 16px 0;
    border-radius: 8px;
    
    .section-header {
      padding: 12px 16px;
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;
      
      h3 {
        font-size: 15px;
      }
      
      .carousel-controls {
        align-self: flex-end;
        gap: 8px;
        
        .carousel-indicator {
          font-size: 11px;
          min-width: 35px;
        }
      }
    }
    
    .announcements-carousel {
      .carousel-container {
        .announcement-slide {
          padding: 16px;
          
          .announcement-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
            
            .announcement-title {
              span {
                font-size: 14px;
              }
            }
            
            .announcement-time {
              align-self: flex-end;
            }
          }
          
          .announcement-content {
            font-size: 13px;
            -webkit-line-clamp: 2;
          }
        }
      }
      
      .carousel-dots {
        padding: 12px 16px 16px;
        
        .dot {
          width: 6px;
          height: 6px;
        }
      }
    }
  }
}