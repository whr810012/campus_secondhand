<template>
  <div class="admin-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <h1>管理后台</h1>
      <p>校园二手交易平台数据概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.userCount }}</h3>
          <p>注册用户</p>
          <span class="stat-change" :class="{ positive: stats.userGrowth > 0 }">
            <i class="fas" :class="stats.userGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down'"></i>
            {{ Math.abs(stats.userGrowth) }}%
          </span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon product-icon">
          <i class="fas fa-box"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.productCount }}</h3>
          <p>商品总数</p>
          <span class="stat-change" :class="{ positive: stats.productGrowth > 0 }">
            <i class="fas" :class="stats.productGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down'"></i>
            {{ Math.abs(stats.productGrowth) }}%
          </span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon order-icon">
          <i class="fas fa-shopping-cart"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.orderCount }}</h3>
          <p>订单总数</p>
          <span class="stat-change" :class="{ positive: stats.orderGrowth > 0 }">
            <i class="fas" :class="stats.orderGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down'"></i>
            {{ Math.abs(stats.orderGrowth) }}%
          </span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon revenue-icon">
          <i class="fas fa-dollar-sign"></i>
        </div>
        <div class="stat-content">
          <h3>¥{{ formatNumber(stats.totalRevenue) }}</h3>
          <p>交易总额</p>
          <span class="stat-change" :class="{ positive: stats.revenueGrowth > 0 }">
            <i class="fas" :class="stats.revenueGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down'"></i>
            {{ Math.abs(stats.revenueGrowth) }}%
          </span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 用户增长趋势 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户增长趋势</h3>
            <div class="chart-controls">
              <select v-model="userChartPeriod" @change="fetchUserTrend">
                <option value="7">最近7天</option>
                <option value="30">最近30天</option>
                <option value="90">最近90天</option>
              </select>
            </div>
          </div>
          <div class="chart-content">
            <canvas ref="userChart" width="400" height="200"></canvas>
          </div>
        </div>

        <!-- 商品分类分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>商品分类分布</h3>
          </div>
          <div class="chart-content">
            <canvas ref="categoryChart" width="400" height="200"></canvas>
          </div>
        </div>
      </div>

      <div class="chart-row">
        <!-- 交易趋势 -->
        <div class="chart-card full-width">
          <div class="chart-header">
            <h3>交易趋势</h3>
            <div class="chart-controls">
              <select v-model="tradePeriod" @change="fetchTradeTrend">
                <option value="7">最近7天</option>
                <option value="30">最近30天</option>
                <option value="90">最近90天</option>
              </select>
            </div>
          </div>
          <div class="chart-content">
            <canvas ref="tradeChart" width="800" height="300"></canvas>
          </div>
        </div>
      </div>
    </div>

    <!-- 待处理事项 -->
    <div class="pending-section">
      <h2>待处理事项</h2>
      <div class="pending-grid">
        <div class="pending-card" @click="$router.push('/admin/audit')">
          <div class="pending-icon audit-icon">
            <i class="fas fa-eye"></i>
          </div>
          <div class="pending-content">
            <h4>待审核商品</h4>
            <p>{{ pendingStats.pendingProducts }} 个</p>
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>

        <div class="pending-card" @click="$router.push('/admin/users')">
          <div class="pending-icon verify-icon">
            <i class="fas fa-user-check"></i>
          </div>
          <div class="pending-content">
            <h4>待认证用户</h4>
            <p>{{ pendingStats.pendingVerifications }} 个</p>
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>

        <div class="pending-card" @click="$router.push('/admin/orders')">
          <div class="pending-icon dispute-icon">
            <i class="fas fa-exclamation-triangle"></i>
          </div>
          <div class="pending-content">
            <h4>待处理纠纷</h4>
            <p>{{ pendingStats.pendingDisputes }} 个</p>
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>

        <div class="pending-card" @click="$router.push('/admin/reports')">
          <div class="pending-icon report-icon">
            <i class="fas fa-flag"></i>
          </div>
          <div class="pending-content">
            <h4>待处理举报</h4>
            <p>{{ pendingStats.pendingReports }} 个</p>
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>
      </div>
    </div>

    <!-- 最新动态 -->
    <div class="activity-section">
      <h2>最新动态</h2>
      <div class="activity-list">
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id" 
          class="activity-item"
        >
          <div class="activity-icon" :class="activity.type">
            <i class="fas" :class="getActivityIcon(activity.type)"></i>
          </div>
          <div class="activity-content">
            <p>{{ activity.description }}</p>
            <span class="activity-time">{{ formatTime(activity.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

export default {
  name: 'AdminDashboard',
  setup() {
    const router = useRouter()
    const userChart = ref(null)
    const categoryChart = ref(null)
    const tradeChart = ref(null)
    
    const stats = reactive({
      userCount: 0,
      productCount: 0,
      orderCount: 0,
      totalRevenue: 0,
      userGrowth: 0,
      productGrowth: 0,
      orderGrowth: 0,
      revenueGrowth: 0
    })

    const pendingStats = reactive({
      pendingProducts: 0,
      pendingVerifications: 0,
      pendingDisputes: 0,
      pendingReports: 0
    })

    const recentActivities = ref([])
    const userChartPeriod = ref('30')
    const tradePeriod = ref('30')

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取待处理事项
    const fetchPendingStats = async () => {
      try {
        const response = await api.get('/admin/pending-stats')
        Object.assign(pendingStats, response.data)
      } catch (error) {
        console.error('获取待处理事项失败:', error)
      }
    }

    // 获取最新动态
    const fetchRecentActivities = async () => {
      try {
        const response = await api.get('/admin/activities?limit=10')
        recentActivities.value = response.data
      } catch (error) {
        console.error('获取最新动态失败:', error)
      }
    }

    // 获取用户增长趋势
    const fetchUserTrend = async () => {
      try {
        const response = await api.get(`/admin/user-trend?days=${userChartPeriod.value}`)
        drawUserChart(response.data)
      } catch (error) {
        console.error('获取用户趋势失败:', error)
      }
    }

    // 获取交易趋势
    const fetchTradeTrend = async () => {
      try {
        const response = await api.get(`/admin/trade-trend?days=${tradePeriod.value}`)
        drawTradeChart(response.data)
      } catch (error) {
        console.error('获取交易趋势失败:', error)
      }
    }

    // 获取分类分布
    const fetchCategoryDistribution = async () => {
      try {
        const response = await api.get('/admin/category-distribution')
        drawCategoryChart(response.data)
      } catch (error) {
        console.error('获取分类分布失败:', error)
      }
    }

    // 绘制用户增长图表
    const drawUserChart = (data) => {
      const ctx = userChart.value.getContext('2d')
      // 这里应该使用Chart.js或其他图表库
      // 简化实现，实际项目中建议使用专业图表库
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
      ctx.fillStyle = '#007bff'
      ctx.fillText('用户增长图表', 10, 20)
    }

    // 绘制分类分布图表
    const drawCategoryChart = (data) => {
      const ctx = categoryChart.value.getContext('2d')
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
      ctx.fillStyle = '#28a745'
      ctx.fillText('分类分布图表', 10, 20)
    }

    // 绘制交易趋势图表
    const drawTradeChart = (data) => {
      const ctx = tradeChart.value.getContext('2d')
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
      ctx.fillStyle = '#ffc107'
      ctx.fillText('交易趋势图表', 10, 20)
    }

    // 格式化数字
    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toLocaleString()
    }

    // 获取活动图标
    const getActivityIcon = (type) => {
      const icons = {
        user: 'fa-user-plus',
        product: 'fa-plus-circle',
        order: 'fa-shopping-cart',
        audit: 'fa-eye',
        report: 'fa-flag'
      }
      return icons[type] || 'fa-info-circle'
    }

    // 格式化时间
    const formatTime = (time) => {
      const now = new Date()
      const target = new Date(time)
      const diff = now - target
      
      if (diff < 60000) {
        return '刚刚'
      } else if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) {
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return Math.floor(diff / 86400000) + '天前'
      }
    }

    onMounted(async () => {
      await fetchStats()
      await fetchPendingStats()
      await fetchRecentActivities()
      
      await nextTick()
      await fetchUserTrend()
      await fetchTradeTrend()
      await fetchCategoryDistribution()
    })

    return {
      stats,
      pendingStats,
      recentActivities,
      userChartPeriod,
      tradePeriod,
      userChart,
      categoryChart,
      tradeChart,
      fetchUserTrend,
      fetchTradeTrend,
      formatNumber,
      getActivityIcon,
      formatTime
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 2rem;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  color: #333;
}

.dashboard-header p {
  margin: 0;
  color: #666;
  font-size: 1.1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
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

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.8rem;
  font-weight: 600;
  color: #333;
}

.stat-content p {
  margin: 0 0 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.stat-change {
  font-size: 0.8rem;
  font-weight: 500;
  color: #dc3545;
}

.stat-change.positive {
  color: #28a745;
}

.stat-change i {
  margin-right: 0.25rem;
}

.charts-section {
  margin-bottom: 2rem;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.chart-row:last-child {
  margin-bottom: 0;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.chart-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.chart-controls select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.chart-content {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-content canvas {
  max-width: 100%;
  max-height: 100%;
}

.pending-section {
  margin-bottom: 2rem;
}

.pending-section h2 {
  margin: 0 0 1rem 0;
  font-size: 1.3rem;
  color: #333;
}

.pending-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.pending-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pending-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.pending-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.audit-icon {
  background: #007bff;
}

.verify-icon {
  background: #28a745;
}

.dispute-icon {
  background: #ffc107;
  color: #333;
}

.report-icon {
  background: #dc3545;
}

.pending-content {
  flex: 1;
}

.pending-content h4 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  color: #333;
}

.pending-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.pending-card > i {
  color: #ccc;
}

.activity-section h2 {
  margin: 0 0 1rem 0;
  font-size: 1.3rem;
  color: #333;
}

.activity-list {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.8rem;
}

.activity-icon.user {
  background: #007bff;
}

.activity-icon.product {
  background: #28a745;
}

.activity-icon.order {
  background: #ffc107;
  color: #333;
}

.activity-icon.audit {
  background: #6f42c1;
}

.activity-icon.report {
  background: #dc3545;
}

.activity-content {
  flex: 1;
}

.activity-content p {
  margin: 0 0 0.25rem 0;
  color: #333;
  font-size: 0.9rem;
}

.activity-time {
  color: #999;
  font-size: 0.8rem;
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: 1rem;
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .pending-grid {
    grid-template-columns: 1fr;
  }
}
</style>