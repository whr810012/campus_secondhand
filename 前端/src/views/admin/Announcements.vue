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
              <span class="title-text" @click="showPreview(row)">{{ row.title }}</span>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'draft'" 
              type="success" 
              size="small" 
              @click="handlePublishAnnouncement(row)"
            >
              发布
            </el-button>
            <el-button 
              v-if="row.status === 'published'" 
              type="warning" 
              size="small" 
              @click="handleOfflineAnnouncement(row)"
            >
              下线
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteAnnouncement(row)">
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
            <el-option label="普通" value="normal" />
            <el-option label="重要" value="important" />
            <el-option label="紧急" value="urgent" />
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
  Warning
} from '@element-plus/icons-vue'
import {
  getAnnouncementPage,
  getAnnouncementById,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  batchDeleteAnnouncements,
  publishAnnouncement,
  offlineAnnouncement,
  batchPublishAnnouncements,
  batchOfflineAnnouncements,
  incrementViewCount,
  getAnnouncementStats
} from '@/api/announcement'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const totalAnnouncements = ref(0)
const selectedAnnouncements = ref([])
const showAddDialog = ref(false)
const showPreviewDialog = ref(false)
const editingAnnouncement = ref(null)
const previewAnnouncementData = ref(null)
const announcementFormRef = ref(null)

// 公告数据
const announcements = ref([])

// 公告表单
const announcementForm = reactive({
  title: '',
  type: 'normal',
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

// 计算属性 - 直接返回所有公告数据
const filteredAnnouncements = computed(() => {
  return announcements.value
})

// 方法
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    const response = await getAnnouncementPage(params)
    if (response.code === 200) {
      announcements.value = response.data.records || []
      totalAnnouncements.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取公告数据失败')
    }
  } catch (error) {
    console.error('获取公告数据失败:', error)
    ElMessage.error('获取公告数据失败')
  } finally {
    loading.value = false
  }
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

const showPreview = (announcement) => {
  previewAnnouncementData.value = announcement
  showPreviewDialog.value = true
}

const previewAnnouncement = computed(() => {
  return previewAnnouncementData.value
})

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

const handlePublishAnnouncement = async (announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布公告 "${announcement.title}" 吗？`,
      '确认发布',
      { type: 'info' }
    )
    
    const response = await publishAnnouncement(announcement.id)
    if (response.code === 200) {
      ElMessage.success('公告发布成功')
      fetchAnnouncements() // 重新获取数据
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
      ElMessage.error('发布失败')
    }
  }
}

const handleOfflineAnnouncement = async (announcement) => {
  try {
    await ElMessageBox.confirm(
      `确定要下线公告 "${announcement.title}" 吗？`,
      '确认下线',
      { type: 'warning' }
    )
    
    const response = await offlineAnnouncement(announcement.id)
    if (response.code === 200) {
      ElMessage.success('公告已下线')
      fetchAnnouncements() // 重新获取数据
    } else {
      ElMessage.error(response.message || '下线失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下线失败:', error)
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

const handleDeleteAnnouncement = async (announcement) => {
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
    
    const response = await deleteAnnouncement(announcement.id)
    if (response.code === 200) {
      ElMessage.success('公告删除成功')
      fetchAnnouncements() // 重新获取数据
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const saveDraft = async () => {
  try {
    await announcementFormRef.value.validate()
    saving.value = true
    
    const announcementData = {
      title: announcementForm.title,
      type: announcementForm.type,
      content: announcementForm.content,
      publishTime: announcementForm.publishTime,
      status: 0 // 草稿状态
    }
    
    let response
    if (editingAnnouncement.value) {
      response = await updateAnnouncement(editingAnnouncement.value.id, announcementData)
    } else {
      response = await createAnnouncement(announcementData)
    }
    
    if (response.code === 200) {
      ElMessage.success('草稿保存成功')
      showAddDialog.value = false
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const saveAndPublish = async () => {
  try {
    await announcementFormRef.value.validate()
    saving.value = true
    
    const announcementData = {
      title: announcementForm.title,
      type: announcementForm.type,
      content: announcementForm.content,
      publishTime: announcementForm.publishTime,
      status: 1 // 发布状态
    }
    
    let response
    if (editingAnnouncement.value) {
      response = await updateAnnouncement(editingAnnouncement.value.id, announcementData)
    } else {
      response = await createAnnouncement(announcementData)
    }
    
    if (response.code === 200) {
      ElMessage.success(editingAnnouncement.value ? '公告更新并发布成功' : '公告发布成功')
      showAddDialog.value = false
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  editingAnnouncement.value = null
  Object.assign(announcementForm, {
    title: '',
    type: 'normal',
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
    
    const ids = selectedAnnouncements.value.map(item => item.id)
    const response = await batchPublishAnnouncements(ids)
    
    if (response.code === 200) {
      ElMessage.success('批量发布成功')
      selectedAnnouncements.value = []
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '批量发布失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量发布失败:', error)
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
    
    const ids = selectedAnnouncements.value.map(item => item.id)
    const response = await batchOfflineAnnouncements(ids)
    
    if (response.code === 200) {
      ElMessage.success('批量下线成功')
      selectedAnnouncements.value = []
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '批量下线失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量下线失败:', error)
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
    
    const ids = selectedAnnouncements.value.map(item => item.id)
    const response = await batchDeleteAnnouncements(ids)
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      selectedAnnouncements.value = []
      fetchAnnouncements()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
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
    normal: 'info',
    important: 'warning',
    urgent: 'danger'
  }
  return types[type] || 'info'
}

const getTypeLabel = (type) => {
  const labels = {
    normal: '普通',
    important: '重要',
    urgent: '紧急'
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

    .batch-actions {
      flex-wrap: wrap;
    }
  }
}
</style>