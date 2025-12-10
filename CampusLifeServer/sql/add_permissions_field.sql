-- 给 sys_role 表添加 permissions 字段
ALTER TABLE `sys_role` ADD COLUMN `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权限列表(JSON格式)' AFTER `description`;
