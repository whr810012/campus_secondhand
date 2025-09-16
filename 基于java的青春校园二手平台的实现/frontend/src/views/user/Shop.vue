<template>
  <div class="shop-container">
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索商品名称、描述..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleSearch"
          ></el-button>
        </el-input>
      </div>
      
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">分类：</span>
          <el-select
            v-model="searchForm.categoryId"
            placeholder="全部分类"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </div>
        
        <div class="filter-item">
          <span class="filter-label">价格：</span>
          <el-input
            v-model="searchForm.minPrice"
            placeholder="最低价"
            size="small"
            style="width: 80px"
            @change="handleSearch"
          />
          <span style="margin: 0 5px">-</span>
          <el-input
            v-model="searchForm.maxPrice"
            placeholder="最高价"
            size="small"
            style="width: 80px"
            @change="handleSearch"
          />
        </div>
        
        <div class="filter-item">
          <span class="filter-label">排序：</span>
          <el-select
            v-model="searchForm.sortBy"
            placeholder="默认排序"
            @change="handleSearch"
          >
            <el-option label="默认排序" value="" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="最新发布" value="time_desc" />
            <el-option label="浏览最多" value="view_desc" />
            <el-option label="点赞最多" value="like_desc" />
          </el-select>
        </div>
        
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="products-section">
      <div class="section-header">
        <div class="result-info">
          <span>共找到 {{ pagination.total }} 件商品</span>
        </div>
        <div class="view-mode">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="grid">
              <i class="el-icon-menu"></i>
            </el-radio-button>
            <el-radio-button label="list">
              <i class="el-icon-s-unfold"></i>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
      
      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="products-grid">
        <product-card
          v-for="product in products"
          :key="product.id"
          :product="product"
          @click="goToProduct(product.id)"
        />
      </div>
      
      <!-- 列表视图 -->
      <div v-else class="products-list">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-list-item"
          @click="goToProduct(product.id)"
        >
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
                <el-avatar :size="24" :src="product.sellerAvatar">
                  {{ product.sellerName ? product.sellerName.charAt(0) : 'U' }}
                </el-avatar>
                <span class="seller-name">{{ product.sellerName || '匿名用户' }}</span>
              </div>
              
              <div class="product-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click.stop="goToProduct(product.id)"
                >
                  查看详情
                </el-button>
                <el-button
                  :type="product.isLiked ? 'danger' : 'default'"
                  :icon="product.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
                  size="small"
                  @click.stop="toggleLike(product)"
                >
                  {{ product.likeCount || 0 }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && products.length === 0" class="empty-state">
        <el-empty description="暂无商品">
          <el-button type="primary" @click="resetSearch">重新搜索</el-button>
        </el-empty>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-section" v-if="pagination.total > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[12, 24, 48, 96]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>
  </div>
</template>

<script>
import ProductCard from '@/components/ProductCard.vue'
import { getProductList, likeProduct, unlikeProduct } from '@/api/product'
import { getEnabledCategories } from '@/api/category'
import { mapGetters } from 'vuex'

export default {
  name: 'Shop',
  components: {
    ProductCard
  },
  data() {
    return {
      loading: false,
      viewMode: 'grid',
      searchForm: {
        keyword: '',
        categoryId: null,
        minPrice: '',
        maxPrice: '',
        sortBy: ''
      },
      products: [],
      categories: [],
      pagination: {
        current: 1,
        size: 24,
        total: 0
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.initFromQuery()
    this.loadCategories()
    this.loadProducts()
  },
  watch: {
    '$route.query': {
      handler() {
        this.initFromQuery()
        this.loadProducts()
      },
      deep: true
    }
  },
  methods: {
    initFromQuery() {
      const query = this.$route.query
      this.searchForm.keyword = query.keyword || ''
      this.searchForm.categoryId = query.categoryId ? parseInt(query.categoryId) : null
      this.searchForm.minPrice = query.minPrice || ''
      this.searchForm.maxPrice = query.maxPrice || ''
      this.searchForm.sortBy = query.sortBy || ''
      this.pagination.current = query.page ? parseInt(query.page) : 1
      this.pagination.size = query.size ? parseInt(query.size) : 24
    },
    
    async loadCategories() {
      try {
        const res = await getEnabledCategories()
        if (res.data.code === 200) {
          this.categories = res.data.data
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
    async loadProducts() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        
        // 清理空值
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        
        const res = await getProductList(params)
        if (res.data.code === 200) {
          this.products = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        this.$message.error('加载商品失败')
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.updateQuery()
      this.loadProducts()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        categoryId: null,
        minPrice: '',
        maxPrice: '',
        sortBy: ''
      }
      this.pagination.current = 1
      this.updateQuery()
      this.loadProducts()
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.updateQuery()
      this.loadProducts()
    },
    
    handleCurrentChange(page) {
      this.pagination.current = page
      this.updateQuery()
      this.loadProducts()
    },
    
    updateQuery() {
      const query = {
        ...this.searchForm,
        page: this.pagination.current,
        size: this.pagination.size
      }
      
      // 清理空值
      Object.keys(query).forEach(key => {
        if (query[key] === '' || query[key] === null) {
          delete query[key]
        }
      })
      
      this.$router.replace({ query })
    },
    
    goToProduct(productId) {
      this.$router.push(`/user/product/${productId}`)
    },
    
    async toggleLike(product) {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        if (product.isLiked) {
          const res = await unlikeProduct(product.id)
          if (res.data.code === 200) {
            product.isLiked = false
            product.likeCount = Math.max(0, (product.likeCount || 0) - 1)
            this.$message.success('取消点赞成功')
          }
        } else {
          const res = await likeProduct(product.id)
          if (res.data.code === 200) {
            product.isLiked = true
            product.likeCount = (product.likeCount || 0) + 1
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
    }
  }
}
</script>

<style scoped>
.shop-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索筛选区域 */
.search-filter-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar {
  margin-bottom: 15px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

/* 商品区域 */
.products-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.result-info {
  font-size: 14px;
  color: #666;
}

/* 网格视图 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

/* 列表视图 */
.products-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-list-item {
  display: flex;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-list-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.product-list-item .product-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 15px;
  position: relative;
  flex-shrink: 0;
}

.product-list-item .product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-list-item .product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-list-item .product-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.product-list-item .product-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-list-item .product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.product-list-item .product-price {
  display: flex;
  align-items: baseline;
}

.product-list-item .price-symbol {
  font-size: 16px;
  color: #e74c3c;
  font-weight: 500;
}

.product-list-item .price-value {
  font-size: 24px;
  color: #e74c3c;
  font-weight: 700;
  margin-left: 2px;
}

.product-list-item .product-stats {
  display: flex;
  gap: 15px;
}

.product-list-item .stat-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.product-list-item .stat-item i {
  margin-right: 4px;
  font-size: 14px;
}

.product-list-item .product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.product-list-item .seller-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-list-item .seller-name {
  font-size: 13px;
  color: #666;
}

.product-list-item .product-actions {
  display: flex;
  gap: 10px;
}

/* 空状态和加载状态 */
.empty-state,
.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .shop-container {
    padding: 10px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-item {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .product-list-item {
    flex-direction: column;
  }
  
  .product-list-item .product-image {
    width: 100%;
    height: 200px;
    margin-right: 0;
    margin-bottom: 10px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>