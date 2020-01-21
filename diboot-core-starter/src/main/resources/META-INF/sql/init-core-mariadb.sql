-- 建表
CREATE TABLE `dictionary` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int unsigned NOT NULL COMMENT '父ID',
  `type` varchar(50) NOT NULL COMMENT '字典类型',
  `item_name` varchar(100) NOT NULL COMMENT '显示名',
  `item_value` varchar(100) DEFAULT NULL COMMENT '存储值',
  `description` varchar(100) DEFAULT NULL COMMENT '描述说明',
  `extdata` varchar(200) DEFAULT NULL COMMENT '扩展JSON',
  `sort_id` smallint NOT NULL DEFAULT '99' COMMENT '排序号',
  `is_editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可改',
  `is_deletable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否可删',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '数据字典';

-- 创建索引
create index idx_directory on dictionary(type, item_value);