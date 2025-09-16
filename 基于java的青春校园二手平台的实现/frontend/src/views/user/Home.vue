<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <el-carousel height="300px" class="banner">
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
    
    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="action-item" @click="$router.push('/user/publish')">
        <i class="el-icon-plus"></i>
        <span>发布商品</span>
      </div>
      <div class="action-item" @click="$router.push('/user/shop')">
        <i class="el-icon-shopping-bag-2"></i>
        <span>购物商城</span>
      </div>
      <div class="action-item" @click="$router.push('/user/my-products')">
        <i class="el-icon-goods"></i>
        <span>我的商品</span>
      </div>
      <div class="action-item" @click="$router.push('/user/orders')">
        <i class="el-icon-s-order"></i>
        <span>我的订单</span>
      </div>
    </div>
    
    <!-- 分类导航 -->
    <div class="categories-section">
      <h3 class="section-title">商品分类</h3>
      <div class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          @click="goToCategory(category)"
        >
          <div class="category-icon">
            <i :class="category.icon"></i>
          </div>
          <span class="category-name">{{ category.name }}</span>
          <span class="category-count">{{ category.productCount }}件</span>
        </div>
      </div>
    </div>
    
    <!-- 热门商品 -->
    <div class="products-section">
      <div class="section-header">
        <h3 class="section-title">热门商品</h3>
        <el-link type="primary" @click="$router.push('/user/shop')">查看更多 ></el-link>
      </div>
      <div class="products-grid">
        <product-card
          v-for="product in hotProducts"
          :key="product.id"
          :product="product"
          @click="goToProduct(product.id)"
        />
      </div>
    </div>
    
    <!-- 最新商品 -->
    <div class="products-section">
      <div class="section-header">
        <h3 class="section-title">最新上架</h3>
        <el-link type="primary" @click="$router.push('/user/shop?sort=latest')">查看更多 ></el-link>
      </div>
      <div class="products-grid">
        <product-card
          v-for="product in latestProducts"
          :key="product.id"
          :product="product"
          @click="goToProduct(product.id)"
        />
      </div>
    </div>
    
    <!-- 系统公告 -->
    <div class="notices-section">
      <h3 class="section-title">系统公告</h3>
      <div class="notices-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-item"
          @click="showNoticeDetail(notice)"
        >
          <div class="notice-content">
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ProductCard from '@/components/ProductCard.vue'
import { getHotProducts, getLatestProducts } from '@/api/product'
import { getEnabledCategories } from '@/api/category'
import { getLatestNotices } from '@/api/notice'
import moment from 'moment'

export default {
  name: 'Home',
  components: {
    ProductCard
  },
  data() {
    return {
      banners: [
        {
          image: '/images/banner1.jpg',
          title: '青春校园二手平台',
          description: '让闲置物品重新焕发价值',
          buttonText: '立即体验'
        },
        {
          image: '/images/banner2.jpg',
          title: '安全交易保障',
          description: '平台担保，交易更安心',
          buttonText: '了解更多'
        },
        {
          image: '/images/banner3.jpg',
          title: '发布商品赚钱',
          description: '一键发布，轻松变现',
          buttonText: '发布商品'
        }
      ],
      categories: [],
      hotProducts: [],
      latestProducts: [],
      notices: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        // 并行加载数据
        const [categoriesRes, hotProductsRes, latestProductsRes, noticesRes] = await Promise.all([
          getEnabledCategories(),
          getHotProducts({ size: 8 }),
          getLatestProducts({ size: 8 }),
          getLatestNotices(5)
        ])
        
        if (categoriesRes.data.code === 200) {
          this.categories = categoriesRes.data.data.map(cat => ({
            ...cat,
            icon: this.getCategoryIcon(cat.name),
            productCount: cat.productCount || 0
          }))
        }
        
        if (hotProductsRes.data.code === 200) {
          this.hotProducts = hotProductsRes.data.data.records || []
        }
        
        if (latestProductsRes.data.code === 200) {
          this.latestProducts = latestProductsRes.data.data.records || []
        }
        
        if (noticesRes.data.code === 200) {
          this.notices = noticesRes.data.data || []
        }
      } catch (error) {
        console.error('加载首页数据失败:', error)
      }
    },
    
    getCategoryIcon(categoryName) {
      const iconMap = {
        '电子产品': 'el-icon-mobile-phone',
        '服装鞋帽': 'el-icon-shopping-bag-1',
        '图书文具': 'el-icon-reading',
        '生活用品': 'el-icon-house',
        '运动户外': 'el-icon-bicycle',
        '美妆护肤': 'el-icon-star-on',
        '其他': 'el-icon-more'
      }
      return iconMap[categoryName] || 'el-icon-goods'
    },
    
    goToCategory(category) {
      this.$router.push({
        path: '/user/shop',
        query: { categoryId: category.id }
      })
    },
    
    goToProduct(productId) {
      this.$router.push(`/user/product/${productId}`)
    },
    
    showNoticeDetail(notice) {
      this.$alert(notice.content, notice.title, {
        confirmButtonText: '确定',
        dangerouslyUseHTMLString: true
      })
    },
    
    formatTime(time) {
      return moment(time).format('MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 轮播图 */
.banner {
  margin-bottom: 30px;
  border-radius: 10px;
  overflow: hidden;
}

.banner-item {
  height: 300px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-item::before {
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
}

.banner-content h2 {
  font-size: 32px;
  margin-bottom: 10px;
}

.banner-content p {
  font-size: 16px;
  margin-bottom: 20px;
}

/* 快捷入口 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.action-item {
  background: white;
  border-radius: 10px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-item i {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
  display: block;
}

.action-item span {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

/* 分类导航 */
.categories-section {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 600;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.category-item {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.category-item:hover {
  background: #f8f9fa;
  border-color: #409EFF;
}

.category-icon {
  margin-bottom: 10px;
}

.category-icon i {
  font-size: 24px;
  color: #409EFF;
}

.category-name {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.category-count {
  font-size: 12px;
  color: #999;
}

/* 商品区域 */
.products-section {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

/* 公告区域 */
.notices-section {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.notices-list {
  space-y: 10px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.notice-item:hover {
  background: #f8f9fa;
  padding-left: 10px;
  padding-right: 10px;
  border-radius: 5px;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-size: 14px;
  color: #333;
  margin-right: 15px;
}

.notice-time {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .banner-content h2 {
    font-size: 24px;
  }
  
  .banner-content p {
    font-size: 14px;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>