<template>
  <div class="announcements-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">公告管理</h1>
        <p class="page-description">发布和管理系统公告，通知用户重要信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon primary">
          <el-icon><Bell /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalAnnouncements }}</div>
          <div class="stat-label">总公告数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.publishedAnnouncements }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.draftAnnouncements }}</div>
          <div class="stat-label">草稿</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalViews }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索公告标题或内容..."
          clearable
          @input="handleSearch"
          style="width: 300px;"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="已发布" value="published" />
          <el-option label="草稿" value="draft" />
          <el-option label="已下线" value="offline" />
        </el-select>
        <el-select v-model="typeFilter" placeholder="类型筛选" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="系统公告" value="system" />
          <el-option label="活动公告" value="activity" />
          <el-option label="维护公告" value="maintenance" />
          <el-option label="紧急公告" value="urgent" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 240px;"
        />
      </div>
      <div class="filter-right">
        <el-button @click="resetFilters">重置筛选</el-button>
        <el-button type="primary" @click="exportAnnouncements">导出数据</el-button>
      </div>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedAnnouncements.length > 0">
      <span class="selected-info">已选择 {{ selectedAnnouncements.length }} 项</span>
      <el-button type="success" @click="batchPublish">批量发布</el-button>
      <el-button type="warning" @click="batchOffline">批量下线</el-button>
      <el-button type="danger" @click="batchDelete">批量删除</el-button>
    </div>

    <!-- 公告列表 -->
    <div class="table-container">
      <el-table
        :data="filteredAnnouncements"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="公告标题" min-width="200">
          <template #default="{ row }">
            <div class="announcement-title">
              <el-icon v-if="row.type === 'urgent'" class="urgent-icon"><Warning /></el-icon>
              <span class="title-text" @click="previewAnnouncement(row)">{{ row.title }}</span>
              <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布者" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100">
          <template #default="{ row }">
            <span class="view-count">{{ row.viewCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ row.publishTime ? formatDate(row.publishTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editAnnouncement(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 'draft'" 
              type="success" 
              size="small" 
              @click="publishAnnouncement(row)"
            >
              发布
            </el-button>
            <el-button 
              v-if="row.status === 'published'" 
              type="warning" 
              size="small" 
              @click="offlineAnnouncement(row)"
            >
              下线
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="toggleTop(row)"
            >
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteAnnouncement(row)">
              删除
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
        :page-sizes="[10, 20, 50, 100]"
        :total="totalAnnouncements"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑公告对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingAnnouncement ? '编辑公告' : '发布公告'"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="announcementFormRef"
        :model="announcementForm"
        :rules="announcementRules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="announcementForm.type" placeholder="请选择公告类型" style="width: 100%;">
            <el-option label="系统公告" value="system" />
            <el-option label="活动公告" value="activity" />
            <el-option label="维护公告" value="maintenance" />
            <el-option label="紧急公告" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker
            v-model="announcementForm.publishTime"
            type="datetime"
            placeholder="选择发布时间（可选）"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="设置">
          <el-checkbox v-model="announcementForm.isTop">置顶显示</el-checkbox>
          <el-checkbox v-model="announcementForm.sendNotification">发送通知</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="saveAndPublish" :loading="saving">
          {{ editingAnnouncement ? '更新并发布' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 公告预览对话框 -->
    <el-dialog
      v-model="showPreviewDialog"
      title="公告预览"
      width="600px"
    >
      <div class="announcement-preview" v-if="previewAnnouncement">
        <div class="preview-header">
          <h2 class="preview-title">{{ previewAnnouncement.title }}</h2>
          <div class="preview-meta">
            <el-tag :type="getTypeTagType(previewAnnouncement.type)" size="small">
              {{ getTypeLabel(previewAnnouncement.type) }}
            </el-tag>
            <span class="preview-author">发布者：{{ previewAnnouncement.author }}</span>
            <span class="preview-time">{{ formatDate(previewAnnouncement.publishTime || previewAnnouncement.createdAt) }}</span>
          </div>
        </div>
        <div class="preview-content">
          {{ previewAnnouncement.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Bell,
  Check,
  Clock,
  View,
  Warning
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const totalAnnouncements = ref(0)
const selectedAnnouncements = ref([])
const showAddDialog = ref(false)
const showPreviewDialog = ref(false)
const editingAnnouncement = ref(null)
const previewAnnouncementData = ref(null)
const announcementFormRef = ref(null)

// 统计数据
const stats = reactive({
  totalAnnouncements: 45,
  publishedAnnouncements: 32,
  draftAnnouncements: 13,
  totalViews: 15420
})

// 公告数据
const announcements = ref([
  {
    id: 1,
    title: '校园二手交易平台正式上线',
    type: 'system',
    status: 'published',
    content: '欢迎使用校园二手交易平台！我们致力于为同学们提供安全、便捷的二手物品交易服务。',
    author: '系统管理员',
    viewCount: 1256,
    isTop: true,
    sendNotification: true,
    publishTime: '2024-01-15 10:00:00',
    createdAt: '2024-01-15 09:30:00'
  },
  {
    id: 2,
    title: '春节期间服务调整通知',
    type: 'maintenance',
    status: 'published',
    content: '春节期间（2月10日-2月17日），平台客服服务时间调整为每日9:00-18:00，请同学们合理安排交易时间。',
    author: '运营团队',
    viewCount: 892,
    isTop: false,
    sendNotification: true,
    publishTime: '2024-02-05 14:30:00',
    createdAt: '2024-02-05 14:00:00'
  },
  {
    id: 3,
    title: '新学期开学季活动预告',
    type: 'activity',
    status: 'draft',
    content: '新学期开学季即将到来，我们将推出一系列优惠活动，敬请期待！',
    author: '活动策划',
    viewCount: 0,
    isTop: false,
    sendNotification: false,
    publishTime: null,
    createdAt: '2024-02-20 16:45:00'
  },
  {
    id: 4,
    title: '紧急：发现虚假商品信息处理公告',
    type: 'urgent',
    status: 'published',
    content: '近期发现部分用户发布虚假商品信息，平台已采取相应措施。请同学们提高警惕，如发现可疑信息请及时举报。',
    author: '安全团队',
    viewCount: 2341,
    isTop: true,
    sendNotification: true,
    publishTime: '2024-02-18 09:15:00',
    createdAt: '2024-02-18 09:00:00'
  },
  {
    id: 5,
    title: '系统维护升级通知',
    type: 'maintenance',
    status: 'offline',
    content: '为提升用户体验，系统将于本周末进行维护升级，届时可能影响部分功能使用。',
    author: '技术团队',
    viewCount: 567,
    isTop: false,
    sendNotification: false,
    publishTime: '2024-02-10 20:00:00',
    createdAt: '2024-02-10 19:30:00'
  }
])

// 公告表单
const announcementForm = reactive({
  title: '',
  type: 'system',
  content: '',
  publishTime: null,
  isTop: false,
  sendNotification: false
})

// 表单验证规则
const announcementRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const filteredAnnouncements = computed(() => {
  let result = announcements.value
  
  if (searchQuery.value) {
    result = result.filter(item => 
      item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.content.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  if (typeFilter.value) {
    result = result.filter(item => item.type === typeFilter.value)
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    result = result.filter(item => {
      const itemDate = new Date(item.createdAt).toISOString().split('T')[0]
      return itemDate >= dateRange.value[0] && itemDate <= dateRange.value[1]
    })
  }
  
  return result
})

// 方法
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    totalAnnouncements.value = announcements.value.length
  } catch (error) {
    ElMessage.error('获取公告数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  dateRange.value = []
  currentPage.value = 1
}

const handleSelectionChange = (selection) => {
  selectedAnnouncements.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchAnnouncements()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAnnouncements()
}

const previewAnnouncement = (announcement) => {
  previewAnnouncementData.value = announcement
  showPreviewDialog.value = true
}

const editAnnouncement = (announcement) => {
  editingAnnouncement.value = announcement
  Object.assign(announcementForm, {
    title: announcement.title,
    type: announcement.type,
    content: announcement.content,
    publishTime: announcement.publishTime,
    isTop: announcement.isTop,
    sendNotification: announcement.sendNotification
  })
  showAddDialog.value = true
}

const publishAnnouncement = async (announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布公告 "${announcement.title}" 吗？`,
      '确认发布',
      { type: 'info' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    announcement.status = 'published'
    announcement.publishTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
    
    ElMessage.success('公告发布成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const offlineAnnouncement = async (announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要下线公告 "${announcement.title}" 吗？`,
      '确认下线',
      { type: 'warning' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    announcement.status = 'offline'
    
    ElMessage.success('公告已下线')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下线失败')
    }
  }
}

const toggleTop = async (announcement) => {
  try {
    const action = announcement.isTop ? '取消置顶' : '置顶'
    await ElMessageBox.confirm(
      `确定要${action}公告 "${announcement.title}" 吗？`,
      `确认${action}`,
      { type: 'info' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    announcement.isTop = !announcement.isTop
    
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const deleteAnnouncement = async (announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告 "${announcement.title}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const index = announcements.value.findIndex(item => item.id === announcement.id)
    if (index > -1) {
      announcements.value.splice(index, 1)
    }
    
    ElMessage.success('公告删除成功')
    fetchAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveDraft = async () => {
  try {
    await announcementFormRef.value.validate()
    saving.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (editingAnnouncement.value) {
      Object.assign(editingAnnouncement.value, announcementForm)
      ElMessage.success('草稿保存成功')
    } else {
      const newAnnouncement = {
        id: Date.now(),
        ...announcementForm,
        status: 'draft',
        author: '当前用户',
        viewCount: 0,
        createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      }
      announcements.value.unshift(newAnnouncement)
      ElMessage.success('草稿保存成功')
    }
    
    showAddDialog.value = false
    fetchAnnouncements()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

const saveAndPublish = async () => {
  try {
    await announcementFormRef.value.validate()
    saving.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const publishTime = announcementForm.publishTime || new Date().toISOString().replace('T', ' ').substring(0, 19)
    
    if (editingAnnouncement.value) {
      Object.assign(editingAnnouncement.value, {
        ...announcementForm,
        status: 'published',
        publishTime
      })
      ElMessage.success('公告更新并发布成功')
    } else {
      const newAnnouncement = {
        id: Date.now(),
        ...announcementForm,
        status: 'published',
        author: '当前用户',
        viewCount: 0,
        publishTime,
        createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      }
      announcements.value.unshift(newAnnouncement)
      ElMessage.success('公告发布成功')
    }
    
    showAddDialog.value = false
    fetchAnnouncements()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  editingAnnouncement.value = null
  Object.assign(announcementForm, {
    title: '',
    type: 'system',
    content: '',
    publishTime: null,
    isTop: false,
    sendNotification: false
  })
  announcementFormRef.value?.clearValidate()
}

const batchPublish = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要发布选中的 ${selectedAnnouncements.value.length} 个公告吗？`,
      '批量发布',
      { type: 'info' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const publishTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
    selectedAnnouncements.value.forEach(announcement => {
      announcement.status = 'published'
      announcement.publishTime = publishTime
    })
    
    ElMessage.success('批量发布成功')
    selectedAnnouncements.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量发布失败')
    }
  }
}

const batchOffline = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要下线选中的 ${selectedAnnouncements.value.length} 个公告吗？`,
      '批量下线',
      { type: 'warning' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedAnnouncements.value.forEach(announcement => {
      announcement.status = 'offline'
    })
    
    ElMessage.success('批量下线成功')
    selectedAnnouncements.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下线失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedAnnouncements.value.length} 个公告吗？此操作不可恢复。`,
      '批量删除',
      { type: 'error' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedAnnouncements.value.forEach(announcement => {
      const index = announcements.value.findIndex(item => item.id === announcement.id)
      if (index > -1) {
        announcements.value.splice(index, 1)
      }
    })
    
    ElMessage.success('批量删除成功')
    selectedAnnouncements.value = []
    fetchAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const exportAnnouncements = () => {
  // 模拟导出功能
  ElMessage.success('公告数据导出成功')
}

// 工具函数
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getTypeTagType = (type) => {
  const types = {
    system: 'primary',
    activity: 'success',
    maintenance: 'warning',
    urgent: 'danger'
  }
  return types[type] || 'info'
}

const getTypeLabel = (type) => {
  const labels = {
    system: '系统公告',
    activity: '活动公告',
    maintenance: '维护公告',
    urgent: '紧急公告'
  }
  return labels[type] || type
}

const getStatusTagType = (status) => {
  const types = {
    published: 'success',
    draft: 'info',
    offline: 'warning'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const labels = {
    published: '已发布',
    draft: '草稿',
    offline: '已下线'
  }
  return labels[status] || status
}

// 生命周期
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style lang="scss" scoped>
.announcements-management {
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
    }

    .filter-right {
      display: flex;
      gap: 12px;
    }
  }

  .batch-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding: 12px 20px;
    background: #f0f9ff;
    border: 1px solid #bae6fd;
    border-radius: 8px;

    .selected-info {
      color: #0369a1;
      font-weight: 500;
    }
  }

  .table-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .announcement-title {
      display: flex;
      align-items: center;
      gap: 8px;

      .urgent-icon {
        color: #f56c6c;
      }

      .title-text {
        cursor: pointer;
        color: #409eff;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }

    .view-count {
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

  .announcement-preview {
    .preview-header {
      margin-bottom: 20px;
      padding-bottom: 16px;
      border-bottom: 1px solid #ebeef5;

      .preview-title {
        margin: 0 0 12px 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }

      .preview-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 14px;
        color: #909399;
      }
    }

    .preview-content {
      line-height: 1.6;
      color: #606266;
      white-space: pre-wrap;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .announcements-management {
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
        flex-wrap: wrap;
      }
    }

    .stats-cards {
      grid-template-columns: 1fr;
    }

    .batch-actions {
      flex-wrap: wrap;
    }
  }
}
</style>