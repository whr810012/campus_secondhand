<template>
  <div class="products-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">商品管理</h1>
        <p class="page-description">管理平台所有商品信息，包括审核、上下架等操作</p>
      </div>

    </div>





    <!-- 商品列表 -->
    <div class="products-table">
      <el-card>
        <el-table
          :data="products"
          v-loading="loading"

          stripe
        >

          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                  :src="row.images[0]"
                  :preview-src-list="row.images"
                  class="product-image"
                  fit="cover"
                />
                <div class="product-details">
                  <div class="product-name">{{ row.title }}</div>
                  <div class="product-desc">{{ row.description }}</div>
                  <div class="product-meta">
                    <el-tag size="small">{{ row.category }}</el-tag>
                    <span class="product-id">ID: {{ row.id }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" prop="price" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="卖家" width="120">
            <template #default="{ row }">
              <div class="seller-info">
                <div class="seller-name">{{ row.sellerNickname }}</div>
                <div class="seller-contact">{{ row.sellerPhone }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" prop="createdAt" width="120">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="浏览/收藏/咨询" width="120">
            <template #default="{ row }">
              <div class="stats">
                <div>{{ row.views }} 浏览</div>
                <div>{{ row.favorites }} 收藏</div>
                <div>{{ row.inquiries }} 咨询</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button size="small" @click="viewProduct(row)">查看</el-button>
                <el-button
                  v-if="row.auditStatus === 0"
                  size="small"
                  type="success"
                  @click="approveProductAction(row)"
                >
                  审核通过
                </el-button>
                <el-button
                  v-if="row.auditStatus === 0"
                  size="small"
                  type="warning"
                  @click="rejectProductAction(row)"
                >
                  审核拒绝
                </el-button>
                <el-button
                  v-if="row.status === 'available' && row.auditStatus === 1"
                  size="small"
                  type="warning"
                  @click="offlineProduct(row)"
                >
                  下架
                </el-button>
                <el-button
                  v-if="row.status === 'unavailable'"
                  size="small"
                  type="success"
                  @click="onlineProduct(row)"
                >
                  上架
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteProduct(row)"
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

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      title="商品详情"
      width="800px"
      :before-close="closeProductDetail"
    >
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="product-images">
              <el-carousel height="300px">
                <el-carousel-item v-for="image in selectedProduct.images" :key="image">
                  <el-image :src="image" fit="cover" style="width: 100%; height: 100%" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="product-info-detail">
              <h3>{{ selectedProduct.title }}</h3>
              <div class="price-info">
                <span class="current-price">¥{{ selectedProduct.price }}</span>
                <span v-if="selectedProduct.originalPrice" class="original-price">
                  ¥{{ selectedProduct.originalPrice }}
                </span>
              </div>
              <div class="product-tags">
                <el-tag>{{ selectedProduct.category }}</el-tag>
                <el-tag :type="getStatusType(selectedProduct.status)">
                  {{ getStatusText(selectedProduct.status) }}
                </el-tag>
                <el-tag :type="getAuditStatusType(selectedProduct.auditStatus)">
                  {{ getAuditStatusText(selectedProduct.auditStatus) }}
                </el-tag>
              </div>
              <div class="product-stats">
                <div class="stat-item">
                  <span class="label">浏览次数：</span>
                  <span class="value">{{ selectedProduct.views }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">收藏次数：</span>
                  <span class="value">{{ selectedProduct.favorites }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">咨询次数：</span>
                  <span class="value">{{ selectedProduct.inquiries }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">发布时间：</span>
                  <span class="value">{{ formatDate(selectedProduct.createdAt) }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="product-description">
          <h4>商品描述</h4>
          <p>{{ selectedProduct.description }}</p>
        </div>
        <div class="seller-info-detail">
          <h4>卖家信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="label">昵称：</span>
                <span class="value">{{ selectedProduct.sellerNickname }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ selectedProduct.sellerPhone }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">学号：</span>
                <span class="value">{{ selectedProduct.sellerStudentId }}</span>
              </div>
              <div class="info-item">
                <span class="label">分类：</span>
                <span class="value">{{ selectedProduct.categoryName }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeProductDetail">关闭</el-button>
          <el-button
              v-if="selectedProduct?.auditStatus === 0"
              type="success"
              @click="approveProductAction(selectedProduct)"
            >
              审核通过
            </el-button>
            <el-button
              v-if="selectedProduct?.auditStatus === 0"
              type="warning"
              @click="rejectProductAction(selectedProduct)"
            >
              审核拒绝
            </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProductList,
  approveProduct,
  rejectProduct,
  getImageById
} from '@/api/admin'
import { updateProductStatus, deleteProduct as deleteProductAPI } from '@/api/product'

// 响应式数据
const loading = ref(false)
const products = ref([])
const productDetailVisible = ref(false)
const selectedProduct = ref(null)

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 状态映射
const statusMap = {
  available: '可售',
  reserved: '已预定', 
  sold: '已售出',
  unavailable: '已下架'
}

// 审核状态映射
const auditStatusMap = {
  0: '待审核',
  1: '审核通过',
  2: '审核拒绝'
}



// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status] || status
}

// 获取审核状态文本
const getAuditStatusText = (auditStatus) => {
  return auditStatusMap[auditStatus] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    available: 'success',
    reserved: 'warning',
    sold: 'success',
    unavailable: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取审核状态类型
const getAuditStatusType = (auditStatus) => {
  const typeMap = {
    0: 'warning',
    1: 'success', 
    2: 'danger'
  }
  return typeMap[auditStatus] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    const { data } = await getProductList(params)
    
    // 处理商品数据 - 直接使用API返回的数据结构
    const processedProducts = data.records.map(product => {
      return {
        ...product,
        name: product.title,
        images: product.imageList || product.images || ['/api/placeholder/300/300'],
        seller: {
          name: product.sellerNickname || '未知用户',
          phone: product.sellerPhone || '未知',
          studentId: product.sellerStudentId || '未知',
          college: product.sellerCollege || '未知'
        },
        views: product.viewCount || 0,
        favorites: product.favoriteCount || 0,
        inquiries: product.inquiryCount || 0,
        category: product.categoryName || '未知分类',
        createdAt: product.createdAt
      }
    })
    
    products.value = processedProducts
    pagination.total = data.total
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}





// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchProducts()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchProducts()
}

// 查看商品详情
const viewProduct = (product) => {
  selectedProduct.value = product
  productDetailVisible.value = true
}

// 关闭商品详情
const closeProductDetail = () => {
  productDetailVisible.value = false
  selectedProduct.value = null
}

// 审核通过
const approveProductAction = async (product) => {
  try {
    await ElMessageBox.confirm('确认审核通过该商品？', '确认操作', {
      type: 'warning'
    })
    
    await approveProduct(product.id, { adminId: 1 }) // 这里应该从用户状态获取管理员ID
    
    ElMessage.success('审核通过成功')
    await fetchProducts() // 重新获取商品列表
    
    if (productDetailVisible.value) {
      closeProductDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败')
    }
  }
}

// 审核拒绝
const rejectProductAction = async (product) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    await rejectProduct(product.id, { 
      adminId: 1, // 这里应该从用户状态获取管理员ID
      reason 
    })
    
    ElMessage.success('审核拒绝成功')
    await fetchProducts() // 重新获取商品列表
    
    if (productDetailVisible.value) {
      closeProductDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核拒绝失败:', error)
      ElMessage.error('审核拒绝失败')
    }
  }
}

// 下架商品
const offlineProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认下架该商品？', '确认操作', {
      type: 'warning'
    })
    
    // 调用API接口更新商品状态为unavailable
    await updateProductStatus(product.id, 'unavailable')
    
    product.status = 'unavailable'
    ElMessage.success('下架成功')
    await fetchProducts() // 重新获取商品列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架商品失败:', error)
      ElMessage.error('下架失败')
    }
  }
}

