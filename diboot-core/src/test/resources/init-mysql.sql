-- create schema diboot_example collate utf8_general_ci;
-- 初始化表
create table department
(
  id bigint unsigned not null comment 'ID'
    primary key,
  parent_id bigint default 0 not null comment '上级部门ID',
  org_id bigint not null comment '单位ID',
  name varchar(50) not null comment '名称',
  extdata varchar(100) null comment '扩展字段',
  is_deleted tinyint(1) default 0 not null comment '已删除',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
  comment '组织单位' charset=utf8mb4;

CREATE TABLE `dictionary` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int unsigned NOT NULL COMMENT '父ID',
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

create table organization
(
  id int auto_increment comment 'ID'
    primary key,
  parent_id int default 0 not null comment '上级单位ID',
  name varchar(100) not null comment '名称',
  telphone varchar(20) null comment '电话',
  is_deleted tinyint(1) default 0 not null comment '是否有效',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
  comment '组织单位' charset=utf8mb4;

create table role
(
  id int auto_increment comment 'ID'
    primary key,
  name varchar(20) null,
  code varchar(20) null,
  is_deleted tinyint(1) default 0 null,
  create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间'
);

create table user
(
  id int auto_increment comment 'ID'
    primary key,
  department_id int default 0 not null,
  username varchar(20) null,
  gender varchar(20) null,
  is_deleted tinyint(1) default 0 null,
  create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间'
);

create table user_role
(
  user_id int not null comment '用户ID',
  role_id int not null comment '角色ID',
  primary key (user_id, role_id)
);

-- 初始化样例数据
INSERT INTO department (id, parent_id, org_id, name)
VALUES (10001, 0, 100001, '产品部'), (10002, 10001, 100001, '研发组'), (10003, 10001, 100001, '测试组'), (10004, 10001, 100001, 'UI组');

INSERT INTO dictionary (id, parent_id, type, item_name, item_value, description, extdata, sort_id, `is_deletable`, is_editable)
VALUES (1, 0, 'GENDER', '性别', null, '', null, 99, 0, 1), (2, 1, 'GENDER', '男', 'M', null, null, 99, 0, 1), (3, 1, 'GENDER', '女', 'F', null, null, 99, 0, 1);

INSERT INTO organization (id, parent_id, name, telphone) VALUES (100001, 0, '苏州帝博', '0512-62988949');

INSERT INTO role (id, name, code) VALUES (101, '管理员', 'ADMIN'), (102, '操作员', 'OPERATOR');

INSERT INTO user (id, department_id, username, gender)
VALUES (1001, 10002, '张三', 'M'), (1002, 10002, '李四', 'F');

INSERT INTO user_role (user_id, role_id) VALUES (1001, 101),(1001, 102),(1002, 102);