<template>
  <div class="messages">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-button @click="markAllAsRead" :disabled="unreadCount === 0">
          全部标记已读
        </el-button>
        <el-button @click="clearAll" :disabled="messages.length === 0">
          清空消息
        </el-button>
      </div>
    </div>

    <!-- 消息统计 -->
    <div class="message-stats">
      <div class="stat-item">
        <span class="stat-label">全部消息</span>
        <span class="stat-value">{{ total }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">未读消息</span>
        <span class="stat-value unread">{{ unreadCount }}</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="系统消息" name="system" />
        <el-tab-pane label="交易消息" name="trade" />
        <el-tab-pane label="评价消息" name="review" />
        <el-tab-pane label="其他" name="other" />
      </el-tabs>
      
      <div class="filter-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索消息"
          style="width: 200px"
          clearable
          @change="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="readStatus" placeholder="阅读状态" style="width: 120px" @change="handleStatusChange">
          <el-option label="全部" value="" />
          <el-option label="未读" value="unread" />
          <el-option label="已读" value="read" />
        </el-select>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages-container" v-loading="loading">
      <div 
        class="message-item" 
        v-for="message in messages" 
        :key="message.id"
        :class="{ unread: !message.isRead }"
        @click="handleMessageClick(message)"
      >
        <div class="message-avatar">
          <el-avatar :size="40" :src="message.sender?.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="message-type-badge" :class="getTypeClass(message.type)">
            <el-icon>
              <Bell v-if="message.type === 'system'" />
              <ShoppingCart v-else-if="message.type === 'trade'" />
              <Star v-else-if="message.type === 'review'" />
              <Message v-else />
            </el-icon>
          </div>
        </div>
        
        <div class="message-content">
          <div class="message-header">
            <h4 class="message-title">{{ message.title }}</h4>
            <div class="message-meta">
              <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              <el-tag v-if="!message.isRead" type="danger" size="small">未读</el-tag>
            </div>
          </div>
          
          <p class="message-text">{{ message.content }}</p>
          
          <div class="message-extra" v-if="message.extra_data">
            <!-- 商品相关消息 -->
            <div class="product-info" v-if="message.extra_data.product">
              <img :src="message.extra_data.product.image" :alt="message.extra_data.product.title" />
              <div class="product-details">
                <span class="product-title">{{ message.extra_data.product.title }}</span>
                <span class="product-price">¥{{ message.extra_data.product.price }}</span>
              </div>
            </div>
            
            <!-- 订单相关消息 -->
            <div class="order-info" v-if="message.extra_data.order">
              <span class="order-number">订单号：{{ message.extra_data.order.order_number }}</span>
              <span class="order-amount">金额：¥{{ message.extra_data.order.amount }}</span>
            </div>
          </div>
        </div>
        
        <div class="message-actions">
          <el-button size="small" @click.stop="markAsRead(message)" v-if="!message.isRead">
            标记已读
          </el-button>
          <el-button size="small" @click.stop="deleteMessage(message)" type="danger" text>
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-if="!loading && messages.length === 0" description="暂无消息" />
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 消息详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="currentMessage?.title" width="600px">
      <div class="message-detail" v-if="currentMessage">
        <div class="detail-header">
          <div class="sender-info">
            <el-avatar :size="50" :src="currentMessage.sender?.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="sender-details">
              <span class="sender-name">{{ currentMessage.sender?.nickname || '系统消息' }}</span>
              <span class="send-time">{{ formatTime(currentMessage.createdAt) }}</span>
            </div>
          </div>
          <el-tag :type="getTypeTagType(currentMessage.type)">{{ getTypeText(currentMessage.type) }}</el-tag>
        </div>
        
        <div class="detail-content">
          <p>{{ currentMessage.content }}</p>
          
          <!-- 附加信息 -->
          <div class="extra-content" v-if="currentMessage.extra_data">
            <div class="product-card" v-if="currentMessage.extra_data.product">
              <img :src="currentMessage.extra_data.product.image" :alt="currentMessage.extra_data.product.title" />
              <div class="product-info">
                <h4>{{ currentMessage.extra_data.product.title }}</h4>
                <p class="product-price">¥{{ currentMessage.extra_data.product.price }}</p>
                <el-button size="small" type="primary" @click="viewProduct(currentMessage.extra_data.product.id)">
                  查看商品
                </el-button>
              </div>
            </div>
            
            <div class="order-card" v-if="currentMessage.extra_data.order">
              <div class="order-header">
                <span class="order-number">订单号：{{ currentMessage.extra_data.order.order_number }}</span>
                <span class="order-status">{{ currentMessage.extra_data.order.status }}</span>
              </div>
              <div class="order-amount">订单金额：¥{{ currentMessage.extra_data.order.amount }}</div>
              <el-button size="small" type="primary" @click="viewOrder(currentMessage.extra_data.order.id)">
                查看订单
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="replyMessage" v-if="currentMessage?.type !== 'system'">
          回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, User, Bell, ShoppingCart, Star, Message } from '@element-plus/icons-vue'
