# 校园二手交易平台 - 前端项目

基于 Vue 3 + Element Plus 构建的现代化校园二手交易平台前端应用。

## 🚀 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **样式**: SCSS
- **图表**: ECharts
- **Cookie 管理**: js-cookie

## 📁 项目结构

```
前端/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── auth.js        # 认证相关接口
│   │   ├── product.js     # 商品相关接口
│   │   └── request.js     # Axios 配置
│   ├── components/        # 公共组件
│   ├── layouts/           # 布局组件
│   │   ├── AdminLayout.vue    # 管理员布局
│   │   ├── AuthLayout.vue     # 认证页面布局
│   │   └── UserLayout.vue     # 用户端布局
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── stores/            # Pinia 状态管理
│   │   └── user.js        # 用户状态
│   ├── styles/            # 样式文件
│   │   ├── index.scss     # 全局样式
│   │   └── variables.scss # SCSS 变量
│   ├── utils/             # 工具函数
│   │   └── auth.js        # 认证工具
│   ├── views/             # 页面组件
│   │   ├── admin/         # 管理员页面
│   │   │   ├── Dashboard.vue          # 仪表盘
│   │   │   ├── UserManagement.vue     # 用户管理
│   │   │   └── ProductManagement.vue  # 商品管理
│   │   ├── auth/          # 认证页面
│   │   │   ├── Login.vue      # 登录
│   │   │   └── Register.vue   # 注册
│   │   └── user/          # 用户端页面
│   │       ├── Home.vue           # 首页
│   │       ├── ProductDetail.vue  # 商品详情
│   │       ├── PublishProduct.vue # 发布商品
│   │       └── Profile.vue        # 个人中心
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML 模板
├── package.json           # 依赖配置
├── vite.config.js         # Vite 配置
└── README.md              # 项目说明
```

## 🛠️ 开发环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

## 📦 安装依赖

```bash
npm install
```

## 🚀 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

## 🏗️ 构建生产版本

```bash
npm run build
```

## 🔍 预览生产构建

```bash
npm run preview
```

## 📋 功能特性

### 用户端功能

- ✅ **用户认证**: 注册、登录、手机验证、学生身份认证
- ✅ **首页浏览**: 商品列表、搜索、分类筛选、轮播图
- ✅ **商品发布**: 图片上传、信息填写、标签管理
- ✅ **商品详情**: 详细信息展示、图片预览、相关推荐
- ✅ **个人中心**: 个人信息、我的发布、订单管理、收藏夹
- 🔄 **交易功能**: 在线沟通、订单管理、支付集成
- 🔄 **评价系统**: 交易后互评、信誉积分

### 管理员端功能

- ✅ **数据仪表盘**: 用户、商品、订单统计，图表展示
- ✅ **用户管理**: 用户列表、状态管理、身份认证审核
- ✅ **商品管理**: 商品审核、状态管理、批量操作
- 🔄 **内容审核**: 商品和评论审核
- 🔄 **订单管理**: 订单查看、纠纷处理
- 🔄 **系统管理**: 分类管理、公告发布、操作日志

### 技术特性

- 📱 **响应式设计**: 适配桌面端和移动端
- 🎨 **现代化 UI**: 基于 Element Plus 的美观界面
- 🔐 **权限控制**: 基于角色的访问控制
- 📊 **数据可视化**: ECharts 图表展示
- 🚀 **性能优化**: 代码分割、懒加载、缓存策略
- 🛡️ **安全防护**: XSS 防护、CSRF 防护、输入验证

## 🔧 配置说明

### API 代理配置

开发环境下，API 请求会代理到后端服务器：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### 环境变量

可以创建 `.env` 文件来配置环境变量：

```bash
# API 基础地址
VITE_API_BASE_URL=http://localhost:8080

# 应用标题
VITE_APP_TITLE=校园二手交易平台
```

## 🎯 开发规范

### 代码风格

- 使用 Vue 3 Composition API
- 遵循 ESLint 规则
- 使用 Prettier 格式化代码
- 组件名使用 PascalCase
- 文件名使用 kebab-case

### 提交规范

```bash
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 🚀 部署说明

### 构建优化

- 代码分割：按路由和第三方库分割
- 资源压缩：CSS/JS 压缩和混淆
- 图片优化：支持 WebP 格式
- CDN 集成：静态资源 CDN 加速

### 部署步骤

1. 构建生产版本：`npm run build`
2. 将 `dist` 目录部署到 Web 服务器
3. 配置 Nginx 反向代理（可选）
4. 配置 HTTPS 证书（推荐）

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支：`git checkout -b feature/new-feature`
3. 提交更改：`git commit -am 'Add new feature'`
4. 推送分支：`git push origin feature/new-feature`
5. 提交 Pull Request

## 📄 许可证

MIT License

## 📞 联系我们

如有问题或建议，请联系开发团队。

---

**校园二手交易平台** - 让闲置物品重新焕发价值 ✨