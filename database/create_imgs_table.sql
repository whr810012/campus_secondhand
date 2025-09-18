-- 创建图片表用于存储base64格式的图片数据
USE `campus_secondhand`;

-- 图片表
CREATE TABLE `imgs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `name` varchar(255) NOT NULL COMMENT '图片名称',
  `original_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小（字节）',
  `base64_data` longtext NOT NULL COMMENT 'base64编码的图片数据',
  `upload_user_id` bigint(20) DEFAULT NULL COMMENT '上传用户ID',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-删除，1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';