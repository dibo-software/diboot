-- 消息模版表
CREATE TABLE `dbt_message_template` (
  `id` varchar(32) NOT NULL COMMENT 'ID' PRIMARY KEY,
  `tenant_id` varchar(32) NOT NULL DEFAULT '0' COMMENT '租户id',
  `app_module` varchar(50) DEFAULT NULL COMMENT '应用模块',
  `code` varchar(20) NOT NULL COMMENT '模版编码',
  `title` varchar(100) NOT NULL COMMENT '模版标题',
  `content` varchar(500) NOT NULL COMMENT '模版内容',
  `ext_data` varchar(500) DEFAULT NULL COMMENT '扩展数据',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_by` varchar(32) default '0' comment '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) DEFAULT CHARSET=utf8 COMMENT '消息模版';
-- 索引
create index idx_dbt_msg_tmpl_code ON dbt_message_template(code);
-- 消息表
CREATE TABLE `dbt_message` (
  `id` varchar(32) NOT NULL COMMENT 'ID' PRIMARY KEY,
  `tenant_id` varchar(32) NOT NULL DEFAULT '0' COMMENT '租户id',
  `app_module` varchar(50) DEFAULT NULL COMMENT '应用模块',
  `template_id` varchar(32) NOT NULL COMMENT '信息模版id',
  `business_type` varchar(100) NOT NULL COMMENT '业务类型',
  `business_code` varchar(100) NOT NULL DEFAULT '0' COMMENT '业务标识',
  `sender` varchar(100) NOT NULL COMMENT '发送方',
  `receiver` varchar(100) NOT NULL COMMENT '接收方',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `channel` varchar(30) NOT NULL COMMENT '发送通道',
  `status` varchar(30) NOT NULL COMMENT '消息状态',
  `result` varchar(200) DEFAULT NULL COMMENT '发送结果',
  `schedule_time` datetime NULL DEFAULT NULL COMMENT '定时发送时间',
  `ext_data` varchar(500) DEFAULT NULL COMMENT '扩展数据',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) DEFAULT CHARSET=utf8 COMMENT '消息';
-- 索引
create index idx_dbt_msg_template on dbt_message (template_id);
create index idx_dbt_msg_receiver on dbt_message (receiver);
