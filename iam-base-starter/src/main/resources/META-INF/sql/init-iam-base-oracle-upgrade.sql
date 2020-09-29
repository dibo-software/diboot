ALTER TABLE ${SCHEMA}.iam_user ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_account ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_role ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_user_role ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_frontend_permission ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_role_permission ADD COLUMN tenant_id NUMBER(20) default 0 not null;
ALTER TABLE ${SCHEMA}.iam_login_trace ADD COLUMN tenant_id NUMBER(20) default 0 not null;

comment on column ${SCHEMA}.iam_user.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_account.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_role.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_user_role.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_frontend_permission.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_role_permission.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_login_trace.tenant_id is '租户ID';

CREATE INDEX idx_iam_user_tenant on ${SCHEMA}.iam_user(tenant_id);
CREATE INDEX idx_iam_account_tenant on ${SCHEMA}.iam_account(tenant_id);
CREATE INDEX idx_iam_role_tenant on ${SCHEMA}.iam_role(tenant_id);
CREATE INDEX idx_iam_user_role_tenant on ${SCHEMA}.iam_user_role(tenant_id);
CREATE INDEX idx_frontend_permission_tenant on ${SCHEMA}.iam_frontend_permission(tenant_id);
CREATE INDEX idx_iam_role_permission_tenant on ${SCHEMA}.iam_role_permission(tenant_id);
CREATE INDEX idx_iam_login_trace_tenant on ${SCHEMA}.iam_login_trace(tenant_id);

-- 操作日志表
create table ${SCHEMA}.iam_operation_log
(
  id NUMBER(20) generated as identity ( start with 100000 nocycle noorder) ,
  tenant_id          NUMBER(20)           default 0  not null,
  business_obj VARCHAR2(100)  not null,
  operation   VARCHAR2(100)  not null,
  user_type VARCHAR2(100) DEFAULT 'IamUser'    not null,
  user_id NUMBER(20)    not null,
  user_realname    VARCHAR2(100)  null,
  request_uri    VARCHAR2(500)                  not null,
  request_method VARCHAR2(20)                   not null,
  request_params    VARCHAR2(1000)              null,
  request_ip   VARCHAR2(50)                     null,
  status_code   NUMBER(6)   default 0   not null,
  error_msg     VARCHAR2(1000)           null,
  is_deleted NUMBER(1) DEFAULT 0    not null,
  create_time timestamp default CURRENT_TIMESTAMP   not null,
  constraint PK_iam_operation_log primary key (id)
);
comment on column ${SCHEMA}.iam_operation_log.id is 'ID';
comment on column ${SCHEMA}.iam_operation_log.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_operation_log.business_obj is '业务对象';
comment on column ${SCHEMA}.iam_operation_log.operation is '操作描述';
comment on column ${SCHEMA}.iam_operation_log.user_type is '用户类型';
comment on column ${SCHEMA}.iam_operation_log.user_id is '用户ID';
comment on column ${SCHEMA}.iam_operation_log.user_realname is '用户姓名';
comment on column ${SCHEMA}.iam_operation_log.request_uri is '请求URI';
comment on column ${SCHEMA}.iam_operation_log.request_method is '请求方式';
comment on column ${SCHEMA}.iam_operation_log.request_params is '请求参数';
comment on column ${SCHEMA}.iam_operation_log.request_ip is 'IP';
comment on column ${SCHEMA}.iam_operation_log.status_code is '状态码';
comment on column ${SCHEMA}.iam_operation_log.error_msg is '异常信息';
comment on column ${SCHEMA}.iam_operation_log.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_operation_log.create_time is '创建时间';
comment on table ${SCHEMA}.iam_operation_log is '操作日志';
-- 创建索引
create index idx_iam_operation_log on ${SCHEMA}.iam_operation_log (user_type, user_id);
create index idx_iam_operation_log_tenant on ${SCHEMA}.iam_operation_log (tenant_id);