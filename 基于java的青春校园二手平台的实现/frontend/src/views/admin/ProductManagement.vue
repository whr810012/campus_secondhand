<template>
  <div class="product-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>商品管理</h2>
      
      <!-- 统计信息 -->
      <div class="stats-summary">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
          <div class="stat-label">总商品数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.pendingProducts || 0 }}</div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.onlineProducts || 0 }}</div>
          <div class="stat-label">已上架</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.soldProducts || 0 }}</div>
          <div class="stat-label">已售出</div>
        </div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索商品名称、描述..."
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
          v-model="searchForm.categoryId"
          placeholder="商品分类"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部分类" value="" />
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        
        <el-select
          v-model="searchForm.status"
          placeholder="商品状态"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已上架" value="ONLINE" />
          <el-option label="已下架" value="OFFLINE" />
          <el-option label="已售出" value="SOLD" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        
        <el-select
          v-model="searchForm.condition"
          placeholder="商品状况"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状况" value="" />
          <el-option label="全新" value="NEW" />
          <el-option label="几乎全新" value="LIKE_NEW" />
          <el-option label="轻微使用" value="LIGHTLY_USED" />
          <el-option label="明显使用" value="HEAVILY_USED" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="发布开始日期"
          end-placeholder="发布结束日期"
          @change="handleDateChange"
        />
        
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="products"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="商品信息" min-width="250">
          <template slot-scope="scope">
            <div class="product-info">
              <div class="product-image">
                <img
                  :src="scope.row.images && scope.row.images[0] || '/default-product.png'"
                  :alt="scope.row.title"
                  @error="handleImageError"
                />
              </div>
              <div class="product-details">
                <div class="product-title">{{ scope.row.title }}</div>
                <div class="product-price">¥{{ scope.row.price }}</div>
                <div class="product-seller">卖家: {{ scope.row.sellerName || '未知' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="分类" width="120">
          <template slot-scope="scope">
            <el-tag size="small">
              {{ scope.row.categoryName || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状况" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getConditionType(scope.row.condition)"
              size="small"
            >
              {{ getConditionText(scope.row.condition) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="统计" width="120">
          <template slot-scope="scope">
            <div class="product-stats">
              <div class="stat-line">
                <span class="stat-label">浏览:</span>
                <span class="stat-value">{{ scope.row.viewCount || 0 }}</span>
              </div>
              <div class="stat-line">
                <span class="stat-label">点赞:</span>
                <span class="stat-value">{{ scope.row.likeCount || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="发布时间" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewProductDetail(scope.row)"
            >
              查看
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="text"
              size="small"
              class="success-btn"
              @click="approveProduct(scope.row)"
            >
              通过
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="text"
              size="small"
              class="danger-btn"
              @click="rejectProduct(scope.row)"
            >
              拒绝
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'ONLINE'"
              type="text"
              size="small"
              class="warning-btn"
              @click="offlineProduct(scope.row)"
            >
              下架
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'OFFLINE'"
              type="text"
              size="small"
              class="success-btn"
              @click="onlineProduct(scope.row)"
            >
              上架
            </el-button>
            
            <el-button
              type="text"
              size="small"
              class="danger-btn"
              @click="deleteProduct(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedProducts.length > 0">
        <span class="selected-info">已选择 {{ selectedProducts.length }} 个商品</span>
        <el-button size="small" @click="batchApprove">批量通过</el-button>
        <el-button size="small" @click="batchReject">批量拒绝</el-button>
        <el-button size="small" @click="batchOffline">批量下架</el-button>
        <el-button size="small" type="danger" @click="batchDelete">批量删除</el-button>
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
    
    <!-- 商品详情对话框 -->
    <el-dialog
      title="商品详情"
      :visible.sync="showDetailDialog"
      width="800px"
      class="product-detail-dialog"
    >
      <div v-if="currentProduct" class="product-detail">
        <!-- 商品图片 -->
        <div class="detail-images">
          <el-carousel
            v-if="currentProduct.images && currentProduct.images.length > 0"
            height="300px"
            indicator-position="outside"
          >
            <el-carousel-item
              v-for="(image, index) in currentProduct.images"
              :key="index"
            >
              <img :src="image" :alt="currentProduct.title" />
            </el-carousel-item>
          </el-carousel>
          <div v-else class="no-image">
            <i class="el-icon-picture"></i>
            <p>暂无图片</p>
          </div>
        </div>
        
        <!-- 商品信息 -->
        <div class="detail-info">
          <h3>{{ currentProduct.title }}</h3>
          <div class="price">¥{{ currentProduct.price }}</div>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="商品分类">
              {{ currentProduct.categoryName || '未分类' }}
            </el-descriptions-item>
            <el-descriptions-item label="商品状况">
              <el-tag :type="getConditionType(currentProduct.condition)" size="small">
                {{ getConditionText(currentProduct.condition) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="交易方式">
              {{ getTradeTypeText(currentProduct.tradeType) }}
            </el-descriptions-item>
            <el-descriptions-item label="商品状态">
              <el-tag :type="getStatusType(currentProduct.status)" size="small">
                {{ getStatusText(currentProduct.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="卖家">
              {{ currentProduct.sellerName || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系方式">
              {{ currentProduct.contactInfo || '未提供' }}
            </el-descriptions-item>
            <el-descriptions-item label="浏览次数">
              {{ currentProduct.viewCount || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="点赞次数">
              {{ currentProduct.likeCount || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="发布时间">
              {{ formatTime(currentProduct.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="更新时间">
              {{ formatTime(currentProduct.updateTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="商品描述" :span="2">
              <div class="description">
                {{ currentProduct.description || '暂无描述' }}
              </div>
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 商品标签 -->
          <div v-if="currentProduct.tags && currentProduct.tags.length > 0" class="product-tags">
            <span class="tags-label">标签：</span>
            <el-tag
              v-for="tag in currentProduct.tags"
              :key="tag"
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button
          v-if="currentProduct && currentProduct.status === 'PENDING'"
          type="success"
          @click="approveProduct(currentProduct)"
        >
          通过审核
        </el-button>
        <el-button
          v-if="currentProduct && currentProduct.status === 'PENDING'"
          type="danger"
          @click="rejectProduct(currentProduct)"
        >
          拒绝审核
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 拒绝原因对话框 -->
    <el-dialog
      title="拒绝原因"
      :visible.sync="showRejectDialog"
      width="500px"
    >
      <el-form
        ref="rejectForm"
        :model="rejectForm"
        :rules="rejectRules"
        label-width="80px"
      >
        <el-form-item label="拒绝原因" prop="reason">
          <el-select
            v-model="rejectForm.reason"
            placeholder="请选择拒绝原因"
            style="width: 100%"
          >
            <el-option label="商品信息不完整" value="INCOMPLETE_INFO" />
            <el-option label="商品图片不清晰" value="UNCLEAR_IMAGES" />
            <el-option label="价格不合理" value="UNREASONABLE_PRICE" />
            <el-option label="违禁商品" value="PROHIBITED_ITEM" />
            <el-option label="重复发布" value="DUPLICATE_POST" />
            <el-option label="其他原因" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="详细说明" prop="remark">
          <el-input
            type="textarea"
            v-model="rejectForm.remark"
            placeholder="请输入详细的拒绝说明..."
            :rows="4"
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">
          确认拒绝
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getProductList,
  getProductStats,
  approveProduct,
  rejectProduct,
  updateProductStatus,
  deleteProduct
} from '@/api/product'
import { getCategoryList } from '@/api/category'
import moment from 'moment'

export default {
  name: 'ProductManagement',
  data() {
    return {
      loading: false,
      rejecting: false,
      showDetailDialog: false,
      showRejectDialog: false,
      searchForm: {
        keyword: '',
        categoryId: '',
        status: '',
        condition: '',
        startTime: '',
        endTime: ''
      },
      dateRange: null,
      products: [],
      categories: [],
      selectedProducts: [],
      currentProduct: null,
      selectedRejectProduct: null,
      stats: {},
      pagination: {
        current: 1,
        size: 20,
        total: 0
      },
      rejectForm: {
        reason: '',
        remark: ''
      },
      rejectRules: {
        reason: [
          { required: true, message: '请选择拒绝原因', trigger: 'change' }
        ],
        remark: [
          { required: true, message: '请输入详细说明', trigger: 'blur' },
          { min: 10, max: 200, message: '说明长度在 10 到 200 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadProducts()
    this.loadCategories()
    this.loadProductStats()
  },
  methods: {
    async loadProducts() {
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
        
        const res = await getProductList(params)
        if (res.data.code === 200) {
          this.products = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
        }
      } catch (error) {
        console.error('加载商品列表失败:', error)
        this.$message.error('加载商品列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadCategories() {
      try {
        const res = await getCategoryList()
        if (res.data.code === 200) {
          this.categories = res.data.data || []
        }
      } catch (error) {
        console.error('加载分类列表失败:', error)
      }
    },
    
    async loadProductStats() {
      try {
        const res = await getProductStats()
        if (res.data.code === 200) {
          this.stats = res.data.data || {}
        }
      } catch (error) {
        console.error('加载商品统计失败:', error)
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadProducts()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        categoryId: '',
        status: '',
        condition: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = null
      this.pagination.current = 1
      this.loadProducts()
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
      this.loadProducts()
    },
    
    handleCurrentChange(page) {
      this.pagination.current = page
      this.loadProducts()
    },
    
    handleSelectionChange(selection) {
      this.selectedProducts = selection
    },
    
    viewProductDetail(product) {
      this.currentProduct = product
      this.showDetailDialog = true
    },
    
    async approveProduct(product) {
      try {
        await this.$confirm(`确定要通过商品 "${product.title}" 的审核吗？`, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        })
        
        const res = await approveProduct(product.id)
        if (res.data.code === 200) {
          this.$message.success('商品审核通过')
          this.showDetailDialog = false
          this.loadProducts()
          this.loadProductStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核通过失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    rejectProduct(product) {
      this.selectedRejectProduct = product
      this.rejectForm = {
        reason: '',
        remark: ''
      }
      this.showRejectDialog = true
    },
    
    async confirmReject() {
      try {
        await this.$refs.rejectForm.validate()
        
        this.rejecting = true
        const res = await rejectProduct(this.selectedRejectProduct.id, this.rejectForm)
        
        if (res.data.code === 200) {
          this.$message.success('商品已拒绝')
          this.showRejectDialog = false
          this.showDetailDialog = false
          this.loadProducts()
          this.loadProductStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('拒绝审核失败:', error)
          this.$message.error('操作失败，请重试')
        }
      } finally {
        this.rejecting = false
      }
    },
    
    async offlineProduct(product) {
      try {
        await this.$confirm(`确定要下架商品 "${product.title}" 吗？`, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await updateProductStatus(product.id, 'OFFLINE')
        if (res.data.code === 200) {
          this.$message.success('商品已下架')
          this.loadProducts()
          this.loadProductStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('下架商品失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    async onlineProduct(product) {
      try {
        const res = await updateProductStatus(product.id, 'ONLINE')
        if (res.data.code === 200) {
          this.$message.success('商品已上架')
          this.loadProducts()
          this.loadProductStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        console.error('上架商品失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    async deleteProduct(product) {
      try {
        await this.$confirm(
          `确定要删除商品 "${product.title}" 吗？删除后无法恢复。`,
          '危险操作',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'error'
          }
        )
        
        const res = await deleteProduct(product.id)
        if (res.data.code === 200) {
          this.$message.success('商品已删除')
          this.loadProducts()
          this.loadProductStats()
        } else {
          this.$message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除商品失败:', error)
          this.$message.error('删除失败，请重试')
        }
      }
    },
    
    async batchApprove() {
      try {
        await this.$confirm(`确定要批量通过选中的 ${this.selectedProducts.length} 个商品吗？`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        })
        
        const promises = this.selectedProducts
          .filter(p => p.status === 'PENDING')
          .map(product => approveProduct(product.id))
        
        await Promise.all(promises)
        
        this.$message.success('批量审核通过成功')
        this.loadProducts()
        this.loadProductStats()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量审核通过失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    async batchReject() {
      try {
        await this.$confirm(`确定要批量拒绝选中的 ${this.selectedProducts.length} 个商品吗？`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const rejectData = {
          reason: 'OTHER',
          remark: '批量拒绝操作'
        }
        
        const promises = this.selectedProducts
          .filter(p => p.status === 'PENDING')
          .map(product => rejectProduct(product.id, rejectData))
        
        await Promise.all(promises)
        
        this.$message.success('批量拒绝成功')
        this.loadProducts()
        this.loadProductStats()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量拒绝失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    async batchOffline() {
      try {
        await this.$confirm(`确定要批量下架选中的 ${this.selectedProducts.length} 个商品吗？`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const promises = this.selectedProducts
          .filter(p => p.status === 'ONLINE')
          .map(product => updateProductStatus(product.id, 'OFFLINE'))
        
        await Promise.all(promises)
        
        this.$message.success('批量下架成功')
        this.loadProducts()
        this.loadProductStats()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量下架失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    async batchDelete() {
      try {
        await this.$confirm(
          `确定要批量删除选中的 ${this.selectedProducts.length} 个商品吗？删除后无法恢复。`,
          '危险操作',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'error'
          }
        )
        
        const promises = this.selectedProducts.map(product => deleteProduct(product.id))
        await Promise.all(promises)
        
        this.$message.success('批量删除成功')
        this.loadProducts()
        this.loadProductStats()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败:', error)
          this.$message.error('批量操作失败，请重试')
        }
      }
    },
    
    handleImageError(e) {
      e.target.src = '/default-product.png'
    },
    
    getConditionType(condition) {
      const typeMap = {
        'NEW': 'success',
        'LIKE_NEW': 'success',
        'LIGHTLY_USED': 'warning',
        'HEAVILY_USED': 'danger'
      }
      return typeMap[condition] || 'info'
    },
    
    getConditionText(condition) {
      const textMap = {
        'NEW': '全新',
        'LIKE_NEW': '几乎全新',
        'LIGHTLY_USED': '轻微使用',
        'HEAVILY_USED': '明显使用'
      }
      return textMap[condition] || condition
    },
    
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'ONLINE': 'success',
        'OFFLINE': 'info',
        'SOLD': 'success',
        'REJECTED': 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'PENDING': '待审核',
        'ONLINE': '已上架',
        'OFFLINE': '已下架',
        'SOLD': '已售出',
        'REJECTED': '已拒绝'
      }
      return textMap[status] || status
    },
    
    getTradeTypeText(tradeType) {
      const textMap = {
        'ONLINE': '线上交易',
        'OFFLINE': '线下交易',
        'BOTH': '线上线下'
      }
      return textMap[tradeType] || tradeType
    },
    
    formatTime(time) {
      if (!time) return '-'
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.product-management-container {
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

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
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
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: #e6a23c;
  margin-bottom: 2px;
}

.product-seller {
  font-size: 12px;
  color: #666;
}

.product-stats {
  font-size: 12px;
}

.stat-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2px;
}

.stat-label {
  color: #666;
}

.stat-value {
  color: #409EFF;
  font-weight: 500;
}

.success-btn {
  color: #67c23a !important;
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

/* 商品详情 */
.product-detail {
  display: flex;
  gap: 30px;
}

.detail-images {
  flex: 1;
}

.detail-images .el-carousel {
  border-radius: 8px;
  overflow: hidden;
}

.detail-images img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #909399;
}

.no-image i {
  font-size: 48px;
  margin-bottom: 10px;
}

.detail-info {
  flex: 1;
}

.detail-info h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
}

.detail-info .price {
  font-size: 24px;
  font-weight: 700;
  color: #e6a23c;
  margin-bottom: 20px;
}

.description {
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}

.product-tags {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.tags-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 4px;
}

/* 对话框样式 */
.product-detail-dialog .el-dialog__body {
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
}

@media (max-width: 768px) {
  .product-management-container {
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
    width: 80px;
    height: 80px;
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
  
  .product-tags {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>