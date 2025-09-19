<template>
  <div class="verification-page">
    <div class="container">
      <el-card class="verification-card">
        <div class="verification-header">
          <h2>学生身份认证</h2>
          <p>完成学生身份认证，享受更多平台特权</p>
        </div>

        <div v-if="!isVerified" class="verification-form">
          <el-form
            ref="verificationFormRef"
            :model="verificationForm"
            :rules="verificationRules"
            label-width="120px"
          >
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="verificationForm.realName"
                placeholder="请输入真实姓名"
                maxlength="20"
              />
            </el-form-item>

            <el-form-item label="学号" prop="studentId">
              <el-input
                v-model="verificationForm.studentId"
                placeholder="请输入学号"
                maxlength="20"
              />
            </el-form-item>

            <el-form-item label="学校" prop="school">
              <el-select
                v-model="verificationForm.school"
                placeholder="请选择学校"
                filterable
                style="width: 100%"
                @change="handleSchoolChange"
              >
                <el-option
                  v-for="school in schools"
                  :key="school.id"
                  :label="school.name"
                  :value="school.name"
                  :data-id="school.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="专业" prop="major">
              <el-input
                v-model="verificationForm.major"
                placeholder="请输入专业"
                maxlength="50"
              />
            </el-form-item>

            <el-form-item label="学生证照片" prop="studentCardImage">
              <div class="image-upload-container">
                <el-upload
                  class="image-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :on-success="handleStudentCardSuccess"
                  :on-error="handleUploadError"
                  :on-preview="handleStudentCardPreview"
                  :before-upload="beforeUpload"
                  :show-file-list="false"
                  accept="image/*"
                  drag
                >
                  <div v-if="verificationForm.studentCardImage" class="uploaded-image">
                    <el-image
                      :src="verificationForm.studentCardImage"
                      fit="cover"
                      style="width: 100%; height: 100%"
                      :preview-src-list="[verificationForm.studentCardImage]"
                    />
                    <div class="image-overlay">
                      <el-button size="small" type="primary" :icon="Upload">重新上传</el-button>
                      <el-button size="small" :icon="View" @click.stop="handleStudentCardPreview">预览</el-button>
                    </div>
                  </div>
                  <div v-else class="upload-area">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">点击或拖拽上传学生证照片</div>
                  </div>
                  <template #tip>
                    <div class="upload-tip">
                      <el-icon><InfoFilled /></el-icon>
                      支持 JPG、PNG、GIF、WebP 格式，大小不超过 5MB
                    </div>
                  </template>
                </el-upload>
              </div>
            </el-form-item>

            <el-form-item label="身份证照片" prop="idCardImage">
              <div class="image-upload-container">
                <el-upload
                  class="image-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :on-success="handleIdCardSuccess"
                  :on-error="handleUploadError"
                  :on-preview="handleIdCardPreview"
                  :before-upload="beforeUpload"
                  :show-file-list="false"
                  accept="image/*"
                  drag
                >
                  <div v-if="verificationForm.idCardImage" class="uploaded-image">
                    <el-image
                      :src="verificationForm.idCardImage"
                      fit="cover"
                      style="width: 100%; height: 100%"
                      :preview-src-list="[verificationForm.idCardImage]"
                    />
                    <div class="image-overlay">
                      <el-button size="small" type="primary" :icon="Upload">重新上传</el-button>
                      <el-button size="small" :icon="View" @click.stop="handleIdCardPreview">预览</el-button>
                    </div>
                  </div>
                  <div v-else class="upload-area">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <div class="upload-text">点击或拖拽上传身份证照片</div>
                  </div>
                  <template #tip>
                    <div class="upload-tip">
                      <el-icon><InfoFilled /></el-icon>
                      支持 JPG、PNG、GIF、WebP 格式，大小不超过 5MB
                    </div>
                  </template>
                </el-upload>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="submitting"
                @click="submitVerification"
              >
                提交认证申请
              </el-button>
              <el-button size="large" @click="$router.go(-1)">
                返回
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-else class="verification-status">
          <div class="status-icon">
            <el-icon v-if="verificationStatus === 'approved'" class="success-icon" size="60">
              <CircleCheck />
            </el-icon>
            <el-icon v-else-if="verificationStatus === 'pending'" class="warning-icon" size="60">
              <Clock />
            </el-icon>
            <el-icon v-else class="error-icon" size="60">
              <CircleClose />
            </el-icon>
          </div>
          
          <div class="status-content">
            <h3 v-if="verificationStatus === 'approved'">认证已通过</h3>
            <h3 v-else-if="verificationStatus === 'pending'">认证审核中</h3>
            <h3 v-else>认证未通过</h3>
            
            <p v-if="verificationStatus === 'approved'">
              恭喜您已通过学生身份认证，现在可以享受平台的所有功能！
            </p>
            <p v-else-if="verificationStatus === 'pending'">
              您的认证申请正在审核中，请耐心等待，我们会在1-3个工作日内完成审核。
            </p>
            <p v-else>
              很抱歉，您的认证申请未通过审核。{{ rejectionReason }}
            </p>
            
            <div class="status-actions">
              <el-button v-if="verificationStatus === 'rejected'" type="primary" @click="resubmit">
                重新提交
              </el-button>
              <el-button @click="$router.push('/profile')">
                返回个人中心
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CircleCheck, Clock, CircleClose, InfoFilled, Upload, View } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getSchools } from '@/api/school'
import { verifyStudent, getVerifyStatus } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const verificationFormRef = ref()
const submitting = ref(false)
const isVerified = ref(false)
const verificationStatus = ref('') // 'pending', 'approved', 'rejected'
const rejectionReason = ref('')
const schools = ref([])

