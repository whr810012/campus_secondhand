# 青春校园二手平台 - 后端API接口文档

## 项目概述

青春校园二手平台是一个基于Spring Boot开发的校园二手商品交易平台，提供用户注册登录、商品发布、订单管理、分类管理等功能。

**基础信息：**
- 后端框架：Spring Boot
- 数据库：MySQL
- 接口风格：RESTful API
- 数据格式：JSON
- 服务端口：8081
- 接口前缀：`/api`

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1640995200000
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null,
  "timestamp": 1640995200000
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1640995200000
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

# 1. 用户管理接口

## 1.1 用户注册

**接口地址：** `POST /api/user/register`

**请求参数：**
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1640995200000
}
```

## 1.2 用户登录

**接口地址：** `POST /api/user/login`

**请求参数：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "email": "test@example.com",
      "role": 0
    }
  },
  "timestamp": 1640995200000
}
```

## 1.3 获取当前用户信息

**接口地址：** `GET /api/user/current`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": "http://example.com/avatar.jpg",
    "gender": 1,
    "school": "某某大学",
    "major": "计算机科学",
    "grade": "2021级",
    "status": 1,
    "role": 0
  },
  "timestamp": 1640995200000
}
```

## 1.4 更新用户信息

**接口地址：** `PUT /api/user/info/{userId}`

**请求参数：**
```json
{
  "nickname": "新昵称",
  "email": "new@example.com",
  "phone": "13800138000",
  "gender": 1,
  "school": "某某大学",
  "major": "计算机科学",
  "grade": "2021级"
}
```

## 1.5 修改密码

**接口地址：** `PUT /api/user/password/{userId}`

**请求参数：**
```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

## 1.6 分页查询用户列表（管理端）

**接口地址：** `GET /api/user/page`

**请求参数：**
- `current`：当前页码（默认1）
- `size`：每页大小（默认10）
- `keyword`：搜索关键词（可选）
- `role`：用户角色（可选）
- `status`：用户状态（可选）

## 1.7 获取用户详情

**接口地址：** `GET /api/user/{userId}`

## 1.8 更新用户状态

**接口地址：** `PUT /api/user/status/{userId}`

**请求参数：**
```json
{
  "status": 1
}
```

## 1.9 检查用户名是否存在

**接口地址：** `GET /api/user/check-username?username=testuser`

## 1.10 检查邮箱是否存在

**接口地址：** `GET /api/user/check-email?email=test@example.com`

---

# 2. 商品管理接口

## 2.1 发布商品

**接口地址：** `POST /api/product/publish`

**请求参数：**
```json
{
  "title": "二手iPhone 13",
  "description": "9成新，无磕碰，功能正常",
  "price": 4500.00,
  "originalPrice": 6000.00,
  "categoryId": 1,
  "images": "[\"image1.jpg\", \"image2.jpg\"]",
  "conditionDesc": "9成新",
  "tradeType": 2,
  "location": "学校南门",
  "contactInfo": "微信：abc123"
}
```

## 2.2 分页查询商品列表（用户端）

**接口地址：** `GET /api/product/page`

