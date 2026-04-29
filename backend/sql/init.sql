-- ============================================
-- 栖居家具 · 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS qiju_furniture DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qiju_furniture;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `role`        VARCHAR(20)  DEFAULT 'user' COMMENT '角色: user/admin',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '商品名称',
  `category`     VARCHAR(20)  NOT NULL COMMENT '分类：沙发/桌子/椅子/床/柜子/灯饰',
  `description`  VARCHAR(500) DEFAULT '' COMMENT '简短描述',
  `detail`       TEXT         COMMENT '详细描述',
  `price`        DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `tag`          VARCHAR(20)  DEFAULT '' COMMENT '标签：new/hot/sale',
  `image`        VARCHAR(500) DEFAULT '' COMMENT '主图URL',
  `images`       JSON         COMMENT '多图URL数组',
  `specs`        JSON         COMMENT '规格参数JSON',
  `rating`       DECIMAL(2,1) DEFAULT '0.0' COMMENT '评分',
  `review_count` INT          DEFAULT 0 COMMENT '评价数',
  `in_stock`     TINYINT(1)   DEFAULT 1 COMMENT '是否现货',
  `stock_count`  INT          DEFAULT 0 COMMENT '库存数量',
  `next_batch`     DATE         DEFAULT NULL COMMENT '预计补货日期',
  `sort`           INT          DEFAULT 0 COMMENT '排序',
  `status`       TINYINT(1)   DEFAULT 1 COMMENT '状态：0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `product_id`   BIGINT       NOT NULL COMMENT '商品ID',
  `author`       VARCHAR(50)  NOT NULL COMMENT '评价人昵称',
  `avatar`       VARCHAR(20)  DEFAULT '🧑' COMMENT '头像emoji',
  `stars`        TINYINT      NOT NULL COMMENT '评分1-5',
  `content`      VARCHAR(500) NOT NULL COMMENT '评价内容',
  `review_date`  DATE         DEFAULT (CURRENT_DATE) COMMENT '评价日期',
  `status`       TINYINT(1)   DEFAULT 1 COMMENT '状态：0隐藏 1显示',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `session_id`   VARCHAR(64)  NOT NULL COMMENT '会话标识（未登录用）',
  `user_id`      BIGINT       DEFAULT NULL COMMENT '用户ID（登录后用）',
  `product_id`   BIGINT       NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(100) DEFAULT '' COMMENT '商品名称（快照）',
  `product_image` VARCHAR(500) DEFAULT '' COMMENT '商品图片（快照）',
  `price`        DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价（快照）',
  `quantity`     INT          NOT NULL DEFAULT 1 COMMENT '数量',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_product` (`session_id`, `product_id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表（注意：order是MySQL保留字，使用orders）
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`        VARCHAR(32)   NOT NULL COMMENT '订单编号',
  `session_id`      VARCHAR(64)   NOT NULL COMMENT '会话标识',
  `customer_name`   VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '客户姓名',
  `customer_email`  VARCHAR(100)  DEFAULT '' COMMENT '客户邮箱',
  `customer_phone`  VARCHAR(20)   DEFAULT '' COMMENT '客户电话',
  `address`         VARCHAR(500)  DEFAULT '' COMMENT '收货地址',
  `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `discount_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '优惠金额',
  `final_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `coupon_code`     VARCHAR(20)   DEFAULT NULL COMMENT '使用的优惠码',
  `status`          TINYINT       DEFAULT 0 COMMENT '状态：0待支付 1已支付 2已发货 3已完成 4已取消',
  `notes`           VARCHAR(500)  DEFAULT NULL COMMENT '备注',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`     BIGINT        NOT NULL COMMENT '订单ID',
  `product_id`   BIGINT        NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(100)  NOT NULL COMMENT '商品名称',
  `product_image` VARCHAR(500) DEFAULT '' COMMENT '商品图片',
  `price`        DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity`     INT           NOT NULL COMMENT '数量',
  `subtotal`     DECIMAL(12,2) NOT NULL COMMENT '小计',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 订阅表
CREATE TABLE IF NOT EXISTS `newsletter` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
  `email`       VARCHAR(100) NOT NULL COMMENT '邮箱',
  `status`      TINYINT(1)   DEFAULT 1 COMMENT '状态：0退订 1订阅',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅表';

