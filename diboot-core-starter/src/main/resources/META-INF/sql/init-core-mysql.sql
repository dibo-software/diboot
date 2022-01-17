-- 字典表
CREATE TABLE `dictionary` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` bigint unsigned NOT NULL COMMENT '父ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  `app_module`  varchar(50)   null comment '应用模块',
  `type` varchar(50) NOT NULL COMMENT '字典类型',
  `item_name` varchar(100) NOT NULL COMMENT '显示名',
  `item_value` varchar(100) DEFAULT NULL COMMENT '存储值',
  `description` varchar(100) DEFAULT NULL COMMENT '描述说明',
  `extdata` varchar(200) DEFAULT NULL COMMENT '扩展JSON',
  `sort_id` smallint NOT NULL DEFAULT '99' COMMENT '排序号',
  `is_editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可改',
  `is_deletable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可删',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '数据字典';
-- 创建索引
create index idx_directory on dictionary(type, item_value);
create index idx_directory_tenant on dictionary(tenant_id);

-- 系统配置表
CREATE TABLE `system_config`
(
    `id`          bigint(20) UNSIGNED             NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `tenant_id`   bigint(20)                      NOT NULL DEFAULT 0 COMMENT '租户ID',
    `type`        varchar(50) CHARACTER SET utf8  NOT NULL COMMENT '类型',
    `prop`        varchar(50) CHARACTER SET utf8  NOT NULL COMMENT '属性',
    `value`       varchar(255) CHARACTER SET utf8 NULL     DEFAULT NULL COMMENT '属性值',
    `is_deleted`  tinyint(1)                      NOT NULL DEFAULT 0 COMMENT '删除标记',
    `create_time` timestamp                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                       NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_system_config_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `idx_system_config` (`type`, `prop`) USING BTREE
) AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8 COMMENT = '系统配置';
