<template>
  <div class="my-orders-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的订单</h2>
      
      <!-- 订单状态统计 -->
      <div class="stats-tabs">
        <div
          v-for="tab in statusTabs"
          :key="tab.value"
          class="stat-tab"
          :class="{ active: searchForm.status === tab.value }"
          @click="switchStatus(tab.value)"
        >
          <div class="tab-count">{{ getStatusCount(tab.value) }}</div>
          <div class="tab-label">{{ tab.label }}</div>
        </div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索商品标题、订单号..."
          style="width: 300px"
          clearable
          @keyup.enter.native="handleSearch"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleSearch"
          ></el-button>
        </el-input>
        
        <el-select
          v-model="searchForm.tradeType"
          placeholder="交易方式"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部方式" value="" />
          <el-option label="线上交易" value="ONLINE" />
          <el-option label="线下交易" value="OFFLINE" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
        
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div class="orders-section">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="orders.length > 0" class="orders-list">
        <div
          v-for="order in orders"
          :key="order.id"
          class="order-item"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-number">订单号：{{ order.orderNumber }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
            
            <div class="order-status">
              <el-tag
                :type="getStatusType(order.status)"
                size="medium"
              >
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="order-content">
            <div class="product-info">
              <div class="product-image">
                <img :src="order.productImage || '/images/default-product.jpg'" :alt="order.productTitle" />
              </div>
              
              <div class="product-details">
                <h4 class="product-title">{{ order.productTitle }}</h4>
                <p class="product-description">{{ order.productDescription }}</p>
                
                <div class="product-meta">
                  <span class="product-price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ order.productPrice }}</span>
                  </span>
                  
                  <span class="trade-type">
                    <i :class="order.tradeType === 'ONLINE' ? 'el-icon-shopping-cart-2' : 'el-icon-location'"></i>
                    {{ order.tradeType === 'ONLINE' ? '线上交易' : '线下交易' }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="seller-info">
              <div class="seller-avatar">
                <el-avatar :size="32" :src="order.sellerAvatar">
                  {{ order.sellerName ? order.sellerName.charAt(0) : 'S' }}
                </el-avatar>
              </div>
              <div class="seller-details">
                <div class="seller-name">{{ order.sellerName || '匿名卖家' }}</div>
                <div class="seller-contact" v-if="order.sellerContact">
                  联系方式：{{ order.sellerContact }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 地址信息 -->
          <div class="address-info" v-if="order.tradeType === 'ONLINE' && order.address">
            <div class="address-label">
              <i class="el-icon-location"></i>
              收货地址：
            </div>
            <div class="address-detail">
              {{ order.address.receiverName }} {{ order.address.phone }}<br>
              {{ order.address.address }}
            </div>
          </div>
          
          <!-- 备注信息 -->
          <div class="remark-info" v-if="order.remark">
            <div class="remark-label">
              <i class="el-icon-chat-dot-round"></i>
              备注：
            </div>
            <div class="remark-content">{{ order.remark }}</div>
          </div>
          
          <div class="order-footer">
            <div class="order-amount">
              <span class="amount-label">订单金额：</span>
              <span class="amount-value">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ order.totalAmount }}</span>
              </span>
            </div>
            
            <div class="order-actions">
              <el-button
                size="small"
                @click="viewOrderDetail(order.id)"
              >
                查看详情
              </el-button>
              
              <el-button
                v-if="order.status === 'PENDING'"
                type="danger"
                size="small"
                @click="cancelOrder(order)"
              >
                取消订单
              </el-button>
              
              <el-button
                v-if="order.status === 'CONFIRMED'"
                type="primary"
                size="small"
                @click="completeOrder(order)"
              >
                确认收货
              </el-button>
              
              <el-button
                v-if="order.status === 'COMPLETED'"
                type="success"
                size="small"
                @click="rateOrder(order)"
              >
                评价
              </el-button>
              
              <el-button
                v-if="canDelete(order.status)"
                size="small"
                @click="deleteOrder(order)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无订单">
          <el-button type="primary" @click="$router.push('/user/shop')">
            去购物
          </el-button>
        </el-empty>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-section" v-if="pagination.total > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>
  </div>
</template>

<script>
import { getMyOrders, cancelOrder, confirmOrder, completeOrder, deleteOrder } from '@/api/order'
import moment from 'moment'

