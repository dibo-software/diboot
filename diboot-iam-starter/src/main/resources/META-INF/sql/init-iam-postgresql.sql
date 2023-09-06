-- 用户表
create table dbt_iam_user
(
  id varchar(32) not null primary key,
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
  is_deleted BOOLEAN not null DEFAULT FALSE,
  create_time  timestamp   not null default CURRENT_TIMESTAMP,
  update_time timestamp   null default CURRENT_TIMESTAMP
);
-- 添加备注
comment on column dbt_iam_user.id is 'ID';
comment on column dbt_iam_user.tenant_id is '租户ID';
comment on column dbt_iam_user.org_id is '组织ID';
comment on column dbt_iam_user.user_num is '用户编号';
comment on column dbt_iam_user.realname is '真实姓名';
comment on column dbt_iam_user.gender is '性别';
comment on column dbt_iam_user.birthdate is '出生日期';
comment on column dbt_iam_user.mobile_phone is '手机号';
comment on column dbt_iam_user.email is 'Email';
comment on column dbt_iam_user.avatar_url is '头像';
comment on column dbt_iam_user.status is '状态';
comment on column dbt_iam_user.is_deleted is '删除标记';
comment on column dbt_iam_user.create_time is '创建时间';
comment on column dbt_iam_user.update_time is '更新时间';
comment on table dbt_iam_user is '系统用户';
-- 索引
create index idx_dbt_iam_user_1 on dbt_iam_user (org_id);
create index idx_dbt_iam_user_2 on dbt_iam_user (mobile_phone);
create index idx_iam_user_num on iam_user (user_num);
create index idx_iam_user_tenant on iam_user(tenant_id);

-- 账号表
create table dbt_iam_account
(
  id varchar(32) not null primary key,
  tenant_id  varchar(32) not null default '0',
  user_type varchar(100) default 'IamUser' not null,
  user_id varchar(32) not null,
  auth_type varchar(20) default 'PWD' not null,
  auth_account varchar(100) not null,
  auth_secret varchar(100) null,
  secret_salt varchar(32) null,
  status varchar(10) default 'A' not null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null,
  update_time timestamp   null default CURRENT_TIMESTAMP
);
comment on column dbt_iam_account.id is 'ID';
comment on column dbt_iam_account.tenant_id is '租户ID';
comment on column dbt_iam_account.user_type is '用户类型';
comment on column dbt_iam_account.user_id is '用户ID';
comment on column dbt_iam_account.auth_type is '认证方式';
comment on column dbt_iam_account.auth_account is '用户名';
comment on column dbt_iam_account.auth_secret is '密码';
comment on column dbt_iam_account.secret_salt is '加密盐';
comment on column dbt_iam_account.status is '用户状态';
comment on column dbt_iam_account.is_deleted is '是否删除';
comment on column dbt_iam_account.create_time is '创建时间';
comment on column dbt_iam_account.update_time is '更新时间';
comment on table dbt_iam_account is '登录账号';
-- 创建索引
create index idx_iam_account on dbt_iam_account(auth_account, auth_type, user_type);
create index idx_iam_account_tenant on dbt_iam_account(tenant_id);

-- 角色表
create table dbt_iam_role
(
  id varchar(32) not null primary key,
  tenant_id  varchar(32) not null default '0',
  name varchar(50) not null,
  code varchar(50) not null,
  description varchar(100) null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP null,
  update_time timestamp   null default CURRENT_TIMESTAMP
);
comment on column dbt_iam_role.id is 'ID';
comment on column dbt_iam_role.tenant_id is '租户ID';
comment on column dbt_iam_role.name is '名称';
comment on column dbt_iam_role.code is '编码';
comment on column dbt_iam_role.description is '备注';
comment on column dbt_iam_role.is_deleted is '是否删除';
comment on column dbt_iam_role.create_time is '创建时间';
comment on column dbt_iam_role.update_time is '更新时间';
comment on table iam_role is '角色';
-- 创建索引
create index idx_iam_role_tenant on dbt_iam_role(tenant_id);

