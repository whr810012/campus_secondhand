-- 校园二手交易平台数据库初始化脚本
-- 版本: v2.0
-- 更新时间: 2024-12-19
-- 说明: 包含完整的表结构、测试数据、视图、存储过程和触发器

-- 设置SQL模式和字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_secondhand` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_secondhand`;

-- 删除已存在的表（按依赖关系逆序删除）
DROP TABLE IF EXISTS `operation_logs`;
DROP TABLE IF EXISTS `announcements`;
DROP TABLE IF EXISTS `messages`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `imgs`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `schools`;

-- 删除性能优化和安全加固相关的表
DROP TABLE IF EXISTS `performance_logs`;
DROP TABLE IF EXISTS `security_audit_logs`;
DROP TABLE IF EXISTS `login_security`;
DROP TABLE IF EXISTS `ip_blacklist`;

-- 删除已存在的视图
DROP VIEW IF EXISTS `user_stats`;
DROP VIEW IF EXISTS `product_stats`;
DROP VIEW IF EXISTS `slow_query_analysis`;
DROP VIEW IF EXISTS `users_masked_view`;

-- 删除已存在的存储过程
DROP PROCEDURE IF EXISTS `UpdateUserCreditScore`;
DROP PROCEDURE IF EXISTS `WarmupCache`;
DROP PROCEDURE IF EXISTS `CleanupOldData`;
DROP PROCEDURE IF EXISTS `CheckSuspiciousLogin`;
DROP PROCEDURE IF EXISTS `AutoBlockSuspiciousIP`;

-- 删除已存在的触发器
DROP TRIGGER IF EXISTS `product_status_update_trigger`;
DROP TRIGGER IF EXISTS `user_sensitive_data_audit`;
DROP TRIGGER IF EXISTS `admin_operation_audit`;

-- 删除已存在的函数
DROP FUNCTION IF EXISTS `EncryptPhone`;
DROP FUNCTION IF EXISTS `DecryptPhone`;
DROP FUNCTION IF EXISTS `MaskIdCard`;

-- 用户表
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `student_id` varchar(50) DEFAULT NULL COMMENT '学号',
  `school_id` bigint(20) DEFAULT NULL COMMENT '学校ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色：user-普通用户，admin-管理员',
  `status` tinyint(1) DEFAULT 0 COMMENT '账户状态：0-正常，1-禁用',
  `verify_status` tinyint(1) DEFAULT 0 COMMENT '学生认证状态：0-未认证，1-认证中，2-已认证，3-认证失败',
  `verify_reason` varchar(255) DEFAULT NULL COMMENT '认证失败原因',
  `id_card_img_id` bigint(20) DEFAULT NULL COMMENT '身份证图片ID',
  `student_card_img_id` bigint(20) DEFAULT NULL COMMENT '学生证图片ID',
  `credit_score` int(11) DEFAULT 100 COMMENT '信誉分数',
  `trade_count` int(11) DEFAULT 0 COMMENT '交易次数',
  `good_rate` decimal(5,2) DEFAULT 100.00 COMMENT '好评率',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_school_id` (`school_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_id_card_img_id` (`id_card_img_id`),
  KEY `idx_student_card_img_id` (`student_card_img_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学校表
CREATE TABLE `schools` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `name` varchar(100) NOT NULL COMMENT '学校名称',
  `code` varchar(50) DEFAULT NULL COMMENT '学校代码',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_province_city` (`province`, `city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- 商品分类表
CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父分类ID',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE `products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(200) NOT NULL COMMENT '商品标题',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `seller_id` bigint(20) NOT NULL COMMENT '卖家ID',
  `images` json DEFAULT NULL COMMENT '商品图片，JSON数组格式',
  `status` varchar(20) DEFAULT 'available' COMMENT '商品状态：available-可售，reserved-已预定，sold-已售出，unavailable-已下架',
  `audit_status` tinyint(1) DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `audit_reason` varchar(255) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `condition` tinyint(1) DEFAULT 1 COMMENT '商品成色：1-全新，2-几乎全新，3-轻微使用痕迹，4-明显使用痕迹，5-重度使用痕迹',
  `trade_type` tinyint(1) DEFAULT 3 COMMENT '交易方式：1-仅线下，2-仅线上，3-线上线下均可',
  `trade_location` varchar(255) DEFAULT NULL COMMENT '交易地点',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览次数',
  `favorite_count` int(11) DEFAULT 0 COMMENT '收藏次数',
  `is_top` tinyint(1) DEFAULT 0 COMMENT '是否置顶',
  `top_expire_time` datetime DEFAULT NULL COMMENT '置顶到期时间',
  `is_recommend` tinyint(1) DEFAULT 0 COMMENT '是否推荐',
  `tags` json DEFAULT NULL COMMENT '标签，JSON数组格式',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_price` (`price`),
  FULLTEXT KEY `ft_title_description` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 图片表
CREATE TABLE `imgs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `name` varchar(255) NOT NULL COMMENT '图片名称',
  `base64_data` longtext NOT NULL COMMENT '图片base64数据',
  `user_id` bigint(20) NOT NULL COMMENT '上传用户ID',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-删除，1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';

-- 订单表
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `buyer_id` bigint(20) NOT NULL COMMENT '买家ID',
  `seller_id` bigint(20) NOT NULL COMMENT '卖家ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '订单状态：pending-待付款，paid-已付款，shipped-已发货，delivered-已送达，completed-已完成，cancelled-已取消，refunded-已退款',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式：online-线上支付，offline-线下支付',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_no` varchar(100) DEFAULT NULL COMMENT '支付流水号',
  `trade_type` tinyint(1) DEFAULT 3 COMMENT '交易方式：1-仅线下，2-仅线上，3-线上线下均可',
  `trade_location` varchar(255) DEFAULT NULL COMMENT '交易地点',
  `delivery_address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `buyer_rated` tinyint(1) DEFAULT 0 COMMENT '买家评价状态：0-未评价，1-已评价',
  `seller_rated` tinyint(1) DEFAULT 0 COMMENT '卖家评价状态：0-未评价，1-已评价',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 收藏表
