<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <img src="@/assets/images/logo.png" alt="青春校园" class="logo">
        <h2 class="title">注册账号</h2>
        <p class="subtitle">加入青春校园，开启二手交易之旅</p>
      </div>
      
      <el-form
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @keyup.enter.native="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
            @blur="checkUsername"
          >
            <template slot="suffix">
              <i v-if="usernameChecking" class="el-icon-loading"></i>
              <i v-else-if="usernameValid === true" class="el-icon-check" style="color: #67c23a;"></i>
              <i v-else-if="usernameValid === false" class="el-icon-close" style="color: #f56c6c;"></i>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="el-icon-message"
            size="large"
            @blur="checkEmail"
          >
            <template slot="suffix">
              <i v-if="emailChecking" class="el-icon-loading"></i>
              <i v-else-if="emailValid === true" class="el-icon-check" style="color: #67c23a;"></i>
              <i v-else-if="emailValid === false" class="el-icon-close" style="color: #f56c6c;"></i>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号（可选）"
            prefix-icon="el-icon-phone"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary">《用户协议》</el-link>
            和
            <el-link type="primary">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <el-form-item class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script>
import { checkUsername, checkEmail } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      loading: false,
      usernameChecking: false,
      emailChecking: false,
      usernameValid: null,
      emailValid: null,
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        agreement: false
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
          { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码必须包含字母和数字', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        agreement: [
          { validator: (rule, value, callback) => {
            if (!value) {
              callback(new Error('请阅读并同意用户协议'))
            } else {
              callback()
            }
          }, trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async checkUsername() {
      if (!this.registerForm.username || this.registerForm.username.length < 3) {
        this.usernameValid = null
        return
      }
      
      this.usernameChecking = true
      try {
        const response = await checkUsername(this.registerForm.username)
        this.usernameValid = response.data.code === 200 && !response.data.data.exists
      } catch (error) {
        this.usernameValid = null
      } finally {
        this.usernameChecking = false
      }
    },
    
    async checkEmail() {
      if (!this.registerForm.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.registerForm.email)) {
        this.emailValid = null
        return
      }
      
      this.emailChecking = true
      try {
        const response = await checkEmail(this.registerForm.email)
        this.emailValid = response.data.code === 200 && !response.data.data.exists
      } catch (error) {
        this.emailValid = null
      } finally {
        this.emailChecking = false
      }
    },
    
    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          if (this.usernameValid === false) {
            this.$message.error('用户名已存在')
            return
          }
          if (this.emailValid === false) {
            this.$message.error('邮箱已存在')
            return
          }
          
          this.loading = true
          try {
            const result = await this.$store.dispatch('user/register', {
              username: this.registerForm.username,
              email: this.registerForm.email,
              password: this.registerForm.password,
              phone: this.registerForm.phone
            })
            
            if (result.success) {
              this.$message.success('注册成功，请登录')
              this.$router.push('/login')
            } else {
              this.$message.error(result.message || '注册失败')
            }
          } catch (error) {
            this.$message.error('注册失败，请稍后重试')
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px 0;
}

.register-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 450px;
  max-width: 90vw;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
  max-height: 90vh;
  overflow-y: auto;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  height: 60px;
  margin-bottom: 15px;
}

.title {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-form-item:last-child {
  margin-bottom: 0;
}

.register-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
}

.login-link span {
  color: #666;
  margin-right: 5px;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-box {
    width: 95%;
    padding: 30px 20px;
  }
  
  .title {
    font-size: 20px;
  }
}
</style>