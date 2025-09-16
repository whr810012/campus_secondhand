<template>
  <div class="my-products-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2>我的商品</h2>
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="$router.push('/user/publish')"
        >
          发布商品
        </el-button>
      </div>
      
      <!-- 统计信息 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-number">{{ stats.total || 0 }}</div>
          <div class="stat-label">全部商品</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.pending || 0 }}</div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.approved || 0 }}</div>
          <div class="stat-label">已上架</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ stats.sold || 0 }}</div>
          <div class="stat-label">已售出</div>
        </div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索商品标题..."
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
          placeholder="商品状态"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已上架" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="已售出" value="SOLD" />
          <el-option label="已下架" value="OFFLINE" />
        </el-select>
        
        <el-select
          v-model="searchForm.categoryId"
          placeholder="商品分类"
          clearable
          @change="handleSearch"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="products-section">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="products.length > 0" class="products-list">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-item"
        >
          <div class="product-image">
            <img :src="product.imageUrl || '/images/default-product.jpg'" :alt="product.title" />
            <div class="product-status">
              <el-tag
                :type="getStatusType(product.status)"
                size="small"
              >
                {{ getStatusText(product.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="product-info">
            <h4 class="product-title">{{ product.title }}</h4>
            <p class="product-description">{{ product.description }}</p>
            
            <div class="product-meta">
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              
              <div class="product-stats">
                <span class="stat-item">
                  <i class="el-icon-view"></i>
                  {{ product.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <i class="el-icon-star-on"></i>
                  {{ product.likeCount || 0 }}
                </span>
              </div>
            </div>
            
            <div class="product-footer">
              <div class="product-time">
                发布时间：{{ formatTime(product.createTime) }}
              </div>
              
              <div class="product-actions">
                <el-button
                  size="small"
                  @click="viewProduct(product.id)"
                >
                  查看
                </el-button>
                
                <el-button
                  v-if="canEdit(product.status)"
                  type="primary"
                  size="small"
                  @click="editProduct(product)"
                >
                  编辑
                </el-button>
                
                <el-dropdown
                  @command="handleCommand"
                  trigger="click"
                >
                  <el-button size="small">
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      v-if="product.status === 'APPROVED'"
                      :command="{action: 'offline', product}"
                    >
                      下架商品
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="product.status === 'OFFLINE'"
                      :command="{action: 'online', product}"
                    >
                      重新上架
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="product.status === 'APPROVED'"
                      :command="{action: 'sold', product}"
                    >
                      标记售出
                    </el-dropdown-item>
                    <el-dropdown-item
                      :command="{action: 'delete', product}"
                      divided
                    >
                      删除商品
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无商品">
          <el-button type="primary" @click="$router.push('/user/publish')">
            发布第一个商品
          </el-button>
        </el-empty>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-section" v-if="pagination.total > 0">
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
    
    <!-- 编辑商品对话框 -->
    <el-dialog
      title="编辑商品"
      :visible.sync="editDialogVisible"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editForm"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="商品标题" prop="title">
          <el-input
            v-model="editForm.title"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input
            v-model="editForm.price"
            style="width: 200px"
          >
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="商品状况" prop="condition">
          <el-radio-group v-model="editForm.condition">
            <el-radio label="NEW">全新</el-radio>
            <el-radio label="LIKE_NEW">几乎全新</el-radio>
            <el-radio label="GOOD">良好</el-radio>
            <el-radio label="FAIR">一般</el-radio>
            <el-radio label="POOR">较差</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="editLoading">
          保存
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getMyProducts, updateProduct, deleteProduct, markAsSold, toggleProductStatus } from '@/api/product'
import { getEnabledCategories } from '@/api/category'
import moment from 'moment'

export default {
  name: 'MyProducts',
  data() {
    return {
      loading: false,
      searchForm: {
        keyword: '',
        status: '',
        categoryId: null
      },
      products: [],
      categories: [],
      stats: {},
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      editDialogVisible: false,
      editLoading: false,
      editForm: {
        id: null,
        title: '',
        price: '',
        description: '',
        condition: 'GOOD'
      },
      editRules: {
        title: [
          { required: true, message: '请输入商品标题', trigger: 'blur' },
          { min: 5, max: 50, message: '标题长度在5到50个字符', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的价格格式', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' },
          { min: 10, max: 500, message: '描述长度在10到500个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadCategories()
    this.loadProducts()
  },
  methods: {
    async loadCategories() {
      try {
        const res = await getEnabledCategories()
        if (res.data.code === 200) {
          this.categories = res.data.data
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
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
        
        const res = await getMyProducts(params)
        if (res.data.code === 200) {
          this.products = res.data.data.records || []
          this.pagination.total = res.data.data.total || 0
          this.stats = res.data.data.stats || {}
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        this.$message.error('加载商品失败')
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadProducts()
    },
    
    resetSearch() {
      this.searchForm = {
        keyword: '',
        status: '',
        categoryId: null
      }
      this.pagination.current = 1
      this.loadProducts()
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
    
    viewProduct(productId) {
      this.$router.push(`/user/product/${productId}`)
    },
    
    editProduct(product) {
      this.editForm = {
        id: product.id,
        title: product.title,
        price: product.price.toString(),
        description: product.description,
        condition: product.condition || 'GOOD'
      }
      this.editDialogVisible = true
    },
    
    async saveEdit() {
      this.$refs.editForm.validate(async (valid) => {
        if (!valid) return
        
        this.editLoading = true
        try {
          const updateData = {
            ...this.editForm,
            price: parseFloat(this.editForm.price)
          }
          
          const res = await updateProduct(this.editForm.id, updateData)
          if (res.data.code === 200) {
            this.$message.success('商品更新成功')
            this.editDialogVisible = false
            this.loadProducts()
          } else {
            this.$message.error(res.data.message || '更新失败')
          }
        } catch (error) {
          console.error('更新商品失败:', error)
          this.$message.error('更新失败，请重试')
        } finally {
          this.editLoading = false
        }
      })
    },
    
    async handleCommand({ action, product }) {
      switch (action) {
        case 'offline':
          await this.offlineProduct(product)
          break
        case 'online':
          await this.onlineProduct(product)
          break
        case 'sold':
          await this.markAsSold(product)
          break
        case 'delete':
          await this.deleteProduct(product)
          break
      }
    },
    
    async offlineProduct(product) {
      try {
        await this.$confirm('确定要下架这个商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await toggleProductStatus(product.id, 0)
        if (res.data.code === 200) {
          this.$message.success('商品已下架')
          this.loadProducts()
        } else {
          this.$message.error(res.data.message || '下架失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('下架商品失败:', error)
          this.$message.error('下架失败，请重试')
        }
      }
    },
    
    async onlineProduct(product) {
      try {
        const res = await toggleProductStatus(product.id, 1)
        if (res.data.code === 200) {
          this.$message.success('商品已重新上架')
          this.loadProducts()
        } else {
          this.$message.error(res.data.message || '上架失败')
        }
      } catch (error) {
        console.error('上架商品失败:', error)
        this.$message.error('上架失败，请重试')
      }
    },
    
    async markAsSold(product) {
      try {
        await this.$confirm('确定要标记这个商品为已售出吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await markAsSold(product.id)
        if (res.data.code === 200) {
          this.$message.success('商品已标记为售出')
          this.loadProducts()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('标记售出失败:', error)
          this.$message.error('操作失败，请重试')
        }
      }
    },
    
    async deleteProduct(product) {
      try {
        await this.$confirm('确定要删除这个商品吗？删除后无法恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteProduct(product.id)
        if (res.data.code === 200) {
          this.$message.success('商品已删除')
          this.loadProducts()
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
    
    canEdit(status) {
      return ['PENDING', 'REJECTED', 'APPROVED'].includes(status)
    },
    
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'SOLD': 'info',
        'OFFLINE': 'info'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'PENDING': '待审核',
        'APPROVED': '已上架',
        'REJECTED': '已拒绝',
        'SOLD': '已售出',
        'OFFLINE': '已下架'
      }
      return textMap[status] || status
    },
    
    formatTime(time) {
      if (!time) return ''
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.my-products-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-content h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.stat-number {
  font-size: 28px;
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

/* 商品列表 */
.products-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.loading-container {
  padding: 40px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s;
}

.product-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.product-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 20px;
  position: relative;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-status {
  position: absolute;
  top: 8px;
  left: 8px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.product-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 16px;
  color: #e74c3c;
  font-weight: 500;
}

.price-value {
  font-size: 24px;
  color: #e74c3c;
  font-weight: 700;
  margin-left: 2px;
}

.product-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.stat-item i {
  margin-right: 4px;
  font-size: 14px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.product-time {
  font-size: 12px;
  color: #999;
}

.product-actions {
  display: flex;
  gap: 10px;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-products-container {
    padding: 10px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .product-item {
    flex-direction: column;
  }
  
  .product-image {
    width: 100%;
    height: 200px;
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .product-footer {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
}
</style>