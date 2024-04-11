-- 用户表
create table dbt_iam_user
(
  id           varchar(32) NOT NULL comment 'ID'  primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  org_id       varchar(32) NOT NULL DEFAULT '0' comment '组织ID',
  user_num     varchar(20) not null comment '用户编号',
  realname     varchar(50) not null comment '真实姓名',
  gender       varchar(10) not null comment '性别',
  birthdate    date        null comment '出生日期',
  mobile_phone varchar(20) null comment '手机号',
  email        varchar(50) null comment 'Email',
  sort_id      bigint null comment '排序号',
  avatar_url   varchar(1000)                          null comment '头像地址',
  status       varchar(10) default 'A'               not null comment '状态',
  is_deleted   tinyint(1)  default 0                 not null comment '是否删除',
  create_time  datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)DEFAULT CHARSET=utf8 COMMENT '系统用户';
-- 索引
create index idx_dbt_iam_user_1 on dbt_iam_user (org_id);
create index idx_dbt_iam_user_2 on dbt_iam_user (mobile_phone);
create index idx_dbt_iam_user_num on dbt_iam_user (user_num);

-- 账号表
create table dbt_iam_account
(
  id           varchar(32) NOT NULL COMMENT 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      varchar(32) NOT NULL DEFAULT '0' comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100) not null comment '用户名',
  auth_secret  varchar(100)  null comment '密码',
  secret_salt  varchar(32)  null comment '加密盐',
  status       varchar(10)  default 'A'               not null comment '用户状态',
  is_deleted   tinyint(1)   default 0                 not null comment '是否删除',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
) DEFAULT CHARSET=utf8 COMMENT '登录账号';
-- 创建索引
create index idx_dbt_iam_account on dbt_iam_account(auth_account, auth_type, user_type);

-- 角色表
create table dbt_iam_role
(
  id        varchar(32) NOT NULL comment 'ID'    primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  name        varchar(50)                          not null comment '名称',
  code        varchar(50)                          not null comment '编码',
  description varchar(100)                         null comment '备注',
  is_deleted  tinyint(1) default 0                not null comment '是否删除',
  create_time datetime  default CURRENT_TIMESTAMP null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)DEFAULT CHARSET=utf8 COMMENT '角色';
-- 创建索引
create index idx_dbt_iam_role_code on dbt_iam_role (code);

-- 用户角色表
create table dbt_iam_user_role
(
  id        varchar(32) NOT NULL comment 'ID'      primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  user_type varchar(100) default 'IamUser'       not null comment '用户类型',
  user_id   varchar(32) NOT NULL comment '用户ID',
  role_id   varchar(32) NOT NULL comment '角色ID',
  is_deleted  tinyint(1)   default 0                 not null comment '是否删除',
  create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)DEFAULT CHARSET=utf8 COMMENT '用户角色关联';
-- 索引
create index idx_dbt_iam_user_role on dbt_iam_user_role (user_type, user_id);

-- 前端资源权限表
create table dbt_iam_resource
(
    id                varchar(32)                           NOT NULL comment 'ID' primary key,
    parent_id         varchar(32) NULL default '0' comment '父级资源',
    tenant_id         varchar(32)                           not null default '0' comment '租户ID',
    app_module        varchar(50) null comment '应用模块',
    display_type      varchar(20)                           not null comment '展现类型',
    display_name      varchar(100) null comment '显示名称',
    display_name_i18n varchar(200) null comment '显示名称国际化资源标识',
    route_path        varchar(200) null comment '路由地址',
    resource_code     varchar(100)                          not null comment '前端资源编码',
    permission_code   varchar(200) null comment '接口权限编码',
    meta              varchar(300) null comment 'meta配置',
    sort_id           bigint      not null default 0 comment '排序号',
    status            varchar(10) default 'A'               not null comment '状态',
    is_deleted        tinyint(1) default 0 not null comment '是否删除',
    create_time       datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)DEFAULT CHARSET=utf8 COMMENT '资源权限';
-- 索引
create index idx_dbt_iam_resource on dbt_iam_resource (parent_id);

-- 角色-权限
create table dbt_iam_role_resource
(
  id  varchar(32) NOT NULL comment 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  role_id   varchar(32) not null comment '角色ID',
  resource_id varchar(32) not null comment '资源ID',
  is_deleted    tinyint(1) default 0 not null comment '是否删除',
  create_time   datetime  default CURRENT_TIMESTAMP not null comment '创建时间'
)DEFAULT CHARSET=utf8 COMMENT '角色权限';
-- 索引
create index idx_dbt_iam_role_resource on dbt_iam_role_resource (role_id, resource_id);

