-- 用户表
create table iam_user
(
  id           bigint auto_increment comment 'ID'  primary key,
  org_id       bigint      default 0                 not null comment '组织ID',
  user_num     varchar(20)                           not null comment '用户编号',
  realname     varchar(50)                           not null comment '真实姓名',
  gender       varchar(10)                           not null comment '性别',
  birthdate    date                                  null comment '出生日期',
  mobile_phone varchar(20)                           null comment '手机号',
  email        varchar(50)                           null comment 'Email',
  avatar_url   varchar(200)                          null comment '头像地址',
  status       varchar(10) default 'A'               not null comment '状态',
  extdata      varchar(100)                          null comment '扩展属性',
  is_deleted   tinyint(1)  default 0                 not null comment '是否删除',
  create_time  timestamp   default CURRENT_TIMESTAMP not null comment '创建时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '系统用户';
-- 索引
create index idx_iam_user on iam_user (mobile_phone);
create index idx_iam_user_2 on iam_user (email);
create unique index uidx_iam_user on iam_user (user_num);

-- 账号表
create table iam_account
(
  id           bigint auto_increment COMMENT 'ID' primary key,
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      bigint                                 not null comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100)                           not null comment '用户名',
  auth_secret  varchar(32)                            null comment '密码',
  secret_salt  varchar(32)                            null comment '加密盐',
  status       varchar(10)  default 'A'               not null comment '用户状态',
  extdata      varchar(100)                           null comment '扩展属性',
  is_deleted   tinyint(1)   default 0                 not null comment '是否删除',
  create_time  timestamp    default CURRENT_TIMESTAMP not null comment '创建时间'
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '登录账号';
-- 创建索引
create unique index idx_iam_account on iam_account(auth_account, auth_type, user_type);

-- 角色表
create table iam_role
(
  id          bigint auto_increment comment 'ID'    primary key,
  name        varchar(20)                          not null comment '名称',
  code        varchar(20)                          not null comment '编码',
  description varchar(100)                         null comment '备注',
  is_deleted  tinyint(1) default 0                not null comment '是否删除',
  create_time timestamp  default CURRENT_TIMESTAMP null comment '创建时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '角色';

-- 用户角色表
create table iam_user_role
(
  id          bigint auto_increment comment 'ID'      primary key,
  user_type   varchar(100) default 'IamUser'       not null comment '用户类型',
  user_id     bigint                                 not null comment '用户ID',
  role_id     bigint                                    not null comment '角色ID',
  is_deleted  tinyint(1)   default 0                 not null comment '是否删除',
  create_time timestamp    default CURRENT_TIMESTAMP not null comment '创建时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '用户角色关联';
-- 索引
create index idx_iam_user_role on iam_user_role (user_type, user_id);

-- 前端资源权限表
create table iam_frontend_permission
(
  id            bigint  auto_increment comment 'ID' primary key,
  parent_id     bigint     default 0                 not null comment '父级菜单',
  display_type  varchar(20)                          not null comment '展现类型',
  display_name  varchar(100)                         not null comment '显示名称',
  frontend_code varchar(100)                         not null comment '前端编码',
  api_set       varchar(5000)                        null comment '接口列表',
  sort_id       bigint                               null comment '排序号',
  is_deleted     tinyint(1)  default 0                 not null comment '是否删除',
  create_time    timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time    timestamp   null on update CURRENT_TIMESTAMP comment '更新时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '前端菜单';

-- 索引
create index idx_iam_frontend_permission on iam_frontend_permission (parent_id);

-- 角色-权限
create table iam_role_permission
(
  id            bigint auto_increment comment 'ID'    primary key,
  role_id       bigint                               not null comment '角色ID',
  permission_id bigint                               not null comment '权限ID',
  is_deleted    tinyint(1) default 0                 not null comment '是否删除',
  create_time   timestamp  default CURRENT_TIMESTAMP not null comment '创建时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '角色权限';
-- 索引
create index idx_iam_role_permission on iam_role_permission (role_id, permission_id);

-- 登录日志表
create table iam_login_trace
(
  id           bigint auto_increment comment 'ID'   primary key,
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      bigint                                 not null comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100)                           not null comment '用户名',
  ip_address   varchar(50)                            null comment 'IP',
  user_agent   varchar(200)                           null comment '客户端信息',
  extdata      varchar(100)                           null comment '扩展字段',
  is_success   tinyint(1)   default 0                 not null comment '是否成功',
  create_time  timestamp    default CURRENT_TIMESTAMP not null comment '创建时间'
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '登录日志';
-- 创建索引
create index idx_iam_login_trace on iam_login_trace (user_type, user_id);
create index idx_iam_login_trace_2 on iam_login_trace (auth_account);