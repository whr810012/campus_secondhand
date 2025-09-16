<template>
  <div class="rating-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <button @click="$router.go(-1)" class="back-btn">
        <i class="fas fa-arrow-left"></i>
      </button>
      <h1>评价订单</h1>
    </div>

    <div class="rating-content" v-if="order">
      <!-- 订单信息 -->
      <div class="order-info">
        <div class="product-info">
          <img :src="order.product.images[0]" :alt="order.product.title" class="product-image" />
          <div class="product-details">
            <h3>{{ order.product.title }}</h3>
            <p class="price">¥{{ order.totalAmount }}</p>
            <p class="order-no">订单号：{{ order.orderNo }}</p>
          </div>
        </div>
      </div>

      <!-- 评价表单 -->
      <div class="rating-form">
        <div class="form-section">
          <h3>评价对方</h3>
          <div class="user-info">
            <img :src="targetUser.avatar || '/default-avatar.png'" :alt="targetUser.nickname" class="user-avatar" />
            <div class="user-details">
              <h4>{{ targetUser.nickname }}</h4>
              <p>{{ targetUser.role === 'SELLER' ? '卖家' : '买家' }}</p>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h4>整体评分 <span class="required">*</span></h4>
          <div class="rating-stars">
            <div 
              v-for="star in 5" 
              :key="star" 
              class="star" 
              :class="{ active: star <= form.rating }"
              @click="setRating(star)"
            >
              <i class="fas fa-star"></i>
            </div>
            <span class="rating-text">{{ getRatingText(form.rating) }}</span>
          </div>
        </div>

        <div class="form-section">
          <h4>详细评价</h4>
          <div class="rating-tags">
            <div class="tag-group">
              <h5>服务态度</h5>
              <div class="tags">
                <span 
                  v-for="tag in serviceTags" 
                  :key="tag" 
                  class="tag" 
                  :class="{ active: form.tags.includes(tag) }"
                  @click="toggleTag(tag)"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
            
            <div class="tag-group">
              <h5>商品质量</h5>
              <div class="tags">
                <span 
                  v-for="tag in qualityTags" 
                  :key="tag" 
                  class="tag" 
                  :class="{ active: form.tags.includes(tag) }"
                  @click="toggleTag(tag)"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
            
            <div class="tag-group">
              <h5>交易体验</h5>
              <div class="tags">
                <span 
                  v-for="tag in experienceTags" 
                  :key="tag" 
                  class="tag" 
                  :class="{ active: form.tags.includes(tag) }"
                  @click="toggleTag(tag)"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h4>评价内容</h4>
          <textarea 
            v-model="form.content" 
            placeholder="分享你的交易体验，帮助其他用户做出更好的选择" 
            maxlength="200"
            rows="4"
          ></textarea>
          <div class="char-count">{{ form.content.length }}/200</div>
        </div>

        <div class="form-section">
          <h4>上传图片</h4>
          <div class="image-upload">
            <div class="image-list">
              <div 
                v-for="(image, index) in form.images" 
                :key="index" 
                class="image-item"
              >
                <img :src="image" alt="评价图片" />
                <button @click="removeImage(index)" class="remove-btn">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <div 
                v-if="form.images.length < 3" 
                class="upload-btn" 
                @click="triggerUpload"
              >
                <i class="fas fa-plus"></i>
                <span>添加图片</span>
                <small>{{ form.images.length }}/3</small>
              </div>
            </div>
            <input 
              ref="fileInput" 
              type="file" 
              multiple 
              accept="image/*" 
              @change="handleImageUpload" 
              style="display: none"
            />
          </div>
        </div>

        <!-- 匿名评价选项 -->
        <div class="form-section">
          <label class="checkbox-label">
            <input v-model="form.anonymous" type="checkbox" />
            <span>匿名评价</span>
          </label>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button @click="submitRating" class="submit-btn" :disabled="loading || !isFormValid">
            <i v-if="loading" class="fas fa-spinner fa-spin"></i>
            {{ loading ? '提交中...' : '提交评价' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading">
      <i class="fas fa-spinner fa-spin"></i>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api'

export default {
  name: 'Rating',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const fileInput = ref(null)
    const loading = ref(false)
    const order = ref(null)
    const targetUser = ref({})
    
    const form = reactive({
      rating: 5,
      tags: [],
      content: '',
      images: [],
      anonymous: false
    })

    // 评价标签
    const serviceTags = ['态度友好', '回复及时', '耐心解答', '专业可靠']
    const qualityTags = ['描述相符', '质量很好', '性价比高', '包装完好']
    const experienceTags = ['交易顺利', '发货快速', '诚信可靠', '推荐购买']

    // 表单验证
    const isFormValid = computed(() => {
      return form.rating > 0
    })

    // 获取评分文本
    const getRatingText = (rating) => {
      const texts = {
        1: '很差',
        2: '较差', 
        3: '一般',
        4: '满意',
        5: '非常满意'
      }
      return texts[rating] || ''
    }

    // 设置评分
    const setRating = (rating) => {
      form.rating = rating
    }

    // 切换标签
    const toggleTag = (tag) => {
      const index = form.tags.indexOf(tag)
      if (index > -1) {
        form.tags.splice(index, 1)
      } else {
        form.tags.push(tag)
      }
    }

    // 触发文件上传
    const triggerUpload = () => {
      fileInput.value.click()
    }

    // 处理图片上传
    const handleImageUpload = async (event) => {
      const files = Array.from(event.target.files)
      const remainingSlots = 3 - form.images.length
      const filesToUpload = files.slice(0, remainingSlots)

      for (const file of filesToUpload) {
        if (file.size > 5 * 1024 * 1024) {
          alert('图片大小不能超过5MB')
          continue
        }

        try {
          const formData = new FormData()
          formData.append('file', file)
          
          const response = await api.post('/upload/image', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          
          form.images.push(response.data.url)
        } catch (error) {
          console.error('图片上传失败:', error)
          alert('图片上传失败，请重试')
        }
      }
      
      // 清空文件输入
      event.target.value = ''
    }

    // 删除图片
    const removeImage = (index) => {
      form.images.splice(index, 1)
    }

    // 获取订单信息
    const fetchOrderInfo = async () => {
      try {
        const orderId = route.params.orderId
        const response = await api.get(`/orders/${orderId}`)
        order.value = response.data
        
        // 确定评价目标用户（买家评价卖家，卖家评价买家）
        const currentUserId = JSON.parse(localStorage.getItem('user'))?.id
        if (order.value.sellerId === currentUserId) {
          // 当前用户是卖家，评价买家
          targetUser.value = {
            ...order.value.buyer,
            role: 'BUYER'
          }
        } else {
          // 当前用户是买家，评价卖家
          targetUser.value = {
            ...order.value.seller,
            role: 'SELLER'
          }
        }
      } catch (error) {
        console.error('获取订单信息失败:', error)
        alert('获取订单信息失败')
        router.go(-1)
      }
    }

    // 提交评价
    const submitRating = async () => {
      if (!isFormValid.value) {
        alert('请完成评分')
        return
      }

      try {
        loading.value = true
        
        const ratingData = {
          orderId: route.params.orderId,
          targetUserId: targetUser.value.id,
          rating: form.rating,
          tags: form.tags.join(','),
          content: form.content,
          images: form.images.join(','),
          anonymous: form.anonymous
        }
        
        await api.post('/ratings', ratingData)
        
        alert('评价提交成功！')
        router.push('/user/orders')
      } catch (error) {
        console.error('提交评价失败:', error)
        alert('提交评价失败，请重试')
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchOrderInfo()
    })

    return {
      order,
      targetUser,
      form,
      serviceTags,
      qualityTags,
      experienceTags,
      loading,
      fileInput,
      isFormValid,
      getRatingText,
      setRating,
      toggleTag,
      triggerUpload,
      handleImageUpload,
      removeImage,
      submitRating
    }
  }
}
</script>

<style scoped>
.rating-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 1rem;
  background: white;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  margin-right: 1rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 500;
}

.rating-content {
  padding: 1rem;
  max-width: 600px;
  margin: 0 auto;
}

.order-info {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.product-info {
  display: flex;
  gap: 1rem;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.product-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
}

.price {
  color: #ff4757;
  font-weight: 600;
  font-size: 1.1rem;
  margin: 0.25rem 0;
}

.order-no {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.rating-form {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
}

.form-section {
  margin-bottom: 2rem;
}

.form-section:last-child {
  margin-bottom: 0;
}

.form-section h3,
.form-section h4 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  font-weight: 500;
  color: #333;
}

.required {
  color: #ff4757;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details h4 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  color: #333;
}

.user-details p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.star {
  font-size: 2rem;
  color: #ddd;
  cursor: pointer;
  transition: color 0.3s;
}

.star.active {
  color: #ffd700;
}

.rating-text {
  margin-left: 1rem;
  font-weight: 500;
  color: #333;
}

.rating-tags {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.tag-group h5 {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #666;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border: 1px solid #eee;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.tag.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.tag:hover {
  border-color: #007bff;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
}

textarea:focus {
  outline: none;
  border-color: #007bff;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #999;
  margin-top: 0.25rem;
}

.image-upload {
  margin-top: 0.5rem;
}

.image-list {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.image-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  background: rgba(255, 71, 87, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 0.7rem;
}

.upload-btn {
  width: 80px;
  height: 80px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.upload-btn:hover {
  border-color: #007bff;
  color: #007bff;
}

.upload-btn i {
  font-size: 1.2rem;
  margin-bottom: 0.25rem;
}

.upload-btn span {
  font-size: 0.8rem;
}

.upload-btn small {
  font-size: 0.7rem;
  color: #999;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
  margin: 0;
}

.form-actions {
  text-align: center;
  margin-top: 2rem;
}

.submit-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  min-width: 200px;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background: #0056b3;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.submit-btn i {
  margin-right: 0.5rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #666;
}

.loading i {
  font-size: 2rem;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .rating-content {
    padding: 0.5rem;
  }
  
  .rating-form {
    padding: 1rem;
  }
  
  .tags {
    gap: 0.25rem;
  }
  
  .tag {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }
}
</style>