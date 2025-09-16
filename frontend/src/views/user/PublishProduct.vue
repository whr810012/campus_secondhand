<template>
  <div class="publish-product">
    <div class="publish-header">
      <h1>发布商品</h1>
      <p>发布你的闲置物品，让它们找到新主人</p>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="publish-form"
    >
      <!-- 商品图片 -->
      <el-form-item label="商品图片" prop="images" required>
        <div class="image-upload">
          <el-upload
            v-model="fileList"
            action="/api/upload/image"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeImageUpload"
            :limit="6"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">
            <p>最多上传6张图片，建议尺寸800x800像素</p>
            <p>支持JPG、PNG格式，单张图片不超过5MB</p>
          </div>
        </div>
      </el-form-item>

      <!-- 商品标题 -->
      <el-form-item label="商品标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入商品标题，简洁明了地描述你的商品"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <!-- 商品分类 -->
      <el-form-item label="商品分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择商品分类" style="width: 100%">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </el-form-item>

      <!-- 商品价格 -->
      <el-form-item label="商品价格" prop="price">
        <el-input-number
          v-model="form.price"
          :min="0.01"
          :max="99999.99"
          :precision="2"
          placeholder="请输入价格"
          style="width: 200px"
        />
        <span class="price-unit">元</span>
      </el-form-item>

      <!-- 新旧程度 -->
      <el-form-item label="新旧程度" prop="condition">
        <el-radio-group v-model="form.condition">
          <el-radio label="全新">全新</el-radio>
          <el-radio label="几乎全新">几乎全新</el-radio>
          <el-radio label="轻微使用痕迹">轻微使用痕迹</el-radio>
          <el-radio label="明显使用痕迹">明显使用痕迹</el-radio>
          <el-radio label="重度使用痕迹">重度使用痕迹</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 交易地点 -->
      <el-form-item label="交易地点" prop="location">
        <el-input
          v-model="form.location"
          placeholder="请输入交易地点，如：学校名称、宿舍楼等"
          maxlength="100"
        />
      </el-form-item>

      <!-- 联系方式 -->
      <el-form-item label="联系方式" prop="contact">
        <el-input
          v-model="form.contact"
          placeholder="请输入联系方式，如：微信号、QQ号等"
          maxlength="50"
        />
      </el-form-item>

      <!-- 商品描述 -->
      <el-form-item label="商品描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="6"
          placeholder="请详细描述商品的特点、使用情况、购买时间等信息，让买家更好地了解商品"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <!-- 发布选项 -->
      <el-form-item label="发布选项">
        <el-checkbox v-model="form.allowBargain">允许议价</el-checkbox>
        <el-checkbox v-model="form.freeShipping">包邮</el-checkbox>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <div class="submit-buttons">
          <el-button size="large" @click="handleCancel">取消</el-button>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitLoading">
            发布商品
          </el-button>
        </div>
      </el-form-item>
    </el-form>

    <!-- 发布须知 -->
    <div class="publish-notice">
      <h3>发布须知</h3>
      <ul>
        <li>请确保商品信息真实有效，虚假信息将被删除</li>
        <li>禁止发布违法违规商品，如假冒伪劣、管制物品等</li>
        <li>商品图片应清晰展示商品实际状态</li>
        <li>价格应合理，过高或过低的价格可能影响交易</li>
        <li>发布后24小时内可免费修改，之后修改需要重新审核</li>
        <li>长期无人问津的商品建议适当降价或重新发布</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()

// 表单数据
const form = reactive({
  title: '',
  categoryId: '',
  price: null,
  condition: '几乎全新',
  location: '',
  contact: '',
  description: '',
  images: [],
  allowBargain: true,
  freeShipping: false
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度应在5-50个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ],
  condition: [
    { required: true, message: '请选择新旧程度', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入交易地点', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 20, max: 1000, message: '描述长度应在20-1000个字符之间', trigger: 'blur' }
  ],
  images: [
    { 
      validator: (rule, value, callback) => {
        if (form.images.length === 0) {
          callback(new Error('请至少上传一张商品图片'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 其他数据
const categories = ref([])
const fileList = ref([])
const submitLoading = ref(false)

// 方法
const fetchCategories = async () => {
  try {
    const response = await api.get('/categories')
    // 后端返回格式: {code: 200, message: '操作成功', data: [...]}
    if (response.data && response.data.code === 200) {
      categories.value = response.data.data
    } else {
      throw new Error(response.data?.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.warning('获取分类列表失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const handleImageSuccess = (response, file) => {
  if (response.code === 200) {
    form.images.push(response.data.url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
    // 移除失败的文件
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
    }
  }
}

const handleImageRemove = (file) => {
  // 从form.images中移除对应的图片URL
  const index = fileList.value.findIndex(item => item.uid === file.uid)
  if (index > -1 && form.images[index]) {
    form.images.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 确认发布
    await ElMessageBox.confirm(
      '确认发布这个商品吗？发布后将进入审核流程。',
      '确认发布',
      {
        confirmButtonText: '确认发布',
        cancelButtonText: '再想想',
        type: 'info'
      }
    )
    
    submitLoading.value = true
    
    const productData = {
      ...form,
      sellerId: userStore.user.id
    }
    
    const response = await api.post('/products', productData)
    
    ElMessage.success('商品发布成功，正在审核中')
    
    // 跳转到商品详情页或个人中心
    router.push(`/user/products/${response.data.id}`)
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败，请重试')
      console.error('发布商品失败:', error)
    }
  } finally {
    submitLoading.value = false
  }
}

const handleCancel = async () => {
  // 检查是否有未保存的内容
  const hasContent = form.title || form.description || form.images.length > 0
  
  if (hasContent) {
    try {
      await ElMessageBox.confirm(
        '确定要取消发布吗？已填写的内容将丢失。',
        '确认取消',
        {
          confirmButtonText: '确定取消',
          cancelButtonText: '继续编辑',
          type: 'warning'
        }
      )
      router.back()
    } catch {
      // 用户选择继续编辑
    }
  } else {
    router.back()
  }
}

// 生命周期
onMounted(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  fetchCategories()
})
</script>

<style scoped>
.publish-product {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.publish-header {
  text-align: center;
  margin-bottom: 40px;
}

.publish-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.publish-header p {
  color: #666;
  font-size: 16px;
}

.publish-form {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.image-upload {
  width: 100%;
}

.upload-tip {
  margin-top: 12px;
  color: #999;
  font-size: 12px;
  line-height: 1.5;
}

.price-unit {
  margin-left: 8px;
  color: #666;
}

.submit-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
}

.submit-buttons .el-button {
  min-width: 120px;
}

.publish-notice {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.publish-notice h3 {
  color: #333;
  margin-bottom: 16px;
  font-size: 16px;
}

.publish-notice ul {
  margin: 0;
  padding-left: 20px;
}

.publish-notice li {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}

@media (max-width: 768px) {
  .publish-product {
    padding: 16px;
  }
  
  .publish-form {
    padding: 20px;
  }
  
  .submit-buttons {
    flex-direction: column;
  }
  
  .submit-buttons .el-button {
    width: 100%;
  }
}
</style>