-- 为messages表添加deleted字段
ALTER TABLE `messages` ADD COLUMN `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除' AFTER `created_at`;

-- 为deleted字段添加索引
CREATE INDEX `idx_deleted` ON `messages` (`deleted`);