<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1>仪表盘</h1>
      <p>欢迎回来，{{ userStore.userInfo?.nickname || '管理员' }}！</p>
    </div>
    
    <!-- 数据概览卡片 -->
    <div class="stats-grid" v-loading="loading.stats">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            +{{ stats.userGrowth }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon product-icon">
          <el-icon><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.productCount }}</div>
          <div class="stat-label">商品总数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            +{{ stats.productGrowth }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon order-icon">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.orderCount }}</div>
          <div class="stat-label">订单总数</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            +{{ stats.orderGrowth }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon revenue-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">¥{{ formatNumber(stats.revenue) }}</div>
          <div class="stat-label">交易金额</div>
          <div class="stat-change positive">
            <el-icon><ArrowUp /></el-icon>
            +{{ stats.revenueGrowth }}%
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid" v-loading="loading.charts">
      <!-- 用户增长趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户增长趋势</h3>
          <el-select v-model="userChartPeriod" size="small" style="width: 120px">
            <el-option label="最近7天" value="7d" />
            <el-option label="最近30天" value="30d" />
            <el-option label="最近90天" value="90d" />
          </el-select>
        </div>
        <div ref="userChartRef" class="chart-container"></div>
      </div>
      
      <!-- 商品分类分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>商品分类分布</h3>
        </div>
        <div ref="categoryChartRef" class="chart-container"></div>
      </div>
    </div>
    
    <!-- 数据表格区域 -->
    <div class="tables-grid">
      <!-- 最新用户 -->
      <div class="table-card">
        <div class="table-header">
          <h3>最新注册用户</h3>
          <el-link type="primary" @click="$router.push('/admin/users')">
            查看全部
          </el-link>
        </div>
        <el-table :data="recentUsers" style="width: 100%" size="small" v-loading="loading.users">
          <el-table-column prop="avatar" label="头像" width="60">
            <template #default="{ row }">
              <el-avatar :src="row.avatar" :size="32">
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="school" label="学校" />
          <el-table-column prop="createdAt" label="注册时间">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 待审核商品 -->
      <div class="table-card">
        <div class="table-header">
          <h3>待审核商品</h3>
          <el-link type="primary" @click="$router.push('/admin/products')">
            查看全部
          </el-link>
        </div>
        <el-table :data="pendingProducts" style="width: 100%" size="small" v-loading="loading.products">
          <el-table-column prop="image" label="图片" width="60">
            <template #default="{ row }">
              <el-image
                :src="row.images?.[0]"
                fit="cover"
                style="width: 40px; height: 40px; border-radius: 4px"
              />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="商品名称" show-overflow-tooltip />
          <el-table-column prop="price" label="价格" width="80">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="seller" label="卖家" />
          <el-table-column prop="createdAt" label="发布时间">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="handleApproveProduct(row.id)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="handleRejectProduct(row.id)">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <!-- 系统状态 -->
    <div class="system-status">
      <div class="status-card">
        <div class="status-header">
          <h3>系统状态</h3>
          <el-tag :type="systemStatus.overall === 'healthy' ? 'success' : 'warning'">
            {{ systemStatus.overall === 'healthy' ? '运行正常' : '需要关注' }}
          </el-tag>
        </div>
        <div class="status-items">
          <div class="status-item">
            <span class="status-label">服务器状态</span>
            <el-tag :type="systemStatus.server === 'online' ? 'success' : 'danger'" size="small">
              {{ systemStatus.server === 'online' ? '在线' : '离线' }}
            </el-tag>
          </div>
          <div class="status-item">
            <span class="status-label">数据库状态</span>
            <el-tag :type="systemStatus.database === 'connected' ? 'success' : 'danger'" size="small">
              {{ systemStatus.database === 'connected' ? '已连接' : '断开' }}
            </el-tag>
          </div>
          <div class="status-item">
            <span class="status-label">存储状态</span>
            <el-tag :type="systemStatus.storage === 'available' ? 'success' : 'warning'" size="small">
              {{ systemStatus.storage === 'available' ? '可用' : '不足' }}
            </el-tag>
          </div>
          <div class="status-item">
            <span class="status-label">CPU使用率</span>
            <span class="status-value">{{ systemStatus.cpu }}%</span>
          </div>
          <div class="status-item">
            <span class="status-label">内存使用率</span>
            <span class="status-value">{{ systemStatus.memory }}%</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'
import {
  getDashboardStats,
  getUserStats,
  getProductStats,
  getOrderStats,
  getRecentUsers,
  getPendingProducts,
  getCategoryStats,
  getSystemStatus,
  getUserGrowthTrend,
  approveProduct,
  rejectProduct
} from '@/api/admin'

const router = useRouter()
const userStore = useUserStore()

const userChartRef = ref()
const categoryChartRef = ref()
const userChartPeriod = ref('30d')

let userChart = null
let categoryChart = null

// 统计数据
const stats = reactive({
  userCount: 0,
  userGrowth: 0,
  productCount: 0,
  productGrowth: 0,
  orderCount: 0,
  orderGrowth: 0,
  revenue: 0,
  revenueGrowth: 0
})

// 加载状态
const loading = reactive({
  stats: false,
  users: false,
  products: false,
  charts: false
})

// 最新用户
const recentUsers = ref([])

// 待审核商品
const pendingProducts = ref([])

// 图表数据
const chartData = reactive({
  userGrowth: [],
  categoryDistribution: []
})

// 系统状态
const systemStatus = reactive({
  overall: 'healthy',
  server: 'online',
  database: 'connected',
  storage: 'available',
  cpu: 45,
  memory: 62
})

// 初始化用户增长图表
const initUserChart = () => {
  if (!userChartRef.value) return
  
  userChart = echarts.init(userChartRef.value)
  updateUserChart()
}

// 更新用户增长图表
const updateUserChart = () => {
  if (!userChart) return
  
  const dates = chartData.userGrowth.map(item => item.date)
  const counts = chartData.userGrowth.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: counts,
        itemStyle: {
          color: '#667eea'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(102, 126, 234, 0.3)'
              },
              {
                offset: 1,
                color: 'rgba(102, 126, 234, 0.1)'
              }
            ]
          }
        }
      }
    ]
  }
  
  userChart.setOption(option)
}