**请求参数：**
- `current`：当前页码（默认1）
- `size`：每页大小（默认10）
- `keyword`：搜索关键词（可选）
- `categoryId`：分类ID（可选）
- `sortBy`：排序字段（可选）
- `sortOrder`：排序方式（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "二手iPhone 13",
        "description": "9成新，无磕碰，功能正常",
        "price": 4500.00,
        "originalPrice": 6000.00,
        "categoryId": 1,
        "sellerId": 1,
        "images": "[\"image1.jpg\", \"image2.jpg\"]",
        "status": 1,
        "viewCount": 100,
        "likeCount": 20,
        "createdTime": "2023-12-01T10:00:00"
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10,
    "pages": 5
  },
  "timestamp": 1640995200000
}
```

## 2.3 获取商品详情

**接口地址：** `GET /api/product/{productId}`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "二手iPhone 13",
    "description": "9成新，无磕碰，功能正常",
    "price": 4500.00,
    "originalPrice": 6000.00,
    "categoryId": 1,
    "sellerId": 1,
    "images": "[\"image1.jpg\", \"image2.jpg\"]",
    "conditionDesc": "9成新",
    "tradeType": 2,
    "location": "学校南门",
    "contactInfo": "微信：abc123",
    "status": 1,
    "viewCount": 100,
    "likeCount": 20,
    "createdTime": "2023-12-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

## 2.4 更新商品信息

**接口地址：** `PUT /api/product/{productId}`

## 2.5 删除商品

**接口地址：** `DELETE /api/product/{productId}`

## 2.6 商品上架/下架

**接口地址：** `PUT /api/product/shelf/{productId}`

**请求参数：**
```json
{
  "status": 1
}
```

## 2.7 审核商品

**接口地址：** `PUT /api/product/audit/{productId}`

**请求参数：**
```json
{
  "status": 1,
  "auditRemark": "审核通过"
}
```

## 2.8 增加商品浏览次数

**接口地址：** `POST /api/product/view/{productId}`

## 2.9 增加商品点赞次数

**接口地址：** `POST /api/product/like/{productId}`

## 2.10 获取热门商品

**接口地址：** `GET /api/product/hot?limit=10`

## 2.11 获取最新商品

**接口地址：** `GET /api/product/latest?limit=10`

## 2.12 获取推荐商品

**接口地址：** `GET /api/product/recommend?limit=10`

## 2.13 搜索商品

**接口地址：** `GET /api/product/search`

**请求参数：**
- `keyword`：搜索关键词（必填）
- `current`：当前页码（默认1）
- `size`：每页大小（默认10）
- `categoryId`：分类ID（可选）
- `sortBy`：排序字段（可选）
- `sortOrder`：排序方式（可选）

## 2.14 上传商品图片

**接口地址：** `POST /api/product/upload-image`

**请求参数：** 文件上传（multipart/form-data）
- `file`：图片文件

---

# 3. 订单管理接口

## 3.1 创建订单

**接口地址：** `POST /api/order/create`

**请求参数：**
```json
{
  "productId": 1,
  "buyerId": 2,
  "sellerId": 1,
  "productTitle": "二手iPhone 13",
  "productImage": "image1.jpg",
  "price": 4500.00,
  "tradeType": 1,
  "deliveryAddress": "某某大学宿舍楼",
  "contactPhone": "13800138000",
  "remark": "请尽快发货"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": "ORD202312010001",
  "timestamp": 1640995200000
}
```

## 3.2 查询用户购买订单

**接口地址：** `GET /api/order/buyer/{userId}`

**请求参数：**
- `current`：当前页码（默认1）
- `size`：每页大小（默认10）
- `status`：订单状态（可选）

## 3.3 查询用户销售订单

**接口地址：** `GET /api/order/seller/{userId}`

## 3.4 根据订单号查询订单

**接口地址：** `GET /api/order/no/{orderNo}`

## 3.5 获取订单详情

**接口地址：** `GET /api/order/{orderId}`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202312010001",
    "productId": 1,
    "buyerId": 2,
    "sellerId": 1,
    "productTitle": "二手iPhone 13",
    "productImage": "image1.jpg",
    "price": 4500.00,
    "tradeType": 1,
    "deliveryAddress": "某某大学宿舍楼",
    "contactPhone": "13800138000",
    "status": 0,
    "remark": "请尽快发货",
    "createdTime": "2023-12-01T10:00:00"
  },
  "timestamp": 1640995200000
}
```

## 3.6 确认订单

**接口地址：** `PUT /api/order/confirm/{orderId}`

## 3.7 取消订单

**接口地址：** `PUT /api/order/cancel/{orderId}`

**请求参数：**
```json
{
  "cancelReason": "不需要了"
}
```

## 3.8 开始交易

**接口地址：** `PUT /api/order/start-trade/{orderId}`

## 3.9 完成订单

**接口地址：** `PUT /api/order/complete/{orderId}`

## 3.10 更新订单状态

**接口地址：** `PUT /api/order/status/{orderId}`

**请求参数：**
```json
{
  "status": 3
}
```

---

# 4. 分类管理接口

## 4.1 创建分类

**接口地址：** `POST /api/category/create`

**请求参数：**
```json
{
  "name": "数码产品",
  "description": "手机、电脑、平板等数码产品",
  "icon": "icon-digital.png",
  "sortOrder": 1
}
```

## 4.2 查询所有启用的分类（用户端）

**接口地址：** `GET /api/category/enabled`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "数码产品",
      "description": "手机、电脑、平板等数码产品",
      "icon": "icon-digital.png",
      "sortOrder": 1,
      "status": 1
    },
    {
      "id": 2,
      "name": "图书文具",
      "description": "教材、小说、文具用品",
      "icon": "icon-book.png",
      "sortOrder": 2,
      "status": 1
    }
  ],
  "timestamp": 1640995200000
}
```

## 4.3 获取分类详情

**接口地址：** `GET /api/category/{categoryId}`

## 4.4 更新分类信息

**接口地址：** `PUT /api/category/{categoryId}`

## 4.5 删除分类

**接口地址：** `DELETE /api/category/{categoryId}`

## 4.6 启用/禁用分类

**接口地址：** `PUT /api/category/status/{categoryId}`

**请求参数：**
```json
{
  "status": 1
}
```

## 4.7 检查分类名称是否存在

**接口地址：** `GET /api/category/check-name?name=数码产品&excludeId=1`

---

# 5. 用户地址管理接口

## 5.1 添加地址

**接口地址：** `POST /api/user-address/add`

**请求参数：**
```json
{
  "userId": 1,
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "海淀区",
  "detailAddress": "某某大学宿舍楼101",
  "isDefault": 1
}
```

## 5.2 查询用户所有地址

**接口地址：** `GET /api/user-address/user/{userId}`

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "海淀区",
      "detailAddress": "某某大学宿舍楼101",
      "isDefault": 1,
      "createdTime": "2023-12-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000
}
```

