<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-form">
        <div class="form-header">
          <h2 class="form-title">登录</h2>
          <p class="form-subtitle">欢迎回到校园二手交易平台</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Phone"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <el-link type="primary" @click="showForgotPassword">忘记密码？</el-link>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-btn"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="form-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </div>
      
      <div class="login-banner">
        <div class="banner-content">
          <h3>校园二手交易</h3>
          <p>安全、便捷的校园二手商品交易平台</p>
          <ul class="feature-list">
            <li>✓ 学生身份认证</li>
            <li>✓ 安全交易保障</li>
            <li>✓ 便捷沟通工具</li>
            <li>✓ 信誉评价体系</li>
          </ul>
        </div>
      </div>
    </div>
    
    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="forgotPasswordVisible"
      title="找回密码"
      width="400px"
    >
      <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordFormRef">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="forgotPasswordForm.phone" placeholder="请输入注册手机号" />
        </el-form-item>
        
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input v-model="forgotPasswordForm.code" placeholder="请输入验证码" />
            <el-button 
              :disabled="codeCountdown > 0" 
              @click="sendCode"
              type="primary"
            >
              {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="forgotPasswordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="forgotPasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" :loading="resetLoading">
          重置密码
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const loginForm = reactive({
  phone: '',
  password: '',
  remember: false
})

// 表单验证规则
const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 忘记密码相关
const forgotPasswordVisible = ref(false)
const forgotPasswordForm = reactive({
  phone: '',
  code: '',
  newPassword: ''
})

const forgotPasswordRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 响应式数据
const loading = ref(false)
const resetLoading = ref(false)
const codeCountdown = ref(0)
const loginFormRef = ref()
const forgotPasswordFormRef = ref()

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const result = await userStore.login({
      phone: loginForm.phone,
      password: loginForm.password
    })
    
    if (result.success) {
      ElMessage.success('登录成功')
      
      // 记住登录状态
      if (loginForm.remember) {
        localStorage.setItem('rememberLogin', 'true')
      }
      
      // 跳转到首页或之前的页面
      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 显示忘记密码对话框
const showForgotPassword = () => {
  forgotPasswordVisible.value = true
  // 如果登录表单中有手机号，自动填入
  if (loginForm.phone) {
    forgotPasswordForm.phone = loginForm.phone
  }
}

// 发送验证码
const sendCode = async () => {
  if (!forgotPasswordForm.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(forgotPasswordForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  try {
    await api.post('/auth/send-reset-code', {
      phone: forgotPasswordForm.phone
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
    ElMessage.error('发送验证码失败')
  }
}

// 重置密码
const handleResetPassword = async () => {
  if (!forgotPasswordFormRef.value) return
  
  try {
    await forgotPasswordFormRef.value.validate()
    resetLoading.value = true
    
    await api.post('/auth/reset-password', {
      phone: forgotPasswordForm.phone,
      code: forgotPasswordForm.code,
      newPassword: forgotPasswordForm.newPassword
    })
    
    ElMessage.success('密码重置成功，请重新登录')
    forgotPasswordVisible.value = false
    
    // 清空表单
    Object.assign(forgotPasswordForm, {
      phone: '',
      code: '',
      newPassword: ''
    })
  } catch (error) {
    ElMessage.error('重置密码失败')
  } finally {
    resetLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  display: flex;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 900px;
  width: 100%;
  min-height: 500px;
}

.login-form {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}

.form-footer {
  text-align: center;
  margin-top: 30px;
  color: var(--text-secondary);
}

.login-banner {
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
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.feature-list {
  list-style: none;
  text-align: left;
}

.feature-list li {
  padding: 8px 0;
  font-size: 14px;
  opacity: 0.9;
}

.code-input {
  display: flex;
  gap: 10px;
}

.code-input .el-input {
  flex: 1;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    max-width: 400px;
  }
  
  .login-banner {
    order: -1;
    padding: 30px;
  }
  
  .banner-content h3 {
    font-size: 24px;
  }
  
  .login-form {
    padding: 40px 30px;
  }
  
  .form-title {
    font-size: 24px;
  }
}
</style>