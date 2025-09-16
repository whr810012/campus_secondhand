<template>
  <div class="chat-page">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="container">
        <div class="header-content">
          <div class="back-btn" @click="$router.go(-1)">
            <el-icon><ArrowLeft /></el-icon>
          </div>
          <div class="chat-info">
            <div class="user-avatar">
              <el-avatar :size="40" :src="chatUser?.avatar" />
            </div>
            <div class="user-details">
              <h3 class="username">{{ chatUser?.nickname }}</h3>
              <p class="user-status">{{ chatUser?.isOnline ? '在线' : '离线' }}</p>
            </div>
          </div>
          <div class="chat-actions">
            <el-button @click="viewUserProfile" size="small">查看资料</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品信息卡片 -->
    <div class="product-card" v-if="product">
      <div class="container">
        <div class="product-info" @click="viewProduct">
          <img :src="product.images[0]" :alt="product.title" class="product-image" />
          <div class="product-details">
            <h4 class="product-title">{{ product.title }}</h4>
            <p class="product-price">¥{{ product.price }}</p>
            <span class="product-status" :class="`status-${product.status}`">
              {{ getStatusText(product.status) }}
            </span>
          </div>
          <div class="product-actions">
            <el-button 
              v-if="product.status === 'available' && !isOwner" 
              @click.stop="buyNow" 
              type="primary" 
              size="small"
            >
              立即购买
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天消息区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div class="container">
        <div class="messages-list">
          <div 
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ 'own-message': message.senderId === currentUserId }"
          >
            <div class="message-avatar">
              <el-avatar :size="32" :src="message.sender?.avatar" />
            </div>
            <div class="message-content">
              <div class="message-info">
                <span class="sender-name">{{ message.sender?.nickname }}</span>
                <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              </div>
              <div class="message-bubble">
                <div class="message-text" v-if="message.type === 'text'">
                  {{ message.content }}
                </div>
                <div class="message-image" v-else-if="message.type === 'image'">
                  <img :src="message.content" @click="previewImage(message.content)" />
                </div>
                <div class="message-system" v-else-if="message.type === 'system'">
                  {{ message.content }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 加载更多 -->
          <div class="load-more" v-if="hasMore">
            <el-button @click="loadMoreMessages" :loading="loadingMore" size="small">
              加载更多消息
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 消息输入区域 -->
    <div class="chat-input">
      <div class="container">
        <div class="input-area">
          <div class="input-tools">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="uploadImage"
              accept="image/*"
            >
              <el-button :icon="Picture" size="small" circle />
            </el-upload>
          </div>
          <div class="input-box">
            <el-input
              v-model="messageText"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keydown.enter.exact="sendMessage"
              @keydown.enter.shift.exact.prevent="messageText += '\n'"
              maxlength="500"
              show-word-limit
            />
          </div>
          <div class="send-btn">
            <el-button 
              @click="sendMessage" 
              type="primary" 
              :disabled="!messageText.trim()"
              :loading="sending"
            >
              发送
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imagePreviewVisible"
      :url-list="[previewImageUrl]"
      @close="imagePreviewVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const chatUser = ref(null)
const product = ref(null)
const messages = ref([])
const messageText = ref('')
const sending = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const messagesContainer = ref(null)

// 图片预览
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// 当前用户ID
const currentUserId = userStore.user?.id
const isOwner = ref(false)

// WebSocket连接
let websocket = null

// 获取聊天信息
const fetchChatInfo = async () => {
  try {
    const { userId, productId } = route.params
    
    // 获取聊天用户信息
    const userResponse = await api.get(`/users/${userId}`)
    if (userResponse.data.code === 200) {
      chatUser.value = userResponse.data.data
    }
    
    // 获取商品信息
    if (productId) {
      const productResponse = await api.get(`/products/${productId}`)
      if (productResponse.data.code === 200) {
        product.value = productResponse.data.data
        isOwner.value = product.value.sellerId === currentUserId
      }
    }
    
    // 获取聊天记录
    await fetchMessages()
    
    // 建立WebSocket连接
    connectWebSocket()
    
  } catch (error) {
    console.error('获取聊天信息失败:', error)
    ElMessage.error('获取聊天信息失败')
  }
}

// 获取消息列表
const fetchMessages = async (page = 1) => {
  try {
    const { userId, productId } = route.params
    const params = {
      page,
      size: 20,
      userId,
      productId
    }
    
    const response = await api.get('/chat/messages', { params })
    if (response.data.code === 200) {
      const newMessages = response.data.data.records
      if (page === 1) {
        messages.value = newMessages
        await nextTick()
        scrollToBottom()
      } else {
        messages.value = [...newMessages, ...messages.value]
      }
      hasMore.value = newMessages.length === 20
    }
  } catch (error) {
    console.error('获取消息失败:', error)
    ElMessage.error('获取消息失败')
  }
}

// 加载更多消息
const loadMoreMessages = async () => {
  loadingMore.value = true
  currentPage.value++
  await fetchMessages(currentPage.value)
  loadingMore.value = false
}

// 发送消息
const sendMessage = async () => {
  if (!messageText.value.trim() || sending.value) return
  
  sending.value = true
  try {
    const { userId, productId } = route.params
    const messageData = {
      receiverId: userId,
      productId,
      type: 'text',
      content: messageText.value.trim()
    }
    
    await api.post('/chat/messages', messageData)
    messageText.value = ''
    
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sending.value = false
  }
}

// 上传图片
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

const uploadImage = async ({ file }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await api.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    if (response.data.code === 200) {
      const { userId, productId } = route.params
      const messageData = {
        receiverId: userId,
        productId,
        type: 'image',
        content: response.data.data.url
      }
      
      await api.post('/chat/messages', messageData)
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    ElMessage.error('上传图片失败')
  }
}

// WebSocket连接
const connectWebSocket = () => {
  const wsUrl = `ws://localhost:8080/ws/chat/${currentUserId}`
  websocket = new WebSocket(wsUrl)
  
  websocket.onopen = () => {
    console.log('WebSocket连接已建立')
  }
  
  websocket.onmessage = (event) => {
    const message = JSON.parse(event.data)
    messages.value.push(message)
    nextTick(() => {
      scrollToBottom()
    })
  }
  
  websocket.onclose = () => {
    console.log('WebSocket连接已关闭')
  }
  
  websocket.onerror = (error) => {
    console.error('WebSocket错误:', error)
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 查看商品
const viewProduct = () => {
  router.push(`/products/${product.value.id}`)
}

// 查看用户资料
const viewUserProfile = () => {
  ElMessage.info('查看用户资料功能开发中...')
}

// 立即购买
const buyNow = () => {
  router.push(`/products/${product.value.id}?action=buy`)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    available: '在售',
    sold: '已售出',
    reserved: '已预定',
    unavailable: '已下架'
  }
  return statusMap[status] || status
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

// 生命周期
onMounted(() => {
  fetchChatInfo()
})

onUnmounted(() => {
  if (websocket) {
    websocket.close()
  }
})
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.chat-header {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
  flex-shrink: 0;
}

.header-content {
  display: flex;
  align-items: center;
  padding: 15px 0;
  gap: 15px;
}

.back-btn {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background: var(--fill-light);
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.user-details h3 {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0;
}

.user-status {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.product-card {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
  flex-shrink: 0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
  cursor: pointer;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}

.product-details {
  flex: 1;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 5px 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: 16px;
  font-weight: 600;
  color: var(--danger-color);
  margin: 0 0 5px 0;
}

.product-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 3px;
}

.status-available {
  background: #f6ffed;
  color: #52c41a;
}

.status-sold {
  background: #f0f0f0;
  color: #999;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  display: flex;
  gap: 10px;
}

.message-item.own-message {
  flex-direction: row-reverse;
}

.message-item.own-message .message-content {
  align-items: flex-end;
}

.message-item.own-message .message-bubble {
  background: var(--primary-color);
  color: #fff;
}

.message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  max-width: 70%;
}

.message-info {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 5px;
}

.message-bubble {
  background: #fff;
  border-radius: 12px;
  padding: 10px 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-text {
  word-wrap: break-word;
  white-space: pre-wrap;
}

.message-image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 6px;
  cursor: pointer;
}

.message-system {
  font-style: italic;
  color: var(--text-secondary);
}

.load-more {
  text-align: center;
  padding: 10px 0;
}

.chat-input {
  background: #fff;
  border-top: 1px solid var(--border-light);
  flex-shrink: 0;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 15px 0;
}

.input-tools {
  display: flex;
  gap: 5px;
}

.input-box {
  flex: 1;
}

.send-btn {
  align-self: flex-end;
}

@media (max-width: 768px) {
  .chat-page {
    height: 100vh;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-area {
    flex-wrap: wrap;
  }
}
</style>