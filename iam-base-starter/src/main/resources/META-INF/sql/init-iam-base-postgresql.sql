-- 用户表
create table iam_user
(
  id bigserial not null,
  org_id bigint not null default 0,
  user_num varchar(20) not null,
  realname varchar(50) not null,
  gender varchar(10) not null,
  birthdate date null,
  mobile_phone varchar(20) null,
  email varchar(50) null,
  avatar_url varchar(200) null,
  status varchar(10) not null default 'A',
  extdata varchar(100) null,
  is_deleted BOOLEAN not null DEFAULT FALSE,
  create_time  timestamp   not null default CURRENT_TIMESTAMP
);
-- 添加备注
comment on column iam_user.id is 'ID';
comment on column iam_user.org_id is '组织ID';
comment on column iam_user.user_num is '用户编号';
comment on column iam_user.realname is '真实姓名';
comment on column iam_user.gender is '性别';
comment on column iam_user.birthdate is '出生日期';
comment on column iam_user.mobile_phone is '手机号';
comment on column iam_user.email is 'Email';
comment on column iam_user.avatar_url is '头像';
comment on column iam_user.status is '状态';
comment on column iam_user.extdata is '扩展属性';
comment on column iam_user.is_deleted is '删除标记';
comment on column iam_user.create_time is '创建时间';
comment on table iam_user is '系统用户';
-- 索引
create index idx_iam_user on iam_user (mobile_phone);
create index idx_iam_user_2 on iam_user (email);
create unique index uidx_iam_user on iam_user (user_num);

-- 账号表
create table iam_account
(
  id bigserial not null,
  user_type varchar(100) default 'IamUser' not null,
  user_id bigint not null,
  auth_type varchar(20) default 'PWD' not null,
  auth_account varchar(100) not null,
  auth_secret varchar(32) null,
  secret_salt varchar(32) null,
  status varchar(10) default 'A' not null,
  extdata varchar(100) null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column iam_account.id is 'ID';
comment on column iam_account.user_type is '用户类型';
comment on column iam_account.user_id is '用户ID';
comment on column iam_account.auth_type is '认证方式';
comment on column iam_account.auth_account is '用户名';
comment on column iam_account.auth_secret is '密码';
comment on column iam_account.secret_salt is '加密盐';
comment on column iam_account.status is '用户状态';
comment on column iam_account.extdata is '扩展属性';
comment on column iam_account.is_deleted is '是否删除';
comment on column iam_account.create_time is '创建时间';
comment on table iam_account is '登录账号';
-- 创建索引
create unique index idx_iam_account on iam_account(auth_account, auth_type, user_type);

-- 角色表
create table iam_role
(
  id serial not null,
  name varchar(20) not null,
  code varchar(20) not null,
  description varchar(100) null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP null
);
comment on column iam_role.id is 'ID';
comment on column iam_role.name is '名称';
comment on column iam_role.code is '编码';
comment on column iam_role.description is '备注';
comment on column iam_role.is_deleted is '是否删除';
comment on column iam_role.create_time is '创建时间';
comment on table iam_role is '角色';

-- 用户角色表
create table iam_user_role
(
  id serial not null,
  user_type varchar(100) default 'IamUser' not null,
  user_id bigint not null,
  role_id int not null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column iam_user_role.id is 'ID';
comment on column iam_user_role.user_type is '用户类型';
comment on column iam_user_role.user_id is '用户ID';
comment on column iam_user_role.role_id is '角色ID';
comment on column iam_user_role.is_deleted is '是否删除';
comment on column iam_user_role.create_time is '创建时间';
comment on table iam_user_role is '用户角色关联';
-- 索引
create index idx_iam_user_role on iam_user_role (user_type, user_id);

-- 权限表
create table iam_permission
(
  id serial not null,
  parent_id int default 0 not null,
  application varchar(50) default 'MS' not null,
  type varchar(10) default 'MENU' not null,
  name varchar(20) not null,
  code varchar(50) null,
  operation_name varchar(50) null,
  operation_code varchar(50) null,
  sort_id smallint default 999 not null,
  extdata varchar(100) null,
  is_deleted BOOLEAN default FALSE not null,
  create_time timestamp default CURRENT_TIMESTAMP not null,
  update_time timestamp null
);
comment on column iam_permission.id is 'ID';
comment on column iam_permission.parent_id is '上级ID';
comment on column iam_permission.application is '所属应用';
comment on column iam_permission.type is '权限类别';
comment on column iam_permission.name is '名称';
comment on column iam_permission.code is '编码';
comment on column iam_permission.operation_name is '操作名称';
comment on column iam_permission.operation_code is '操作编码';
comment on column iam_permission.sort_id is '排序号';
comment on column iam_permission.extdata is '扩展属性';
comment on column iam_permission.is_deleted is '是否删除';
comment on column iam_permission.create_time is '创建时间';
comment on column iam_permission.update_time is '更新时间';
comment on table iam_permission is '权限';
-- 索引
create index idx_iam_permission on iam_permission (code);

-- 角色-权限
create table iam_role_permission
(
  id serial not null ,
  role_id int not null ,
  permission_id int not null ,
  is_deleted BOOLEAN default FALSE not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column iam_role_permission.id is 'ID';
comment on column iam_role_permission.role_id is '角色ID';
comment on column iam_role_permission.permission_id is '权限ID';
comment on column iam_role_permission.is_deleted is '是否删除';
comment on column iam_role_permission.create_time is '创建时间';
comment on table iam_role_permission is '角色权限';
-- 索引
create index idx_iam_role_permission on iam_role_permission (role_id, permission_id);

-- 登录日志表
create table iam_login_trace
(
  id bigserial not null ,
  user_type varchar(100) default 'IamUser' not null ,
  user_id bigint not null ,
  auth_type varchar(20) default 'PWD' not null ,
  auth_account varchar(100) not null ,
  ip_address varchar(50) null ,
  user_agent varchar(200) null ,
  extdata varchar(100) null ,
  is_success BOOLEAN default FALSE not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null
);
comment on column iam_login_trace.id is 'ID';
comment on column iam_login_trace.user_type is '用户类型';
comment on column iam_login_trace.user_id is '用户ID';
comment on column iam_login_trace.auth_type is '认证方式';
comment on column iam_login_trace.auth_account is '用户名';
comment on column iam_login_trace.ip_address is 'IP';
comment on column iam_login_trace.user_agent is '客户端信息';
comment on column iam_login_trace.extdata is '扩展字段';
comment on column iam_login_trace.is_success is '是否成功';
comment on column iam_login_trace.create_time is '创建时间';
comment on table iam_login_trace is '登录日志';
-- 创建索引
create index idx_iam_login_trace on iam_login_trace (user_type, user_id);
create index idx_iam_login_trace_2 on iam_login_trace (auth_account);