## 5.3 查询用户默认地址

**接口地址：** `GET /api/user-address/default/{userId}`

## 5.4 获取地址详情

**接口地址：** `GET /api/user-address/{addressId}`

## 5.5 更新地址

**接口地址：** `PUT /api/user-address/{addressId}`

## 5.6 删除地址

**接口地址：** `DELETE /api/user-address/{addressId}`

## 5.7 设置默认地址

**接口地址：** `PUT /api/user-address/default/{addressId}`

## 5.8 获取用户地址数量

**接口地址：** `GET /api/user-address/count/{userId}`

---

# 6. 数据模型说明

## 6.1 用户实体（User）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| password | String | 密码 |
| email | String | 邮箱 |
| phone | String | 手机号 |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| gender | Integer | 性别：0-未知，1-男，2-女 |
| birthday | LocalDate | 生日 |
| school | String | 学校 |
| major | String | 专业 |
| grade | String | 年级 |
| status | Integer | 状态：0-禁用，1-正常 |
| role | Integer | 角色：0-普通用户，1-管理员，2-超级管理员 |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

## 6.2 商品实体（Product）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |
| title | String | 商品标题 |
| description | String | 商品描述 |
| price | BigDecimal | 商品价格 |
| originalPrice | BigDecimal | 原价 |
| categoryId | Long | 分类ID |
| sellerId | Long | 卖家ID |
| images | String | 商品图片（JSON格式） |
| conditionDesc | String | 新旧程度描述 |
| tradeType | Integer | 交易方式：0-线下，1-线上，2-都支持 |
| location | String | 交易地点 |
| contactInfo | String | 联系方式 |
| status | Integer | 状态：0-待审核，1-已上架，2-已下架，3-已售出，4-审核拒绝 |
| viewCount | Integer | 浏览次数 |
| likeCount | Integer | 点赞次数 |
| auditTime | LocalDateTime | 审核时间 |
| auditUserId | Long | 审核人ID |
| auditRemark | String | 审核备注 |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

## 6.3 订单实体（Order）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单号 |
| productId | Long | 商品ID |
| buyerId | Long | 买家ID |
| sellerId | Long | 卖家ID |
| productTitle | String | 商品标题 |
| productImage | String | 商品图片 |
| price | BigDecimal | 交易价格 |
| tradeType | Integer | 交易方式：0-线下，1-线上 |
| deliveryAddress | String | 收货地址 |
| contactPhone | String | 联系电话 |
| status | Integer | 订单状态：0-待确认，1-已确认，2-交易中，3-已完成，4-已取消 |
| remark | String | 备注 |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

## 6.4 分类实体（Category）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| name | String | 分类名称 |
| description | String | 分类描述 |
| icon | String | 分类图标 |
| sortOrder | Integer | 排序 |
| status | Integer | 状态：0-禁用，1-启用 |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

## 6.5 用户地址实体（UserAddress）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 地址ID |
| userId | Long | 用户ID |
| receiverName | String | 收货人姓名 |
| receiverPhone | String | 收货人电话 |
| province | String | 省份 |
| city | String | 城市 |
| district | String | 区县 |
| detailAddress | String | 详细地址 |
| isDefault | Integer | 是否默认地址：0-否，1-是 |
| createdTime | LocalDateTime | 创建时间 |
| updatedTime | LocalDateTime | 更新时间 |

---

# 7. 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 200 | 操作成功 | 请求成功 |
| 400 | 参数错误 | 请求参数不正确 |
| 401 | 未授权访问 | 需要登录 |
| 403 | 权限不足 | 没有操作权限 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 操作失败 | 服务器内部错误 |
| 4001 | Token无效 | 登录凭证无效 |
| 4002 | Token已过期 | 登录凭证已过期 |
| 4003 | 请先登录 | 需要先登录 |

---

# 8. 开发说明

## 8.1 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis（可选）

## 8.2 项目启动

1. 配置数据库连接信息
2. 执行数据库脚本
3. 启动项目：`mvn spring-boot:run`
4. 访问地址：http://localhost:8081

## 8.3 接口测试

推荐使用Postman或其他API测试工具进行接口测试。

## 8.4 注意事项

1. 所有接口都支持跨域访问（@CrossOrigin）
2. 需要登录的接口需要在请求头中携带Token
3. 文件上传接口使用multipart/form-data格式
4. 时间格式统一使用ISO 8601格式
5. 金额字段使用BigDecimal类型，保证精度

---

**文档版本：** v1.0  
**更新时间：** 2023-12-01  
**维护人员：** Campus Team