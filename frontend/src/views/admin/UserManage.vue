<template>
  <div class="user-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理平台用户和身份认证</p>
    </div>

    <!-- 用户统计 -->
    <div class="user-stats">
      <div class="stat-card">
        <div class="stat-icon total-icon">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalUsers }}</h3>
          <p>总用户数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon active-icon">
          <i class="fas fa-user-check"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.activeUsers }}</h3>
          <p>活跃用户</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon verified-icon">
          <i class="fas fa-certificate"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.verifiedUsers }}</h3>
          <p>已认证用户</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon banned-icon">
          <i class="fas fa-user-slash"></i>
        </div>
        <div class="stat-content">
          <h3>{{ stats.bannedUsers }}</h3>
          <p>封禁用户</p>
        </div>
      </div>
    </div>

    <!-- 管理选项卡 -->
    <div class="manage-tabs">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'users' }"
        @click="switchTab('users')"
      >
        用户列表
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'verification' }"
        @click="switchTab('verification')"
      >
        身份认证 ({{ stats.pendingVerifications }})
      </button>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <select v-model="filters.status" @change="fetchData">
          <option value="">全部状态</option>
          <option value="ACTIVE">正常</option>
          <option value="BANNED">封禁</option>
          <option value="INACTIVE">未激活</option>
        </select>
        
        <select v-model="filters.verified" @change="fetchData" v-if="activeTab === 'users'">
          <option value="">全部认证状态</option>
          <option value="true">已认证</option>
          <option value="false">未认证</option>
        </select>
        
        <select v-model="filters.verificationStatus" @change="fetchData" v-if="activeTab === 'verification'">
          <option value="">全部状态</option>
          <option value="PENDING">待审核</option>
          <option value="APPROVED">已通过</option>
          <option value="REJECTED">已拒绝</option>
        </select>
        
        <input 
          v-model="filters.keyword" 
          type="text" 
          placeholder="搜索用户名、手机号、学号" 
          @keyup.enter="fetchData"
        />
        
        <button @click="fetchData" class="search-btn">
          <i class="fas fa-search"></i>
        </button>
      </div>
      
      <div class="filter-right">
        <button @click="exportUsers" class="export-btn">
          <i class="fas fa-download"></i>
          导出数据
        </button>
      </div>
    </div>

    <!-- 用户列表 -->
    <div v-if="activeTab === 'users'" class="user-list">
      <div class="list-header">
        <span>共 {{ pagination.total }} 个用户</span>
      </div>
      
      <div class="users-table">
        <table>
          <thead>
            <tr>
              <th>用户信息</th>
              <th>联系方式</th>
              <th>认证状态</th>
              <th>账户状态</th>
              <th>注册时间</th>
              <th>最后登录</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="user-row">
              <td>
                <div class="user-info">
                  <img :src="user.avatar || '/default-avatar.png'" :alt="user.nickname" class="user-avatar" />
                  <div class="user-details">
                    <h4>{{ user.nickname }}</h4>
                    <p>ID: {{ user.id }}</p>
                    <p v-if="user.studentId">学号: {{ user.studentId }}</p>
                  </div>
                </div>
              </td>
              <td>
                <div class="contact-info">
                  <p><i class="fas fa-phone"></i> {{ user.phone }}</p>
                  <p v-if="user.email"><i class="fas fa-envelope"></i> {{ user.email }}</p>
                </div>
              </td>
              <td>
                <span class="verification-status" :class="user.verified ? 'verified' : 'unverified'">
                  <i class="fas" :class="user.verified ? 'fa-check-circle' : 'fa-times-circle'"></i>
                  {{ user.verified ? '已认证' : '未认证' }}
                </span>
              </td>
              <td>
                <span class="account-status" :class="user.status.toLowerCase()">
                  {{ getStatusText(user.status) }}
                </span>
              </td>
              <td>{{ formatTime(user.createdAt) }}</td>
              <td>{{ user.lastLoginAt ? formatTime(user.lastLoginAt) : '从未登录' }}</td>
              <td>
                <div class="user-actions">
                  <button @click="viewUser(user)" class="action-btn view">
                    <i class="fas fa-eye"></i>
                  </button>
                  <button 
                    @click="toggleUserStatus(user)" 
                    class="action-btn" 
                    :class="user.status === 'ACTIVE' ? 'ban' : 'unban'"
                  >
                    <i class="fas" :class="user.status === 'ACTIVE' ? 'fa-ban' : 'fa-check'"></i>
                  </button>
                  <button @click="resetPassword(user)" class="action-btn reset">
                    <i class="fas fa-key"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 身份认证列表 -->
    <div v-if="activeTab === 'verification'" class="verification-list">
      <div class="list-header">
        <span>共 {{ pagination.total }} 个认证申请</span>
      </div>
      
      <div class="verification-cards">
        <div 
          v-for="verification in verifications" 
          :key="verification.id" 
          class="verification-card"
        >
          <div class="card-header">
            <div class="user-info">
              <img :src="verification.user.avatar || '/default-avatar.png'" :alt="verification.user.nickname" class="user-avatar" />
              <div class="user-details">
                <h4>{{ verification.user.nickname }}</h4>
                <p>{{ verification.user.phone }}</p>
              </div>
            </div>
            <span class="verification-status" :class="verification.status.toLowerCase()">
              {{ getVerificationStatusText(verification.status) }}
            </span>
          </div>
          
          <div class="card-content">
            <div class="verification-info">
              <div class="info-row">
                <label>真实姓名：</label>
                <span>{{ verification.realName }}</span>
              </div>
              <div class="info-row">
                <label>学号：</label>
                <span>{{ verification.studentId }}</span>
              </div>
              <div class="info-row">
                <label>学校：</label>
                <span>{{ verification.school }}</span>
              </div>
              <div class="info-row">
                <label>专业：</label>
                <span>{{ verification.major }}</span>
              </div>
              <div class="info-row">
                <label>申请时间：</label>
                <span>{{ formatTime(verification.createdAt) }}</span>
              </div>
            </div>
            
            <div class="verification-images">
              <h5>认证材料</h5>
              <div class="images-grid">
                <div 
                  v-for="(image, index) in verification.images" 
                  :key="index" 
                  class="image-item"
                  @click="previewImage(image)"
                >
                  <img :src="image" alt="认证材料" />
                  <div class="image-overlay">
                    <i class="fas fa-search-plus"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="card-actions" v-if="verification.status === 'PENDING'">
            <button @click="approveVerification(verification.id)" class="action-btn approve">
              <i class="fas fa-check"></i>
              通过认证
            </button>
            <button @click="rejectVerification(verification.id)" class="action-btn reject">
              <i class="fas fa-times"></i>
              拒绝认证
            </button>
          </div>
          
          <div class="card-result" v-if="verification.status !== 'PENDING'">
            <div class="result-info">
              <p><strong>处理结果：</strong>{{ getVerificationStatusText(verification.status) }}</p>
              <p v-if="verification.rejectReason"><strong>拒绝原因：</strong>{{ verification.rejectReason }}</p>
              <p><strong>处理时间：</strong>{{ formatTime(verification.updatedAt) }}</p>
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

    <!-- 用户详情弹窗 -->
    <div v-if="showUserModal" class="modal-overlay" @click="closeUserModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>用户详情</h3>
          <button @click="closeUserModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedUser">
          <div class="user-profile">
            <img :src="selectedUser.avatar || '/default-avatar.png'" :alt="selectedUser.nickname" class="profile-avatar" />
            <div class="profile-info">
              <h4>{{ selectedUser.nickname }}</h4>
              <p>用户ID: {{ selectedUser.id }}</p>
              <p>手机号: {{ selectedUser.phone }}</p>
              <p v-if="selectedUser.email">邮箱: {{ selectedUser.email }}</p>
            </div>
          </div>
          
          <div class="user-stats-detail">
            <div class="stat-item">
              <span class="stat-label">发布商品</span>
              <span class="stat-value">{{ selectedUser.productCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">交易订单</span>
              <span class="stat-value">{{ selectedUser.orderCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">信誉评分</span>
              <span class="stat-value">{{ selectedUser.rating || 0 }}/5</span>
            </div>
          </div>
          
          <div class="user-timeline">
            <h5>用户动态</h5>
            <div class="timeline-list">
              <div 
                v-for="activity in selectedUser.activities" 
                :key="activity.id" 
                class="timeline-item"
              >
                <div class="timeline-icon">
                  <i class="fas" :class="getActivityIcon(activity.type)"></i>
                </div>
                <div class="timeline-content">
                  <p>{{ activity.description }}</p>
                  <span class="timeline-time">{{ formatTime(activity.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览弹窗 -->
    <div v-if="showImageModal" class="modal-overlay" @click="closeImageModal">
      <div class="image-modal" @click.stop>
        <button @click="closeImageModal" class="close-btn">
          <i class="fas fa-times"></i>
        </button>
        <img :src="previewImageUrl" alt="预览图片" />
      </div>
    </div>

    <!-- 拒绝认证弹窗 -->
    <div v-if="showRejectModal" class="modal-overlay" @click="closeRejectModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>拒绝认证</h3>
          <button @click="closeRejectModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="reject-reasons">
            <h5>拒绝原因</h5>
            <label v-for="reason in rejectReasons" :key="reason" class="reason-option">
              <input 
                type="checkbox" 
                :value="reason" 
                v-model="selectedReasons"
              />
              <span>{{ reason }}</span>
            </label>
          </div>
          
          <div class="custom-reason">
            <label>其他原因：</label>
            <textarea 
              v-model="customReason" 
              placeholder="请输入具体的拒绝原因" 
              rows="3"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeRejectModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="confirmReject" class="modal-btn reject">
            确认拒绝
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api'

export default {
  name: 'UserManage',
  setup() {
    const activeTab = ref('users')
    const loading = ref(false)
    const users = ref([])
    const verifications = ref([])
    const selectedUser = ref(null)
    const showUserModal = ref(false)
    const showImageModal = ref(false)
    const showRejectModal = ref(false)
    const previewImageUrl = ref('')
    const rejectVerificationId = ref(null)
    const selectedReasons = ref([])
    const customReason = ref('')
    
    const stats = reactive({
      totalUsers: 0,
      activeUsers: 0,
      verifiedUsers: 0,
      bannedUsers: 0,
      pendingVerifications: 0
    })
    
    const filters = reactive({
      status: '',
      verified: '',
      verificationStatus: 'PENDING',
      keyword: ''
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
      pages: 0
    })
    
    const rejectReasons = [
      '证件信息不清晰',
      '证件信息不匹配',
      '学号格式错误',
      '学校信息有误',
      '材料不完整',
      '疑似虚假信息'
    ]

    // 切换选项卡
    const switchTab = (tab) => {
      activeTab.value = tab
      pagination.current = 1
      
      // 重置筛选条件
      if (tab === 'verification') {
        filters.verificationStatus = 'PENDING'
      }
      
      fetchData()
    }

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/users/stats')
        Object.assign(stats, response.data)
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    }

    // 获取数据
    const fetchData = async () => {
      loading.value = true
      try {
        const endpoint = activeTab.value === 'users' ? '/admin/users' : '/admin/verifications'
        const params = {
          page: pagination.current,
          size: pagination.size,
          ...filters
        }
        
        const response = await api.get(endpoint, { params })
        
        if (activeTab.value === 'users') {
          users.value = response.data.records
        } else {
          verifications.value = response.data.records
        }
        
        pagination.total = response.data.total
        pagination.pages = response.data.pages
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 查看用户详情
    const viewUser = async (user) => {
      try {
        const response = await api.get(`/admin/users/${user.id}/detail`)
        selectedUser.value = response.data
        showUserModal.value = true
      } catch (error) {
        console.error('获取用户详情失败:', error)
        alert('获取用户详情失败')
      }
    }

    // 切换用户状态
    const toggleUserStatus = async (user) => {
      const action = user.status === 'ACTIVE' ? '封禁' : '解封'
      if (!confirm(`确定要${action}用户 ${user.nickname} 吗？`)) return
      
      try {
        const endpoint = user.status === 'ACTIVE' ? '/admin/users/ban' : '/admin/users/unban'
        await api.post(endpoint, { userId: user.id })
        
        alert(`${action}成功`)
        await fetchData()
        await fetchStats()
      } catch (error) {
        console.error(`${action}用户失败:`, error)
        alert(`${action}用户失败，请重试`)
      }
    }

    // 重置密码
    const resetPassword = async (user) => {
      if (!confirm(`确定要重置用户 ${user.nickname} 的密码吗？`)) return
      
      try {
        const response = await api.post('/admin/users/reset-password', { userId: user.id })
        alert(`密码重置成功，新密码：${response.data.newPassword}`)
      } catch (error) {
        console.error('重置密码失败:', error)
        alert('重置密码失败，请重试')
      }
    }

    // 通过认证
    const approveVerification = async (id) => {
      if (!confirm('确定要通过这个认证申请吗？')) return
      
      try {
        await api.post('/admin/verifications/approve', { id })
        alert('认证通过成功')
        await fetchData()
        await fetchStats()
      } catch (error) {
        console.error('认证通过失败:', error)
        alert('认证通过失败，请重试')
      }
    }

    // 拒绝认证
    const rejectVerification = (id) => {
      rejectVerificationId.value = id
      selectedReasons.value = []
      customReason.value = ''
      showRejectModal.value = true
    }

    // 确认拒绝认证
    const confirmReject = async () => {
      if (selectedReasons.value.length === 0 && !customReason.value.trim()) {
        alert('请选择或填写拒绝原因')
        return
      }
      
      try {
        const reasons = [...selectedReasons.value]
        if (customReason.value.trim()) {
          reasons.push(customReason.value.trim())
        }
        
        await api.post('/admin/verifications/reject', {
          id: rejectVerificationId.value,
          reason: reasons.join('; ')
        })
        
        alert('认证拒绝成功')
        await fetchData()
        await fetchStats()
        closeRejectModal()
      } catch (error) {
        console.error('认证拒绝失败:', error)
        alert('认证拒绝失败，请重试')
      }
    }

    // 导出用户数据
    const exportUsers = async () => {
      try {
        const response = await api.get('/admin/users/export', {
          params: filters,
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `用户数据_${new Date().toISOString().slice(0, 10)}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
        alert('导出失败，请重试')
      }
    }

    // 预览图片
    const previewImage = (imageUrl) => {
      previewImageUrl.value = imageUrl
      showImageModal.value = true
    }

    // 关闭弹窗
    const closeUserModal = () => {
      showUserModal.value = false
      selectedUser.value = null
    }

    const closeImageModal = () => {
      showImageModal.value = false
      previewImageUrl.value = ''
    }

    const closeRejectModal = () => {
      showRejectModal.value = false
      rejectVerificationId.value = null
    }

    // 分页
    const changePage = (page) => {
      pagination.current = page
      fetchData()
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        ACTIVE: '正常',
        BANNED: '封禁',
        INACTIVE: '未激活'
      }
      return statusMap[status] || status
    }

    // 获取认证状态文本
    const getVerificationStatusText = (status) => {
      const statusMap = {
        PENDING: '待审核',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
      }
      return statusMap[status] || status
    }

    // 获取活动图标
    const getActivityIcon = (type) => {
      const icons = {
        login: 'fa-sign-in-alt',
        product: 'fa-plus-circle',
        order: 'fa-shopping-cart',
        comment: 'fa-comment'
      }
      return icons[type] || 'fa-info-circle'
    }

    // 格式化时间
    const formatTime = (time) => {
      return new Date(time).toLocaleString('zh-CN')
    }

    onMounted(() => {
      fetchStats()
      fetchData()
    })

    return {
      activeTab,
      loading,
      users,
      verifications,
      selectedUser,
      showUserModal,
      showImageModal,
      showRejectModal,
      previewImageUrl,
      selectedReasons,
      customReason,
      rejectReasons,
      stats,
      filters,
      pagination,
      switchTab,
      fetchData,
      viewUser,
      toggleUserStatus,
      resetPassword,
      approveVerification,
      rejectVerification,
      confirmReject,
      exportUsers,
      previewImage,
      closeUserModal,
      closeImageModal,
      closeRejectModal,
      changePage,
      getStatusText,
      getVerificationStatusText,
      getActivityIcon,
      formatTime
    }
  }
}
</script>

<style scoped>
.user-manage {
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

.user-stats {
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

.active-icon {
  background: #28a745;
}

.verified-icon {
  background: #17a2b8;
}

.banned-icon {
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

.manage-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
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
}

.filter-left select,
.filter-left input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn,
.export-btn {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-list,
.verification-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.list-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  color: #666;
}

.users-table {
  overflow-x: auto;
}

.users-table table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.users-table th {
  background: #f8f9fa;
  font-weight: 500;
  color: #333;
}

.user-row:hover {
  background-color: #f8f9fa;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details h4 {
  margin: 0 0 0.25rem 0;
  font-size: 0.9rem;
  color: #333;
}

.user-details p {
  margin: 0;
  font-size: 0.8rem;
  color: #666;
}

.contact-info p {
  margin: 0 0 0.25rem 0;
  font-size: 0.8rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.verification-status,
.account-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.verification-status.verified {
  background: #d4edda;
  color: #155724;
}

.verification-status.unverified {
  background: #f8d7da;
  color: #721c24;
}

.account-status.active {
  background: #d4edda;
  color: #155724;
}

.account-status.banned {
  background: #f8d7da;
  color: #721c24;
}

.account-status.inactive {
  background: #fff3cd;
  color: #856404;
}

.user-actions {
  display: flex;
  gap: 0.25rem;
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

.action-btn.ban {
  background: #dc3545;
  color: white;
}

.action-btn.unban {
  background: #28a745;
  color: white;
}

.action-btn.reset {
  background: #ffc107;
  color: #333;
}

.action-btn.approve {
  background: #28a745;
  color: white;
}

.action-btn.reject {
  background: #dc3545;
  color: white;
}

.verification-cards {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.verification-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  background: #fafafa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.card-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 1rem;
}

.verification-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-row {
  display: flex;
  gap: 0.5rem;
}

.info-row label {
  font-weight: 500;
  color: #333;
  min-width: 80px;
}

.info-row span {
  color: #666;
}

.verification-images h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 0.5rem;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.card-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border-left: 4px solid #007bff;
}

.result-info p {
  margin: 0 0 0.5rem 0;
  color: #666;
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

.user-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.profile-info h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.profile-info p {
  margin: 0 0 0.25rem 0;
  color: #666;
}

.user-stats-detail {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.user-stats-detail .stat-item {
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.stat-label {
  display: block;
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.stat-value {
  display: block;
  color: #333;
  font-size: 1.2rem;
  font-weight: 600;
}

.user-timeline h5 {
  margin: 0 0 1rem 0;
  color: #333;
}

.timeline-list {
  max-height: 300px;
  overflow-y: auto;
}

.timeline-item {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.timeline-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
}

.timeline-content p {
  margin: 0 0 0.25rem 0;
  color: #333;
  font-size: 0.9rem;
}

.timeline-time {
  color: #999;
  font-size: 0.8rem;
}

.image-modal {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.image-modal img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-modal .close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.reject-reasons {
  margin-bottom: 1rem;
}

.reject-reasons h5 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.reason-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  cursor: pointer;
}

.custom-reason label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.custom-reason textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
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

.modal-btn.approve {
  background: #28a745;
  color: white;
}

.modal-btn.reject {
  background: #dc3545;
  color: white;
}

.modal-btn.cancel {
  background: #6c757d;
  color: white;
}

@media (max-width: 768px) {
  .user-manage {
    padding: 1rem;
  }
  
  .user-stats {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
  
  .users-table {
    font-size: 0.8rem;
  }
  
  .card-content {
    grid-template-columns: 1fr;
  }
  
  .user-stats-detail {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
}
</style>