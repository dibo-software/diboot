-- 用户表
create table ${SCHEMA}.iam_user
(
   id bigint identity,
   org_id bigint not null default 0,
   user_num varchar(20) not null,
   report_manager_id bigint not null default 0,
   realname varchar(50) not null,
   gender varchar(10) not null,
   birthdate date null,
   mobile_phone varchar(20) null,
   email varchar(50) null,
   avatar_url varchar(200) null,
   status varchar(10) not null default 'A',
   extdata varchar(100) null,
   is_deleted tinyint not null DEFAULT 0,
   create_time datetime not null default CURRENT_TIMESTAMP,
   constraint PK_iam_user primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'组织ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'org_id';
execute sp_addextendedproperty 'MS_Description', N'用户编号', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'user_num';
execute sp_addextendedproperty 'MS_Description', N'汇报上级ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'report_manager_id';
execute sp_addextendedproperty 'MS_Description', N'真实姓名', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'realname';
execute sp_addextendedproperty 'MS_Description', N'性别', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'gender';
execute sp_addextendedproperty 'MS_Description', N'出生日期', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'birthdate';
execute sp_addextendedproperty 'MS_Description', N'手机号', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'mobile_phone';
execute sp_addextendedproperty 'MS_Description', N'Email', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'email';
execute sp_addextendedproperty 'MS_Description', N'头像', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'avatar_url';
execute sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'扩展属性', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_user, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'系统用户', 'SCHEMA', '${SCHEMA}', 'table', iam_user, null, null;
-- 索引
create index idx_iam_user_1 on iam_user (org_id);
create index idx_iam_user_2 on iam_user (mobile_phone);
create index idx_iam_user_3 on iam_user (report_manager_id);
create unique index uidx_iam_user on iam_user (user_num);

-- 账号表
create table ${SCHEMA}.iam_account
(
   id bigint identity,
   user_type varchar(100) default 'IamUser' not null,
   user_id bigint not null,
   auth_type varchar(20) default 'PWD' not null,
   auth_account varchar(100) not null,
   auth_secret varchar(32) null,
   secret_salt varchar(32) null,
   status varchar(10) default 'A' not null,
   extdata varchar(100) null,
   is_deleted tinyint default 0 not null,
   create_time datetime default CURRENT_TIMESTAMP not null,
   constraint PK_iam_account primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'认证方式', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'auth_type';
execute sp_addextendedproperty 'MS_Description', N'用户名', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'auth_account';
execute sp_addextendedproperty 'MS_Description', N'密码', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'auth_secret';
execute sp_addextendedproperty 'MS_Description', N'加密盐', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'secret_salt';
execute sp_addextendedproperty 'MS_Description', N'用户状态', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'扩展属性', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_account, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'登录账号', 'SCHEMA', '${SCHEMA}', 'table', iam_account, null, null;
-- 创建索引
create unique index idx_iam_account on iam_account(auth_account, auth_type, user_type);

-- 角色表
create table ${SCHEMA}.iam_role
(
   id bigint identity,
   name varchar(20) not null,
   code varchar(20) not null,
   description varchar(100) null,
   is_deleted tinyint default 0 not null,
   create_time datetime default CURRENT_TIMESTAMP null,
   constraint PK_iam_role primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'名称', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'name';
execute sp_addextendedproperty 'MS_Description', N'编码', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_role, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'角色', 'SCHEMA', '${SCHEMA}', 'table', iam_role, null, null;

-- 用户角色表
create table ${SCHEMA}.iam_user_role
(
   id bigint identity,
   user_type varchar(100) default 'IamUser' not null,
   user_id bigint not null,
   role_id bigint not null,
   is_deleted tinyint default 0 not null,
   create_time datetime default CURRENT_TIMESTAMP not null,
      constraint PK_iam_user_role primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'role_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'用户角色关联', 'SCHEMA', '${SCHEMA}', 'table', iam_user_role, null, null;
-- 索引
create nonclustered index idx_iam_user_role on iam_user_role (user_type, user_id);

-- 前端权限表
create table ${SCHEMA}.iam_frontend_permission
(
   id bigint identity,
   parent_id bigint default 0   not null,
   display_type varchar(20) not null,
   display_name varchar(100) not null,
   frontend_code varchar(100)   null,
   api_set varchar(3000)   null,
   sort_id bigint   null,
   is_deleted tinyint default 0 not null,
   create_time datetime default CURRENT_TIMESTAMP not null,
   update_time datetime null,
   constraint PK_iam_frontend_permission primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'菜单ID', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'展现类型', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'display_type';
execute sp_addextendedproperty 'MS_Description', N'显示名称', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'display_name';
execute sp_addextendedproperty 'MS_Description', N'前端编码', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'frontend_code';
execute sp_addextendedproperty 'MS_Description', N'接口列表', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'api_set';
execute sp_addextendedproperty 'MS_Description', N'排序号', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'前端权限表', 'SCHEMA', '${SCHEMA}', 'table', iam_frontend_permission, null, null;

-- 索引
create nonclustered index idx_iam_frontend_permission on iam_frontend_permission (parent_id);


-- 角色-权限
create table ${SCHEMA}.iam_role_permission
(
   id bigint identity ,
   role_id bigint not null ,
   permission_id bigint not null ,
   is_deleted tinyint default 0 not null ,
   create_time datetime default CURRENT_TIMESTAMP not null,
   constraint PK_iam_role_permission primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'role_id';
execute sp_addextendedproperty 'MS_Description', N'权限ID', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'permission_id';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'角色权限', 'SCHEMA', '${SCHEMA}', 'table', iam_role_permission, null, null;
-- 索引
create nonclustered index idx_iam_role_permission on iam_role_permission (role_id, permission_id);

-- 登录日志表
create table ${SCHEMA}.iam_login_trace
(
   id bigint identity ,
   user_type varchar(100) default 'IamUser' not null ,
   user_id bigint not null ,
   auth_type varchar(20) default 'PWD' not null ,
   auth_account varchar(100) not null ,
   ip_address varchar(50) null ,
   user_agent varchar(200) null ,
   extdata varchar(100) null ,
   is_success tinyint default 0 not null,
   create_time datetime default CURRENT_TIMESTAMP not null,
   constraint PK_iam_login_trace primary key (id)
);
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'认证方式', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'auth_type';
execute sp_addextendedproperty 'MS_Description', N'用户名', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'auth_account';
execute sp_addextendedproperty 'MS_Description', N'IP', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'ip_address';
execute sp_addextendedproperty 'MS_Description', N'客户端信息', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'user_agent';
execute sp_addextendedproperty 'MS_Description', N'扩展字段', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', N'是否成功', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'is_success';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'登录日志', 'SCHEMA', '${SCHEMA}', 'table', iam_login_trace, null, null;
-- 创建索引
create nonclustered index idx_iam_login_trace on iam_login_trace (user_type, user_id);
create nonclustered index idx_iam_login_trace_2 on iam_login_trace (auth_account);