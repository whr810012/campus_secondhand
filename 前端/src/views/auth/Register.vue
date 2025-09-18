<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <h2>注册账户</h2>
        <p>加入校园二手交易平台，开启便捷交易之旅</p>
      </div>
      
      <el-steps :active="currentStep" align-center class="register-steps">
        <el-step title="基本信息" />
        <el-step title="完善资料" />
      </el-steps>
      
      <!-- 第一步：基本信息 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-form
          ref="basicFormRef"
          :model="basicForm"
          :rules="basicRules"
          class="register-form"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="basicForm.phone"
              placeholder="请输入手机号"
              size="large"
              prefix-icon="Phone"
              maxlength="11"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="basicForm.password"
              type="password"
              placeholder="请输入密码（6-20位）"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="basicForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <div class="form-agreement">
            <el-checkbox v-model="basicForm.agreement" size="large">
              我已阅读并同意
              <el-link type="primary" @click="showAgreementDialog = true">
                《用户协议》
              </el-link>
              和
              <el-link type="primary" @click="showPrivacyDialog = true">
                《隐私政策》
              </el-link>
            </el-checkbox>
          </div>
          
          <el-button
            type="primary"
            size="large"
            class="step-button"
            @click="nextStep"
          >
            下一步
          </el-button>
        </el-form>
      </div>
      

      
      <!-- 第二步：完善资料 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="profile-header">
          <h3>完善个人资料</h3>
          <p>完善资料有助于其他用户了解您</p>
        </div>
        
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          class="register-form"
          label-width="80px"
        >

          
          <el-form-item label="昵称" prop="nickname">
            <el-input
              v-model="profileForm.nickname"
              placeholder="请输入昵称"
              size="large"
              maxlength="20"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="profileForm.gender" size="large">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
              <el-radio :label="0">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="学校" prop="school">
            <el-select
              v-model="profileForm.school"
              placeholder="请选择学校"
              size="large"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="school in schools"
                :key="school.id"
                :label="school.name"
                :value="school.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="专业" prop="major">
            <el-input
              v-model="profileForm.major"
              placeholder="请输入专业"
              size="large"
            />
          </el-form-item>
          

          
          <div class="step-buttons">
            <el-button size="large" @click="prevStep">
              上一步
            </el-button>
            <el-button
              type="primary"
              size="large"
              :loading="registerLoading"
              @click="completeRegister"
            >
              完成注册
            </el-button>
          </div>
        </el-form>
      </div>
      
      <div class="form-footer">
        <span>已有账户？</span>
        <el-link type="primary" @click="$router.push('/login')">
          立即登录
        </el-link>
      </div>
    </div>
    
    <!-- 用户协议对话框 -->
    <el-dialog
      v-model="showAgreementDialog"
      title="用户协议"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="agreement-content">
        <h4>1. 服务条款</h4>
        <p>欢迎使用校园二手交易平台。本协议是您与平台之间的法律协议。</p>
        
        <h4>2. 用户责任</h4>
        <p>用户应确保发布的商品信息真实有效，不得发布违法违规内容。</p>
        
        <h4>3. 交易规则</h4>
        <p>平台仅提供信息展示和交易撮合服务，交易双方应自行承担交易风险。</p>
        
        <h4>4. 隐私保护</h4>
        <p>平台将严格保护用户隐私信息，不会向第三方泄露用户个人信息。</p>
        
        <h4>5. 免责声明</h4>
        <p>平台对用户间的交易纠纷不承担责任，但会积极协助解决。</p>
      </div>
      
      <template #footer>
        <el-button @click="showAgreementDialog = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 隐私政策对话框 -->
    <el-dialog
      v-model="showPrivacyDialog"
      title="隐私政策"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="privacy-content">
        <h4>1. 信息收集</h4>
        <p>我们会收集您的基本信息、联系方式等必要信息以提供服务。</p>
        
        <h4>2. 信息使用</h4>
        <p>收集的信息仅用于提供服务、改善用户体验和保障交易安全。</p>
        
        <h4>3. 信息保护</h4>
        <p>我们采用行业标准的安全措施保护您的个人信息安全。</p>
        
        <h4>4. 信息共享</h4>
        <p>除法律要求外，我们不会向第三方分享您的个人信息。</p>
        
        <h4>5. 权利保障</h4>
        <p>您有权查看、修改或删除您的个人信息。</p>
      </div>
      
      <template #footer>
        <el-button @click="showPrivacyDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { register, getSchools } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const basicFormRef = ref()
const profileFormRef = ref()

const currentStep = ref(0)
const registerLoading = ref(false)
const showAgreementDialog = ref(false)
const showPrivacyDialog = ref(false)
const schools = ref([])

