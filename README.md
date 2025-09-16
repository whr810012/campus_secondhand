# 校园二手交易平台

一个基于Vue.js + Spring Boot的校园二手商品交易平台，为在校学生提供安全、便捷的二手商品交易服务。

## 项目特色

- 🎓 **学生身份认证** - 确保平台用户均为在校学生
- 🛡️ **安全交易保障** - 完善的信誉评价体系和交易流程
- 💬 **便捷沟通工具** - 内置聊天系统，方便买卖双方沟通
- 📱 **响应式设计** - 支持PC端和移动端访问
- 🔧 **完善的管理后台** - 内容审核、用户管理、数据统计等功能

## 技术栈

### 前端
- Vue 3 + Composition API
- Vue Router 4 - 路由管理
- Pinia - 状态管理
- Element Plus - UI组件库
- Axios - HTTP客户端
- Vite - 构建工具

### 后端
- Spring Boot 2.7.x - 主框架
- Spring Security - 安全框架
- MyBatis Plus - ORM框架
- MySQL 8.0 - 数据库
- Redis - 缓存
- JWT - 身份认证

## 功能模块

### 用户端功能

1. **首页浏览**
   - 商品列表展示
   - 搜索功能
   - 分类筛选
   - 排序功能

2. **商品发布**
   - 图片上传
   - 商品信息填写
   - 发布闲置商品

3. **个人中心**
   - 个人信息管理
   - 我的发布
   - 我的订单
   - 收藏夹

4. **交易沟通**
   - 商品留言
   - 收藏商品
   - 在线聊天

5. **订单管理**
   - 商品购买
   - 支付功能（线上/线下）
   - 确认收货
   - 订单状态跟踪

6. **注册登录**
   - 手机号注册
   - 学生身份认证
   - 密码找回

7. **信誉评价**
   - 交易后互评
   - 评分系统
   - 信誉积分

### 管理员端功能

1. **仪表盘**
   - 用户数据统计
   - 商品数据统计
   - 订单数据统计

2. **内容审核**
   - 商品审核
   - 评论审核
   - 违规内容处理

3. **用户管理**
   - 用户列表
   - 用户封禁
   - 学生身份认证审核

4. **商品管理**
   - 商品列表
   - 下架违规商品
   - 商品分类管理

5. **订单管理**
   - 订单列表
   - 纠纷处理
   - 退款管理

6. **分类与公告**
   - 分类管理
   - 公告发布
   - 系统配置

7. **操作日志**
   - 管理操作记录
   - 系统日志查看

## 项目结构

```
campus_secondhand/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   │   ├── user/        # 用户端页面
│   │   │   ├── admin/       # 管理员端页面
│   │   │   └── auth/        # 认证相关页面
│   │   ├── components/      # 公共组件
│   │   ├── stores/          # 状态管理
│   │   ├── router/          # 路由配置
│   │   ├── api/             # API接口
│   │   └── utils/           # 工具函数
│   ├── package.json
│   └── vite.config.js
├── backend/                  # 后端项目
│   ├── src/main/java/com/campus/secondhand/
│   │   ├── controller/      # 控制器
│   │   ├── service/         # 服务层
│   │   ├── mapper/          # 数据访问层
│   │   ├── entity/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   ├── config/          # 配置类
│   │   └── common/          # 公共类
│   ├── src/main/resources/
│   │   ├── mapper/          # MyBatis映射文件
│   │   └── application.yml  # 配置文件
│   └── pom.xml
├── database/                 # 数据库脚本
│   └── init.sql
└── README.md
```

## 快速开始

### 环境要求

- Node.js 16+
- Java 8+
- MySQL 8.0+
- Redis 6.0+

### 数据库初始化

1. 创建MySQL数据库
```sql
CREATE DATABASE campus_secondhand CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本
```bash
mysql -u root -p campus_secondhand < database/init.sql
```

### 后端启动

1. 进入后端目录
```bash
cd backend
```

2. 修改配置文件
编辑 `src/main/resources/application.yml`，配置数据库连接信息

3. 启动应用
```bash
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端应用将在 http://localhost:3000 启动

## 默认账户

### 管理员账户
- 手机号：13800000000
- 密码：admin123456

## 部署说明

### 前端部署

1. 构建生产版本
```bash
npm run build
```

2. 将 `dist` 目录部署到Web服务器

### 后端部署

1. 打包应用
```bash
mvn clean package
```

2. 运行JAR文件
```bash
java -jar target/secondhand-1.0.0.jar
```

## 开发规范

### 前端规范
- 使用 Vue 3 Composition API
- 组件命名采用 PascalCase
- 文件命名采用 kebab-case
- 使用 ESLint 进行代码检查

### 后端规范
- 遵循 RESTful API 设计规范
- 使用统一的响应格式
- 异常统一处理
- 接口文档使用 Swagger

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱：campus-secondhand@example.com
- 项目地址：https://github.com/your-username/campus-secondhand

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础的用户注册登录功能
- 实现商品发布和浏览功能
- 实现基础的订单管理功能
- 实现管理员后台基础功能