-- 用户角色表
create table dbt_iam_user_role
(
  id  varchar(32) not null,
  tenant_id  varchar(32) not null default '0',
  user_type varchar(100) default 'IamUser' not null,
  user_id varchar(32) not null,
  role_id varchar(32) not null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null,
  update_time timestamp   null default CURRENT_TIMESTAMP
);
comment on column dbt_iam_user_role.id is 'ID';
comment on column dbt_iam_user_role.tenant_id is '租户ID';
comment on column dbt_iam_user_role.user_type is '用户类型';
comment on column dbt_iam_user_role.user_id is '用户ID';
comment on column dbt_iam_user_role.role_id is '角色ID';
comment on column dbt_iam_user_role.is_deleted is '是否删除';
comment on column dbt_iam_user_role.create_time is '创建时间';
comment on column dbt_iam_user_role.update_time is '更新时间';
comment on table dbt_iam_user_role is '用户角色关联';
-- 索引
create index idx_dbt_iam_user_role on dbt_iam_user_role (user_type, user_id);
create index idx_dbt_iam_user_role_tenant on dbt_iam_user_role(tenant_id);

-- 资源权限表
create table dbt_iam_resource
(
  id  varchar(32) not null,
  parent_id varchar(32) default 0   not null,
  tenant_id  varchar(32) not null default '0',
  app_module          varchar(50),
  display_type varchar(20) not null,
  display_name varchar(100) not null,
  display_name_i18n varchar(200) null,
  route_path        varchar(200) null,
  resource_code varchar(100)   null,
  permission_code varchar(200)   null,
  meta              varchar(300) null,
  sort_id bigint  default 0  not null,
  status            varchar(10) default 'A',
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null,
  update_time timestamp default CURRENT_TIMESTAMP null,
  constraint PK_dbt_iam_resource primary key (id)
);
comment on column dbt_iam_resource.id is 'ID';
comment on column dbt_iam_resource.tenant_id is '租户ID';
comment on column dbt_iam_resource.app_module is '应用模块';
comment on column dbt_iam_resource.parent_id is '父资源ID';
comment on column dbt_iam_resource.display_type is '展现类型';
comment on column dbt_iam_resource.display_name is '显示名称';
comment on column dbt_iam_resource.display_name is '显示名称国际化资源标识';
comment on column dbt_iam_resource.resource_code is '前端编码';
comment on column dbt_iam_resource.permission_code is '权限码';
comment on column dbt_iam_resource.route_path is '路由地址';
comment on column dbt_iam_resource.meta is 'meta配置';
comment on column dbt_iam_resource.status is '状态';
comment on column dbt_iam_resource.sort_id is '排序号';
comment on column dbt_iam_resource.is_deleted is '是否删除';
comment on column dbt_iam_resource.create_time is '创建时间';
comment on column dbt_iam_resource.update_time is '更新时间';
comment on table dbt_iam_resource is '资源权限表';
-- 索引
create index idx_dbt_iam_resource on dbt_iam_resource (parent_id);
create index idx_resource_permission_tenant on dbt_iam_resource(tenant_id);