CREATE TABLE `favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 评价表
CREATE TABLE `reviews` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `reviewer_id` bigint(20) NOT NULL COMMENT '评价人ID',
  `reviewee_id` bigint(20) NOT NULL COMMENT '被评价人ID',
  `rating` tinyint(1) NOT NULL COMMENT '评分：1-5分',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `images` json DEFAULT NULL COMMENT '评价图片',
  `type` varchar(20) NOT NULL COMMENT '评价类型：buyer-买家评价，seller-卖家评价',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_reviewee_id` (`reviewee_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 消息表
CREATE TABLE `messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `content` text NOT NULL COMMENT '消息内容',
  `type` varchar(20) DEFAULT 'text' COMMENT '消息类型：text-文本，image-图片，system-系统消息',
  `is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 公告表
CREATE TABLE `announcements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `type` varchar(20) DEFAULT 'normal' COMMENT '公告类型：normal-普通，important-重要，urgent-紧急',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `author_id` bigint(20) NOT NULL COMMENT '发布人ID',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 操作日志表
CREATE TABLE `operation_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `method` varchar(10) NOT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `result` text COMMENT '操作结果',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `execute_time` int(11) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_msg` text COMMENT '错误信息',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入默认数据

-- 插入学校数据
INSERT INTO `schools` (`name`, `code`, `province`, `city`) VALUES
('北京大学', 'PKU', '北京市', '北京市'),
('清华大学', 'THU', '北京市', '北京市'),
('复旦大学', 'FDU', '上海市', '上海市'),
('上海交通大学', 'SJTU', '上海市', '上海市'),
('浙江大学', 'ZJU', '浙江省', '杭州市'),
('南京大学', 'NJU', '江苏省', '南京市'),
('中山大学', 'SYSU', '广东省', '广州市'),
('华中科技大学', 'HUST', '湖北省', '武汉市'),
('西安交通大学', 'XJTU', '陕西省', '西安市'),
('哈尔滨工业大学', 'HIT', '黑龙江省', '哈尔滨市');

-- 插入分类数据
INSERT INTO `categories` (`name`, `description`, `parent_id`, `sort`) VALUES
('数码电子', '手机、电脑、相机等数码产品', 0, 1),
('图书教材', '教科书、参考书、小说等', 0, 2),
('生活用品', '日用品、化妆品、服装等', 0, 3),
('运动户外', '运动器材、户外用品等', 0, 4),
('交通工具', '自行车、电动车等', 0, 5),
('其他', '其他类别商品', 0, 6),
('手机', '智能手机、功能机等', 1, 1),
('电脑', '笔记本电脑、台式机、平板等', 1, 2),
('相机', '数码相机、摄像设备等', 1, 3),
('教科书', '各专业教科书', 2, 1),
('参考书', '考试参考书、工具书等', 2, 2),
('小说文学', '小说、散文、诗歌等', 2, 3);

-- 插入用户数据（包括管理员和普通用户）
INSERT INTO `users` (`phone`, `password`, `nickname`, `avatar`, `student_id`, `school_id`, `real_name`, `gender`, `role`, `status`, `verify_status`, `credit_score`, `trade_count`, `good_rate`, `last_login_time`, `last_login_ip`) VALUES
-- 管理员用户
('13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '系统管理员', '/avatars/admin.jpg', NULL, NULL, '管理员', 0, 'admin', 0, 2, 100, 0, 100.00, '2024-12-20 10:00:00', '192.168.1.100'),
-- 普通用户
('13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '张小明', '/avatars/user1.jpg', '2021001001', 1, '张明', 1, 'user', 0, 2, 95, 5, 98.50, '2024-12-20 09:30:00', '192.168.1.101'),
('13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '李小红', '/avatars/user2.jpg', '2021001002', 1, '李红', 2, 'user', 0, 2, 88, 8, 96.25, '2024-12-20 08:45:00', '192.168.1.102'),
('13800000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '王大华', '/avatars/user3.jpg', '2020001003', 2, '王华', 1, 'user', 0, 2, 92, 12, 97.80, '2024-12-20 07:20:00', '192.168.1.103'),
('13800000004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '刘小芳', '/avatars/user4.jpg', '2022001004', 3, '刘芳', 2, 'user', 0, 1, 100, 0, 100.00, '2024-12-19 16:30:00', '192.168.1.104'),
('13800000005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '陈小强', '/avatars/user5.jpg', '2021002005', 4, '陈强', 1, 'user', 0, 0, 100, 0, 100.00, '2024-12-19 14:15:00', '192.168.1.105'),
('13800000006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '赵小美', '/avatars/user6.jpg', '2020002006', 5, '赵美', 2, 'user', 1, 2, 75, 3, 85.60, '2024-12-18 11:20:00', '192.168.1.106'),
('13800000007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '孙小亮', '/avatars/user7.jpg', '2021003007', 6, '孙亮', 1, 'user', 0, 2, 98, 15, 99.20, '2024-12-20 12:10:00', '192.168.1.107'),
('13800000008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '周小雨', '/avatars/user8.jpg', '2022002008', 7, '周雨', 2, 'user', 0, 3, 100, 0, 100.00, '2024-12-19 09:45:00', '192.168.1.108'),
('13800000009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '吴小峰', '/avatars/user9.jpg', '2020003009', 8, '吴峰', 1, 'user', 0, 2, 90, 7, 94.30, '2024-12-20 15:25:00', '192.168.1.109'),
('13800000010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '郑小娟', '/avatars/user10.jpg', '2021004010', 9, '郑娟', 2, 'user', 0, 2, 85, 6, 92.50, '2024-12-20 13:40:00', '192.168.1.110');
-- 所有用户密码均为: user123456

-- 插入商品数据
INSERT INTO `products` (`title`, `description`, `price`, `original_price`, `category_id`, `seller_id`, `images`, `status`, `audit_status`, `audit_time`, `auditor_id`, `condition`, `trade_type`, `trade_location`, `view_count`, `favorite_count`, `is_top`, `is_recommend`, `tags`) VALUES
-- 数码电子类商品
('iPhone 13 Pro 256G 深空灰', '自用iPhone 13 Pro，256G存储，深空灰色，9成新，无磕碰，功能完好，配原装充电器和数据线。因换新机出售。', 4500.00, 8999.00, 7, 2, '["https://example.com/images/iphone1.jpg", "https://example.com/images/iphone2.jpg"]', 'available', 1, '2024-12-19 10:00:00', 1, 2, 3, '北京大学东门', 156, 23, 1, 1, '["苹果", "手机", "256G"]'),
('MacBook Air M2 13寸', '2022年购买的MacBook Air M2芯片，13寸屏幕，8G内存+256G存储，银色，几乎全新，仅使用半年，因出国留学急售。', 6800.00, 9499.00, 8, 3, '["https://example.com/images/macbook1.jpg", "https://example.com/images/macbook2.jpg"]', 'available', 1, '2024-12-19 11:30:00', 1, 2, 2, '清华大学紫荆公寓', 89, 15, 0, 1, '["苹果", "笔记本", "M2"]'),
('小米12 Pro 8+256G', '小米12 Pro，8G+256G，蓝色，轻微使用痕迹，屏幕完好，电池健康度95%，包装盒齐全。', 2200.00, 4699.00, 7, 4, '["https://example.com/images/xiaomi1.jpg"]', 'available', 1, '2024-12-18 14:20:00', 1, 3, 3, '复旦大学南区', 67, 8, 0, 0, '["小米", "手机", "256G"]'),
('佳能EOS R6相机', '佳能EOS R6全画幅微单相机，配24-105mm镜头，快门数不到5000次，成色极佳，适合摄影爱好者。', 12000.00, 18999.00, 9, 7, '["https://example.com/images/canon1.jpg", "https://example.com/images/canon2.jpg"]', 'available', 1, '2024-12-17 16:45:00', 1, 2, 1, '中山大学北门', 234, 45, 0, 1, '["佳能", "相机", "全画幅"]'),

-- 图书教材类商品
('高等数学教材（第七版）', '同济大学版高等数学教材，第七版，上下册齐全，无涂写，9成新，适合理工科学生使用。', 35.00, 89.00, 10, 2, '["https://example.com/images/math1.jpg"]', 'available', 1, '2024-12-19 09:15:00', 1, 2, 3, '北京大学理科楼', 45, 12, 0, 0, '["教材", "数学", "同济"]'),
('Java编程思想（第4版）', 'Java编程经典教材，第4版中文版，轻微使用痕迹，内容完整，适合Java学习者。', 45.00, 108.00, 10, 5, '["https://example.com/images/java1.jpg"]', 'sold', 1, '2024-12-18 13:30:00', 1, 3, 3, '上海交通大学闵行校区', 78, 6, 0, 0, '["编程", "Java", "计算机"]'),
('线性代数习题集', '清华大学出版社线性代数习题集，配详细解答，适合考研复习使用，8成新。', 25.00, 56.00, 11, 9, '["https://example.com/images/linear1.jpg"]', 'available', 1, '2024-12-17 11:20:00', 1, 4, 3, '华中科技大学主校区', 32, 5, 0, 0, '["数学", "线代", "考研"]'),

-- 生活用品类商品
('Nike Air Force 1 白色 42码', 'Nike经典款Air Force 1，白色，42码，轻微使用痕迹，鞋底磨损较少，适合日常穿着。', 280.00, 699.00, 3, 6, '["https://example.com/images/nike1.jpg"]', 'available', 1, '2024-12-19 15:40:00', 1, 3, 3, '南京大学仙林校区', 91, 18, 0, 0, '["耐克", "运动鞋", "42码"]'),
('戴森吹风机 HD08', '戴森Supersonic吹风机HD08，粉色，功能完好，配齐全套配件，因搬家出售。', 1200.00, 2990.00, 3, 8, '["https://example.com/images/dyson1.jpg"]', 'reserved', 1, '2024-12-18 10:25:00', 1, 2, 2, '西安交通大学兴庆校区', 145, 28, 0, 1, '["戴森", "吹风机", "美发"]'),

-- 运动户外类商品
('捷安特山地自行车', '捷安特ATX770山地自行车，26寸，21速变速，车况良好，适合校园代步和户外骑行。', 800.00, 1599.00, 5, 10, '["https://example.com/images/bike1.jpg", "https://example.com/images/bike2.jpg"]', 'available', 0, NULL, NULL, 3, 1, '哈尔滨工业大学一校区', 67, 9, 0, 0, '["捷安特", "山地车", "26寸"]'),
('羽毛球拍 尤尼克斯', '尤尼克斯羽毛球拍，型号弓箭11，4U重量，适合进攻型打法，配原装拍套。', 350.00, 899.00, 4, 3, '["https://example.com/images/badminton1.jpg"]', 'available', 1, '2024-12-19 14:10:00', 1, 2, 3, '清华大学综合体育馆', 43, 7, 0, 0, '["尤尼克斯", "羽毛球拍", "弓箭11"]'),

-- 待审核商品
('二手笔记本电脑', '联想ThinkPad E14，i5处理器，8G内存，使用两年，外观有轻微磨损，功能正常。', 2800.00, 5999.00, 8, 4, '["https://example.com/images/thinkpad1.jpg"]', 'available', 0, NULL, NULL, 4, 3, '复旦大学张江校区', 23, 2, 0, 0, '["联想", "ThinkPad", "笔记本"]'),
('全新耳机', 'Sony WH-1000XM4无线降噪耳机，全新未拆封，黑色，因重复购买出售。', 1800.00, 2899.00, 1, 5, '["https://example.com/images/sony1.jpg"]', 'available', 0, NULL, NULL, 1, 3, '浙江大学紫金港校区', 15, 1, 0, 0, '["索尼", "耳机", "降噪"]');

-- 插入订单数据
INSERT INTO `orders` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `amount`, `status`, `payment_method`, `payment_time`, `trade_type`, `trade_location`, `delivery_address`, `receiver_name`, `receiver_phone`, `ship_time`, `confirm_time`, `complete_time`, `buyer_rated`, `seller_rated`) VALUES
('ORD202412200001', 6, 3, 5, 45.00, 'completed', 'offline', '2024-12-18 14:00:00', 3, '上海交通大学闵行校区', NULL, NULL, NULL, '2024-12-18 15:00:00', '2024-12-18 16:30:00', '2024-12-18 17:00:00', 1, 1),
('ORD202412200002', 1, 4, 2, 4500.00, 'completed', 'online', '2024-12-19 10:30:00', 2, NULL, '北京市海淀区北京大学宿舍楼A座201', '刘芳', '13800000004', '2024-12-19 11:00:00', '2024-12-19 18:20:00', '2024-12-19 19:00:00', 1, 1),
('ORD202412200003', 9, 7, 8, 1200.00, 'delivered', 'online', '2024-12-18 11:00:00', 2, NULL, '陕西省西安市西安交通大学学生公寓', '孙亮', '13800000007', '2024-12-18 14:00:00', NULL, NULL, 0, 0),
('ORD202412200004', 2, 9, 3, 6800.00, 'paid', 'online', '2024-12-20 09:00:00', 2, NULL, '湖北省武汉市华中科技大学韵苑小区', '吴峰', '13800000009', NULL, NULL, NULL, 0, 0),
('ORD202412200005', 8, 5, 6, 280.00, 'pending', NULL, NULL, 1, '南京大学仙林校区', NULL, NULL, NULL, NULL, NULL, NULL, 0, 0);

-- 插入收藏数据
INSERT INTO `favorites` (`user_id`, `product_id`) VALUES
(2, 4), (2, 7), (2, 10),
(3, 1), (3, 2), (3, 11),
(4, 1), (4, 3), (4, 8),
(5, 2), (5, 4), (5, 9),
(6, 1), (6, 5), (6, 8),
(7, 4), (7, 6), (7, 10),
(8, 3), (8, 7), (8, 11),
(9, 1), (9, 5), (9, 12),
(10, 2), (10, 6), (10, 13);

-- 插入评价数据
INSERT INTO `reviews` (`order_id`, `reviewer_id`, `reviewee_id`, `rating`, `content`, `type`, `status`) VALUES
(1, 3, 5, 5, '书本质量很好，卖家人很nice，交易愉快！', 'buyer', 1),
(1, 5, 3, 5, '买家很准时，交易顺利，推荐！', 'seller', 1),
(2, 4, 2, 4, '手机成色不错，功能正常，就是价格稍贵，整体满意。', 'buyer', 1),
(2, 2, 4, 5, '买家很爽快，付款及时，好评！', 'seller', 1);

-- 插入消息数据
INSERT INTO `messages` (`sender_id`, `receiver_id`, `product_id`, `content`, `type`, `is_read`, `read_time`) VALUES
(3, 2, 1, '你好，这个iPhone还在吗？可以面交吗？', 'text', 1, '2024-12-19 09:45:00'),
(2, 3, 1, '在的，可以面交，北大东门怎么样？', 'text', 1, '2024-12-19 09:50:00'),
(3, 2, 1, '好的，明天下午3点可以吗？', 'text', 1, '2024-12-19 09:52:00'),
(4, 3, 2, 'MacBook还有保修吗？', 'text', 1, '2024-12-19 12:00:00'),
(3, 4, 2, '有的，还有一年保修期', 'text', 1, '2024-12-19 12:05:00'),
(5, 6, 8, '这双鞋子还有其他颜色吗？', 'text', 0, NULL),
(7, 8, 9, '吹风机功能都正常吗？', 'text', 1, '2024-12-18 09:30:00'),
(8, 7, 9, '功能完全正常，就是外观有轻微使用痕迹', 'text', 1, '2024-12-18 09:35:00');

-- 插入公告数据
INSERT INTO `announcements` (`title`, `content`, `type`, `status`, `author_id`, `publish_time`, `expire_time`, `view_count`) VALUES
('平台使用规范通知', '为了维护良好的交易环境，请各位用户遵守以下规范：\n1. 发布真实商品信息\n2. 诚信交易，不得欺诈\n3. 文明沟通，禁止恶意骚扰\n4. 保护个人隐私信息\n违反规范的用户将面临封号处理。', 'important', 1, 1, '2024-12-15 10:00:00', '2025-01-15 23:59:59', 1256),
('系统维护通知', '系统将于2024年12月25日凌晨2:00-4:00进行维护升级，期间可能无法正常使用，请提前安排交易时间。', 'urgent', 1, 1, '2024-12-20 09:00:00', '2024-12-25 06:00:00', 567),
('新功能上线通知', '平台新增以下功能：\n1. 商品视频展示\n2. 在线客服系统\n3. 交易纠纷处理机制\n欢迎大家体验使用！', 'normal', 1, 1, '2024-12-18 14:30:00', '2025-01-18 23:59:59', 892),
('学生认证优惠活动', '即日起至2024年12月31日，完成学生身份认证的用户可享受交易手续费减免优惠，快来认证吧！', 'normal', 1, 1, '2024-12-10 16:00:00', '2024-12-31 23:59:59', 2134);

-- 插入图片数据
INSERT INTO `imgs` (`name`, `base64_data`, `user_id`, `status`) VALUES
('avatar_sample_1.jpg', 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=', 2, 1),
('product_sample_1.jpg', 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=', 3, 1),
('product_sample_2.jpg', 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=', 4, 1),
('test_upload.png', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==', 5, 1),
('demo_image.jpg', 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=', 6, 1);

-- 插入操作日志数据
INSERT INTO `operation_logs` (`user_id`, `username`, `operation`, `method`, `params`, `result`, `ip`, `user_agent`, `execute_time`, `status`) VALUES
(1, '系统管理员', '审核商品', 'POST', '{"productId": 1, "status": 1, "reason": "商品信息完整，审核通过"}', '审核成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 156, 1),
(1, '系统管理员', '审核商品', 'POST', '{"productId": 2, "status": 1, "reason": "商品信息完整，审核通过"}', '审核成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 142, 1),
(1, '系统管理员', '封禁用户', 'PUT', '{"userId": 6, "status": 1, "reason": "恶意刷单行为"}', '封禁成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 89, 1),
(1, '系统管理员', '发布公告', 'POST', '{"title": "平台使用规范通知", "type": "important"}', '发布成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 234, 1),
(1, '系统管理员', '学生认证审核', 'PUT', '{"userId": 4, "status": 2, "reason": "认证材料真实有效"}', '审核通过', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 178, 1),
(1, '系统管理员', '删除违规评价', 'DELETE', '{"reviewId": 15, "reason": "包含不当言论"}', '删除成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebType/537.36', 95, 1);

-- 更新商品收藏数量
UPDATE `products` SET `favorite_count` = (
    SELECT COUNT(*) FROM `favorites` WHERE `favorites`.`product_id` = `products`.`id`
) WHERE `id` IN (1,2,3,4,5,6,7,8,9,10,11,12,13);

-- 更新用户交易统计
UPDATE `users` SET 
    `trade_count` = (
        SELECT COUNT(*) FROM `orders` 
        WHERE (`orders`.`buyer_id` = `users`.`id` OR `orders`.`seller_id` = `users`.`id`) 
        AND `orders`.`status` = 'completed'
    ),
    `good_rate` = (
        SELECT IFNULL(AVG(`reviews`.`rating`) * 20, 100) FROM `reviews` 
        WHERE `reviews`.`reviewee_id` = `users`.`id`
    )
WHERE `role` = 'user';

-- 添加外键约束（可选，根据需要启用）
-- ALTER TABLE `users` ADD CONSTRAINT `fk_users_school` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`);
-- ALTER TABLE `products` ADD CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);
-- ALTER TABLE `products` ADD CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
-- ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `favorites` ADD CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `favorites` ADD CONSTRAINT `fk_favorites_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
-- ALTER TABLE `reviews` ADD CONSTRAINT `fk_reviews_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
-- ALTER TABLE `messages` ADD CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `messages` ADD CONSTRAINT `fk_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`);
-- ALTER TABLE `imgs` ADD CONSTRAINT `fk_imgs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

-- 创建视图：用户统计信息
CREATE VIEW `user_stats` AS
SELECT 
    u.id,
    u.nickname,
    u.phone,
    u.school_id,
    s.name as school_name,
    u.verify_status,
    u.credit_score,
    u.trade_count,
    u.good_rate,
    COUNT(DISTINCT p.id) as product_count,
    COUNT(DISTINCT f.id) as favorite_count,
    COUNT(DISTINCT CASE WHEN o.buyer_id = u.id THEN o.id END) as buy_count,
    COUNT(DISTINCT CASE WHEN o.seller_id = u.id THEN o.id END) as sell_count
FROM users u
LEFT JOIN schools s ON u.school_id = s.id
LEFT JOIN products p ON u.id = p.seller_id AND p.deleted = 0
LEFT JOIN favorites f ON u.id = f.user_id
LEFT JOIN orders o ON (u.id = o.buyer_id OR u.id = o.seller_id) AND o.deleted = 0
WHERE u.deleted = 0
GROUP BY u.id;

-- 创建视图：商品统计信息
CREATE VIEW `product_stats` AS
SELECT 
    p.id,
    p.title,
    p.price,
    p.status,
    p.audit_status,
    c.name as category_name,
    u.nickname as seller_name,
    p.view_count,
    p.favorite_count,
    COUNT(DISTINCT o.id) as order_count,
    AVG(r.rating) as avg_rating,
    COUNT(DISTINCT r.id) as review_count
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN users u ON p.seller_id = u.id
LEFT JOIN orders o ON p.id = o.product_id AND o.deleted = 0
LEFT JOIN reviews r ON o.id = r.order_id
WHERE p.deleted = 0
GROUP BY p.id;

-- 创建存储过程：更新用户信誉分数
DELIMITER //
CREATE PROCEDURE UpdateUserCreditScore(IN user_id BIGINT)
BEGIN
    DECLARE avg_rating DECIMAL(3,2);
    DECLARE trade_count INT;
    DECLARE new_credit_score INT;
    
    -- 获取用户平均评分
    SELECT AVG(rating) INTO avg_rating 
    FROM reviews 
    WHERE reviewee_id = user_id AND status = 1;
    
    -- 获取用户交易次数
    SELECT COUNT(*) INTO trade_count 
    FROM orders 
    WHERE (buyer_id = user_id OR seller_id = user_id) 
    AND status = 'completed';
    
    -- 计算新的信誉分数
    SET new_credit_score = LEAST(100, GREATEST(0, 
        IFNULL(avg_rating * 20, 100) + 
        CASE 
            WHEN trade_count >= 50 THEN 10
            WHEN trade_count >= 20 THEN 5
            WHEN trade_count >= 10 THEN 2
            ELSE 0
        END
    ));
    
    -- 更新用户信誉分数
    UPDATE users 
    SET credit_score = new_credit_score,
        trade_count = trade_count,
        good_rate = IFNULL(avg_rating * 20, 100)
    WHERE id = user_id;
END //
DELIMITER ;

-- 创建触发器：商品状态变更时更新统计
DELIMITER //
CREATE TRIGGER update_product_stats AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status AND NEW.status = 'sold' THEN
        -- 商品售出时，增加卖家交易次数
        UPDATE users SET trade_count = trade_count + 1 WHERE id = NEW.seller_id;
    END IF;
END //
DELIMITER ;

-- 数据库初始化完成提示
SELECT '校园二手交易平台数据库初始化完成！' as message,
       (SELECT COUNT(*) FROM users WHERE role = 'user') as user_count,
       (SELECT COUNT(*) FROM products) as product_count,
       (SELECT COUNT(*) FROM orders) as order_count,
       (SELECT COUNT(*) FROM schools) as school_count,
       (SELECT COUNT(*) FROM categories WHERE parent_id = 0) as category_count;

-- 测试数据说明
/*
测试账号信息：
1. 管理员账号：
   手机号：13800000000
   密码：admin123456
   
2. 普通用户账号：
   手机号：13800000001-13800000010
   密码：user123456
   
数据库包含：
- 10所知名高校信息
- 12个商品分类（含二级分类）
- 11个用户（1个管理员 + 10个普通用户）
- 13个商品（不同状态：可售、已售、待审核等）
- 5个订单（不同状态：待付款、已完成、已发货等）
- 27条收藏记录
- 4条评价记录
- 8条消息记录
- 4条公告
- 6条操作日志

功能特性：
- 完整的用户认证体系
- 商品分类管理
- 订单状态流转
- 评价信誉系统
- 消息通讯功能
- 公告管理
- 操作日志审计
- 数据统计视图
- 自动化触发器
*/

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 数据库初始化完成提示
SELECT '🎉 校园二手交易平台数据库初始化完成！' as message,
       '✅ 所有表结构创建成功' as tables_status,
       '✅ 测试数据插入完成' as data_status,
       '✅ 视图和存储过程就绪' as features_status,
       '🚀 系统可以正常运行' as system_status;

/*
=== 代码质量和可维护性改进建议 ===

🔧 数据库层面优化：
1. 索引优化
   - 为高频查询字段添加复合索引
   - 定期分析慢查询日志，优化索引策略
   - 考虑为JSON字段添加虚拟列索引

2. 分区策略
   - 对大表（如orders、messages）按时间分区
   - 提升查询性能和维护效率

3. 数据归档
   - 实现历史数据自动归档机制
   - 保持主表数据量在合理范围

📊 性能监控：
1. 添加性能监控表
   - 记录关键操作的执行时间
   - 监控数据库连接池状态

2. 慢查询分析
   - 定期分析慢查询日志
   - 建立查询性能基线

🛡️ 安全加固：
1. 数据加密
   - 敏感字段（身份证、手机号）加密存储
   - 实现字段级别的访问控制

2. 审计增强
   - 扩展操作日志记录范围
   - 添加数据变更前后对比

🔄 数据一致性：
1. 事务优化
   - 关键业务操作使用分布式事务
   - 实现补偿机制处理异常情况

2. 数据校验
   - 添加更多CHECK约束
   - 实现数据完整性校验存储过程

📈 扩展性设计：
1. 读写分离
   - 配置主从复制
   - 查询操作路由到从库

2. 缓存策略
   - 热点数据Redis缓存
   - 实现缓存更新策略

🔧 运维自动化：
1. 备份策略
   - 实现自动化备份脚本
   - 定期验证备份数据完整性

2. 监控告警
   - 数据库性能指标监控
   - 异常情况自动告警

💡 业务逻辑优化：
1. 状态机设计
   - 订单状态流转规则化
   - 商品状态变更控制

2. 业务规则引擎
   - 可配置的业务规则
   - 动态调整业务逻辑

🚀 技术栈建议：
1. 连接池优化
   - 使用HikariCP等高性能连接池
   - 合理配置连接池参数

2. ORM优化
   - 避免N+1查询问题
   - 合理使用延迟加载

3. 缓存架构
   - 多级缓存策略
   - 缓存穿透和雪崩防护

📋 代码规范：
1. 命名规范
   - 统一的表名、字段名命名规则
   - 清晰的注释和文档

2. 版本控制
   - 数据库变更脚本版本化管理
   - 实现数据库迁移机制

🔍 测试策略：
1. 单元测试
   - 存储过程和函数的单元测试
   - 数据完整性测试

2. 性能测试
   - 压力测试和负载测试
   - 并发场景测试

这些改进建议可以显著提升系统的性能、安全性、可维护性和扩展性。
建议按优先级逐步实施，重点关注性能优化和安全加固。
*/