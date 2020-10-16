ALTER TABLE ${SCHEMA}.iam_user ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_account ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_role ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_user_role ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_frontend_permission ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_role_permission ADD tenant_id bigint not null default 0;
ALTER TABLE ${SCHEMA}.iam_login_trace ADD tenant_id bigint not null default 0;

execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'tenant_id';

CREATE nonclustered INDEX idx_iam_user_tenant on iam_user(tenant_id);
CREATE nonclustered INDEX idx_iam_account_tenant on iam_account(tenant_id);
CREATE nonclustered INDEX idx_iam_role_tenant on iam_role(tenant_id);
CREATE nonclustered INDEX idx_iam_user_role_tenant on iam_user_role(tenant_id);
CREATE nonclustered INDEX idx_frontend_permission_tenant on iam_frontend_permission(tenant_id);
CREATE nonclustered INDEX idx_iam_role_permission_tenant on iam_role_permission(tenant_id);
CREATE nonclustered INDEX idx_iam_login_trace_tenant on iam_login_trace(tenant_id);

-- 操作日志表
create table ${SCHEMA}.iam_operation_log
(
  id bigint identity ,
  tenant_id            bigint        not null default 0,
  business_obj varchar(100)  not null,
  operation   varchar(100)  not null,
  user_type varchar(100) default 'IamUser' not null ,
  user_id bigint not null ,
  user_realname    varchar(100)  null,
  request_uri    varchar(500)                  not null,
  request_method varchar(20)                   not null,
  request_params    varchar(1000)              null,
  request_ip   varchar(50)                     null,
  status_code smallint default 0   not null,
  error_msg     varchar(1000)           null,
  is_deleted tinyint default 0 not null ,
  create_time datetime default CURRENT_TIMESTAMP not null,
  constraint PK_iam_operation_log primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'业务对象', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'business_obj';
execute sp_addextendedproperty 'MS_Description', N'操作描述', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'operation';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'用户姓名', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'user_realname';
execute sp_addextendedproperty 'MS_Description', N'请求URI', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'request_uri';
execute sp_addextendedproperty 'MS_Description', N'请求方式', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'request_method';
execute sp_addextendedproperty 'MS_Description', N'请求参数', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'request_params';
execute sp_addextendedproperty 'MS_Description', N'IP', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'request_ip';
execute sp_addextendedproperty 'MS_Description', N'状态码', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'status_code';
execute sp_addextendedproperty 'MS_Description', N'异常信息', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'error_msg';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'操作日志', 'SCHEMA', '${SCHEMA}', 'table', iam_operation_log, null, null;
-- 创建索引
create nonclustered index idx_iam_operation_log on iam_operation_log (user_type, user_id);
create nonclustered index idx_iam_operation_log_tenant on iam_operation_log(tenant_id);