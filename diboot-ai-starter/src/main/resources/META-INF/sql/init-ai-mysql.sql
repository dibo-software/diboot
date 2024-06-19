-- 会话表
CREATE TABLE `dbt_ai_session` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `tenant_id`  varchar(32)  default '0' not null comment '租户ID',
  `title` varchar(100) NOT NULL COMMENT 'session标题',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  `create_by` varchar(32)  not null default '0' comment '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
  PRIMARY KEY (`id`)
) DEFAULT charset = utf8mb4 COMMENT 'AI会话';

-- 会话记录
CREATE TABLE `dbt_ai_session_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `tenant_id`  varchar(32)  default '0' not null comment '租户ID',
  `session_id` varchar(32) NOT NULL COMMENT '会话id',
  `model` varchar(32) NOT NULL COMMENT '问答模型',
  `request_body` text NOT NULL COMMENT '请求内容',
  `response_body` text NOT NULL COMMENT '响应内容',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  `create_by` varchar(32)  not null default '0' comment '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) DEFAULT charset = utf8mb4 COMMENT 'AI会话记录';