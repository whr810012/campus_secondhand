# 校园二手交易平台数据库文档

## 📋 概述

本目录包含校园二手交易平台的完整数据库解决方案，包括表结构设计、测试数据、性能优化、安全加固等多个方面。

## 📁 文件结构

```
database/
├── init.sql                    # 主数据库初始化脚本
├── migration_template.sql      # 数据库迁移模板
├── performance_optimization.sql # 性能优化脚本
├── security_enhancement.sql    # 安全加固脚本
└── README.md                  # 本文档
```

## 🚀 快速开始

### 1. 初始化数据库

```bash
# 执行主初始化脚本
mysql -u root -p < init.sql
```

### 2. 应用性能优化

```bash
# 执行性能优化脚本
mysql -u root -p campus_secondhand < performance_optimization.sql
```

### 3. 启用安全加固

```bash
# 执行安全加固脚本
mysql -u root -p campus_secondhand < security_enhancement.sql
```

## 📊 数据库架构

### 核心表结构

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, phone, nickname, school_id, verify_status |
| `schools` | 学校表 | id, name, province, city |
| `categories` | 商品分类表 | id, name, parent_id, sort |
| `products` | 商品表 | id, title, price, category_id, seller_id, status |
| `orders` | 订单表 | id, product_id, buyer_id, seller_id, status |
| `favorites` | 收藏表 | id, user_id, product_id |
| `reviews` | 评价表 | id, order_id, reviewer_id, rating |
| `messages` | 消息表 | id, sender_id, receiver_id, content |
| `announcements` | 公告表 | id, title, content, target_type |
| `operation_logs` | 操作日志表 | id, operator_id, operation_type, target_id |

### 扩展功能表

| 表名 | 说明 | 用途 |
|------|------|------|
| `performance_logs` | 性能监控表 | 记录查询执行时间 |
| `security_audit_logs` | 安全审计表 | 记录敏感操作 |
| `login_security` | 登录安全表 | 记录登录行为 |
| `ip_blacklist` | IP黑名单表 | 管理封禁IP |

## 🔧 功能特性

### ✅ 已实现功能

- **完整的表结构设计**：支持用户管理、商品交易、消息通讯等核心功能
- **丰富的测试数据**：包含11个用户、13个商品、5个订单等测试数据
- **性能优化**：复合索引、查询优化、缓存预热等
- **安全加固**：数据加密、访问控制、审计日志等
- **数据统计**：用户统计、商品统计等视图
- **自动化功能**：触发器、存储过程等

### 🎯 核心业务流程

1. **用户注册登录**：手机号注册 → 学生认证 → 完善信息
2. **商品发布**：发布商品 → 管理员审核 → 上架销售
3. **交易流程**：浏览商品 → 下单购买 → 支付确认 → 交易完成
4. **评价系统**：交易完成 → 互相评价 → 信誉更新
5. **消息沟通**：买卖双方沟通 → 协商细节 → 达成交易

## 📈 性能优化

### 索引策略

- **主键索引**：所有表都有自增主键
- **唯一索引**：用户手机号、学校代码等
- **复合索引**：商品搜索、订单查询等高频操作
- **JSON索引**：商品图片数量虚拟列索引

### 查询优化

- **分页查询**：使用LIMIT和OFFSET优化
- **条件查询**：合理使用WHERE条件和索引
- **关联查询**：避免N+1查询问题
- **统计查询**：使用视图预计算统计数据

### 缓存策略

- **查询缓存**：MySQL查询缓存配置
- **应用缓存**：Redis缓存热点数据
- **缓存预热**：系统启动时预加载常用数据

## 🔒 安全措施

### 数据加密

- **敏感字段加密**：手机号、身份证号等
- **密码安全**：使用bcrypt等强哈希算法
- **传输加密**：启用SSL/TLS连接

### 访问控制

- **用户权限**：不同角色的数据访问权限
- **IP白名单**：限制数据库访问来源
- **操作审计**：记录所有敏感操作

### 安全监控

- **异常登录检测**：多次失败登录告警
- **可疑操作监控**：敏感数据变更记录
- **自动防护**：可疑IP自动封禁

## 🛠️ 运维管理

### 数据备份

```bash
# 全量备份
mysqldump --single-transaction --routines --triggers \
  --user=root --password=your_password \
  campus_secondhand > backup_$(date +%Y%m%d).sql

# 增量备份
mysqlbinlog --start-datetime="2024-12-19 00:00:00" \
  mysql-bin.000001 > incremental_backup.sql
```

### 性能监控

```sql
-- 查看慢查询
SELECT * FROM slow_query_analysis;

-- 检查表大小
SELECT table_name, table_rows, 
       ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size(MB)'
FROM information_schema.tables 
WHERE table_schema = 'campus_secondhand';

-- 缓存预热
CALL WarmupCache();
```

### 数据清理

```sql
-- 清理90天前的日志数据
CALL CleanupOldData(90);

-- 检查可疑登录
CALL CheckSuspiciousLogin();

-- 自动封禁可疑IP
CALL AutoBlockSuspiciousIP();
```

## 📋 测试数据

### 测试账号

**管理员账号**：
- 手机号：`13800000000`
- 密码：`admin123456`

**普通用户账号**：
- 手机号：`13800000001` - `13800000010`
- 密码：`user123456`

### 数据统计

- 👥 用户数量：11个（1个管理员 + 10个普通用户）
- 🏫 学校数量：10所知名高校
- 📦 商品数量：13个（不同分类和状态）
- 📋 订单数量：5个（不同状态流转）
- ❤️ 收藏数量：27条收藏记录
- ⭐ 评价数量：4条评价记录
- 💬 消息数量：8条消息记录
- 📢 公告数量：4条系统公告

## 🔄 数据库迁移

### 使用迁移模板

1. 复制 `migration_template.sql`
2. 修改版本号和描述信息
3. 编写具体的迁移脚本
4. 测试迁移脚本
5. 在生产环境执行

### 迁移最佳实践

- ✅ 迁移前备份数据
- ✅ 在测试环境先验证
- ✅ 使用事务确保原子性
- ✅ 准备回滚脚本
- ✅ 记录迁移日志

## 🚨 故障排查

### 常见问题

1. **表已存在错误**
   - 解决方案：执行 `init.sql` 中的 DROP TABLE 语句

2. **外键约束错误**
   - 解决方案：检查数据完整性，确保关联数据存在

3. **性能问题**
   - 解决方案：检查慢查询日志，优化索引策略

4. **连接数过多**
   - 解决方案：优化连接池配置，检查连接泄漏

### 监控指标

- 📊 QPS（每秒查询数）
- ⏱️ 平均响应时间
- 💾 缓存命中率
- 🔗 活跃连接数
- 📈 慢查询数量

## 📞 技术支持

如有问题，请参考以下资源：

- 📖 [MySQL官方文档](https://dev.mysql.com/doc/)
- 🔧 [性能优化指南](performance_optimization.sql)
- 🔒 [安全加固指南](security_enhancement.sql)
- 🔄 [迁移模板](migration_template.sql)

## 📝 更新日志

### v2.0 (2024-12-19)
- ✅ 完善数据库表结构设计
- ✅ 添加丰富的测试数据
- ✅ 实现性能优化方案
- ✅ 加强安全防护措施
- ✅ 提供完整的运维工具

### v1.0 (初始版本)
- ✅ 基础表结构创建
- ✅ 基本数据插入

---

**注意**：在生产环境中使用前，请务必：
1. 修改默认密码和加密密钥
2. 配置适当的用户权限
3. 启用SSL连接
4. 设置定期备份
5. 配置监控告警

🎉 **祝您使用愉快！**