// 上传配置
const uploadUrl = computed(() => {
  const userId = userStore.userInfo?.id
  return userId ? `/api/upload/image/database?userId=${userId}` : '/api/upload/image/database'
})
const uploadHeaders = ref({
  'Authorization': `Bearer ${userStore.token}`
})

// 表单数据
const verificationForm = reactive({
  realName: '',
  studentId: '',
  school: '',
  schoolId: null,
  major: '',
  studentCardImage: '',
  idCardImage: ''
})

// 表单验证规则
const verificationRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 6, max: 20, message: '学号长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  school: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' },
    { max: 50, message: '专业名称不能超过 50 个字符', trigger: 'blur' }
  ],
  studentCardImage: [
    { required: true, message: '请上传学生证照片', trigger: 'change' }
  ],
  idCardImage: [
    { required: true, message: '请上传身份证照片', trigger: 'change' }
  ]
}

// 方法
const fetchSchools = async () => {
  try {
    const response = await getSchools()
    schools.value = response.data || []
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

const checkVerificationStatus = async () => {
  try {
    // 调用API检查认证状态
    const response = await getVerifyStatus()
    
    if (response.data) {
      const { status, realName, studentId, schoolName, major, rejectionReason } = response.data
      
      // 根据数据库字段含义处理状态
      // 0-未认证，1-认证中，2-已认证，3-认证失败
      let statusText = ''
      let verified = false
      
      if (status === 0) {
        statusText = ''
        verified = false
      } else if (status === 1) {
        statusText = 'pending'
        verified = true // 认证中时不显示表单
      } else if (status === 2) {
        statusText = 'approved'
        verified = true
      } else if (status === 3) {
        statusText = 'rejected'
        verified = false // 认证失败时允许重新申请
      }
      
      // 更新认证状态
      verificationStatus.value = statusText
      isVerified.value = verified
      rejectionReason.value = rejectionReason || ''
      
      // 如果有认证信息，填充表单
      if (realName) verificationForm.realName = realName
      if (studentId) verificationForm.studentId = studentId
      if (schoolName) verificationForm.school = schoolName
      if (major) verificationForm.major = major
    } else {
      // 未认证状态
      isVerified.value = false
      verificationStatus.value = ''
    }
  } catch (error) {
    console.error('获取认证状态失败:', error)
    // 发生错误时设置为未认证状态
    isVerified.value = false
    verificationStatus.value = ''
  }
}

// 处理学校选择变化
const handleSchoolChange = (schoolName) => {
  const selectedSchool = schools.value.find(school => school.name === schoolName)
  if (selectedSchool) {
    verificationForm.schoolId = selectedSchool.id
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

// 学生证照片预览
const handleStudentCardPreview = () => {
  if (verificationForm.studentCardImage) {
    // Element Plus 的 el-image 组件会自动处理预览
    console.log('预览学生证照片')
  }
}

// 身份证照片预览
const handleIdCardPreview = () => {
  if (verificationForm.idCardImage) {
    // Element Plus 的 el-image 组件会自动处理预览
    console.log('预览身份证照片')
  }
}

const handleStudentCardSuccess = (response) => {
  verificationForm.studentCardImage = response.data.base64
  ElMessage.success('学生证照片上传成功')
}

const handleIdCardSuccess = (response) => {
  verificationForm.idCardImage = response.data.base64
  ElMessage.success('身份证照片上传成功')
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败，请重试')
}

const submitVerification = async () => {
  try {
    await verificationFormRef.value.validate()
    
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      realName: verificationForm.realName,
      idCard: verificationForm.idCard,
      studentId: verificationForm.studentId,
      schoolId: verificationForm.schoolId,
      verifyImages: [verificationForm.studentCardImage, verificationForm.idCardImage].filter(img => img)
    }
    
    // 调用API提交认证申请
    const response = await verifyStudent(submitData)
    console.log('response', response)
    
      ElMessage.success('认证申请提交成功，请等待审核')
      // 重新获取认证状态
      await checkVerificationStatus()
  } catch (error) {
    console.error('提交认证申请失败:', error)
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const resubmit = () => {
  isVerified.value = false
  verificationStatus.value = ''
  rejectionReason.value = ''
  
  // 清空表单
  Object.assign(verificationForm, {
    realName: '',
    studentId: '',
    school: '',
    schoolId: null,
    major: '',
    studentCardImage: '',
    idCardImage: ''
  })
  
  ElMessage.info('请重新填写认证信息并上传图片')
}

// 生命周期
onMounted(() => {
  fetchSchools()
  checkVerificationStatus()
})
</script>

<style scoped>
.verification-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.verification-card {
  margin-bottom: 20px;
}

.verification-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.verification-header h2 {
  color: #303133;
  margin-bottom: 10px;
}

.verification-header p {
  color: #909399;
  font-size: 14px;
}

.verification-form {
  max-width: 600px;
  margin: 0 auto;
}

/* 图片上传组件样式 */
.image-upload-container {
  .image-uploader {
    :deep(.el-upload) {
      width: 240px;
      height: 160px;
      border-radius: 12px;
      border: 2px dashed var(--el-border-color);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      &:hover {
        border-color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);
      }
    }
    
    .uploaded-image {
      width: 100%;
      height: 100%;
      position: relative;
      
      &:hover .image-overlay {
        opacity: 1;
      }
      
      .image-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.6);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        .el-button {
          color: white;
          border-color: white;
          
          &:hover {
            background-color: var(--el-color-primary);
            border-color: var(--el-color-primary);
          }
        }
      }
    }
    
    .upload-area {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      
      .upload-icon {
        font-size: 36px;
        color: var(--el-text-color-placeholder);
        margin-bottom: 12px;
      }
      
      .upload-text {
        font-size: 16px;
        color: var(--el-text-color-regular);
        margin-bottom: 8px;
        font-weight: 500;
      }
      
      .upload-tip {
        font-size: 13px;
        color: var(--el-text-color-placeholder);
        display: flex;
        align-items: center;
        gap: 4px;
        
        .el-icon {
          color: var(--el-color-info);
        }
      }
    }
  }
}

.verification-status {
  text-align: center;
  padding: 40px 20px;
}

.status-icon {
  margin-bottom: 20px;
}

.success-icon {
  color: #67c23a;
}

.warning-icon {
  color: #e6a23c;
}

.error-icon {
  color: #f56c6c;
}

.status-content h3 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 20px;
}

.status-content p {
  color: #606266;
  margin-bottom: 25px;
  line-height: 1.6;
}

.status-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .verification-form {
    max-width: 100%;
  }
  
  .upload-placeholder {
    width: 100%;
    max-width: 200px;
  }
  
  .status-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .status-actions .el-button {
    width: 200px;
  }
}
</style>