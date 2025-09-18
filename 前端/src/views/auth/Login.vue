<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h2>登录账户</h2>
        <p>欢迎回到校园二手交易平台</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <div class="form-options">
          <el-checkbox v-model="loginForm.remember">
            记住我
          </el-checkbox>
        </div>
        
        <el-button
          type="primary"
          size="large"
          class="login-button"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
        
        <div class="form-footer">
          <span>还没有账户？</span>
          <el-link type="primary" @click="$router.push('/register')">
            立即注册
          </el-link>
        </div>
      </el-form>
    </div>
    

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'


const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

// 登录表单
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



// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    await userStore.loginAction({
      phone: loginForm.phone,
      password: loginForm.password,
      remember: loginForm.remember
    })
    
    ElMessage.success('登录成功')
    
    // 根据用户角色智能跳转
    let redirectPath = route.query.redirect
    if (!redirectPath) {
      // 如果是管理员，跳转到管理后台
      if (userStore.isAdmin) {
        redirectPath = '/admin/dashboard'
      } else {
        redirectPath = '/'
      }
    }
    router.push(redirectPath)
    
  } catch (error) {
    console.error('登录失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}





// 从本地存储加载记住的登录信息
const loadRememberedLogin = () => {
  const remembered = localStorage.getItem('rememberedLogin')
  if (remembered) {
    try {
      const loginInfo = JSON.parse(remembered)
      loginForm.phone = loginInfo.phone || ''
      loginForm.remember = true
    } catch (error) {
      console.error('加载记住的登录信息失败:', error)
    }
  }
}

// 保存记住的登录信息
const saveRememberedLogin = () => {
  if (loginForm.remember) {
    localStorage.setItem('rememberedLogin', JSON.stringify({
      phone: loginForm.phone
    }))
  } else {
    localStorage.removeItem('rememberedLogin')
  }
}

// 监听登录表单变化
watch(() => loginForm.remember, (newVal) => {
  if (!newVal) {
    localStorage.removeItem('rememberedLogin')
  }
})

// 组件挂载时加载记住的登录信息
onMounted(() => {
  loadRememberedLogin()
})



// 监听登录成功，保存记住的登录信息
watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    saveRememberedLogin()
  }
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .login-container {
    width: 100%;
    max-width: 400px;
    padding: 40px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    
    .login-header {
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
    
    .login-form {
      .el-form-item {
        margin-bottom: 20px;
      }
      
      .form-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
        
        .el-checkbox {
          :deep(.el-checkbox__label) {
            font-size: 14px;
            color: var(--text-secondary);
          }
        }
      }
      
      .login-button {
        width: 100%;
        height: 48px;
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 20px;
      }
      
      .form-footer {
        text-align: center;
        font-size: 14px;
        color: var(--text-secondary);
        
        .el-link {
          margin-left: 4px;
        }
      }
    }
    

  }
}



// 响应式设计
@media (max-width: 480px) {
  .login-page {
    padding: 20px;
    
    .login-container {
      padding: 24px;
      
      .login-header {
        margin-bottom: 24px;
        
        h2 {
          font-size: 24px;
        }
        
        p {
          font-size: 14px;
        }
      }
    }
  }
}

// 输入框样式优化
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

// 按钮样式优化
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
</style>