-- 二手商品分类表
-- 表名: market_categories

-- 插入初始分类数据
INSERT INTO market_categories (name, icon, sort_order, status, create_time, update_time) VALUES
('数码产品', 'Monitor', 1, 1, NOW(), NOW()),
('书籍教材', 'Reading', 2, 1, NOW(), NOW()),
('生活用品', 'HomeFilled', 3, 1, NOW(), NOW()),
('衣物鞋包', 'Goods', 4, 1, NOW(), NOW()),
('运动户外', 'Basketball', 5, 1, NOW(), NOW()),
('美妆护肤', 'MagicStick', 6, 1, NOW(), NOW()),
('食品零食', 'Food', 7, 1, NOW(), NOW()),
('其他', 'More', 99, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();