-- 咨询表
CREATE TABLE IF NOT EXISTS `contact` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(50)  NOT NULL COMMENT '姓名',
  `email`       VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '电话',
  `message`     TEXT         NOT NULL COMMENT '留言',
  `is_read`     TINYINT(1)   DEFAULT 0 COMMENT '是否已读',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询表';

-- ============================================
-- 初始化商品数据 (16款)
-- ============================================
INSERT INTO `product` (`id`, `name`, `category`, `description`, `detail`, `price`, `original_price`, `tag`, `image`, `images`, `specs`, `rating`, `review_count`, `in_stock`, `stock_count`) VALUES
(1, '云朵沙发', '沙发', '松软如云的坐感体验，高弹海绵填充，北欧极简设计',
 '云朵沙发采用高密度回弹海绵与羽绒混合填充，坐感如云端般轻盈。可拆卸棉麻面料方便清洗，加宽扶手设计可当枕头使用。',
 5999.00, 6999.00, 'hot',
 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80',
 '["https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80","https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&q=80","https://images.unsplash.com/photo-1544457070-4cd773b4d71e?w=600&q=80"]',
 '{"材质":"棉麻+高弹海绵","尺寸":"220×85×75cm","颜色":"米白 / 浅灰","承重":"300kg"}',
 4.8, 326, 1, 23),

(2, '原木餐桌', '桌子', '北美白橡木，手工打磨，温润触感，可容纳6-8人',
 '精选北美进口白橡木，纹理清晰自然。采用传统榫卯结构与现代工艺结合，稳固耐用。表面涂抹天然木蜡油，环保健康。',
 4299.00, NULL, 'new',
 'https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=600&q=80',
 '["https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=600&q=80","https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=600&q=80"]',
 '{"材质":"北美白橡木","尺寸":"160×85×75cm","工艺":"手工打磨+木蜡油","承重":"80kg"}',
 4.7, 218, 1, 15),

(3, '贝壳椅', '椅子', '北欧经典造型，一体化曲木工艺，舒适贴合背部曲线',
 '经典北欧贝壳椅设计，3D曲木压弯工艺打造流畅线条。人体工学靠背弧度，久坐不累。',
 1599.00, NULL, '',
 'https://images.unsplash.com/photo-1592078615290-033ee584e267?w=600&q=80',
 '["https://images.unsplash.com/photo-1592078615290-033ee584e267?w=600&q=80","https://images.unsplash.com/photo-1505843490538-5133c6d7cd29?w=600&q=80"]',
 '{"材质":"榉木+胡桃木贴面","尺寸":"55×50×78cm","工艺":"3D曲木压弯","颜色":"原木色"}',
 4.6, 189, 1, 48),

(4, '悬浮床架', '床', '极简悬浮设计，夜灯氛围灯带，静音排骨架结构',
 '悬浮式设计搭配底部氛围灯带，营造轻盈漂浮的视觉效果。加密排骨架均匀承重，静音结构翻身无异响。',
 6999.00, 7999.00, 'sale',
 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&q=80',
 '["https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&q=80","https://images.unsplash.com/photo-1616594039964-ae9021a400a0?w=600&q=80"]',
 '{"材质":"实木框架+科技布","尺寸":"180×200×35cm","颜色":"深灰 / 驼色","特点":"内置氛围灯"}',
 4.9, 452, 1, 8),

(5, '藤编收纳柜', '柜子', '东南亚风情藤编面板，三开门大容量储物空间',
 '手工藤编面板透气防潮，橡胶木框架稳固耐用。内部隔板可自由调节，满足不同收纳需求。',
 3599.00, NULL, '',
 'https://images.unsplash.com/photo-1550226891-ef816aed4a98?w=600&q=80',
 '["https://images.unsplash.com/photo-1550226891-ef816aed4a98?w=600&q=80","https://images.unsplash.com/photo-1594026112284-02bb6f3352fe?w=600&q=80"]',
 '{"材质":"橡胶木+藤编","尺寸":"120×40×90cm","风格":"日式/东南亚","层数":"3层可调"}',
 4.5, 167, 0, 0),

(6, '纸艺吊灯', '灯饰', '日本和纸手工制作，光线柔和不刺眼，直径50cm',
 '日本工匠手工和纸打造，灯光透过纸面散发出温暖柔和的光晕。竹编结构轻盈自然，为空间增添东方韵味。',
 899.00, NULL, 'new',
 'https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=600&q=80',
 '["https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=600&q=80","https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600&q=80"]',
 '{"材质":"日本和纸+竹编","尺寸":"直径50×高30cm","光源":"E27螺口（不含灯泡）","线长":"可调120cm"}',
 4.7, 98, 1, 32),