export default {
  name: 'MyOrders',
  data() {
    return {
      loading: false,
      searchForm: {
        keyword: '',
        status: '',
        tradeType: '',
        startTime: '',
        endTime: ''
      },
      dateRange: null,
      orders: [],
      stats: {},
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      statusTabs: [
        { label: '全部', value: '' },
        { label: '待确认', value: 'PENDING' },
        { label: '已确认', value: 'CONFIRMED' },
        { label: '进行中', value: 'IN_PROGRESS' },
        { label: '已完成', value: 'COMPLETED' },
        { label: '已取消', value: 'CANCELLED' }
      ]
    }
  },
  created() {
    this.initFromQuery()
    this.loadOrders()
  },
  watch: {
    '$route.query': {
      handler() {
        this.initFromQuery()
        this.loadOrders()
      },
      deep: true
    }
  },
  methods: {
    initFromQuery() {
      const query = this.$route.query
      this.searchForm.status = query.status || ''
      this.pagination.current = query.page ? parseInt(query.page) : 1
    },
    
    async loadOrders() {
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
        
        const res = await getMyOrders(params)
        if (res.data.code === 200) {
          this.orders = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
          this.stats = res.data.data.stats || {}
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        this.$message.error('加载订单失败')
      } finally {
        this.loading = false
      }
    },
    
    switchStatus(status) {
      this.searchForm.status = status
      this.pagination.current = 1
      this.updateQuery()
      this.loadOrders()
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.updateQuery()
      this.loadOrders()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        status: '',
        tradeType: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = null
      this.pagination.current = 1
      this.updateQuery()
      this.loadOrders()
    },
    
    handleDateChange(dates) {
      if (dates && dates.length === 2) {
        this.searchForm.startTime = moment(dates[0]).format('YYYY-MM-DD')
        this.searchForm.endTime = moment(dates[1]).format('YYYY-MM-DD')
      } else {
        this.searchForm.startTime = ''
        this.searchForm.endTime = ''
      }
      this.handleSearch()
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.updateQuery()
      this.loadOrders()
    },
    
    handleCurrentChange(page) {
      this.pagination.current = page
      this.updateQuery()
      this.loadOrders()
    },
    
    updateQuery() {
      const query = {
        status: this.searchForm.status,
        page: this.pagination.current
      }
      
      // 清理空值
      Object.keys(query).forEach(key => {
        if (query[key] === '' || query[key] === null) {
          delete query[key]
        }
      })
      
      this.$router.replace({ query })
    },
    
    viewOrderDetail(orderId) {
      this.$router.push(`/user/order/${orderId}`)
    },
    
    async cancelOrder(order) {
      try {
        await this.$confirm('确定要取消这个订单吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await cancelOrder(order.id)
        if (res.data.code === 200) {
          this.$message.success('订单已取消')
          this.loadOrders()
        } else {
          this.$message.error(res.data.message || '取消失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('取消订单失败:', error)
          this.$message.error('取消失败，请重试')
        }
      }
    },
    
    async completeOrder(order) {
      try {
        await this.$confirm('确认已收到商品吗？', '确认收货', {
          confirmButtonText: '确认收货',
          cancelButtonText: '取消',
          type: 'success'
        })
        
        const res = await completeOrder(order.id)
        if (res.data.code === 200) {
          this.$message.success('订单已完成')
          this.loadOrders()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('完成订单失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    rateOrder(order) {
      this.$message.info('评价功能开发中...')
    },
    
    async deleteOrder(order) {
      try {
        await this.$confirm('确定要删除这个订单吗？删除后无法恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteOrder(order.id)
        if (res.data.code === 200) {
          this.$message.success('订单已删除')
          this.loadOrders()
        } else {
          this.$message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除订单失败:', error)
          this.$message.error('删除失败，请重试')
        }
      }
    },
    
    canDelete(status) {
      return ['COMPLETED', 'CANCELLED'].includes(status)
    },
    
    getStatusCount(status) {
      if (status === '') {
        return this.stats.total || 0
      }
      return this.stats[status.toLowerCase()] || 0
    },
    
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'CONFIRMED': 'primary',
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
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
.my-orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 30px 0;
}

.stats-tabs {
  display: flex;
  gap: 20px;
}

.stat-tab {
  text-align: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 80px;
}

.stat-tab:hover {
  background: #e9ecef;
}

.stat-tab.active {
  background: #409EFF;
  color: white;
}

.tab-count {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 5px;
}

.tab-label {
  font-size: 14px;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

/* 订单列表 */
.orders-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.loading-container {
  padding: 40px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s;
}

.order-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.order-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-number {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.order-content {
  padding: 20px;
}

.product-info {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 5px 0;
  line-height: 1.4;
}

.product-description {
  font-size: 13px;
  color: #666;
  margin: 0 0 10px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  gap: 20px;
  align-items: center;
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
  font-size: 18px;
  color: #e74c3c;
  font-weight: 700;
  margin-left: 2px;
}

.trade-type {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #666;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-bottom: 3px;
}

.seller-contact {
  font-size: 12px;
  color: #666;
}

.address-info,
.remark-info {
  display: flex;
  gap: 10px;
  padding: 10px 15px;
  background: #f0f8ff;
  border-radius: 6px;
  margin-bottom: 15px;
  font-size: 13px;
}

.address-label,
.remark-label {
  color: #409EFF;
  font-weight: 500;
  white-space: nowrap;
}

.address-detail,
.remark-content {
  color: #666;
  line-height: 1.4;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.order-amount {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.amount-label {
  font-size: 14px;
  color: #666;
}

.amount-value {
  display: flex;
  align-items: baseline;
}

.amount-value .price-symbol {
  font-size: 16px;
}

.amount-value .price-value {
  font-size: 20px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

/* 空状态 */
.empty-state {
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
  .my-orders-container {
    padding: 10px;
  }
  
  .stats-tabs {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .stat-tab {
    min-width: 60px;
    padding: 10px 15px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .order-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .product-info {
    flex-direction: column;
  }
  
  .product-image {
    width: 100%;
    height: 150px;
  }
  
  .order-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .order-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .stats-tabs {
    grid-template-columns: repeat(3, 1fr);
    display: grid;
  }
  
  .order-actions {
    flex-direction: column;
  }
}
</style>