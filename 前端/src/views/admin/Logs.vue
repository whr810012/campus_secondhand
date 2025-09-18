<template>
  <div class="logs-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">操作日志</h1>
        <p class="page-description">查看系统操作日志，监控用户行为和系统状态</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="exportLogs">
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
        <el-button type="danger" @click="clearOldLogs">
          <el-icon><Delete /></el-icon>
          清理日志
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon primary">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalLogs }}</div>
          <div class="stat-label">总日志数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.todayUsers }}</div>
          <div class="stat-label">今日活跃用户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.errorLogs }}</div>
          <div class="stat-label">错误日志</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><Operation /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.todayOperations }}</div>
          <div class="stat-label">今日操作数</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户名、操作内容..."
          clearable
          @input="handleSearch"
          style="width: 300px;"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="moduleFilter" placeholder="模块筛选" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="用户管理" value="user" />
          <el-option label="商品管理" value="product" />
          <el-option label="订单管理" value="order" />
          <el-option label="分类管理" value="category" />
          <el-option label="公告管理" value="announcement" />
          <el-option label="系统设置" value="system" />
        </el-select>
        <el-select v-model="actionFilter" placeholder="操作类型" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="创建" value="create" />
          <el-option label="更新" value="update" />
          <el-option label="删除" value="delete" />
          <el-option label="查看" value="view" />
          <el-option label="登录" value="login" />
          <el-option label="登出" value="logout" />
        </el-select>
        <el-select v-model="levelFilter" placeholder="日志级别" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="信息" value="info" />
          <el-option label="警告" value="warning" />
          <el-option label="错误" value="error" />
          <el-option label="调试" value="debug" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 350px;"
        />
      </div>
      <div class="filter-right">
        <el-button @click="resetFilters">重置筛选</el-button>
        <el-button type="primary" @click="refreshLogs">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="table-container">
      <el-table
        :data="filteredLogs"
        v-loading="loading"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="createdAt" label="时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="24" :src="row.avatar" />
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag :type="getModuleTagType(row.module)" size="small">
              {{ getModuleLabel(row.module) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="80">
          <template #default="{ row }">
            <el-tag :type="getActionTagType(row.action)" size="small">
              {{ getActionLabel(row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200">
          <template #default="{ row }">
            <span class="description">{{ row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)" size="small">
              {{ getLevelLabel(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="userAgent" label="用户代理" width="150">
          <template #default="{ row }">
            <el-tooltip :content="row.userAgent" placement="top">
              <span class="user-agent">{{ getBrowserInfo(row.userAgent) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时" width="80">
          <template #default="{ row }">
            <span class="duration">{{ row.duration }}ms</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewLogDetail(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.level === 'error'" 
              type="danger" 
              size="small" 
              @click="reportError(row)"
            >
              上报
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[20, 50, 100, 200]"
        :total="totalLogs"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="日志详情"
      width="800px"
    >
      <div class="log-detail" v-if="selectedLog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="时间">
            {{ formatDate(selectedLog.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            <div class="user-info">
              <el-avatar :size="24" :src="selectedLog.avatar" />
              <span>{{ selectedLog.username }}</span>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="模块">
            <el-tag :type="getModuleTagType(selectedLog.module)" size="small">
              {{ getModuleLabel(selectedLog.module) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作">
            <el-tag :type="getActionTagType(selectedLog.action)" size="small">
              {{ getActionLabel(selectedLog.action) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="级别">
            <el-tag :type="getLevelTagType(selectedLog.level)" size="small">
              {{ getLevelLabel(selectedLog.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="耗时">
            {{ selectedLog.duration }}ms
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">
            {{ selectedLog.ip }}
          </el-descriptions-item>
          <el-descriptions-item label="地理位置">
            {{ selectedLog.location || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="操作描述" :span="2">
            {{ selectedLog.description }}
          </el-descriptions-item>
          <el-descriptions-item label="用户代理" :span="2">
            {{ selectedLog.userAgent }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="request-info" v-if="selectedLog.requestData">
          <h4>请求信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方法">
              <el-tag :type="getMethodTagType(selectedLog.requestData.method)" size="small">
                {{ selectedLog.requestData.method }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="请求URL">
              {{ selectedLog.requestData.url }}
            </el-descriptions-item>
            <el-descriptions-item label="请求参数" v-if="selectedLog.requestData.params">
              <pre class="json-data">{{ JSON.stringify(selectedLog.requestData.params, null, 2) }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="响应状态">
              <el-tag :type="getStatusTagType(selectedLog.requestData.status)" size="small">
                {{ selectedLog.requestData.status }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="error-info" v-if="selectedLog.level === 'error' && selectedLog.errorData">
          <h4>错误信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="错误类型">
              {{ selectedLog.errorData.type }}
            </el-descriptions-item>
            <el-descriptions-item label="错误消息">
              {{ selectedLog.errorData.message }}
            </el-descriptions-item>
            <el-descriptions-item label="错误堆栈" v-if="selectedLog.errorData.stack">
              <pre class="error-stack">{{ selectedLog.errorData.stack }}</pre>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Delete,
  Search,
  Refresh,
  Document,
  User,
  Warning,
  Operation
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const moduleFilter = ref('')
const actionFilter = ref('')
const levelFilter = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const totalLogs = ref(0)
const showDetailDialog = ref(false)
const selectedLog = ref(null)

// 统计数据
const stats = reactive({
  totalLogs: 15420,
  todayUsers: 342,
  errorLogs: 23,
  todayOperations: 1256
})

// 日志数据
const logs = ref([
  {
    id: 1,
    createdAt: '2024-02-20 14:30:25',
    username: '张三',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'user',
    action: 'create',
    description: '创建新用户账户',
    level: 'info',
    ip: '192.168.1.100',
    location: '北京市海淀区',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    duration: 245,
    requestData: {
      method: 'POST',
      url: '/api/users',
      params: { name: '张三', email: 'zhangsan@example.com' },
      status: 200
    }
  },
  {
    id: 2,
    createdAt: '2024-02-20 14:25:18',
    username: '李四',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'product',
    action: 'update',
    description: '更新商品信息：iPhone 15 Pro',
    level: 'info',
    ip: '192.168.1.101',
    location: '上海市浦东新区',
    userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
    duration: 156,
    requestData: {
      method: 'PUT',
      url: '/api/products/123',
      params: { title: 'iPhone 15 Pro', price: 8999 },
      status: 200
    }
  },
  {
    id: 3,
    createdAt: '2024-02-20 14:20:42',
    username: '王五',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'order',
    action: 'delete',
    description: '删除订单：ORD20240220001',
    level: 'warning',
    ip: '192.168.1.102',
    location: '广州市天河区',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    duration: 89,
    requestData: {
      method: 'DELETE',
      url: '/api/orders/ORD20240220001',
      status: 200
    }
  },
  {
    id: 4,
    createdAt: '2024-02-20 14:15:33',
    username: '赵六',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'system',
    action: 'login',
    description: '用户登录系统',
    level: 'info',
    ip: '192.168.1.103',
    location: '深圳市南山区',
    userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15',
    duration: 1234,
    requestData: {
      method: 'POST',
      url: '/api/auth/login',
      status: 200
    }
  },
  {
    id: 5,
    createdAt: '2024-02-20 14:10:15',
    username: '系统',
    avatar: '',
    module: 'system',
    action: 'create',
    description: '系统自动备份数据库',
    level: 'info',
    ip: '127.0.0.1',
    location: '本地服务器',
    userAgent: 'System/1.0',
    duration: 5678,
    requestData: {
      method: 'POST',
      url: '/api/system/backup',
      status: 200
    }
  },
  {
    id: 6,
    createdAt: '2024-02-20 14:05:28',
    username: '钱七',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'product',
    action: 'view',
    description: '查看商品详情页面',
    level: 'debug',
    ip: '192.168.1.104',
    location: '杭州市西湖区',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    duration: 45,
    requestData: {
      method: 'GET',
      url: '/api/products/456',
      status: 200
    }
  },
  {
    id: 7,
    createdAt: '2024-02-20 14:00:12',
    username: '孙八',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    module: 'user',
    action: 'update',
    description: '更新用户个人信息失败',
    level: 'error',
    ip: '192.168.1.105',
    location: '成都市锦江区',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    duration: 2345,
    requestData: {
      method: 'PUT',
      url: '/api/users/profile',
      status: 500
    },
    errorData: {
      type: 'ValidationError',
      message: '用户邮箱格式不正确',
      stack: 'ValidationError: 用户邮箱格式不正确\n    at validateEmail (/app/utils/validator.js:15:11)\n    at updateProfile (/app/controllers/user.js:45:7)'
    }
  }
])

// 计算属性
const filteredLogs = computed(() => {
  let result = logs.value
  
  if (searchQuery.value) {
    result = result.filter(item => 
      item.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (moduleFilter.value) {
    result = result.filter(item => item.module === moduleFilter.value)
  }
  
  if (actionFilter.value) {
    result = result.filter(item => item.action === actionFilter.value)
  }
  
  if (levelFilter.value) {
    result = result.filter(item => item.level === levelFilter.value)
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    result = result.filter(item => {
      const itemTime = new Date(item.createdAt).getTime()
      const startTime = new Date(dateRange.value[0]).getTime()
      const endTime = new Date(dateRange.value[1]).getTime()
      return itemTime >= startTime && itemTime <= endTime
    })
  }
  
  return result
})

// 方法
const fetchLogs = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    totalLogs.value = logs.value.length
  } catch (error) {
    ElMessage.error('获取日志数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchQuery.value = ''
  moduleFilter.value = ''
  actionFilter.value = ''
  levelFilter.value = ''
  dateRange.value = []
  currentPage.value = 1
}

const refreshLogs = () => {
  fetchLogs()
  ElMessage.success('日志数据已刷新')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchLogs()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchLogs()
}

const viewLogDetail = (log) => {
  selectedLog.value = log
  showDetailDialog.value = true
}

const reportError = async (log) => {
  try {
    await ElMessageBox.confirm(
      `确定要上报错误日志 "${log.description}" 吗？`,
      '确认上报',
      { type: 'warning' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('错误日志已上报给技术团队')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('上报失败')
    }
  }
}

const exportLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要导出当前筛选条件下的日志数据吗？',
      '确认导出',
      { type: 'info' }
    )
    
    // 模拟导出功能
    ElMessage.success('日志数据导出成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('导出失败')
    }
  }
}

const clearOldLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清理30天前的日志数据吗？此操作不可恢复。',
      '确认清理',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('旧日志数据清理成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清理失败')
    }
  }
}

// 工具函数
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getModuleTagType = (module) => {
  const types = {
    user: 'primary',
    product: 'success',
    order: 'warning',
    category: 'info',
    announcement: 'primary',
    system: 'danger'
  }
  return types[module] || 'info'
}

const getModuleLabel = (module) => {
  const labels = {
    user: '用户管理',
    product: '商品管理',
    order: '订单管理',
    category: '分类管理',
    announcement: '公告管理',
    system: '系统设置'
  }
  return labels[module] || module
}

const getActionTagType = (action) => {
  const types = {
    create: 'success',
    update: 'primary',
    delete: 'danger',
    view: 'info',
    login: 'success',
    logout: 'warning'
  }
  return types[action] || 'info'
}

const getActionLabel = (action) => {
  const labels = {
    create: '创建',
    update: '更新',
    delete: '删除',
    view: '查看',
    login: '登录',
    logout: '登出'
  }
  return labels[action] || action
}

const getLevelTagType = (level) => {
  const types = {
    info: 'primary',
    warning: 'warning',
    error: 'danger',
    debug: 'info'
  }
  return types[level] || 'info'
}

const getLevelLabel = (level) => {
  const labels = {
    info: '信息',
    warning: '警告',
    error: '错误',
    debug: '调试'
  }
  return labels[level] || level
}

const getMethodTagType = (method) => {
  const types = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return types[method] || 'info'
}

const getStatusTagType = (status) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 300 && status < 400) return 'info'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  return 'info'
}

const getBrowserInfo = (userAgent) => {
  if (userAgent.includes('Chrome')) return 'Chrome'
  if (userAgent.includes('Firefox')) return 'Firefox'
  if (userAgent.includes('Safari')) return 'Safari'
  if (userAgent.includes('Edge')) return 'Edge'
  if (userAgent.includes('iPhone')) return 'iPhone'
  if (userAgent.includes('Android')) return 'Android'
  if (userAgent.includes('System')) return '系统'
  return '未知'
}

// 生命周期
onMounted(() => {
  fetchLogs()
})
</script>

<style lang="scss" scoped>
.logs-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      .page-title {
        margin: 0 0 8px 0;
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }

      .page-description {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-2px);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;

        &.primary {
          background: #e3f2fd;
          color: #1976d2;
        }

        &.success {
          background: #e8f5e8;
          color: #4caf50;
        }

        &.warning {
          background: #fff3e0;
          color: #ff9800;
        }

        &.info {
          background: #e1f5fe;
          color: #00bcd4;
        }
      }

      .stat-content {
        .stat-number {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          line-height: 1;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }

  .filter-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .filter-left {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }

    .filter-right {
      display: flex;
      gap: 12px;
    }
  }

  .table-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .username {
        font-weight: 500;
      }
    }

    .description {
      color: #606266;
    }

    .user-agent {
      color: #909399;
      font-size: 12px;
    }

    .duration {
      color: #409eff;
      font-weight: 500;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .log-detail {
    .request-info,
    .error-info {
      margin-top: 20px;

      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 16px;
      }
    }

    .json-data {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 4px;
      font-size: 12px;
      color: #606266;
      max-height: 200px;
      overflow-y: auto;
    }

    .error-stack {
      background: #fef0f0;
      padding: 12px;
      border-radius: 4px;
      font-size: 12px;
      color: #f56c6c;
      max-height: 300px;
      overflow-y: auto;
      border: 1px solid #fbc4c4;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .logs-management {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .filter-section {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .filter-left,
      .filter-right {
        justify-content: center;
      }
    }

    .stats-cards {
      grid-template-columns: 1fr;
    }
  }
}
</style>