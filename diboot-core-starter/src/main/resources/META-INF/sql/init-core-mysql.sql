-- 字典表
CREATE TABLE `dictionary` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `parent_id` varchar(32) NULL COMMENT '父ID',
  `tenant_id` varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  `app_module`  varchar(50)   null comment '应用模块',
  `type` varchar(50) NOT NULL COMMENT '字典类型',
  `item_name` varchar(100) NOT NULL COMMENT '显示名',
  `item_name_i18n` varchar(200) NULL COMMENT '显示名国际化资源标识',
  `item_value` varchar(100) DEFAULT NULL COMMENT '存储值',
  `description` varchar(100) DEFAULT NULL COMMENT '描述说明',
  `extension` varchar(200) DEFAULT NULL COMMENT '扩展JSON',
  `sort_id` smallint NOT NULL DEFAULT '99' COMMENT '排序号',
  `is_editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可改',
  `is_deletable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可删',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 COMMENT '数据字典';
-- 创建索引
create index idx_directory on dictionary(type, item_value);
create index idx_directory_tenant on dictionary(tenant_id);

-- 国际化表
create table i18n_config
(
    id          varchar(32)                           not null comment 'ID',
    type        varchar(20) default 'CUSTOM'          not null comment '类型',
    language    varchar(20)                           not null comment '语言',
    code        varchar(200)                          not null comment '资源标识',
    content     varchar(1000)                         not null comment '内容',
    is_deleted  int         default 0                 not null comment '逻辑删除',
    create_time timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    PRIMARY KEY (`id`)
) comment '国际化配置';
-- 创建索引
create index idx_i18n_config on i18n_config (code, language);