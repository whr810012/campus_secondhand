<template>
  <div class="user-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>用户管理</h2>
      
      <!-- 统计信息 -->
      <div class="stats-summary">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.activeUsers || 0 }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.newUsers || 0 }}</div>
          <div class="stat-label">今日新增</div>
        </div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索用户名、邮箱、手机号..."
          style="width: 300px"
          clearable
          @keyup.enter.native="handleSearch"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="handleSearch"
          ></el-button>
        </el-input>
        
        <el-select
          v-model="searchForm.status"
          placeholder="用户状态"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="正常" value="ACTIVE" />
          <el-option label="禁用" value="DISABLED" />
          <el-option label="锁定" value="LOCKED" />
        </el-select>
        
        <el-select
          v-model="searchForm.role"
          placeholder="用户角色"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部角色" value="" />
          <el-option label="普通用户" value="USER" />
          <el-option label="管理员" value="ADMIN" />
          <el-option label="超级管理员" value="SUPER_ADMIN" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="注册开始日期"
          end-placeholder="注册结束日期"
          @change="handleDateChange"
        />
        
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="showAddAdminDialog = true">
          <i class="el-icon-plus"></i>
          添加管理员
        </el-button>
      </div>
    </div>
    
    <!-- 用户列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="用户信息" min-width="200">
          <template slot-scope="scope">
            <div class="user-info">
              <el-avatar :size="40" :src="scope.row.avatar">
                {{ scope.row.username ? scope.row.username.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <div class="user-details">
                <div class="username">{{ scope.row.username || '未设置' }}</div>
                <div class="email">{{ scope.row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="phone" label="手机号" width="120">
          <template slot-scope="scope">
            {{ scope.row.phone || '未绑定' }}
          </template>
        </el-table-column>
        
        <el-table-column label="角色" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getRoleType(scope.row.role)"
              size="small"
            >
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="统计信息" width="120">
          <template slot-scope="scope">
            <div class="user-stats">
              <div class="stat-line">
                <span class="stat-label">商品:</span>
                <span class="stat-value">{{ scope.row.productCount || 0 }}</span>
              </div>
              <div class="stat-line">
                <span class="stat-label">订单:</span>
                <span class="stat-value">{{ scope.row.orderCount || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="lastLoginTime" label="最后登录" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.lastLoginTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewUserDetail(scope.row)"
            >
              查看
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'ACTIVE'"
              type="text"
              size="small"
              class="warning-btn"
              @click="disableUser(scope.row)"
            >
              禁用
            </el-button>
            
            <el-button
              v-else-if="scope.row.status === 'DISABLED'"
              type="text"
              size="small"
              class="success-btn"
              @click="enableUser(scope.row)"
            >
              启用
            </el-button>
            
            <el-button
              v-if="canPromoteToAdmin(scope.row)"
              type="text"
              size="small"
              @click="promoteToAdmin(scope.row)"
            >
              设为管理员
            </el-button>
            
            <el-button
              v-if="canDelete(scope.row)"
              type="text"
              size="small"
              class="danger-btn"
              @click="deleteUser(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedUsers.length > 0">
        <span class="selected-info">已选择 {{ selectedUsers.length }} 个用户</span>
        <el-button size="small" @click="batchDisable">批量禁用</el-button>
        <el-button size="small" @click="batchEnable">批量启用</el-button>
        <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>
    
    <!-- 用户详情对话框 -->
    <el-dialog
      title="用户详情"
      :visible.sync="showDetailDialog"
      width="600px"
    >
      <div v-if="currentUser" class="user-detail">
        <div class="detail-header">
          <el-avatar :size="60" :src="currentUser.avatar">
            {{ currentUser.username ? currentUser.username.charAt(0).toUpperCase() : 'U' }}
          </el-avatar>
          <div class="user-basic">
            <h3>{{ currentUser.username || '未设置用户名' }}</h3>
            <p>{{ currentUser.email }}</p>
            <div class="user-tags">
              <el-tag :type="getRoleType(currentUser.role)" size="small">
                {{ getRoleText(currentUser.role) }}
              </el-tag>
              <el-tag :type="getStatusType(currentUser.status)" size="small">
                {{ getStatusText(currentUser.status) }}
              </el-tag>
            </div>
          </div>
        </div>
        
        <div class="detail-content">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="手机号">
              {{ currentUser.phone || '未绑定' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ getGenderText(currentUser.gender) }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ formatTime(currentUser.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="最后登录">
              {{ formatTime(currentUser.lastLoginTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="发布商品">
              {{ currentUser.productCount || 0 }} 个
            </el-descriptions-item>
            <el-descriptions-item label="订单数量">
              {{ currentUser.orderCount || 0 }} 个
            </el-descriptions-item>
            <el-descriptions-item label="个人简介" :span="2">
              {{ currentUser.bio || '暂无简介' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
    
    <!-- 添加管理员对话框 -->
    <el-dialog
      title="添加管理员"
      :visible.sync="showAddAdminDialog"
      width="500px"
      @close="resetAdminForm"
    >
      <el-form
        ref="adminForm"
        :model="adminForm"
        :rules="adminRules"
        label-width="80px"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="adminForm.email"
            placeholder="请输入管理员邮箱"
          />
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="adminForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            type="password"
            v-model="adminForm.password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="adminForm.role">
            <el-radio label="ADMIN">普通管理员</el-radio>
            <el-radio label="SUPER_ADMIN" v-if="canCreateSuperAdmin">超级管理员</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddAdminDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddAdmin" :loading="addingAdmin">
          添加
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  getUserList,
  getUserStats,
  updateUserStatus,
  deleteUser,
  promoteToAdmin,
  createAdmin
} from '@/api/user'
import moment from 'moment'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      addingAdmin: false,
      showDetailDialog: false,
      showAddAdminDialog: false,
      searchForm: {
        keyword: '',
        status: '',
        role: '',
        startTime: '',
        endTime: ''
      },
      dateRange: null,
      users: [],
      selectedUsers: [],
      currentUser: null,
      stats: {},
      pagination: {
        current: 1,
        size: 20,
        total: 0
      },
      adminForm: {
        email: '',
        username: '',
        password: '',
        role: 'ADMIN'
      },
      adminRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    canCreateSuperAdmin() {
      return this.userInfo.role === 'SUPER_ADMIN'
    }
  },
  created() {
    this.loadUsers()
    this.loadUserStats()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        
        // 清理空值
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        
        const res = await getUserList(params)
        if (res.data.code === 200) {
          this.users = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载用户列表失败:', error)
        this.$message.error('加载用户列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadUserStats() {
      try {
        const res = await getUserStats()
        if (res.data.code === 200) {
          this.stats = res.data.data || {}
        }
      } catch (error) {
        console.error('加载用户统计失败:', error)
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadUsers()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        status: '',
        role: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = null
      this.pagination.current = 1
      this.loadUsers()
    },
    
    handleDateChange(dates) {
      if (dates && dates.length === 2) {
        this.searchForm.startTime = moment(dates[0]).format('YYYY-MM-DD')
        this.searchForm.endTime = moment(dates[1]).format('YYYY-MM-DD')
      } else {
        this.searchForm.startTime = ''
        this.searchForm.endTime = ''
      }
      this.handleSearch()
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.loadUsers()
    },
    
    handleCurrentChange(page) {
      this.pagination.current = page
      this.loadUsers()
    },
    
    handleSelectionChange(selection) {
      this.selectedUsers = selection
    },
    
    viewUserDetail(user) {
      this.currentUser = user
      this.showDetailDialog = true
    },
    
    async disableUser(user) {
      try {
        await this.$confirm(`确定要禁用用户 "${user.username || user.email}" 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await updateUserStatus(user.id, 'DISABLED')
        if (res.data.code === 200) {
          this.$message.success('用户已禁用')
          this.loadUsers()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('禁用用户失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    async enableUser(user) {
      try {
        const res = await updateUserStatus(user.id, 'ACTIVE')
        if (res.data.code === 200) {
          this.$message.success('用户已启用')
          this.loadUsers()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        console.error('启用用户失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    async promoteToAdmin(user) {
      try {
        await this.$confirm(`确定要将用户 "${user.username || user.email}" 设为管理员吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await promoteToAdmin(user.id)
        if (res.data.code === 200) {
          this.$message.success('用户已设为管理员')
          this.loadUsers()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('设置管理员失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    async deleteUser(user) {
      try {
        await this.$confirm(
          `确定要删除用户 "${user.username || user.email}" 吗？删除后无法恢复。`,
          '危险操作',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'error'
          }
        )
        
        const res = await deleteUser(user.id)
        if (res.data.code === 200) {
          this.$message.success('用户已删除')
          this.loadUsers()
        } else {
          this.$message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          this.$message.error('删除失败，请重试')
        }
      }
    },
    
    async batchDisable() {
      try {
        await this.$confirm(`确定要禁用选中的 ${this.selectedUsers.length} 个用户吗？`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const promises = this.selectedUsers.map(user => updateUserStatus(user.id, 'DISABLED'))
        await Promise.all(promises)
        
        this.$message.success('批量禁用成功')
        this.loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量禁用失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    async batchEnable() {
      try {
        const promises = this.selectedUsers.map(user => updateUserStatus(user.id, 'ACTIVE'))
        await Promise.all(promises)
        
        this.$message.success('批量启用成功')
        this.loadUsers()
      } catch (error) {
        console.error('批量启用失败:', error)
        this.$message.error('批量操作失败，请重试')
      }
    },
    
    async batchDelete() {
      try {
        await this.$confirm(
          `确定要删除选中的 ${this.selectedUsers.length} 个用户吗？删除后无法恢复。`,
          '危险操作',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'error'
          }
        )
        
        const promises = this.selectedUsers.map(user => deleteUser(user.id))
        await Promise.all(promises)
        
        this.$message.success('批量删除成功')
        this.loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    resetAdminForm() {
      this.adminForm = {
        email: '',
        username: '',
        password: '',
        role: 'ADMIN'
      }
      this.$nextTick(() => {
        this.$refs.adminForm && this.$refs.adminForm.clearValidate()
      })
    },
    
    async handleAddAdmin() {
      try {
        await this.$refs.adminForm.validate()
        
        this.addingAdmin = true
        const res = await createAdmin(this.adminForm)
        
        if (res.data.code === 200) {
          this.$message.success('管理员添加成功')
          this.showAddAdminDialog = false
          this.loadUsers()
        } else {
          this.$message.error(res.data.message || '添加失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('添加管理员失败:', error)
          this.$message.error('添加失败，请重试')
        }
      } finally {
        this.addingAdmin = false
      }
    },
    
    canPromoteToAdmin(user) {
      return user.role === 'USER' && this.userInfo.role === 'SUPER_ADMIN'
    },
    
    canDelete(user) {
      // 超级管理员不能删除，普通管理员只能由超级管理员删除
      if (user.role === 'SUPER_ADMIN') return false
      if (user.role === 'ADMIN') return this.userInfo.role === 'SUPER_ADMIN'
      return true
    },
    
    getRoleType(role) {
      const typeMap = {
        'USER': '',
        'ADMIN': 'warning',
        'SUPER_ADMIN': 'danger'
      }
      return typeMap[role] || ''
    },
    
    getRoleText(role) {
      const textMap = {
        'USER': '普通用户',
        'ADMIN': '管理员',
        'SUPER_ADMIN': '超级管理员'
      }
      return textMap[role] || role
    },
    
    getStatusType(status) {
      const typeMap = {
        'ACTIVE': 'success',
        'DISABLED': 'danger',
        'LOCKED': 'warning'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'ACTIVE': '正常',
        'DISABLED': '禁用',
        'LOCKED': '锁定'
      }
      return textMap[status] || status
    },
    
    getGenderText(gender) {
      const textMap = {
        'MALE': '男',
        'FEMALE': '女',
        'UNKNOWN': '保密'
      }
      return textMap[gender] || '保密'
    },
    
    formatTime(time) {
      if (!time) return '从未登录'
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.user-management-container {
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

.stats-summary {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

/* 表格区域 */
.table-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
}

.email {
  font-size: 12px;
  color: #666;
}

.user-stats {
  font-size: 12px;
}

.stat-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2px;
}

.stat-label {
  color: #666;
}

.stat-value {
  color: #409EFF;
  font-weight: 500;
}

.warning-btn {
  color: #e6a23c !important;
}

.success-btn {
  color: #67c23a !important;
}

.danger-btn {
  color: #f56c6c !important;
}

/* 批量操作 */
.batch-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.selected-info {
  font-size: 14px;
  color: #666;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

/* 用户详情 */
.user-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.user-basic h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 5px 0;
}

.user-basic p {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
}

.user-tags {
  display: flex;
  gap: 8px;
}

.detail-content {
  margin-top: 20px;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .stats-summary {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .user-management-container {
    padding: 10px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .stats-summary {
    gap: 20px;
  }
  
  .detail-header {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .stats-summary {
    flex-direction: column;
    gap: 15px;
  }
  
  .batch-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>