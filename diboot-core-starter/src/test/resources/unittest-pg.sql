-- 建表
CREATE TABLE dictionary (
    id bigserial not null,
    parent_id bigint NOT NULL,
    tenant_id bigint NOT NULL DEFAULT 0,
    app_module varchar(50) null,
    type varchar(50) NOT NULL,
    item_name varchar(100) NOT NULL,
    item_value varchar(100) DEFAULT NULL,
    description varchar(100) DEFAULT NULL,
    extdata varchar(200) DEFAULT NULL,
    sort_id smallint NOT NULL DEFAULT '99',
    is_editable BOOLEAN NOT NULL DEFAULT TRUE,
    is_deletable BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint PK_dictionary primary key (id)
);


create table department
(
    id bigint not null,
    parent_id bigint default 0 not null,
    org_id bigint not null,
    name varchar(50) not null,
    extdata varchar(100) null,
    extjsonarr JSON null,
    extjsonobj JSON null,
    character varchar(100) null,
    is_deleted BOOLEAN default FALSE not null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    constraint PK_department primary key (id)
);

create table organization
(
    id int not null,
    parent_id int default 0 not null,
    name varchar(100) not null,
    telphone varchar(20) null,
    manager_id bigint not null,
    is_deleted BOOLEAN default FALSE not null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    constraint PK_organization primary key (id)
);

create table role
(
    id int not null,
    name varchar(20) null,
    code varchar(20) null,
    is_deleted BOOLEAN default FALSE null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP null,
    constraint PK_role primary key (id)
);

create table "user"
(
    id int not null,
    department_id int default 0 not null,
    username varchar(20) null,
    gender varchar(20) null,
    birthdate date null,
    character varchar(100) null,
    is_deleted BOOLEAN default FALSE null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP null,
    local_datetime TIMESTAMP null,
    constraint PK_user primary key (id)
);

create table user_role
(
    user_type varchar(20) not null,
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id)
);

create table cc_city_info
(
    id int not null,
    parent_id int null,
    region_id int not null,
    region_name varchar(100) null,
    constraint PK_cc_city_info primary key (id)
);

CREATE TABLE db_goods_goods_info (
     goods_id bigint DEFAULT NULL,
     goods_nm varchar(10) DEFAULT NULL,
     create_ts TIMESTAMP default CURRENT_TIMESTAMP null,
     update_ts TIMESTAMP null,
     is_del BOOLEAN DEFAULT false,
     constraint PK_db_goods_goods_info primary key (goods_id)
);

CREATE TABLE db_purchase_rel_plan_goods (
    rel_id bigint DEFAULT NULL,
    purchase_form_plan_id bigint DEFAULT NULL,
    goods_id bigint DEFAULT NULL,
    create_ts TIMESTAMP default CURRENT_TIMESTAMP null,
    update_ts TIMESTAMP null,
    is_del BOOLEAN default false,
    constraint PK_db_purchase_rel_plan_goods primary key (rel_id)
);

CREATE TABLE db_purchase_form_plan (
   purchase_form_plan_id bigint DEFAULT NULL,
   name varchar(100) DEFAULT NULL,
   is_del BOOLEAN default false,
   create_ts TIMESTAMP default CURRENT_TIMESTAMP null,
   update_ts TIMESTAMP null,
   constraint PK_db_purchase_form_plan primary key (purchase_form_plan_id)
);

-- 上传文件表
CREATE TABLE test_upload_file (
      uuid varchar(32) NOT NULL,
      rel_obj_type varchar(50) DEFAULT NULL,
      rel_obj_id varchar(32) DEFAULT NULL,
      rel_obj_field varchar(50) DEFAULT NULL,
      file_name varchar(100) NOT NULL,
      storage_path varchar(200) NOT NULL,
      access_url varchar(200) NULL,
      file_type varchar(20) DEFAULT NULL,
      is_deleted BOOLEAN default FALSE not null,
      create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
      constraint PK_test_upload_file primary key (uuid)
);

-- playground.demo_test definition

CREATE TABLE demo_test (
   id bigserial not null,
   is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
   name varchar(100) NOT NULL,
   age bigint NOT NULL,
   id_card varchar(100) DEFAULT NULL,
   mobile_phone varchar(100) DEFAULT NULL,
   email varchar(100) DEFAULT NULL,
   sex varchar(100) DEFAULT NULL,
   birthday date DEFAULT NULL,
   sss_img varchar(200) DEFAULT NULL,
   mmm_img varchar(1000) DEFAULT NULL,
   data_file varchar(1000) DEFAULT NULL,
   file_test varchar(200) DEFAULT NULL,
   create_by bigint DEFAULT '0',
   create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
   constraint PK_demo_test primary key (id)
);

-- playground.demo_test_join definition

CREATE TABLE demo_test_join (
    id bigserial not null,
    demo_test_id bigint DEFAULT NULL,
    name varchar(100) DEFAULT NULL,
    email varchar(100) DEFAULT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint PK_demo_test_join primary key (id)
);

create table customer
(
    id bigint not null,
    realname varchar(50) not null,
    cellphone varchar(50) not null,
    extjsonarr JSON null,
    is_deleted BOOLEAN default FALSE not null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    constraint PK_customer primary key (id)
);