-- 登录日志表
create table dbt_iam_login_trace
(
  id           varchar(32) NOT NULL comment 'ID'   primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  user_type    varchar(100) default 'IamUser'         not null comment '用户类型',
  user_id      varchar(32) NOT NULL  comment '用户ID',
  auth_type    varchar(20)  default 'PWD'             not null comment '认证方式',
  auth_account varchar(100) not null comment '用户名',
  ip_address   varchar(50)  null comment 'IP',
  user_agent   varchar(200) null comment '客户端信息',
  is_success   tinyint(1)   default 0                 not null comment '是否成功',
  logout_time  datetime    null comment '退出时间',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间'
) DEFAULT CHARSET=utf8 COMMENT '登录日志';
-- 创建索引
create index idx_dbt_iam_login_trace on dbt_iam_login_trace (user_type, user_id);
create index idx_dbt_iam_login_trace_2 on dbt_iam_login_trace (auth_account);

-- 操作日志表
create table dbt_iam_operation_log
(
  id        varchar(32) NOT NULL comment 'ID'   primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  app_module  varchar(50)   null comment '应用模块',
  business_obj    varchar(100)  not null comment '业务对象',
  operation   varchar(100)  not null comment '操作描述',
  user_type    varchar(100) default 'IamUser'       null comment '用户类型',
  user_id      varchar(32) NOT NULL DEFAULT '0' comment '用户ID',
  user_realname    varchar(100)          null   comment '用户姓名',
  request_uri    varchar(500)                  not null comment '请求URI',
  request_method varchar(20) not null comment '请求方式',
  request_params    varchar(1000)                  null comment '请求参数',
  request_ip   varchar(50) null comment 'IP',
  status_code   smallint   default 0   not null comment '状态码',
  error_msg     varchar(1000)                  null comment '异常信息',
  is_deleted       tinyint(1)  null comment '删除标记',
  create_time      datetime   default CURRENT_TIMESTAMP null comment '创建时间'
)
  DEFAULT CHARSET=utf8 COMMENT '操作日志';
-- 创建索引
create index idx_dbt_iam_operation_log on dbt_iam_operation_log (user_type, user_id);

-- 组织表
create table dbt_iam_org
(
    id              varchar(32)  NOT NULL comment 'ID' primary key,
    tenant_id       varchar(32)  NOT NULL DEFAULT '0' COMMENT '租户ID',
    parent_id       varchar(32)  NOT NULL default '0' comment '上级ID',
    parent_ids_path varchar(500) comment '上级ID路径',
    root_org_id     varchar(32)  NOT NULL default '0' comment '企业ID',
    name            varchar(100) not null comment '名称',
    type            varchar(100) default 'DEPT' not null comment '组织类别',
    code            varchar(50)  not null comment '编码',
    manager_id      varchar(32) null comment '负责人ID',
    sort_id         bigint null comment '排序号',
    status          varchar(10)           default 'A' not null comment '状态',
    org_comment     varchar(200) COMMENT '备注',
    is_deleted      tinyint(1) default 0 not null comment '是否删除',
    create_time     datetime              default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)
  comment '组织';
create index idx_dbt_iam_org on dbt_iam_org (parent_id);
create index idx_dbt_iam_org_parent_path on dbt_iam_org (parent_ids_path);

-- 岗位
create table dbt_iam_position
(
  id        varchar(32) NOT NULL comment 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  name                 varchar(100)                          not null comment '名称',
  code                 varchar(50) not null comment '编码',
  is_virtual           tinyint(1)  default 0                 not null comment '是否虚拟岗',
  grade_name           varchar(50) null comment '职级头衔',
  grade_value          varchar(30) default '0'               null comment '职级',
  data_permission_type varchar(20) default 'SELF'            null comment '数据权限类型',
  is_deleted           tinyint(1)  default 0                 not null comment '是否删除',
  create_time          datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime null DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)
comment '岗位';
create index idx_dbt_iam_position on dbt_iam_position (code);

-- 用户岗位
create table dbt_iam_user_position
(
  id        varchar(32) NOT NULL comment 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  user_type           varchar(100) default 'IamUser' not null comment '用户类型',
  user_id   varchar(32) NOT NULL comment '用户ID',
  org_id    varchar(32) NOT NULL default '0' comment '组织ID',
  position_id varchar(32) NOT NULL comment '岗位ID',
  is_primary_position tinyint(1)   default 1                 not null comment '是否主岗',
  is_deleted          tinyint(1)   default 0                 not null comment '是否删除',
  create_time         datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
comment '用户岗位关联';
create index idx_dbt_iam_user_position on dbt_iam_user_position (user_type, user_id);
create index idx_dbt_iam_user_position_2 on dbt_iam_user_position (org_id, position_id);

-- 系统配置表
CREATE TABLE `dbt_system_config`
(
    `id`          varchar(32) NOT NULL COMMENT 'ID' primary key,
    `tenant_id`   varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
    `category`    varchar(50) NULL COMMENT '类别',
    `prop_key`    varchar(50) NOT NULL COMMENT '属性名',
    `prop_value`  varchar(255) NULL COMMENT '属性值',
    `data_type`   varchar(50) NULL DEFAULT 'text' COMMENT '数据类型',
    `is_deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)COMMENT = '系统配置';
create index idx_dbt_system_config on dbt_system_config (`category`, `prop_key`);