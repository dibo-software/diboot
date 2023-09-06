-- 用户表
create table ${SCHEMA}.dbt_iam_user
(
   id varchar(32) not null,
   tenant_id  varchar(32) not null default '0',
   org_id varchar(32) not null default 0,
   user_num varchar(20) not null,
   realname varchar(50) not null,
   gender varchar(10) not null,
   birthdate date null,
   mobile_phone varchar(20) null,
   email varchar(50) null,
   avatar_url varchar(200) null,
   status varchar(10) not null default 'A',
   is_deleted tinyint not null DEFAULT 0,
   create_time datetime not null default CURRENT_TIMESTAMP,
   update_time datetime null default CURRENT_TIMESTAMP,
   constraint PK_dbt_iam_user primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'组织ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'org_id';
execute sp_addextendedproperty 'MS_Description', N'用户编号', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'user_num';
execute sp_addextendedproperty 'MS_Description', N'真实姓名', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'realname';
execute sp_addextendedproperty 'MS_Description', N'性别', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'gender';
execute sp_addextendedproperty 'MS_Description', N'出生日期', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'birthdate';
execute sp_addextendedproperty 'MS_Description', N'手机号', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'mobile_phone';
execute sp_addextendedproperty 'MS_Description', N'Email', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'email';
execute sp_addextendedproperty 'MS_Description', N'头像', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'avatar_url';
execute sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'系统用户', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user, null, null;
-- 索引
create nonclustered index idx_dbt_iam_user_1 on iam_user (org_id);
create nonclustered index idx_iam_user_2 on iam_user (mobile_phone);
create nonclustered index idx_iam_user_num on iam_user (user_num);
create nonclustered index idx_iam_user_tenant on iam_user(tenant_id);

-- 账号表
create table ${SCHEMA}.dbt_iam_account
(
    id varchar(32) not null,
    tenant_id  varchar(32) not null default '0',
    user_type varchar(100) default 'IamUser' not null,
    user_id varchar(32) not null,
    auth_type varchar(20) default 'PWD' not null,
    auth_account varchar(100) not null,
    auth_secret varchar(100) null,
    secret_salt varchar(32) null,
    status varchar(10) default 'A' not null,
    is_deleted tinyint default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime null default CURRENT_TIMESTAMP,
    constraint PK_dbt_iam_account primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'认证方式', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'auth_type';
execute sp_addextendedproperty 'MS_Description', N'用户名', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'auth_account';
execute sp_addextendedproperty 'MS_Description', N'密码', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'auth_secret';
execute sp_addextendedproperty 'MS_Description', N'加密盐', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'secret_salt';
execute sp_addextendedproperty 'MS_Description', N'用户状态', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'登录账号', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_account, null, null;
-- 创建索引
create index idx_dbt_iam_account on dbt_iam_account(auth_account, auth_type, user_type);
create nonclustered index idx_dbt_iam_account_tenant on dbt_iam_account(tenant_id);

-- 角色表
create table ${SCHEMA}.dbt_iam_role
(
    id varchar(32) not null,
    tenant_id  varchar(32) not null default '0',
    name varchar(50) not null,
    code varchar(50) not null,
    description varchar(100) null,
    is_deleted tinyint default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime null default CURRENT_TIMESTAMP,
    constraint PK_iam_role primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'名称', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'name';
execute sp_addextendedproperty 'MS_Description', N'编码', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'角色', 'SCHEMA', '${SCHEMA}', 'table', iam_role, null, null;
-- 创建索引
create nonclustered index idx_iam_role_tenant on iam_role(tenant_id);

-- 用户角色表
create table ${SCHEMA}.dbt_iam_user_role
(
    id varchar(32) not null,
    tenant_id  varchar(32) not null default '0',
    user_type varchar(100) default 'IamUser' not null,
    user_id varchar(32) not null,
    role_id varchar(32) not null,
    is_deleted tinyint default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime null default CURRENT_TIMESTAMP,
    constraint PK_dbt_iam_user_role primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'role_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'用户角色关联', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_role, null, null;
-- 索引
create nonclustered index idx_dbt_iam_user_role on dbt_iam_user_role (user_type, user_id);
create nonclustered index idx_dbt_iam_user_role_tenant on dbt_iam_user_role(tenant_id);

-- 资源权限表
create table ${SCHEMA}.dbt_iam_resource
(
    id varchar(32) not null,
    tenant_id  varchar(32) not null default '0',
    app_module varchar(50),
    parent_id varchar(32) default 0   not null,
    display_type varchar(20) not null,
    display_name varchar(100) not null,
    display_name_i18n varchar(200) null,
    route_path        varchar(200) null,
    resource_code varchar(100)   null,
    permission_code varchar(200)   null,
    meta              varchar(300) null,
    sort_id bigint  default 0 not null,
    status            varchar(10) default 'A',
    is_deleted tinyint default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP null,
    constraint PK_dbt_iam_resource primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块','SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'父资源ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'展现类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'display_type';
execute sp_addextendedproperty 'MS_Description', N'显示名称', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'display_name';
execute sp_addextendedproperty 'MS_Description', N'显示名称国际化资源标识', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'display_name';
execute sp_addextendedproperty 'MS_Description', N'前端编码', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'resource_code';
execute sp_addextendedproperty 'MS_Description', N'权限码', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'permission_code';
execute sp_addextendedproperty 'MS_Description', N'路由地址', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'route_path';
execute sp_addextendedproperty 'MS_Description', N'meta配置', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'meta';
execute sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'排序号', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'资源权限表', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_resource, null, null;

-- 索引
create nonclustered index idx_dbt_iam_resource on dbt_iam_resource (parent_id);
create nonclustered index idx_resource_permission_tenant on dbt_iam_resource(tenant_id);

-- 角色-权限
create table ${SCHEMA}.dbt_iam_role_resource
(
    id varchar(32) not null ,
    tenant_id  varchar(32) not null default '0',
    role_id varchar(32) not null,
    resource_id varchar(32) not null,
    is_deleted tinyint default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    constraint PK_dbt_iam_role_resource primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'role_id';
execute sp_addextendedproperty 'MS_Description', N'资源ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'resource_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'角色权限', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_role_resource, null, null;
-- 索引
create nonclustered index idx_dbt_iam_role_resource on dbt_iam_role_resource (role_id, resource_id);
create nonclustered index idx_dbt_iam_role_resource_tenant on dbt_iam_role_resource(tenant_id);

-- 登录日志表
create table ${SCHEMA}.dbt_iam_login_trace
(
    id varchar(32) not null ,
    tenant_id  varchar(32) not null default '0',
    user_type varchar(100) default 'IamUser' not null,
    user_id varchar(32) not null,
    auth_type varchar(20) default 'PWD' not null,
    auth_account varchar(100) not null,
    ip_address varchar(50) null,
    user_agent varchar(200) null,
    is_success tinyint default 0 not null,
    logout_time datetime null,
    create_time datetime default CURRENT_TIMESTAMP not null,
    constraint PK_dbt_iam_login_trace primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'认证方式', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'auth_type';
execute sp_addextendedproperty 'MS_Description', N'用户名', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'auth_account';
execute sp_addextendedproperty 'MS_Description', N'IP', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'ip_address';
execute sp_addextendedproperty 'MS_Description', N'客户端信息', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'user_agent';
execute sp_addextendedproperty 'MS_Description', N'是否成功', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'is_success';
execute sp_addextendedproperty 'MS_Description', N'退出时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'logout_time';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'登录日志', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_login_trace, null, null;
-- 创建索引
create nonclustered index idx_dbt_iam_login_trace on dbt_iam_login_trace (user_type, user_id);
create nonclustered index idx_dbt_iam_login_trace_2 on dbt_iam_login_trace (auth_account);
create nonclustered index idx_dbt_iam_login_trace_tenant on dbt_iam_login_trace(tenant_id);

-- 操作日志表
create table ${SCHEMA}.dbt_iam_operation_log
(
   id  varchar(32) not null,
   tenant_id  varchar(32) not null default '0',
   app_module   varchar(50),
   business_obj varchar(100)  not null,
   operation   varchar(100)  not null,
   user_type varchar(100) default 'IamUser' not null ,
   user_id varchar(32) not null ,
   user_realname    varchar(100)  null,
   request_uri    varchar(500)                  not null,
   request_method varchar(20)                   not null,
   request_params    varchar(1000)              null,
   request_ip   varchar(50)                     null,
   status_code smallint default 0   not null,
   error_msg     varchar(1000)           null,
   is_deleted tinyint default 0 not null ,
   create_time datetime default CURRENT_TIMESTAMP not null,
   constraint PK_dbt_iam_operation_log primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块','SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'业务对象', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'business_obj';
execute sp_addextendedproperty 'MS_Description', N'操作描述', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'operation';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'用户姓名', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'user_realname';
execute sp_addextendedproperty 'MS_Description', N'请求URI', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'request_uri';
execute sp_addextendedproperty 'MS_Description', N'请求方式', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'request_method';
execute sp_addextendedproperty 'MS_Description', N'请求参数', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'request_params';
execute sp_addextendedproperty 'MS_Description', N'IP', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'request_ip';
execute sp_addextendedproperty 'MS_Description', N'状态码', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'status_code';
execute sp_addextendedproperty 'MS_Description', N'异常信息', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'error_msg';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'操作日志', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_operation_log, null, null;
-- 创建索引
create nonclustered index idx_dbt_iam_operation_log on dbt_iam_operation_log (user_type, user_id);
create nonclustered index idx_dbt_iam_operation_log_tenant on dbt_iam_operation_log(tenant_id);

-- 部门表
CREATE TABLE ${SCHEMA}.dbt_iam_org (
    id varchar(32) not null,
    tenant_id varchar(32) default '0' not null,
    parent_id varchar(32) DEFAULT '0' NOT NULL,
    parent_ids_path varchar(32) NULL,
    root_org_id varchar(32) DEFAULT '0' NOT NULL,
    name varchar(100) NOT NULL,
    type        varchar(100) DEFAULT 'DEPT' NOT NULL,
    code        varchar(50)  NOT NULL,
    manager_id varchar(32)  DEFAULT '0' NOT NULL,
    sort_id bigint DEFAULT 1 NOT NULL,
    status      varchar(10)  DEFAULT 'A' NOT NULL,
    org_comment varchar(255) null,
    is_deleted tinyint DEFAULT 0    not null,
    create_time datetime default CURRENT_TIMESTAMP   not null,
    update_time datetime null default CURRENT_TIMESTAMP,
    constraint PK_dbt_iam_org primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'上级ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'上级ID路径','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'parent_ids_path';
execute sp_addextendedproperty 'MS_Description', N'企业ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'root_org_id';
execute sp_addextendedproperty 'MS_Description', N'名称','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'name';
execute sp_addextendedproperty 'MS_Description', N'类型','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'type';
execute sp_addextendedproperty 'MS_Description', N'编码','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'负责人','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'manager_id';
execute sp_addextendedproperty 'MS_Description', N'排序号','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'状态','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'备注','SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'org_comment';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'部门', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_org, null, null;
-- 创建索引
create nonclustered index idx_dbt_iam_org on dbt_iam_org (parent_id);
create nonclustered index idx_dbt_iam_org_tenant on dbt_iam_org (tenant_id);
create nonclustered index idx_dbt_iam_org_parent_path on iam_org (parent_ids_path);

-- 岗位
create table ${SCHEMA}.dbt_iam_position
(
    id varchar(32) not null,
    tenant_id varchar(32) default '0' not null,
    name                 varchar(100)                          not null,
    code                 varchar(50)                           not null,
    is_virtual           tinyint  default 0                 not null,
    grade_name           varchar(50)                           null,
    grade_value          varchar(30) default '0'               null,
    data_permission_type varchar(20) default 'SELF'            null,
    is_deleted tinyint DEFAULT 0    not null,
    create_time datetime default CURRENT_TIMESTAMP   not null,
    update_time datetime null default CURRENT_TIMESTAMP,
    constraint PK_dbt_iam_position primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'名称','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'name';
execute sp_addextendedproperty 'MS_Description', N'编码','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'是否虚拟岗','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'is_virtual';
execute sp_addextendedproperty 'MS_Description', N'职级头衔','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'grade_name';
execute sp_addextendedproperty 'MS_Description', N'职级','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'grade_value';
execute sp_addextendedproperty 'MS_Description', N'数据权限类型','SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'data_permission_type';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'岗位', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_position, null, null;
-- 创建索引
create nonclustered index idx_dbt_iam_position on dbt_iam_position (code);
create nonclustered index idx_dbt_iam_position_tenant on dbt_iam_position (tenant_id);

-- 用户岗位
create table dbt_iam_user_position
(
    id  varchar(32) not null,
    tenant_id varchar(32) default '0' not null,
    user_type           varchar(100) default 'IamUser' not null,
    user_id             varchar(32)  not null,
    org_id              varchar(32)  not null,
    position_id         varchar(32) not null,
    is_primary_position tinyint   default 1 not null,
    is_deleted tinyint DEFAULT 0    not null,
    create_time datetime default CURRENT_TIMESTAMP   not null,
    update_time datetime default CURRENT_TIMESTAMP null,
    constraint PK_dbt_iam_user_position primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'用户类型','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'组织ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'org_id';
execute sp_addextendedproperty 'MS_Description', N'岗位ID','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'position_id';
execute sp_addextendedproperty 'MS_Description', N'是否主岗','SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'is_primary_position';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'用户岗位关联', 'SCHEMA', '${SCHEMA}', 'table', dbt_iam_user_position, null, null;
-- 创建索引
create nonclustered index idx_dbt_iam_user_position on dbt_iam_user_position (user_type, user_id);
create nonclustered index idx_dbt_iam_user_position_pos on dbt_iam_user_position (position_id);

-- 系统配置表
create table ${SCHEMA}.dbt_system_config
(
    id          varchar(32)  not null,
    tenant_id   varchar(32)  not null default '0',
    category        varchar(50)  null,
    prop_key        varchar(50)  not null,
    prop_value       varchar(255) null,
    data_type       varchar(50) not null default 'text',
    is_deleted  tinyint      not null default 0,
    create_time datetime     not null default CURRENT_TIMESTAMP,
    update_time datetime     null default CURRENT_TIMESTAMP,
    constraint PK_dbt_system_config primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'类别','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'category';
execute sp_addextendedproperty 'MS_Description', N'属性名','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'prop_key';
execute sp_addextendedproperty 'MS_Description', N'属性值','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'prop_value';
execute sp_addextendedproperty 'MS_Description', N'数据类型','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'data_type';
execute sp_addextendedproperty 'MS_Description', N'删除标记','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, 'column', 'update_time';

execute sp_addextendedproperty 'MS_Description', N'系统配置','SCHEMA', '${SCHEMA}', 'table', dbt_system_config, null, null;
-- 创建索引
create nonclustered index idx_dbt_system_config on ${SCHEMA}.dbt_system_config(category, prop_key);
create nonclustered index idx_dbt_system_config_tenant on ${SCHEMA}.dbt_system_config(tenant_id);