// 上架商品
const onlineProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认上架该商品？', '确认操作', {
      type: 'warning'
    })
    
    // 调用API接口更新商品状态为available
    await updateProductStatus(product.id, 'available')
    
    product.status = 'available'
    ElMessage.success('上架成功')
    await fetchProducts() // 重新获取商品列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('上架商品失败:', error)
      ElMessage.error('上架失败')
    }
  }
}

// 删除商品
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm('确认删除该商品？删除后无法恢复！', '确认删除', {
      type: 'error'
    })
    
    // 调用API删除商品
    await deleteProductAPI(product.id, product.sellerId)
    
    // 删除成功后重新获取商品列表
    await fetchProducts()
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败')
    }
  }
}



// 初始化
onMounted(async () => {
  await fetchProducts() // 获取商品数据
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables.scss' as *;

.products-management {
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





  .products-table {
    margin-bottom: 20px;

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

        .product-desc {
          font-size: 12px;
          color: #909399;
          margin-bottom: 6px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .product-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .product-id {
            font-size: 12px;
            color: #c0c4cc;
          }
        }
      }
    }

    .price {
      font-weight: 600;
      color: #e6a23c;
      font-size: 16px;
    }

    .seller-info {
      .seller-name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 2px;
      }

      .seller-contact {
        font-size: 12px;
        color: #909399;
      }
    }

    .stats {
      font-size: 12px;
      color: #909399;
      line-height: 1.4;
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

  .product-detail {
    .product-images {
      .el-carousel {
        border-radius: 8px;
        overflow: hidden;
      }
    }

    .product-info-detail {
      h3 {
        margin: 0 0 16px 0;
        font-size: 20px;
        color: #303133;
      }

      .price-info {
        margin-bottom: 16px;

        .current-price {
          font-size: 24px;
          font-weight: 600;
          color: #e6a23c;
          margin-right: 12px;
        }

        .original-price {
          font-size: 16px;
          color: #c0c4cc;
          text-decoration: line-through;
        }
      }

      .product-tags {
        margin-bottom: 16px;

        .el-tag {
          margin-right: 8px;
        }
      }

      .product-stats {
        .stat-item {
          display: flex;
          margin-bottom: 8px;

          .label {
            width: 80px;
            color: #909399;
            font-size: 14px;
          }

          .value {
            color: #303133;
            font-size: 14px;
          }
        }
      }
    }

    .product-description {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;

      h4 {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #303133;
      }

      p {
        margin: 0;
        color: #606266;
        line-height: 1.6;
      }
    }

    .seller-info-detail {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;

      h4 {
        margin: 0 0 16px 0;
        font-size: 16px;
        color: #303133;
      }

      .info-item {
        display: flex;
        margin-bottom: 8px;

        .label {
          width: 80px;
          color: #909399;
          font-size: 14px;
        }

        .value {
          color: #303133;
          font-size: 14px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .products-management {
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

    .products-table {
      .action-buttons {
        flex-direction: column;

        .el-button {
          width: 100%;
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style>