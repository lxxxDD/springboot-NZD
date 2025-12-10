-- =============================================
-- 校园新闻表
-- =============================================

-- 创建新闻表
CREATE TABLE IF NOT EXISTS `news` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
    `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '新闻摘要',
    `content` TEXT COMMENT '新闻内容',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `category` VARCHAR(50) DEFAULT 'notice' COMMENT '分类：notice-通知公告, academic-学术动态, campus-校园生活, activity-活动资讯',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者/来源',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `status` VARCHAR(20) DEFAULT 'published' COMMENT '状态：draft-草稿, published-已发布, archived-已归档',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校园新闻表';

-- =============================================
-- 插入示例数据
-- =============================================

INSERT INTO `news` (`title`, `summary`, `content`, `cover_image`, `category`, `author`, `view_count`, `status`, `publish_time`) VALUES
('2024年秋季学期开学典礼圆满举行', 
 '我校2024年秋季学期开学典礼在体育馆隆重举行，校长发表重要讲话。', 
 '9月1日上午，我校2024年秋季学期开学典礼在体育馆隆重举行。校长在典礼上发表了题为"砥砺前行，共创辉煌"的重要讲话，勉励全体师生在新学期里继续努力，追求卓越。\n\n典礼上，优秀学生代表和教师代表分别发言，分享了他们的学习和教学心得。最后，全体师生齐唱校歌，开学典礼在热烈的掌声中圆满结束。', 
 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400', 
 'notice', '校办公室', 1256, 'published', NOW() - INTERVAL 2 DAY),

('计算机学院举办人工智能学术讲座', 
 '知名AI专家张教授莅临我校，分享人工智能最新研究成果。', 
 '11月28日下午，计算机学院在学术报告厅举办了一场精彩的人工智能学术讲座。本次讲座邀请了国内知名AI专家张教授作为主讲嘉宾。\n\n张教授从人工智能的发展历程讲起，深入浅出地介绍了深度学习、自然语言处理等前沿技术，并分享了他在大语言模型领域的最新研究成果。讲座吸引了近300名师生参加，现场气氛热烈。', 
 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=400', 
 'academic', '计算机学院', 892, 'published', NOW() - INTERVAL 1 DAY),

('校园美食节即将开幕', 
 '一年一度的校园美食节将于下周举行，届时将有各地特色美食供师生品尝。', 
 '为丰富校园文化生活，后勤服务中心将于12月5日至7日在食堂广场举办"舌尖上的校园"美食节活动。\n\n本次美食节将汇集全国各地特色小吃，包括四川麻辣烫、广东早茶、东北烧烤、江浙点心等。同时还设有美食DIY体验区，师生可以亲手制作传统美食。\n\n活动期间，凭学生证可享受8折优惠，欢迎广大师生踊跃参与！', 
 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400', 
 'campus', '后勤服务中心', 2341, 'published', NOW() - INTERVAL 12 HOUR),

('校运动会报名通知', 
 '2024年秋季运动会即将举行，现开始接受各院系报名。', 
 '为增强学生体质，丰富校园体育文化，学校定于12月15日至16日举办2024年秋季田径运动会。\n\n比赛项目包括：100米、200米、400米、800米、1500米、跳高、跳远、铅球、接力赛等。各院系请于12月8日前完成报名工作。\n\n报名方式：登录教务系统-体育模块-运动会报名\n\n联系人：体育部李老师 电话：12345678', 
 'https://images.unsplash.com/photo-1461896836934- voices-of-the-game?w=400', 
 'activity', '体育部', 1567, 'published', NOW() - INTERVAL 6 HOUR),

('图书馆延长开放时间通知', 
 '为满足期末复习需求，图书馆将延长开放时间至晚上11点。', 
 '各位同学：\n\n期末考试临近，为满足广大同学的复习需求，图书馆决定从即日起至期末考试结束，延长开放时间。\n\n调整后的开放时间：\n- 周一至周五：7:00 - 23:00\n- 周六、周日：8:00 - 22:00\n\n请同学们合理安排学习时间，注意劳逸结合。祝大家期末考试取得好成绩！\n\n图书馆\n2024年11月30日', 
 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=400', 
 'notice', '图书馆', 3421, 'published', NOW());
