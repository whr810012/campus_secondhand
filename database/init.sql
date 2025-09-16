-- 校园二手交易平台数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_secondhand` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_secondhand`;

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

-- 插入默认管理员用户
INSERT INTO `users` (`phone`, `password`, `nickname`, `role`, `verify_status`) VALUES
('13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '系统管理员', 'admin', 2);
-- 密码为: admin123456