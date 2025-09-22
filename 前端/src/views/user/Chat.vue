<template>
  <div class="chat-page">
    <div class="container">
      <div class="chat-container">
        <!-- 左侧聊天列表 -->
        <div class="chat-list">
          <div class="chat-header">
            <h3>聊天列表</h3>
            <el-button 
              type="primary" 
              size="small" 
              @click="refreshChatList"
              :loading="listLoading"
            >
              刷新
            </el-button>
          </div>
          
          <div class="chat-items" v-loading="listLoading">
            <div 
              v-for="chat in chatList" 
              :key="`${chat.userId}-${chat.productId}`"
              class="chat-item"
              :class="{ active: isActiveChatItem(chat) }"
              @click="selectChat(chat)"
            >
              <el-avatar :src="chat.avatar" :size="40">
                {{ chat.nickname?.charAt(0) }}
              </el-avatar>
              
              <div class="chat-info">
                <div class="chat-title">
                  <span class="nickname">{{ chat.nickname }}</span>
                  <span class="time">{{ formatTime(chat.lastMessageTime) }}</span>
                </div>
                
                <div class="chat-subtitle">
                  <span class="product-title">{{ chat.productTitle }}</span>
                  <el-badge 
                    v-if="chat.unreadCount > 0" 
                    :value="chat.unreadCount" 
                    class="unread-badge"
                  />
                </div>
                
                <div class="last-message">{{ chat.lastMessage }}</div>
              </div>
            </div>
            
            <el-empty v-if="chatList.length === 0 && !listLoading" description="暂无聊天记录" />
          </div>
        </div>
        
        <!-- 右侧聊天窗口 -->
        <div class="chat-window">
          <div v-if="!currentChat" class="empty-chat">
            <el-empty description="请选择一个聊天开始对话" />
          </div>
          
          <div v-else class="chat-content">
            <!-- 聊天头部 -->
            <div class="chat-window-header">
              <div class="chat-user-info">
                <el-avatar :src="currentChat.avatar" :size="32">
                  {{ currentChat.nickname?.charAt(0) }}
                </el-avatar>
                <div class="user-details">
                  <div class="nickname">{{ currentChat.nickname }}</div>
                  <div class="product-info">关于：{{ currentChat.productTitle }}</div>
                </div>
              </div>
              
              <div class="chat-actions">
                <el-button size="small" @click="viewProduct(currentChat.productId)">
                  查看商品
                </el-button>
                <el-button size="small" @click="markAllAsRead">
                  标记已读
                </el-button>
              </div>
            </div>
            
            <!-- 消息列表 -->
            <div class="messages-container" ref="messagesContainer" v-loading="messagesLoading">
              <div class="messages-list">
                <div 
                  v-for="message in messages" 
                  :key="message.id"
                  class="message-item"
                  :class="{ 
                    'own-message': message.senderId === userStore.userInfo?.id,
                    'other-message': message.senderId !== userStore.userInfo?.id
                  }"
                >
                  <div class="message-avatar">
                    <el-avatar 
                      v-if="message.senderId !== userStore.userInfo?.id" 
                      :src="currentChat.avatar" 
                      :size="28"
                    >
                      {{ currentChat.nickname?.charAt(0) }}
                    </el-avatar>
                  </div>
                  
                  <div class="message-content">
                    <div class="message-bubble">
                      <div class="message-text">{{ message.content }}</div>
                      <div class="message-time">{{ formatMessageTime(message.createdAt) }}</div>
                    </div>
                    
                    <div v-if="message.senderId === userStore.userInfo?.id" class="message-status">
                      <el-icon v-if="message.isRead" color="#409eff"><Select /></el-icon>
                      <el-icon v-else color="#909399"><Clock /></el-icon>
                    </div>
                  </div>
                </div>
                
                <el-empty v-if="messages.length === 0 && !messagesLoading" description="暂无消息" />
              </div>
            </div>
            
            <!-- 消息输入框 -->
            <div class="message-input">
              <el-input
                v-model="newMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keydown.ctrl.enter="sendNewMessage"
                maxlength="500"
                show-word-limit
              />
              
              <div class="input-actions">
                <span class="input-tip">Ctrl + Enter 发送</span>
                <el-button 
                  type="primary" 
                  @click="sendNewMessage"
                  :loading="sendLoading"
                  :disabled="!newMessage.trim()"
                >
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Select, Clock } from '@element-plus/icons-vue'
import { getChatList, getChatMessages, sendMessage, markChatAsRead } from '@/api/message'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const listLoading = ref(false)
const messagesLoading = ref(false)
const sendLoading = ref(false)
const chatList = ref([])
const messages = ref([])
const currentChat = ref(null)
const newMessage = ref('')
const messagesContainer = ref(null)

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const messagesPagination = reactive({
  page: 1,
  size: 50,
  total: 0
})

