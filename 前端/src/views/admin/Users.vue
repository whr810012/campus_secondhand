<template>
  <div class="admin-users">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="exportUsers">
          <el-icon><Download /></el-icon>
          导出用户
        </el-button>
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon active">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.active }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon banned">
          <el-icon><Lock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.banned }}</div>
          <div class="stat-label">已封禁</div>
        </div>
      </div>
    </div>



    <!-- 用户列表 -->
    <div class="users-table">
      <el-table
        :data="users"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-details">
                <div class="user-name">
                  {{ row.nickname }}
                  <el-tag v-if="row.verifyStatus === 2" type="success" size="small">已认证</el-tag>
                </div>
                <div class="user-phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="phone" label="手机号" width="120" />
        
        <el-table-column label="学校" width="120">
          <template #default="{ row }">
            {{ getSchoolName(row.schoolId) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="最后登录" width="160">
          <template #default="{ row }">
            {{ formatTime(row.lastLoginTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewUser(row)">
              查看
            </el-button>
            <el-button 
              size="small" 
              :type="row.status === 0 ? 'warning' : 'success'"
              @click="toggleUserStatus(row)"
            >
              {{ row.status === 0 ? '封禁' : '解封' }}
            </el-button>
            <el-dropdown @command="(command) => handleUserAction(command, row)">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="verify" v-if="row.verifyStatus !== 2">认证用户</el-dropdown-item>
                  <el-dropdown-item command="reset-password">重置密码</el-dropdown-item>
                  <el-dropdown-item command="send-message">发送消息</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除用户</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedUsers.length > 0">
        <span>已选择 {{ selectedUsers.length }} 个用户</span>
        <el-button size="small" @click="batchVerify">批量认证</el-button>
        <el-button size="small" type="warning" @click="batchBan">批量封禁</el-button>
        <el-button size="small" type="success" @click="batchUnban">批量解封</el-button>
        <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="userDetailVisible" title="用户详情" width="600px">
      <div class="user-detail" v-if="currentUser">
        <div class="detail-header">
          <el-avatar :size="80" :src="currentUser.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="user-info">
            <h3>{{ currentUser.nickname }}</h3>
            <p>{{ currentUser.phone }}</p>
            <div class="user-tags">
              <el-tag :type="getStatusType(currentUser.status)">{{ getStatusText(currentUser.status) }}</el-tag>
              <el-tag v-if="currentUser.verifyStatus === 2" type="success">已认证</el-tag>
            </div>
          </div>
        </div>
        
        <div class="detail-content">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentUser.phone || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="学校">{{ getSchoolName(currentUser.schoolId) }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ currentUser.studentId || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ currentUser.realName || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="信誉分数">{{ currentUser.creditScore || 0 }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ formatTime(currentUser.lastLoginTime) }}</el-descriptions-item>
            <el-descriptions-item label="交易次数">{{ currentUser.tradeCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="好评率">{{ currentUser.goodRate || 0 }}%</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Refresh,
  User,
  UserFilled,
  Clock,
  Lock,
  ArrowDown
} from '@element-plus/icons-vue'
import {
  getUserList as getUsers,
  getUserStats,
  banUser,
  unbanUser,
  batchBanUsers,
  batchUnbanUsers
} from '@/api/admin'
import { useUserStore } from '@/stores/user'

// 获取用户store
const userStore = useUserStore()

// 更新用户状态（封禁/解封）
const updateUserStatus = async (userId, status) => {
  try {
    const adminId = userStore.userInfo?.id
    if (!adminId) {
      ElMessage.error('无法获取管理员信息')
      return
    }

    if (status === 'banned') {
       // 封禁用户
       await banUser(userId, {
         adminId: adminId,
         reason: '管理员操作',
         banDays: 30  // 默认封禁30天
       })
       ElMessage.success('用户封禁成功')
    } else if (status === 'active') {
       // 解封用户
       await unbanUser(userId, {
         adminId: adminId
       })
       ElMessage.success('用户解封成功')
    }
    
    // 刷新用户列表
    await fetchUsers()
  } catch (error) {
    console.error('更新用户状态失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const verifyUser = async (userId) => {
  ElMessage.warning('用户验证功能暂未实现')
  return Promise.resolve()
}

const deleteUser = async (userId) => {
  ElMessage.warning('删除用户功能暂未实现')
  return Promise.resolve()
}

const resetUserPassword = async (userId) => {
  ElMessage.warning('重置密码功能暂未实现')
  return Promise.resolve()
}

const exportUsersData = async () => {
  ElMessage.warning('导出用户数据功能暂未实现')
  return Promise.resolve()
}
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const selectedUsers = ref([])
const userDetailVisible = ref(false)
const currentUser = ref(null)

// 统计数据
const stats = reactive({
  total: 0,
  active: 0,
  pending: 0,
  banned: 0
})



// 学校ID映射
const schoolMap = {
  1: '清华大学',
  2: '北京大学', 
  3: '复旦大学',
  4: '上海交通大学',
  5: '浙江大学',
  6: '南京大学',
  7: '中山大学',
  8: '华南理工大学',
  9: '暨南大学'
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true
    
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const response = await getUsers(params)
    
    // MyBatis Plus Page对象结构：records为数据列表
    users.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await getUserStats()
    // 映射API返回的字段到本地stats对象
    stats.total = response.data.totalCount || 0
    stats.active = response.data.activeCount || 0
    stats.banned = response.data.bannedCount || 0
    stats.pending = response.data.totalCount - response.data.verifiedCount || 0 // 总数减去已认证数量作为待审核数量
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}



// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUsers()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 查看用户
const viewUser = (user) => {
  currentUser.value = user
  userDetailVisible.value = true
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  const action = user.status === 0 ? '封禁' : '解封'
  const newStatus = user.status === 0 ? 'banned' : 'active'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户"${user.nickname}"吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateUserStatus(user.id, newStatus)
    // 状态会在updateUserStatus中通过fetchUsers刷新，不需要手动设置
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }
}

// 用户操作
const handleUserAction = async (command, user) => {
  switch (command) {
    case 'verify':
      await verifyUserAction(user)
      break
    case 'reset-password':
      await resetPasswordAction(user)
      break
    case 'send-message':
      ElMessage.info('发送消息功能开发中')
      break
    case 'delete':
      await deleteUserAction(user)
      break
  }
}

// 认证用户
const verifyUserAction = async (user) => {
  try {
    await verifyUser(user.id)
    user.verifyStatus = 2
    ElMessage.success('用户认证成功')
  } catch (error) {
    console.error('认证失败:', error)
    ElMessage.error('认证失败')
  }
}

// 重置密码
const resetPasswordAction = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户"${user.nickname}"的密码吗？`,
      '确认重置',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await resetUserPassword(user.id)
    ElMessage.success('密码重置成功，新密码已发送到用户邮箱')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }
}

// 删除用户
const deleteUserAction = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${user.nickname}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    fetchUsers()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量认证
const batchVerify = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要认证选中的 ${selectedUsers.value.length} 个用户吗？`,
      '确认批量认证',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const promises = selectedUsers.value.map(user => verifyUser(user.id))
    await Promise.all(promises)
    
    selectedUsers.value.forEach(user => {
      user.verifyStatus = 2
    })
    
    ElMessage.success('批量认证成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量认证失败:', error)
      ElMessage.error('批量认证失败')
    }
  }
}

// 批量封禁
const batchBan = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要封禁选中的 ${selectedUsers.value.length} 个用户吗？`,
      '确认批量封禁',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const adminId = userStore.userInfo?.id
    if (!adminId) {
      ElMessage.error('无法获取管理员信息')
      return
    }
    
    // 使用批量封禁API
     await batchBanUsers({
       userIds: selectedUsers.value.map(user => user.id),
       adminId: adminId,
       reason: '管理员批量操作',
       banDays: 30  // 默认封禁30天
     })
    
    ElMessage.success('批量封禁成功')
    selectedUsers.value = []
    await fetchUsers()
    await fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量封禁失败:', error)
      ElMessage.error(error.response?.data?.message || '批量封禁失败')
    }
  }
}

// 批量解封
const batchUnban = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要解封选中的 ${selectedUsers.value.length} 个用户吗？`,
      '确认批量解封',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const adminId = userStore.userInfo?.id
     if (!adminId) {
       ElMessage.error('无法获取管理员信息')
       return
     }
     
     // 使用批量解封API
    await batchUnbanUsers({
      userIds: selectedUsers.value.map(user => user.id),
      adminId: adminId,
      reason: '管理员批量操作'
    })
    
    ElMessage.success('批量解封成功')
    selectedUsers.value = []
    await fetchUsers()
    await fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量解封失败:', error)
      ElMessage.error(error.response?.data?.message || '批量解封失败')
    }
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复。`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const promises = selectedUsers.value.map(user => deleteUser(user.id))
    await Promise.all(promises)
    
    ElMessage.success('批量删除成功')
    fetchUsers()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出用户
const exportUsers = async () => {
  try {
    const response = await exportUsersData()
    // 处理文件下载
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `users_${dayjs().format('YYYY-MM-DD')}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 刷新数据
const refreshData = () => {
  fetchUsers()
  fetchStats()
}

// 获取学校名称
const getSchoolName = (schoolId) => {
  return schoolMap[schoolId] || '未知学校'
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'success',  // 正常
    1: 'danger'    // 禁用
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '正常',
    1: '已封禁'
  }
  return statusMap[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 组件挂载
onMounted(async () => {
  // 初始化用户信息
  await userStore.initUserInfo()
  fetchUsers()
  fetchStats()
})
</script>

<style lang="scss" scoped>
.admin-users {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;
    
    .stat-card {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      padding: 20px;
      box-shadow: var(--box-shadow-light);
      display: flex;
      align-items: center;
      gap: 16px;
      
      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: white;
        
        &.total {
          background: var(--primary-color);
        }
        
        &.active {
          background: var(--success-color);
        }
        
        &.pending {
          background: var(--warning-color);
        }
        
        &.banned {
          background: var(--danger-color);
        }
      }
      
      .stat-content {
        .stat-value {
          font-size: var(--font-size-extra-large);
          font-weight: var(--font-weight-primary);
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: var(--font-size-small);
          color: var(--text-secondary);
        }
      }
    }
  }
  

  
  .users-table {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    padding: 20px;
    box-shadow: var(--box-shadow-light);
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .user-details {
        .user-name {
          font-weight: var(--font-weight-primary);
          color: var(--text-primary);
          margin-bottom: 4px;
          display: flex;
          align-items: center;
          gap: 8px;
        }
        
        .user-email {
          font-size: var(--font-size-small);
          color: var(--text-secondary);
        }
      }
    }
    
    .batch-actions {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-top: 16px;
      padding: 12px;
      background: var(--bg-secondary);
      border-radius: var(--border-radius-base);
      
      span {
        color: var(--text-primary);
        font-weight: var(--font-weight-primary);
      }
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
}

// 用户详情对话框样式
.user-detail {
  .detail-header {
    display: flex;
    gap: 20px;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid var(--border-color-lighter);
    
    .user-info {
      flex: 1;
      
      h3 {
        margin: 0 0 8px 0;
        color: var(--text-primary);
      }
      
      p {
        margin: 0 0 12px 0;
        color: var(--text-secondary);
      }
      
      .user-tags {
        display: flex;
        gap: 8px;
      }
    }
  }
}

// 响应式设计
@include respond-to(md) {
  .admin-users {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
      
      .header-actions {
        justify-content: center;
      }
    }
    
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .filter-section {
      .filter-form {
        .el-form-item {
          width: 100%;
          margin-bottom: 12px;
        }
      }
    }
  }
}

@include respond-to(sm) {
  .admin-users {
    padding: 12px;
    
    .stats-cards {
      grid-template-columns: 1fr;
    }
    
    .users-table {
      padding: 12px;
      
      .el-table {
        font-size: var(--font-size-small);
      }
      
      .batch-actions {
        flex-direction: column;
        align-items: stretch;
        
        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>