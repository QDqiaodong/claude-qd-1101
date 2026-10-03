SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- ---------------------------------------------------------------------
-- 花店 · 花材与花束管理
-- 库名 flower_shop；application.yml 里 ddl-auto: none，表结构全部由本文件在 MySQL 镜像构建期导入。
-- 注意：mysql 容器是 UTC，时间戳一律写 UTC_TIMESTAMP() + INTERVAL 8 HOUR（北京时间）。

CREATE DATABASE IF NOT EXISTS flower_shop
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE flower_shop;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS bouquet_item;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS delivery;
DROP TABLE IF EXISTS customer_order;
DROP TABLE IF EXISTS bouquet;
DROP TABLE IF EXISTS flower_material;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------- 花材台账 ----------------
CREATE TABLE flower_material (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(64) NOT NULL COMMENT '花材名',
    category     VARCHAR(16) NOT NULL COMMENT '切花/叶材/辅材',
    unit_price   DECIMAL(10,2) NOT NULL COMMENT '单价',
    stock        INT NOT NULL COMMENT '库存',
    status       VARCHAR(16) NOT NULL COMMENT '在售/停用',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 花束配方（主表） ----------------
CREATE TABLE bouquet (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(64) NOT NULL COMMENT '花束名',
    price        DECIMAL(10,2) NOT NULL COMMENT '售价',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 花束配方明细：@OneToMany，bouquet_id 归属花束，flower_material_id 引用花材
CREATE TABLE bouquet_item (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    bouquet_id         BIGINT NOT NULL,
    flower_material_id BIGINT NOT NULL,
    qty                INT NOT NULL COMMENT '数量',
    CONSTRAINT fk_bi_bouquet FOREIGN KEY (bouquet_id) REFERENCES bouquet (id),
    CONSTRAINT fk_bi_material FOREIGN KEY (flower_material_id) REFERENCES flower_material (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 客户订单（主表） ----------------
CREATE TABLE customer_order (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer     VARCHAR(64) NOT NULL COMMENT '客户名',
    phone        VARCHAR(24) NOT NULL COMMENT '手机号',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    status       VARCHAR(16) NOT NULL COMMENT '待付款/已付款/已取消',
    created_at   DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_item (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    bouquet_id BIGINT NOT NULL,
    qty        INT NOT NULL COMMENT '数量',
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES customer_order (id),
    CONSTRAINT fk_oi_bouquet FOREIGN KEY (bouquet_id) REFERENCES bouquet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- 今日配送 ----------------
CREATE TABLE delivery (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    address      VARCHAR(255) NOT NULL COMMENT '配送地址',
    expect_slot  VARCHAR(32) NOT NULL COMMENT '期望时段',
    status       VARCHAR(16) NOT NULL COMMENT '待配送/配送中/已完成/已取消',
    created_at   DATETIME COMMENT '创建时间',
    CONSTRAINT fk_dv_order FOREIGN KEY (order_id) REFERENCES customer_order (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 种子数据
-- =====================================================================

INSERT INTO flower_material (name, category, unit_price, stock, status, created_at) VALUES
('红玫瑰',   '切花', 5.00, 200, '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('百合',     '切花', 8.00, 150, '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('尤加利叶', '叶材', 2.00, 300, '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('满天星',   '辅材', 3.50, 120, '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('栀子叶',   '叶材', 1.80, 260, '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('洋桔梗',   '切花', 6.50, 90,  '在售', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY),
('龟背竹',   '叶材', 4.00, 0,   '停用', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 60 DAY);

INSERT INTO bouquet (name, price, created_at) VALUES
('热恋红玫瑰', 99.00,  UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 30 DAY),
('清新百合',   128.00, UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 30 DAY),
('满天星花束', 59.00,  UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 30 DAY),
('婚礼混搭',   188.00, UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 30 DAY);

INSERT INTO bouquet_item (bouquet_id, flower_material_id, qty) VALUES
(1, 1, 11), (1, 3, 3),
(2, 2, 6),  (2, 5, 2),
(3, 4, 8),
(4, 1, 9),  (4, 6, 5), (4, 3, 4);

INSERT INTO customer_order (customer, phone, total_amount, status, created_at) VALUES
('张伟', '13800001111', 217.00, '待付款', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 10 DAY),
('李娜', '13800002222', 316.00, '已付款', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 9 DAY),
('王芳', '13800003333', 177.00, '待付款', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 8 DAY);

INSERT INTO order_item (order_id, bouquet_id, qty) VALUES
(1, 1, 1), (1, 3, 2),
(2, 2, 1), (2, 4, 1),
(3, 3, 3);

INSERT INTO delivery (order_id, address, expect_slot, status, created_at) VALUES
(1, '北京市朝阳区建国路88号',     '14:00-16:00', '待配送', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 2 DAY),
(2, '上海市黄浦区南京东路100号',  '10:00-12:00', '配送中', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 1 DAY),
(3, '广州市天河区天河路200号',    '16:00-18:00', '待配送', UTC_TIMESTAMP() + INTERVAL 8 HOUR - INTERVAL 1 DAY);
