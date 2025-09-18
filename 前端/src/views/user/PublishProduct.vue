<template>
  <div class="publish-product-page">
    <div class="container">
      <div class="page-header">
        <h2>发布商品</h2>
        <p>发布您的闲置物品，让它们找到新主人</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="publish-form"
      >
        <el-card class="form-section">
          <template #header>
            <h3>基本信息</h3>
          </template>
          
          <el-form-item label="商品标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入商品标题，简洁明了地描述您的商品"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="商品分类" prop="categoryId">
            <el-cascader
              v-model="form.categoryId"
              :options="categories"
              :props="cascaderProps"
              placeholder="请选择商品分类"
              clearable
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="商品价格" prop="price">
            <el-input
              v-model="form.price"
              type="number"
              placeholder="请输入商品价格"
              style="width: 200px"
            >
              <template #prepend>¥</template>
            </el-input>
            <span class="price-tip">建议定价合理，有助于快速成交</span>
          </el-form-item>
          
          <el-form-item label="原价" prop="originalPrice">
            <el-input
              v-model="form.originalPrice"
              type="number"
              placeholder="商品原价（选填）"
              style="width: 200px"
            >
              <template #prepend>¥</template>
            </el-input>
            <span class="price-tip">填写原价可以让买家了解商品的价值</span>
          </el-form-item>
          
          <el-form-item label="商品成色" prop="condition">
            <el-radio-group v-model="form.condition">
              <el-radio :label="1">全新</el-radio>
              <el-radio :label="2">几乎全新</el-radio>
              <el-radio :label="3">轻微使用痕迹</el-radio>
              <el-radio :label="4">明显使用痕迹</el-radio>
              <el-radio :label="5">重度使用痕迹</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-card>
        
        <el-card class="form-section">
          <template #header>
            <h3>商品图片</h3>
          </template>
          
          <el-form-item label="商品图片" prop="images">
            <div class="image-upload-container">
              <!-- 图片上传区域 -->
              <el-upload
                v-model:file-list="fileList"
                :action="uploadAction"
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-remove="handleRemove"
                :before-upload="beforeUpload"
                :on-preview="handlePreview"
                list-type="picture-card"
                :limit="9"
                multiple
                drag
                class="image-uploader"
              >
                <template #default>
                  <div class="upload-area">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">点击或拖拽上传</div>
                  </div>
                </template>
                <template #tip>
                  <div class="upload-tips">
                    <p><el-icon><InfoFilled /></el-icon> 最多上传9张图片，建议上传3-5张</p>
                    <p><el-icon><InfoFilled /></el-icon> 支持JPG、PNG、GIF、WebP格式，单张不超过5MB</p>
                    <p><el-icon><InfoFilled /></el-icon> 第一张图片将作为封面图片</p>
                    <p><el-icon><InfoFilled /></el-icon> 可拖拽图片调整顺序</p>
                  </div>
                </template>
              </el-upload>
              
              <!-- 图片预览对话框 -->
              <el-dialog
                v-model="previewVisible"
                title="图片预览"
                width="80%"
                center
                :before-close="handlePreviewClose"
              >
                <div class="preview-container">
                  <el-image
                    :src="previewImageUrl"
                    fit="contain"
                    style="width: 100%; max-height: 70vh;"
                    :preview-src-list="[previewImageUrl]"
                    :initial-index="0"
                    hide-on-click-modal
                  />
                </div>
              </el-dialog>
            </div>
          </el-form-item>
        </el-card>
        
        <el-card class="form-section">
          <template #header>
            <h3>详细描述</h3>
          </template>
          
          <el-form-item label="商品描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="请详细描述商品的特点、使用情况、购买时间等信息，有助于买家了解商品"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="商品标签" prop="tags">
            <div class="tags-input">
              <el-tag
                v-for="tag in form.tags"
                :key="tag"
                closable
                @close="removeTag(tag)"
              >
                {{ tag }}
              </el-tag>
              
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                v-model="tagInputValue"
                size="small"
                style="width: 100px"
                @keyup.enter="addTag"
                @blur="addTag"
              />
              
              <el-button
                v-else
                size="small"
                type="primary"
                :disabled="form.tags.length >= 5"
                @click="showTagInput"
              >
                + 添加标签
              </el-button>
            </div>
            <div class="tags-tips">
              最多添加5个标签，有助于买家搜索到您的商品
            </div>
          </el-form-item>
        </el-card>
        
        <el-card class="form-section">
          <template #header>
            <h3>交易设置</h3>
          </template>
          
          <el-form-item label="交易方式" prop="tradeType">
            <el-checkbox-group v-model="form.tradeType">
              <el-checkbox label="online">线上交易</el-checkbox>
              <el-checkbox label="offline">线下交易</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item v-if="form.tradeType.includes('offline')" label="交易地点" prop="location">
            <el-input
              v-model="form.location"
              placeholder="请输入交易地点，如：学校南门、图书馆等"
            />
          </el-form-item>
          
          <el-form-item label="联系方式" prop="contact">
            <el-input
              v-model="form.contact"
              placeholder="请输入联系方式，如：微信号、QQ号等"
            />
          </el-form-item>
        </el-card>
        
        <div class="form-actions">
          <el-button size="large" @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="submitForm">
            发布商品
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, InfoFilled } from '@element-plus/icons-vue'
import { getCategories, createProduct } from '@/api/product'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const tagInputRef = ref()
const submitting = ref(false)
const categories = ref([])
const fileList = ref([])
const tagInputVisible = ref(false)
const tagInputValue = ref('')

