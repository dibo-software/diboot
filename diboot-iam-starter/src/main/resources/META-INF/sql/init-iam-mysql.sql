-- 用户表
create table iam_user
(
  id           bigint comment 'ID'  primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  org_id       bigint      default 0                 not null comment '组织ID',
  user_num     varchar(20)                           not null comment '用户编号',
  realname     varchar(50)                           not null comment '真实姓名',
  gender       varchar(10)                           not null comment '性别',
  birthdate    date                                  null comment '出生日期',
  mobile_phone varchar(20)                           null comment '手机号',
  email        varchar(50)                           null comment 'Email',
  avatar_url   varchar(200)                          null comment '头像地址',
  status       varchar(10) default 'A'               not null comment '状态',
  is_deleted   tinyint(1)  default 0                 not null comment '是否删除',
  create_time  datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '系统用户';
-- 索引
create index idx_iam_user_1 on iam_user (org_id);
create index idx_iam_user_2 on iam_user (mobile_phone);
create index idx_iam_user_num on iam_user (user_num);
create index idx_iam_user_tenant on iam_user (tenant_id);

-- 账号表
create table iam_account
(
  id           bigint COMMENT 'ID' primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      bigint                                 not null comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100)                           not null comment '用户名',
  auth_secret  varchar(100)                            null comment '密码',
  secret_salt  varchar(32)                            null comment '加密盐',
  status       varchar(10)  default 'A'               not null comment '用户状态',
  is_deleted   tinyint(1)   default 0                 not null comment '是否删除',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '登录账号';
-- 创建索引
create index idx_iam_account on iam_account(auth_account, auth_type, user_type);
create index idx_iam_account_tenant on iam_account (tenant_id);

-- 角色表
create table iam_role
(
  id          bigint comment 'ID'    primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  name        varchar(50)                          not null comment '名称',
  code        varchar(50)                          not null comment '编码',
  description varchar(100)                         null comment '备注',
  is_deleted  tinyint(1) default 0                not null comment '是否删除',
  create_time datetime  default CURRENT_TIMESTAMP null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '角色';
-- 创建索引
create index idx_iam_role_tenant on iam_role (tenant_id);

-- 用户角色表
create table iam_user_role
(
  id          bigint auto_increment comment 'ID'      primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  user_type   varchar(100) default 'IamUser'       not null comment '用户类型',
  user_id     bigint                                 not null comment '用户ID',
  role_id     bigint                                    not null comment '角色ID',
  is_deleted  tinyint(1)   default 0                 not null comment '是否删除',
  create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '用户角色关联';
-- 索引
create index idx_iam_user_role on iam_user_role (user_type, user_id);
create index idx_iam_user_role_tenant on iam_user_role (tenant_id);

-- 前端资源权限表
create table iam_resource_permission
(
  id            bigint  auto_increment comment 'ID' primary key,
  parent_id     bigint     default 0                 not null comment '父级资源',
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  app_module  varchar(50)   null comment '应用模块',
  display_type  varchar(20)                          not null comment '展现类型',
  display_name  varchar(100)                         not null comment '显示名称',
  resource_code varchar(100)                        null comment '权限编码',
  permission_code varchar(200)                      null comment '权限编码',
  sort_id       bigint                               null comment '排序号',
  is_deleted     tinyint(1)  default 0                 not null comment '是否删除',
  create_time    datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time    datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '资源权限';
-- 索引
create index idx_iam_resource_permission on iam_resource_permission (parent_id);
create index idx_iam_resource_permission_tenant on iam_resource_permission (tenant_id);

-- 角色-权限
create table iam_role_resource
(
  id            bigint auto_increment comment 'ID'    primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  role_id       bigint                               not null comment '角色ID',
  resource_id bigint                               not null comment '资源ID',
  is_deleted    tinyint(1) default 0                 not null comment '是否删除',
  create_time   datetime  default CURRENT_TIMESTAMP not null comment '创建时间'
)AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '角色资源';
-- 索引
create index idx_iam_role_resource on iam_role_resource (role_id, resource_id);
create index idx_iam_role_resource_tenant on iam_role_resource (tenant_id);

-- 登录日志表
create table iam_login_trace
(
  id           bigint auto_increment comment 'ID'   primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      bigint                                 not null comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100)                           not null comment '用户名',
  ip_address   varchar(50)                            null comment 'IP',
  user_agent   varchar(200)                           null comment '客户端信息',
  is_success   tinyint(1)   default 0                 not null comment '是否成功',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间'
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '登录日志';
-- 创建索引
create index idx_iam_login_trace on iam_login_trace (user_type, user_id);
create index idx_iam_login_trace_2 on iam_login_trace (auth_account);
create index idx_iam_login_trace_tenant on iam_login_trace (tenant_id);

-- 操作日志表
create table iam_operation_log
(
  id           bigint auto_increment comment 'ID'   primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  app_module  varchar(50)   null comment '应用模块',
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
  create_time      datetime   default CURRENT_TIMESTAMP null comment '创建时间'
)
  AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '操作日志';
-- 创建索引
create index idx_iam_operation_log on iam_operation_log (user_type, user_id);
create index idx_iam_operation_log_tenant on iam_operation_log (tenant_id);

-- 组织表
create table iam_org
(
  id          bigint comment 'ID' primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  parent_id   bigint       default 0                 not null comment '上级ID',
  top_org_id  bigint       default 0                 not null comment '企业ID',
  name        varchar(100)                           not null comment '名称',
  short_name  varchar(50)                            not null comment '短名称',
  type        varchar(100) default 'DEPT'             not null comment '组织类别',
  code        varchar(50)                            not null comment '编码',
  manager_id  bigint       default 0                 not null comment '负责人ID',
  depth       smallint(6)  default 1                 not null comment '层级',
  sort_id     bigint                                 null comment '排序号',
  status      varchar(10)  default 'A'               not null comment '状态',
  org_comment varchar(200) COMMENT '备注',
  is_deleted  tinyint(1)   default 0                 not null comment '是否删除',
  create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)
  comment '组织';
create index idx_iam_org on iam_org (parent_id);
create index idx_iam_org_tenant on iam_org (tenant_id);

-- 岗位
create table iam_position
(
  id                   bigint comment 'ID' primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  name                 varchar(100)                          not null comment '名称',
  code                 varchar(50)                           not null comment '编码',
  is_virtual           tinyint(1)  default 0                 not null comment '是否虚拟岗',
  grade_name           varchar(50)                           null comment '职级头衔',
  grade_value          varchar(30) default '0'               null comment '职级',
  data_permission_type varchar(20) default 'SELF'            null comment '数据权限类型',
  is_deleted           tinyint(1)  default 0                 not null comment '是否删除',
  create_time          datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)
comment '岗位';
create index idx_iam_position on iam_position (code);
create index idx_iam_position_tenant on iam_position (tenant_id);

-- 用户岗位
create table iam_user_position
(
  id                  int auto_increment comment 'ID' primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  user_type           varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id             bigint                                 not null comment '用户ID',
  org_id              bigint       default 0                 not null comment '组织ID',
  position_id         bigint                                 not null comment '岗位ID',
  is_primary_position tinyint(1)   default 1                 not null comment '是否主岗',
  is_deleted          tinyint(1)   default 0                 not null comment '是否删除',
  create_time         datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
comment '用户岗位关联';
create index idx_iam_user_position on iam_user_position (user_type, user_id);
create index idx_iam_user_position_2 on iam_user_position (org_id, position_id);

-- 系统配置表
CREATE TABLE `system_config`
(
    `id`          bigint(20) UNSIGNED             NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `tenant_id`   bigint(20)                      NOT NULL DEFAULT 0 COMMENT '租户ID',
    `type`        varchar(50) CHARACTER SET utf8  NOT NULL COMMENT '类型',
    `prop`        varchar(50) CHARACTER SET utf8  NOT NULL COMMENT '属性',
    `value`       varchar(255) CHARACTER SET utf8 NULL     DEFAULT NULL COMMENT '属性值',
    `is_deleted`  tinyint(1)                      NOT NULL DEFAULT 0 COMMENT '删除标记',
    `create_time` datetime                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_system_config_tenant_id` (`tenant_id`) USING BTREE,
    INDEX `idx_system_config` (`type`, `prop`) USING BTREE
) AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8 COMMENT = '系统配置';
