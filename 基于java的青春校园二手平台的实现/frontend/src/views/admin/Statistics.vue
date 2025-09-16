<template>
  <div class="statistics-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>统计分析</h2>
      
      <!-- 时间范围选择 -->
      <div class="date-range-selector">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          @change="handleDateRangeChange"
        />
        
        <el-button-group>
          <el-button
            :type="quickRange === 'today' ? 'primary' : 'default'"
            @click="setQuickRange('today')"
          >
            今日
          </el-button>
          <el-button
            :type="quickRange === 'week' ? 'primary' : 'default'"
            @click="setQuickRange('week')"
          >
            本周
          </el-button>
          <el-button
            :type="quickRange === 'month' ? 'primary' : 'default'"
            @click="setQuickRange('month')"
          >
            本月
          </el-button>
          <el-button
            :type="quickRange === 'year' ? 'primary' : 'default'"
            @click="setQuickRange('year')"
          >
            本年
          </el-button>
        </el-button-group>
        
        <el-button type="primary" @click="refreshData">
          <i class="el-icon-refresh"></i>
          刷新数据
        </el-button>
      </div>
    </div>
    
    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <div class="metrics-grid">
        <div class="metric-card">
          <div class="metric-icon user-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="metric-content">
            <div class="metric-value">{{ coreMetrics.totalUsers || 0 }}</div>
            <div class="metric-label">总用户数</div>
            <div class="metric-change" :class="getChangeClass(coreMetrics.userGrowth)">
              <i :class="getChangeIcon(coreMetrics.userGrowth)"></i>
              {{ formatChange(coreMetrics.userGrowth) }}
            </div>
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-icon product-icon">
            <i class="el-icon-goods"></i>
          </div>
          <div class="metric-content">
            <div class="metric-value">{{ coreMetrics.totalProducts || 0 }}</div>
            <div class="metric-label">商品总数</div>
            <div class="metric-change" :class="getChangeClass(coreMetrics.productGrowth)">
              <i :class="getChangeIcon(coreMetrics.productGrowth)"></i>
              {{ formatChange(coreMetrics.productGrowth) }}
            </div>
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-icon order-icon">
            <i class="el-icon-shopping-cart-2"></i>
          </div>
          <div class="metric-content">
            <div class="metric-value">{{ coreMetrics.totalOrders || 0 }}</div>
            <div class="metric-label">订单总数</div>
            <div class="metric-change" :class="getChangeClass(coreMetrics.orderGrowth)">
              <i :class="getChangeIcon(coreMetrics.orderGrowth)"></i>
              {{ formatChange(coreMetrics.orderGrowth) }}
            </div>
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-icon revenue-icon">
            <i class="el-icon-money"></i>
          </div>
          <div class="metric-content">
            <div class="metric-value">¥{{ formatMoney(coreMetrics.totalRevenue) }}</div>
            <div class="metric-label">交易总额</div>
            <div class="metric-change" :class="getChangeClass(coreMetrics.revenueGrowth)">
              <i :class="getChangeIcon(coreMetrics.revenueGrowth)"></i>
              {{ formatChange(coreMetrics.revenueGrowth) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 用户增长趋势 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>用户增长趋势</h3>
              <el-button-group size="small">
                <el-button
                  :type="userChartType === 'daily' ? 'primary' : 'default'"
                  @click="userChartType = 'daily'; loadUserTrendChart()"
                >
                  日增长
                </el-button>
                <el-button
                  :type="userChartType === 'monthly' ? 'primary' : 'default'"
                  @click="userChartType = 'monthly'; loadUserTrendChart()"
                >
                  月增长
                </el-button>
              </el-button-group>
            </div>
            <div ref="userTrendChart" class="chart-container"></div>
          </div>
        </el-col>
        
        <!-- 订单统计 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>订单状态分布</h3>
            </div>
            <div ref="orderStatusChart" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px">
        <!-- 商品分类统计 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>商品分类分布</h3>
            </div>
            <div ref="categoryChart" class="chart-container"></div>
          </div>
        </el-col>
        
        <!-- 交易趋势 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3>交易金额趋势</h3>
            </div>
            <div ref="revenueTrendChart" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 详细数据表格 -->
    <div class="table-section">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 用户统计 -->
        <el-tab-pane label="用户统计" name="users">
          <div class="table-card">
            <div class="table-header">
              <h3>用户详细统计</h3>
              <el-button @click="exportUserData">
                <i class="el-icon-download"></i>
                导出数据
              </el-button>
            </div>
            
            <el-table
              v-loading="userTableLoading"
              :data="userStatistics"
              style="width: 100%"
            >
              <el-table-column prop="date" label="日期" width="120" />
              <el-table-column prop="newUsers" label="新增用户" width="100" />
              <el-table-column prop="activeUsers" label="活跃用户" width="100" />
              <el-table-column prop="totalUsers" label="累计用户" width="100" />
              <el-table-column prop="userRetention" label="用户留存率" width="120">
                <template slot-scope="scope">
                  {{ scope.row.userRetention }}%
                </template>
              </el-table-column>
              <el-table-column prop="avgSessionTime" label="平均会话时长" width="140" />
            </el-table>
            
            <div class="table-pagination">
              <el-pagination
                @size-change="handleUserPageSizeChange"
                @current-change="handleUserPageChange"
                :current-page="userPagination.page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="userPagination.size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="userPagination.total"
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 商品统计 -->
        <el-tab-pane label="商品统计" name="products">
          <div class="table-card">
            <div class="table-header">
              <h3>商品详细统计</h3>
              <el-button @click="exportProductData">
                <i class="el-icon-download"></i>
                导出数据
              </el-button>
            </div>
            
            <el-table
              v-loading="productTableLoading"
              :data="productStatistics"
              style="width: 100%"
            >
              <el-table-column prop="categoryName" label="分类" width="120" />
              <el-table-column prop="totalProducts" label="商品总数" width="100" />
              <el-table-column prop="publishedProducts" label="已发布" width="100" />
              <el-table-column prop="soldProducts" label="已售出" width="100" />
              <el-table-column prop="avgPrice" label="平均价格" width="120">
                <template slot-scope="scope">
                  ¥{{ formatMoney(scope.row.avgPrice) }}
                </template>
              </el-table-column>
              <el-table-column prop="totalViews" label="总浏览量" width="100" />
              <el-table-column prop="conversionRate" label="转化率" width="100">
                <template slot-scope="scope">
                  {{ scope.row.conversionRate }}%
                </template>
              </el-table-column>
            </el-table>
            
            <div class="table-pagination">
              <el-pagination
                @size-change="handleProductPageSizeChange"
                @current-change="handleProductPageChange"
                :current-page="productPagination.page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="productPagination.size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="productPagination.total"
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 订单统计 -->
        <el-tab-pane label="订单统计" name="orders">
          <div class="table-card">
            <div class="table-header">
              <h3>订单详细统计</h3>
              <el-button @click="exportOrderData">
                <i class="el-icon-download"></i>
                导出数据
              </el-button>
            </div>
            
            <el-table
              v-loading="orderTableLoading"
              :data="orderStatistics"
              style="width: 100%"
            >
              <el-table-column prop="date" label="日期" width="120" />
              <el-table-column prop="totalOrders" label="订单总数" width="100" />
              <el-table-column prop="completedOrders" label="已完成" width="100" />
              <el-table-column prop="cancelledOrders" label="已取消" width="100" />
              <el-table-column prop="totalAmount" label="交易金额" width="120">
                <template slot-scope="scope">
                  ¥{{ formatMoney(scope.row.totalAmount) }}
                </template>
              </el-table-column>
              <el-table-column prop="avgOrderValue" label="客单价" width="120">
                <template slot-scope="scope">
                  ¥{{ formatMoney(scope.row.avgOrderValue) }}
                </template>
              </el-table-column>
              <el-table-column prop="completionRate" label="完成率" width="100">
                <template slot-scope="scope">
                  {{ scope.row.completionRate }}%
                </template>
              </el-table-column>
            </el-table>
            
            <div class="table-pagination">
              <el-pagination
                @size-change="handleOrderPageSizeChange"
                @current-change="handleOrderPageChange"
                :current-page="orderPagination.page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="orderPagination.size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="orderPagination.total"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import {
  getCoreMetrics,
  getUserTrendData,
  getOrderStatusData,
  getCategoryDistribution,
  getRevenueTrendData,
  getUserStatistics,
  getProductStatistics,
  getOrderStatistics,
  exportStatisticsData
} from '@/api/statistics'
import * as echarts from 'echarts'
import moment from 'moment'

export default {
  name: 'Statistics',
  data() {
    return {
      dateRange: [],
      quickRange: 'month',
      coreMetrics: {},
      activeTab: 'users',
      userChartType: 'daily',
      
      // 图表实例
      userTrendChart: null,
      orderStatusChart: null,
      categoryChart: null,
      revenueTrendChart: null,
      
      // 表格数据
      userStatistics: [],
      productStatistics: [],
      orderStatistics: [],
      
      // 表格加载状态
      userTableLoading: false,
      productTableLoading: false,
      orderTableLoading: false,
      
      // 分页信息
      userPagination: {
        page: 1,
        size: 20,
        total: 0
      },
      productPagination: {
        page: 1,
        size: 20,
        total: 0
      },
      orderPagination: {
        page: 1,
        size: 20,
        total: 0
      }
    }
  },
  created() {
    this.initDateRange()
    this.loadCoreMetrics()
  },
  mounted() {
    this.initCharts()
    this.loadAllChartData()
    this.loadTableData()
  },
  beforeDestroy() {
    this.destroyCharts()
  },
  methods: {
    initDateRange() {
      const end = moment().format('YYYY-MM-DD')
      const start = moment().subtract(30, 'days').format('YYYY-MM-DD')
      this.dateRange = [start, end]
    },
    
    setQuickRange(range) {
      this.quickRange = range
      let start, end
      
      switch (range) {
        case 'today':
          start = end = moment().format('YYYY-MM-DD')
          break
        case 'week':
          start = moment().startOf('week').format('YYYY-MM-DD')
          end = moment().endOf('week').format('YYYY-MM-DD')
          break
        case 'month':
          start = moment().startOf('month').format('YYYY-MM-DD')
          end = moment().endOf('month').format('YYYY-MM-DD')
          break
        case 'year':
          start = moment().startOf('year').format('YYYY-MM-DD')
          end = moment().endOf('year').format('YYYY-MM-DD')
          break
      }
      
      this.dateRange = [start, end]
      this.refreshData()
    },
    
    handleDateRangeChange() {
      this.quickRange = ''
      this.refreshData()
    },
    
    refreshData() {
      this.loadCoreMetrics()
      this.loadAllChartData()
      this.loadTableData()
    },
    
    async loadCoreMetrics() {
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await getCoreMetrics(params)
        if (res.data.code === 200) {
          this.coreMetrics = res.data.data || {}
        }
      } catch (error) {
        console.error('加载核心指标失败:', error)
      }
    },
    
    initCharts() {
      this.userTrendChart = echarts.init(this.$refs.userTrendChart)
      this.orderStatusChart = echarts.init(this.$refs.orderStatusChart)
      this.categoryChart = echarts.init(this.$refs.categoryChart)
      this.revenueTrendChart = echarts.init(this.$refs.revenueTrendChart)
      
      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },
    
    destroyCharts() {
      if (this.userTrendChart) {
        this.userTrendChart.dispose()
      }
      if (this.orderStatusChart) {
        this.orderStatusChart.dispose()
      }
      if (this.categoryChart) {
        this.categoryChart.dispose()
      }
      if (this.revenueTrendChart) {
        this.revenueTrendChart.dispose()
      }
      
      window.removeEventListener('resize', this.handleResize)
    },
    
    handleResize() {
      this.userTrendChart && this.userTrendChart.resize()
      this.orderStatusChart && this.orderStatusChart.resize()
      this.categoryChart && this.categoryChart.resize()
      this.revenueTrendChart && this.revenueTrendChart.resize()
    },
    
    loadAllChartData() {
      this.loadUserTrendChart()
      this.loadOrderStatusChart()
      this.loadCategoryChart()
      this.loadRevenueTrendChart()
    },
    
    async loadUserTrendChart() {
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1],
          type: this.userChartType
        }
        const res = await getUserTrendData(params)
        if (res.data.code === 200) {
          const data = res.data.data || []
          this.renderUserTrendChart(data)
        }
      } catch (error) {
        console.error('加载用户趋势数据失败:', error)
      }
    },
    
    renderUserTrendChart(data) {
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['新增用户', '活跃用户']
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '新增用户',
            type: 'line',
            data: data.map(item => item.newUsers),
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '活跃用户',
            type: 'line',
            data: data.map(item => item.activeUsers),
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
      
      this.userTrendChart.setOption(option)
    },
    
    async loadOrderStatusChart() {
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await getOrderStatusData(params)
        if (res.data.code === 200) {
          const data = res.data.data || []
          this.renderOrderStatusChart(data)
        }
      } catch (error) {
        console.error('加载订单状态数据失败:', error)
      }
    },
    
    renderOrderStatusChart(data) {
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '订单状态',
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
            data: data
          }
        ]
      }
      
      this.orderStatusChart.setOption(option)
    },
    
    async loadCategoryChart() {
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await getCategoryDistribution(params)
        if (res.data.code === 200) {
          const data = res.data.data || []
          this.renderCategoryChart(data)
        }
      } catch (error) {
        console.error('加载分类分布数据失败:', error)
      }
    },
    
    renderCategoryChart(data) {
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.categoryName)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '商品数量',
            type: 'bar',
            data: data.map(item => item.count),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#87CEEB' }
              ])
            }
          }
        ]
      }
      
      this.categoryChart.setOption(option)
    },
    
    async loadRevenueTrendChart() {
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await getRevenueTrendData(params)
        if (res.data.code === 200) {
          const data = res.data.data || []
          this.renderRevenueTrendChart(data)
        }
      } catch (error) {
        console.error('加载收入趋势数据失败:', error)
      }
    },
    
    renderRevenueTrendChart(data) {
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            return params[0].name + '<br/>' +
                   params[0].marker + params[0].seriesName + ': ¥' + params[0].value.toFixed(2)
          }
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date)
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            name: '交易金额',
            type: 'line',
            data: data.map(item => item.amount),
            smooth: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      }
      
      this.revenueTrendChart.setOption(option)
    },
    
    handleTabClick(tab) {
      if (tab.name !== this.activeTab) {
        this.loadTableData()
      }
    },
    
    loadTableData() {
      switch (this.activeTab) {
        case 'users':
          this.loadUserStatistics()
          break
        case 'products':
          this.loadProductStatistics()
          break
        case 'orders':
          this.loadOrderStatistics()
          break
      }
    },
    
    async loadUserStatistics() {
      this.userTableLoading = true
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1],
          page: this.userPagination.page,
          size: this.userPagination.size
        }
        const res = await getUserStatistics(params)
        if (res.data.code === 200) {
          this.userStatistics = res.data.data.list || []
          this.userPagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载用户统计失败:', error)
      } finally {
        this.userTableLoading = false
      }
    },
    
    async loadProductStatistics() {
      this.productTableLoading = true
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1],
          page: this.productPagination.page,
          size: this.productPagination.size
        }
        const res = await getProductStatistics(params)
        if (res.data.code === 200) {
          this.productStatistics = res.data.data.list || []
          this.productPagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载商品统计失败:', error)
      } finally {
        this.productTableLoading = false
      }
    },
    
    async loadOrderStatistics() {
      this.orderTableLoading = true
      try {
        const params = {
          startDate: this.dateRange[0],
          endDate: this.dateRange[1],
          page: this.orderPagination.page,
          size: this.orderPagination.size
        }
        const res = await getOrderStatistics(params)
        if (res.data.code === 200) {
          this.orderStatistics = res.data.data.list || []
          this.orderPagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载订单统计失败:', error)
      } finally {
        this.orderTableLoading = false
      }
    },
    
    // 分页处理
    handleUserPageSizeChange(size) {
      this.userPagination.size = size
      this.userPagination.page = 1
      this.loadUserStatistics()
    },
    
    handleUserPageChange(page) {
      this.userPagination.page = page
      this.loadUserStatistics()
    },
    
    handleProductPageSizeChange(size) {
      this.productPagination.size = size
      this.productPagination.page = 1
      this.loadProductStatistics()
    },
    
    handleProductPageChange(page) {
      this.productPagination.page = page
      this.loadProductStatistics()
    },
    
    handleOrderPageSizeChange(size) {
      this.orderPagination.size = size
      this.orderPagination.page = 1
      this.loadOrderStatistics()
    },
    
    handleOrderPageChange(page) {
      this.orderPagination.page = page
      this.loadOrderStatistics()
    },
    
    // 数据导出
    async exportUserData() {
      try {
        const params = {
          type: 'users',
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await exportStatisticsData(params)
        if (res.data.code === 200) {
          // 处理文件下载
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `用户统计_${moment().format('YYYY-MM-DD')}.xlsx`
          link.click()
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        }
      } catch (error) {
        console.error('导出用户数据失败:', error)
        this.$message.error('导出失败')
      }
    },
    
    async exportProductData() {
      try {
        const params = {
          type: 'products',
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await exportStatisticsData(params)
        if (res.data.code === 200) {
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `商品统计_${moment().format('YYYY-MM-DD')}.xlsx`
          link.click()
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        }
      } catch (error) {
        console.error('导出商品数据失败:', error)
        this.$message.error('导出失败')
      }
    },
    
    async exportOrderData() {
      try {
        const params = {
          type: 'orders',
          startDate: this.dateRange[0],
          endDate: this.dateRange[1]
        }
        const res = await exportStatisticsData(params)
        if (res.data.code === 200) {
          const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
          const url = window.URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `订单统计_${moment().format('YYYY-MM-DD')}.xlsx`
          link.click()
          window.URL.revokeObjectURL(url)
          this.$message.success('导出成功')
        }
      } catch (error) {
        console.error('导出订单数据失败:', error)
        this.$message.error('导出失败')
      }
    },
    
    // 工具方法
    getChangeClass(value) {
      if (!value) return ''
      return value > 0 ? 'positive' : value < 0 ? 'negative' : ''
    },
    
    getChangeIcon(value) {
      if (!value) return 'el-icon-minus'
      return value > 0 ? 'el-icon-top' : 'el-icon-bottom'
    },
    
    formatChange(value) {
      if (!value) return '0%'
      const sign = value > 0 ? '+' : ''
      return `${sign}${value.toFixed(1)}%`
    },
    
    formatMoney(value) {
      if (!value) return '0.00'
      return parseFloat(value).toFixed(2)
    }
  }
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 10px;
  padding: 25px 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.date-range-selector {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 指标卡片 */
.metrics-section {
  margin-bottom: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.metric-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s;
}

.metric-card:hover {
  transform: translateY(-2px);
}

.metric-icon {
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

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.metric-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.metric-change.positive {
  color: #67C23A;
}

.metric-change.negative {
  color: #F56C6C;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.chart-container {
  width: 100%;
  height: 320px;
}

/* 表格区域 */
.table-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table-card {
  margin-top: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.table-pagination {
  margin-top: 20px;
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .date-range-selector {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .metric-card {
    padding: 20px;
  }
  
  .metric-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .metric-value {
    font-size: 24px;
  }
  
  .chart-card {
    height: 300px;
  }
  
  .chart-container {
    height: 220px;
  }
  
  .date-range-selector {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 480px) {
  .metric-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .table-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
}
</style>