// 图片预览相关
const previewVisible = ref(false)
const previewImageUrl = ref('')

// 表单数据
const form = reactive({
  title: '',
  categoryId: [],
  price: '',
  originalPrice: '',
  condition: 1,
  images: [],
  description: '',
  tags: [],
  tradeType: ['online'],
  location: '',
  contact: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的价格格式', trigger: 'blur' }
  ],
  condition: [
    { required: true, message: '请选择商品成色', trigger: 'change' }
  ],
  images: [
    { required: true, message: '请至少上传一张商品图片', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ],
  tradeType: [
    { required: true, message: '请选择交易方式', trigger: 'change' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ]
}

// 级联选择器配置
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  emitPath: false
}

// 上传配置
const uploadAction = computed(() => {
  const userId = userStore.userInfo?.id
  return userId ? `/api/upload/image/database?userId=${userId}` : '/api/upload/image/database'
})
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = buildCategoryTree(response.data)
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 构建分类树
const buildCategoryTree = (categories) => {
  const tree = []
  const map = {}
  
  // 创建映射
  categories.forEach(category => {
    map[category.id] = { ...category, children: [] }
  })
  
  // 构建树结构
  categories.forEach(category => {
    if (category.parentId === 0) {
      tree.push(map[category.id])
    } else {
      if (map[category.parentId]) {
        map[category.parentId].children.push(map[category.id])
      }
    }
  })
  
  return tree
}

// 上传成功回调
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    form.images.push(response.data.base64)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 上传失败回调
const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败')
}

