<template>
  <div class="orders-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">订单管理</h1>
        <p class="page-description">管理平台所有订单信息，包括订单状态跟踪、退款处理等操作</p>
      </div>
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
                <div class="order-amount">¥{{ row.amount }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  :src="getProductImage(row.product)"
                  class="product-image"
                  fit="cover"
                />
                <div class="product-details">
                  <div class="product-name">{{ row.product.title }}</div>
                  <div class="product-price">¥{{ row.product.price }}</div>
                  <div class="product-quantity">数量：1</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="买家信息" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <div class="user-name">买家ID: {{ row.buyerId }}</div>
                <div class="user-contact">-</div>
                <div class="user-id">-</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="卖家信息" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <div class="user-name">卖家ID: {{ row.sellerId }}</div>
                <div class="user-contact">-</div>
                <div class="user-id">-</div>
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
              <span>{{ row.tradeLocation || '-' }}</span>
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
                  <span class="value">{{ selectedOrder.tradeLocation || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">订单金额：</span>
                  <span class="value amount">¥{{ selectedOrder.amount }}</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>商品信息</h4>
              <div class="product-detail">
                <el-image
                  :src="getProductImage(selectedOrder.product)"
                  class="product-image-large"
                  fit="cover"
                />
                <div class="product-info-detail">
                  <div class="product-name">{{ selectedOrder.product.title }}</div>
                  <div class="product-desc">{{ selectedOrder.product.description }}</div>
                  <div class="product-price">单价：¥{{ selectedOrder.product.price }}</div>
                  <div class="product-quantity">数量：1</div>
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
                  <span class="label">买家ID：</span>
                  <span class="value">{{ selectedOrder.buyerId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学号：</span>
                  <span class="value">-</span>
                </div>
                <div class="info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">-</span>
                </div>
                <div class="info-item">
                  <span class="label">学院：</span>
                  <span class="value">-</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-section">
              <h4>卖家信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">卖家ID：</span>
                  <span class="value">{{ selectedOrder.sellerId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学号：</span>
                  <span class="value">-</span>
                </div>
                <div class="info-item">
                  <span class="label">联系电话：</span>
                  <span class="value">-</span>
                </div>
                <div class="info-item">
                  <span class="label">学院：</span>
                  <span class="value">-</span>
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
import { getAllOrders, cancelOrder as cancelOrderApi, requestRefund, deleteOrder as deleteOrderApi } from '@/api/order'

// 响应式数据
const loading = ref(false)
const orders = ref([])
const selectedOrders = ref([])
const orderDetailVisible = ref(false)
const selectedOrder = ref(null)

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
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await getAllOrders(params)
    console.log('API响应数据:', response)
    
    // 根据实际API响应结构处理数据
    if (response.data && response.data.records) {
      orders.value = response.data.records
      pagination.total = response.data.total || 0
      console.log('处理后的订单数据:', orders.value)
      console.log('总数:', pagination.total)
    } else if (response.message === '操作成功' && response.data) {
      // 兼容没有code字段但有message的情况
      orders.value = response.data.records || []
      pagination.total = response.data.total || 0
      console.log('处理后的订单数据:', orders.value)
      console.log('总数:', pagination.total)
    } else {
      ElMessage.error(response.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
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
    
    const response = await cancelOrderApi(order.id, { reason })
    
    if (response.code === 200) {
      ElMessage.success('订单取消成功')
      // 重新获取订单列表
      fetchOrders()
      
      if (orderDetailVisible.value) {
        closeOrderDetail()
      }
    } else {
      ElMessage.error(response.message || '订单取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('订单取消失败:', error)
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
    
    const response = await requestRefund(order.id, { reason })
    
    if (response.code === 200) {
      ElMessage.success('退款处理成功')
      // 重新获取订单列表
      fetchOrders()
      
      if (orderDetailVisible.value) {
        closeOrderDetail()
      }
    } else {
      ElMessage.error(response.message || '退款处理失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退款处理失败:', error)
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
    
    // TODO: 等待后端提供完成订单的API接口
    // 暂时使用模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success('订单完成成功')
    // 重新获取订单列表以确保数据同步
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('订单完成失败:', error)
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
    
    const response = await deleteOrderApi(order.id)
    
    if (response.code === 200 || response.message === '操作成功') {
      ElMessage.success('删除成功')
      // 重新获取订单列表以确保数据同步
      fetchOrders()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
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
    
    // 批量取消订单
    const promises = selectedOrders.value
      .filter(order => order.status === 'pending')
      .map(order => cancelOrderApi(order.id, { reason }))
    
    await Promise.all(promises)
    
    ElMessage.success('批量取消成功')
    selectedOrders.value = []
    // 重新获取订单列表
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量取消失败:', error)
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
    
    // 批量退款处理
    const promises = selectedOrders.value
      .filter(order => ['paid', 'shipped'].includes(order.status))
      .map(order => requestRefund(order.id, { reason }))
    
    await Promise.all(promises)
    
    ElMessage.success('批量退款成功')
    selectedOrders.value = []
    // 重新获取订单列表
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量退款失败:', error)
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
    
    // 批量删除订单
    const promises = selectedOrders.value.map(order => deleteOrderApi(order.id))
    
    await Promise.all(promises)
    
    ElMessage.success('批量删除成功')
    selectedOrders.value = []
    // 重新获取订单列表
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 初始化
onMounted(() => {
  fetchOrders()
})

// 工具函数
const getProductImage = (product) => {
  if (!product) return 'https://via.placeholder.com/300x200/f5f5f5/cccccc?text=暂无图片'
  
  // 处理imageList数组
  if (product.imageList && product.imageList.length > 0) {
    return product.imageList[0]
  }
  
  // 处理images数组
  if (product.images && product.images.length > 0) {
    return product.images[0]
  }
  
  // 处理单个image字段
  if (product.image) {
    return product.image
  }
  
  return 'https://via.placeholder.com/300x200/f5f5f5/cccccc?text=暂无图片'
}
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