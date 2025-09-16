<template>
  <div class="profile-container">
    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="80" :src="userInfo.avatar">
            {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
          </el-avatar>
          
          <el-upload
            class="avatar-uploader"
            action="/api/user/upload-avatar"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <el-button size="mini" type="text">
              <i class="el-icon-camera"></i>
              更换头像
            </el-button>
          </el-upload>
        </div>
        
        <div class="user-basic-info">
          <h2 class="username">{{ userInfo.username || '未设置用户名' }}</h2>
          <p class="user-email">{{ userInfo.email }}</p>
          <p class="join-time">加入时间：{{ formatTime(userInfo.createTime) }}</p>
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.productCount || 0 }}</div>
              <div class="stat-label">发布商品</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.likeCount || 0 }}</div>
              <div class="stat-label">获得点赞</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 功能导航 -->
    <div class="function-nav">
      <div class="nav-grid">
        <div class="nav-item" @click="$router.push('/user/my-products')">
          <div class="nav-icon">
            <i class="el-icon-goods"></i>
          </div>
          <div class="nav-text">我的商品</div>
          <div class="nav-count" v-if="stats.productCount">{{ stats.productCount }}</div>
        </div>
        
        <div class="nav-item" @click="$router.push('/user/my-orders')">
          <div class="nav-icon">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="nav-text">我的订单</div>
          <div class="nav-count" v-if="stats.orderCount">{{ stats.orderCount }}</div>
        </div>
        
        <div class="nav-item" @click="$router.push('/user/addresses')">
          <div class="nav-icon">
            <i class="el-icon-location"></i>
          </div>
          <div class="nav-text">地址管理</div>
          <div class="nav-count" v-if="stats.addressCount">{{ stats.addressCount }}</div>
        </div>
        
        <div class="nav-item" @click="showEditDialog = true">
          <div class="nav-icon">
            <i class="el-icon-edit"></i>
          </div>
          <div class="nav-text">编辑资料</div>
        </div>
        
        <div class="nav-item" @click="showPasswordDialog = true">
          <div class="nav-icon">
            <i class="el-icon-key"></i>
          </div>
          <div class="nav-text">修改密码</div>
        </div>
        
        <div class="nav-item" @click="$router.push('/user/shop')">
          <div class="nav-icon">
            <i class="el-icon-shopping-cart-2"></i>
          </div>
          <div class="nav-text">购物商城</div>
        </div>
      </div>
    </div>
    
    <!-- 最近活动 -->
    <div class="recent-activity">
      <div class="section-header">
        <h3>最近活动</h3>
        <el-button type="text" size="small" @click="loadActivities">
          <i class="el-icon-refresh"></i>
          刷新
        </el-button>
      </div>
      
      <div class="activity-list" v-if="activities.length > 0">
        <div
          v-for="activity in activities"
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon">
            <i :class="getActivityIcon(activity.type)"></i>
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-desc">{{ activity.description }}</div>
            <div class="activity-time">{{ formatTime(activity.createTime) }}</div>
          </div>
        </div>
      </div>
      
      <div v-else class="no-activity">
        <el-empty description="暂无活动记录" :image-size="80" />
      </div>
    </div>
    
    <!-- 编辑资料对话框 -->
    <el-dialog
      title="编辑个人资料"
      :visible.sync="showEditDialog"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        ref="editForm"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="editForm.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="editForm.nickname"
            placeholder="请输入昵称"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="editForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="MALE">男</el-radio>
            <el-radio label="FEMALE">女</el-radio>
            <el-radio label="UNKNOWN">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="个人简介" prop="bio">
          <el-input
            type="textarea"
            v-model="editForm.bio"
            placeholder="介绍一下自己吧..."
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
          保存
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="showPasswordDialog"
      width="400px"
      @close="resetPasswordForm"
    >
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="80px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            type="password"
            v-model="passwordForm.oldPassword"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            type="password"
            v-model="passwordForm.newPassword"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            type="password"
            v-model="passwordForm.confirmPassword"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
          确认修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import moment from 'moment'

