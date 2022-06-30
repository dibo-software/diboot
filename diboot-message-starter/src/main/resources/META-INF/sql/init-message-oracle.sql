-- 消息模版表
CREATE TABLE ${SCHEMA}.message_template (
     id                 NUMBER(20) generated as identity ( start with 10000 nocycle noorder),
     tenant_id          NUMBER(20)           default 0  not null,
     app_module         VARCHAR2(50),
     code VARCHAR2(20) NOT NULL,
     title VARCHAR2(100) NOT NULL,
     content VARCHAR2(500) NOT NULL,
     ext_data VARCHAR2(500),
     is_deleted   NUMBER(1) DEFAULT 0    not null,
     create_time  timestamp default CURRENT_TIMESTAMP   not null,
     create_by NUMBER(20) DEFAULT 0 NOT NULL,
     update_time  timestamp   default CURRENT_TIMESTAMP null,
     constraint PK_message_template primary key (id)
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
  id NUMBER(20) generated as identity ( start with 100000 nocycle noorder),
  tenant_id          NUMBER(20)           default 0  not null,
  app_module         VARCHAR2(50),
  template_id        NUMBER(20),
  business_type       VARCHAR2(100)          not null,
  business_code       VARCHAR2(100) default 0  not null,
  sender VARCHAR2(100)  not null,
  receiver VARCHAR2(100) not null,
  title VARCHAR2(100) NOT NULL,
  content VARCHAR2(500) NOT NULL,
  channel VARCHAR2(30) NOT NULL,
  status VARCHAR2(30) NOT NULL,
  result      VARCHAR2(200),
  schedule_time  timestamp   null,
  ext_data VARCHAR2(200),
  is_deleted   NUMBER(1) DEFAULT 0    not null,
  create_time  timestamp default CURRENT_TIMESTAMP   not null,
  update_time  timestamp default CURRENT_TIMESTAMP null,
  constraint PK_message primary key (id)
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
