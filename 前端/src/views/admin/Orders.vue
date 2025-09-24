<template>
  <div class="orders-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">订单管理</h1>
        <p class="page-description">管理平台所有订单信息，包括订单状态跟踪、退款处理等操作</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="exportOrders">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pending }}</div>
              <div class="stat-label">待付款</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon processing">
              <el-icon><Truck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.processing }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filters" inline>
          <el-form-item label="订单号">
            <el-input
              v-model="filters.orderNo"
              placeholder="请输入订单号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="filters.status" placeholder="请选择状态" clearable style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="待付款" value="pending" />
              <el-option label="已付款" value="paid" />
              <el-option label="已发货" value="shipped" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
              <el-option label="退款中" value="refunding" />
              <el-option label="已退款" value="refunded" />
            </el-select>
          </el-form-item>
          <el-form-item label="买家姓名">
            <el-input
              v-model="filters.buyerName"
              placeholder="请输入买家姓名"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="卖家姓名">
            <el-input
              v-model="filters.sellerName"
              placeholder="请输入卖家姓名"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="filters.dateRange"
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
            <el-button type="primary" @click="searchOrders">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilters">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedOrders.length > 0">
      <el-alert
        :title="`已选择 ${selectedOrders.length} 个订单`"
        type="info"
        show-icon
        :closable="false"
      >
        <template #default>
          <div class="batch-buttons">
            <el-button size="small" @click="batchCancel">批量取消</el-button>
            <el-button size="small" @click="batchRefund">批量退款</el-button>
            <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
          </div>
        </template>
      </el-alert>
    </div>

    <!-- 订单列表 -->
    <div class="orders-table">
      <el-card>
        <el-table
          :data="orders"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="订单信息" min-width="250">
            <template #default="{ row }">
              <div class="order-info">
                <div class="order-no">订单号：{{ row.orderNo }}</div>
                <div class="order-time">{{ formatDate(row.createdAt) }}</div>
                <div class="order-amount">¥{{ row.totalAmount }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  :src="row.product.image"
                  class="product-image"
                  fit="cover"
                />
                <div class="product-details">
                  <div class="product-name">{{ row.product.name }}</div>
                  <div class="product-price">¥{{ row.product.price }}</div>
                  <div class="product-quantity">数量：{{ row.quantity }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="买家信息" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <div class="user-name">{{ row.buyer.name }}</div>
                <div class="user-contact">{{ row.buyer.phone }}</div>
                <div class="user-id">{{ row.buyer.studentId }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="卖家信息" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <div class="user-name">{{ row.seller.name }}</div>
                <div class="user-contact">{{ row.seller.phone }}</div>
                <div class="user-id">{{ row.seller.studentId }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="订单状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付方式" width="100">
            <template #default="{ row }">
              <span>{{ getPaymentMethodText(row.paymentMethod) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="交易地点" width="120">
            <template #default="{ row }">
              <span>{{ row.meetingLocation || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button size="small" @click="viewOrder(row)">查看</el-button>
                <el-button
                  v-if="row.status === 'pending'"
                  size="small"
                  type="warning"
                  @click="cancelOrder(row)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="['paid', 'shipped'].includes(row.status)"
                  size="small"
                  type="warning"
                  @click="refundOrder(row)"
                >
                  退款
                </el-button>
                <el-button
                  v-if="row.status === 'shipped'"
                  size="small"
                  type="success"
                  @click="completeOrder(row)"
                >
                  完成订单
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteOrder(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailVisible"
      title="订单详情"
      width="900px"
      :before-close="closeOrderDetail"
    >
      <div v-if="selectedOrder" class="order-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-section">
              <h4>订单信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">订单号：</span>
                  <span class="value">{{ selectedOrder.orderNo }}</span>
                </div>
                <div class="info-item">
                  <span class="label">订单状态：</span>
                  <el-tag :type="getStatusType(selectedOrder.status)">
                    {{ getStatusText(selectedOrder.status) }}
                  </el-tag>
                </div>
                <div class="info-item">
                  <span class="label">创建时间：</span>
                  <span class="value">{{ formatDateTime(selectedOrder.createdAt) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">支付方式：</span>
                  <span class="value">{{ getPaymentMethodText(selectedOrder.paymentMethod) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">交易地点：</span>
                  <span class="value">{{ selectedOrder.meetingLocation || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">订单金额：</span>
                  <span class="value amount">¥{{ selectedOrder.totalAmount }}</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>商品信息</h4>
              <div class="product-detail">
                <el-image
                  :src="selectedOrder.product.image"
                  class="product-image-large"
                  fit="cover"
                />
                <div class="product-info-detail">
                  <div class="product-name">{{ selectedOrder.product.name }}</div>
                  <div class="product-desc">{{ selectedOrder.product.description }}</div>
                  <div class="product-price">单价：¥{{ selectedOrder.product.price }}</div>
                  <div class="product-quantity">数量：{{ selectedOrder.quantity }}</div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <div class="detail-section">
              <h4>买家信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">姓名：</span>
                  <span class="value">{{ selectedOrder.buyer.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学号：</span>
                  <span class="value">{{ selectedOrder.buyer.studentId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">{{ selectedOrder.buyer.phone }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学院：</span>
                  <span class="value">{{ selectedOrder.buyer.college }}</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>卖家信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">姓名：</span>
                  <span class="value">{{ selectedOrder.seller.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学号：</span>
                  <span class="value">{{ selectedOrder.seller.studentId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">{{ selectedOrder.seller.phone }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学院：</span>
                  <span class="value">{{ selectedOrder.seller.college }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="detail-section" style="margin-top: 20px" v-if="selectedOrder.timeline">
          <h4>订单时间线</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in selectedOrder.timeline"
              :key="item.id"
              :timestamp="formatDateTime(item.time)"
              :type="item.type"
            >
              {{ item.description }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeOrderDetail">关闭</el-button>
          <el-button
            v-if="selectedOrder?.status === 'pending'"
            type="warning"
            @click="cancelOrder(selectedOrder)"
          >
            取消订单
          </el-button>
          <el-button
            v-if="['paid', 'shipped'].includes(selectedOrder?.status)"
            type="warning"
            @click="refundOrder(selectedOrder)"
          >
            退款处理
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Search,
  Refresh,
  Document,
  Clock,
  Truck,
  Check
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const orders = ref([])
const selectedOrders = ref([])
const orderDetailVisible = ref(false)
const selectedOrder = ref(null)

// 统计数据
const stats = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0
})

// 筛选条件
const filters = reactive({
  orderNo: '',
  status: '',
  buyerName: '',
  sellerName: '',
  dateRange: []
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 状态映射
const statusMap = {
  pending: '待付款',
  paid: '已付款',
  shipped: '已发货',
  completed: '已完成',
  cancelled: '已取消',
  refunding: '退款中',
  refunded: '已退款'
}

// 支付方式映射
const paymentMethodMap = {
  wechat: '微信支付',
  alipay: '支付宝',
  cash: '现金交易',
  bank: '银行转账'
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    paid: 'primary',
    shipped: 'info',
    completed: 'success',
    cancelled: 'danger',
    refunding: 'warning',
    refunded: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  return paymentMethodMap[method] || method
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    const mockOrders = [
      {
        id: 1,
        orderNo: 'ORD202401150001',
        status: 'paid',
        totalAmount: 6800,
        quantity: 1,
        paymentMethod: 'wechat',
        meetingLocation: '图书馆门口',
        createdAt: '2024-01-15T10:30:00Z',
        product: {
          id: 1,
          name: 'iPhone 13 Pro',
          description: '9成新，无磕碰，功能正常',
          price: 6800,
          image: '/api/placeholder/300/300'
        },
        buyer: {
          id: 1,
          name: '张三',
          studentId: '2021001',
          phone: '138****1234',
          college: '计算机学院'
        },
        seller: {
          id: 2,
          name: '李四',
          studentId: '2021002',
          phone: '139****5678',
          college: '电子工程学院'
        },
        timeline: [
          {
            id: 1,
            time: '2024-01-15T10:30:00Z',
            description: '订单创建',
            type: 'primary'
          },
          {
            id: 2,
            time: '2024-01-15T10:35:00Z',
            description: '买家付款成功',
            type: 'success'
          }
        ]
      },
      {
        id: 2,
        orderNo: 'ORD202401140001',
        status: 'completed',
        totalAmount: 45,
        quantity: 1,
        paymentMethod: 'cash',
        meetingLocation: '宿舍楼下',
        createdAt: '2024-01-14T15:20:00Z',
        product: {
          id: 2,
          name: '高等数学教材',
          description: '同济版高等数学上下册',
          price: 45,
          image: '/api/placeholder/300/300'
        },
        buyer: {
          id: 3,
          name: '王五',
          studentId: '2021003',
          phone: '137****9012',
          college: '数学学院'
        },
        seller: {
          id: 4,
          name: '赵六',
          studentId: '2021004',
          phone: '136****3456',
          college: '物理学院'
        },
        timeline: [
          {
            id: 1,
            time: '2024-01-14T15:20:00Z',
            description: '订单创建',
            type: 'primary'
          },
          {
            id: 2,
            time: '2024-01-14T15:25:00Z',
            description: '买家付款成功',
            type: 'success'
          },
          {
            id: 3,
            time: '2024-01-14T16:00:00Z',
            description: '卖家发货',
            type: 'info'
          },
          {
            id: 4,
            time: '2024-01-14T16:30:00Z',
            description: '交易完成',
            type: 'success'
          }
        ]
      }
    ]
    
    orders.value = mockOrders
    pagination.total = mockOrders.length
    
    // 更新统计数据
    updateStats()
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.total = orders.value.length
  stats.pending = orders.value.filter(o => o.status === 'pending').length
  stats.processing = orders.value.filter(o => ['paid', 'shipped'].includes(o.status)).length
  stats.completed = orders.value.filter(o => o.status === 'completed').length
}

// 搜索订单
const searchOrders = () => {
  pagination.page = 1
  fetchOrders()
}

// 重置筛选条件
const resetFilters = () => {
  Object.keys(filters).forEach(key => {
    if (key === 'dateRange') {
      filters[key] = []
    } else {
      filters[key] = ''
    }
  })
  searchOrders()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchOrders()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchOrders()
}

// 查看订单详情
const viewOrder = (order) => {
  selectedOrder.value = order
  orderDetailVisible.value = true
}

// 关闭订单详情
const closeOrderDetail = () => {
  orderDetailVisible.value = false
  selectedOrder.value = null
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    order.status = 'cancelled'
    order.cancelReason = reason
    ElMessage.success('订单取消成功')
    updateStats()
    
    if (orderDetailVisible.value) {
      closeOrderDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单取消失败')
    }
  }
}

// 退款处理
const refundOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入退款原因', '退款处理', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入退款原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    order.status = 'refunded'
    order.refundReason = reason
    ElMessage.success('退款处理成功')
    updateStats()
    
    if (orderDetailVisible.value) {
      closeOrderDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退款处理失败')
    }
  }
}

// 完成订单
const completeOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认完成该订单？', '确认操作', {
      type: 'warning'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    order.status = 'completed'
    ElMessage.success('订单完成成功')
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订单完成失败')
    }
  }
}

// 删除订单
const deleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认删除该订单？删除后无法恢复！', '确认删除', {
      type: 'error'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const index = orders.value.findIndex(o => o.id === order.id)
    if (index > -1) {
      orders.value.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量操作
const batchCancel = async () => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入取消原因', '批量取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入取消原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedOrders.value.forEach(order => {
      if (order.status === 'pending') {
        order.status = 'cancelled'
        order.cancelReason = reason
      }
    })
    
    ElMessage.success('批量取消成功')
    selectedOrders.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

const batchRefund = async () => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入退款原因', '批量退款处理', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入退款原因'
    })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedOrders.value.forEach(order => {
      if (['paid', 'shipped'].includes(order.status)) {
        order.status = 'refunded'
        order.refundReason = reason
      }
    })
    
    ElMessage.success('批量退款成功')
    selectedOrders.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量退款失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确认批量删除 ${selectedOrders.value.length} 个订单？删除后无法恢复！`,
      '确认删除',
      { type: 'error' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const idsToDelete = selectedOrders.value.map(o => o.id)
    orders.value = orders.value.filter(o => !idsToDelete.includes(o.id))
    
    ElMessage.success('批量删除成功')
    selectedOrders.value = []
    updateStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出数据
const exportOrders = async () => {
  try {
    ElMessage.info('正在导出数据...')
    
    // 模拟导出
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 初始化
onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.orders-management {
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
        color: white;

        &.total {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.pending {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.processing {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.completed {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
    margin-bottom: 20px;
  }

  .batch-actions {
    margin-bottom: 20px;

    .batch-buttons {
      margin-top: 10px;

      .el-button {
        margin-right: 10px;
      }
    }
  }

  .orders-table {
    margin-bottom: 20px;

    .order-info {
      .order-no {
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .order-time {
        font-size: 12px;
        color: #909399;
        margin-bottom: 4px;
      }

      .order-amount {
        font-weight: 600;
        color: #e6a23c;
        font-size: 16px;
      }
    }

    .product-info {
      display: flex;
      align-items: center;

      .product-image {
        width: 60px;
        height: 60px;
        border-radius: 6px;
        margin-right: 12px;
        flex-shrink: 0;
      }

      .product-details {
        flex: 1;
        min-width: 0;

        .product-name {
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .product-price {
          font-weight: 600;
          color: #e6a23c;
          margin-bottom: 2px;
        }

        .product-quantity {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .user-info {
      .user-name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 2px;
      }

      .user-contact {
        font-size: 12px;
        color: #909399;
        margin-bottom: 2px;
      }

      .user-id {
        font-size: 12px;
        color: #c0c4cc;
      }
    }

    .action-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;

      .el-button {
        padding: 4px 8px;
        font-size: 12px;
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .order-detail {
    .detail-section {
      h4 {
        margin: 0 0 16px 0;
        font-size: 16px;
        color: #303133;
        border-bottom: 1px solid #ebeef5;
        padding-bottom: 8px;
      }

      .info-grid {
        .info-item {
          display: flex;
          margin-bottom: 12px;
          align-items: center;

          .label {
            width: 80px;
            color: #909399;
            font-size: 14px;
            flex-shrink: 0;
          }

          .value {
            color: #303133;
            font-size: 14px;
            flex: 1;

            &.amount {
              font-weight: 600;
              color: #e6a23c;
              font-size: 16px;
            }
          }
        }
      }

      .product-detail {
        display: flex;
        gap: 16px;

        .product-image-large {
          width: 120px;
          height: 120px;
          border-radius: 8px;
          flex-shrink: 0;
        }

        .product-info-detail {
          flex: 1;

          .product-name {
            font-size: 16px;
            font-weight: 500;
            color: #303133;
            margin-bottom: 8px;
          }

          .product-desc {
            font-size: 14px;
            color: #606266;
            margin-bottom: 8px;
            line-height: 1.4;
          }

          .product-price {
            font-weight: 600;
            color: #e6a23c;
            margin-bottom: 4px;
          }

          .product-quantity {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .orders-management {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .stats-cards {
      .el-col {
        margin-bottom: 10px;
      }
    }

    .filter-section {
      .el-form {
        .el-form-item {
          width: 100%;
          margin-bottom: 16px;
        }
      }
    }

    .orders-table {
      .action-buttons {
        flex-direction: column;

        .el-button {
          width: 100%;
          margin-bottom: 4px;
        }
      }
    }

    .order-detail {
      .detail-section {
        .product-detail {
          flex-direction: column;

          .product-image-large {
            width: 100%;
            height: 200px;
          }
        }
      }
    }
  }
}
</style>