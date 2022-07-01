-- 消息模版表
CREATE TABLE `message_template` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `app_module` varchar(50) DEFAULT NULL COMMENT '应用模块',
  `code` varchar(20) NOT NULL COMMENT '模版编码',
  `title` varchar(100) NOT NULL COMMENT '模版标题',
  `content` varchar(500) NOT NULL COMMENT '模版内容',
  `ext_data` varchar(500) DEFAULT NULL COMMENT '扩展数据',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_by` bigint DEFAULT '0' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '消息模版';
-- 索引
create index idx_msg_tmpl_tenant on message_template (tenant_id);
create index idx_msg_tmpl_code ON message_template(code);
-- 消息表
CREATE TABLE `message` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `app_module` varchar(50) DEFAULT NULL COMMENT '应用模块',
  `template_id` bigint COMMENT '信息模版id',
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
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '消息';
-- 索引
create index idx_msg_tenant on message (tenant_id);
create index idx_msg_template on message (template_id);
create index idx_msg_receiver on message (receiver);
