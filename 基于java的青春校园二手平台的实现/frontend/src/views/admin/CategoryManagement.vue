<template>
  <div class="category-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>分类管理</h2>
      
      <!-- 统计信息 -->
      <div class="stats-summary">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalCategories || 0 }}</div>
          <div class="stat-label">总分类数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.activeCategories || 0 }}</div>
          <div class="stat-label">启用分类</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
          <div class="stat-label">关联商品</div>
        </div>
      </div>
    </div>
    
    <!-- 操作栏 -->
    <div class="action-section">
      <div class="action-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索分类名称..."
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
          v-model="statusFilter"
          placeholder="分类状态"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="启用" value="ACTIVE" />
          <el-option label="禁用" value="DISABLED" />
        </el-select>
        
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="showAddDialog = true">
          <i class="el-icon-plus"></i>
          添加分类
        </el-button>
        <el-button @click="expandAll">展开全部</el-button>
        <el-button @click="collapseAll">收起全部</el-button>
      </div>
    </div>
    
    <!-- 分类树表格 -->
    <div class="table-section">
      <el-table
        ref="categoryTable"
        v-loading="loading"
        :data="filteredCategories"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      >
        <el-table-column label="分类信息" min-width="250">
          <template slot-scope="scope">
            <div class="category-info">
              <div class="category-icon">
                <i :class="scope.row.icon || 'el-icon-folder'"></i>
              </div>
              <div class="category-details">
                <div class="category-name">{{ scope.row.name }}</div>
                <div class="category-desc">{{ scope.row.description || '暂无描述' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="sort" label="排序" width="80" sortable>
          <template slot-scope="scope">
            <span class="sort-value">{{ scope.row.sort || 0 }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="ACTIVE"
              inactive-value="DISABLED"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="商品数量" width="100">
          <template slot-scope="scope">
            <span class="product-count">{{ scope.row.productCount || 0 }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="150">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="editCategory(scope.row)"
            >
              编辑
            </el-button>
            
            <el-button
              v-if="!scope.row.parentId"
              type="text"
              size="small"
              @click="addSubCategory(scope.row)"
            >
              添加子分类
            </el-button>
            
            <el-button
              type="text"
              size="small"
              @click="moveUp(scope.row)"
              :disabled="!canMoveUp(scope.row)"
            >
              上移
            </el-button>
            
            <el-button
              type="text"
              size="small"
              @click="moveDown(scope.row)"
              :disabled="!canMoveDown(scope.row)"
            >
              下移
            </el-button>
            
            <el-button
              type="text"
              size="small"
              class="danger-btn"
              @click="deleteCategory(scope.row)"
              :disabled="scope.row.productCount > 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="showDialog"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="categoryForm"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="上级分类" prop="parentId">
          <el-cascader
            v-model="categoryForm.parentId"
            :options="parentCategoryOptions"
            :props="cascaderProps"
            placeholder="请选择上级分类（不选则为顶级分类）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="分类图标" prop="icon">
          <div class="icon-selector">
            <el-input
              v-model="categoryForm.icon"
              placeholder="请输入图标类名或选择图标"
              style="width: 300px"
            >
              <template slot="prepend">
                <i :class="categoryForm.icon || 'el-icon-folder'"></i>
              </template>
            </el-input>
            <el-button @click="showIconSelector = true">选择图标</el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input
            type="textarea"
            v-model="categoryForm.description"
            placeholder="请输入分类描述"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="排序值" prop="sort">
          <el-input-number
            v-model="categoryForm.sort"
            :min="0"
            :max="9999"
            placeholder="数值越小排序越靠前"
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio label="ACTIVE">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 图标选择器对话框 -->
    <el-dialog
      title="选择图标"
      :visible.sync="showIconSelector"
      width="800px"
    >
      <div class="icon-grid">
        <div
          v-for="icon in iconList"
          :key="icon"
          class="icon-item"
          :class="{ active: categoryForm.icon === icon }"
          @click="selectIcon(icon)"
        >
          <i :class="icon"></i>
          <span>{{ icon }}</span>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showIconSelector = false">取消</el-button>
        <el-button type="primary" @click="confirmIcon">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCategoryTree,
  getCategoryStats,
  createCategory,
  updateCategory,
  deleteCategory,
  updateCategoryStatus,
  updateCategorySort
} from '@/api/category'
import moment from 'moment'

export default {
  name: 'CategoryManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      showDialog: false,
      showAddDialog: false,
      showIconSelector: false,
      isEdit: false,
      searchKeyword: '',
      statusFilter: '',
      categories: [],
      stats: {},
      categoryForm: {
        id: null,
        parentId: null,
        name: '',
        icon: '',
        description: '',
        sort: 0,
        status: 'ACTIVE'
      },
      categoryRules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序值', trigger: 'blur' },
          { type: 'number', min: 0, max: 9999, message: '排序值范围 0-9999', trigger: 'blur' }
        ]
      },
      cascaderProps: {
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: true,
        emitPath: false
      },
      selectedIcon: '',
      iconList: [
        'el-icon-folder',
        'el-icon-folder-opened',
        'el-icon-document',
        'el-icon-menu',
        'el-icon-setting',
        'el-icon-user',
        'el-icon-phone',
        'el-icon-goods',
        'el-icon-shopping-cart-2',
        'el-icon-shopping-bag-2',
        'el-icon-present',
        'el-icon-box',
        'el-icon-truck',
        'el-icon-house',
        'el-icon-office-building',
        'el-icon-school',
        'el-icon-cpu',
        'el-icon-monitor',
        'el-icon-mobile-phone',
        'el-icon-camera',
        'el-icon-headset',
        'el-icon-basketball',
        'el-icon-football',
        'el-icon-bicycle',
        'el-icon-knife-fork',
        'el-icon-coffee-cup',
        'el-icon-ice-cream',
        'el-icon-cherry',
        'el-icon-apple',
        'el-icon-pear',
        'el-icon-orange',
        'el-icon-watermelon',
        'el-icon-grape',
        'el-icon-strawberry',
        'el-icon-lemon',
        'el-icon-banana'
      ]
    }
  },
  computed: {
    dialogTitle() {
      if (this.showAddDialog) {
        return '添加子分类'
      }
      return this.isEdit ? '编辑分类' : '添加分类'
    },
    
    parentCategoryOptions() {
      // 编辑时排除自己及其子分类
      if (this.isEdit && this.categoryForm.id) {
        return this.filterSelfAndChildren(this.categories, this.categoryForm.id)
      }
      return this.categories
    },
    
    filteredCategories() {
      let result = [...this.categories]
      
      // 关键词搜索
      if (this.searchKeyword) {
        result = this.filterByKeyword(result, this.searchKeyword)
      }
      
      // 状态筛选
      if (this.statusFilter) {
        result = this.filterByStatus(result, this.statusFilter)
      }
      
      return result
    }
  },
  watch: {
    showAddDialog(val) {
      if (val) {
        this.showDialog = true
        this.showAddDialog = false
      }
    }
  },
  created() {
    this.loadCategories()
    this.loadCategoryStats()
  },
  methods: {
    async loadCategories() {
      this.loading = true
      try {
        const res = await getCategoryTree()
        if (res.data.code === 200) {
          this.categories = res.data.data || []
        }
      } catch (error) {
        console.error('加载分类列表失败:', error)
        this.$message.error('加载分类列表失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadCategoryStats() {
      try {
        const res = await getCategoryStats()
        if (res.data.code === 200) {
          this.stats = res.data.data || {}
        }
      } catch (error) {
        console.error('加载分类统计失败:', error)
      }
    },
    
    handleSearch() {
      // 搜索逻辑在计算属性中处理
    },
    
    resetSearch() {
      this.searchKeyword = ''
      this.statusFilter = ''
    },
    
    expandAll() {
      this.$refs.categoryTable.store.states.defaultExpandAll = true
      this.$refs.categoryTable.store.updateTreeExpandKeys(this.getAllKeys(this.categories))
    },
    
    collapseAll() {
      this.$refs.categoryTable.store.states.defaultExpandAll = false
      this.$refs.categoryTable.store.updateTreeExpandKeys([])
    },
    
    getAllKeys(data) {
      let keys = []
      data.forEach(item => {
        keys.push(item.id)
        if (item.children && item.children.length > 0) {
          keys = keys.concat(this.getAllKeys(item.children))
        }
      })
      return keys
    },
    
    editCategory(category) {
      this.isEdit = true
      this.categoryForm = {
        id: category.id,
        parentId: category.parentId || null,
        name: category.name,
        icon: category.icon || '',
        description: category.description || '',
        sort: category.sort || 0,
        status: category.status
      }
      this.showDialog = true
    },
    
    addSubCategory(parentCategory) {
      this.isEdit = false
      this.categoryForm = {
        id: null,
        parentId: parentCategory.id,
        name: '',
        icon: '',
        description: '',
        sort: 0,
        status: 'ACTIVE'
      }
      this.showDialog = true
    },
    
    async handleStatusChange(category) {
      try {
        const res = await updateCategoryStatus(category.id, category.status)
        if (res.data.code === 200) {
          this.$message.success('状态更新成功')
          this.loadCategoryStats()
        } else {
          // 恢复原状态
          category.status = category.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
          this.$message.error(res.data.message || '状态更新失败')
        }
      } catch (error) {
        // 恢复原状态
        category.status = category.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
        console.error('状态更新失败:', error)
        this.$message.error('状态更新失败')
      }
    },
    
    async moveUp(category) {
      try {
        const res = await updateCategorySort(category.id, 'up')
        if (res.data.code === 200) {
          this.$message.success('排序更新成功')
          this.loadCategories()
        } else {
          this.$message.error(res.data.message || '排序更新失败')
        }
      } catch (error) {
        console.error('排序更新失败:', error)
        this.$message.error('排序更新失败')
      }
    },
    
    async moveDown(category) {
      try {
        const res = await updateCategorySort(category.id, 'down')
        if (res.data.code === 200) {
          this.$message.success('排序更新成功')
          this.loadCategories()
        } else {
          this.$message.error(res.data.message || '排序更新失败')
        }
      } catch (error) {
        console.error('排序更新失败:', error)
        this.$message.error('排序更新失败')
      }
    },
    
    canMoveUp(category) {
      // 简化判断，实际应该根据同级分类的排序来判断
      return category.sort > 0
    },
    
    canMoveDown(category) {
      // 简化判断，实际应该根据同级分类的排序来判断
      return true
    },
    
    async deleteCategory(category) {
      if (category.productCount > 0) {
        this.$message.warning('该分类下还有商品，无法删除')
        return
      }
      
      if (category.children && category.children.length > 0) {
        this.$message.warning('该分类下还有子分类，无法删除')
        return
      }
      
      try {
        await this.$confirm(`确定要删除分类 "${category.name}" 吗？`, '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const res = await deleteCategory(category.id)
        if (res.data.code === 200) {
          this.$message.success('分类删除成功')
          this.loadCategories()
          this.loadCategoryStats()
        } else {
          this.$message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除分类失败:', error)
          this.$message.error('删除失败，请重试')
        }
      }
    },
    
    resetForm() {
      this.categoryForm = {
        id: null,
        parentId: null,
        name: '',
        icon: '',
        description: '',
        sort: 0,
        status: 'ACTIVE'
      }
      this.isEdit = false
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    
    async handleSubmit() {
      try {
        await this.$refs.categoryForm.validate()
        
        this.submitting = true
        const formData = { ...this.categoryForm }
        
        // 处理parentId
        if (!formData.parentId) {
          delete formData.parentId
        }
        
        let res
        if (this.isEdit) {
          res = await updateCategory(formData.id, formData)
        } else {
          res = await createCategory(formData)
        }
        
        if (res.data.code === 200) {
          this.$message.success(this.isEdit ? '分类更新成功' : '分类添加成功')
          this.showDialog = false
          this.loadCategories()
          this.loadCategoryStats()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('提交失败:', error)
          this.$message.error('操作失败，请重试')
        }
      } finally {
        this.submitting = false
      }
    },
    
    selectIcon(icon) {
      this.selectedIcon = icon
    },
    
    confirmIcon() {
      if (this.selectedIcon) {
        this.categoryForm.icon = this.selectedIcon
      }
      this.showIconSelector = false
    },
    
    filterSelfAndChildren(categories, excludeId) {
      return categories.filter(category => {
        if (category.id === excludeId) {
          return false
        }
        if (category.children) {
          category.children = this.filterSelfAndChildren(category.children, excludeId)
        }
        return true
      })
    },
    
    filterByKeyword(categories, keyword) {
      const result = []
      categories.forEach(category => {
        if (category.name.toLowerCase().includes(keyword.toLowerCase()) ||
            (category.description && category.description.toLowerCase().includes(keyword.toLowerCase()))) {
          result.push({ ...category })
        } else if (category.children && category.children.length > 0) {
          const filteredChildren = this.filterByKeyword(category.children, keyword)
          if (filteredChildren.length > 0) {
            result.push({
              ...category,
              children: filteredChildren
            })
          }
        }
      })
      return result
    },
    
    filterByStatus(categories, status) {
      const result = []
      categories.forEach(category => {
        if (category.status === status) {
          result.push({ ...category })
        } else if (category.children && category.children.length > 0) {
          const filteredChildren = this.filterByStatus(category.children, status)
          if (filteredChildren.length > 0) {
            result.push({
              ...category,
              children: filteredChildren
            })
          }
        }
      })
      return result
    },
    
    formatTime(time) {
      if (!time) return '-'
      return moment(time).format('YYYY-MM-DD HH:mm')
    }
  }
}
</script>

<style scoped>
.category-management-container {
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

/* 操作栏 */
.action-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-bar {
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f9ff;
  border-radius: 6px;
  color: #409EFF;
  font-size: 16px;
}

.category-details {
  flex: 1;
}

.category-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
}

.category-desc {
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.sort-value {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.product-count {
  font-size: 14px;
  font-weight: 500;
  color: #409EFF;
}

.danger-btn {
  color: #f56c6c !important;
}

/* 图标选择器 */
.icon-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-item:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.icon-item.active {
  border-color: #409EFF;
  background: #409EFF;
  color: white;
}

.icon-item i {
  font-size: 24px;
  margin-bottom: 8px;
}

.icon-item span {
  font-size: 12px;
  text-align: center;
  word-break: break-all;
}

/* 对话框样式 */
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
}

@media (max-width: 768px) {
  .category-management-container {
    padding: 10px;
  }
  
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .stats-summary {
    gap: 20px;
  }
  
  .category-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .category-desc {
    max-width: none;
  }
  
  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }
}

@media (max-width: 480px) {
  .stats-summary {
    flex-direction: column;
    gap: 15px;
  }
  
  .icon-selector {
    flex-direction: column;
    align-items: stretch;
  }
  
  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }
}
</style>