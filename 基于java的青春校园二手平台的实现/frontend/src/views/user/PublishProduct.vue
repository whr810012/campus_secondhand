<template>
  <div class="publish-container">
    <div class="publish-form-wrapper">
      <div class="form-header">
        <h2>发布商品</h2>
        <p>请如实填写商品信息，审核通过后将在商城展示</p>
      </div>
      
      <el-form
        ref="productForm"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="publish-form"
      >
        <!-- 商品图片 -->
        <el-form-item label="商品图片" prop="images" required>
          <div class="image-upload-section">
            <el-upload
              ref="imageUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :on-success="handleImageSuccess"
              :on-error="handleImageError"
              :on-remove="handleImageRemove"
              :before-upload="beforeImageUpload"
              list-type="picture-card"
              :limit="5"
              multiple
            >
              <i class="el-icon-plus"></i>
              <div slot="tip" class="el-upload__tip">
                支持jpg/png格式，单张不超过2MB，最多5张
              </div>
            </el-upload>
          </div>
        </el-form-item>
        
        <!-- 商品标题 -->
        <el-form-item label="商品标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入商品标题，简洁明了"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 商品分类 -->
        <el-form-item label="商品分类" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            placeholder="请选择商品分类"
            style="width: 100%"
          >
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
          <el-input
            v-model="form.price"
            placeholder="请输入价格"
            style="width: 200px"
          >
            <template slot="prepend">¥</template>
            <template slot="append">元</template>
          </el-input>
          <div class="price-tips">
            <span>建议定价合理，有助于快速成交</span>
          </div>
        </el-form-item>
        
        <!-- 商品描述 -->
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请详细描述商品的外观、功能、使用情况等信息，有助于买家了解商品"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 商品状况 -->
        <el-form-item label="商品状况" prop="condition">
          <el-radio-group v-model="form.condition">
            <el-radio label="NEW">全新</el-radio>
            <el-radio label="LIKE_NEW">几乎全新</el-radio>
            <el-radio label="GOOD">良好</el-radio>
            <el-radio label="FAIR">一般</el-radio>
            <el-radio label="POOR">较差</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 交易方式 -->
        <el-form-item label="交易方式" prop="tradeTypes">
          <el-checkbox-group v-model="form.tradeTypes">
            <el-checkbox label="ONLINE">支持线上交易</el-checkbox>
            <el-checkbox label="OFFLINE">支持线下交易</el-checkbox>
          </el-checkbox-group>
          <div class="trade-tips">
            <p><i class="el-icon-info"></i> 线上交易：通过平台担保，安全可靠</p>
            <p><i class="el-icon-info"></i> 线下交易：面对面交易，当面验货</p>
          </div>
        </el-form-item>
        
        <!-- 联系方式 -->
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="form.contactInfo"
            placeholder="请输入联系方式（手机号、微信等）"
            maxlength="50"
          />
          <div class="contact-tips">
            <span>联系方式仅对有意向的买家可见</span>
          </div>
        </el-form-item>
        
        <!-- 商品标签 -->
        <el-form-item label="商品标签">
          <div class="tags-input">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              style="margin-right: 10px; margin-bottom: 10px;"
            >
              {{ tag }}
            </el-tag>
            
            <el-input
              v-if="tagInputVisible"
              ref="tagInput"
              v-model="tagInputValue"
              size="small"
              style="width: 100px;"
              @keyup.enter.native="addTag"
              @blur="addTag"
            />
            
            <el-button
              v-else
              size="small"
              @click="showTagInput"
              icon="el-icon-plus"
            >
              添加标签
            </el-button>
          </div>
          <div class="tags-tips">
            <span>添加标签有助于买家找到您的商品，最多5个标签</span>
          </div>
        </el-form-item>
        
        <!-- 提交按钮 -->
        <el-form-item>
          <div class="submit-actions">
            <el-button
              type="primary"
              size="large"
              @click="submitForm"
              :loading="submitLoading"
            >
              发布商品
            </el-button>
            
            <el-button
              size="large"
              @click="saveDraft"
              :loading="draftLoading"
            >
              保存草稿
            </el-button>
            
            <el-button
              size="large"
              @click="resetForm"
            >
              重置
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 发布须知 -->
    <div class="publish-notice">
      <h3>发布须知</h3>
      <ul>
        <li>请确保商品信息真实有效，虚假信息将被拒绝</li>
        <li>商品图片清晰，能够真实反映商品状况</li>
        <li>价格合理，符合市场行情</li>
        <li>禁止发布违法违规商品</li>
        <li>商品发布后需要管理员审核，审核通过后才会在商城展示</li>
        <li>审核时间通常为1-3个工作日</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { publishProduct, uploadProductImages } from '@/api/product'
import { getEnabledCategories } from '@/api/category'
import { mapGetters } from 'vuex'

