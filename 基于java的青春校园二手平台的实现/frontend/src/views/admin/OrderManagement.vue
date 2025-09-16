<template>
  <div class="order-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>订单管理</h2>
      
      <!-- 统计信息 -->
      <div class="stats-summary">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
          <div class="stat-label">总订单数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
          <div class="stat-label">待处理</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.completedOrders || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">¥{{ stats.totalAmount || 0 }}</div>
          <div class="stat-label">总交易额</div>
        </div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索订单号、商品名称、用户名..."
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
          placeholder="订单状态"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待付款" value="PENDING_PAYMENT" />
          <el-option label="待发货" value="PENDING_DELIVERY" />
          <el-option label="待收货" value="PENDING_RECEIPT" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
          <el-option label="退款中" value="REFUNDING" />
          <el-option label="已退款" value="REFUNDED" />
        </el-select>
        
        <el-select
          v-model="searchForm.tradeType"
          placeholder="交易方式"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部方式" value="" />
          <el-option label="线上交易" value="ONLINE" />
          <el-option label="线下交易" value="OFFLINE" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="订单开始日期"
          end-placeholder="订单结束日期"
          @change="handleDateChange"
        />
        
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="exportOrders">
          <i class="el-icon-download"></i>
          导出订单
        </el-button>
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="orders"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="订单信息" min-width="200">
          <template slot-scope="scope">
            <div class="order-info">
              <div class="order-number">{{ scope.row.orderNumber }}</div>
              <div class="order-time">{{ formatTime(scope.row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="商品信息" min-width="250">
          <template slot-scope="scope">
            <div class="product-info">
              <div class="product-image">
                <img
                  :src="scope.row.productImage || '/default-product.png'"
                  :alt="scope.row.productTitle"
                  @error="handleImageError"
                />
              </div>
              <div class="product-details">
                <div class="product-title">{{ scope.row.productTitle }}</div>
                <div class="product-price">¥{{ scope.row.productPrice }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="买家" width="120">
          <template slot-scope="scope">
            <div class="user-info">
              <div class="username">{{ scope.row.buyerName || '未知' }}</div>
              <div class="user-contact">{{ scope.row.buyerPhone || scope.row.buyerEmail }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="卖家" width="120">
          <template slot-scope="scope">
            <div class="user-info">
              <div class="username">{{ scope.row.sellerName || '未知' }}</div>
              <div class="user-contact">{{ scope.row.sellerPhone || scope.row.sellerEmail }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="交易方式" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getTradeTypeType(scope.row.tradeType)"
              size="small"
            >
              {{ getTradeTypeText(scope.row.tradeType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="订单状态" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template slot-scope="scope">
            <span class="amount">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="updateTime" label="更新时间" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewOrderDetail(scope.row)"
            >
              查看详情
            </el-button>
            
            <el-button
              v-if="canCancel(scope.row)"
              type="text"
              size="small"
              class="danger-btn"
              @click="cancelOrder(scope.row)"
            >
              取消订单
            </el-button>
            
            <el-button
              v-if="canRefund(scope.row)"
              type="text"
              size="small"
              class="warning-btn"
              @click="refundOrder(scope.row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedOrders.length > 0">
        <span class="selected-info">已选择 {{ selectedOrders.length }} 个订单</span>
        <el-button size="small" @click="batchExport">批量导出</el-button>
        <el-button size="small" type="danger" @click="batchCancel">批量取消</el-button>
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
    
    <!-- 订单详情对话框 -->
    <el-dialog
      title="订单详情"
      :visible.sync="showDetailDialog"
      width="800px"
      class="order-detail-dialog"
    >
      <div v-if="currentOrder" class="order-detail">
        <!-- 订单基本信息 -->
        <div class="detail-section">
          <h4>订单信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">
              {{ currentOrder.orderNumber }}
            </el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)" size="small">
                {{ getStatusText(currentOrder.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="交易方式">
              <el-tag :type="getTradeTypeType(currentOrder.tradeType)" size="small">
                {{ getTradeTypeText(currentOrder.tradeType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="amount">¥{{ currentOrder.totalAmount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatTime(currentOrder.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="更新时间">
              {{ formatTime(currentOrder.updateTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 商品信息 -->
        <div class="detail-section">
          <h4>商品信息</h4>
          <div class="product-detail">
            <div class="product-image">
              <img
                :src="currentOrder.productImage || '/default-product.png'"
                :alt="currentOrder.productTitle"
                @error="handleImageError"
              />
            </div>
            <div class="product-info">
              <h5>{{ currentOrder.productTitle }}</h5>
              <p class="product-price">¥{{ currentOrder.productPrice }}</p>
              <p class="product-desc">{{ currentOrder.productDescription || '暂无描述' }}</p>
            </div>
          </div>
        </div>
        
        <!-- 用户信息 -->
        <div class="detail-section">
          <h4>交易双方</h4>
          <div class="users-info">
            <div class="user-card">
              <h5>买家信息</h5>
              <p><strong>姓名：</strong>{{ currentOrder.buyerName || '未知' }}</p>
              <p><strong>手机：</strong>{{ currentOrder.buyerPhone || '未提供' }}</p>
              <p><strong>邮箱：</strong>{{ currentOrder.buyerEmail || '未提供' }}</p>
            </div>
            <div class="user-card">
              <h5>卖家信息</h5>
              <p><strong>姓名：</strong>{{ currentOrder.sellerName || '未知' }}</p>
              <p><strong>手机：</strong>{{ currentOrder.sellerPhone || '未提供' }}</p>
              <p><strong>邮箱：</strong>{{ currentOrder.sellerEmail || '未提供' }}</p>
            </div>
          </div>
        </div>
        
        <!-- 收货地址 -->
        <div v-if="currentOrder.deliveryAddress" class="detail-section">
          <h4>收货地址</h4>
          <div class="address-info">
            <p><strong>收货人：</strong>{{ currentOrder.deliveryAddress.receiverName }}</p>
            <p><strong>联系电话：</strong>{{ currentOrder.deliveryAddress.receiverPhone }}</p>
            <p><strong>详细地址：</strong>{{ currentOrder.deliveryAddress.fullAddress }}</p>
          </div>
        </div>
        
        <!-- 订单备注 -->
        <div v-if="currentOrder.remark" class="detail-section">
          <h4>订单备注</h4>
          <p class="remark">{{ currentOrder.remark }}</p>
        </div>
        
        <!-- 订单日志 -->
        <div v-if="currentOrder.logs && currentOrder.logs.length > 0" class="detail-section">
          <h4>订单日志</h4>
          <el-timeline>
            <el-timeline-item
              v-for="log in currentOrder.logs"
              :key="log.id"
              :timestamp="formatTime(log.createTime)"
            >
              {{ log.content }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button
          v-if="currentOrder && canCancel(currentOrder)"
          type="danger"
          @click="cancelOrder(currentOrder)"
        >
          取消订单
        </el-button>
        <el-button
          v-if="currentOrder && canRefund(currentOrder)"
          type="warning"
          @click="refundOrder(currentOrder)"
        >
          处理退款
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 退款处理对话框 -->
    <el-dialog
      title="处理退款"
      :visible.sync="showRefundDialog"
      width="500px"
    >
      <el-form
        ref="refundForm"
        :model="refundForm"
        :rules="refundRules"
        label-width="100px"
      >
        <el-form-item label="退款金额" prop="refundAmount">
          <el-input
            v-model="refundForm.refundAmount"
            placeholder="请输入退款金额"
            type="number"
            step="0.01"
          >
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="退款原因" prop="refundReason">
          <el-select
            v-model="refundForm.refundReason"
            placeholder="请选择退款原因"
            style="width: 100%"
          >
            <el-option label="商品质量问题" value="QUALITY_ISSUE" />
            <el-option label="商品描述不符" value="DESCRIPTION_MISMATCH" />
            <el-option label="买家申请退款" value="BUYER_REQUEST" />
            <el-option label="卖家同意退款" value="SELLER_AGREE" />
            <el-option label="平台介入处理" value="PLATFORM_INTERVENTION" />
            <el-option label="其他原因" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="处理说明" prop="remark">
          <el-input
            type="textarea"
            v-model="refundForm.remark"
            placeholder="请输入处理说明..."
            :rows="4"
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund" :loading="refunding">
          确认退款
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getOrderList,
  getOrderStats,
  getOrderDetail,
  cancelOrder,
  refundOrder,
  exportOrders
} from '@/api/order'
import moment from 'moment'

export default {
  name: 'OrderManagement',
  data() {
    return {
      loading: false,
      refunding: false,
      showDetailDialog: false,
      showRefundDialog: false,
      searchForm: {
        keyword: '',
        status: '',
        tradeType: '',
        startTime: '',
        endTime: ''
      },
      dateRange: null,
      orders: [],
      selectedOrders: [],
      currentOrder: null,
      selectedRefundOrder: null,
      stats: {},
      pagination: {
        current: 1,
        size: 20,
        total: 0
      },
      refundForm: {
        refundAmount: '',
        refundReason: '',
        remark: ''
      },
      refundRules: {
        refundAmount: [
          { required: true, message: '请输入退款金额', trigger: 'blur' },
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的金额格式', trigger: 'blur' }
        ],
        refundReason: [
          { required: true, message: '请选择退款原因', trigger: 'change' }
        ],
        remark: [
          { required: true, message: '请输入处理说明', trigger: 'blur' },
          { min: 10, max: 200, message: '说明长度在 10 到 200 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadOrders()
    this.loadOrderStats()
  },
  methods: {
    async loadOrders() {
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
        
        const res = await getOrderList(params)
        if (res.data.code === 200) {
          this.orders = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载订单列表失败:', error)
        this.$message.error('加载订单列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadOrderStats() {
      try {
        const res = await getOrderStats()
        if (res.data.code === 200) {
          this.stats = res.data.data || {}
        }
      } catch (error) {
        console.error('加载订单统计失败:', error)
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadOrders()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        status: '',
        tradeType: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = null
      this.pagination.current = 1
      this.loadOrders()
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
      this.loadOrders()
    },
    
    handleCurrentChange(page) {
      this.pagination.current = page
      this.loadOrders()
    },
    
    handleSelectionChange(selection) {
      this.selectedOrders = selection
    },
    
    async viewOrderDetail(order) {
      try {
        const res = await getOrderDetail(order.id)
        if (res.data.code === 200) {
          this.currentOrder = res.data.data
          this.showDetailDialog = true
        } else {
          this.$message.error('获取订单详情失败')
        }
      } catch (error) {
        console.error('获取订单详情失败:', error)
        this.$message.error('获取订单详情失败')
      }
    },
    
    async cancelOrder(order) {
      try {
        await this.$confirm(`确定要取消订单 "${order.orderNumber}" 吗？`, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await cancelOrder(order.id, {
          reason: '管理员取消',
          remark: '管理员操作取消订单'
        })
        
        if (res.data.code === 200) {
          this.$message.success('订单已取消')
          this.showDetailDialog = false
          this.loadOrders()
          this.loadOrderStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('取消订单失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    refundOrder(order) {
      this.selectedRefundOrder = order
      this.refundForm = {
        refundAmount: order.totalAmount,
        refundReason: '',
        remark: ''
      }
      this.showRefundDialog = true
    },
    
    async confirmRefund() {
      try {
        await this.$refs.refundForm.validate()
        
        this.refunding = true
        const res = await refundOrder(this.selectedRefundOrder.id, this.refundForm)
        
        if (res.data.code === 200) {
          this.$message.success('退款处理成功')
          this.showRefundDialog = false
          this.showDetailDialog = false
          this.loadOrders()
          this.loadOrderStats()
        } else {
          this.$message.error(res.data.message || '退款处理失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('退款处理失败:', error)
          this.$message.error('退款处理失败，请重试')
        }
      } finally {
        this.refunding = false
      }
    },
    
    async exportOrders() {
      try {
        const params = { ...this.searchForm }
        // 清理空值
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        
        const res = await exportOrders(params)
        
        // 创建下载链接
        const blob = new Blob([res.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `订单数据_${moment().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        
        this.$message.success('订单导出成功')
      } catch (error) {
        console.error('导出订单失败:', error)
        this.$message.error('导出失败，请重试')
      }
    },
    
    async batchExport() {
      try {
        const orderIds = this.selectedOrders.map(order => order.id)
        const res = await exportOrders({ orderIds })
        
        // 创建下载链接
        const blob = new Blob([res.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `批量订单数据_${moment().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
        
        this.$message.success('批量导出成功')
      } catch (error) {
        console.error('批量导出失败:', error)
        this.$message.error('批量导出失败，请重试')
      }
    },
    
    async batchCancel() {
      try {
        await this.$confirm(`确定要批量取消选中的 ${this.selectedOrders.length} 个订单吗？`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const cancelData = {
          reason: '管理员批量取消',
          remark: '管理员批量操作取消订单'
        }
        
        const promises = this.selectedOrders
          .filter(order => this.canCancel(order))
          .map(order => cancelOrder(order.id, cancelData))
        
        await Promise.all(promises)
        
        this.$message.success('批量取消成功')
        this.loadOrders()
        this.loadOrderStats()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量取消失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    canCancel(order) {
      return ['PENDING_PAYMENT', 'PENDING_DELIVERY'].includes(order.status)
    },
    
    canRefund(order) {
      return ['PENDING_RECEIPT', 'COMPLETED'].includes(order.status)
    },
    
    handleImageError(e) {
      e.target.src = '/default-product.png'
    },
    
    getTradeTypeType(tradeType) {
      const typeMap = {
        'ONLINE': 'success',
        'OFFLINE': 'warning'
      }
      return typeMap[tradeType] || 'info'
    },
    
    getTradeTypeText(tradeType) {
      const textMap = {
        'ONLINE': '线上交易',
        'OFFLINE': '线下交易'
      }
      return textMap[tradeType] || tradeType
    },
    
    getStatusType(status) {
      const typeMap = {
        'PENDING_PAYMENT': 'warning',
        'PENDING_DELIVERY': 'primary',
        'PENDING_RECEIPT': 'info',
        'COMPLETED': 'success',
        'CANCELLED': 'danger',
        'REFUNDING': 'warning',
        'REFUNDED': 'info'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'PENDING_PAYMENT': '待付款',
        'PENDING_DELIVERY': '待发货',
        'PENDING_RECEIPT': '待收货',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消',
        'REFUNDING': '退款中',
        'REFUNDED': '已退款'
      }
      return textMap[status] || status
    },
    
    formatTime(time) {
      if (!time) return '-'
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.order-management-container {
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

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-number {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.order-time {
  font-size: 12px;
  color: #666;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
  min-width: 0;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 14px;
  font-weight: 600;
  color: #e6a23c;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.user-contact {
  font-size: 12px;
  color: #666;
}

.amount {
  font-size: 14px;
  font-weight: 600;
  color: #e6a23c;
}

.warning-btn {
  color: #e6a23c !important;
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

/* 订单详情 */
.order-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.product-detail {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.product-detail .product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
}

.product-detail .product-info h5 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.product-detail .product-price {
  font-size: 18px;
  font-weight: 700;
  color: #e6a23c;
  margin: 0 0 8px 0;
}

.product-detail .product-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.users-info {
  display: flex;
  gap: 30px;
}

.user-card {
  flex: 1;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.user-card h5 {
  font-size: 14px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.user-card p {
  font-size: 13px;
  color: #666;
  margin: 0 0 5px 0;
  line-height: 1.4;
}

.address-info p {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.remark {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin: 0;
}

/* 对话框样式 */
.order-detail-dialog .el-dialog__body {
  padding: 20px 20px 10px 20px;
}

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
  
  .product-detail {
    flex-direction: column;
  }
  
  .users-info {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .order-management-container {
    padding: 10px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .stats-summary {
    gap: 20px;
  }
  
  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .product-image {
    width: 60px !important;
    height: 60px !important;
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