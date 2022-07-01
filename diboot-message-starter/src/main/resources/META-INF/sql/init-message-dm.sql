-- 消息模版表
CREATE TABLE ${SCHEMA}.message_template (
     id                 BIGINT identity ( 100000,1 ) primary key,
     tenant_id          BIGINT           default 0  not null,
     app_module         VARCHAR(50),
     code VARCHAR(50) NOT NULL,
     title VARCHAR(200) NOT NULL,
     content VARCHAR(1000) NOT NULL,
     ext_data VARCHAR(1000),
     is_deleted   BIT DEFAULT 0    not null,
     create_time  timestamp default CURRENT_TIMESTAMP   not null,
     create_by BIGINT DEFAULT 0 NOT NULL,
     update_time  timestamp   default CURRENT_TIMESTAMP null
);
-- 添加备注
comment on column ${SCHEMA}.message_template.id is 'ID';
comment on column ${SCHEMA}.message_template.tenant_id is '租户ID';
comment on column ${SCHEMA}.message_template.app_module is '应用模块';
comment on column ${SCHEMA}.message_template.code is '模版编码';
comment on column ${SCHEMA}.message_template.title is '模版标题';
comment on column ${SCHEMA}.message_template.content is '模版内容';
comment on column ${SCHEMA}.message_template.ext_data is '扩展数据';
comment on column ${SCHEMA}.message_template.create_by is '创建人';
comment on column ${SCHEMA}.message_template.update_time is '更新时间';
comment on column ${SCHEMA}.message_template.is_deleted is '是否删除';
comment on column ${SCHEMA}.message_template.create_time is '创建时间';
comment on table ${SCHEMA}.message_template is '消息模版';
-- 创建索引
create index idx_msg_tmpl_tenant on ${SCHEMA}.message_template (tenant_id);
create index idx_msg_tmpl_code ON ${SCHEMA}.message_template(code);

-- 消息表
CREATE TABLE ${SCHEMA}.message (
  id BIGINT identity ( 100000,1 ) primary key,
  tenant_id          BIGINT           default 0  not null,
  app_module         VARCHAR(100),
  template_id        BIGINT,
  business_type       VARCHAR(100)          not null,
  business_code       VARCHAR(100) default 0  not null,
  sender VARCHAR(200)  not null,
  receiver VARCHAR(200) not null,
  title VARCHAR(200) NOT NULL,
  content VARCHAR(1500) NOT NULL,
  channel VARCHAR(50) NOT NULL,
  status VARCHAR(30) NOT NULL,
  result      VARCHAR(600),
  schedule_time  timestamp   null,
  ext_data VARCHAR(600),
  is_deleted   BIT DEFAULT 0    not null,
  create_time  timestamp default CURRENT_TIMESTAMP   not null,
  update_time  timestamp default CURRENT_TIMESTAMP null
);
comment on column ${SCHEMA}.message.id is 'ID';
comment on column ${SCHEMA}.message.tenant_id is '租户ID';
comment on column ${SCHEMA}.message.app_module is '应用模块';
comment on column ${SCHEMA}.message.template_id is '模版id';
comment on column ${SCHEMA}.message.business_type is '业务类型';
comment on column ${SCHEMA}.message.business_code is '业务标识';
comment on column ${SCHEMA}.message.sender is '发送方';
comment on column ${SCHEMA}.message.receiver is '接收方';
comment on column ${SCHEMA}.message.title is '标题';
comment on column ${SCHEMA}.message.content is '内容';
comment on column ${SCHEMA}.message.channel is '发送通道';
comment on column ${SCHEMA}.message.status is '消息状态';
comment on column ${SCHEMA}.message.result is '发送结果';
comment on column ${SCHEMA}.message.schedule_time is '定时发送时间';
comment on column ${SCHEMA}.message.ext_data is '扩展数据';
comment on column ${SCHEMA}.message.is_deleted is '是否删除';
comment on column ${SCHEMA}.message.update_time is '更新时间';
comment on column ${SCHEMA}.message.create_time is '创建时间';
comment on table ${SCHEMA}.message is '消息';
create index idx_msg_tenant on ${SCHEMA}.message (tenant_id);
create index idx_msg_template on ${SCHEMA}.message (template_id);
create index idx_msg_receiver on ${SCHEMA}.message (receiver);
