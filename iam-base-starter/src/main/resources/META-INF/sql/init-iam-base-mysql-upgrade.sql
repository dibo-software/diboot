ALTER TABLE iam_user ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_account ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_role ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_user_role ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_frontend_permission ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_role_permission ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
ALTER TABLE iam_login_trace ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;

CREATE INDEX idx_iam_user_tenant on iam_user(tenant_id);
CREATE INDEX idx_iam_account_tenant on iam_account(tenant_id);
CREATE INDEX idx_iam_role_tenant on iam_role(tenant_id);
CREATE INDEX idx_iam_user_role_tenant on iam_user_role(tenant_id);
CREATE INDEX idx_frontend_permission_tenant on iam_frontend_permission(tenant_id);
CREATE INDEX idx_iam_role_permission_tenant on iam_role_permission(tenant_id);
CREATE INDEX idx_iam_login_trace_tenant on iam_login_trace(tenant_id);

-- 操作日志表
create table iam_operation_log
(
  id           bigint auto_increment comment 'ID'   primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  business_obj    varchar(100)  not null comment '业务对象',
  operation   varchar(100)  not null comment '操作描述',
  user_type    varchar(100) default 'IamUser'       null comment '用户类型',
  user_id      bigint                                 null comment '用户ID',
  user_realname    varchar(100)          null   comment '用户姓名',
  request_uri    varchar(500)                  not null comment '请求URI',
  request_method varchar(20)                           not null comment '请求方式',
  request_params    varchar(1000)                  null comment '请求参数',
  request_ip   varchar(50)                           null comment 'IP',
  status_code   smallint   default 0   not null comment '状态码',
  error_msg     varchar(1000)                  null comment '异常信息',
  is_deleted       tinyint(1)                            null comment '删除标记',
  create_time      timestamp   default CURRENT_TIMESTAMP null comment '创建时间'
)
  AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '操作日志';
-- 创建索引
create index idx_iam_operation_log on iam_operation_log (user_type, user_id);
create index idx_iam_operation_log_tenant on iam_operation_log (tenant_id);