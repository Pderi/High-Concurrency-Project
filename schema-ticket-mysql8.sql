-- =============================================================================
-- 抢票票务平台 - MySQL 8.0 建表脚本
-- 关联文档：《数据库设计-抢票票务平台.md》
-- 字符集：utf8mb4 / utf8mb4_unicode_ci
-- 说明：idempotency_key 为 NULL 时，uk_order_idempotent 不互斥（MySQL 语义），
--       幂等仅在有客户端幂等键时由该唯一索引保证；无键时依赖应用防重。
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 逆序删除（开发环境重建用；生产请使用迁移工具）
DROP TABLE IF EXISTS `tkt_admin_audit`;
DROP TABLE IF EXISTS `tkt_stock_ledger`;
DROP TABLE IF EXISTS `tkt_ticket`;
DROP TABLE IF EXISTS `tkt_order`;
DROP TABLE IF EXISTS `tkt_tier`;
DROP TABLE IF EXISTS `tkt_session`;
DROP TABLE IF EXISTS `tkt_show`;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 演出
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_show` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(128) NOT NULL COMMENT '演出名称',
  `subtitle` VARCHAR(256) DEFAULT NULL COMMENT '副标题',
  `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面图 URL',
  `description` TEXT COMMENT '详情（富文本）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1已发布 2已下架',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '列表排序，越大越靠前',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_show_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演出';

-- -----------------------------------------------------------------------------
-- 场次
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `show_id` BIGINT NOT NULL COMMENT '演出ID',
  `venue_name` VARCHAR(256) NOT NULL COMMENT '场馆名称',
  `start_time` DATETIME NOT NULL COMMENT '开场时间',
  `sale_start_time` DATETIME NOT NULL COMMENT '开售时间',
  `sale_end_time` DATETIME DEFAULT NULL COMMENT '停售时间，NULL表示无单独停售时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '场次状态：0草稿 1即将开售 2开售中 3停售 4结束',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_session_show` (`show_id`),
  KEY `idx_session_sale_start` (`sale_start_time`),
  KEY `idx_session_start` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场次';

-- -----------------------------------------------------------------------------
-- 票档（库存热点行）
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_tier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` BIGINT NOT NULL COMMENT '场次ID',
  `tier_name` VARCHAR(64) NOT NULL COMMENT '票档名称，如VIP、看台A',
  `price_cent` INT NOT NULL COMMENT '单价（分）',
  `total_stock` INT NOT NULL COMMENT '总库存张数',
  `sold_stock` INT NOT NULL DEFAULT 0 COMMENT '已占用/已售张数（含待支付）',
  `per_user_limit` INT NOT NULL DEFAULT 1 COMMENT '单人限购张数',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_tier_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='票档';

-- -----------------------------------------------------------------------------
-- 订单
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) NOT NULL COMMENT '业务订单号，全局唯一',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `session_id` BIGINT NOT NULL COMMENT '场次ID',
  `tier_id` BIGINT NOT NULL COMMENT '票档ID',
  `quantity` INT NOT NULL COMMENT '购买张数',
  `unit_price_cent` INT NOT NULL COMMENT '下单时单价快照（分）',
  `total_amount_cent` INT NOT NULL COMMENT '应付总金额（分）',
  `order_status` TINYINT NOT NULL DEFAULT 10 COMMENT '10待支付 20已支付 30已关闭 40已退款(预留)',
  `pay_channel` TINYINT NOT NULL DEFAULT 0 COMMENT '0未支付 1模拟支付 2微信 3支付宝',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付成功时间',
  `pay_deadline` DATETIME NOT NULL COMMENT '支付截止时间',
  `close_reason` VARCHAR(64) DEFAULT NULL COMMENT '关闭原因 timeout/cancel 等',
  `idempotency_key` VARCHAR(64) DEFAULT NULL COMMENT '客户端幂等键',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  UNIQUE KEY `uk_order_idempotent` (`user_id`, `session_id`, `tier_id`, `idempotency_key`),
  KEY `idx_order_user` (`user_id`, `create_time`),
  KEY `idx_order_session_tier` (`session_id`, `tier_id`),
  KEY `idx_order_pay_deadline` (`order_status`, `pay_deadline`),
  KEY `idx_order_idem_lookup` (`user_id`, `idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单';

-- -----------------------------------------------------------------------------
-- 电子票凭证
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_ticket` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ticket_no` VARCHAR(32) NOT NULL COMMENT '票号，全局唯一',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `session_id` BIGINT NOT NULL COMMENT '场次ID',
  `tier_id` BIGINT NOT NULL COMMENT '票档ID',
  `ticket_status` TINYINT NOT NULL DEFAULT 1 COMMENT '0未生效 1有效 2作废',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ticket_no` (`ticket_no`),
  KEY `idx_ticket_order` (`order_id`),
  KEY `idx_ticket_user` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电子票凭证';

-- -----------------------------------------------------------------------------
-- 库存流水
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_stock_ledger` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tier_id` BIGINT NOT NULL COMMENT '票档ID',
  `session_id` BIGINT NOT NULL COMMENT '场次ID（冗余）',
  `change_type` TINYINT NOT NULL COMMENT '1下单预占 2支付确认 3超时释放 4管理端调整 5对账修正',
  `delta` INT NOT NULL COMMENT 'sold_stock 变动量（示例：+2 表示占用2张）',
  `before_sold` INT NOT NULL COMMENT '变动前 sold_stock',
  `after_sold` INT NOT NULL COMMENT '变动后 sold_stock',
  `order_id` BIGINT DEFAULT NULL COMMENT '关联订单',
  `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
  `creator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '操作者或system',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_ledger_tier_time` (`tier_id`, `create_time`),
  KEY `idx_ledger_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存流水';

-- -----------------------------------------------------------------------------
-- 管理端审计（可选）
-- -----------------------------------------------------------------------------
CREATE TABLE `tkt_admin_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `admin_user_id` BIGINT NOT NULL COMMENT '管理员用户ID',
  `action` VARCHAR(64) NOT NULL COMMENT '动作，如 SHOW_PUBLISH',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 show/session/tier',
  `biz_id` BIGINT NOT NULL COMMENT '业务主键',
  `payload_json` JSON DEFAULT NULL COMMENT '变更快照',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_audit_biz` (`biz_type`, `biz_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理端审计';

-- =============================================================================
-- 结束
-- =============================================================================
