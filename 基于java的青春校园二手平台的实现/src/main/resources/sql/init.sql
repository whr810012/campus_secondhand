-- 青春校园二手平台数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_secondhand DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_secondhand;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE COMMENT '生日',
    `school` VARCHAR(100) COMMENT '学校',
    `major` VARCHAR(100) COMMENT '专业',
    `grade` VARCHAR(20) COMMENT '年级',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员，2-超级管理员',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` TEXT COMMENT '分类描述',
    `icon` VARCHAR(255) COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    `title` VARCHAR(200) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `original_price` DECIMAL(10,2) COMMENT '原价',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `images` TEXT COMMENT '商品图片，JSON格式存储',
    `condition_desc` VARCHAR(100) COMMENT '新旧程度描述',
    `trade_type` TINYINT DEFAULT 0 COMMENT '交易方式：0-线下，1-线上，2-都支持',
    `location` VARCHAR(200) COMMENT '交易地点',
    `contact_info` VARCHAR(500) COMMENT '联系方式',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已上架，2-已下架，3-已售出，4-审核拒绝',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `audit_time` DATETIME COMMENT '审核时间',
    `audit_user_id` BIGINT COMMENT '审核人ID',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
    `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
    `product_title` VARCHAR(200) NOT NULL COMMENT '商品标题',
    `product_image` VARCHAR(255) COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '交易价格',
    `trade_type` TINYINT NOT NULL COMMENT '交易方式：0-线下，1-线上',
    `delivery_address` TEXT COMMENT '收货地址',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态：0-待确认，1-已确认，2-交易中，3-已完成，4-已取消',
    `remark` VARCHAR(500) COMMENT '备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_buyer_id` (`buyer_id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 用户地址表
CREATE TABLE IF NOT EXISTS `user_address` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 系统公告表
CREATE TABLE IF NOT EXISTS `notice` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` TINYINT DEFAULT 0 COMMENT '公告类型：0-系统公告，1-活动公告',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_user_id` BIGINT NOT NULL COMMENT '创建人ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- 插入初始数据

-- 插入超级管理员
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTY.5zt6v.6YpKkdJoTKjKKjTh6', 'admin@campus.com', '超级管理员', 2, 1);

-- 插入默认分类
INSERT INTO `category` (`name`, `description`, `sort_order`) VALUES
('电子产品', '手机、电脑、数码相机等电子设备', 1),
('图书教材', '教科书、参考书、小说等各类书籍', 2),
('生活用品', '日用品、化妆品、家居用品等', 3),
('服装鞋帽', '衣服、鞋子、帽子、包包等', 4),
('运动健身', '运动器材、健身用品、体育用品等', 5),
('其他', '其他未分类商品', 99);

-- 插入系统公告
INSERT INTO `notice` (`title`, `content`, `type`, `created_user_id`) VALUES
('欢迎使用青春校园二手平台', '欢迎大家使用青春校园二手平台！在这里你可以买到心仪的二手商品，也可以出售自己闲置的物品。让我们一起打造绿色环保的校园交易环境！', 0, 1),
('平台交易规则', '为了保障大家的交易安全，请遵守以下规则：1.诚信交易，如实描述商品；2.线下交易请选择安全的交易地点；3.发现问题及时联系管理员。', 0, 1);