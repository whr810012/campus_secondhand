<template>
  <div class="user-management-page">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理平台用户信息和权限</p>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-card>
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="搜索">
            <el-input
              v-model="searchForm.keyword"
              placeholder="输入用户昵称、手机号或学校"
              style="width: 300px"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="选择状态" style="width: 120px" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="认证状态">
            <el-select v-model="searchForm.verified" placeholder="认证状态" style="width: 120px" clearable>
              <el-option label="已认证" :value="1" />
              <el-option label="未认证" :value="0" />
              <el-option label="待审核" :value="2" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="注册时间">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 用户列表 -->
    <div class="table-section">
      <el-card>
        <div class="table-header">
          <div class="table-title">
            <h3>用户列表</h3>
            <span class="total-count">共 {{ pagination.total }} 个用户</span>
          </div>
          <div class="table-actions">
            <el-button type="primary" @click="exportUsers">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
        
        <el-table
          v-loading="loading"
          :data="userList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="avatar" label="头像" width="80">
            <template #default="{ row }">
              <el-avatar :src="row.avatar" :size="40">
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>
          
          <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
          
          <el-table-column prop="phone" label="手机号" width="130">
            <template #default="{ row }">
              {{ maskPhone(row.phone) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="school" label="学校" min-width="150" show-overflow-tooltip />
          
          <el-table-column prop="major" label="专业" min-width="120" show-overflow-tooltip />
          
          <el-table-column prop="gender" label="性别" width="80">
            <template #default="{ row }">
              <span v-if="row.gender === 1">男</span>
              <span v-else-if="row.gender === 2">女</span>
              <span v-else>保密</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="verified" label="认证状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.verified === 1" type="success" size="small">
                已认证
              </el-tag>
              <el-tag v-else-if="row.verified === 2" type="warning" size="small">
                待审核
              </el-tag>
              <el-tag v-else type="info" size="small">
                未认证
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdAt" label="注册时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewUser(row)">
                查看
              </el-button>
              <el-button
                v-if="row.verified === 2"
                type="success"
                size="small"
                @click="approveVerification(row.id)"
              >
                审核
              </el-button>
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                size="small"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="showUserDialog"
      title="用户详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentUser" class="user-detail">
        <div class="user-basic">
          <div class="user-avatar">
            <el-avatar :src="currentUser.avatar" :size="80">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="user-info">
            <h3>{{ currentUser.nickname }}</h3>
            <p>{{ currentUser.phone }}</p>
            <div class="user-tags">
              <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
                {{ currentUser.status === 1 ? '正常' : '禁用' }}
              </el-tag>
              <el-tag v-if="currentUser.verified === 1" type="success" size="small">
                已认证
              </el-tag>
              <el-tag v-else-if="currentUser.verified === 2" type="warning" size="small">
                待审核
              </el-tag>
              <el-tag v-else type="info" size="small">
                未认证
              </el-tag>
            </div>
          </div>
        </div>
        
        <el-divider />
        
        <div class="user-details">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学校">{{ currentUser.school || '-' }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ currentUser.major || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">
              <span v-if="currentUser.gender === 1">男</span>
              <span v-else-if="currentUser.gender === 2">女</span>
              <span v-else>保密</span>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ formatDate(currentUser.lastLoginAt) }}</el-descriptions-item>
            <el-descriptions-item label="发布商品数">{{ currentUser.productCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="交易次数">{{ currentUser.orderCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="信誉评分">{{ currentUser.rating || 0 }}/5</el-descriptions-item>
          </el-descriptions>
          
          <div v-if="currentUser.bio" class="user-bio">
            <h4>个人简介</h4>
            <p>{{ currentUser.bio }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showUserDialog = false">关闭</el-button>
        <el-button
          v-if="currentUser?.verified === 2"
          type="success"
          @click="approveVerification(currentUser.id)"
        >
          通过认证
        </el-button>
        <el-button
          :type="currentUser?.status === 1 ? 'danger' : 'success'"
          @click="toggleUserStatus(currentUser)"
        >
          {{ currentUser?.status === 1 ? '禁用用户' : '启用用户' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 认证审核对话框 -->
    <el-dialog
      v-model="showVerifyDialog"
      title="学生身份认证审核"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="verifyUser" class="verify-content">
        <div class="verify-info">
          <h4>用户信息</h4>
          <p><strong>姓名：</strong>{{ verifyUser.realName }}</p>
          <p><strong>学号：</strong>{{ verifyUser.studentId }}</p>
          <p><strong>学校：</strong>{{ verifyUser.school }}</p>
          <p><strong>专业：</strong>{{ verifyUser.major }}</p>
        </div>
        
        <div class="verify-images">
          <h4>认证材料</h4>
          <div class="image-grid">
            <div v-for="(image, index) in verifyUser.verifyImages" :key="index" class="image-item">
              <el-image
                :src="image"
                fit="cover"
                style="width: 120px; height: 120px"
                :preview-src-list="verifyUser.verifyImages"
                :initial-index="index"
              />
            </div>
          </div>
        </div>
        
        <div class="verify-form">
          <el-form :model="verifyForm" label-width="80px">
            <el-form-item label="审核结果">
              <el-radio-group v-model="verifyForm.result">
                <el-radio :label="1">通过</el-radio>
                <el-radio :label="0">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核备注">
              <el-input
                v-model="verifyForm.remark"
                type="textarea"
                :rows="3"
                placeholder="请输入审核备注（拒绝时必填）"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showVerifyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitVerification">
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const showUserDialog = ref(false)
const showVerifyDialog = ref(false)
const currentUser = ref(null)
const verifyUser = ref(null)
const selectedUsers = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  verified: '',
  dateRange: []
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 认证审核表单
const verifyForm = reactive({
  result: 1,
  remark: ''
})

// 用户列表
const userList = ref([
  {
    id: 1,
    avatar: '',
    nickname: '张同学',
    phone: '13812345678',
    school: '北京大学',
    major: '计算机科学与技术',
    gender: 1,
    verified: 1,
    status: 1,
    createdAt: '2024-01-10 10:30:00',
    lastLoginAt: '2024-01-15 14:20:00',
    productCount: 5,
    orderCount: 12,
    rating: 4.8,
    bio: '热爱编程的大学生，喜欢分享技术心得。'
  },
  {
    id: 2,
    avatar: '',
    nickname: '李同学',
    phone: '13987654321',
    school: '清华大学',
    major: '电子工程',
    gender: 2,
    verified: 2,
    status: 1,
    createdAt: '2024-01-12 09:15:00',
    lastLoginAt: '2024-01-15 16:45:00',
    productCount: 3,
    orderCount: 8,
    rating: 4.5,
    bio: '喜欢电子产品，经常在平台上交易数码设备。',
    realName: '李小明',
    studentId: '2021001234',
    verifyImages: ['/images/verify/student-card.jpg', '/images/verify/id-card.jpg']
  },
  {
    id: 3,
    avatar: '',
    nickname: '王同学',
    phone: '13611111111',
    school: '复旦大学',
    major: '经济学',
    gender: 0,
    verified: 0,
    status: 0,
    createdAt: '2024-01-14 16:20:00',
    lastLoginAt: '2024-01-14 18:30:00',
    productCount: 1,
    orderCount: 2,
    rating: 3.0,
    bio: ''
  }
])

// 搜索用户
const handleSearch = async () => {
  pagination.page = 1
  await fetchUsers()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    verified: '',
    dateRange: []
  })
  handleSearch()
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    // 调用API获取用户列表
    // const response = await getUserList(params)
    // userList.value = response.data.list
    // pagination.total = response.data.total
    
    // 模拟数据
    pagination.total = 156
    
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 查看用户详情
const viewUser = (user) => {
  currentUser.value = user
  showUserDialog.value = true
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  const action = user.status === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.nickname}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API切换用户状态
    // await toggleUserStatus(user.id, user.status === 1 ? 0 : 1)
    
    user.status = user.status === 1 ? 0 : 1
    ElMessage.success(`用户${action}成功`)
    
    // 如果是在详情对话框中操作，更新当前用户信息
    if (currentUser.value && currentUser.value.id === user.id) {
      currentUser.value.status = user.status
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}用户失败:`, error)
      ElMessage.error(`${action}用户失败`)
    }
  }
}

// 审核认证
const approveVerification = (userId) => {
  const user = userList.value.find(u => u.id === userId)
  if (user) {
    verifyUser.value = user
    verifyForm.result = 1
    verifyForm.remark = ''
    showVerifyDialog.value = true
  }
}

// 提交认证审核
const submitVerification = async () => {
  if (verifyForm.result === 0 && !verifyForm.remark.trim()) {
    ElMessage.warning('拒绝认证时必须填写审核备注')
    return
  }
  
  try {
    // 调用API提交审核结果
    // await submitVerification(verifyUser.value.id, verifyForm)
    
    verifyUser.value.verified = verifyForm.result
    
    ElMessage.success('审核提交成功')
    showVerifyDialog.value = false
    
    // 更新列表中的用户状态
    const userIndex = userList.value.findIndex(u => u.id === verifyUser.value.id)
    if (userIndex !== -1) {
      userList.value[userIndex].verified = verifyForm.result
    }
    
    // 如果是在详情对话框中的用户，也要更新
    if (currentUser.value && currentUser.value.id === verifyUser.value.id) {
      currentUser.value.verified = verifyForm.result
    }
    
  } catch (error) {
    console.error('提交审核失败:', error)
    ElMessage.error('提交审核失败')
  }
}

// 导出用户数据
const exportUsers = async () => {
  try {
    ElMessage.info('正在导出用户数据...')
    // 调用导出API
    // await exportUserData(searchForm)
    ElMessage.success('用户数据导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchUsers()
}

// 处理页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchUsers()
}

// 手机号掩码
const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 组件挂载时获取数据
onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.user-management-page {
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
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
  
  .search-section {
    margin-bottom: 24px;
    
    .search-form {
      .el-form-item {
        margin-bottom: 16px;
      }
    }
  }
  
  .table-section {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .table-title {
        display: flex;
        align-items: center;
        gap: 12px;
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
        
        .total-count {
          font-size: 14px;
          color: var(--text-secondary);
        }
      }
    }
    
    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
}

// 用户详情对话框样式
.user-detail {
  .user-basic {
    display: flex;
    gap: 16px;
    align-items: center;
    
    .user-avatar {
      flex-shrink: 0;
    }
    
    .user-info {
      flex: 1;
      
      h3 {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 8px 0;
      }
      
      p {
        font-size: 14px;
        color: var(--text-secondary);
        margin: 0 0 12px 0;
      }
      
      .user-tags {
        display: flex;
        gap: 8px;
      }
    }
  }
  
  .user-details {
    .user-bio {
      margin-top: 20px;
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      p {
        font-size: 14px;
        color: var(--text-secondary);
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}

// 认证审核对话框样式
.verify-content {
  .verify-info {
    margin-bottom: 20px;
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 12px;
    }
    
    p {
      font-size: 14px;
      color: var(--text-secondary);
      margin: 8px 0;
    }
  }
  
  .verify-images {
    margin-bottom: 20px;
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 12px;
    }
    
    .image-grid {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      
      .image-item {
        border: 1px solid var(--border-color);
        border-radius: 8px;
        overflow: hidden;
      }
    }
  }
  
  .verify-form {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-management-page {
    padding: 16px;
    
    .search-form {
      .el-form-item {
        display: block;
        margin-right: 0;
        
        .el-input,
        .el-select,
        .el-date-picker {
          width: 100% !important;
        }
      }
    }
    
    .table-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }
  
  .user-detail {
    .user-basic {
      flex-direction: column;
      text-align: center;
    }
  }
  
  .verify-content {
    .image-grid {
      justify-content: center;
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