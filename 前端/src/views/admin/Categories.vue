<template>
  <div class="categories-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">分类管理</h1>
        <p class="page-description">管理商品分类，维护分类层级结构</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          添加分类
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon primary">
          <el-icon><FolderOpened /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalCategories }}</div>
          <div class="stat-label">总分类数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.activeCategories }}</div>
          <div class="stat-label">启用分类</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><Close /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.inactiveCategories }}</div>
          <div class="stat-label">禁用分类</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalProducts }}</div>
          <div class="stat-label">关联商品</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchQuery"
          placeholder="搜索分类名称..."
          clearable
          @input="handleSearch"
          style="width: 300px;"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="启用" value="active" />
          <el-option label="禁用" value="inactive" />
        </el-select>
        <el-select v-model="levelFilter" placeholder="层级筛选" clearable style="width: 120px;">
          <el-option label="全部" value="" />
          <el-option label="一级分类" value="1" />
          <el-option label="二级分类" value="2" />
          <el-option label="三级分类" value="3" />
        </el-select>
      </div>
      <div class="filter-right">
        <el-button @click="resetFilters">重置筛选</el-button>
        <el-button type="primary" @click="exportCategories">导出数据</el-button>
      </div>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedCategories.length > 0">
      <span class="selected-info">已选择 {{ selectedCategories.length }} 项</span>
      <el-button type="success" @click="batchEnable">批量启用</el-button>
      <el-button type="warning" @click="batchDisable">批量禁用</el-button>
      <el-button type="danger" @click="batchDelete">批量删除</el-button>
    </div>

    <!-- 分类树形表格 -->
    <div class="table-container">
      <el-table
        :data="filteredCategories"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        @selection-change="handleSelectionChange"
        default-expand-all
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="{ row }">
            <div class="category-name">
              <el-icon v-if="row.level === 1" class="level-icon"><Folder /></el-icon>
              <el-icon v-else-if="row.level === 2" class="level-icon"><FolderOpened /></el-icon>
              <el-icon v-else class="level-icon"><Document /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="分类编码" width="120" />
        <el-table-column prop="level" label="层级" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)" size="small">
              {{ row.level }}级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productCount" label="商品数量" width="100">
          <template #default="{ row }">
            <span class="product-count">{{ row.productCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="inactive"
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editCategory(row)">
              编辑
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="addSubCategory(row)"
              v-if="row.level < 3"
            >
              添加子分类
            </el-button>
            <el-button type="danger" size="small" @click="deleteCategory(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="totalCategories"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingCategory ? '编辑分类' : '添加分类'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="上级分类" prop="parentId">
          <el-cascader
            v-model="categoryForm.parentId"
            :options="parentCategoryOptions"
            :props="cascaderProps"
            placeholder="选择上级分类（可选）"
            clearable
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="categoryForm.code" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标类名" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio label="active">启用</el-radio>
            <el-radio label="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="saving">
          {{ editingCategory ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  FolderOpened,
  Check,
  Close,
  Goods,
  Folder,
  Document
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const levelFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalCategories = ref(0)
const selectedCategories = ref([])
const showAddDialog = ref(false)
const editingCategory = ref(null)
const categoryFormRef = ref(null)

// 统计数据
const stats = reactive({
  totalCategories: 156,
  activeCategories: 142,
  inactiveCategories: 14,
  totalProducts: 2847
})

// 分类数据
const categories = ref([
  {
    id: 1,
    name: '电子产品',
    code: 'ELECTRONICS',
    level: 1,
    parentId: null,
    productCount: 856,
    sort: 1,
    status: 'active',
    icon: 'el-icon-mobile-phone',
    description: '各类电子产品',
    createdAt: '2024-01-15 10:30:00',
    children: [
      {
        id: 11,
        name: '手机数码',
        code: 'MOBILE',
        level: 2,
        parentId: 1,
        productCount: 324,
        sort: 1,
        status: 'active',
        icon: 'el-icon-mobile',
        description: '手机及数码配件',
        createdAt: '2024-01-15 10:35:00',
        children: [
          {
            id: 111,
            name: '智能手机',
            code: 'SMARTPHONE',
            level: 3,
            parentId: 11,
            productCount: 156,
            sort: 1,
            status: 'active',
            createdAt: '2024-01-15 10:40:00'
          },
          {
            id: 112,
            name: '手机配件',
            code: 'MOBILE_ACC',
            level: 3,
            parentId: 11,
            productCount: 168,
            sort: 2,
            status: 'active',
            createdAt: '2024-01-15 10:45:00'
          }
        ]
      },
      {
        id: 12,
        name: '电脑办公',
        code: 'COMPUTER',
        level: 2,
        parentId: 1,
        productCount: 532,
        sort: 2,
        status: 'active',
        icon: 'el-icon-monitor',
        description: '电脑及办公设备',
        createdAt: '2024-01-15 11:00:00'
      }
    ]
  },
  {
    id: 2,
    name: '服装鞋帽',
    code: 'CLOTHING',
    level: 1,
    parentId: null,
    productCount: 1245,
    sort: 2,
    status: 'active',
    icon: 'el-icon-shopping-bag-1',
    description: '各类服装鞋帽',
    createdAt: '2024-01-16 09:20:00',
    children: [
      {
        id: 21,
        name: '男装',
        code: 'MENS_WEAR',
        level: 2,
        parentId: 2,
        productCount: 456,
        sort: 1,
        status: 'active',
        createdAt: '2024-01-16 09:25:00'
      },
      {
        id: 22,
        name: '女装',
        code: 'WOMENS_WEAR',
        level: 2,
        parentId: 2,
        productCount: 789,
        sort: 2,
        status: 'active',
        createdAt: '2024-01-16 09:30:00'
      }
    ]
  },
  {
    id: 3,
    name: '图书文具',
    code: 'BOOKS',
    level: 1,
    parentId: null,
    productCount: 746,
    sort: 3,
    status: 'inactive',
    icon: 'el-icon-reading',
    description: '图书及文具用品',
    createdAt: '2024-01-17 14:15:00'
  }
])

// 分类表单
const categoryForm = reactive({
  parentId: null,
  name: '',
  code: '',
  icon: '',
  sort: 0,
  status: 'active',
  description: ''
})

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入分类编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '分类编码只能包含大写字母和下划线', trigger: 'blur' }
  ]
}

// 级联选择器配置
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false
}

