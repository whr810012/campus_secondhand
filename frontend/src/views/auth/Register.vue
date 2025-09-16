<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-form">
        <div class="form-header">
          <h2 class="form-title">注册</h2>
          <p class="form-subtitle">加入校园二手交易平台</p>
        </div>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Phone"
            />
          </el-form-item>
          
          <el-form-item prop="code">
            <div class="code-input">
              <el-input
                v-model="registerForm.code"
                placeholder="请输入验证码"
                size="large"
              />
              <el-button 
                :disabled="codeCountdown > 0" 
                @click="sendCode"
                size="large"
                type="primary"
              >
                {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="请输入昵称"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="studentId">
            <el-input
              v-model="registerForm.studentId"
              placeholder="请输入学号"
              size="large"
              :prefix-icon="CreditCard"
            />
          </el-form-item>
          
          <el-form-item prop="school">
            <el-select
              v-model="registerForm.school"
              placeholder="请选择学校"
              size="large"
              style="width: 100%"
              filterable
            >
              <el-option
                v-for="school in schools"
                :key="school.id"
                :label="school.name"
                :value="school.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="agreement">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" @click="showAgreement">《用户协议》</el-link>
              和
              <el-link type="primary" @click="showPrivacy">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleRegister"
              class="register-btn"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="form-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </div>
      
      <div class="register-banner">
        <div class="banner-content">
          <h3>开始你的校园交易之旅</h3>
          <p>安全、便捷、可信赖的二手交易平台</p>
          <div class="steps">
            <div class="step">
              <div class="step-icon">1</div>
              <div class="step-text">注册账号</div>
            </div>
            <div class="step">
              <div class="step-icon">2</div>
              <div class="step-text">学生认证</div>
            </div>
            <div class="step">
              <div class="step-icon">3</div>
              <div class="step-text">开始交易</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 用户协议对话框 -->
    <el-dialog
      v-model="agreementVisible"
      title="用户协议"
      width="600px"
      :show-close="true"
    >
      <div class="agreement-content">
        <h4>1. 服务条款</h4>
        <p>欢迎使用校园二手交易平台。在使用本平台前，请仔细阅读本用户协议。</p>
        
        <h4>2. 用户责任</h4>
        <p>用户应保证所提供的信息真实、准确、完整，并承担因信息不实而产生的一切责任。</p>
        
        <h4>3. 交易规则</h4>
        <p>用户在平台上进行的所有交易行为应遵守国家法律法规，不得从事违法违规活动。</p>
        
        <h4>4. 平台责任</h4>
        <p>平台仅提供信息发布和交流服务，不参与具体交易过程，不承担交易风险。</p>
        
        <h4>5. 协议修改</h4>
        <p>平台有权根据需要修改本协议，修改后的协议将在平台上公布。</p>
      </div>
    </el-dialog>
    
    <!-- 隐私政策对话框 -->
    <el-dialog
      v-model="privacyVisible"
      title="隐私政策"
      width="600px"
      :show-close="true"
    >
      <div class="privacy-content">
        <h4>1. 信息收集</h4>
        <p>我们会收集您在注册和使用过程中提供的必要信息，包括但不限于手机号、学号等。</p>
        
        <h4>2. 信息使用</h4>
        <p>我们仅将您的信息用于提供服务、身份验证、安全保障等必要用途。</p>
        
        <h4>3. 信息保护</h4>
        <p>我们采用行业标准的安全措施保护您的个人信息，防止信息泄露、滥用。</p>
        
        <h4>4. 信息共享</h4>
        <p>除法律法规要求外，我们不会向第三方分享您的个人信息。</p>
        
        <h4>5. 权利保障</h4>
        <p>您有权查询、修改、删除您的个人信息，如有需要请联系客服。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, Lock, User, CreditCard } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const registerForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  studentId: '',
  school: '',
  agreement: false
})

// 自定义验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  school: [
    { required: true, message: '请选择学校', trigger: 'change' }
  ],
  agreement: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议和隐私政策'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// 响应式数据
const loading = ref(false)
const codeCountdown = ref(0)
const registerFormRef = ref()
const schools = ref([])
const agreementVisible = ref(false)
const privacyVisible = ref(false)

// 获取学校列表
const fetchSchools = async () => {
  try {
    const response = await api.get('/schools')
    schools.value = response.data
  } catch (error) {
    console.error('获取学校列表失败:', error)
    // 提供默认学校列表
    schools.value = [
      { id: 1, name: '北京大学' },
      { id: 2, name: '清华大学' },
      { id: 3, name: '复旦大学' },
      { id: 4, name: '上海交通大学' },
      { id: 5, name: '浙江大学' },
      { id: 6, name: '南京大学' },
      { id: 7, name: '中山大学' },
      { id: 8, name: '华中科技大学' },
      { id: 9, name: '西安交通大学' },
      { id: 10, name: '哈尔滨工业大学' }
    ]
  }
}

// 发送验证码
const sendCode = async () => {
  if (!registerForm.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  try {
    await api.post('/auth/send-register-code', {
      phone: registerForm.phone
    })
    
    ElMessage.success('验证码已发送')
    
    // 开始倒计时
    codeCountdown.value = 60
    const timer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发送验证码失败')
  }
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    const result = await userStore.register({
      phone: registerForm.phone,
      code: registerForm.code,
      password: registerForm.password,
      nickname: registerForm.nickname,
      studentId: registerForm.studentId,
      schoolId: registerForm.school
    })
    
    if (result.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 显示用户协议
const showAgreement = () => {
  agreementVisible.value = true
}

// 显示隐私政策
const showPrivacy = () => {
  privacyVisible.value = true
}

// 初始化
onMounted(() => {
  fetchSchools()
})
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  display: flex;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 1000px;
  width: 100%;
  min-height: 600px;
}

.register-form {
  flex: 1.2;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-height: 80vh;
  overflow-y: auto;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.code-input {
  display: flex;
  gap: 10px;
}

.code-input .el-input {
  flex: 1;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: var(--text-secondary);
}

.register-banner {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color) 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  padding: 50px;
}

.banner-content {
  text-align: center;
}

.banner-content h3 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 16px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.step {
  display: flex;
  align-items: center;
  gap: 15px;
}

.step-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.step-text {
  font-size: 16px;
  opacity: 0.9;
}

.agreement-content,
.privacy-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}

.agreement-content h4,
.privacy-content h4 {
  color: var(--text-primary);
  margin: 20px 0 10px 0;
  font-size: 16px;
}

.agreement-content h4:first-child,
.privacy-content h4:first-child {
  margin-top: 0;
}

.agreement-content p,
.privacy-content p {
  color: var(--text-regular);
  line-height: 1.6;
  margin-bottom: 15px;
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    max-width: 400px;
  }
  
  .register-banner {
    order: -1;
    padding: 30px;
  }
  
  .banner-content h3 {
    font-size: 24px;
  }
  
  .steps {
    flex-direction: row;
    justify-content: space-around;
  }
  
  .step {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .step-icon {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .step-text {
    font-size: 12px;
  }
  
  .register-form {
    padding: 30px 25px;
  }
  
  .form-title {
    font-size: 24px;
  }
}
</style>