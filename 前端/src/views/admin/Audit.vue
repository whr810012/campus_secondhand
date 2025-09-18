<template>
  <div class="audit-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">审核管理</h1>
        <p class="page-description">管理商品发布审核、用户认证审核等待处理事项</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="batchApprove">
          <el-icon><Check /></el-icon>
          批量通过
        </el-button>
        <el-button type="danger" @click="batchReject">
          <el-icon><Close /></el-icon>
          批量拒绝
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.pendingCount }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.approvedCount }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon danger">
          <el-icon><Close /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.rejectedCount }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.avgProcessTime }}</div>
          <div class="stat-label">平均处理时间(小时)</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索标题、用户名..."
          clearable
          @input="handleSearch"
          style="width: 300px;"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="typeFilter" placeholder="审核类型" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="商品发布" value="product" />
          <el-option label="用户认证" value="user" />
          <el-option label="举报处理" value="report" />
          <el-option label="退款申请" value="refund" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="审核状态" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-select v-model="priorityFilter" placeholder="优先级" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
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
        <el-button type="primary" @click="refreshAudits">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 审核列表 -->
    <div class="table-container">
      <el-table
        :data="filteredAudits"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="createdAt" label="提交时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="title-cell">
              <span class="title">{{ row.title }}</span>
              <el-tag v-if="row.priority === 'high'" type="danger" size="small" class="priority-tag">
                高优先级
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="submitter" label="提交人" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="24" :src="row.submitterAvatar" />
              <span class="username">{{ row.submitter }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewer" label="审核人" width="120">
          <template #default="{ row }">
            <div class="user-info" v-if="row.reviewer">
              <el-avatar :size="24" :src="row.reviewerAvatar" />
              <span class="username">{{ row.reviewer }}</span>
            </div>
            <span v-else class="no-reviewer">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="reviewedAt" label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.reviewedAt ? formatDate(row.reviewedAt) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="success" 
              size="small" 
              @click="approveAudit(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="danger" 
              size="small" 
              @click="rejectAudit(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status !== 'pending'" 
              type="warning" 
              size="small" 
              @click="reassignAudit(row)"
            >
              重新审核
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
        :total="totalAudits"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 审核详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="审核详情"
      width="900px"
      :before-close="handleCloseDetail"
    >
      <div class="audit-detail" v-if="selectedAudit">
        <!-- 基本信息 -->
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-tag :type="getStatusTagType(selectedAudit.status)" size="small">
                {{ getStatusLabel(selectedAudit.status) }}
              </el-tag>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="审核类型">
              <el-tag :type="getTypeTagType(selectedAudit.type)" size="small">
                {{ getTypeLabel(selectedAudit.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="getPriorityTagType(selectedAudit.priority)" size="small">
                {{ getPriorityLabel(selectedAudit.priority) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="提交人">
              <div class="user-info">
                <el-avatar :size="24" :src="selectedAudit.submitterAvatar" />
                <span>{{ selectedAudit.submitter }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">
              {{ formatDate(selectedAudit.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="审核人">
              <div class="user-info" v-if="selectedAudit.reviewer">
                <el-avatar :size="24" :src="selectedAudit.reviewerAvatar" />
                <span>{{ selectedAudit.reviewer }}</span>
              </div>
              <span v-else>未分配</span>
            </el-descriptions-item>
            <el-descriptions-item label="审核时间">
              {{ selectedAudit.reviewedAt ? formatDate(selectedAudit.reviewedAt) : '未审核' }}
            </el-descriptions-item>
            <el-descriptions-item label="标题" :span="2">
              {{ selectedAudit.title }}
            </el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">
              {{ selectedAudit.description }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 审核内容 -->
        <el-card class="detail-card" v-if="selectedAudit.content">
          <template #header>
            <span>审核内容</span>
          </template>
          <div class="audit-content">
            <!-- 商品审核内容 -->
            <div v-if="selectedAudit.type === 'product'" class="product-content">
              <div class="product-images" v-if="selectedAudit.content.images">
                <h4>商品图片</h4>
                <div class="image-gallery">
                  <el-image
                    v-for="(image, index) in selectedAudit.content.images"
                    :key="index"
                    :src="image"
                    :preview-src-list="selectedAudit.content.images"
                    :initial-index="index"
                    fit="cover"
                    class="product-image"
                  />
                </div>
              </div>
              <div class="product-info">
                <h4>商品信息</h4>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="商品名称">
                    {{ selectedAudit.content.name }}
                  </el-descriptions-item>
                  <el-descriptions-item label="价格">
                    ¥{{ selectedAudit.content.price }}
                  </el-descriptions-item>
                  <el-descriptions-item label="分类">
                    {{ selectedAudit.content.category }}
                  </el-descriptions-item>
                  <el-descriptions-item label="成色">
                    {{ selectedAudit.content.condition }}
                  </el-descriptions-item>
                  <el-descriptions-item label="商品描述" :span="2">
                    {{ selectedAudit.content.description }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>

            <!-- 用户认证内容 -->
            <div v-else-if="selectedAudit.type === 'user'" class="user-content">
              <div class="cert-images">
                <h4>认证材料</h4>
                <div class="image-gallery">
                  <el-image
                    v-for="(image, index) in selectedAudit.content.certImages"
                    :key="index"
                    :src="image"
                    :preview-src-list="selectedAudit.content.certImages"
                    :initial-index="index"
                    fit="cover"
                    class="cert-image"
                  />
                </div>
              </div>
              <div class="user-info-detail">
                <h4>用户信息</h4>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="真实姓名">
                    {{ selectedAudit.content.realName }}
                  </el-descriptions-item>
                  <el-descriptions-item label="学号">
                    {{ selectedAudit.content.studentId }}
                  </el-descriptions-item>
                  <el-descriptions-item label="学院">
                    {{ selectedAudit.content.college }}
                  </el-descriptions-item>
                  <el-descriptions-item label="专业">
                    {{ selectedAudit.content.major }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>

            <!-- 举报内容 -->
            <div v-else-if="selectedAudit.type === 'report'" class="report-content">
              <h4>举报详情</h4>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="举报类型">
                  {{ selectedAudit.content.reportType }}
                </el-descriptions-item>
                <el-descriptions-item label="被举报对象">
                  {{ selectedAudit.content.targetType }}
                </el-descriptions-item>
                <el-descriptions-item label="举报原因" :span="2">
                  {{ selectedAudit.content.reason }}
                </el-descriptions-item>
              </el-descriptions>
              <div class="evidence-images" v-if="selectedAudit.content.evidence">
                <h4>举报证据</h4>
                <div class="image-gallery">
                  <el-image
                    v-for="(image, index) in selectedAudit.content.evidence"
                    :key="index"
                    :src="image"
                    :preview-src-list="selectedAudit.content.evidence"
                    :initial-index="index"
                    fit="cover"
                    class="evidence-image"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 审核记录 -->
        <el-card class="detail-card" v-if="selectedAudit.auditHistory">
          <template #header>
            <span>审核记录</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(record, index) in selectedAudit.auditHistory"
              :key="index"
              :timestamp="formatDate(record.time)"
              :type="getTimelineType(record.action)"
            >
              <div class="timeline-content">
                <div class="action-info">
                  <span class="action">{{ record.action }}</span>
                  <span class="operator">{{ record.operator }}</span>
                </div>
                <div class="comment" v-if="record.comment">
                  {{ record.comment }}
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <!-- 审核操作 -->
        <div class="audit-actions" v-if="selectedAudit.status === 'pending'">
          <el-card>
            <template #header>
              <span>审核操作</span>
            </template>
            <div class="action-form">
              <el-form :model="auditForm" label-width="80px">
                <el-form-item label="审核结果">
                  <el-radio-group v-model="auditForm.result">
                    <el-radio label="approved">通过</el-radio>
                    <el-radio label="rejected">拒绝</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="审核意见">
                  <el-input
                    v-model="auditForm.comment"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入审核意见..."
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitAudit">
                    提交审核
                  </el-button>
                  <el-button @click="showDetailDialog = false">
                    取消
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-card>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Close,
  Clock,
  Timer,
  Search,
  Refresh
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const typeFilter = ref('')
const statusFilter = ref('')
const priorityFilter = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const totalAudits = ref(0)
const selectedRows = ref([])
const showDetailDialog = ref(false)
const selectedAudit = ref(null)

// 统计数据
const stats = reactive({
  pendingCount: 45,
  approvedCount: 1234,
  rejectedCount: 89,
  avgProcessTime: 2.5
})

// 审核表单
const auditForm = reactive({
  result: 'approved',
  comment: ''
})

// 审核数据
const audits = ref([
  {
    id: 1,
    type: 'product',
    title: 'iPhone 15 Pro 256GB 深空黑色',
    description: '用户发布商品待审核',
    submitter: '张三',
    submitterAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    status: 'pending',
    priority: 'high',
    createdAt: '2024-02-20 14:30:25',
    reviewer: null,
    reviewerAvatar: null,
    reviewedAt: null,
    content: {
      name: 'iPhone 15 Pro 256GB 深空黑色',
      price: 8999,
      category: '数码产品',
      condition: '9成新',
      description: '个人使用，成色很好，配件齐全',
      images: [
        'https://via.placeholder.com/300x300/409EFF/fff?text=iPhone+1',
        'https://via.placeholder.com/300x300/67C23A/fff?text=iPhone+2',
        'https://via.placeholder.com/300x300/E6A23C/fff?text=iPhone+3'
      ]
    },
    auditHistory: [
      {
        time: '2024-02-20 14:30:25',
        action: '提交审核',
        operator: '张三',
        comment: '用户提交商品发布申请'
      }
    ]
  },
  {
    id: 2,
    type: 'user',
    title: '学生身份认证申请',
    description: '用户申请学生身份认证',
    submitter: '李四',
    submitterAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    status: 'approved',
    priority: 'medium',
    createdAt: '2024-02-20 13:25:18',
    reviewer: '管理员A',
    reviewerAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    reviewedAt: '2024-02-20 14:15:30',
    content: {
      realName: '李四',
      studentId: '2021001234',
      college: '计算机学院',
      major: '软件工程',
      certImages: [
        'https://via.placeholder.com/300x400/409EFF/fff?text=学生证正面',
        'https://via.placeholder.com/300x400/67C23A/fff?text=学生证背面'
      ]
    },
    auditHistory: [
      {
        time: '2024-02-20 13:25:18',
        action: '提交审核',
        operator: '李四',
        comment: '用户提交学生身份认证申请'
      },
      {
        time: '2024-02-20 14:15:30',
        action: '审核通过',
        operator: '管理员A',
        comment: '学生证信息核实无误，审核通过'
      }
    ]
  },
  {
    id: 3,
    type: 'report',
    title: '举报虚假商品信息',
    description: '用户举报商品信息不实',
    submitter: '王五',
    submitterAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    status: 'rejected',
    priority: 'low',
    createdAt: '2024-02-20 12:20:42',
    reviewer: '管理员B',
    reviewerAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    reviewedAt: '2024-02-20 13:45:15',
    content: {
      reportType: '虚假信息',
      targetType: '商品',
      reason: '商品描述与实际不符，涉嫌虚假宣传',
      evidence: [
        'https://via.placeholder.com/300x300/F56C6C/fff?text=证据1',
        'https://via.placeholder.com/300x300/E6A23C/fff?text=证据2'
      ]
    },
    auditHistory: [
      {
        time: '2024-02-20 12:20:42',
        action: '提交举报',
        operator: '王五',
        comment: '用户提交举报申请'
      },
      {
        time: '2024-02-20 13:45:15',
        action: '审核拒绝',
        operator: '管理员B',
        comment: '经核实，商品信息属实，举报不成立'
      }
    ]
  },
  {
    id: 4,
    type: 'refund',
    title: '退款申请 - 订单ORD20240220001',
    description: '用户申请订单退款',
    submitter: '赵六',
    submitterAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    status: 'pending',
    priority: 'medium',
    createdAt: '2024-02-20 11:15:33',
    reviewer: null,
    reviewerAvatar: null,
    reviewedAt: null,
    content: {
      orderId: 'ORD20240220001',
      refundAmount: 299,
      refundReason: '商品质量问题',
      description: '收到商品后发现有明显瑕疵，申请退款'
    },
    auditHistory: [
      {
        time: '2024-02-20 11:15:33',
        action: '提交退款申请',
        operator: '赵六',
        comment: '用户提交退款申请'
      }
    ]
  }
])

// 计算属性
const filteredAudits = computed(() => {
  let result = audits.value
  
  if (searchQuery.value) {
    result = result.filter(item => 
      item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.submitter.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (typeFilter.value) {
    result = result.filter(item => item.type === typeFilter.value)
  }
  
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  if (priorityFilter.value) {
    result = result.filter(item => item.priority === priorityFilter.value)
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
const fetchAudits = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    totalAudits.value = audits.value.length
  } catch (error) {
    ElMessage.error('获取审核数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchQuery.value = ''
  typeFilter.value = ''
  statusFilter.value = ''
  priorityFilter.value = ''
  dateRange.value = []
  currentPage.value = 1
}

const refreshAudits = () => {
  fetchAudits()
  ElMessage.success('审核数据已刷新')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchAudits()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAudits()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const viewDetail = (audit) => {
  selectedAudit.value = audit
  auditForm.result = 'approved'
  auditForm.comment = ''
  showDetailDialog.value = true
}

const handleCloseDetail = () => {
  selectedAudit.value = null
  showDetailDialog.value = false
}

const approveAudit = async (audit) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过审核 "${audit.title}" 吗？`,
      '确认通过',
      { type: 'success' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    audit.status = 'approved'
    audit.reviewer = '当前管理员'
    audit.reviewedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
    
    ElMessage.success('审核通过成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const rejectAudit = async (audit) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入拒绝原因',
      '确认拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因...',
        inputValidator: (value) => {
          if (!value) {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    audit.status = 'rejected'
    audit.reviewer = '当前管理员'
    audit.reviewedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
    audit.rejectReason = reason
    
    ElMessage.success('审核拒绝成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const reassignAudit = async (audit) => {
  try {
    await ElMessageBox.confirm(
      `确定要重新审核 "${audit.title}" 吗？`,
      '确认重新审核',
      { type: 'warning' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    audit.status = 'pending'
    audit.reviewer = null
    audit.reviewedAt = null
    
    ElMessage.success('已重新提交审核')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const submitAudit = async () => {
  if (!auditForm.comment.trim()) {
    ElMessage.warning('请输入审核意见')
    return
  }
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedAudit.value.status = auditForm.result
    selectedAudit.value.reviewer = '当前管理员'
    selectedAudit.value.reviewedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
    
    // 添加审核记录
    selectedAudit.value.auditHistory.push({
      time: new Date().toISOString().slice(0, 19).replace('T', ' '),
      action: auditForm.result === 'approved' ? '审核通过' : '审核拒绝',
      operator: '当前管理员',
      comment: auditForm.comment
    })
    
    ElMessage.success('审核提交成功')
    showDetailDialog.value = false
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const batchApprove = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要操作的审核项')
    return
  }
  
  const pendingItems = selectedRows.value.filter(item => item.status === 'pending')
  if (pendingItems.length === 0) {
    ElMessage.warning('所选项目中没有待审核的项目')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量通过 ${pendingItems.length} 个审核项吗？`,
      '确认批量通过',
      { type: 'success' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    pendingItems.forEach(item => {
      item.status = 'approved'
      item.reviewer = '当前管理员'
      item.reviewedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
    })
    
    ElMessage.success(`批量通过 ${pendingItems.length} 个审核项成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

const batchReject = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要操作的审核项')
    return
  }
  
  const pendingItems = selectedRows.value.filter(item => item.status === 'pending')
  if (pendingItems.length === 0) {
    ElMessage.warning('所选项目中没有待审核的项目')
    return
  }
  
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `确定要批量拒绝 ${pendingItems.length} 个审核项吗？请输入拒绝原因：`,
      '确认批量拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因...',
        inputValidator: (value) => {
          if (!value) {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    pendingItems.forEach(item => {
      item.status = 'rejected'
      item.reviewer = '当前管理员'
      item.reviewedAt = new Date().toISOString().slice(0, 19).replace('T', ' ')
      item.rejectReason = reason
    })
    
    ElMessage.success(`批量拒绝 ${pendingItems.length} 个审核项成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

// 工具函数
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getTypeTagType = (type) => {
  const types = {
    product: 'primary',
    user: 'success',
    report: 'warning',
    refund: 'danger'
  }
  return types[type] || 'info'
}

const getTypeLabel = (type) => {
  const labels = {
    product: '商品发布',
    user: '用户认证',
    report: '举报处理',
    refund: '退款申请'
  }
  return labels[type] || type
}

const getStatusTagType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const labels = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return labels[status] || status
}

const getPriorityTagType = (priority) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

const getPriorityLabel = (priority) => {
  const labels = {
    high: '高',
    medium: '中',
    low: '低'
  }
  return labels[priority] || priority
}

const getTimelineType = (action) => {
  if (action.includes('通过')) return 'success'
  if (action.includes('拒绝')) return 'danger'
  if (action.includes('提交')) return 'primary'
  return 'info'
}

// 生命周期
onMounted(() => {
  fetchAudits()
})
</script>

<style lang="scss" scoped>
.audit-management {
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

        &.pending {
          background: #fff3e0;
          color: #ff9800;
        }

        &.success {
          background: #e8f5e8;
          color: #4caf50;
        }

        &.danger {
          background: #ffebee;
          color: #f44336;
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

    .title-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .title {
        flex: 1;
        font-weight: 500;
      }

      .priority-tag {
        flex-shrink: 0;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .username {
        font-weight: 500;
      }
    }

    .no-reviewer {
      color: #909399;
      font-style: italic;
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

  .audit-detail {
    .detail-card {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }

    .audit-content {
      .product-images,
      .cert-images,
      .evidence-images {
        margin-bottom: 20px;

        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
        }

        .image-gallery {
          display: flex;
          gap: 12px;
          flex-wrap: wrap;

          .product-image,
          .cert-image,
          .evidence-image {
            width: 120px;
            height: 120px;
            border-radius: 8px;
            cursor: pointer;
          }
        }
      }

      .product-info,
      .user-info-detail {
        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
        }
      }

      .report-content {
        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
        }
      }
    }

    .timeline-content {
      .action-info {
        display: flex;
        gap: 12px;
        align-items: center;
        margin-bottom: 4px;

        .action {
          font-weight: 500;
          color: #303133;
        }

        .operator {
          color: #909399;
          font-size: 14px;
        }
      }

      .comment {
        color: #606266;
        font-size: 14px;
        line-height: 1.5;
      }
    }

    .audit-actions {
      .action-form {
        max-width: 600px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .audit-management {
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

    .audit-detail {
      .audit-content {
        .image-gallery {
          .product-image,
          .cert-image,
          .evidence-image {
            width: 80px;
            height: 80px;
          }
        }
      }
    }
  }
}
</style>