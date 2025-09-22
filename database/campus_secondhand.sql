/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : campus_secondhand

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 22/09/2025 10:56:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '公告类型：normal-普通，important-重要，urgent-紧急',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `author_id` bigint NOT NULL COMMENT '发布人ID',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '平台使用规范通知', '为了维护良好的交易环境，请各位用户遵守以下规范：\n1. 发布真实商品信息\n2. 诚信交易，不得欺诈\n3. 文明沟通，禁止恶意骚扰\n4. 保护个人隐私信息\n违反规范的用户将面临封号处理。', 'important', 1, 1, '2024-12-15 10:00:00', '2025-01-15 23:59:59', 1256, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `announcements` VALUES (2, '系统维护通知', '系统将于2024年12月25日凌晨2:00-4:00进行维护升级，期间可能无法正常使用，请提前安排交易时间。', 'urgent', 1, 1, '2024-12-20 09:00:00', '2024-12-25 06:00:00', 567, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `announcements` VALUES (3, '新功能上线通知', '平台新增以下功能：\n1. 商品视频展示\n2. 在线客服系统\n3. 交易纠纷处理机制\n欢迎大家体验使用！', 'normal', 1, 1, '2024-12-18 14:30:00', '2025-01-18 23:59:59', 892, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `announcements` VALUES (4, '学生认证优惠活动', '即日起至2024年12月31日，完成学生身份认证的用户可享受交易手续费减免优惠，快来认证吧！', 'normal', 1, 1, '2024-12-10 16:00:00', '2024-12-31 23:59:59', 2134, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '数码电子', '手机、电脑、相机等数码产品', NULL, 0, 1, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (2, '图书教材', '教科书、参考书、小说等', NULL, 0, 2, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (3, '生活用品', '日用品、化妆品、服装等', NULL, 0, 3, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (4, '运动户外', '运动器材、户外用品等', NULL, 0, 4, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (5, '交通工具', '自行车、电动车等', NULL, 0, 5, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (6, '其他', '其他类别商品', NULL, 0, 6, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (7, '手机', '智能手机、功能机等', NULL, 1, 1, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (8, '电脑', '笔记本电脑、台式机、平板等', NULL, 1, 2, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (9, '相机', '数码相机、摄像设备等', NULL, 1, 3, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (10, '教科书', '各专业教科书', NULL, 2, 1, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (11, '参考书', '考试参考书、工具书等', NULL, 2, 2, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `categories` VALUES (12, '小说文学', '小说、散文、诗歌等', NULL, 2, 3, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '电子产品', '手机、电脑、数码相机等电子设备', NULL, 1, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `category` VALUES (2, '图书教材', '教科书、参考书、小说等各类书籍', NULL, 2, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `category` VALUES (3, '生活用品', '日用品、化妆品、家居用品等', NULL, 3, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `category` VALUES (4, '服装鞋帽', '衣服、鞋子、帽子、包包等', NULL, 4, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `category` VALUES (5, '运动健身', '运动器材、健身用品、体育用品等', NULL, 5, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `category` VALUES (6, '其他', '其他未分类商品', NULL, 99, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorites
-- ----------------------------
INSERT INTO `favorites` VALUES (1, 2, 4, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (2, 2, 7, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (3, 2, 10, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (4, 3, 1, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (5, 3, 2, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (6, 3, 11, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (7, 4, 1, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (8, 4, 3, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (9, 4, 8, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (10, 5, 2, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (11, 5, 4, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (12, 5, 9, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (13, 6, 1, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (14, 6, 5, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (15, 6, 8, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (16, 7, 4, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (17, 7, 6, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (18, 7, 10, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (19, 8, 3, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (20, 8, 7, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (21, 8, 11, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (22, 9, 1, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (23, 9, 5, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (24, 9, 12, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (25, 10, 2, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (26, 10, 6, '2025-09-18 16:28:20');
INSERT INTO `favorites` VALUES (27, 10, 13, '2025-09-18 16:28:20');

-- ----------------------------
-- Table structure for imgs
-- ----------------------------
DROP TABLE IF EXISTS `imgs`;
CREATE TABLE `imgs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片名称',
  `base64_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片base64数据',
  `user_id` bigint NOT NULL COMMENT '上传用户ID',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-删除，1-正常',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of imgs
-- ----------------------------

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'text' COMMENT '消息类型：text-文本，image-图片，system-系统消息',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of messages
-- ----------------------------

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` tinyint NULL DEFAULT 0 COMMENT '公告类型：0-系统公告，1-活动公告',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `created_user_id` bigint NOT NULL COMMENT '创建人ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '欢迎使用青春校园二手平台', '欢迎大家使用青春校园二手平台！在这里你可以买到心仪的二手商品，也可以出售自己闲置的物品。让我们一起打造绿色环保的校园交易环境！', 0, 1, 0, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);
INSERT INTO `notice` VALUES (2, '平台交易规则', '为了保障大家的交易安全，请遵守以下规则：1.诚信交易，如实描述商品；2.线下交易请选择安全的交易地点；3.发现问题及时联系管理员。', 0, 1, 0, 1, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);

-- ----------------------------
-- Table structure for operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作结果',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `execute_time` int NULL DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_operation`(`operation` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_logs
-- ----------------------------
INSERT INTO `operation_logs` VALUES (1, 1, '系统管理员', '审核商品', 'POST', '{\"productId\": 1, \"status\": 1, \"reason\": \"商品信息完整，审核通过\"}', '审核成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 156, 1, NULL, '2025-09-18 16:28:20');
INSERT INTO `operation_logs` VALUES (2, 1, '系统管理员', '审核商品', 'POST', '{\"productId\": 2, \"status\": 1, \"reason\": \"商品信息完整，审核通过\"}', '审核成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 142, 1, NULL, '2025-09-18 16:28:20');
INSERT INTO `operation_logs` VALUES (3, 1, '系统管理员', '封禁用户', 'PUT', '{\"userId\": 6, \"status\": 1, \"reason\": \"恶意刷单行为\"}', '封禁成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 89, 1, NULL, '2025-09-18 16:28:20');
INSERT INTO `operation_logs` VALUES (4, 1, '系统管理员', '发布公告', 'POST', '{\"title\": \"平台使用规范通知\", \"type\": \"important\"}', '发布成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 234, 1, NULL, '2025-09-18 16:28:20');
INSERT INTO `operation_logs` VALUES (5, 1, '系统管理员', '学生认证审核', 'PUT', '{\"userId\": 4, \"status\": 2, \"reason\": \"认证材料真实有效\"}', '审核通过', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 178, 1, NULL, '2025-09-18 16:28:20');
INSERT INTO `operation_logs` VALUES (6, 1, '系统管理员', '删除违规评价', 'DELETE', '{\"reviewId\": 15, \"reason\": \"包含不当言论\"}', '删除成功', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebType/537.36', 95, 1, NULL, '2025-09-18 16:28:20');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `product_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '交易价格',
  `trade_type` tinyint NOT NULL COMMENT '交易方式：0-线下，1-线上',
  `delivery_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '收货地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NULL DEFAULT 0 COMMENT '订单状态：0-待确认，1-已确认，2-交易中，3-已完成，4-已取消',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '订单状态：pending-待付款，paid-已付款，shipped-已发货，delivered-已送达，completed-已完成，cancelled-已取消，refunded-已退款',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：online-线上支付，offline-线下支付',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `payment_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付流水号',
  `trade_type` tinyint(1) NULL DEFAULT 3 COMMENT '交易方式：1-仅线下，2-仅线上，3-线上线下均可',
  `trade_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易地点',
  `delivery_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认收货时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `buyer_rated` tinyint(1) NULL DEFAULT 0 COMMENT '买家评价状态：0-未评价，1-已评价',
  `seller_rated` tinyint(1) NULL DEFAULT 0 COMMENT '卖家评价状态：0-未评价，1-已评价',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD202412200001', 6, 3, 5, 45.00, 'completed', 'offline', '2024-12-18 14:00:00', NULL, 3, '上海交通大学闵行校区', NULL, NULL, NULL, '2024-12-18 15:00:00', '2024-12-18 16:30:00', '2024-12-18 17:00:00', NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `orders` VALUES (2, 'ORD202412200002', 1, 4, 2, 4500.00, 'completed', 'online', '2024-12-19 10:30:00', NULL, 2, NULL, '北京市海淀区北京大学宿舍楼A座201', '刘芳', '13800000004', '2024-12-19 11:00:00', '2024-12-19 18:20:00', '2024-12-19 19:00:00', NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `orders` VALUES (3, 'ORD202412200003', 9, 7, 8, 1200.00, 'delivered', 'online', '2024-12-18 11:00:00', NULL, 2, NULL, '陕西省西安市西安交通大学学生公寓', '孙亮', '13800000007', '2024-12-18 14:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `orders` VALUES (4, 'ORD202412200004', 2, 9, 3, 6800.00, 'paid', 'online', '2024-12-20 09:00:00', NULL, 2, NULL, '湖北省武汉市华中科技大学韵苑小区', '吴峰', '13800000009', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `orders` VALUES (5, 'ORD202412200005', 8, 5, 6, 280.00, 'pending', NULL, NULL, NULL, 1, '南京大学仙林校区', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品图片，JSON格式存储',
  `condition_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新旧程度描述',
  `trade_type` tinyint NULL DEFAULT 0 COMMENT '交易方式：0-线下，1-线上，2-都支持',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易地点',
  `contact_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已上架，2-已下架，3-已售出，4-审核拒绝',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `images` json NULL COMMENT '商品图片，JSON数组格式',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'available' COMMENT '商品状态：available-可售，reserved-已预定，sold-已售出，unavailable-已下架',
  `audit_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `audit_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `condition` tinyint(1) NULL DEFAULT 1 COMMENT '商品成色：1-全新，2-几乎全新，3-轻微使用痕迹，4-明显使用痕迹，5-重度使用痕迹',
  `trade_type` tinyint(1) NULL DEFAULT 3 COMMENT '交易方式：1-仅线下，2-仅线上，3-线上线下均可',
  `trade_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易地点',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `top_expire_time` datetime NULL DEFAULT NULL COMMENT '置顶到期时间',
  `is_recommend` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐',
  `tags` json NULL COMMENT '标签，JSON数组格式',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_price`(`price` ASC) USING BTREE,
  FULLTEXT INDEX `ft_title_description`(`title`, `description`)
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `reviewer_id` bigint NOT NULL COMMENT '评价人ID',
  `reviewee_id` bigint NOT NULL COMMENT '被评价人ID',
  `rating` tinyint(1) NOT NULL COMMENT '评分：1-5分',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `images` json NULL COMMENT '评价图片',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价类型：buyer-买家评价，seller-卖家评价',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_reviewee_id`(`reviewee_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reviews
-- ----------------------------
INSERT INTO `reviews` VALUES (1, 1, 3, 5, 5, '书本质量很好，卖家人很nice，交易愉快！', NULL, 'buyer', 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20');
INSERT INTO `reviews` VALUES (2, 1, 5, 3, 5, '买家很准时，交易顺利，推荐！', NULL, 'seller', 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20');
INSERT INTO `reviews` VALUES (3, 2, 4, 2, 4, '手机成色不错，功能正常，就是价格稍贵，整体满意。', NULL, 'buyer', 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20');
INSERT INTO `reviews` VALUES (4, 2, 2, 4, 5, '买家很爽快，付款及时，好评！', NULL, 'seller', 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20');

-- ----------------------------
-- Table structure for schools
-- ----------------------------
DROP TABLE IF EXISTS `schools`;
CREATE TABLE `schools`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校代码',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_province_city`(`province` ASC, `city` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学校表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schools
-- ----------------------------
INSERT INTO `schools` VALUES (1, '北京大学', 'PKU', '北京市', '北京市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (2, '清华大学', 'THU', '北京市', '北京市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (3, '复旦大学', 'FDU', '上海市', '上海市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (4, '上海交通大学', 'SJTU', '上海市', '上海市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (5, '浙江大学', 'ZJU', '浙江省', '杭州市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (6, '南京大学', 'NJU', '江苏省', '南京市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (7, '中山大学', 'SYSU', '广东省', '广州市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (8, '华中科技大学', 'HUST', '湖北省', '武汉市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (9, '西安交通大学', 'XJTU', '陕西省', '西安市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `schools` VALUES (10, '哈尔滨工业大学', 'HIT', '黑龙江省', '哈尔滨市', NULL, 1, '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `role` tinyint NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员，2-超级管理员',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTY.5zt6v.6YpKkdJoTKjKKjTh6', 'admin@campus.com', NULL, '超级管理员', NULL, 0, NULL, NULL, NULL, NULL, 1, 2, '2025-09-16 10:53:42', '2025-09-16 10:53:42', 0);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `school_id` bigint NULL DEFAULT NULL COMMENT '学校ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色：user-普通用户，admin-管理员',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '账户状态：0-正常，1-禁用',
  `verify_status` tinyint(1) NULL DEFAULT 0 COMMENT '学生认证状态：0-未认证，1-认证中，2-已认证，3-认证失败',
  `verify_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '认证失败原因',
  `id_card_img_id` bigint NULL DEFAULT NULL COMMENT '身份证图片ID',
  `student_card_img_id` bigint NULL DEFAULT NULL COMMENT '学生证图片ID',
  `credit_score` int NULL DEFAULT 100 COMMENT '信誉分数',
  `trade_count` int NULL DEFAULT 0 COMMENT '交易次数',
  `good_rate` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '好评率',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_school_id`(`school_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_id_card_img_id`(`id_card_img_id` ASC) USING BTREE,
  INDEX `idx_student_card_img_id`(`student_card_img_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '13800000000', '123456', '系统管理员', '/avatars/admin.jpg', '666666666', 4, '666666666', NULL, 0, 'admin', 0, 1, NULL, 32, 33, 100, 0, 100.00, '2025-09-22 10:27:14', '192.168.1.100', '2025-09-18 16:28:20', '2025-09-18 16:53:28', 0);
INSERT INTO `users` VALUES (2, '13800000001', '123456', '张小明', '/avatars/user1.jpg', '2021001001', 1, '张明', NULL, 1, 'user', 0, 2, NULL, NULL, NULL, 95, 1, 80.00, '2025-09-22 10:37:25', '192.168.1.101', '2025-09-18 16:28:20', '2025-09-22 10:28:02', 0);
INSERT INTO `users` VALUES (3, '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '李小红', '/avatars/user2.jpg', '2021001002', 1, '李红', NULL, 2, 'user', 0, 2, NULL, NULL, NULL, 88, 1, 100.00, '2024-12-20 08:45:00', '192.168.1.102', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (4, '13800000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '王大华', '/avatars/user3.jpg', '2020001003', 2, '王华', NULL, 1, 'user', 0, 2, NULL, NULL, NULL, 92, 1, 100.00, '2024-12-20 07:20:00', '192.168.1.103', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (5, '13800000004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '刘小芳', '/avatars/user4.jpg', '2022001004', 3, '刘芳', NULL, 2, 'user', 0, 1, NULL, NULL, NULL, 100, 1, 100.00, '2024-12-19 16:30:00', '192.168.1.104', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (6, '13800000005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '陈小强', '/avatars/user5.jpg', '2021002005', 4, '陈强', NULL, 1, 'user', 0, 0, NULL, NULL, NULL, 100, 0, 100.00, '2024-12-19 14:15:00', '192.168.1.105', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (7, '13800000006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '赵小美', '/avatars/user6.jpg', '2020002006', 5, '赵美', NULL, 2, 'user', 1, 2, NULL, NULL, NULL, 75, 0, 100.00, '2024-12-18 11:20:00', '192.168.1.106', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (8, '13800000007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '孙小亮', '/avatars/user7.jpg', '2021003007', 6, '孙亮', NULL, 1, 'user', 0, 2, NULL, NULL, NULL, 98, 0, 100.00, '2024-12-20 12:10:00', '192.168.1.107', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (9, '13800000008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '周小雨', '/avatars/user8.jpg', '2022002008', 7, '周雨', NULL, 2, 'user', 0, 3, NULL, NULL, NULL, 100, 0, 100.00, '2024-12-19 09:45:00', '192.168.1.108', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (10, '13800000009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '吴小峰', '/avatars/user9.jpg', '2020003009', 8, '吴峰', NULL, 1, 'user', 0, 2, NULL, NULL, NULL, 90, 0, 100.00, '2024-12-20 15:25:00', '192.168.1.109', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);
INSERT INTO `users` VALUES (11, '13800000010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey', '郑小娟', '/avatars/user10.jpg', '2021004010', 9, '郑娟', NULL, 2, 'user', 0, 2, NULL, NULL, NULL, 85, 0, 100.00, '2024-12-20 13:40:00', '192.168.1.110', '2025-09-18 16:28:20', '2025-09-18 16:28:20', 0);

-- ----------------------------
-- View structure for product_stats
-- ----------------------------
DROP VIEW IF EXISTS `product_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `product_stats` AS select `p`.`id` AS `id`,`p`.`title` AS `title`,`p`.`price` AS `price`,`p`.`status` AS `status`,`p`.`audit_status` AS `audit_status`,`c`.`name` AS `category_name`,`u`.`nickname` AS `seller_name`,`p`.`view_count` AS `view_count`,`p`.`favorite_count` AS `favorite_count`,count(distinct `o`.`id`) AS `order_count`,avg(`r`.`rating`) AS `avg_rating`,count(distinct `r`.`id`) AS `review_count` from ((((`products` `p` left join `categories` `c` on((`p`.`category_id` = `c`.`id`))) left join `users` `u` on((`p`.`seller_id` = `u`.`id`))) left join `orders` `o` on(((`p`.`id` = `o`.`product_id`) and (`o`.`deleted` = 0)))) left join `reviews` `r` on((`o`.`id` = `r`.`order_id`))) where (`p`.`deleted` = 0) group by `p`.`id`;

-- ----------------------------
-- View structure for user_stats
-- ----------------------------
DROP VIEW IF EXISTS `user_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `user_stats` AS select `u`.`id` AS `id`,`u`.`nickname` AS `nickname`,`u`.`phone` AS `phone`,`u`.`school_id` AS `school_id`,`s`.`name` AS `school_name`,`u`.`verify_status` AS `verify_status`,`u`.`credit_score` AS `credit_score`,`u`.`trade_count` AS `trade_count`,`u`.`good_rate` AS `good_rate`,count(distinct `p`.`id`) AS `product_count`,count(distinct `f`.`id`) AS `favorite_count`,count(distinct (case when (`o`.`buyer_id` = `u`.`id`) then `o`.`id` end)) AS `buy_count`,count(distinct (case when (`o`.`seller_id` = `u`.`id`) then `o`.`id` end)) AS `sell_count` from ((((`users` `u` left join `schools` `s` on((`u`.`school_id` = `s`.`id`))) left join `products` `p` on(((`u`.`id` = `p`.`seller_id`) and (`p`.`deleted` = 0)))) left join `favorites` `f` on((`u`.`id` = `f`.`user_id`))) left join `orders` `o` on((((`u`.`id` = `o`.`buyer_id`) or (`u`.`id` = `o`.`seller_id`)) and (`o`.`deleted` = 0)))) where (`u`.`deleted` = 0) group by `u`.`id`;

-- ----------------------------
-- Procedure structure for UpdateUserCreditScore
-- ----------------------------
DROP PROCEDURE IF EXISTS `UpdateUserCreditScore`;
delimiter ;;
CREATE PROCEDURE `UpdateUserCreditScore`(IN user_id BIGINT)
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table products
-- ----------------------------
DROP TRIGGER IF EXISTS `update_product_stats`;
delimiter ;;
CREATE TRIGGER `update_product_stats` AFTER UPDATE ON `products` FOR EACH ROW BEGIN
    IF OLD.status != NEW.status AND NEW.status = 'sold' THEN
        -- 商品售出时，增加卖家交易次数
        UPDATE users SET trade_count = trade_count + 1 WHERE id = NEW.seller_id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