// 初始化分类分布图表
const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  
  categoryChart = echarts.init(categoryChartRef.value)
  updateCategoryChart()
}

// 更新分类分布图表
const updateCategoryChart = () => {
  if (!categoryChart) return
  
  const categories = chartData.categoryDistribution.map(item => item.name)
  const data = chartData.categoryDistribution.map(item => ({
    value: item.count,
    name: item.name
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: categories
    },
    series: [
      {
        name: '商品分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data,
        itemStyle: {
          color: function(params) {
            const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#43e97b']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }
  
  categoryChart.setOption(option)
}

// 格式化数字
const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 审核商品
const handleApproveProduct = async (id) => {
  try {
    await approveProduct(id)
    ElMessage.success('商品审核通过')
    // 从待审核列表中移除
    const index = pendingProducts.value.findIndex(item => item.id === id)
    if (index > -1) {
      pendingProducts.value.splice(index, 1)
    }
    // 刷新统计数据
    await fetchStats()
  } catch (error) {
    console.error('审核通过失败:', error)
    ElMessage.error('审核操作失败')
  }
}

const handleRejectProduct = async (id) => {
  try {
    await rejectProduct(id)
    ElMessage.success('商品审核拒绝')
    // 从待审核列表中移除
    const index = pendingProducts.value.findIndex(item => item.id === id)
    if (index > -1) {
      pendingProducts.value.splice(index, 1)
    }
    // 刷新统计数据
    await fetchStats()
  } catch (error) {
    console.error('审核拒绝失败:', error)
    ElMessage.error('审核操作失败')
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    loading.stats = true
    const response = await getDashboardStats()
    if (response && response.data) {
      Object.assign(stats, response.data)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.stats = false
  }
}

// 获取最新用户
const fetchRecentUsers = async () => {
  try {
    loading.users = true
    const response = await getRecentUsers()
    if (response && response.data) {
      recentUsers.value = response.data
    }
  } catch (error) {
    console.error('获取最新用户失败:', error)
    ElMessage.error('获取最新用户失败')
  } finally {
    loading.users = false
  }
}

// 获取待审核商品
const fetchPendingProducts = async () => {
  try {
    loading.products = true
    const response = await getPendingProducts()
    if (response && response.data) {
      pendingProducts.value = response.data
    }
  } catch (error) {
    console.error('获取待审核商品失败:', error)
    ElMessage.error('获取待审核商品失败')
  } finally {
    loading.products = false
  }
}

// 获取系统状态
const fetchSystemStatus = async () => {
  try {
    const response = await getSystemStatus()
    if (response && response.data) {
      Object.assign(systemStatus, response.data)
    }
  } catch (error) {
    console.error('获取系统状态失败:', error)
    ElMessage.error('获取系统状态失败')
  }
}

// 获取图表数据
const fetchChartData = async () => {
  try {
    loading.charts = true
    
    // 获取用户增长趋势
    const userGrowthResponse = await getUserGrowthTrend()
    if (userGrowthResponse && userGrowthResponse.data) {
      chartData.userGrowth = userGrowthResponse.data
      updateUserChart()
    }
    
    // 获取分类统计
    const categoryResponse = await getCategoryStats()
    if (categoryResponse && categoryResponse.data) {
      chartData.categoryDistribution = categoryResponse.data
      updateCategoryChart()
    }
  } catch (error) {
    console.error('获取图表数据失败:', error)
    ElMessage.error('获取图表数据失败')
  } finally {
    loading.charts = false
  }
}

// 组件挂载时初始化
onMounted(async () => {
  // 获取数据
  await Promise.all([
    fetchStats(),
    fetchRecentUsers(),
    fetchPendingProducts(),
    fetchSystemStatus()
  ])
  
  // 初始化图表
  await nextTick()
  initUserChart()
  initCategoryChart()
  
  // 获取图表数据
  await fetchChartData()
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    userChart?.resize()
    categoryChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  padding: 24px;
  
  .page-header {
    margin-bottom: 32px;
    
    h1 {
      font-size: 28px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }
    
    p {
      font-size: 16px;
      color: var(--text-secondary);
      margin: 0;
    }
  }
  
  // 统计卡片网格
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
    
    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
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
        
        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.product-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        &.order-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &.revenue-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-number {
          font-size: 32px;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }
        
        .stat-change {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          font-weight: 500;
          
          &.positive {
            color: var(--success-color);
          }
          
          &.negative {
            color: var(--danger-color);
          }
        }
      }
    }
  }
  
  // 图表网格
  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 24px;
    margin-bottom: 32px;
    
    .chart-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }
      
      .chart-container {
        height: 300px;
      }
    }
  }
  
  // 表格网格
  .tables-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 32px;
    
    .table-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }
    }
  }
  
  // 系统状态
  .system-status {
    .status-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      .status-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }
      
      .status-items {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 16px;
        
        .status-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          background: var(--bg-secondary);
          border-radius: 8px;
          
          .status-label {
            font-size: 14px;
            color: var(--text-secondary);
          }
          
          .status-value {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .dashboard-page {
    .charts-grid {
      grid-template-columns: 1fr;
    }
    
    .tables-grid {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
    
    .stats-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }
    
    .charts-grid,
    .tables-grid {
      gap: 16px;
    }
    
    .stat-card,
    .chart-card,
    .table-card,
    .status-card {
      padding: 16px;
    }
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background: var(--bg-secondary);
      color: var(--text-primary);
      font-weight: 600;
    }
  }
  
  .el-table__row {
    &:hover {
      background: var(--bg-secondary);
    }
  }
}

// 按钮样式优化
:deep(.el-button--small) {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}
</style>