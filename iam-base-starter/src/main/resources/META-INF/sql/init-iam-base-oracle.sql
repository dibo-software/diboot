-- 用户表
create table ${SCHEMA}.iam_user
(
    id NUMBER(20) generated as identity ( start with 100000 nocycle noorder),
    org_id NUMBER(20)   default 0 not null,
    user_num VARCHAR2(20)   not null,
    realname VARCHAR2(50)   not null,
    gender VARCHAR2(10)   not null,
    birthdate date   null,
    mobile_phone VARCHAR2(20)   null,
    email VARCHAR2(50)   null,
    avatar_url VARCHAR2(200)   null,
    status VARCHAR2(10)   default 'A' not null,
    extdata VARCHAR2(100)   null,
    is_deleted NUMBER(1)   DEFAULT 0 not null,
    create_time timestamp   default CURRENT_TIMESTAMP not null,
    constraint PK_iam_user primary key (id)
);
-- 添加备注,
comment on column ${SCHEMA}.iam_user.id is 'ID';
comment on column ${SCHEMA}.iam_user.org_id is '组织ID';
comment on column ${SCHEMA}.iam_user.user_num is '用户编号';
comment on column ${SCHEMA}.iam_user.realname is '真实姓名';
comment on column ${SCHEMA}.iam_user.gender is '性别';
comment on column ${SCHEMA}.iam_user.birthdate is '出生日期';
comment on column ${SCHEMA}.iam_user.mobile_phone is '手机号';
comment on column ${SCHEMA}.iam_user.email is 'Email';
comment on column ${SCHEMA}.iam_user.avatar_url is '头像';
comment on column ${SCHEMA}.iam_user.status is '状态';
comment on column ${SCHEMA}.iam_user.extdata is '扩展属性';
comment on column ${SCHEMA}.iam_user.is_deleted is '删除标记';
comment on column ${SCHEMA}.iam_user.create_time is '创建时间';
comment on table ${SCHEMA}.iam_user is '系统用户';
-- 索引
create index idx_iam_user on ${SCHEMA}.iam_user (mobile_phone);
create index idx_iam_user_2 on ${SCHEMA}.iam_user (email);
create unique index uidx_iam_user on ${SCHEMA}.iam_user (user_num);

-- 账号表
create table ${SCHEMA}.iam_account
(
    id NUMBER(20) generated as identity ( start with 100000 nocycle noorder),
    user_type VARCHAR2(100) default 'IamUser'   not null,
    user_id NUMBER(20)   not null,
    auth_type VARCHAR2(20) default 'PWD'   not null,
    auth_account VARCHAR2(100)   not null,
    auth_secret VARCHAR2(32)   null,
    secret_salt VARCHAR2(32)   null,
    status VARCHAR2(10) default 'A'   not null,
    extdata VARCHAR2(100)   null,
    is_deleted NUMBER(1) DEFAULT 0   not null,
    create_time timestamp default CURRENT_TIMESTAMP   not null,
    constraint PK_iam_account primary key (id)
);
comment on column ${SCHEMA}.iam_account.id is 'ID';
comment on column ${SCHEMA}.iam_account.user_type is '用户类型';
comment on column ${SCHEMA}.iam_account.user_id is '用户ID';
comment on column ${SCHEMA}.iam_account.auth_type is '认证方式';
comment on column ${SCHEMA}.iam_account.auth_account is '用户名';
comment on column ${SCHEMA}.iam_account.auth_secret is '密码';
comment on column ${SCHEMA}.iam_account.secret_salt is '加密盐';
comment on column ${SCHEMA}.iam_account.status is '用户状态';
comment on column ${SCHEMA}.iam_account.extdata is '扩展属性';
comment on column ${SCHEMA}.iam_account.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_account.create_time is '创建时间';
comment on table ${SCHEMA}.iam_account is '登录账号';
-- 创建索引
create unique index idx_iam_account on ${SCHEMA}.iam_account(auth_account, auth_type, user_type);

-- 角色表
create table ${SCHEMA}.iam_role
(
    id NUMBER(11) generated as identity ( start with 10000 nocycle noorder),
    name VARCHAR2(20)   not null,
    code VARCHAR2(20)   not null,
    comment VARCHAR2(100)   null,
    is_deleted NUMBER(1) DEFAULT 0   not null,
    create_time timestamp default CURRENT_TIMESTAMP   null,
    constraint PK_iam_role primary key (id)
);
comment on column ${SCHEMA}.iam_role.id is 'ID';
comment on column ${SCHEMA}.iam_role.name is '名称';
comment on column ${SCHEMA}.iam_role.code is '编码';
comment on column ${SCHEMA}.iam_role.comment is '备注';
comment on column ${SCHEMA}.iam_role.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_role.create_time is '创建时间';
comment on table ${SCHEMA}.iam_role is '角色';