(7, '真皮躺椅', '沙发', '头层牛皮电动躺椅，一键调节角度，USB充电功能',
 '意大利进口头层牛皮，触感细腻温润。电动无极调节，从110°到160°任意锁定。两侧USB快充接口，边躺边充。',
 8999.00, NULL, 'hot',
 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600&q=80',
 '["https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600&q=80","https://images.unsplash.com/photo-1544457070-4cd773b4d71e?w=600&q=80"]',
 '{"材质":"头层牛皮","尺寸":"75×85×105cm","功能":"电动躺平+USB充电","颜色":"深咖 / 黑色"}',
 4.8, 274, 1, 5),

(8, '玻璃茶几', '桌子', '钢化玻璃+不锈钢腿，极简通透设计，适合现代风格',
 '8mm加厚钢化玻璃面板，安全防爆。拉丝不锈钢腿呈现高级金属质感。底部置物层方便收纳杂志遥控器。',
 1999.00, 2599.00, 'sale',
 'https://images.unsplash.com/photo-1532372576444-dda954194ad0?w=600&q=80',
 '["https://images.unsplash.com/photo-1532372576444-dda954194ad0?w=600&q=80","https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=600&q=80"]',
 '{"材质":"8mm钢化玻璃+拉丝不锈钢","尺寸":"110×60×40cm","颜色":"透明/茶色","承重":"40kg"}',
 4.4, 156, 1, 19),

(9, '办公人体工学椅', '椅子', '4D扶手+腰背分离，透气网布，久坐办公首选',
 '腰背分离设计精准支撑腰椎，4D扶手可上下前后左右旋转调节。高弹透气网布四季适用，后仰135°午休功能。',
 2499.00, NULL, '',
 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=600&q=80',
 '["https://images.unsplash.com/photo-1586105251261-72a756497a11?w=600&q=80","https://images.unsplash.com/photo-1505843490538-5133c6d7cd29?w=600&q=80"]',
 '{"材质":"高弹网布+铝合金脚","功能":"腰背分离+4D扶手","承重":"150kg","颜色":"黑 / 灰"}',
 4.6, 342, 1, 56),

(10, '储物高箱床', '床', '气压杆掀起，超大储物空间，可收纳换季被褥',
 '气压杆助力轻松掀起床板，床箱深度35cm，可收纳换季衣物被褥。床头软包设计，靠感舒适。',
 5499.00, NULL, '',
 'https://images.unsplash.com/photo-1589782182703-2aaa69037b5b?w=600&q=80',
 '["https://images.unsplash.com/photo-1589782182703-2aaa69037b5b?w=600&q=80","https://images.unsplash.com/photo-1616594039964-ae9021a400a0?w=600&q=80"]',
 '{"材质":"实木+环保皮","尺寸":"180×200×45cm","储物":"床箱高35cm","颜色":"米白 / 灰蓝"}',
 4.7, 231, 1, 12),

(11, '黑胶唱片柜', '柜子', '为黑胶爱好者设计，45°倾斜展示，可装约60张',
 '45°倾斜展示层板，便于翻阅黑胶唱片。白蜡木纹理清晰，可融入各种家居风格。底部加高防潮脚垫。',
 1299.00, NULL, 'new',
 'https://images.unsplash.com/photo-1594026112284-02bb6f3352fe?w=600&q=80',
 '["https://images.unsplash.com/photo-1594026112284-02bb6f3352fe?w=600&q=80"]',
 '{"材质":"白蜡木","尺寸":"80×30×85cm","容量":"约60张黑胶","风格":"中古风"}',
 4.9, 87, 1, 7),

(12, '极简落地灯', '灯饰', '黄铜+亚麻灯罩，三档调光，氛围感满满',
 '黄铜灯杆经过拉丝处理，质感出众。亚麻灯罩透出柔和暖光，三档亮度调节满足阅读、观影、睡眠不同场景。',
 699.00, NULL, '',
 'https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=600&q=80',
 '["https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=600&q=80","https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600&q=80"]',
 '{"材质":"黄铜+亚麻","尺寸":"高158cm","光源":"LED三档调光","颜色":"复古铜色"}',
 4.5, 143, 1, 44),

