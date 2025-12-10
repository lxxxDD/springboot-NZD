-- 更新超级管理员角色，添加所有权限
UPDATE `sys_role` 
SET `permissions` = '[
  "dashboard",
  "user:list",
  "user:verify",
  "service:repair",
  "service:canteen",
  "service:food",
  "service:food-order",
  "operation:secondhand",
  "operation:activity",
  "operation:content",
  "finance:recharge",
  "finance:transaction",
  "settings:admin",
  "settings:role",
  "settings:log"
]'
WHERE `name` = '超级管理员' OR `id` = 1;

-- 如果超级管理员角色不存在，则创建
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `permissions`, `status`, `create_time`, `update_time`)
SELECT 1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 
'[
  "dashboard",
  "user:list",
  "user:verify",
  "service:repair",
  "service:canteen",
  "service:food",
  "service:food-order",
  "operation:secondhand",
  "operation:activity",
  "operation:content",
  "finance:recharge",
  "finance:transaction",
  "settings:admin",
  "settings:role",
  "settings:log"
]',
1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `id` = 1);

-- 确保admin账号关联到超级管理员角色
UPDATE `sys_admin` 
SET `role_id` = 1 
WHERE `username` = 'admin';
