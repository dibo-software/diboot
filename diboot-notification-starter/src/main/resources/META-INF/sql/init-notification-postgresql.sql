-- 消息模版表
CREATE TABLE dbt_message_template (
     id  varchar(32) not null,
     tenant_id varchar(32) default '0' not null,
     app_module         VARCHAR(50),
     code VARCHAR(20) NOT NULL,
     title VARCHAR(100) NOT NULL,
     content VARCHAR(500) NOT NULL,
     ext_data VARCHAR(500),
     is_deleted   BOOLEAN default FALSE   not null,
     create_by bigint DEFAULT 0 NOT NULL,
     create_time  timestamp default CURRENT_TIMESTAMP   not null,
     update_time  timestamp default CURRENT_TIMESTAMP null
);
-- 添加备注
comment on column dbt_message_template.id is 'ID';
comment on column dbt_message_template.tenant_id is '租户ID';
comment on column dbt_message_template.app_module is '应用模块';
comment on column dbt_message_template.code is '模版编码';
comment on column dbt_message_template.title is '模版标题';
comment on column dbt_message_template.content is '模版内容';
comment on column dbt_message_template.ext_data is '扩展数据';
comment on column dbt_message_template.is_deleted is '是否删除';
comment on column dbt_message_template.create_by is '创建人';
comment on column dbt_message_template.create_time is '创建时间';
comment on column dbt_message_template.update_time is '更新时间';
comment on table dbt_message_template is '消息模版';
-- 创建索引
create index idx_dbt_msg_tmpl_tenant on dbt_message_template (tenant_id);
create index idx_dbt_msg_tmpl_code ON dbt_message_template(code);

-- 消息表
CREATE TABLE dbt_message (
  id  varchar(32) not null,
  tenant_id varchar(32) default '0' not null,
  app_module         VARCHAR(50),
  template_id        varchar(32) NULL,
  business_type       VARCHAR(100)          not null,
  business_code       VARCHAR(50) default 0  not null,
  sender VARCHAR(100)  not null,
  receiver VARCHAR(100) not null,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(500) NOT NULL,
  channel VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL,
  result      VARCHAR(200),
  schedule_time  timestamp   null,
  ext_data VARCHAR(200),
  is_deleted   BOOLEAN default FALSE   not null,
  create_time  timestamp default CURRENT_TIMESTAMP  not null,
  update_time  timestamp default CURRENT_TIMESTAMP  null
);
comment on column dbt_message.id is 'ID';
comment on column dbt_message.tenant_id is '租户ID';
comment on column dbt_message.app_module is '应用模块';
comment on column dbt_message.template_id is '模版id';
comment on column dbt_message.business_type is '业务类型';
comment on column dbt_message.business_code is '业务标识';
comment on column dbt_message.sender is '发送方';
comment on column dbt_message.receiver is '接收方';
comment on column dbt_message.title is '标题';
comment on column dbt_message.content is '内容';
comment on column dbt_message.channel is '发送通道';
comment on column dbt_message.status is '消息状态';
comment on column dbt_message.result is '发送结果';
comment on column dbt_message.schedule_time is '定时发送时间';
comment on column dbt_message.ext_data is '扩展数据';
comment on column dbt_message.is_deleted is '是否删除';
comment on column dbt_message.update_time is '更新时间';
comment on column dbt_message.create_time is '创建时间';
comment on table dbt_message is '消息';
create index idx_dbt_msg_tenant on dbt_message (tenant_id);
create index idx_dbt_msg_template on dbt_message (template_id);
create index idx_dbt_msg_receiver on dbt_message (receiver);