export default {
  name: 'Profile',
  data() {
    return {
      stats: {},
      activities: [],
      showEditDialog: false,
      showPasswordDialog: false,
      updating: false,
      changingPassword: false,
      editForm: {
        username: '',
        nickname: '',
        phone: '',
        gender: 'UNKNOWN',
        bio: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      editRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
        ],
        nickname: [
          { max: 20, message: '昵称长度不能超过 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        bio: [
          { max: 200, message: '个人简介不能超过 200 个字符', trigger: 'blur' }
        ]
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
          { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'token']),
    uploadHeaders() {
      return {
        'Authorization': `Bearer ${this.token}`
      }
    }
  },
  created() {
    this.loadUserProfile()
    this.loadActivities()
  },
  methods: {
    ...mapActions(['updateUserInfo']),
    
    async loadUserProfile() {
      try {
        const res = await getUserInfo()
        if (res.data.code === 200) {
          const data = res.data.data
          this.stats = data.stats || {}
          // 更新 Vuex 中的用户信息
          this.$store.commit('SET_USER_INFO', data.userInfo)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    },
    
    loadActivities() {
      // 模拟活动数据，实际应该从 API 获取
      this.activities = [
        {
          id: 1,
          type: 'PRODUCT_PUBLISH',
          title: '发布了新商品',
          description: '《算法导论》- 计算机经典教材',
          createTime: new Date(Date.now() - 2 * 60 * 60 * 1000)
        },
        {
          id: 2,
          type: 'ORDER_CREATE',
          title: '创建了新订单',
          description: '购买了《数据结构与算法》',
          createTime: new Date(Date.now() - 5 * 60 * 60 * 1000)
        },
        {
          id: 3,
          type: 'PRODUCT_LIKE',
          title: '点赞了商品',
          description: '《机器学习实战》',
          createTime: new Date(Date.now() - 24 * 60 * 60 * 1000)
        }
      ]
    },
    
    getActivityIcon(type) {
      const iconMap = {
        'PRODUCT_PUBLISH': 'el-icon-goods',
        'ORDER_CREATE': 'el-icon-s-order',
        'PRODUCT_LIKE': 'el-icon-star-on',
        'ORDER_COMPLETE': 'el-icon-circle-check'
      }
      return iconMap[type] || 'el-icon-info'
    },
    
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.$message.success('头像更新成功')
        this.$store.commit('SET_USER_INFO', {
          ...this.userInfo,
          avatar: res.data.url
        })
      } else {
        this.$message.error(res.message || '头像上传失败')
      }
    },
    
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    
    resetEditForm() {
      this.editForm = {
        username: this.userInfo.username || '',
        nickname: this.userInfo.nickname || '',
        phone: this.userInfo.phone || '',
        gender: this.userInfo.gender || 'UNKNOWN',
        bio: this.userInfo.bio || ''
      }
      this.$nextTick(() => {
        this.$refs.editForm && this.$refs.editForm.clearValidate()
      })
    },
    
    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$nextTick(() => {
        this.$refs.passwordForm && this.$refs.passwordForm.clearValidate()
      })
    },
    
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    
    async handleUpdateProfile() {
      try {
        await this.$refs.editForm.validate()
        
        this.updating = true
        const res = await updateUserInfo(this.editForm)
        
        if (res.data.code === 200) {
          this.$message.success('个人资料更新成功')
          this.showEditDialog = false
          
          // 更新 Vuex 中的用户信息
          this.$store.commit('SET_USER_INFO', {
            ...this.userInfo,
            ...this.editForm
          })
        } else {
          this.$message.error(res.data.message || '更新失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('更新个人资料失败:', error)
          this.$message.error('更新失败，请重试')
        }
      } finally {
        this.updating = false
      }
    },
    
    async handleChangePassword() {
      try {
        await this.$refs.passwordForm.validate()
        
        this.changingPassword = true
        const res = await changePassword({
          oldPassword: this.passwordForm.oldPassword,
          newPassword: this.passwordForm.newPassword
        })
        
        if (res.data.code === 200) {
          this.$message.success('密码修改成功')
          this.showPasswordDialog = false
          this.resetPasswordForm()
        } else {
          this.$message.error(res.data.message || '密码修改失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('修改密码失败:', error)
          this.$message.error('修改失败，请重试')
        }
      } finally {
        this.changingPassword = false
      }
    },
    
    formatTime(time) {
      if (!time) return ''
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  },
  
  watch: {
    showEditDialog(val) {
      if (val) {
        this.resetEditForm()
      }
    },
    
    showPasswordDialog(val) {
      if (val) {
        this.resetPasswordForm()
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 个人信息卡片 */
.profile-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  grid-column: 1 / -1;
}

.profile-header {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.avatar-section {
  text-align: center;
}

.avatar-uploader {
  margin-top: 10px;
}

.user-basic-info {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 10px 0;
}

.user-email {
  font-size: 16px;
  color: #666;
  margin: 0 0 5px 0;
}

.join-time {
  font-size: 14px;
  color: #999;
  margin: 0 0 20px 0;
}

.user-stats {
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

/* 功能导航 */
.function-nav {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.nav-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.nav-icon {
  font-size: 24px;
  color: #409EFF;
  margin-bottom: 8px;
}

.nav-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.nav-count {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

/* 最近活动 */
.recent-activity {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s;
}

.activity-item:hover {
  background: #e9ecef;
}

.activity-icon {
  width: 40px;
  height: 40px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.activity-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.no-activity {
  text-align: center;
  padding: 40px 0;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
  
  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .user-stats {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }
  
  .profile-card,
  .function-nav,
  .recent-activity {
    padding: 20px;
  }
  
  .nav-grid {
    grid-template-columns: 1fr;
  }
  
  .user-stats {
    gap: 20px;
  }
  
  .username {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .nav-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .nav-item {
    padding: 15px 10px;
  }
  
  .nav-icon {
    font-size: 20px;
  }
  
  .nav-text {
    font-size: 12px;
  }
  
  .user-stats {
    gap: 15px;
  }
  
  .stat-value {
    font-size: 20px;
  }
}
</style>