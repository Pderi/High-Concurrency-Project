-- =============================================================================
-- 演示种子数据（开发环境）
-- 依赖：schema-ticket-mysql8.sql 已执行
-- 说明：固定 ID 便于联调；重复执行前请先清空业务表或重建库
-- =============================================================================

SET NAMES utf8mb4;

INSERT INTO `tkt_show` (`id`, `name`, `subtitle`, `cover_url`, `description`, `status`, `sort`, `creator`, `updater`, `deleted`, `tenant_id`)
VALUES
(1, '星河音乐节', '夏日限定场', 'https://cdn.example.com/show/xinghe.jpg', '高并发演示用演出（已发布）', 1, 100, 'seed', 'seed', 0, 0),
(2, '草稿演出（不可见）', NULL, NULL, 'C 端不应展示', 0, 0, 'seed', 'seed', 0, 0)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `status` = VALUES(`status`);

INSERT INTO `tkt_session` (`id`, `show_id`, `venue_name`, `start_time`, `sale_start_time`, `sale_end_time`, `status`, `creator`, `updater`, `deleted`, `tenant_id`)
VALUES
(1, 1, '演示体育场', '2026-08-01 19:30:00', '2026-07-01 10:00:00', '2026-08-01 18:00:00', 2, 'seed', 'seed', 0, 0)
ON DUPLICATE KEY UPDATE `venue_name` = VALUES(`venue_name`), `status` = VALUES(`status`);

INSERT INTO `tkt_tier` (`id`, `session_id`, `tier_name`, `price_cent`, `total_stock`, `sold_stock`, `per_user_limit`, `version`, `status`, `creator`, `updater`, `deleted`, `tenant_id`)
VALUES
(1, 1, 'VIP', 128000, 100, 0, 2, 0, 1, 'seed', 'seed', 0, 0),
(2, 1, '看台A', 68000, 500, 0, 4, 0, 1, 'seed', 'seed', 0, 0),
(3, 1, '看台B（下架）', 38000, 300, 0, 4, 0, 0, 'seed', 'seed', 0, 0)
ON DUPLICATE KEY UPDATE `tier_name` = VALUES(`tier_name`), `total_stock` = VALUES(`total_stock`), `status` = VALUES(`status`);