// 移除图片
const handleRemove = (file) => {
  const index = form.images.findIndex(base64 => base64 === file.response?.data?.base64)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 图片预览
const handlePreview = (file) => {
  if (file.response && file.response.data && file.response.data.url) {
    previewImageUrl.value = file.response.data.url
  } else if (file.url) {
    previewImageUrl.value = file.url
  }
  previewVisible.value = true
}

// 关闭预览
const handlePreviewClose = () => {
  previewVisible.value = false
  previewImageUrl.value = ''
}

// 显示标签输入框
const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

// 添加标签
const addTag = () => {
  const tag = tagInputValue.value.trim()
  if (tag && !form.tags.includes(tag) && form.tags.length < 5) {
    form.tags.push(tag)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

// 移除标签
const removeTag = (tag) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  }
}

// 保存草稿
const saveDraft = () => {
  localStorage.setItem('productDraft', JSON.stringify(form))
  ElMessage.success('草稿保存成功')
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (form.images.length === 0) {
      ElMessage.error('请至少上传一张商品图片')
      return
    }
    
    submitting.value = true
    
    // 处理交易方式：1-仅线下，2-仅线上，3-线上线下均可
    let tradeTypeValue = 3 // 默认线上线下均可
    if (form.tradeType.length === 1) {
      if (form.tradeType.includes('offline')) {
        tradeTypeValue = 1 // 仅线下
      } else if (form.tradeType.includes('online')) {
        tradeTypeValue = 2 // 仅线上
      }
    } else if (form.tradeType.length === 2) {
      tradeTypeValue = 3 // 线上线下均可
    }
    
    const submitData = {
      title: form.title,
      description: form.description,
      price: parseFloat(form.price),
      originalPrice: form.originalPrice ? parseFloat(form.originalPrice) : null,
      categoryId: Array.isArray(form.categoryId) ? form.categoryId[form.categoryId.length - 1] : form.categoryId,
      condition: form.condition,
      imageList: form.images, // 修改为imageList字段
      tagList: form.tags, // 修改为tagList字段
      tradeType: tradeTypeValue,
      tradeLocation: form.location
      // 注意：contact字段不包含在Product实体中，联系方式通过用户信息获取
    }
    
    await createProduct(submitData)
    
    ElMessage.success('商品发布成功，等待审核')
    
    // 清除草稿
    localStorage.removeItem('productDraft')
    
    // 跳转到我的发布页面
    router.push('/my-products')
    
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error(error.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

// 加载草稿
const loadDraft = () => {
  const draft = localStorage.getItem('productDraft')
  if (draft) {
    try {
      const draftData = JSON.parse(draft)
      Object.assign(form, draftData)
      ElMessage.info('已加载上次保存的草稿')
    } catch (error) {
      console.error('加载草稿失败:', error)
    }
  }
}

// 初始化
onMounted(async () => {
  await fetchCategories()
  loadDraft()
})
</script>

<style lang="scss" scoped>
.publish-product-page {
  .page-header {
    text-align: center;
    margin-bottom: 40px;
    
    h2 {
      font-size: 28px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }
    
    p {
      font-size: 16px;
      color: var(--text-secondary);
    }
  }
  
  .publish-form {
    max-width: 800px;
    margin: 0 auto;
    
    .form-section {
      margin-bottom: 24px;
      
      :deep(.el-card__header) {
        background: var(--bg-secondary);
        
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }
    }
    
    .price-tip {
      margin-left: 12px;
      font-size: 12px;
      color: var(--text-placeholder);
    }
    
    .image-upload-container {
      .upload-tips {
        margin-top: 16px;
        padding: 12px;
        background: var(--bg-secondary);
        border-radius: 8px;
        
        p {
          margin: 4px 0;
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }
    
    .tags-input {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      align-items: center;
      
      .el-tag {
        margin: 0;
      }
    }
    
    .tags-tips {
      margin-top: 8px;
      font-size: 12px;
      color: var(--text-placeholder);
    }
    
    .form-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
      margin-top: 40px;
      padding-top: 24px;
      border-top: 1px solid var(--border-color);
    }
  }
}

// 图片上传组件样式
.image-upload-container {
  .image-uploader {
    :deep(.el-upload--picture-card) {
      width: 140px;
      height: 140px;
      border-radius: 12px;
      border: 2px dashed var(--el-border-color);
      transition: all 0.3s ease;
      
      &:hover {
        border-color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);
      }
    }
    
    :deep(.el-upload-list--picture-card .el-upload-list__item) {
      width: 140px;
      height: 140px;
      border-radius: 12px;
      overflow: hidden;
      position: relative;
      
      &:hover .el-upload-list__item-actions {
        opacity: 1;
      }
    }
    
    :deep(.el-upload-list__item-actions) {
      opacity: 0;
      transition: opacity 0.3s ease;
      background: rgba(0, 0, 0, 0.6);
      
      .el-upload-list__item-preview,
      .el-upload-list__item-delete {
        color: white;
        font-size: 18px;
        
        &:hover {
          color: var(--el-color-primary);
        }
      }
    }
  }
  
  .upload-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    
    .upload-icon {
      font-size: 32px;
      color: var(--el-text-color-placeholder);
      margin-bottom: 8px;
    }
    
    .upload-text {
      font-size: 14px;
      color: var(--el-text-color-regular);
      text-align: center;
    }
  }
  
  .upload-tips {
    margin-top: 16px;
    padding: 12px;
    background: var(--el-color-info-light-9);
    border-radius: 8px;
    border-left: 4px solid var(--el-color-info);
    
    p {
      margin: 0;
      padding: 4px 0;
      font-size: 13px;
      color: var(--el-text-color-regular);
      display: flex;
      align-items: center;
      gap: 6px;
      
      .el-icon {
        color: var(--el-color-info);
        font-size: 14px;
      }
    }
  }
}

// 预览对话框样式
.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  
  .el-image {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .publish-product-page {
    .publish-form {
      margin: 0 16px;
      
      .form-actions {
        flex-direction: column;
        
        .el-button {
          width: 100%;
        }
      }
    }
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>