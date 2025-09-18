<template>
  <div class="home-page">
    <div class="container">
      <!-- 轮播图 -->
      <div class="banner-section">
        <el-carousel height="300px" class="banner-carousel">
          <el-carousel-item v-for="(banner, index) in banners" :key="index">
            <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }">
              <div class="banner-content">
                <h2>{{ banner.title }}</h2>
                <p>{{ banner.description }}</p>
                <el-button type="primary" size="large">{{ banner.buttonText }}</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      
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
      
      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <div class="filter-bar">
          <div class="filter-left">
            <el-select v-model="filters.categoryId" placeholder="选择分类" clearable @change="handleFilterChange">
              <el-option label="全部分类" :value="null" />
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
            
            <el-select v-model="filters.condition" placeholder="商品成色" clearable @change="handleFilterChange">
              <el-option label="全部成色" :value="null" />
              <el-option label="全新" :value="1" />
              <el-option label="几乎全新" :value="2" />
              <el-option label="轻微使用痕迹" :value="3" />
              <el-option label="明显使用痕迹" :value="4" />
              <el-option label="重度使用痕迹" :value="5" />
            </el-select>
            
            <div class="price-filter">
              <el-input
                v-model="filters.minPrice"
                placeholder="最低价格"
                type="number"
                @change="handleFilterChange"
              />
              <span class="price-separator">-</span>
              <el-input
                v-model="filters.maxPrice"
                placeholder="最高价格"
                type="number"
                @change="handleFilterChange"
              />
            </div>
          </div>
          
          <div class="filter-right">
            <el-select v-model="filters.sortBy" @change="handleFilterChange">
              <el-option label="最新发布" value="latest" />
              <el-option label="价格从低到高" value="price_asc" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="浏览量最多" value="view_count" />
            </el-select>
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
                <div class="seller-info">
                  <el-avatar :src="product.seller?.avatar" :size="24">
                    {{ product.seller?.nickname?.charAt(0) }}
                  </el-avatar>
                  <span class="seller-name">{{ product.seller?.nickname }}</span>
                </div>
                
                <div class="product-tags">
                  <el-tag
                    v-for="tag in product.tags?.slice(0, 2)"
                    :key="tag"
                    size="small"
                    type="info"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProducts, getCategories } from '@/api/product'
import { useUserStore } from '@/stores/user'
import { Setting, View, Star, Box } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const categories = ref([])

// 轮播图数据
const banners = ref([
  {
    title: '欢迎来到校园二手交易平台',
    description: '安全、便捷的校园二手商品交易服务',
    buttonText: '立即体验',
    image: 'https://via.placeholder.com/1200x300/409eff/ffffff?text=校园二手交易平台'
  },
  {
    title: '学生身份认证',
    description: '确保平台用户均为在校学生，交易更安全',
    buttonText: '了解更多',
    image: 'https://via.placeholder.com/1200x300/67c23a/ffffff?text=学生身份认证'
  },
  {
    title: '信誉评价体系',
    description: '完善的评价系统，让每一次交易都有保障',
    buttonText: '查看详情',
    image: 'https://via.placeholder.com/1200x300/e6a23c/ffffff?text=信誉评价体系'
  }
])

// 筛选条件
const filters = reactive({
  keyword: '',
  categoryId: null,
  condition: null,
  minPrice: '',
  maxPrice: '',
  sortBy: 'latest'
})

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
      size: pagination.size,
      ...filters
    }
    
    // 清除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
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

// 处理筛选条件变化
const handleFilterChange = () => {
  pagination.page = 1
  fetchProducts()
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

// 监听路由查询参数变化
watch(
  () => route.query,
  (newQuery) => {
    if (newQuery.keyword) {
      filters.keyword = newQuery.keyword
    }
    if (newQuery.categoryId) {
      filters.categoryId = parseInt(newQuery.categoryId)
    }
    fetchProducts()
  },
  { immediate: true }
)

// 初始化
onMounted(async () => {
  await fetchCategories()
  
  // 从路由参数中获取初始筛选条件
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
  if (route.query.categoryId) {
    filters.categoryId = parseInt(route.query.categoryId)
  }
  
  await fetchProducts()
})
</script>

<style lang="scss" scoped>
.home-page {
  .banner-section {
    margin-bottom: 30px;
    
    .banner-carousel {
      border-radius: 12px;
      overflow: hidden;
      
      .banner-item {
        height: 100%;
        background-size: cover;
        background-position: center;
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.3);
        }
        
        .banner-content {
          text-align: center;
          color: white;
          position: relative;
          z-index: 1;
          
          h2 {
            font-size: 32px;
            margin-bottom: 16px;
            font-weight: 600;
          }
          
          p {
            font-size: 18px;
            margin-bottom: 24px;
            opacity: 0.9;
          }
        }
      }
    }
  }
  
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
  
  .filter-section {
    margin-bottom: 30px;
    
    .filter-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: white;
      padding: 20px;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      .filter-left {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .price-filter {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .el-input {
            width: 120px;
          }
          
          .price-separator {
            color: var(--text-secondary);
          }
        }
      }
      
      .filter-right {
        .el-select {
          width: 150px;
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
    .banner-section {
      .banner-carousel {
        height: 200px;
        
        .banner-content {
          h2 {
            font-size: 24px;
          }
          
          p {
            font-size: 16px;
          }
        }
      }
    }
    
    .filter-section {
      .filter-bar {
        flex-direction: column;
        gap: 16px;
        
        .filter-left {
          flex-wrap: wrap;
          justify-content: center;
        }
      }
    }
    
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