import { getUserMessages, markMessageAsRead, markAllMessagesAsRead, deleteMessage as deleteMessageApi, clearAllMessages } from '@/api/message'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const messages = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')
const searchKeyword = ref('')
const readStatus = ref('')

// 消息详情对话框
const detailDialogVisible = ref(false)
const currentMessage = ref(null)

// 计算未读消息数量
const unreadCount = computed(() => {
  return messages.value.filter(msg => !msg.isRead).length
})

// 获取消息列表
const fetchMessages = async () => {
  try {
    loading.value = true
    const params = {
      userId: userStore.userInfo?.id,
      page: currentPage.value,
      size: pageSize.value,
      type: activeTab.value === 'all' ? undefined : activeTab.value,
      keyword: searchKeyword.value,
      readStatus: readStatus.value === '' ? undefined : readStatus.value
    }
    
    const response = await getUserMessages(params)
    // MyBatis Plus Page对象结构：records为数据列表
    messages.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('获取消息列表失败:', error)
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  fetchMessages()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchMessages()
}

// 状态筛选
const handleStatusChange = () => {
  currentPage.value = 1
  fetchMessages()
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchMessages()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchMessages()
}

// 点击消息
const handleMessageClick = (message) => {
  currentMessage.value = message
  detailDialogVisible.value = true
  
  // 如果是未读消息，标记为已读
  if (!message.isRead) {
    markAsRead(message)
  }
}

// 标记单条消息为已读
const markAsRead = async (message) => {
  try {
    await markMessageAsRead(message.id, userStore.userInfo?.id)
    message.isRead = true
    ElMessage.success('标记成功')
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

// 标记全部为已读
const markAllAsRead = async () => {
  try {
    await markAllMessagesAsRead(userStore.userInfo?.id)
    messages.value.forEach(msg => {
      msg.isRead = true
    })
    ElMessage.success('全部标记成功')
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

// 删除消息
const deleteMessage = async (message) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除消息"${message.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMessageApi(message.id, userStore.userInfo?.id)
    ElMessage.success('删除成功')
    fetchMessages()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 清空所有消息
const clearAll = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有消息吗？此操作不可恢复。',
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await clearAllMessages(userStore.userInfo?.id)
    ElMessage.success('清空成功')
    fetchMessages()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
      ElMessage.error('清空失败')
    }
  }
}

// 查看商品
const viewProduct = (productId) => {
  window.open(`/product/${productId}`, '_blank')
  detailDialogVisible.value = false
}

// 查看订单
const viewOrder = (orderId) => {
  window.open(`/order/${orderId}`, '_blank')
  detailDialogVisible.value = false
}

// 回复消息
const replyMessage = () => {
  ElMessage.info('回复功能开发中')
}

// 获取消息类型样式类
const getTypeClass = (type) => {
  const typeMap = {
    system: 'type-system',
    trade: 'type-trade',
    review: 'type-review',
    other: 'type-other'
  }
  return typeMap[type] || 'type-other'
}

// 获取消息类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    system: 'info',
    trade: 'success',
    review: 'warning',
    other: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取消息类型文本
const getTypeText = (type) => {
  const typeMap = {
    system: '系统消息',
    trade: '交易消息',
    review: '评价消息',
    other: '其他消息'
  }
  return typeMap[type] || '未知类型'
}

// 格式化时间
const formatTime = (time) => {
  const now = dayjs()
  const msgTime = dayjs(time)
  
  if (now.diff(msgTime, 'day') === 0) {
    return msgTime.format('HH:mm')
  } else if (now.diff(msgTime, 'day') === 1) {
    return '昨天 ' + msgTime.format('HH:mm')
  } else if (now.diff(msgTime, 'year') === 0) {
    return msgTime.format('MM-DD HH:mm')
  } else {
    return msgTime.format('YYYY-MM-DD HH:mm')
  }
}

// 组件挂载
onMounted(async () => {
  // 确保用户已登录
  if (!userStore.userInfo?.id) {
    await userStore.getUserInfo()
  }
  
  if (userStore.userInfo?.id) {
    fetchMessages()
  } else {
    ElMessage.error('请先登录')
  }
})
</script>

<style lang="scss" scoped>
.messages {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .message-stats {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    
    .stat-item {
      background: var(--bg-primary);
      padding: 16px 20px;
      border-radius: var(--border-radius-large);
      box-shadow: var(--box-shadow-light);
      text-align: center;
      
      .stat-label {
        display: block;
        color: var(--text-secondary);
        font-size: var(--font-size-small);
        margin-bottom: 4px;
      }
      
      .stat-value {
        display: block;
        font-size: var(--font-size-extra-large);
        font-weight: var(--font-weight-primary);
        color: var(--text-primary);
        
        &.unread {
          color: var(--danger-color);
        }
      }
    }
  }
  
  .filter-bar {
    background: var(--bg-primary);
    padding: 16px;
    border-radius: var(--border-radius-large);
    margin-bottom: 20px;
    box-shadow: var(--box-shadow-light);
    
    .el-tabs {
      margin-bottom: 16px;
    }
    
    .filter-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
  
  .messages-container {
    .message-item {
      background: var(--bg-primary);
      border-radius: var(--border-radius-large);
      box-shadow: var(--box-shadow-light);
      margin-bottom: 12px;
      padding: 16px;
      display: flex;
      gap: 12px;
      cursor: pointer;
      transition: var(--transition-all);
      border-left: 4px solid transparent;
      
      &:hover {
        box-shadow: var(--box-shadow-dark);
        transform: translateX(4px);
      }
      
      &.unread {
        border-left-color: var(--primary-color);
        background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, var(--bg-primary) 100%);
      }
      
      .message-avatar {
        position: relative;
        flex-shrink: 0;
        
        .message-type-badge {
          position: absolute;
          bottom: -2px;
          right: -2px;
          width: 20px;
          height: 20px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 10px;
          color: white;
          
          &.type-system {
            background: var(--info-color);
          }
          
          &.type-trade {
            background: var(--success-color);
          }
          
          &.type-review {
            background: var(--warning-color);
          }
          
          &.type-other {
            background: var(--text-secondary);
          }
        }
      }
      
      .message-content {
        flex: 1;
        min-width: 0;
        
        .message-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 8px;
          
          .message-title {
            font-size: var(--font-size-medium);
            font-weight: var(--font-weight-primary);
            color: var(--text-primary);
            margin: 0;
            @include text-ellipsis;
          }
          
          .message-meta {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-shrink: 0;
            
            .message-time {
              color: var(--text-secondary);
              font-size: var(--font-size-small);
            }
          }
        }
        
        .message-text {
          color: var(--text-regular);
          font-size: var(--font-size-small);
          line-height: 1.5;
          margin: 0 0 8px 0;
          @include multi-line-ellipsis(2);
        }
        
        .message-extra {
          .product-info {
            display: flex;
            gap: 8px;
            align-items: center;
            padding: 8px;
            background: var(--bg-secondary);
            border-radius: var(--border-radius-base);
            
            img {
              width: 40px;
              height: 40px;
              object-fit: cover;
              border-radius: var(--border-radius-small);
            }
            
            .product-details {
              flex: 1;
              
              .product-title {
                display: block;
                font-size: var(--font-size-small);
                color: var(--text-primary);
                @include text-ellipsis;
              }
              
              .product-price {
                display: block;
                font-size: var(--font-size-small);
                color: var(--danger-color);
                font-weight: var(--font-weight-primary);
              }
            }
          }
          
          .order-info {
            display: flex;
            gap: 16px;
            padding: 8px;
            background: var(--bg-secondary);
            border-radius: var(--border-radius-base);
            font-size: var(--font-size-small);
            
            .order-number {
              color: var(--text-primary);
            }
            
            .order-amount {
              color: var(--danger-color);
              font-weight: var(--font-weight-primary);
            }
          }
        }
      }
      
      .message-actions {
        display: flex;
        flex-direction: column;
        gap: 4px;
        align-items: flex-end;
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }
}

// 消息详情对话框样式
.message-detail {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-color-lighter);
    
    .sender-info {
      display: flex;
      gap: 12px;
      align-items: center;
      
      .sender-details {
        .sender-name {
          display: block;
          font-weight: var(--font-weight-primary);
          color: var(--text-primary);
        }
        
        .send-time {
          display: block;
          font-size: var(--font-size-small);
          color: var(--text-secondary);
        }
      }
    }
  }
  
  .detail-content {
    line-height: 1.6;
    color: var(--text-regular);
    
    .extra-content {
      margin-top: 16px;
      
      .product-card,
      .order-card {
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--border-radius-base);
        
        .product-info {
          display: flex;
          gap: 12px;
          align-items: center;
          
          img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: var(--border-radius-small);
          }
          
          h4 {
            margin: 0 0 8px 0;
            color: var(--text-primary);
          }
          
          .product-price {
            color: var(--danger-color);
            font-weight: var(--font-weight-primary);
            margin: 0 0 8px 0;
          }
        }
        
        .order-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          
          .order-number {
            font-weight: var(--font-weight-primary);
          }
        }
        
        .order-amount {
          color: var(--danger-color);
          font-weight: var(--font-weight-primary);
          margin-bottom: 12px;
        }
      }
    }
  }
}

// 响应式设计
@include respond-to(md) {
  .messages {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
      
      h2 {
        text-align: center;
      }
      
      .header-actions {
        justify-content: center;
      }
    }
    
    .message-stats {
      justify-content: center;
      
      .stat-item {
        flex: 1;
        max-width: 150px;
      }
    }
    
    .filter-bar {
      .filter-actions {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
        
        .el-input,
        .el-select {
          width: 100% !important;
        }
      }
    }
    
    .messages-container {
      .message-item {
        .message-content {
          .message-header {
            flex-direction: column;
            align-items: stretch;
            
            .message-meta {
              justify-content: flex-end;
            }
          }
        }
        
        .message-actions {
          flex-direction: row;
          align-items: center;
        }
      }
    }
  }
}

@include respond-to(sm) {
  .messages {
    padding: 12px;
    
    .message-stats {
      flex-direction: column;
      
      .stat-item {
        max-width: none;
      }
    }
    
    .messages-container {
      .message-item {
        flex-direction: column;
        
        .message-avatar {
          align-self: flex-start;
        }
        
        .message-actions {
          align-self: flex-end;
        }
      }
    }
  }
}
</style>