-- 用户角色表
create table ${SCHEMA}.iam_user_role
(
    id NUMBER(11) generated as identity ( start with 10000 nocycle noorder),
    user_type VARCHAR2(100) default 'IamUser' not null,
    user_id NUMBER(20) not null,
    role_id int not null,
    is_deleted NUMBER(1) DEFAULT 0   not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint PK_iam_user_role primary key (id)
);
comment on column ${SCHEMA}.iam_user_role.id is 'ID';
comment on column ${SCHEMA}.iam_user_role.user_type is '用户类型';
comment on column ${SCHEMA}.iam_user_role.user_id is '用户ID';
comment on column ${SCHEMA}.iam_user_role.role_id is '角色ID';
comment on column ${SCHEMA}.iam_user_role.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_user_role.create_time is '创建时间';
comment on table ${SCHEMA}.iam_user_role is '用户角色关联';
-- 索引
create index idx_iam_user_role on ${SCHEMA}.iam_user_role (user_type, user_id);

-- 权限表
create table ${SCHEMA}.iam_permission
(
    id NUMBER(11) generated as identity ( start with 10000 nocycle noorder),
    parent_id int default 0   not null,
    application VARCHAR2(50) default 'MS'   not null,
    type VARCHAR2(10) default 'MENU'   not null,
    name VARCHAR2(20)   not null,
    code VARCHAR2(50)   null,
    operation_name VARCHAR2(50)   null,
    operation_code VARCHAR2(50)   null,
    sort_id smallint default 999   not null,
    extdata VARCHAR2(100)   null,
    is_deleted NUMBER(1) DEFAULT 0   not null,
    create_time timestamp default CURRENT_TIMESTAMP   not null,
    update_time timestamp   null,
    constraint PK_iam_permission primary key (id)
);
comment on column ${SCHEMA}.iam_permission.id is 'ID';
comment on column ${SCHEMA}.iam_permission.parent_id is '上级ID';
comment on column ${SCHEMA}.iam_permission.application is '所属应用';
comment on column ${SCHEMA}.iam_permission.type is '权限类别';
comment on column ${SCHEMA}.iam_permission.name is '名称';
comment on column ${SCHEMA}.iam_permission.code is '编码';
comment on column ${SCHEMA}.iam_permission.operation_name is '操作名称';
comment on column ${SCHEMA}.iam_permission.operation_code is '操作编码';
comment on column ${SCHEMA}.iam_permission.sort_id is '排序号';
comment on column ${SCHEMA}.iam_permission.extdata is '扩展属性';
comment on column ${SCHEMA}.iam_permission.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_permission.create_time is '创建时间';
comment on column ${SCHEMA}.iam_permission.update_time is '更新时间';
comment on table ${SCHEMA}.iam_permission is '权限';
-- 索引
create index idx_iam_permission on ${SCHEMA}.iam_permission (code);

-- 角色-权限
create table ${SCHEMA}.iam_role_permission
(
    id NUMBER(11) generated as identity ( start with 10000 nocycle noorder) ,
    role_id int    not null,
    permission_id int    not null,
    is_deleted NUMBER(1) DEFAULT 0    not null,
    create_time timestamp default CURRENT_TIMESTAMP   not null,
    constraint PK_iam_role_permission primary key (id)
);
comment on column ${SCHEMA}.iam_role_permission.id is 'ID';
comment on column ${SCHEMA}.iam_role_permission.role_id is '角色ID';
comment on column ${SCHEMA}.iam_role_permission.permission_id is '权限ID';
comment on column ${SCHEMA}.iam_role_permission.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_role_permission.create_time is '创建时间';
comment on table ${SCHEMA}.iam_role_permission is '角色权限';
-- 索引
create index idx_iam_role_permission on ${SCHEMA}.iam_role_permission (role_id, permission_id);

-- 登录日志表
create table ${SCHEMA}.iam_login_trace
(
    id NUMBER(20) generated as identity ( start with 100000 nocycle noorder) ,
    user_type VARCHAR2(100) default 'IamUser'    not null,
    user_id NUMBER(20)    not null,
    auth_type VARCHAR2(20) default 'PWD'    not null,
    auth_account VARCHAR2(100)    not null,
    ip_address VARCHAR2(50)    null,
    user_agent VARCHAR2(200)    null,
    extdata VARCHAR2(100)    null,
    is_success NUMBER(1) DEFAULT 0    not null,
    create_time timestamp default CURRENT_TIMESTAMP   not null,
    constraint PK_iam_login_trace primary key (id)
);
comment on column ${SCHEMA}.iam_login_trace.id is 'ID';
comment on column ${SCHEMA}.iam_login_trace.user_type is '用户类型';
comment on column ${SCHEMA}.iam_login_trace.user_id is '用户ID';
comment on column ${SCHEMA}.iam_login_trace.auth_type is '认证方式';
comment on column ${SCHEMA}.iam_login_trace.auth_account is '用户名';
comment on column ${SCHEMA}.iam_login_trace.ip_address is 'IP';
comment on column ${SCHEMA}.iam_login_trace.user_agent is '客户端信息';
comment on column ${SCHEMA}.iam_login_trace.extdata is '扩展字段';
comment on column ${SCHEMA}.iam_login_trace.is_success is '是否成功';
comment on column ${SCHEMA}.iam_login_trace.create_time is '创建时间';
comment on table ${SCHEMA}.iam_login_trace is '登录日志';
-- 创建索引
create index idx_iam_login_trace on ${SCHEMA}.iam_login_trace (user_type, user_id);
create index idx_iam_login_trace_2 on ${SCHEMA}.iam_login_trace (auth_account);