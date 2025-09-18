<template>
  <div class="auth-layout">
    <div class="auth-container">
      <!-- 左侧背景区域 -->
      <div class="auth-banner">
        <div class="banner-content">
          <div class="logo">
            <el-icon class="logo-icon"><ShoppingBag /></el-icon>
            <h1 class="logo-text">校园二手交易平台</h1>
          </div>
          <p class="banner-description">
            为在校学生提供安全、便捷的二手商品交易服务
          </p>
          <div class="features">
            <div class="feature-item">
              <el-icon><Shield /></el-icon>
              <span>学生身份认证</span>
            </div>
            <div class="feature-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>便捷沟通工具</span>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>信誉评价体系</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧表单区域 -->
      <div class="auth-form">
        <div class="form-container">
          <router-view />
        </div>
        
        <!-- 返回首页 -->
        <div class="back-home">
          <el-button text @click="$router.push('/home')">
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 这里可以添加认证布局相关的逻辑
</script>

<style lang="scss" scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.auth-banner {
  flex: 1;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  color: white;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
    opacity: 0.3;
  }
  
  .banner-content {
    text-align: center;
    position: relative;
    z-index: 1;
  }
  
  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 30px;
    
    .logo-icon {
      font-size: 48px;
    }
    
    .logo-text {
      font-size: 28px;
      font-weight: 600;
      margin: 0;
    }
  }
  
  .banner-description {
    font-size: 18px;
    margin-bottom: 40px;
    opacity: 0.9;
    line-height: 1.6;
  }
  
  .features {
    display: flex;
    flex-direction: column;
    gap: 20px;
    
    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 16px;
      
      .el-icon {
        font-size: 20px;
      }
    }
  }
}

.auth-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  
  .form-container {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60px 40px;
  }
  
  .back-home {
    position: absolute;
    top: 20px;
    left: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .auth-layout {
    padding: 10px;
  }
  
  .auth-container {
    flex-direction: column;
    min-height: auto;
  }
  
  .auth-banner {
    padding: 40px 20px;
    
    .logo {
      .logo-text {
        font-size: 24px;
      }
    }
    
    .banner-description {
      font-size: 16px;
      margin-bottom: 30px;
    }
    
    .features {
      gap: 15px;
      
      .feature-item {
        font-size: 14px;
        
        .el-icon {
          font-size: 18px;
        }
      }
    }
  }
  
  .auth-form {
    .form-container {
      padding: 40px 20px;
    }
  }
}

@media (max-width: 480px) {
  .auth-banner {
    .logo {
      flex-direction: column;
      gap: 8px;
      
      .logo-text {
        font-size: 20px;
      }
    }
  }
}
</style>