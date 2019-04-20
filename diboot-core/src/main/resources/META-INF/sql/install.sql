SET FOREIGN_KEY_CHECKS=0;
-- 元数据表
-- DROP TABLE IF EXISTS `metadata`;
CREATE TABLE `metadata` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int unsigned NOT NULL COMMENT '父ID',
  `type` varchar(50) NOT NULL COMMENT '元数据类型',
  `item_name` varchar(100) NOT NULL COMMENT '元数据项显示名',
  `item_value` varchar(100) DEFAULT NULL COMMENT '元数据项存储值',
  `comment` varchar(100) DEFAULT NULL COMMENT '备注',
  `extdata` varchar(200) DEFAULT NULL COMMENT '扩展属性',
  `sort_id` smallint NOT NULL DEFAULT '99' COMMENT '排序号',
  `system` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否是系统预置',
  `editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可编辑',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_metadata` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;