INSERT INTO customer (id, realname, cellphone, extjsonarr)
VALUES (10001, '张三', '13800001111', '["WEBSOCKET","EMAIL"]'), (10002, '李四', '13800002222', '["TEXT_MESSAGE"]');

-- 初始化样例数据
INSERT INTO department (id, parent_id, org_id, name, character, extjsonobj, extjsonarr)
VALUES (10001, 0, 100001, '产品部', 'WW', null, null), (10002, 10001, 100001, '研发组', '1001', null, null),
       (10003, 10001, 100001, '测试组', '[1001,1002]', '{"id": 1001, "name": "TEST"}', '[1001,1002]'), (10004, 10001, 100001, '市场部', '1001,1002', '{"id": 1001, "name": "TEST"}', '[1001,1002]'),
       (10005, 10003, 100001, '自动化测试', null, null, null), (10006, 10003, 100001, '功能测试', null, null, null);
INSERT INTO dictionary (id, parent_id, app_module, type, item_name, item_value) VALUES (1, 0, '', 'GENDER', '性别', null), (2, 1, '', 'GENDER', '男', 'M'), (3, 1, '', 'GENDER', '女', 'F');
INSERT INTO dictionary
(id, parent_id, tenant_id, app_module, type, item_name, item_value, description, extdata, sort_id, is_editable, is_deletable)
VALUES(10050, 0, 0, NULL, 'MESSAGE_CHANNEL', '发送通道', NULL, 'message发送通道', NULL, 99, true, true),
      (10051, 10050, 0, NULL, 'MESSAGE_CHANNEL', '站内信', 'WEBSOCKET', NULL, NULL, 0, true, true),
      (10052, 10050, 0, NULL, 'MESSAGE_CHANNEL', '短信', 'TEXT_MESSAGE', NULL, NULL, 1, true, true),
      (10053, 10050, 0, NULL, 'MESSAGE_CHANNEL', '邮件', 'EMAIL', NULL, NULL, 2, true, true);
INSERT INTO organization (id, parent_id, name, telphone, manager_id) VALUES (100001, 0, '苏州帝博', '0512-62988949', 1001), (100002, 0, '成都帝博', '028-62988949', 1002);
INSERT INTO role (id, name, code) VALUES (101, '管理员', 'ADMIN'), (102, '操作员', 'OPERATOR');
INSERT INTO "user" (id, department_id, username, gender, character) VALUES (1001, 10002, '张三', 'M', 'test123456'), (1002, 10002, '李四', 'F', 'test123456,test234567'), (1003, 10001, '王五', 'M', 'WW');
INSERT INTO user_role (user_type, user_id, role_id) VALUES ('SysUser', 1001, 101),('SysUser', 1001, 102),('OrgUser', 1002, 102);
INSERT INTO cc_city_info (id, parent_id, region_id, region_name) VALUES (10000, 0, 10000, '江苏省'), (10010, 10000, 10010, '苏州市'), (10020, 10010, 10020, '园区');
INSERT INTO db_goods_goods_info (goods_id, goods_nm, is_del) VALUES(1001, 'abcde', false), (1002, 'abcd', false);
INSERT INTO db_purchase_rel_plan_goods(rel_id, purchase_form_plan_id, goods_id, is_del)VALUES(1, 1, 1001, false), (2, 1, 1002, false);
INSERT INTO db_purchase_form_plan(purchase_form_plan_id, name, is_del)VALUES(1, '5月份采购计划', false);
INSERT INTO test_upload_file(uuid, rel_obj_type, rel_obj_id, file_name, access_url, storage_path)
values ('test123456', 'IamUser', 1001, '123456.jpg', 'http://www.baidu.com', '/temp'),
       ('test234567', 'IamUser', 1001, '234567.jpg', 'http://www.baidu.com', '/temp');

INSERT INTO demo_test
(id, is_deleted, name, age, id_card, mobile_phone, email, sex, birthday, sss_img, mmm_img, data_file, file_test, create_by)
VALUES(10000074, false, '666', 666, 'kdt3kHX4lAEshODwXMaqAg==', 'dI7AKyanaPYiWL0c5CfuPQ==', 'kdt3kHX4lAEshODwXMaqAg==', NULL, NULL, NULL, NULL, NULL, NULL, 0),
      (10000075, false, '1', 111, 'kBzIU3XgeowKFQnyeGZfZZHbd5B1+JQBLITg8FzGqgI=', 'AYdMZnNVJwYmpHh8VmC11A==', '5tEbe9Q9hti2Z0spAE5fsA==', NULL, NULL, NULL, NULL, NULL, NULL, 0);

INSERT INTO demo_test_join (id, demo_test_id, name, email)
VALUES(10000081, 10000074, '111', 'L9vrF7wJBKbbLRZChI33WA=='),
      (10000084, 10000074, '114', 'A2Y6CZ4tv0V7QxPbq9EYkg=='),
      (10000089, 10000075, '119', 'VVnnuR7fXzsIHCMilEsgLw=='),
      (10000091, 10000075, '112', 'BqAefnSadfixkCebXakUDg=='),
      (10000093, 10000075, '114', 'A2Y6CZ4tv0V7QxPbq9EYkg==');