// 获取聊天列表
const fetchChatList = async () => {
  if (!userStore.userInfo?.id) return
  
  listLoading.value = true
  try {
    const response = await getChatList(userStore.userInfo.id, {
      page: pagination.page,
      size: pagination.size
    })
    
    chatList.value = response.data?.records || []
    pagination.total = response.data?.total || 0
  } catch (error) {
    console.error('获取聊天列表失败:', error)
    ElMessage.error('获取聊天列表失败')
  } finally {
    listLoading.value = false
  }
}

// 获取聊天消息
const fetchChatMessages = async () => {
  if (!currentChat.value || !userStore.userInfo?.id) return
  
  messagesLoading.value = true
  try {
    const response = await getChatMessages({
      userId1: userStore.userInfo.id,
      userId2: currentChat.value.userId,
      productId: currentChat.value.productId,
      page: messagesPagination.page,
      size: messagesPagination.size
    })
    
    messages.value = response.data?.records || []
    messagesPagination.total = response.data?.total || 0
    
    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('获取聊天消息失败:', error)
    ElMessage.error('获取聊天消息失败')
  } finally {
    messagesLoading.value = false
  }
}

// 选择聊天
const selectChat = (chat) => {
  currentChat.value = chat
  messagesPagination.page = 1
  fetchChatMessages()
}

// 判断是否为当前选中的聊天项
const isActiveChatItem = (chat) => {
  return currentChat.value && 
         currentChat.value.userId === chat.userId && 
         currentChat.value.productId === chat.productId
}

// 发送新消息
const sendNewMessage = async () => {
  if (!newMessage.value.trim() || !currentChat.value || !userStore.userInfo?.id) return
  
  sendLoading.value = true
  try {
    const messageData = {
      senderId: userStore.userInfo.id,
      receiverId: currentChat.value.userId,
      productId: currentChat.value.productId,
      content: newMessage.value.trim(),
      type: 'text'
    }
    
    await sendMessage(messageData)
    newMessage.value = ''
    
    // 重新获取消息列表
    await fetchChatMessages()
    
    // 更新聊天列表中的最后消息
    const chatIndex = chatList.value.findIndex(chat => 
      chat.userId === currentChat.value.userId && 
      chat.productId === currentChat.value.productId
    )
    if (chatIndex !== -1) {
      chatList.value[chatIndex].lastMessage = messageData.content
      chatList.value[chatIndex].lastMessageTime = new Date().toISOString()
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sendLoading.value = false
  }
}

// 标记所有消息为已读
const markAllAsRead = async () => {
  if (!currentChat.value || !userStore.userInfo?.id) return
  
  try {
    await markChatAsRead({
      senderId: currentChat.value.userId,
      receiverId: userStore.userInfo.id,
      productId: currentChat.value.productId
    })
    
    // 更新未读数量
    const chatIndex = chatList.value.findIndex(chat => 
      chat.userId === currentChat.value.userId && 
      chat.productId === currentChat.value.productId
    )
    if (chatIndex !== -1) {
      chatList.value[chatIndex].unreadCount = 0
    }
    
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

// 刷新聊天列表
const refreshChatList = () => {
  fetchChatList()
}

// 查看商品
const viewProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 24小时内
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

// 格式化消息时间
const formatMessageTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 初始化
onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchChatList()
  } else {
    router.push('/login')
  }
})
</script>