// 计算属性
const filteredCategories = computed(() => {
  let result = categories.value
  
  if (searchQuery.value) {
    result = filterBySearch(result, searchQuery.value)
  }
  
  if (statusFilter.value) {
    result = filterByStatus(result, statusFilter.value)
  }
  
  if (levelFilter.value) {
    result = filterByLevel(result, parseInt(levelFilter.value))
  }
  
  return result
})

const parentCategoryOptions = computed(() => {
  return buildCascaderOptions(categories.value)
})

// 方法
const fetchCategories = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    totalCategories.value = categories.value.length
  } catch (error) {
    ElMessage.error('获取分类数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  levelFilter.value = ''
  currentPage.value = 1
}

const handleSelectionChange = (selection) => {
  selectedCategories.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchCategories()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchCategories()
}

const toggleStatus = async (category) => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success(`${category.name} 状态已更新`)
  } catch (error) {
    // 恢复原状态
    category.status = category.status === 'active' ? 'inactive' : 'active'
    ElMessage.error('状态更新失败')
  }
}

const editCategory = (category) => {
  editingCategory.value = category
  Object.assign(categoryForm, {
    parentId: category.parentId,
    name: category.name,
    code: category.code,
    icon: category.icon,
    sort: category.sort,
    status: category.status,
    description: category.description
  })
  showAddDialog.value = true
}

const addSubCategory = (parentCategory) => {
  editingCategory.value = null
  resetForm()
  categoryForm.parentId = parentCategory.id
  showAddDialog.value = true
}

const deleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${category.name}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 从数据中移除
    removeFromCategories(categories.value, category.id)
    
    ElMessage.success('分类删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveCategory = async () => {
  try {
    await categoryFormRef.value.validate()
    saving.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (editingCategory.value) {
      ElMessage.success('分类更新成功')
    } else {
      ElMessage.success('分类添加成功')
    }
    
    showAddDialog.value = false
    fetchCategories()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  editingCategory.value = null
  Object.assign(categoryForm, {
    parentId: null,
    name: '',
    code: '',
    icon: '',
    sort: 0,
    status: 'active',
    description: ''
  })
  categoryFormRef.value?.clearValidate()
}

const batchEnable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要启用选中的 ${selectedCategories.value.length} 个分类吗？`,
      '批量启用',
      { type: 'info' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedCategories.value.forEach(category => {
      category.status = 'active'
    })
    
    ElMessage.success('批量启用成功')
    selectedCategories.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量启用失败')
    }
  }
}

const batchDisable = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用选中的 ${selectedCategories.value.length} 个分类吗？`,
      '批量禁用',
      { type: 'warning' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedCategories.value.forEach(category => {
      category.status = 'inactive'
    })
    
    ElMessage.success('批量禁用成功')
    selectedCategories.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量禁用失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCategories.value.length} 个分类吗？此操作不可恢复。`,
      '批量删除',
      { type: 'error' }
    )
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedCategories.value.forEach(category => {
      removeFromCategories(categories.value, category.id)
    })
    
    ElMessage.success('批量删除成功')
    selectedCategories.value = []
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const exportCategories = () => {
  // 模拟导出功能
  ElMessage.success('分类数据导出成功')
}

// 工具函数
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getLevelTagType = (level) => {
  const types = { 1: 'primary', 2: 'success', 3: 'info' }
  return types[level] || 'info'
}

const filterBySearch = (data, query) => {
  const result = []
  for (const item of data) {
    if (item.name.toLowerCase().includes(query.toLowerCase()) ||
        item.code.toLowerCase().includes(query.toLowerCase())) {
      result.push(item)
    } else if (item.children) {
      const filteredChildren = filterBySearch(item.children, query)
      if (filteredChildren.length > 0) {
        result.push({ ...item, children: filteredChildren })
      }
    }
  }
  return result
}

const filterByStatus = (data, status) => {
  const result = []
  for (const item of data) {
    if (item.status === status) {
      result.push(item)
    } else if (item.children) {
      const filteredChildren = filterByStatus(item.children, status)
      if (filteredChildren.length > 0) {
        result.push({ ...item, children: filteredChildren })
      }
    }
  }
  return result
}

const filterByLevel = (data, level) => {
  const result = []
  for (const item of data) {
    if (item.level === level) {
      result.push(item)
    } else if (item.children && item.level < level) {
      const filteredChildren = filterByLevel(item.children, level)
      if (filteredChildren.length > 0) {
        result.push({ ...item, children: filteredChildren })
      }
    }
  }
  return result
}

const buildCascaderOptions = (data) => {
  return data.filter(item => item.level < 3).map(item => ({
    id: item.id,
    name: item.name,
    children: item.children ? buildCascaderOptions(item.children) : []
  }))
}

const removeFromCategories = (data, id) => {
  for (let i = 0; i < data.length; i++) {
    if (data[i].id === id) {
      data.splice(i, 1)
      return true
    }
    if (data[i].children && removeFromCategories(data[i].children, id)) {
      return true
    }
  }
  return false
}

// 生命周期
onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.categories-management {
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
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
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

        &.primary {
          background: #e3f2fd;
          color: #1976d2;
        }

        &.success {
          background: #e8f5e8;
          color: #4caf50;
        }

        &.warning {
          background: #fff3e0;
          color: #ff9800;
        }

        &.info {
          background: #e1f5fe;
          color: #00bcd4;
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
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .filter-left {
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .filter-right {
      display: flex;
      gap: 12px;
    }
  }

  .batch-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding: 12px 20px;
    background: #f0f9ff;
    border: 1px solid #bae6fd;
    border-radius: 8px;

    .selected-info {
      color: #0369a1;
      font-weight: 500;
    }
  }

  .table-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .category-name {
      display: flex;
      align-items: center;
      gap: 8px;

      .level-icon {
        color: #909399;
      }
    }

    .product-count {
      color: #409eff;
      font-weight: 500;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .categories-management {
    padding: 10px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .filter-section {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .filter-left,
      .filter-right {
        justify-content: center;
      }
    }

    .stats-cards {
      grid-template-columns: 1fr;
    }

    .batch-actions {
      flex-wrap: wrap;
    }
  }
}
</style>