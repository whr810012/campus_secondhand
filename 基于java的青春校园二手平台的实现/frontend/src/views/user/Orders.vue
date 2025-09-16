<template>
  <div class="orders-page">
    <div class="page-header">
      <h2>我的订单</h2>
    </div>

    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待付款" name="pending"></el-tab-pane>
        <el-tab-pane label="待发货" name="paid"></el-tab-pane>
        <el-tab-pane label="待收货" name="shipped"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
        <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </div>
        
        <div class="order-content">
          <div class="product-info">
            <img :src="order.productImage" :alt="order.productTitle" class="product-image">
            <div class="product-details">
              <h4 class="product-title">{{ order.productTitle }}</h4>
              <p class="product-desc">{{ order.productDescription }}</p>
              <div class="price-info">
                <span class="price">¥{{ order.price }}</span>
                <span class="quantity">x{{ order.quantity }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-actions">
            <div class="total-price">
              <span>实付款：</span>
              <span class="amount">¥{{ order.totalAmount }}</span>
            </div>
            <div class="action-buttons">
              <el-button 
                v-if="order.status === 'pending'" 
                type="primary" 
                size="small"
                @click="payOrder(order)"
              >
                立即付款
              </el-button>
              <el-button 
                v-if="order.status === 'shipped'" 
                type="success" 
                size="small"
                @click="confirmReceive(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 'completed'" 
                type="primary" 
                size="small"
                @click="rateOrder(order)"
              >
                评价
              </el-button>
              <el-button 
                v-if="['pending', 'paid'].includes(order.status)" 
                size="small"
                @click="cancelOrder(order)"
              >
                取消订单
              </el-button>
              <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="orders.length === 0" class="empty-state">
        <i class="el-icon-s-order"></i>
        <p>暂无订单</p>
        <el-button type="primary" @click="$router.push('/shop')">去逛逛</el-button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="orders.length > 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      >
      </el-pagination>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      title="订单详情"
      :visible.sync="detailDialogVisible"
      width="600px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-item">
            <span class="label">订单号：</span>
            <span>{{ selectedOrder.orderNumber }}</span>
          </div>
          <div class="detail-item">
            <span class="label">下单时间：</span>
            <span>{{ selectedOrder.createTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusType(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</el-tag>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>商品信息</h4>
          <div class="product-detail">
            <img :src="selectedOrder.productImage" :alt="selectedOrder.productTitle" class="detail-image">
            <div>
              <p class="detail-title">{{ selectedOrder.productTitle }}</p>
              <p class="detail-price">¥{{ selectedOrder.price }} x {{ selectedOrder.quantity }}</p>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>收货信息</h4>
          <div class="address-info">
            <p><strong>{{ selectedOrder.recipientName }}</strong> {{ selectedOrder.phone }}</p>
            <p>{{ selectedOrder.address }}</p>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>费用明细</h4>
          <div class="cost-detail">
            <div class="cost-item">
              <span>商品金额：</span>
              <span>¥{{ selectedOrder.price }}</span>
            </div>
            <div class="cost-item">
              <span>运费：</span>
              <span>¥0.00</span>
            </div>
            <div class="cost-item total">
              <span>实付款：</span>
              <span class="total-amount">¥{{ selectedOrder.totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Orders',
  data() {
    return {
      loading: false,
      activeTab: 'all',
      orders: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      detailDialogVisible: false,
      selectedOrder: null
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      this.loading = true
      try {
        // 模拟数据
        const mockData = {
          records: [
            {
              id: 1,
              orderNumber: 'ORD202401150001',
              productId: 1,
              productTitle: 'iPhone 13 Pro Max',
              productDescription: '256GB 深空灰色，9成新',
              productImage: '/images/products/iphone13.jpg',
              price: 6800.00,
              quantity: 1,
              totalAmount: 6800.00,
              status: 'pending',
              createTime: '2024-01-15 14:30:00',
              recipientName: '张三',
              phone: '13800138000',
              address: '北京市海淀区中关村大街1号'
            },
            {
              id: 2,
              orderNumber: 'ORD202401140002',
              productId: 2,
              productTitle: 'MacBook Pro 14寸',
              productDescription: 'M1 Pro芯片，512GB存储',
              productImage: '/images/products/macbook.jpg',
              price: 12800.00,
              quantity: 1,
              totalAmount: 12800.00,
              status: 'shipped',
              createTime: '2024-01-14 10:20:00',
              recipientName: '张三',
              phone: '13800138000',
              address: '北京市海淀区中关村大街1号'
            },
            {
              id: 3,
              orderNumber: 'ORD202401130003',
              productId: 3,
              productTitle: 'iPad Air',
              productDescription: '64GB WiFi版，蓝色',
              productImage: '/images/products/ipad.jpg',
              price: 3200.00,
              quantity: 1,
              totalAmount: 3200.00,
              status: 'completed',
              createTime: '2024-01-13 16:45:00',
              recipientName: '张三',
              phone: '13800138000',
              address: '北京市海淀区中关村大街1号'
            }
          ],
          total: 3,
          current: 1,
          size: 10
        }
        
        // 根据选中的标签过滤订单
        let filteredOrders = mockData.records
        if (this.activeTab !== 'all') {
          filteredOrders = mockData.records.filter(order => order.status === this.activeTab)
        }
        
        this.orders = filteredOrders
        this.pagination.total = filteredOrders.length
      } catch (error) {
        this.$message.error('加载订单列表失败')
      } finally {
        this.loading = false
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.pagination.current = 1
      this.loadOrders()
    },
    getStatusType(status) {
      const statusMap = {
        pending: 'warning',
        paid: 'info',
        shipped: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        pending: '待付款',
        paid: '待发货',
        shipped: '待收货',
        completed: '已完成',
        cancelled: '已取消'
      }
      return statusMap[status] || '未知状态'
    },
    async payOrder(order) {
      try {
        // 这里应该调用支付API
        await new Promise(resolve => setTimeout(resolve, 1000))
        order.status = 'paid'
        this.$message.success('支付成功')
      } catch (error) {
        this.$message.error('支付失败')
      }
    },
    async confirmReceive(order) {
      this.$confirm('确认已收到商品？', '提示', {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'info'
      }).then(async () => {
        try {
          // 这里应该调用确认收货API
          await new Promise(resolve => setTimeout(resolve, 500))
          order.status = 'completed'
          this.$message.success('确认收货成功')
        } catch (error) {
          this.$message.error('操作失败')
        }
      })
    },
    rateOrder(order) {
      this.$message.info('评价功能开发中...')
    },
    cancelOrder(order) {
      this.$confirm('确定要取消这个订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用取消订单API
          await new Promise(resolve => setTimeout(resolve, 500))
          order.status = 'cancelled'
          this.$message.success('订单已取消')
        } catch (error) {
          this.$message.error('取消失败')
        }
      })
    },
    viewOrderDetail(order) {
      this.selectedOrder = order
      this.detailDialogVisible = true
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadOrders()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadOrders()
    }
  }
}
</script>

<style scoped>
.orders-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.order-tabs {
  margin-bottom: 20px;
}

.order-list {
  min-height: 400px;
}

.order-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 16px;
  background: white;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-number {
  font-weight: bold;
}

.order-time {
  color: #909399;
}

.order-content {
  padding: 20px;
}

.product-info {
  display: flex;
  margin-bottom: 16px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 16px;
}

.product-details {
  flex: 1;
}

.product-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: bold;
}

.product-desc {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price {
  color: #e6a23c;
  font-weight: bold;
  font-size: 16px;
}

.quantity {
  color: #909399;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price {
  font-size: 16px;
}

.amount {
  color: #e6a23c;
  font-weight: bold;
  font-size: 18px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

/* 订单详情样式 */
.order-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
}

.detail-item .label {
  width: 80px;
  color: #606266;
}

.product-detail {
  display: flex;
  align-items: center;
}

.detail-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}

.detail-title {
  margin: 0 0 4px 0;
  font-weight: bold;
}

.detail-price {
  margin: 0;
  color: #e6a23c;
}

.address-info p {
  margin: 4px 0;
}

.cost-detail {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.cost-item.total {
  border-top: 1px solid #e4e7ed;
  padding-top: 8px;
  margin-top: 8px;
  font-weight: bold;
}

.total-amount {
  color: #e6a23c;
  font-size: 16px;
}
</style>