<style lang="scss" scoped>
.chat-page {
  min-height: 100vh;
  background: var(--bg-primary);
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .chat-container {
    display: grid;
    grid-template-columns: 300px 1fr;
    height: calc(100vh - 120px);
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  .chat-list {
    border-right: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    
    .chat-header {
      padding: 16px;
      border-bottom: 1px solid var(--border-color);
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
      }
    }
    
    .chat-items {
      flex: 1;
      overflow-y: auto;
      
      .chat-item {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        cursor: pointer;
        border-bottom: 1px solid var(--border-light);
        transition: background-color 0.3s ease;
        
        &:hover {
          background: var(--bg-secondary);
        }
        
        &.active {
          background: var(--primary-light);
          border-right: 3px solid var(--primary-color);
        }
        
        .chat-info {
          flex: 1;
          margin-left: 12px;
          min-width: 0;
          
          .chat-title {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 4px;
            
            .nickname {
              font-weight: 500;
              color: var(--text-primary);
            }
            
            .time {
              font-size: 12px;
              color: var(--text-placeholder);
            }
          }
          
          .chat-subtitle {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 4px;
            
            .product-title {
              font-size: 12px;
              color: var(--text-secondary);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              flex: 1;
            }
            
            .unread-badge {
              margin-left: 8px;
            }
          }
          
          .last-message {
            font-size: 12px;
            color: var(--text-placeholder);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }
  }
  
  .chat-window {
    display: flex;
    flex-direction: column;
    
    .empty-chat {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .chat-content {
      display: flex;
      flex-direction: column;
      height: 100%;
      
      .chat-window-header {
        padding: 16px 20px;
        border-bottom: 1px solid var(--border-color);
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .chat-user-info {
          display: flex;
          align-items: center;
          
          .user-details {
            margin-left: 12px;
            
            .nickname {
              font-weight: 500;
              color: var(--text-primary);
              margin-bottom: 2px;
            }
            
            .product-info {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
        
        .chat-actions {
          display: flex;
          gap: 8px;
        }
      }
      
      .messages-container {
        flex: 1;
        overflow-y: auto;
        padding: 16px 20px;
        
        .messages-list {
          .message-item {
            display: flex;
            margin-bottom: 16px;
            
            &.own-message {
              flex-direction: row-reverse;
              
              .message-content {
                align-items: flex-end;
                
                .message-bubble {
                  background: var(--primary-color);
                  color: white;
                }
              }
            }
            
            &.other-message {
              .message-content {
                align-items: flex-start;
                
                .message-bubble {
                  background: var(--bg-secondary);
                  color: var(--text-primary);
                }
              }
            }
            
            .message-avatar {
              margin: 0 8px;
            }
            
            .message-content {
              display: flex;
              flex-direction: column;
              max-width: 60%;
              
              .message-bubble {
                padding: 8px 12px;
                border-radius: 12px;
                word-wrap: break-word;
                
                .message-text {
                  margin-bottom: 4px;
                  line-height: 1.4;
                }
                
                .message-time {
                  font-size: 11px;
                  opacity: 0.7;
                }
              }
              
              .message-status {
                margin-top: 4px;
                text-align: right;
              }
            }
          }
        }
      }
      
      .message-input {
        padding: 16px 20px;
        border-top: 1px solid var(--border-color);
        
        .input-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 8px;
          
          .input-tip {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .chat-container {
    grid-template-columns: 1fr;
    height: calc(100vh - 80px);
    
    .chat-list {
      display: none;
    }
  }
}
</style>