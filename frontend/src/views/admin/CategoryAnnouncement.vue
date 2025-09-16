<template>
  <div class="category-announcement">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>分类与公告管理</h1>
      <p>管理商品分类和系统公告</p>
    </div>

    <!-- 管理选项卡 -->
    <div class="manage-tabs">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'categories' }"
        @click="switchTab('categories')"
      >
        分类管理
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'announcements' }"
        @click="switchTab('announcements')"
      >
        公告管理
      </button>
    </div>

    <!-- 分类管理 -->
    <div v-if="activeTab === 'categories'" class="category-section">
      <!-- 分类统计 -->
      <div class="category-stats">
        <div class="stat-card">
          <div class="stat-icon total-icon">
            <i class="fas fa-tags"></i>
          </div>
          <div class="stat-content">
            <h3>{{ categoryStats.totalCategories }}</h3>
            <p>总分类数</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon active-icon">
            <i class="fas fa-check-circle"></i>
          </div>
          <div class="stat-content">
            <h3>{{ categoryStats.activeCategories }}</h3>
            <p>启用分类</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon product-icon">
            <i class="fas fa-box"></i>
          </div>
          <div class="stat-content">
            <h3>{{ categoryStats.totalProducts }}</h3>
            <p>关联商品</p>
          </div>
        </div>
      </div>

      <!-- 分类操作 -->
      <div class="category-actions">
        <button @click="showAddCategoryModal" class="add-btn">
          <i class="fas fa-plus"></i>
          添加分类
        </button>
        <button @click="refreshCategories" class="refresh-btn">
          <i class="fas fa-sync-alt"></i>
          刷新
        </button>
      </div>

      <!-- 分类列表 -->
      <div class="category-list">
        <div class="category-tree">
          <div 
            v-for="category in categories" 
            :key="category.id" 
            class="category-item"
            :class="{ disabled: !category.enabled }"
          >
            <div class="category-header">
              <div class="category-info">
                <div class="category-icon">
                  <i :class="category.icon || 'fas fa-tag'"></i>
                </div>
                <div class="category-details">
                  <h4>{{ category.name }}</h4>
                  <p v-if="category.description">{{ category.description }}</p>
                  <div class="category-meta">
                    <span class="product-count">
                      <i class="fas fa-box"></i>
                      {{ category.productCount || 0 }} 个商品
                    </span>
                    <span class="category-status" :class="{ enabled: category.enabled }">
                      <i class="fas" :class="category.enabled ? 'fa-check-circle' : 'fa-times-circle'"></i>
                      {{ category.enabled ? '已启用' : '已禁用' }}
                    </span>
                  </div>
                </div>
              </div>
              
              <div class="category-actions">
                <button @click="editCategory(category)" class="action-btn edit">
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  @click="toggleCategoryStatus(category)" 
                  class="action-btn toggle"
                  :class="{ enabled: category.enabled }"
                >
                  <i class="fas" :class="category.enabled ? 'fa-eye-slash' : 'fa-eye'"></i>
                </button>
                <button 
                  @click="deleteCategory(category)" 
                  class="action-btn delete"
                  :disabled="category.productCount > 0"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
            
            <!-- 子分类 -->
            <div v-if="category.children && category.children.length > 0" class="subcategories">
              <div 
                v-for="subcategory in category.children" 
                :key="subcategory.id" 
                class="subcategory-item"
                :class="{ disabled: !subcategory.enabled }"
              >
                <div class="subcategory-info">
                  <h5>{{ subcategory.name }}</h5>
                  <p v-if="subcategory.description">{{ subcategory.description }}</p>
                  <div class="subcategory-meta">
                    <span class="product-count">
                      <i class="fas fa-box"></i>
                      {{ subcategory.productCount || 0 }} 个商品
                    </span>
                    <span class="category-status" :class="{ enabled: subcategory.enabled }">
                      <i class="fas" :class="subcategory.enabled ? 'fa-check-circle' : 'fa-times-circle'"></i>
                      {{ subcategory.enabled ? '已启用' : '已禁用' }}
                    </span>
                  </div>
                </div>
                
                <div class="subcategory-actions">
                  <button @click="editCategory(subcategory)" class="action-btn edit">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button 
                    @click="toggleCategoryStatus(subcategory)" 
                    class="action-btn toggle"
                    :class="{ enabled: subcategory.enabled }"
                  >
                    <i class="fas" :class="subcategory.enabled ? 'fa-eye-slash' : 'fa-eye'"></i>
                  </button>
                  <button 
                    @click="deleteCategory(subcategory)" 
                    class="action-btn delete"
                    :disabled="subcategory.productCount > 0"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 公告管理 -->
    <div v-if="activeTab === 'announcements'" class="announcement-section">
      <!-- 公告统计 -->
      <div class="announcement-stats">
        <div class="stat-card">
          <div class="stat-icon total-icon">
            <i class="fas fa-bullhorn"></i>
          </div>
          <div class="stat-content">
            <h3>{{ announcementStats.totalAnnouncements }}</h3>
            <p>总公告数</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon active-icon">
            <i class="fas fa-broadcast-tower"></i>
          </div>
          <div class="stat-content">
            <h3>{{ announcementStats.activeAnnouncements }}</h3>
            <p>发布中</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon view-icon">
            <i class="fas fa-eye"></i>
          </div>
          <div class="stat-content">
            <h3>{{ announcementStats.totalViews }}</h3>
            <p>总浏览量</p>
          </div>
        </div>
      </div>

      <!-- 公告操作 -->
      <div class="announcement-actions">
        <button @click="showAddAnnouncementModal" class="add-btn">
          <i class="fas fa-plus"></i>
          发布公告
        </button>
        
        <div class="filter-options">
          <select v-model="announcementFilters.status" @change="fetchAnnouncements">
            <option value="">全部状态</option>
            <option value="DRAFT">草稿</option>
            <option value="PUBLISHED">已发布</option>
            <option value="ARCHIVED">已归档</option>
          </select>
          
          <select v-model="announcementFilters.type" @change="fetchAnnouncements">
            <option value="">全部类型</option>
            <option value="SYSTEM">系统公告</option>
            <option value="ACTIVITY">活动公告</option>
            <option value="MAINTENANCE">维护公告</option>
            <option value="POLICY">政策公告</option>
          </select>
          
          <input 
            v-model="announcementFilters.keyword" 
            type="text" 
            placeholder="搜索公告标题" 
            @keyup.enter="fetchAnnouncements"
          />
          
          <button @click="fetchAnnouncements" class="search-btn">
            <i class="fas fa-search"></i>
          </button>
        </div>
      </div>

      <!-- 公告列表 -->
      <div class="announcement-list">
        <div class="announcements-grid">
          <div 
            v-for="announcement in announcements" 
            :key="announcement.id" 
            class="announcement-card"
            :class="announcement.status.toLowerCase()"
          >
            <div class="card-header">
              <div class="announcement-type">
                <span class="type-badge" :class="announcement.type.toLowerCase()">
                  {{ getAnnouncementTypeText(announcement.type) }}
                </span>
                <span class="status-badge" :class="announcement.status.toLowerCase()">
                  {{ getAnnouncementStatusText(announcement.status) }}
                </span>
              </div>
              
              <div class="card-actions">
                <button @click="viewAnnouncement(announcement)" class="action-btn view">
                  <i class="fas fa-eye"></i>
                </button>
                <button @click="editAnnouncement(announcement)" class="action-btn edit">
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  @click="toggleAnnouncementStatus(announcement)" 
                  class="action-btn toggle"
                >
                  <i class="fas" :class="announcement.status === 'PUBLISHED' ? 'fa-pause' : 'fa-play'"></i>
                </button>
                <button @click="deleteAnnouncement(announcement)" class="action-btn delete">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
            
            <div class="card-content">
              <h4>{{ announcement.title }}</h4>
              <p class="announcement-summary">{{ announcement.summary || announcement.content.substring(0, 100) + '...' }}</p>
              
              <div class="announcement-meta">
                <div class="meta-item">
                  <i class="fas fa-calendar"></i>
                  <span>{{ formatTime(announcement.createdAt) }}</span>
                </div>
                <div class="meta-item" v-if="announcement.publishedAt">
                  <i class="fas fa-broadcast-tower"></i>
                  <span>{{ formatTime(announcement.publishedAt) }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-eye"></i>
                  <span>{{ announcement.viewCount || 0 }} 次浏览</span>
                </div>
                <div class="meta-item" v-if="announcement.priority">
                  <i class="fas fa-star"></i>
                  <span>置顶</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <button 
          @click="changeAnnouncementPage(announcementPagination.current - 1)" 
          :disabled="announcementPagination.current <= 1"
          class="page-btn"
        >
          上一页
        </button>
        
        <span class="page-info">
          第 {{ announcementPagination.current }} 页，共 {{ announcementPagination.pages }} 页
        </span>
        
        <button 
          @click="changeAnnouncementPage(announcementPagination.current + 1)" 
          :disabled="announcementPagination.current >= announcementPagination.pages"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 分类编辑弹窗 -->
    <div v-if="showCategoryModal" class="modal-overlay" @click="closeCategoryModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingCategory ? '编辑分类' : '添加分类' }}</h3>
          <button @click="closeCategoryModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="saveCategory">
            <div class="form-group">
              <label>分类名称 *</label>
              <input 
                v-model="categoryForm.name" 
                type="text" 
                placeholder="请输入分类名称" 
                required
              />
            </div>
            
            <div class="form-group">
              <label>分类描述</label>
              <textarea 
                v-model="categoryForm.description" 
                placeholder="请输入分类描述" 
                rows="3"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>父分类</label>
              <select v-model="categoryForm.parentId">
                <option value="">无（顶级分类）</option>
                <option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :value="category.id"
                  :disabled="editingCategory && category.id === editingCategory.id"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>分类图标</label>
              <div class="icon-selector">
                <input 
                  v-model="categoryForm.icon" 
                  type="text" 
                  placeholder="请输入图标类名，如：fas fa-laptop" 
                />
                <div class="icon-preview">
                  <i :class="categoryForm.icon || 'fas fa-tag'"></i>
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label>排序权重</label>
              <input 
                v-model.number="categoryForm.sortOrder" 
                type="number" 
                placeholder="数字越大排序越靠前" 
                min="0"
              />
            </div>
            
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  v-model="categoryForm.enabled" 
                  type="checkbox"
                />
                <span>启用分类</span>
              </label>
            </div>
          </form>
        </div>
        
        <div class="modal-footer">
          <button @click="closeCategoryModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="saveCategory" class="modal-btn save">
            {{ editingCategory ? '保存' : '添加' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 公告编辑弹窗 -->
    <div v-if="showAnnouncementModal" class="modal-overlay" @click="closeAnnouncementModal">
      <div class="modal-content announcement-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ editingAnnouncement ? '编辑公告' : '发布公告' }}</h3>
          <button @click="closeAnnouncementModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="saveAnnouncement">
            <div class="form-row">
              <div class="form-group">
                <label>公告标题 *</label>
                <input 
                  v-model="announcementForm.title" 
                  type="text" 
                  placeholder="请输入公告标题" 
                  required
                />
              </div>
              
              <div class="form-group">
                <label>公告类型 *</label>
                <select v-model="announcementForm.type" required>
                  <option value="">请选择类型</option>
                  <option value="SYSTEM">系统公告</option>
                  <option value="ACTIVITY">活动公告</option>
                  <option value="MAINTENANCE">维护公告</option>
                  <option value="POLICY">政策公告</option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label>公告摘要</label>
              <textarea 
                v-model="announcementForm.summary" 
                placeholder="请输入公告摘要（可选）" 
                rows="2"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>公告内容 *</label>
              <textarea 
                v-model="announcementForm.content" 
                placeholder="请输入公告内容" 
                rows="8"
                required
              ></textarea>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>发布时间</label>
                <input 
                  v-model="announcementForm.publishedAt" 
                  type="datetime-local"
                />
                <small>留空则立即发布</small>
              </div>
              
              <div class="form-group">
                <label>过期时间</label>
                <input 
                  v-model="announcementForm.expiredAt" 
                  type="datetime-local"
                />
                <small>留空则永不过期</small>
              </div>
            </div>
            
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  v-model="announcementForm.priority" 
                  type="checkbox"
                />
                <span>置顶公告</span>
              </label>
            </div>
            
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  v-model="announcementForm.sendNotification" 
                  type="checkbox"
                />
                <span>发送通知给所有用户</span>
              </label>
            </div>
          </form>
        </div>
        
        <div class="modal-footer">
          <button @click="closeAnnouncementModal" class="modal-btn cancel">
            取消
          </button>
          <button @click="saveDraft" class="modal-btn draft" v-if="!editingAnnouncement">
            保存草稿
          </button>
          <button @click="saveAnnouncement" class="modal-btn save">
            {{ editingAnnouncement ? '保存' : '发布' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <div v-if="showAnnouncementDetailModal" class="modal-overlay" @click="closeAnnouncementDetailModal">
      <div class="modal-content announcement-detail-modal" @click.stop>
        <div class="modal-header">
          <h3>公告详情</h3>
          <button @click="closeAnnouncementDetailModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedAnnouncement">
          <div class="announcement-detail">
            <div class="detail-header">
              <h2>{{ selectedAnnouncement.title }}</h2>
              <div class="detail-meta">
                <span class="type-badge" :class="selectedAnnouncement.type.toLowerCase()">
                  {{ getAnnouncementTypeText(selectedAnnouncement.type) }}
                </span>
                <span class="status-badge" :class="selectedAnnouncement.status.toLowerCase()">
                  {{ getAnnouncementStatusText(selectedAnnouncement.status) }}
                </span>
                <span v-if="selectedAnnouncement.priority" class="priority-badge">
                  <i class="fas fa-star"></i>
                  置顶
                </span>
              </div>
            </div>
            
            <div class="detail-info">
              <div class="info-item">
                <label>发布时间：</label>
                <span>{{ formatTime(selectedAnnouncement.publishedAt || selectedAnnouncement.createdAt) }}</span>
              </div>
              <div class="info-item" v-if="selectedAnnouncement.expiredAt">
                <label>过期时间：</label>
                <span>{{ formatTime(selectedAnnouncement.expiredAt) }}</span>
              </div>
              <div class="info-item">
                <label>浏览次数：</label>
                <span>{{ selectedAnnouncement.viewCount || 0 }} 次</span>
              </div>
            </div>
            
            <div class="detail-content">
              <h4>公告内容</h4>
              <div class="content-text" v-html="selectedAnnouncement.content.replace(/\n/g, '<br>')">
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

export default {
  name: 'CategoryAnnouncement',
  setup() {
    const activeTab = ref('categories')
    const loading = ref(false)
    
    // 分类相关
    const categories = ref([])
    const editingCategory = ref(null)
    const showCategoryModal = ref(false)
    
    const categoryStats = reactive({
      totalCategories: 0,
      activeCategories: 0,
      totalProducts: 0
    })
    
    const categoryForm = reactive({
      name: '',
      description: '',
      parentId: '',
      icon: '',
      sortOrder: 0,
      enabled: true
    })
    
    // 公告相关
    const announcements = ref([])
    const editingAnnouncement = ref(null)
    const selectedAnnouncement = ref(null)
    const showAnnouncementModal = ref(false)
    const showAnnouncementDetailModal = ref(false)
    
    const announcementStats = reactive({
      totalAnnouncements: 0,
      activeAnnouncements: 0,
      totalViews: 0
    })
    
    const announcementFilters = reactive({
      status: '',
      type: '',
      keyword: ''
    })
    
    const announcementPagination = reactive({
      current: 1,
      size: 10,
      total: 0,
      pages: 0
    })
    
    const announcementForm = reactive({
      title: '',
      type: '',
      summary: '',
      content: '',
      publishedAt: '',
      expiredAt: '',
      priority: false,
      sendNotification: false
    })

    // 切换选项卡
    const switchTab = (tab) => {
      activeTab.value = tab
      if (tab === 'categories') {
        fetchCategories()
        fetchCategoryStats()
      } else {
        fetchAnnouncements()
        fetchAnnouncementStats()
      }
    }

    // 获取分类统计
    const fetchCategoryStats = async () => {
      try {
        const response = await api.get('/admin/categories/stats')
        Object.assign(categoryStats, response.data)
      } catch (error) {
        console.error('获取分类统计失败:', error)
      }
    }

    // 获取分类列表
    const fetchCategories = async () => {
      try {
        const response = await api.get('/admin/categories')
        categories.value = response.data
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    }

    // 刷新分类
    const refreshCategories = () => {
      fetchCategories()
      fetchCategoryStats()
    }

    // 显示添加分类弹窗
    const showAddCategoryModal = () => {
      editingCategory.value = null
      Object.assign(categoryForm, {
        name: '',
        description: '',
        parentId: '',
        icon: '',
        sortOrder: 0,
        enabled: true
      })
      showCategoryModal.value = true
    }

    // 编辑分类
    const editCategory = (category) => {
      editingCategory.value = category
      Object.assign(categoryForm, {
        name: category.name,
        description: category.description || '',
        parentId: category.parentId || '',
        icon: category.icon || '',
        sortOrder: category.sortOrder || 0,
        enabled: category.enabled
      })
      showCategoryModal.value = true
    }

    // 保存分类
    const saveCategory = async () => {
      try {
        if (editingCategory.value) {
          await api.put(`/admin/categories/${editingCategory.value.id}`, categoryForm)
          alert('分类更新成功')
        } else {
          await api.post('/admin/categories', categoryForm)
          alert('分类添加成功')
        }
        
        await fetchCategories()
        await fetchCategoryStats()
        closeCategoryModal()
      } catch (error) {
        console.error('保存分类失败:', error)
        alert('保存分类失败，请重试')
      }
    }

    // 切换分类状态
    const toggleCategoryStatus = async (category) => {
      try {
        await api.put(`/admin/categories/${category.id}/toggle`)
        alert(`分类已${category.enabled ? '禁用' : '启用'}`)
        await fetchCategories()
        await fetchCategoryStats()
      } catch (error) {
        console.error('切换分类状态失败:', error)
        alert('操作失败，请重试')
      }
    }

    // 删除分类
    const deleteCategory = async (category) => {
      if (category.productCount > 0) {
        alert('该分类下还有商品，无法删除')
        return
      }
      
      if (!confirm(`确定要删除分类 "${category.name}" 吗？此操作不可恢复！`)) return
      
      try {
        await api.delete(`/admin/categories/${category.id}`)
        alert('分类删除成功')
        await fetchCategories()
        await fetchCategoryStats()
      } catch (error) {
        console.error('删除分类失败:', error)
        alert('删除分类失败，请重试')
      }
    }

    // 关闭分类弹窗
    const closeCategoryModal = () => {
      showCategoryModal.value = false
      editingCategory.value = null
    }

    // 获取公告统计
    const fetchAnnouncementStats = async () => {
      try {
        const response = await api.get('/admin/announcements/stats')
        Object.assign(announcementStats, response.data)
      } catch (error) {
        console.error('获取公告统计失败:', error)
      }
    }

    // 获取公告列表
    const fetchAnnouncements = async () => {
      try {
        const params = {
          page: announcementPagination.current,
          size: announcementPagination.size,
          ...announcementFilters
        }
        
        const response = await api.get('/admin/announcements', { params })
        announcements.value = response.data.records
        announcementPagination.total = response.data.total
        announcementPagination.pages = response.data.pages
      } catch (error) {
        console.error('获取公告列表失败:', error)
      }
    }

    // 显示添加公告弹窗
    const showAddAnnouncementModal = () => {
      editingAnnouncement.value = null
      Object.assign(announcementForm, {
        title: '',
        type: '',
        summary: '',
        content: '',
        publishedAt: '',
        expiredAt: '',
        priority: false,
        sendNotification: false
      })
      showAnnouncementModal.value = true
    }

    // 编辑公告
    const editAnnouncement = (announcement) => {
      editingAnnouncement.value = announcement
      Object.assign(announcementForm, {
        title: announcement.title,
        type: announcement.type,
        summary: announcement.summary || '',
        content: announcement.content,
        publishedAt: announcement.publishedAt ? new Date(announcement.publishedAt).toISOString().slice(0, 16) : '',
        expiredAt: announcement.expiredAt ? new Date(announcement.expiredAt).toISOString().slice(0, 16) : '',
        priority: announcement.priority || false,
        sendNotification: false
      })
      showAnnouncementModal.value = true
    }

    // 查看公告详情
    const viewAnnouncement = (announcement) => {
      selectedAnnouncement.value = announcement
      showAnnouncementDetailModal.value = true
    }

    // 保存草稿
    const saveDraft = async () => {
      try {
        await api.post('/admin/announcements', {
          ...announcementForm,
          status: 'DRAFT'
        })
        alert('草稿保存成功')
        await fetchAnnouncements()
        await fetchAnnouncementStats()
        closeAnnouncementModal()
      } catch (error) {
        console.error('保存草稿失败:', error)
        alert('保存草稿失败，请重试')
      }
    }

    // 保存公告
    const saveAnnouncement = async () => {
      try {
        const data = {
          ...announcementForm,
          status: 'PUBLISHED'
        }
        
        if (editingAnnouncement.value) {
          await api.put(`/admin/announcements/${editingAnnouncement.value.id}`, data)
          alert('公告更新成功')
        } else {
          await api.post('/admin/announcements', data)
          alert('公告发布成功')
        }
        
        await fetchAnnouncements()
        await fetchAnnouncementStats()
        closeAnnouncementModal()
      } catch (error) {
        console.error('保存公告失败:', error)
        alert('保存公告失败，请重试')
      }
    }

    // 切换公告状态
    const toggleAnnouncementStatus = async (announcement) => {
      try {
        const newStatus = announcement.status === 'PUBLISHED' ? 'ARCHIVED' : 'PUBLISHED'
        await api.put(`/admin/announcements/${announcement.id}/status`, { status: newStatus })
        alert(`公告已${newStatus === 'PUBLISHED' ? '发布' : '归档'}`)
        await fetchAnnouncements()
        await fetchAnnouncementStats()
      } catch (error) {
        console.error('切换公告状态失败:', error)
        alert('操作失败，请重试')
      }
    }

    // 删除公告
    const deleteAnnouncement = async (announcement) => {
      if (!confirm(`确定要删除公告 "${announcement.title}" 吗？此操作不可恢复！`)) return
      
      try {
        await api.delete(`/admin/announcements/${announcement.id}`)
        alert('公告删除成功')
        await fetchAnnouncements()
        await fetchAnnouncementStats()
      } catch (error) {
        console.error('删除公告失败:', error)
        alert('删除公告失败，请重试')
      }
    }

    // 关闭公告弹窗
    const closeAnnouncementModal = () => {
      showAnnouncementModal.value = false
      editingAnnouncement.value = null
    }

    const closeAnnouncementDetailModal = () => {
      showAnnouncementDetailModal.value = false
      selectedAnnouncement.value = null
    }

    // 分页
    const changeAnnouncementPage = (page) => {
      announcementPagination.current = page
      fetchAnnouncements()
    }

    // 获取公告类型文本
    const getAnnouncementTypeText = (type) => {
      const typeMap = {
        SYSTEM: '系统公告',
        ACTIVITY: '活动公告',
        MAINTENANCE: '维护公告',
        POLICY: '政策公告'
      }
      return typeMap[type] || type
    }

    // 获取公告状态文本
    const getAnnouncementStatusText = (status) => {
      const statusMap = {
        DRAFT: '草稿',
        PUBLISHED: '已发布',
        ARCHIVED: '已归档'
      }
      return statusMap[status] || status
    }

    // 格式化时间
    const formatTime = (time) => {
      return new Date(time).toLocaleString('zh-CN')
    }

    onMounted(() => {
      fetchCategories()
      fetchCategoryStats()
    })

    return {
      activeTab,
      loading,
      categories,
      editingCategory,
      showCategoryModal,
      categoryStats,
      categoryForm,
      announcements,
      editingAnnouncement,
      selectedAnnouncement,
      showAnnouncementModal,
      showAnnouncementDetailModal,
      announcementStats,
      announcementFilters,
      announcementPagination,
      announcementForm,
      switchTab,
      fetchCategories,
      refreshCategories,
      showAddCategoryModal,
      editCategory,
      saveCategory,
      toggleCategoryStatus,
      deleteCategory,
      closeCategoryModal,
      fetchAnnouncements,
      showAddAnnouncementModal,
      editAnnouncement,
      viewAnnouncement,
      saveDraft,
      saveAnnouncement,
      toggleAnnouncementStatus,
      deleteAnnouncement,
      closeAnnouncementModal,
      closeAnnouncementDetailModal,
      changeAnnouncementPage,
      getAnnouncementTypeText,
      getAnnouncementStatusText,
      formatTime
    }
  }
}
</script>

<style scoped>
.category-announcement {
  padding: 2rem;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.manage-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

/* 分类管理样式 */
.category-stats,
.announcement-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.total-icon {
  background: #007bff;
}

.active-icon {
  background: #28a745;
}

.product-icon {
  background: #17a2b8;
}

.view-icon {
  background: #6f42c1;
}

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.category-actions,
.announcement-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.add-btn,
.refresh-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  transition: all 0.3s;
}

.add-btn {
  background: #28a745;
  color: white;
}

.refresh-btn {
  background: #6c757d;
  color: white;
}

.filter-options {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.filter-options select,
.filter-options input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-btn {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.category-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.category-tree {
  padding: 1rem;
}

.category-item {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 1rem;
  overflow: hidden;
}

.category-item.disabled {
  opacity: 0.6;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
}

.category-details h4 {
  margin: 0 0 0.25rem 0;
  color: #333;
}

.category-details p {
  margin: 0 0 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.category-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.product-count,
.category-status {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: #666;
}

.category-status.enabled {
  color: #28a745;
}

.category-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.action-btn.edit {
  background: #007bff;
  color: white;
}

.action-btn.toggle {
  background: #ffc107;
  color: #333;
}

.action-btn.toggle.enabled {
  background: #6c757d;
  color: white;
}

.action-btn.delete {
  background: #dc3545;
  color: white;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.subcategories {
  padding: 1rem;
  background: white;
  border-top: 1px solid #eee;
}

.subcategory-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #007bff;
}

.subcategory-item.disabled {
  opacity: 0.6;
  border-left-color: #6c757d;
}

.subcategory-info h5 {
  margin: 0 0 0.25rem 0;
  color: #333;
  font-size: 0.9rem;
}

.subcategory-info p {
  margin: 0 0 0.25rem 0;
  color: #666;
  font-size: 0.8rem;
}

.subcategory-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.subcategory-actions {
  display: flex;
  gap: 0.25rem;
}

/* 公告管理样式 */
.announcement-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.announcements-grid {
  padding: 1rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1rem;
}

.announcement-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  background: white;
  transition: all 0.3s;
}

.announcement-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.announcement-card.draft {
  border-left: 4px solid #6c757d;
}

.announcement-card.published {
  border-left: 4px solid #28a745;
}

.announcement-card.archived {
  border-left: 4px solid #dc3545;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.announcement-type {
  display: flex;
  gap: 0.5rem;
}

.type-badge,
.status-badge,
.priority-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 500;
}

.type-badge.system {
  background: #d1ecf1;
  color: #0c5460;
}

.type-badge.activity {
  background: #d4edda;
  color: #155724;
}

.type-badge.maintenance {
  background: #fff3cd;
  color: #856404;
}

.type-badge.policy {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.draft {
  background: #e2e3e5;
  color: #383d41;
}

.status-badge.published {
  background: #d4edda;
  color: #155724;
}

.status-badge.archived {
  background: #f8d7da;
  color: #721c24;
}

.priority-badge {
  background: #fff3cd;
  color: #856404;
}

.card-actions {
  display: flex;
  gap: 0.25rem;
}

.action-btn.view {
  background: #17a2b8;
  color: white;
}

.card-content h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.announcement-summary {
  margin: 0 0 1rem 0;
  color: #666;
  line-height: 1.5;
  font-size: 0.9rem;
}

.announcement-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #666;
  font-size: 0.8rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.announcement-modal {
  max-width: 700px;
}

.announcement-detail-modal {
  max-width: 800px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
}

.modal-body {
  padding: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group textarea {
  resize: vertical;
}

.form-group small {
  color: #666;
  font-size: 0.8rem;
  margin-top: 0.25rem;
  display: block;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input {
  width: auto !important;
}

.icon-selector {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.icon-selector input {
  flex: 1;
}

.icon-preview {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  color: #007bff;
  font-size: 1.1rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem;
  border-top: 1px solid #eee;
}

.modal-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.modal-btn.save {
  background: #28a745;
  color: white;
}

.modal-btn.draft {
  background: #6c757d;
  color: white;
}

.modal-btn.cancel {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

/* 公告详情样式 */
.announcement-detail {
  max-width: none;
}

.detail-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #007bff;
}

.detail-header h2 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.detail-meta {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.detail-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  gap: 0.5rem;
}

.info-item label {
  font-weight: 500;
  color: #333;
  min-width: 80px;
}

.info-item span {
  color: #666;
}

.detail-content h4 {
  margin: 0 0 1rem 0;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
}

.content-text {
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .category-announcement {
    padding: 1rem;
  }
  
  .category-stats,
  .announcement-stats {
    grid-template-columns: 1fr;
  }
  
  .category-actions,
  .announcement-actions {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-options {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .announcements-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: none;
  }
  
  .detail-info {
    grid-template-columns: 1fr;
  }
}
</style>