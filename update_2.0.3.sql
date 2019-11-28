-- 2019-08-25 18:35:47 by diboot
CREATE TABLE `test_uuid`( `uuid` VARCHAR(32) NOT NULL COMMENT '编号', `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '已删除', `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',PRIMARY KEY (`uuid`)) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT 'UUID测试';
-- 2019-08-25 18:38:50 by diboot
ALTER TABLE `test_uuid` MODIFY COLUMN `uuid` varchar(100) NOT NULL COMMENT '编号';
-- 2019-08-25 18:45:16 by diboot
ALTER TABLE `test_uuid` ADD COLUMN `name` varchar(100) COMMENT '名称' AFTER `uuid`, ADD COLUMN `title` varchar(100) COMMENT '标题' AFTER `name`;
-- 2019-08-25 18:45:16 by diboot
ALTER TABLE `test_uuid` MODIFY COLUMN `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '已删除' AFTER `title`;
-- 2019-09-03 10:49:54 by diboot
CREATE TABLE `test_cid`( `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '编号', `extdata` varchar(100) DEFAULT NULL COMMENT '扩展字段', `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '已删除', `create_by` bigint(20) NULL DEFAULT 0 COMMENT '创建人', `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1000000001 DEFAULT CHARSET=utf8mb4 COMMENT '程序自增测试';
-- 2019-09-03 10:50:47 by diboot
ALTER TABLE `test_cid` ADD COLUMN `name` varchar(100) COMMENT '名称' AFTER `id`;
-- 2019-09-03 10:50:47 by diboot
ALTER TABLE `test_cid` MODIFY COLUMN `extdata` varchar(100) COMMENT '扩展字段' AFTER `name`;