// 基本信息表单
const basicForm = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 个人资料表单
const profileForm = reactive({
  nickname: '',
  gender: 0,
  school: '',
  major: ''
})

// 表单验证规则
const basicRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== basicForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  school: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ]
}



// 获取学校列表
const fetchSchools = async () => {
  try {
    const response = await getSchools()
    schools.value = response.data || []
  } catch (error) {
    console.error('获取学校列表失败:', error)
  }
}

// 下一步
const nextStep = async () => {
  if (currentStep.value === 0) {
    if (!basicFormRef.value) return
    
    try {
      await basicFormRef.value.validate()
      
      if (!basicForm.agreement) {
        ElMessage.warning('请先同意用户协议和隐私政策')
        return
      }
      
      // 直接跳到完善资料步骤，跳过手机验证
      currentStep.value = 1
      
    } catch (error) {
      console.error('表单验证失败:', error)
    }
  }
}

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}





// 完成注册
const completeRegister = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    registerLoading.value = true
    
    const registerData = {
      ...basicForm,
      ...profileForm
    }
    
    delete registerData.confirmPassword
    delete registerData.agreement
    
    await register(registerData)
    
    ElMessage.success('注册成功！')
    
    // 自动登录
    await userStore.login({
      phone: basicForm.phone,
      password: basicForm.password
    })
    
    // 跳转到首页
    router.push('/')
    
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error(error.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}

// 组件挂载时获取学校列表
onMounted(() => {
  fetchSchools()
})
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  
  .register-container {
    width: 100%;
    max-width: 500px;
    padding: 40px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    
    .register-header {
      text-align: center;
      margin-bottom: 32px;
      
      h2 {
        font-size: 28px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      p {
        font-size: 16px;
        color: var(--text-secondary);
        margin: 0;
      }
    }
    
    .register-steps {
      margin-bottom: 32px;
    }
    
    .step-content {
      .verify-info {
        text-align: center;
        margin-bottom: 32px;
        
        .verify-icon {
          font-size: 48px;
          color: var(--primary-color);
          margin-bottom: 16px;
        }
        
        h3 {
          font-size: 20px;
          font-weight: 600;
          margin-bottom: 8px;
        }
        
        p {
          color: var(--text-secondary);
          margin: 0;
        }
      }
      
      .profile-header {
        text-align: center;
        margin-bottom: 24px;
        
        h3 {
          font-size: 20px;
          font-weight: 600;
          margin-bottom: 8px;
        }
        
        p {
          color: var(--text-secondary);
          margin: 0;
        }
      }
      
      .register-form {
        .el-form-item {
          margin-bottom: 20px;
        }
        
        .form-agreement {
          margin-bottom: 24px;
          
          .el-checkbox {
            :deep(.el-checkbox__label) {
              font-size: 14px;
              color: var(--text-secondary);
              line-height: 1.5;
            }
          }
        }
        
        .code-input {
          display: flex;
          gap: 12px;
          
          .el-input {
            flex: 1;
          }
        }
        
        .avatar-upload {
          text-align: center;
          
          .el-upload {
            display: inline-block;
            
            .upload-tip {
              margin-top: 8px;
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
        
        .step-button {
          width: 100%;
          height: 48px;
          font-size: 16px;
          font-weight: 500;
        }
        
        .step-buttons {
          display: flex;
          gap: 12px;
          
          .el-button {
            flex: 1;
            height: 48px;
            font-size: 16px;
            font-weight: 500;
          }
        }
      }
    }
    
    .form-footer {
      text-align: center;
      margin-top: 24px;
      font-size: 14px;
      color: var(--text-secondary);
      
      .el-link {
        margin-left: 4px;
      }
    }
  }
}

// 协议和隐私政策内容样式
.agreement-content,
.privacy-content {
  max-height: 400px;
  overflow-y: auto;
  
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 20px 0 12px 0;
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  p {
    font-size: 14px;
    color: var(--text-secondary);
    line-height: 1.6;
    margin-bottom: 12px;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .register-page {
    padding: 10px;
    
    .register-container {
      padding: 24px;
      
      .register-header {
        margin-bottom: 24px;
        
        h2 {
          font-size: 24px;
        }
        
        p {
          font-size: 14px;
        }
      }
      
      .register-steps {
        margin-bottom: 24px;
      }
    }
  }
}

// 输入框和按钮样式优化
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  }
  
  &.is-focus {
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

:deep(.el-steps--horizontal) {
  .el-step__line {
    background: var(--border-color);
  }
  
  .el-step.is-process .el-step__icon {
    background: var(--primary-color);
    border-color: var(--primary-color);
  }
  
  .el-step.is-finish .el-step__icon {
    background: var(--success-color);
    border-color: var(--success-color);
  }
}
</style>