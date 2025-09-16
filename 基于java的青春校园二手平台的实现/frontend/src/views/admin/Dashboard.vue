<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户总数</div>
          <div class="stat-change" :class="{ positive: stats.userGrowth > 0 }">
            <i :class="stats.userGrowth > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(stats.userGrowth || 0) }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon product-icon">
          <i class="el-icon-goods"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.productCount || 0 }}</div>
          <div class="stat-label">商品总数</div>
          <div class="stat-change" :class="{ positive: stats.productGrowth > 0 }">
            <i :class="stats.productGrowth > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(stats.productGrowth || 0) }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon order-icon">
          <i class="el-icon-s-order"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.orderCount || 0 }}</div>
          <div class="stat-label">订单总数</div>
          <div class="stat-change" :class="{ positive: stats.orderGrowth > 0 }">
            <i :class="stats.orderGrowth > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(stats.orderGrowth || 0) }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon revenue-icon">
          <i class="el-icon-money"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ formatMoney(stats.totalRevenue || 0) }}</div>
          <div class="stat-label">交易总额</div>
          <div class="stat-change" :class="{ positive: stats.revenueGrowth > 0 }">
            <i :class="stats.revenueGrowth > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(stats.revenueGrowth || 0) }}%
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 用户注册趋势 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户注册趋势</h3>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              size="small"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleDateChange"
            />
          </div>
          <div class="chart-content">
            <div ref="userTrendChart" class="chart"></div>
          </div>
        </div>
        
        <!-- 商品分类分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>商品分类分布</h3>
            <el-button type="text" size="small" @click="loadCategoryStats">
              <i class="el-icon-refresh"></i>
              刷新
            </el-button>
          </div>
          <div class="chart-content">
            <div ref="categoryChart" class="chart"></div>
          </div>
        </div>
      </div>
      
      <div class="chart-row">
        <!-- 订单状态分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>订单状态分布</h3>
          </div>
          <div class="chart-content">
            <div ref="orderStatusChart" class="chart"></div>
          </div>
        </div>
        
        <!-- 交易金额趋势 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>交易金额趋势</h3>
          </div>
          <div class="chart-content">
            <div ref="revenueTrendChart" class="chart"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 快捷操作和最新动态 -->
    <div class="bottom-section">
      <!-- 快捷操作 -->
      <div class="quick-actions">
        <div class="section-header">
          <h3>快捷操作</h3>
        </div>
        
        <div class="action-grid">
          <div class="action-item" @click="$router.push('/admin/users')">
            <div class="action-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="action-text">用户管理</div>
          </div>
          
          <div class="action-item" @click="$router.push('/admin/products')">
            <div class="action-icon">
              <i class="el-icon-goods"></i>
            </div>
            <div class="action-text">商品管理</div>
          </div>
          
          <div class="action-item" @click="$router.push('/admin/orders')">
            <div class="action-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="action-text">订单管理</div>
          </div>
          
          <div class="action-item" @click="$router.push('/admin/categories')">
            <div class="action-icon">
              <i class="el-icon-menu"></i>
            </div>
            <div class="action-text">分类管理</div>
          </div>
          
          <div class="action-item" @click="$router.push('/admin/notices')">
            <div class="action-icon">
              <i class="el-icon-bell"></i>
            </div>
            <div class="action-text">公告管理</div>
          </div>
          
          <div class="action-item" @click="$router.push('/admin/statistics')">
            <div class="action-icon">
              <i class="el-icon-data-analysis"></i>
            </div>
            <div class="action-text">数据统计</div>
          </div>
        </div>
      </div>
      
      <!-- 最新动态 -->
      <div class="recent-activities">
        <div class="section-header">
          <h3>最新动态</h3>
          <el-button type="text" size="small" @click="loadRecentActivities">
            <i class="el-icon-refresh"></i>
            刷新
          </el-button>
        </div>
        
        <div class="activity-list" v-if="recentActivities.length > 0">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="activity-item"
          >
            <div class="activity-icon">
              <i :class="getActivityIcon(activity.type)"></i>
            </div>
            <div class="activity-content">
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-desc">{{ activity.description }}</div>
              <div class="activity-time">{{ formatTime(activity.createTime) }}</div>
            </div>
          </div>
        </div>
        
        <div v-else class="no-activity">
          <el-empty description="暂无最新动态" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getPlatformOverview,
  getUserTrend,
  getCategoryStats,
  getOrderStats,
  getRevenueTrend
} from '@/api/statistics'
import moment from 'moment'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {},
      dateRange: null,
      recentActivities: [],
      // 图表实例
      userTrendChart: null,
      categoryChart: null,
      orderStatusChart: null,
      revenueTrendChart: null
    }
  },
  created() {
    this.initDateRange()
    this.loadDashboardData()
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    // 销毁图表实例
    if (this.userTrendChart) {
      this.userTrendChart.dispose()
    }
    if (this.categoryChart) {
      this.categoryChart.dispose()
    }
    if (this.orderStatusChart) {
      this.orderStatusChart.dispose()
    }
    if (this.revenueTrendChart) {
      this.revenueTrendChart.dispose()
    }
  },
  methods: {
    initDateRange() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30) // 最近30天
      this.dateRange = [start, end]
    },
    
    async loadDashboardData() {
      await Promise.all([
        this.loadPlatformStats(),
        this.loadUserTrend(),
        this.loadCategoryStats(),
        this.loadOrderStats(),
        this.loadRevenueTrend(),
        this.loadRecentActivities()
      ])
    },
    
    async loadPlatformStats() {
      try {
        const res = await getPlatformOverview()
        if (res.data.code === 200) {
          this.stats = res.data.data || {}
        }
      } catch (error) {
        console.error('加载平台统计失败:', error)
      }
    },
    
    async loadUserTrend() {
      try {
        const params = {
          startTime: moment(this.dateRange[0]).format('YYYY-MM-DD'),
          endTime: moment(this.dateRange[1]).format('YYYY-MM-DD')
        }
        const res = await getUserTrend(params)
        if (res.data.code === 200) {
          this.updateUserTrendChart(res.data.data || [])
        }
      } catch (error) {
        console.error('加载用户趋势失败:', error)
      }
    },
    
    async loadCategoryStats() {
      try {
        const res = await getCategoryStats()
        if (res.data.code === 200) {
          this.updateCategoryChart(res.data.data || [])
        }
      } catch (error) {
        console.error('加载分类统计失败:', error)
      }
    },
    
    async loadOrderStats() {
      try {
        const res = await getOrderStats()
        if (res.data.code === 200) {
          this.updateOrderStatusChart(res.data.data || {})
        }
      } catch (error) {
        console.error('加载订单统计失败:', error)
      }
    },
    
    async loadRevenueTrend() {
      try {
        const params = {
          startTime: moment(this.dateRange[0]).format('YYYY-MM-DD'),
          endTime: moment(this.dateRange[1]).format('YYYY-MM-DD')
        }
        const res = await getRevenueTrend(params)
        if (res.data.code === 200) {
          this.updateRevenueTrendChart(res.data.data || [])
        }
      } catch (error) {
        console.error('加载收入趋势失败:', error)
      }
    },
    
    loadRecentActivities() {
      // 模拟最新动态数据
      this.recentActivities = [
        {
          id: 1,
          type: 'USER_REGISTER',
          title: '新用户注册',
          description: '用户 "张三" 完成注册',
          createTime: new Date(Date.now() - 5 * 60 * 1000)
        },
        {
          id: 2,
          type: 'PRODUCT_PUBLISH',
          title: '商品发布',
          description: '用户发布了商品 "《算法导论》"',
          createTime: new Date(Date.now() - 15 * 60 * 1000)
        },
        {
          id: 3,
          type: 'ORDER_CREATE',
          title: '新订单创建',
          description: '订单 #202312150001 已创建',
          createTime: new Date(Date.now() - 30 * 60 * 1000)
        },
        {
          id: 4,
          type: 'PRODUCT_AUDIT',
          title: '商品审核',
          description: '商品 "《数据结构》" 审核通过',
          createTime: new Date(Date.now() - 60 * 60 * 1000)
        },
        {
          id: 5,
          type: 'ORDER_COMPLETE',
          title: '订单完成',
          description: '订单 #202312140005 已完成',
          createTime: new Date(Date.now() - 2 * 60 * 60 * 1000)
        }
      ]
    },
    
    handleDateChange() {
      this.loadUserTrend()
      this.loadRevenueTrend()
    },
    
    initCharts() {
      this.initUserTrendChart()
      this.initCategoryChart()
      this.initOrderStatusChart()
      this.initRevenueTrendChart()
    },
    
    initUserTrendChart() {
      this.userTrendChart = echarts.init(this.$refs.userTrendChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '注册用户数',
          type: 'line',
          smooth: true,
          data: [],
          itemStyle: {
            color: '#409EFF'
          }
        }]
      }
      this.userTrendChart.setOption(option)
    },
    
    initCategoryChart() {
      this.categoryChart = echarts.init(this.$refs.categoryChart)
      const option = {
        tooltip: {
          trigger: 'item'
        },
        series: [{
          name: '商品分类',
          type: 'pie',
          radius: '60%',
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      this.categoryChart.setOption(option)
    },
    
    initOrderStatusChart() {
      this.orderStatusChart = echarts.init(this.$refs.orderStatusChart)
      const option = {
        tooltip: {
          trigger: 'item'
        },
        series: [{
          name: '订单状态',
          type: 'doughnut',
          radius: ['40%', '70%'],
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      this.orderStatusChart.setOption(option)
    },
    
    initRevenueTrendChart() {
      this.revenueTrendChart = echarts.init(this.$refs.revenueTrendChart)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '交易金额',
          type: 'bar',
          data: [],
          itemStyle: {
            color: '#67C23A'
          }
        }]
      }
      this.revenueTrendChart.setOption(option)
    },
    
    updateUserTrendChart(data) {
      if (!this.userTrendChart) return
      
      const dates = data.map(item => moment(item.date).format('MM-DD'))
      const counts = data.map(item => item.count)
      
      this.userTrendChart.setOption({
        xAxis: {
          data: dates
        },
        series: [{
          data: counts
        }]
      })
    },
    
    updateCategoryChart(data) {
      if (!this.categoryChart) return
      
      const chartData = data.map(item => ({
        name: item.categoryName,
        value: item.productCount
      }))
      
      this.categoryChart.setOption({
        series: [{
          data: chartData
        }]
      })
    },
    
    updateOrderStatusChart(data) {
      if (!this.orderStatusChart) return
      
      const statusMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      
      const chartData = Object.keys(data).map(key => ({
        name: statusMap[key] || key,
        value: data[key]
      }))
      
      this.orderStatusChart.setOption({
        series: [{
          data: chartData
        }]
      })
    },
    
    updateRevenueTrendChart(data) {
      if (!this.revenueTrendChart) return
      
      const dates = data.map(item => moment(item.date).format('MM-DD'))
      const amounts = data.map(item => item.amount)
      
      this.revenueTrendChart.setOption({
        xAxis: {
          data: dates
        },
        series: [{
          data: amounts
        }]
      })
    },
    
    getActivityIcon(type) {
      const iconMap = {
        'USER_REGISTER': 'el-icon-user-solid',
        'PRODUCT_PUBLISH': 'el-icon-goods',
        'ORDER_CREATE': 'el-icon-s-order',
        'PRODUCT_AUDIT': 'el-icon-view',
        'ORDER_COMPLETE': 'el-icon-circle-check'
      }
      return iconMap[type] || 'el-icon-info'
    },
    
    formatMoney(amount) {
      return (amount / 100).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    
    formatTime(time) {
      if (!time) return ''
      return moment(time).fromNow()
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-change {
  font-size: 12px;
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 3px;
}

.stat-change.positive {
  color: #67c23a;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 30px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.chart-content {
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 底部区域 */
.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.quick-actions,
.recent-activities {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 快捷操作 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 24px;
  color: #409EFF;
  margin-bottom: 10px;
}

.action-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 最新动态 */
.activity-list {
  max-height: 400px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 36px;
  height: 36px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.activity-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.no-activity {
  text-align: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .bottom-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .chart-card {
    padding: 20px;
  }
  
  .chart-content {
    height: 250px;
  }
  
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stat-card {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-header {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
}
</style>