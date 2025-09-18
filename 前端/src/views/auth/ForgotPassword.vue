<template>
  <div class="forgot-password">
    <div class="forgot-password-container">
      <div class="forgot-password-header">
        <h2>找回密码</h2>
        <p>请输入您的邮箱地址，我们将发送重置密码链接到您的邮箱</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="forgot-password-form"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱地址"
            size="large"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            发送重置链接
          </el-button>
        </el-form-item>
      </el-form>

      <div class="forgot-password-footer">
        <el-link @click="goToLogin">返回登录</el-link>
        <span class="divider">|</span>
        <el-link @click="goToRegister">注册账号</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message } from '@element-plus/icons-vue'
import { sendPasswordResetEmail } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

// 表单数据
const form = reactive({
  email: ''
})

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    loading.value = true
    await sendPasswordResetEmail(form.email)
    
    ElMessage.success('重置密码链接已发送到您的邮箱，请查收')
    
    // 可以选择跳转到登录页面或显示成功页面
    setTimeout(() => {
      router.push('/auth/login')
    }, 2000)
    
  } catch (error) {
    console.error('发送重置邮件失败:', error)
    ElMessage.error(error.message || '发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/auth/login')
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/auth/register')
}
</script>

<style lang="scss" scoped>
.forgot-password {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-color-light) 100%);
  padding: 20px;
  
  .forgot-password-container {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--box-shadow-dark);
    padding: 40px;
    width: 100%;
    max-width: 400px;
    
    .forgot-password-header {
      text-align: center;
      margin-bottom: 32px;
      
      h2 {
        color: var(--text-primary);
        font-size: var(--font-size-extra-large);
        font-weight: var(--font-weight-primary);
        margin: 0 0 12px 0;
      }
      
      p {
        color: var(--text-secondary);
        font-size: var(--font-size-small);
        line-height: 1.5;
        margin: 0;
      }
    }
    
    .forgot-password-form {
      .el-form-item {
        margin-bottom: 24px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      .submit-btn {
        width: 100%;
        height: 48px;
        font-size: var(--font-size-medium);
        font-weight: var(--font-weight-primary);
      }
    }
    
    .forgot-password-footer {
      text-align: center;
      margin-top: 24px;
      
      .divider {
        color: var(--text-secondary);
        margin: 0 12px;
      }
      
      .el-link {
        font-size: var(--font-size-small);
      }
    }
  }
}

// 响应式设计
@include respond-to(sm) {
  .forgot-password {
    padding: 12px;
    
    .forgot-password-container {
      padding: 24px;
      
      .forgot-password-header {
        margin-bottom: 24px;
        
        h2 {
          font-size: var(--font-size-large);
        }
      }
    }
  }
}
</style>