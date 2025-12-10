-- 维修人员表
CREATE TABLE IF NOT EXISTS `technicians` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `specialty` VARCHAR(50) COMMENT '专长类型: 电路(Electric)/水管(Water)/网络(Wifi)/家具(Furniture)/空调(AC)/其他(Other)',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: 在职(active)/离职(inactive)',
    `avg_rating` DECIMAL(3,2) DEFAULT 5.00 COMMENT '平均评分(1-5分)',
    `completed_count` INT DEFAULT 0 COMMENT '累计完成订单数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修人员表';

-- 插入示例数据
INSERT INTO `technicians` (`name`, `phone`, `specialty`, `status`) VALUES
('张师傅', '13800138001', 'Electric', 'active'),
('李师傅', '13800138002', 'Water', 'active'),
('王师傅', '13800138003', 'AC', 'active'),
('赵师傅', '13800138004', 'Wifi', 'active'),
('刘师傅', '13800138005', 'Furniture', 'active');
