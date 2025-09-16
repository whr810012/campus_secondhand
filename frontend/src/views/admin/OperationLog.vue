<template>
  <div class="operation-log">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>操作日志</h1>
      <p>查看管理员操作记录和系统日志</p>
    </div>

    <!-- 日志统计 -->
    <div class="log-stats">
      <div class="stat-card">
        <div class="stat-icon total-icon">
          <i class="fas fa-list"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalLogs }}</h3>
          <p>总日志数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon today-icon">
          <i class="fas fa-calendar-day"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.todayLogs }}</h3>
          <p>今日操作</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon admin-icon">
          <i class="fas fa-user-shield"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.activeAdmins }}</h3>
          <p>活跃管理员</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon error-icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.errorLogs }}</h3>
          <p>异常操作</p>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <select v-model="filters.adminId" @change="fetchLogs">
          <option value="">全部管理员</option>
          <option 
            v-for="admin in admins" 
            :key="admin.id" 
            :value="admin.id"
          >
            {{ admin.nickname }} ({{ admin.username }})
          </option>
        </select>
        
        <select v-model="filters.module" @change="fetchLogs">
          <option value="">全部模块</option>
          <option value="USER">用户管理</option>
          <option value="PRODUCT">商品管理</option>
          <option value="ORDER">订单管理</option>
          <option value="CATEGORY">分类管理</option>
          <option value="ANNOUNCEMENT">公告管理</option>
          <option value="AUDIT">内容审核</option>
          <option value="SYSTEM">系统设置</option>
        </select>
        
        <select v-model="filters.action" @change="fetchLogs">
          <option value="">全部操作</option>
          <option value="CREATE">创建</option>
          <option value="UPDATE">更新</option>
          <option value="DELETE">删除</option>
          <option value="APPROVE">审核通过</option>
          <option value="REJECT">审核拒绝</option>
          <option value="BAN">封禁</option>
          <option value="UNBAN">解封</option>
          <option value="LOGIN">登录</option>
          <option value="LOGOUT">登出</option>
        </select>
        
        <select v-model="filters.status" @change="fetchLogs">
          <option value="">全部状态</option>
          <option value="SUCCESS">成功</option>
          <option value="FAILED">失败</option>
          <option value="ERROR">异常</option>
        </select>
        
        <input 
          type="date" 
          v-model="filters.startDate" 
          @change="fetchLogs"
        />
        <span>至</span>
        <input 
          type="date" 
          v-model="filters.endDate" 
          @change="fetchLogs"
        />
        
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="搜索操作描述、IP地址" 
          @keyup.enter="fetchLogs"
        />
        
        <button @click="fetchLogs" class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
      
      <div class="filter-right">
        <button @click="exportLogs" class="export-btn">
          <i class="fas fa-download"></i>
          导出日志
        </button>
        <button @click="clearLogs" class="clear-btn">
          <i class="fas fa-trash"></i>
          清理日志
        </button>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="log-list">
      <div class="list-header">
        <span>共 {{ pagination.total }} 条日志</span>
        <div class="view-options">
          <button 
            @click="viewMode = 'table'" 
            class="view-btn"
            :class="{ active: viewMode === 'table' }"
          >
            <i class="fas fa-table"></i>
            表格视图
          </button>
          <button 
            @click="viewMode = 'timeline'" 
            class="view-btn"
            :class="{ active: viewMode === 'timeline' }"
          >
            <i class="fas fa-stream"></i>
            时间线视图
          </button>
        </div>
      </div>
      
      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="table-view">
        <table class="logs-table">
          <thead>
            <tr>
              <th>时间</th>
              <th>管理员</th>
              <th>模块</th>
              <th>操作</th>
              <th>描述</th>
              <th>状态</th>
              <th>IP地址</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="log in logs" 
              :key="log.id" 
              class="log-row"
              :class="log.status.toLowerCase()"
            >
              <td>
                <div class="time-info">
                  <div class="date">{{ formatDate(log.createdAt) }}</div>
                  <div class="time">{{ formatTime(log.createdAt) }}</div>
                </div>
              </td>
              <td>
                <div class="admin-info">
                  <img 
                    :src="log.admin.avatar || '/default-avatar.png'" 
                    :alt="log.admin.nickname" 
                    class="admin-avatar"
                  />
                  <div class="admin-details">
                    <div class="admin-name">{{ log.admin.nickname }}</div>
                    <div class="admin-username">{{ log.admin.username }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span class="module-badge" :class="log.module.toLowerCase()">
                  {{ getModuleText(log.module) }}
                </span>
              </td>
              <td>
                <span class="action-badge" :class="log.action.toLowerCase()">
                  {{ getActionText(log.action) }}
                </span>
              </td>
              <td>
                <div class="description">
                  {{ log.description }}
                </div>
              </td>
              <td>
                <span class="status-badge" :class="log.status.toLowerCase()">
                  <i class="fas" :class="getStatusIcon(log.status)"></i>
                  {{ getStatusText(log.status) }}
                </span>
              </td>
              <td>
                <div class="ip-info">
                  <div class="ip-address">{{ log.ipAddress }}</div>
                  <div class="user-agent" v-if="log.userAgent">
                    {{ getBrowserInfo(log.userAgent) }}
                  </div>
                </div>
              </td>
              <td>
                <button @click="viewLogDetail(log)" class="action-btn view">
                  <i class="fas fa-eye"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 时间线视图 -->
      <div v-if="viewMode === 'timeline'" class="timeline-view">
        <div class="timeline">
          <div 
            v-for="log in logs" 
            :key="log.id" 
            class="timeline-item"
            :class="log.status.toLowerCase()"
          >
            <div class="timeline-marker">
              <div class="marker-icon" :class="log.status.toLowerCase()">
                <i class="fas" :class="getStatusIcon(log.status)"></i>
              </div>
              <div class="marker-time">{{ formatTime(log.createdAt) }}</div>
            </div>
            
            <div class="timeline-content">
              <div class="log-header">
                <div class="log-info">
                  <h4>{{ log.description }}</h4>
                  <div class="log-meta">
                    <span class="module-badge" :class="log.module.toLowerCase()">
                      {{ getModuleText(log.module) }}
                    </span>
                    <span class="action-badge" :class="log.action.toLowerCase()">
                      {{ getActionText(log.action) }}
                    </span>
                    <span class="status-badge" :class="log.status.toLowerCase()">
                      {{ getStatusText(log.status) }}
                    </span>
                  </div>
                </div>
                
                <div class="admin-info">
                  <img 
                    :src="log.admin.avatar || '/default-avatar.png'" 
                    :alt="log.admin.nickname" 
                    class="admin-avatar"
                  />
                  <div class="admin-details">
                    <div class="admin-name">{{ log.admin.nickname }}</div>
                    <div class="admin-username">{{ log.admin.username }}</div>
                  </div>
                </div>
              </div>
              
              <div class="log-details" v-if="log.details">
                <div class="details-content">
                  {{ log.details }}
                </div>
              </div>
              
              <div class="log-footer">
                <div class="ip-info">
                  <i class="fas fa-map-marker-alt"></i>
                  <span>{{ log.ipAddress }}</span>
                </div>
                <div class="browser-info" v-if="log.userAgent">
                  <i class="fas fa-globe"></i>
                  <span>{{ getBrowserInfo(log.userAgent) }}</span>
                </div>
                <button @click="viewLogDetail(log)" class="detail-btn">
                  <i class="fas fa-info-circle"></i>
                  详情
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button 
        @click="changePage(pagination.current - 1)" 
        :disabled="pagination.current <= 1"
        class="page-btn"
      >
        上一页
      </button>
      
      <span class="page-info">
        第 {{ pagination.current }} 页，共 {{ pagination.pages }} 页
      </span>
      
      <button 
        @click="changePage(pagination.current + 1)" 
        :disabled="pagination.current >= pagination.pages"
        class="page-btn"
      >
        下一页
      </button>
    </div>

    <!-- 日志详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content log-detail-modal" @click.stop>
        <div class="modal-header">
          <h3>操作日志详情</h3>
          <button @click="closeDetailModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedLog">
          <div class="log-detail">
            <div class="detail-section">
              <h4>基本信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>操作时间：</label>
                  <span>{{ formatDateTime(selectedLog.createdAt) }}</span>
                </div>
                <div class="detail-item">
                  <label>操作管理员：</label>
                  <span>{{ selectedLog.admin.nickname }} ({{ selectedLog.admin.username }})</span>
                </div>
                <div class="detail-item">
                  <label>操作模块：</label>
                  <span>{{ getModuleText(selectedLog.module) }}</span>
                </div>
                <div class="detail-item">
                  <label>操作类型：</label>
                  <span>{{ getActionText(selectedLog.action) }}</span>
                </div>
                <div class="detail-item">
                  <label>操作状态：</label>
                  <span class="status-badge" :class="selectedLog.status.toLowerCase()">
                    <i class="fas" :class="getStatusIcon(selectedLog.status)"></i>
                    {{ getStatusText(selectedLog.status) }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>IP地址：</label>
                  <span>{{ selectedLog.ipAddress }}</span>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4>操作描述</h4>
              <div class="description-content">
                {{ selectedLog.description }}
              </div>
            </div>
            
            <div class="detail-section" v-if="selectedLog.details">
              <h4>详细信息</h4>
              <div class="details-content">
                <pre>{{ selectedLog.details }}</pre>
              </div>
            </div>
            
            <div class="detail-section" v-if="selectedLog.requestData">
              <h4>请求数据</h4>
              <div class="request-data">
                <pre>{{ JSON.stringify(selectedLog.requestData, null, 2) }}</pre>
              </div>
            </div>
            
            <div class="detail-section" v-if="selectedLog.responseData">
              <h4>响应数据</h4>
              <div class="response-data">
                <pre>{{ JSON.stringify(selectedLog.responseData, null, 2) }}</pre>
              </div>
            </div>
            
            <div class="detail-section" v-if="selectedLog.userAgent">
              <h4>环境信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>浏览器：</label>
                  <span>{{ getBrowserInfo(selectedLog.userAgent) }}</span>
                </div>
                <div class="detail-item">
                  <label>User Agent：</label>
                  <span class="user-agent">{{ selectedLog.userAgent }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 清理日志确认弹窗 -->
    <div v-if="showClearModal" class="modal-overlay" @click="closeClearModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>清理日志</h3>
          <button @click="closeClearModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="clear-options">
            <h5>请选择清理范围</h5>
            <label class="clear-option">
              <input 
                type="radio" 
                value="7" 
                v-model="clearDays"
              />
              <span>清理7天前的日志</span>
            </label>
            <label class="clear-option">
              <input 
                type="radio" 
                value="30" 
                v-model="clearDays"
              />
              <span>清理30天前的日志</span>
            </label>
            <label class="clear-option">
              <input 
                type="radio" 
                value="90" 
                v-model="clearDays"
              />
              <span>清理90天前的日志</span>
            </label>
            <label class="clear-option">
              <input 
                type="radio" 
                value="all" 
                v-model="clearDays"
              />
              <span>清理所有日志（危险操作）</span>
            </label>
          </div>
          
          <div class="warning-message">
            <i class="fas fa-exclamation-triangle"></i>
            <p>警告：清理操作不可恢复，请谨慎操作！</p>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeClearModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="confirmClearLogs" class="modal-btn danger">
            确认清理
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

export default {
  name: 'OperationLog',
  setup() {
    const loading = ref(false)
    const viewMode = ref('table')
    const logs = ref([])
    const admins = ref([])
    const selectedLog = ref(null)
    const showDetailModal = ref(false)
    const showClearModal = ref(false)
    const clearDays = ref('30')
    
    const stats = reactive({
      totalLogs: 0,
      todayLogs: 0,
      activeAdmins: 0,
      errorLogs: 0
    })
    
    const filters = reactive({
      adminId: '',
      module: '',
      action: '',
      status: '',
      startDate: '',
      endDate: '',
      keyword: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 20,
      total: 0,
      pages: 0
    })

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/logs/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取管理员列表
    const fetchAdmins = async () => {
      try {
        const response = await api.get('/admin/users/admins')
        admins.value = response.data
      } catch (error) {
        console.error('获取管理员列表失败:', error)
      }
    }

    // 获取日志列表
    const fetchLogs = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...filters
        }
        
        const response = await api.get('/admin/logs', { params })
        logs.value = response.data.records
        pagination.total = response.data.total
        pagination.pages = response.data.pages
      } catch (error) {
        console.error('获取日志列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 查看日志详情
    const viewLogDetail = (log) => {
      selectedLog.value = log
      showDetailModal.value = true
    }

    // 导出日志
    const exportLogs = async () => {
      try {
        const response = await api.get('/admin/logs/export', {
          params: filters,
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `操作日志_${new Date().toISOString().slice(0, 10)}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
        alert('导出失败，请重试')
      }
    }

    // 清理日志
    const clearLogs = () => {
      showClearModal.value = true
    }

    // 确认清理日志
    const confirmClearLogs = async () => {
      if (!confirm('确定要清理日志吗？此操作不可恢复！')) return
      
      try {
        await api.delete('/admin/logs/clear', {
          data: { days: clearDays.value }
        })
        
        alert('日志清理成功')
        await fetchLogs()
        await fetchStats()
        closeClearModal()
      } catch (error) {
        console.error('清理日志失败:', error)
        alert('清理日志失败，请重试')
      }
    }

    // 关闭弹窗
    const closeDetailModal = () => {
      showDetailModal.value = false
      selectedLog.value = null
    }

    const closeClearModal = () => {
      showClearModal.value = false
      clearDays.value = '30'
    }

    // 分页
    const changePage = (page) => {
      pagination.current = page
      fetchLogs()
    }

    // 获取模块文本
    const getModuleText = (module) => {
      const moduleMap = {
        USER: '用户管理',
        PRODUCT: '商品管理',
        ORDER: '订单管理',
        CATEGORY: '分类管理',
        ANNOUNCEMENT: '公告管理',
        AUDIT: '内容审核',
        SYSTEM: '系统设置'
      }
      return moduleMap[module] || module
    }

    // 获取操作文本
    const getActionText = (action) => {
      const actionMap = {
        CREATE: '创建',
        UPDATE: '更新',
        DELETE: '删除',
        APPROVE: '审核通过',
        REJECT: '审核拒绝',
        BAN: '封禁',
        UNBAN: '解封',
        LOGIN: '登录',
        LOGOUT: '登出'
      }
      return actionMap[action] || action
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        SUCCESS: '成功',
        FAILED: '失败',
        ERROR: '异常'
      }
      return statusMap[status] || status
    }

    // 获取状态图标
    const getStatusIcon = (status) => {
      const iconMap = {
        SUCCESS: 'fa-check-circle',
        FAILED: 'fa-times-circle',
        ERROR: 'fa-exclamation-triangle'
      }
      return iconMap[status] || 'fa-info-circle'
    }

    // 获取浏览器信息
    const getBrowserInfo = (userAgent) => {
      if (!userAgent) return '未知'
      
      if (userAgent.includes('Chrome')) return 'Chrome'
      if (userAgent.includes('Firefox')) return 'Firefox'
      if (userAgent.includes('Safari')) return 'Safari'
      if (userAgent.includes('Edge')) return 'Edge'
      if (userAgent.includes('Opera')) return 'Opera'
      
      return '其他浏览器'
    }

    // 格式化日期
    const formatDate = (time) => {
      return new Date(time).toLocaleDateString('zh-CN')
    }

    // 格式化时间
    const formatTime = (time) => {
      return new Date(time).toLocaleTimeString('zh-CN')
    }

    // 格式化日期时间
    const formatDateTime = (time) => {
      return new Date(time).toLocaleString('zh-CN')
    }

    onMounted(() => {
      fetchStats()
      fetchAdmins()
      fetchLogs()
    })

    return {
      loading,
      viewMode,
      logs,
      admins,
      selectedLog,
      showDetailModal,
      showClearModal,
      clearDays,
      stats,
      filters,
      pagination,
      fetchLogs,
      viewLogDetail,
      exportLogs,
      clearLogs,
      confirmClearLogs,
      closeDetailModal,
      closeClearModal,
      changePage,
      getModuleText,
      getActionText,
      getStatusText,
      getStatusIcon,
      getBrowserInfo,
      formatDate,
      formatTime,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.operation-log {
  padding: 2rem;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.log-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.total-icon {
  background: #007bff;
}

.today-icon {
  background: #28a745;
}

.admin-icon {
  background: #17a2b8;
}

.error-icon {
  background: #dc3545;
}

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-left {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-left select,
.filter-left input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn,
.export-btn,
.clear-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.search-btn {
  background: #007bff;
  color: white;
}

.export-btn {
  background: #6c757d;
  color: white;
}

.clear-btn {
  background: #dc3545;
  color: white;
}

.filter-right {
  display: flex;
  gap: 0.5rem;
}

.log-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.view-options {
  display: flex;
  gap: 0.5rem;
}

.view-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.view-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

/* 表格视图样式 */
.table-view {
  overflow-x: auto;
}

.logs-table {
  width: 100%;
  border-collapse: collapse;
}

.logs-table th,
.logs-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.logs-table th {
  background: #f8f9fa;
  font-weight: 500;
  color: #333;
}

.log-row:hover {
  background-color: #f8f9fa;
}

.log-row.success {
  border-left: 4px solid #28a745;
}

.log-row.failed {
  border-left: 4px solid #ffc107;
}

.log-row.error {
  border-left: 4px solid #dc3545;
}

.time-info {
  text-align: center;
}

.time-info .date {
  font-size: 0.9rem;
  color: #333;
  margin-bottom: 0.25rem;
}

.time-info .time {
  font-size: 0.8rem;
  color: #666;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.admin-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.admin-details .admin-name {
  font-size: 0.9rem;
  color: #333;
  margin-bottom: 0.25rem;
}

.admin-details .admin-username {
  font-size: 0.8rem;
  color: #666;
}

.module-badge,
.action-badge,
.status-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.module-badge.user {
  background: #e3f2fd;
  color: #1976d2;
}

.module-badge.product {
  background: #f3e5f5;
  color: #7b1fa2;
}

.module-badge.order {
  background: #e8f5e8;
  color: #388e3c;
}

.module-badge.category {
  background: #fff3e0;
  color: #f57c00;
}

.module-badge.announcement {
  background: #fce4ec;
  color: #c2185b;
}

.module-badge.audit {
  background: #e0f2f1;
  color: #00695c;
}

.module-badge.system {
  background: #f1f8e9;
  color: #558b2f;
}

.action-badge.create {
  background: #e8f5e8;
  color: #388e3c;
}

.action-badge.update {
  background: #e3f2fd;
  color: #1976d2;
}

.action-badge.delete {
  background: #ffebee;
  color: #d32f2f;
}

.action-badge.approve {
  background: #e8f5e8;
  color: #388e3c;
}

.action-badge.reject {
  background: #fff3e0;
  color: #f57c00;
}

.action-badge.ban {
  background: #ffebee;
  color: #d32f2f;
}

.action-badge.unban {
  background: #e8f5e8;
  color: #388e3c;
}

.action-badge.login {
  background: #e3f2fd;
  color: #1976d2;
}

.action-badge.logout {
  background: #f5f5f5;
  color: #616161;
}

.status-badge.success {
  background: #e8f5e8;
  color: #388e3c;
}

.status-badge.failed {
  background: #fff8e1;
  color: #f57c00;
}

.status-badge.error {
  background: #ffebee;
  color: #d32f2f;
}

.description {
  max-width: 300px;
  word-wrap: break-word;
  line-height: 1.4;
}

.ip-info .ip-address {
  font-size: 0.9rem;
  color: #333;
  margin-bottom: 0.25rem;
}

.ip-info .user-agent {
  font-size: 0.8rem;
  color: #666;
}

.action-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s;
}

.action-btn.view {
  background: #007bff;
  color: white;
}

/* 时间线视图样式 */
.timeline-view {
  padding: 1rem;
}

.timeline {
  position: relative;
  padding-left: 2rem;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 1rem;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #dee2e6;
}

.timeline-item {
  position: relative;
  margin-bottom: 2rem;
}

.timeline-marker {
  position: absolute;
  left: -2rem;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.marker-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.9rem;
  z-index: 1;
}

.marker-icon.success {
  background: #28a745;
}

.marker-icon.failed {
  background: #ffc107;
}

.marker-icon.error {
  background: #dc3545;
}

.marker-time {
  font-size: 0.7rem;
  color: #666;
  white-space: nowrap;
}

.timeline-content {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  margin-left: 1rem;
}

.timeline-item.success .timeline-content {
  border-left: 4px solid #28a745;
}

.timeline-item.failed .timeline-content {
  border-left: 4px solid #ffc107;
}

.timeline-item.error .timeline-content {
  border-left: 4px solid #dc3545;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.log-info h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1rem;
}

.log-meta {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.log-details {
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.details-content {
  color: #666;
  line-height: 1.5;
}

.log-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid #eee;
}

.ip-info,
.browser-info {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #666;
  font-size: 0.8rem;
}

.detail-btn {
  padding: 0.25rem 0.5rem;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.log-detail-modal {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
}

.modal-body {
  padding: 1rem;
}

.log-detail {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.detail-item {
  display: flex;
  gap: 0.5rem;
}

.detail-item label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.detail-item span {
  color: #666;
}

.description-content,
.details-content {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  color: #666;
  line-height: 1.5;
}

.request-data,
.response-data {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  overflow-x: auto;
}

.request-data pre,
.response-data pre {
  margin: 0;
  color: #666;
  font-size: 0.8rem;
  white-space: pre-wrap;
}

.user-agent {
  word-break: break-all;
  font-size: 0.8rem;
}

.clear-options {
  margin-bottom: 1rem;
}

.clear-options h5 {
  margin: 0 0 1rem 0;
  color: #333;
}

.clear-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  cursor: pointer;
}

.clear-option input {
  margin: 0;
}

.warning-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
}

.warning-message i {
  font-size: 1.2rem;
}

.warning-message p {
  margin: 0;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem;
  border-top: 1px solid #eee;
}

.modal-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.modal-btn.danger {
  background: #dc3545;
  color: white;
}

.modal-btn.cancel {
  background: #6c757d;
  color: white;
}

@media (max-width: 768px) {
  .operation-log {
    padding: 1rem;
  }
  
  .log-stats {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-left {
    justify-content: center;
  }
  
  .list-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .logs-table {
    font-size: 0.8rem;
  }
  
  .timeline {
    padding-left: 1rem;
  }
  
  .timeline::before {
    left: 0.5rem;
  }
  
  .timeline-marker {
    left: -1.5rem;
  }
  
  .timeline-content {
    margin-left: 0.5rem;
  }
  
  .log-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .log-footer {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
}
</style>