-- create schema diboot_example collate utf8_general_ci;
-- 建表
CREATE TABLE `dictionary` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `parent_id` bigint unsigned NOT NULL COMMENT '父ID',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
    `app_module`  varchar(50)   null comment '应用模块',
    `type` varchar(50) NOT NULL COMMENT '字典类型',
    `item_name` varchar(100) NOT NULL COMMENT '显示名',
    `item_value` varchar(100) DEFAULT NULL COMMENT '存储值',
    `description` varchar(100) DEFAULT NULL COMMENT '描述说明',
    `extdata` varchar(200) DEFAULT NULL COMMENT '扩展JSON',
    `sort_id` smallint NOT NULL DEFAULT '99' COMMENT '排序号',
    `is_editable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可改',
    `is_deletable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否可删',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT '数据字典';

create table department
(
  id bigint unsigned not null comment 'ID' primary key,
  parent_id bigint default 0 not null comment '上级部门ID',
  org_id bigint not null comment '单位ID',
  name varchar(50) not null comment '名称',
  extdata varchar(100) null comment '扩展字段',
  `character` varchar(100) null comment '关键字',
  is_deleted tinyint(1) default 0 not null comment '已删除',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
  comment '部门' charset=utf8mb4;

create table organization
(
  id int auto_increment comment 'ID'
    primary key,
  parent_id int default 0 not null comment '上级单位ID',
  name varchar(100) not null comment '名称',
  telphone varchar(20) null comment '电话',
  manager_id bigint not null comment '负责人id',
  is_deleted tinyint(1) default 0 not null comment '是否有效',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
  comment '单位' charset=utf8mb4;

create table role
(
  id int auto_increment comment 'ID' primary key,
  name varchar(20) null,
  code varchar(20) null,
  is_deleted tinyint(1) default 0 null,
  create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间'
) comment '角色' charset=utf8mb4;

create table user
(
  id int auto_increment comment 'ID' primary key,
  department_id int default 0 not null,
  username varchar(20) null,
  gender varchar(20) null,
  birthdate date null,
  `character` varchar(100) null comment '关键字',
  is_deleted tinyint(1) default 0 null,
  create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间',
  local_datetime datetime null comment '本地时间'
) comment '用户' charset=utf8mb4;

create table user_role
(
  user_type varchar(20) not null comment '用户类型',
  user_id int not null comment '用户ID',
  role_id int not null comment '角色ID',
  primary key (user_id, role_id)
) comment '用户角色' charset=utf8mb4;

create table cc_city_info
(
  id          int auto_increment primary key,
  parent_id   int          null,
  region_id   int          not null,
  region_name varchar(100) null
) comment '行政区划' charset=utf8mb4;

CREATE TABLE `db_goods_goods_info` (
  `goods_id` bigint DEFAULT NULL,
  `goods_nm` varchar(10) DEFAULT NULL,
  `create_ts` timestamp default CURRENT_TIMESTAMP null,
  `update_ts`  timestamp null,
  `is_del` tinyint DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `db_purchase_rel_plan_goods` (
  `rel_id` bigint DEFAULT NULL,
  `purchase_form_plan_id` bigint DEFAULT NULL,
  `goods_id` bigint DEFAULT NULL,
  `create_ts` timestamp default CURRENT_TIMESTAMP null,
  `update_ts`  timestamp null,
  `is_del` tinyint DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `db_purchase_form_plan` (
  `purchase_form_plan_id` bigint DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `is_del` tinyint DEFAULT '0',
  `create_ts` timestamp default CURRENT_TIMESTAMP null,
  `update_ts`  timestamp null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 初始化样例数据
INSERT INTO department (id, parent_id, org_id, name, `character`) VALUES (10001, 0, 100001, '产品部', 'CP'), (10002, 10001, 100001, '研发组', 'YF'), (10003, 10001, 100001, '测试组', 'CS'),
       (10004, 10001, 100001, 'UI组'), (10005, 10003, 100001, '自动化测试'), (10006, 10003, 100001, '功能测试');
INSERT INTO dictionary (id, parent_id, app_module, type, item_name, item_value) VALUES (1, 0, '', 'GENDER', '性别', null), (2, 1, '', 'GENDER', '男', 'M'), (3, 1, '', 'GENDER', '女', 'F');
INSERT INTO organization (id, parent_id, name, telphone, manager_id) VALUES (100001, 0, '苏州帝博', '0512-62988949', 1001), (100002, 0, '成都帝博', null, null);
INSERT INTO role (id, name, code) VALUES (101, '管理员', 'ADMIN'), (102, '操作员', 'OPERATOR');
INSERT INTO user (id, department_id, username, gender, `character`) VALUES (1001, 10002, '张三', 'M', 'ZS'), (1002, 10002, '李四', 'F', 'LS');
INSERT INTO user_role (user_type, user_id, role_id) VALUES ('SysUser', 1001, 101),('SysUser', 1001, 102),('OrgUser', 1002, 102);
INSERT INTO cc_city_info (id, parent_id, region_id, region_name) VALUES (10000, 0, 10000, '江苏省'), (10010, 10000, 10010, '苏州市'), (10020, 10010, 10020, '园区');
INSERT INTO db_goods_goods_info (goods_id, goods_nm, is_del) VALUES(1001, 'abcde', 0), (1002, 'abcd', 0);
INSERT INTO db_purchase_rel_plan_goods(rel_id, purchase_form_plan_id, goods_id, is_del)VALUES(1, 1, 1001, 0), (2, 1, 1002, 0);
INSERT INTO db_purchase_form_plan(purchase_form_plan_id, name, is_del)VALUES(1, '5月份采购计划', 0);