(13, '模块组合沙发', '沙发', '自由拼接，灵活变换，L型/U型随心组合',
 '模块化设计，每个单元可独立移动。三人位+单人位+贵妃榻自由组合，轻松适应大小户型变动。科技布面料防水防污易打理。',
 8999.00, 10999.00, 'sale',
 'https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&q=80',
 '["https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&q=80","https://images.unsplash.com/photo-1544457070-4cd773b4d71e?w=600&q=80"]',
 '{"材质":"科技布+高弹海绵","模块":"3+1+贵妃","尺寸":"可自定义","颜色":"雾蓝 / 浅灰 / 米白"}',
 4.6, 289, 1, 18),

(14, '升降办公桌', '桌子', '电动升降，记忆高度，站坐交替健康办公',
 '静音双电机升降系统，升降平稳无抖动。3档高度记忆，一键切换坐姿/站姿。加宽桌面可放双屏显示器+笔记本。',
 3299.00, NULL, 'hot',
 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&q=80',
 '["https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&q=80"]',
 '{"材质":"北美黑胡桃木桌面+钢架","升降范围":"72-120cm","记忆高度":"3档","承重":"80kg"}',
 4.8, 178, 1, 9),

(15, '转角书柜', '柜子', 'L型转角设计，充分利用角落空间，多层分类收纳',
 'L型转角完美适配书房角落，左右对称设计视觉统一。隔板高度可自由调节，书籍/摆件/绿植随心摆放。底部离地防潮设计。',
 2899.00, NULL, '',
 'https://images.unsplash.com/photo-1585421514284-efb74c2b69ba?w=600&q=80',
 '["https://images.unsplash.com/photo-1585421514284-efb74c2b69ba?w=600&q=80"]',
 '{"材质":"橡木+E0级板材","尺寸":"120×120×200cm","层数":"6层可调","颜色":"原木色"}',
 4.4, 92, 1, 14),

(16, '智能护眼台灯', '灯饰', 'Ra>95高显色，无频闪，智能感光调光',
 '高显色无频闪护眼LED光源，Ra>95还原真实色彩。内置环境光传感器，自动调节亮度。45分钟定时提醒休息，呵护双眼。',
 499.00, 699.00, 'new',
 'https://images.unsplash.com/photo-1534105615250-8e2aeb2bf0b0?w=600&q=80',
 '["https://images.unsplash.com/photo-1534105615250-8e2aeb2bf0b0?w=600&q=80"]',
 '{"材质":"铝合金+ABS","色温":"3000-5000K可调","显色指数":"Ra>95","功能":"智能感光+45分钟定时"}',
 4.9, 215, 1, 67);

-- 初始化评价数据
INSERT INTO `review` (`product_id`, `author`, `avatar`, `stars`, `content`, `review_date`) VALUES
(1, '李明', '👨', 5, '沙发太舒服了！坐上去就不想起来，面料触感非常棒。', '2026-04-15'),
(1, '张婷', '👩', 4, '外观很大气，颜色和家里很搭。就是送货稍微慢了一点。', '2026-04-10'),
(2, '王工', '👨‍💼', 5, '白橡木纹理太美了，手工打磨的质感确实不一样。', '2026-04-12'),
(2, '小陈', '👨‍🎓', 4, '桌子很稳，六个人吃饭完全没问题。', '2026-04-05'),
(4, '刘女士', '👩‍🦰', 5, '悬浮床真的太有科技感了，氛围灯晚上特别好看！', '2026-04-20'),
(4, '赵先生', '👨‍🦱', 5, '安装师傅很专业，床架质量超出预期。', '2026-04-18'),
(7, '周总', '🧑‍💼', 5, '牛皮手感一流，电动躺平功能太适合午休了。', '2026-04-08'),
(12, '小杨', '🧑‍🎨', 5, '灯光非常温暖，黄铜质感很好，氛围神器！', '2026-04-22'),
(13, '陈女士', '👩', 5, '搬家后客厅大了一倍，模块沙发自由组合太实用了！', '2026-04-19'),
(13, '阿杰', '🧑', 4, '科技布真的防水，孩子洒了牛奶一擦就干净。', '2026-04-14'),
(14, '码农小王', '🧑‍💻', 5, '每天站坐交替办公，腰确实不疼了。升降很安静。', '2026-04-21'),
(16, '学生家长', '👨‍👩‍👧', 5, '给孩子买的，光线很柔和，写作业不刺眼。', '2026-04-25');
