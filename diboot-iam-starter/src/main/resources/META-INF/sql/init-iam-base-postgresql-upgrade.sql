ALTER TABLE iam_user ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_account ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_role ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_user_role ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_frontend_permission ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_role_permission ADD COLUMN tenant_id bigint not null default 0;
ALTER TABLE iam_login_trace ADD COLUMN tenant_id bigint not null default 0;

comment on column iam_user.tenant_id is '租户ID';
comment on column iam_account.tenant_id is '租户ID';
comment on column iam_role.tenant_id is '租户ID';
comment on column iam_user_role.tenant_id is '租户ID';
comment on column iam_frontend_permission.tenant_id is '租户ID';
comment on column iam_role_permission.tenant_id is '租户ID';
comment on column iam_login_trace.tenant_id is '租户ID';

create index idx_iam_user_tenant on iam_user(tenant_id);
CREATE INDEX idx_iam_account_tenant on iam_account(tenant_id);
CREATE INDEX idx_iam_role_tenant on iam_role(tenant_id);
CREATE INDEX idx_iam_user_role_tenant on iam_user_role(tenant_id);
CREATE INDEX idx_frontend_permission_tenant on iam_frontend_permission(tenant_id);
CREATE INDEX idx_iam_role_permission_tenant on iam_role_permission(tenant_id);
CREATE INDEX idx_iam_login_trace_tenant on iam_login_trace(tenant_id);

-- 操作日志表
create table iam_operation_log
(
  id bigserial not null ,
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
  status_code   smallint   default 0   not null,
  error_msg     varchar(1000)           null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column iam_operation_log.id is 'ID';
comment on column iam_operation_log.tenant_id is '租户ID';
comment on column iam_operation_log.business_obj is '业务对象';
comment on column iam_operation_log.operation is '操作描述';
comment on column iam_operation_log.user_type is '用户类型';
comment on column iam_operation_log.user_id is '用户ID';
comment on column iam_operation_log.user_realname is '用户姓名';
comment on column iam_operation_log.request_uri is '请求URI';
comment on column iam_operation_log.request_method is '请求方式';
comment on column iam_operation_log.request_params is '请求参数';
comment on column iam_operation_log.request_ip is 'IP';
comment on column iam_operation_log.status_code is '状态码';
comment on column iam_operation_log.error_msg is '异常信息';
comment on column iam_operation_log.is_deleted is '是否删除';
comment on column iam_operation_log.create_time is '创建时间';
comment on table iam_operation_log is '操作日志';
-- 创建索引
create index idx_iam_operation_log on iam_operation_log (user_type, user_id);
create index idx_iam_operation_log_tenant on iam_operation_log(tenant_id);