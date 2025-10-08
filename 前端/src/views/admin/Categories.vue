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
        :data="categories"
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
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150">
          <template #default="{ row }">
            <span class="description">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 1 ? 'success' : 'danger'" 
              size="small"
              @click="toggleStatus(row)"
              style="cursor: pointer"
            >
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteCategoryItem(row)"
              link
            >
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
      class="category-dialog"
      @close="resetForm"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
        class="category-form"
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
          <el-input 
            v-model="categoryForm.name" 
            placeholder="请输入分类名称"
            class="form-input"
          />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input 
            v-model="categoryForm.code" 
            placeholder="请输入分类编码"
            class="form-input"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number 
            v-model="categoryForm.sort" 
            :min="0" 
            :max="999"
            class="form-input-number"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
            class="form-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="showAddDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            class="save-btn"
            @click="saveCategory" 
            :loading="saving"
          >
            {{ editingCategory ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus
} from '@element-plus/icons-vue'
import {
  getCategoriesPage,
  getAllCategories,
  getCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  batchDeleteCategories,
  updateCategoryStatus
} from '@/api/category'

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const totalCategories = ref(0)
const selectedCategories = ref([])
const showAddDialog = ref(false)
const editingCategory = ref(null)
const categoryFormRef = ref(null)

// 分类数据
const categories = ref([])

// 分类表单
const categoryForm = reactive({
  parentId: null,
  name: '',
  description: '',
  sort: 0,
  status: 1,
  icon: ''
})

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
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
const parentCategoryOptions = computed(() => {
  return buildCascaderOptions(categories.value)
})

// 方法
const fetchCategories = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    const response = await getCategoriesPage(params)
    if (response.code === 200) {
      // 根据实际API返回结构处理数据
      if (Array.isArray(response.data)) {
        // 如果data直接是数组，构建树形结构
        const treeData = buildCategoryTree(response.data)
        categories.value = treeData
        totalCategories.value = response.data.length
      } else if (response.data && response.data.records) {
        // 如果data是分页对象
        const treeData = buildCategoryTree(response.data.records)
        categories.value = treeData
        totalCategories.value = response.data.total || 0
      } else {
        categories.value = []
        totalCategories.value = 0
      }
    } else {
      ElMessage.error(response.message || '获取分类数据失败')
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
    ElMessage.error('获取分类数据失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedCategories.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchCategories()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchCategories()
}

const toggleStatus = async (category) => {
  try {
    const newStatus = category.status === 1 ? 0 : 1
    const response = await updateCategoryStatus(category.id, newStatus)
    if (response.code === 200) {
      category.status = newStatus
      ElMessage.success(`${category.name} 状态已更新`)
    } else {
      ElMessage.error(response.message || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}



const deleteCategoryItem = async (category) => {
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
    
    const response = await deleteCategory(category.id)
    if (response.code === 200) {
      ElMessage.success('分类删除成功')
      fetchCategories()
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

const saveCategory = async () => {
  try {
    await categoryFormRef.value.validate()
    saving.value = true
    
    let response
    if (editingCategory.value) {
      response = await updateCategory(editingCategory.value.id, categoryForm)
    } else {
      response = await createCategory(categoryForm)
    }
    
    if (response.code === 200) {
      ElMessage.success(editingCategory.value ? '分类更新成功' : '分类添加成功')
      showAddDialog.value = false
      fetchCategories()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  editingCategory.value = null
  Object.assign(categoryForm, {
    parentId: null,
    name: '',
    description: '',
    sort: 0,
    status: 1,
    icon: ''
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
    
    const promises = selectedCategories.value.map(category => 
      updateCategoryStatus(category.id, 1)
    )
    
    await Promise.all(promises)
    ElMessage.success('批量启用成功')
    selectedCategories.value = []
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
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
    
    const promises = selectedCategories.value.map(category => 
      updateCategoryStatus(category.id, 0)
    )
    
    await Promise.all(promises)
    ElMessage.success('批量禁用成功')
    selectedCategories.value = []
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
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
    
    const ids = selectedCategories.value.map(category => category.id)
    const response = await batchDeleteCategories(ids)
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      selectedCategories.value = []
      fetchCategories()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 工具函数
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getLevelTagType = (level) => {
  const types = { 1: 'primary', 2: 'success', 3: 'info' }
  return types[level] || 'info'
}

// 构建分类树形结构
const buildCategoryTree = (flatData) => {
  const map = {}
  const roots = []
  
  // 创建映射表
  flatData.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  
  // 构建树形结构
  flatData.forEach(item => {
    if (item.parentId === 0 || item.parentId === null) {
      roots.push(map[item.id])
    } else {
      if (map[item.parentId]) {
        map[item.parentId].children.push(map[item.id])
      }
    }
  })
  
  return roots
}

const buildCascaderOptions = (data) => {
  return data.filter(item => (item.parentId === 0 || item.parentId === null)).map(item => ({
    id: item.id,
    name: item.name,
    children: data.filter(child => child.parentId === item.id).map(child => ({
      id: child.id,
      name: child.name,
      children: []
    }))
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
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 24px 32px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);

    .header-left {
      .page-title {
        margin: 0 0 8px 0;
        font-size: 28px;
        font-weight: 700;
        color: #1a202c;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .page-description {
        margin: 0;
        color: #718096;
        font-size: 15px;
        font-weight: 400;
      }
    }

    .el-button {
      height: 44px;
      padding: 0 24px;
      font-weight: 600;
      border-radius: 8px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      
      &.el-button--primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
        }
      }
    }
  }

  .batch-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
    padding: 16px 24px;
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    border: 1px solid #90caf9;
    border-radius: 12px;
    box-shadow: 0 2px 10px rgba(33, 150, 243, 0.1);

    .selected-info {
      color: #1565c0;
      font-weight: 600;
      font-size: 15px;
    }

    .el-button {
      height: 36px;
      padding: 0 16px;
      font-weight: 500;
      border-radius: 6px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-1px);
      }
    }
  }

  .table-container {
    background: white;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, 0.2);

    :deep(.el-table) {
      .el-table__header {
        background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
        
        th {
          background: transparent !important;
          color: #2d3748;
          font-weight: 600;
          font-size: 14px;
          padding: 16px 12px;
          border-bottom: 2px solid #e2e8f0;
        }
      }

      .el-table__body {
        tr {
          transition: all 0.3s ease;
          
          &:hover {
            background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
            transform: scale(1.001);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          }

          td {
            padding: 16px 12px;
            border-bottom: 1px solid #f1f5f9;
            vertical-align: middle;
          }
        }
      }
    }

    .category-name {
      display: flex;
      align-items: center;
      gap: 12px;
      font-weight: 500;
      color: #2d3748;
      
      .level-1 {
        font-size: 16px;
        font-weight: 600;
      }
      
      .level-2 {
        font-size: 15px;
        padding-left: 20px;
        color: #4a5568;
      }
      
      .level-3 {
        font-size: 14px;
        padding-left: 40px;
        color: #718096;
      }
    }

    .product-count {
      color: #3182ce;
      font-weight: 600;
      font-size: 15px;
      background: linear-gradient(135deg, #e6f3ff 0%, #cce7ff 100%);
      padding: 4px 12px;
      border-radius: 20px;
      display: inline-block;
    }

    .status-tag {
       &.el-tag--success {
         background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
         color: #155724;
         border: 1px solid #c3e6cb;
         font-weight: 500;
         border-radius: 20px;
         padding: 4px 12px;
       }
       
       &.el-tag--danger {
         background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
         color: #721c24;
         border: 1px solid #f5c6cb;
         font-weight: 500;
         border-radius: 20px;
         padding: 4px 12px;
       }
     }

     .level-tag {
       border-radius: 16px;
       font-weight: 600;
       font-size: 12px;
       padding: 4px 10px;
       
       &.el-tag--primary {
         background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
         color: #1565c0;
         border: 1px solid #90caf9;
       }
       
       &.el-tag--success {
         background: linear-gradient(135deg, #e8f5e8 0%, #c8e6c9 100%);
         color: #2e7d32;
         border: 1px solid #a5d6a7;
       }
       
       &.el-tag--warning {
         background: linear-gradient(135deg, #fff3e0 0%, #ffcc02 100%);
         color: #f57c00;
         border: 1px solid #ffb74d;
       }
     }

    .action-buttons {
      display: flex;
      gap: 8px;
      
      .el-button {
        height: 32px;
        padding: 0 12px;
        font-size: 13px;
        font-weight: 500;
        border-radius: 6px;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-1px);
        }
        
        &.el-button--text {
          &.edit-btn {
            color: #3182ce;
            &:hover {
              background: #e6f3ff;
              color: #2c5aa0;
            }
          }
          
          &.delete-btn {
            color: #e53e3e;
            &:hover {
              background: #fed7d7;
              color: #c53030;
            }
          }
          
          &.add-sub-btn {
            color: #38a169;
            &:hover {
              background: #c6f6d5;
              color: #2f855a;
            }
          }
        }
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding: 24px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
}

// 对话框样式
:deep(.category-dialog) {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 20px 24px;
    border-radius: 16px 16px 0 0;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
    
    .el-dialog__headerbtn {
      .el-dialog__close {
        color: white;
        font-size: 18px;
        
        &:hover {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    background: #fafbfc;
  }

  .el-dialog__footer {
    padding: 16px 24px 24px;
    background: #fafbfc;
    border-radius: 0 0 16px 16px;
  }
}

.category-form {
  .el-form-item {
    margin-bottom: 24px;
    
    .el-form-item__label {
      color: #2d3748;
      font-weight: 600;
      font-size: 14px;
    }
    
    .form-input {
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
        
        &:hover {
          border-color: #667eea;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        
        &.is-focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }
    
    .form-input-number {
      width: 100%;
      
      :deep(.el-input-number) {
        width: 100%;
        
        .el-input__wrapper {
          border-radius: 8px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          border: 1px solid #e2e8f0;
          transition: all 0.3s ease;
          
          &:hover {
            border-color: #667eea;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
          }
          
          &.is-focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }
    }
    
    .form-textarea {
      :deep(.el-textarea__inner) {
        border-radius: 8px;
        border: 1px solid #e2e8f0;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        resize: vertical;
        
        &:hover {
          border-color: #667eea;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        
        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }
    
    .status-radio-group {
      .status-radio {
        margin-right: 24px;
        
        :deep(.el-radio__input) {
          .el-radio__inner {
            border-color: #e2e8f0;
            
            &:hover {
              border-color: #667eea;
            }
          }
          
          &.is-checked .el-radio__inner {
            background-color: #667eea;
            border-color: #667eea;
          }
        }
        
        :deep(.el-radio__label) {
          color: #2d3748;
          font-weight: 500;
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  .cancel-btn {
    height: 40px;
    padding: 0 20px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    color: #4a5568;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #cbd5e0;
      background-color: #f7fafc;
      transform: translateY(-1px);
    }
  }
  
  .save-btn {
    height: 40px;
    padding: 0 24px;
    border-radius: 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .categories-management {
    .table-container {
      :deep(.el-table) {
        .el-table__header th,
        .el-table__body td {
          padding: 12px 6px;
          font-size: 13px;
        }
      }
    }
  }
}

@media (max-width: 992px) {
  .categories-management {
    .page-header {
      .header-left .page-title {
        font-size: 26px;
      }
    }
    
    .batch-actions {
      gap: 10px;
      
      .el-button {
        padding: 8px 16px;
        font-size: 13px;
      }
    }
    
    .table-container {
      :deep(.el-table) {
        .el-table__header th,
        .el-table__body td {
          padding: 10px 4px;
          font-size: 12px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .categories-management {
    padding: 12px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      padding: 16px;
      
      .header-left .page-title {
        font-size: 22px;
      }
      
      .header-right {
        width: 100%;
        justify-content: flex-start;
      }
    }

    .batch-actions {
      flex-wrap: wrap;
      gap: 8px;
      padding: 12px;
      
      .el-button {
        flex: 1;
        min-width: 120px;
        padding: 8px 12px;
        font-size: 12px;
      }
    }

    .table-container {
      margin: 0 -12px;
      border-radius: 0;
      
      :deep(.el-table) {
        .el-table__header th,
        .el-table__body td {
          padding: 8px 4px;
          font-size: 11px;
        }
        
        .el-table__header th:first-child,
        .el-table__body td:first-child {
          padding-left: 8px;
        }
        
        .el-table__header th:last-child,
        .el-table__body td:last-child {
          padding-right: 8px;
        }
      }
      
      .action-buttons {
        flex-direction: column;
        gap: 2px;
        
        .el-button {
          width: 100%;
          justify-content: center;
          padding: 4px 8px;
          font-size: 10px;
          min-height: 24px;
        }
      }
    }
    
    .pagination-container {
      padding: 12px;
      
      :deep(.el-pagination) {
        .el-pagination__sizes,
        .el-pagination__jump {
          display: none;
        }
        
        .el-pager li {
          min-width: 28px;
          height: 28px;
          line-height: 28px;
          font-size: 12px;
        }
        
        .btn-prev,
        .btn-next {
          min-width: 28px;
          height: 28px;
          line-height: 28px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .categories-management {
    padding: 8px;
    
    .page-header {
      padding: 12px;
      
      .header-left .page-title {
        font-size: 20px;
      }
    }
    
    .batch-actions {
      padding: 8px;
      
      .el-button {
        min-width: 100px;
        padding: 6px 10px;
        font-size: 11px;
      }
    }
    
    .table-container {
      :deep(.el-table) {
        .el-table__header th,
        .el-table__body td {
          padding: 6px 2px;
          font-size: 10px;
        }
      }
      
      .category-name {
        font-size: 11px;
        
        &.level-1 { padding-left: 4px; }
        &.level-2 { padding-left: 8px; }
        &.level-3 { padding-left: 12px; }
      }
      
      .level-tag,
      .status-tag {
        font-size: 9px;
        padding: 2px 6px;
        transform: scale(0.9);
      }
    }
    
    .pagination-container {
      padding: 8px;
      
      :deep(.el-pagination) {
        .el-pager li {
          min-width: 24px;
          height: 24px;
          line-height: 24px;
          font-size: 11px;
          margin: 0 1px;
        }
        
        .btn-prev,
        .btn-next {
          min-width: 24px;
          height: 24px;
          line-height: 24px;
        }
      }
    }
  }
}
</style>