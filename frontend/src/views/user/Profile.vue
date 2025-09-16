<template>
  <div class="profile">
    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <el-avatar :src="userInfo.avatar" :size="80">{{ userInfo.nickname }}</el-avatar>
        <div class="profile-info">
          <h2>{{ userInfo.nickname }}</h2>
          <p class="school">{{ userInfo.school }}</p>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.publishCount }}</span>
              <span class="stat-label">发布</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.soldCount }}</span>
              <span class="stat-label">已售</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.favoriteCount }}</span>
              <span class="stat-label">收藏</span>
            </div>
          </div>
        </div>
        <el-button type="primary" @click="editDialogVisible = true">
          编辑资料
        </el-button>
      </div>
      
      <div class="profile-rating">
        <div class="rating-item">
          <span class="rating-label">信誉评分</span>
          <el-rate v-model="userInfo.rating" disabled show-score />
        </div>
        <div class="join-time">
          <span>加入时间：{{ formatDate(userInfo.createTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-grid">
      <div class="menu-item" @click="$router.push('/user/products')">
        <el-icon><Goods /></el-icon>
        <span>我的发布</span>
        <el-badge :value="userInfo.publishCount" :hidden="userInfo.publishCount === 0" />
      </div>
      <div class="menu-item" @click="$router.push('/user/orders')">
        <el-icon><ShoppingBag /></el-icon>
        <span>我的订单</span>
        <el-badge :value="userInfo.orderCount" :hidden="userInfo.orderCount === 0" />
      </div>
      <div class="menu-item" @click="$router.push('/user/favorites')">
        <el-icon><Star /></el-icon>
        <span>我的收藏</span>
        <el-badge :value="userInfo.favoriteCount" :hidden="userInfo.favoriteCount === 0" />
      </div>
      <div class="menu-item" @click="$router.push('/user/messages')">
        <el-icon><ChatDotRound /></el-icon>
        <span>消息中心</span>
        <el-badge :value="userInfo.unreadCount" :hidden="userInfo.unreadCount === 0" />
      </div>
      <div class="menu-item" @click="$router.push('/user/address')">
        <el-icon><Location /></el-icon>
        <span>收货地址</span>
      </div>
      <div class="menu-item" @click="$router.push('/user/settings')">
        <el-icon><Setting /></el-icon>
        <span>设置</span>
      </div>
    </div>

    <!-- 最近发布 -->
    <div class="recent-products">
      <div class="section-header">
        <h3>最近发布</h3>
        <el-button text @click="$router.push('/user/products')">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      
      <div v-if="recentProducts.length === 0" class="empty-state">
        <el-empty description="还没有发布任何商品">
          <el-button type="primary" @click="$router.push('/user/publish')">
            立即发布
          </el-button>
        </el-empty>
      </div>
      
      <div v-else class="products-grid">
        <div 
          v-for="product in recentProducts" 
          :key="product.id" 
          class="product-card"
          @click="$router.push(`/products/${product.id}`)"
        >
          <img :src="product.images[0]" :alt="product.title" class="product-image" />
          <div class="product-info">
            <h4 class="product-title">{{ product.title }}</h4>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-status">
              <el-tag 
                :type="getStatusType(product.status)" 
                size="small"
              >
                {{ getStatusText(product.status) }}
              </el-tag>
              <span class="product-time">{{ formatTime(product.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑个人资料" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload/avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" maxlength="20" show-word-limit />
        </el-form-item>
        
        <el-form-item label="学校" prop="school">
          <el-select v-model="editForm.school" placeholder="请选择学校" style="width: 100%">
            <el-option
              v-for="school in schools"
              :key="school.id"
              :label="school.name"
              :value="school.name"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.bio"
            type="textarea"
            :rows="3"
            maxlength="100"
            show-word-limit
            placeholder="介绍一下自己吧"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile" :loading="updateLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Goods, 
  ShoppingBag, 
  Star, 
  ChatDotRound, 
  Location, 
  Setting, 
  ArrowRight,
  Plus 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const editFormRef = ref()

// 响应式数据
const userInfo = reactive({
  id: '',
  nickname: '',
  avatar: '',
  school: '',
  bio: '',
  rating: 0,
  publishCount: 0,
  soldCount: 0,
  favoriteCount: 0,
  orderCount: 0,
  unreadCount: 0,
  createTime: ''
})

const recentProducts = ref([])
const schools = ref([])
const editDialogVisible = ref(false)
const updateLoading = ref(false)

const editForm = reactive({
  nickname: '',
  avatar: '',
  school: '',
  bio: ''
})

const editRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度应在2-20个字符之间', trigger: 'blur' }
  ],
  school: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ]
}

// 方法
const fetchUserInfo = async () => {
  try {
    const response = await api.get('/user/profile')
    Object.assign(userInfo, response.data)
    
    // 同步到编辑表单
    Object.assign(editForm, {
      nickname: userInfo.nickname,
      avatar: userInfo.avatar,
      school: userInfo.school,
      bio: userInfo.bio
    })
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    console.error('获取用户信息失败:', error)
  }
}

const fetchRecentProducts = async () => {
  try {
    const response = await api.get('/user/products/recent')
    recentProducts.value = response.data
  } catch (error) {
    console.error('获取最近发布失败:', error)
  }
}

const fetchSchools = async () => {
  try {
    const response = await api.get('/schools')
    schools.value = response.data
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

const handleUpdateProfile = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    
    updateLoading.value = true
    
    await api.put('/user/profile', editForm)
    
    // 更新本地数据
    Object.assign(userInfo, editForm)
    
    // 更新store中的用户信息
    await userStore.fetchUserInfo()
    
    ElMessage.success('个人资料更新成功')
    editDialogVisible.value = false
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('更新失败，请重试')
      console.error('更新个人资料失败:', error)
    }
  } finally {
    updateLoading.value = false
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    editForm.avatar = response.data.url
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'available': 'success',
    'sold': 'info',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '审核中',
    'available': '在售',
    'sold': '已售',
    'rejected': '已下架'
  }
  return statusMap[status] || '未知'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const formatTime = (dateString) => {
  const now = new Date()
  const date = new Date(dateString)
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return `${Math.floor(diff / 86400000)}天前`
  }
}

// 生命周期
onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  fetchUserInfo()
  fetchRecentProducts()
  fetchSchools()
})
</script>

<style scoped>
.profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.profile-info {
  flex: 1;
}

.profile-info h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 4px;
}

.school {
  color: #666;
  margin-bottom: 16px;
}

.stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.profile-rating {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-label {
  color: #666;
}

.join-time {
  color: #999;
  font-size: 14px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.menu-item {
  background: white;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.menu-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.menu-item .el-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 8px;
}

.menu-item span {
  display: block;
  color: #333;
  font-weight: 500;
}

.menu-item .el-badge {
  position: absolute;
  top: 16px;
  right: 16px;
}

.recent-products {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  color: #333;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.product-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 16px;
  font-weight: 600;
  color: #e74c3c;
  margin-bottom: 8px;
}

.product-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-time {
  font-size: 12px;
  color: #999;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  line-height: 80px;
}

.avatar {
  width: 80px;
  height: 80px;
  display: block;
  object-fit: cover;
}

@media (max-width: 768px) {
  .profile {
    padding: 16px;
  }
  
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .stats {
    justify-content: center;
  }
  
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .profile-rating {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>