export default {
  name: 'PublishProduct',
  data() {
    return {
      form: {
        title: '',
        categoryId: null,
        price: '',
        description: '',
        condition: 'GOOD',
        tradeTypes: ['ONLINE'],
        contactInfo: '',
        tags: [],
        images: []
      },
      rules: {
        title: [
          { required: true, message: '请输入商品标题', trigger: 'blur' },
          { min: 5, max: 50, message: '标题长度在5到50个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择商品分类', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的价格格式', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' },
          { min: 10, max: 500, message: '描述长度在10到500个字符', trigger: 'blur' }
        ],
        condition: [
          { required: true, message: '请选择商品状况', trigger: 'change' }
        ],
        tradeTypes: [
          { type: 'array', required: true, message: '请选择交易方式', trigger: 'change' },
          { type: 'array', min: 1, message: '至少选择一种交易方式', trigger: 'change' }
        ],
        contactInfo: [
          { required: true, message: '请输入联系方式', trigger: 'blur' }
        ]
      },
      categories: [],
      fileList: [],
      submitLoading: false,
      draftLoading: false,
      tagInputVisible: false,
      tagInputValue: ''
    }
  },
  computed: {
    ...mapGetters(['token']),
    uploadUrl() {
      return process.env.VUE_APP_API_BASE_URL + '/api/product/upload-images'
    },
    uploadHeaders() {
      return {
        'Authorization': `Bearer ${this.token}`
      }
    }
  },
  created() {
    this.loadCategories()
    this.loadDraft()
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
    
    loadDraft() {
      const draft = localStorage.getItem('product_draft')
      if (draft) {
        try {
          const draftData = JSON.parse(draft)
          this.form = { ...this.form, ...draftData }
        } catch (error) {
          console.error('加载草稿失败:', error)
        }
      }
    },
    
    saveDraft() {
      this.draftLoading = true
      try {
        localStorage.setItem('product_draft', JSON.stringify(this.form))
        this.$message.success('草稿保存成功')
      } catch (error) {
        this.$message.error('草稿保存失败')
      } finally {
        this.draftLoading = false
      }
    },
    
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        this.$message.error('只能上传图片文件')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过2MB')
        return false
      }
      return true
    },
    
    handleImageSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.form.images.push(response.data.url)
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.message || '图片上传失败')
        this.fileList = fileList.filter(item => item.uid !== file.uid)
      }
    },
    
    handleImageError(error, file, fileList) {
      console.error('图片上传失败:', error)
      this.$message.error('图片上传失败，请重试')
      this.fileList = fileList.filter(item => item.uid !== file.uid)
    },
    
    handleImageRemove(file, fileList) {
      if (file.response && file.response.data) {
        const imageUrl = file.response.data.url
        this.form.images = this.form.images.filter(url => url !== imageUrl)
      }
    },
    
    showTagInput() {
      this.tagInputVisible = true
      this.$nextTick(() => {
        this.$refs.tagInput.$refs.input.focus()
      })
    },
    
    addTag() {
      const tag = this.tagInputValue.trim()
      if (tag && !this.form.tags.includes(tag) && this.form.tags.length < 5) {
        this.form.tags.push(tag)
      }
      this.tagInputVisible = false
      this.tagInputValue = ''
    },
    
    removeTag(tag) {
      this.form.tags = this.form.tags.filter(t => t !== tag)
    },
    
    submitForm() {
      this.$refs.productForm.validate(async (valid) => {
        if (!valid) {
          this.$message.error('请完善表单信息')
          return
        }
        
        if (this.form.images.length === 0) {
          this.$message.error('请至少上传一张商品图片')
          return
        }
        
        this.submitLoading = true
        try {
          const productData = {
            ...this.form,
            price: parseFloat(this.form.price),
            imageUrl: this.form.images[0], // 主图
            images: this.form.images // 所有图片
          }
          
          const res = await publishProduct(productData)
          if (res.data.code === 200) {
            this.$message.success('商品发布成功，等待审核')
            // 清除草稿
            localStorage.removeItem('product_draft')
            // 跳转到我的商品页面
            this.$router.push('/user/my-products')
          } else {
            this.$message.error(res.data.message || '发布失败')
          }
        } catch (error) {
          console.error('发布商品失败:', error)
          this.$message.error('发布失败，请重试')
        } finally {
          this.submitLoading = false
        }
      })
    },
    
    resetForm() {
      this.$refs.productForm.resetFields()
      this.form.images = []
      this.form.tags = []
      this.fileList = []
      localStorage.removeItem('product_draft')
    }
  }
}
</script>

<style scoped>
.publish-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.publish-form-wrapper {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-header {
  margin-bottom: 30px;
  text-align: center;
}

.form-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px 0;
}

.form-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.publish-form {
  max-width: 600px;
  margin: 0 auto;
}

/* 图片上传 */
.image-upload-section {
  width: 100%;
}

.el-upload--picture-card {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

.el-upload-list--picture-card .el-upload-list__item {
  width: 100px;
  height: 100px;
}

/* 价格输入 */
.price-tips {
  margin-top: 5px;
  font-size: 12px;
  color: #999;
}

/* 交易方式 */
.trade-tips {
  margin-top: 10px;
}

.trade-tips p {
  font-size: 12px;
  color: #666;
  margin: 5px 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.trade-tips i {
  color: #409EFF;
}

/* 联系方式 */
.contact-tips {
  margin-top: 5px;
  font-size: 12px;
  color: #999;
}

/* 标签输入 */
.tags-input {
  min-height: 40px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

.tags-tips {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

/* 提交按钮 */
.submit-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
}

/* 发布须知 */
.publish-notice {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.publish-notice h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
}

.publish-notice ul {
  margin: 0;
  padding-left: 20px;
}

.publish-notice li {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .publish-container {
    grid-template-columns: 1fr;
    padding: 10px;
  }
  
  .publish-form-wrapper,
  .publish-notice {
    padding: 20px;
  }
  
  .submit-actions {
    flex-direction: column;
  }
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 25px;
}

.el-form-item__label {
  font-weight: 500;
  color: #333;
}

.el-input__inner,
.el-textarea__inner {
  border-radius: 6px;
}

.el-button {
  border-radius: 6px;
}

/* 上传组件样式 */
.el-upload__tip {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}
</style>