-- 角色-权限
create table dbt_iam_role_resource
(
  id  varchar(32) not null ,
  tenant_id  varchar(32) not null default '0',
  role_id varchar(32) not null ,
  resource_id varchar(32) not null ,
  is_deleted BOOLEAN default FALSE not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column dbt_iam_role_resource.id is 'ID';
comment on column dbt_iam_role_resource.tenant_id is '租户ID';
comment on column dbt_iam_role_resource.role_id is '角色ID';
comment on column dbt_iam_role_resource.resource_id is '权限ID';
comment on column dbt_iam_role_resource.is_deleted is '是否删除';
comment on column dbt_iam_role_resource.create_time is '创建时间';
comment on table dbt_iam_role_resource is '角色权限';
-- 索引
create index idx_dbt_iam_role_resource on dbt_iam_role_resource (role_id, resource_id);
create index idx_dbt_iam_role_resource_tenant on dbt_iam_role_resource(tenant_id);

-- 登录日志表
create table dbt_iam_login_trace
(
  id  varchar(32) not null ,
  tenant_id  varchar(32) not null default '0',
  user_type varchar(100) default 'IamUser' not null ,
  user_id varchar(32) not null ,
  auth_type varchar(20) default 'PWD' not null ,
  auth_account varchar(100) not null ,
  ip_address varchar(50) null ,
  user_agent varchar(200) null ,
  is_success BOOLEAN default FALSE not null ,
  logout_time timestamp null,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column dbt_iam_login_trace.id is 'ID';
comment on column dbt_iam_login_trace.tenant_id is '租户ID';
comment on column dbt_iam_login_trace.user_type is '用户类型';
comment on column dbt_iam_login_trace.user_id is '用户ID';
comment on column dbt_iam_login_trace.auth_type is '认证方式';
comment on column dbt_iam_login_trace.auth_account is '用户名';
comment on column dbt_iam_login_trace.ip_address is 'IP';
comment on column dbt_iam_login_trace.user_agent is '客户端信息';
comment on column dbt_iam_login_trace.is_success is '是否成功';
comment on column dbt_iam_login_trace.logout_time is '退出时间';
comment on column dbt_iam_login_trace.create_time is '创建时间';
comment on table dbt_iam_login_trace is '登录日志';
-- 创建索引
create index idx_dbt_iam_login_trace on dbt_iam_login_trace (user_type, user_id);
create index idx_dbt_iam_login_trace_2 on dbt_iam_login_trace (auth_account);
create index idx_dbt_iam_login_trace_tenant on dbt_iam_login_trace(tenant_id);

-- 操作日志表
create table dbt_iam_operation_log
(
  id  varchar(32) not null ,
  tenant_id  varchar(32) not null default '0',
  app_module          varchar(50),
  business_obj varchar(100)  not null,
  operation   varchar(100)  not null,
  user_type varchar(100) default 'IamUser' not null ,
  user_id varchar(32) not null ,
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
comment on column dbt_iam_operation_log.id is 'ID';
comment on column dbt_iam_operation_log.tenant_id is '租户ID';
comment on column dbt_iam_operation_log.app_module is '应用模块';
comment on column dbt_iam_operation_log.business_obj is '业务对象';
comment on column dbt_iam_operation_log.operation is '操作描述';
comment on column dbt_iam_operation_log.user_type is '用户类型';
comment on column dbt_iam_operation_log.user_id is '用户ID';
comment on column dbt_iam_operation_log.user_realname is '用户姓名';
comment on column dbt_iam_operation_log.request_uri is '请求URI';
comment on column dbt_iam_operation_log.request_method is '请求方式';
comment on column dbt_iam_operation_log.request_params is '请求参数';
comment on column dbt_iam_operation_log.request_ip is 'IP';
comment on column dbt_iam_operation_log.status_code is '状态码';
comment on column dbt_iam_operation_log.error_msg is '异常信息';
comment on column dbt_iam_operation_log.is_deleted is '是否删除';
comment on column dbt_iam_operation_log.create_time is '创建时间';
comment on table dbt_iam_operation_log is '操作日志';
-- 创建索引
create index idx_dbt_iam_operation_log on dbt_iam_operation_log (user_type, user_id);
create index idx_dbt_iam_operation_log_tenant on dbt_iam_operation_log(tenant_id);

-- 部门表
CREATE TABLE dbt_iam_org
(
    id varchar(32) not null primary key,
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
    org_comment varchar(255)   null,
    is_deleted BOOLEAN DEFAULT FALSE  not null,
    create_time timestamp default CURRENT_TIMESTAMP   not null,
    update_time timestamp   null default CURRENT_TIMESTAMP
);
comment on column dbt_iam_org.id is 'ID';
comment on column dbt_iam_org.tenant_id is '租户ID';
comment on column dbt_iam_org.parent_id is '上级ID';
comment on column dbt_iam_org.parent_ids_path  is '上级ID路径';
comment on column dbt_iam_org.root_org_id is '企业ID';
comment on column dbt_iam_org.name is '名称';
comment on column dbt_iam_org.type is '类型';
comment on column dbt_iam_org.code is '编码';
comment on column dbt_iam_org.manager_id is '负责人';
comment on column dbt_iam_org.sort_id is '排序号';
comment on column dbt_iam_org.status is '状态';
comment on column dbt_iam_org.org_comment is '备注';
comment on column dbt_iam_org.is_deleted is '是否删除';
comment on column dbt_iam_org.create_time is '创建时间';
comment on column dbt_iam_org.update_time is '更新时间';
comment on table dbt_iam_org is '部门';
create index idx_dbt_iam_org on dbt_iam_org (parent_id);
create index idx_dbt_iam_org_tenant on dbt_iam_org (tenant_id);
create index idx_dbt_iam_org_parent_path on dbt_iam_org (parent_ids_path);

-- 岗位
create table dbt_iam_position
(
  id varchar(32) not null primary key,
  tenant_id varchar(32) default '0' not null,
  name                 varchar(100)                          not null,
  code                 varchar(50)                           not null,
  is_virtual           BOOLEAN  default FALSE          not null,
  grade_name           varchar(50)                           null,
  grade_value          varchar(30) default '0'               null,
  data_permission_type varchar(20) default 'SELF'            null,
  is_deleted BOOLEAN DEFAULT FALSE  not null,
  create_time timestamp default CURRENT_TIMESTAMP   not null,
  update_time timestamp   null default CURRENT_TIMESTAMP
);
comment on column dbt_iam_position.id is 'ID';
comment on column dbt_iam_position.tenant_id is '租户ID';
comment on column dbt_iam_position.name is '名称';
comment on column dbt_iam_position.code is '编码';
comment on column dbt_iam_position.is_virtual is '是否虚拟岗';
comment on column dbt_iam_position.grade_name is '职级头衔';
comment on column dbt_iam_position.grade_value is '职级';
comment on column dbt_iam_position.data_permission_type is '数据权限类型';
comment on column dbt_iam_position.is_deleted is '是否删除';
comment on column dbt_iam_position.create_time is '创建时间';
comment on column dbt_iam_position.update_time is '更新时间';
comment on table dbt_iam_position is '岗位';
create index idx_dbt_iam_position on dbt_iam_position (code);
create index idx_dbt_iam_position_tenant on dbt_iam_position (tenant_id);

-- 用户岗位
create table dbt_iam_user_position
(
  id  varchar(32) not null,
  tenant_id varchar(32) default '0' not null,
  user_type           varchar(100) default 'IamUser' not null,
  user_id             varchar(32)  not null,
  org_id              varchar(32)  not null,
  position_id         varchar(32)  not null,
  is_primary_position BOOLEAN   default FALSE not null,
  is_deleted BOOLEAN DEFAULT FALSE  not null,
  create_time timestamp default CURRENT_TIMESTAMP   not null,
  update_time timestamp default CURRENT_TIMESTAMP null
);
comment on column dbt_iam_user_position.id is 'ID';
comment on column dbt_iam_user_position.tenant_id is '租户ID';
comment on column dbt_iam_user_position.user_type is '用户类型';
comment on column dbt_iam_user_position.user_id is '用户ID';
comment on column dbt_iam_user_position.org_id is '组织ID';
comment on column dbt_iam_user_position.position_id is '岗位ID';
comment on column dbt_iam_user_position.is_primary_position is '是否主岗';
comment on column dbt_iam_user_position.is_deleted is '是否删除';
comment on column dbt_iam_user_position.create_time is '创建时间';
comment on column dbt_iam_user_position.update_time is '更新时间';
comment on table dbt_iam_user_position is '用户岗位关联';
create index idx_dbt_iam_user_position on dbt_iam_user_position (user_type, user_id);
create index idx_dbt_iam_user_position_pos on dbt_iam_user_position (position_id);

-- 系统配置表
create table dbt_system_config
(
  id          varchar(32) not null,
  tenant_id   varchar(32) not null default '0',
  category        VARCHAR(50) null,
  prop_key        VARCHAR(50) not null,
  prop_value       VARCHAR(255),
  data_type       VARCHAR(50) not null default 'text',
  is_deleted  BOOLEAN     not null default FALSE,
  create_time timestamp   not null default CURRENT_TIMESTAMP,
  update_time timestamp   null default CURRENT_TIMESTAMP,
  constraint PK_dbt_system_config primary key (id)
);
-- 添加备注
comment on column dbt_system_config.id is 'ID';
comment on column dbt_system_config.tenant_id is '租户ID';
comment on column dbt_system_config.category is '类别';
comment on column dbt_system_config.prop_key is '属性名';
comment on column dbt_system_config.prop_value is '属性值';
comment on column dbt_system_config.data_type is '属性类型';
comment on column dbt_system_config.is_deleted is '删除标记';
comment on column dbt_system_config.create_time is '创建时间';
comment on column dbt_system_config.update_time is '更新时间';

comment on table dbt_system_config is '系统配置';
-- 创建索引
create index idx_dbt_system_config on dbt_system_config (category, prop_key);
create index idx_dbt_system_config_tenant on dbt_system_config (tenant_id);
