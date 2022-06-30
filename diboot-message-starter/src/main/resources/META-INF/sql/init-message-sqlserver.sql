-- 消息模版表
CREATE TABLE message_template (
     id bigint identity,
     tenant_id          bigint           default 0  not null,
     app_module         VARCHAR(50),
     code VARCHAR(20) NOT NULL,
     title VARCHAR(100) NOT NULL,
     content VARCHAR(500) NOT NULL,
     ext_data VARCHAR(500),
     is_deleted   tinyint not null DEFAULT 0,
     create_by bigint DEFAULT 0 NOT NULL,
     create_time  datetime default CURRENT_TIMESTAMP   not null,
     update_time  datetime default CURRENT_TIMESTAMP  null,
     constraint PK_message_template primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'模版编码', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'模版标题', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'title';
execute sp_addextendedproperty 'MS_Description', N'模版内容', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'content';
execute sp_addextendedproperty 'MS_Description', N'扩展数据', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'ext_data';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建人', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'create_by';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', message_template, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'消息模版', 'SCHEMA', '${SCHEMA}', 'table', message_template, null, null;

-- 创建索引
create nonclustered index idx_msg_tmpl_tenant on message_template (tenant_id);
create nonclustered index idx_msg_tmpl_code ON message_template(code);

-- 消息表
CREATE TABLE message (
  id bigint identity,
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
  schedule_time  datetime   null,
  ext_data VARCHAR(200),
  is_deleted   tinyint not null DEFAULT 0,
  create_time  datetime default CURRENT_TIMESTAMP  not null,
  update_time  datetime default CURRENT_TIMESTAMP  null,
  constraint PK_message primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', message, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块','SCHEMA', '${SCHEMA}', 'table', message, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'模版id', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'template_id';
execute sp_addextendedproperty 'MS_Description', N'业务类型', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'business_type';
execute sp_addextendedproperty 'MS_Description', N'业务标识', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'business_code';
execute sp_addextendedproperty 'MS_Description', N'发送方', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'sender';
execute sp_addextendedproperty 'MS_Description', N'接收方', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'receiver';
execute sp_addextendedproperty 'MS_Description', N'标题', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'title';
execute sp_addextendedproperty 'MS_Description', N'内容', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'content';
execute sp_addextendedproperty 'MS_Description', N'发送通道', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'channel';
execute sp_addextendedproperty 'MS_Description', N'发送状态', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'发送结果', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'result';
execute sp_addextendedproperty 'MS_Description', N'定时发送时间', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'schedule_time';
execute sp_addextendedproperty 'MS_Description', N'扩展数据', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'ext_data';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', message, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'消息', 'SCHEMA', '${SCHEMA}', 'table', message, null, null;
-- 添加索引
create nonclustered index idx_msg_tenant on message (tenant_id);
create nonclustered index idx_msg_template on message (template_id);
create nonclustered index idx_msg_receiver on message (receiver);
