-- 消息模版表
CREATE TABLE message_template (
     id bigserial not null,
     tenant_id          bigint           default 0  not null,
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
comment on column message_template.id is 'ID';
comment on column message_template.tenant_id is '租户ID';
comment on column message_template.app_module is '应用模块';
comment on column message_template.code is '模版编码';
comment on column message_template.title is '模版标题';
comment on column message_template.content is '模版内容';
comment on column message_template.ext_data is '扩展数据';
comment on column message_template.is_deleted is '是否删除';
comment on column message_template.create_by is '创建人';
comment on column message_template.create_time is '创建时间';
comment on column message_template.update_time is '更新时间';
comment on table message_template is '消息模版';
-- 创建索引
create index idx_msg_tmpl_tenant on message_template (tenant_id);
create index idx_msg_tmpl_code ON message_template(code);

-- 消息表
CREATE TABLE message (
  id bigserial not null,
  tenant_id          bigint           default 0  not null,
  app_module         VARCHAR(50),
  template_id        bigint,
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
comment on column message.id is 'ID';
comment on column message.tenant_id is '租户ID';
comment on column message.app_module is '应用模块';
comment on column message.template_id is '模版id';
comment on column message.business_type is '业务类型';
comment on column message.business_code is '业务标识';
comment on column message.sender is '发送方';
comment on column message.receiver is '接收方';
comment on column message.title is '标题';
comment on column message.content is '内容';
comment on column message.channel is '发送通道';
comment on column message.status is '消息状态';
comment on column message.result is '发送结果';
comment on column message.schedule_time is '定时发送时间';
comment on column message.ext_data is '扩展数据';
comment on column message.is_deleted is '是否删除';
comment on column message.update_time is '更新时间';
comment on column message.create_time is '创建时间';
comment on table message is '消息';
create index idx_msg_tenant on message (tenant_id);
create index idx_msg_template on message (template_id);
create index idx_msg_receiver on message (receiver);
