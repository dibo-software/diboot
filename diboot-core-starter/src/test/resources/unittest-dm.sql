--set schema diboot_unittest;

-- create schema diboot_example collate utf8_general_ci;
-- 建表
-- 字典表
create table dictionary (
                            id                 BIGINT primary key,
                            parent_id          BIGINT           default 0  not null,
                            tenant_id          BIGINT           default 0  not null,
                            app_module          VARCHAR(150),
                            type               VARCHAR(150)          not null,
                            item_name          VARCHAR(300)         not null,
                            item_value         VARCHAR(300),
                            description        VARCHAR(300),
                            extdata            VARCHAR(600),
                            sort_id            SMALLINT           default 99  not null,
                            is_deletable       BIT          default 1  not null,
                            is_editable        BIT          default 1  not null,
                            is_deleted         BIT          default 0  not null,
                            create_time        DATETIME    null
);

create table department
(
    id BIGINT primary key,
    parent_id bigint default 0 not null,
    org_id bigint not null ,
    name varchar(50) not null,
    extdata varchar(100) null ,
    character varchar(100) null ,
    is_deleted BIT default 0 not null ,
    create_time datetime null
);

create table organization
(
    id INT identity ( 10000,1) primary key,
    parent_id int default 0 not null,
    name varchar(100) not null,
    telphone varchar(20) null,
    manager_id bigint not null,
    is_deleted BIT default 0 not null,
    create_time datetime default CURRENT_TIMESTAMP not null
);

create table role
(
    id INT identity ( 10000,1) primary key,
    name varchar(20) null,
    code varchar(20) null,
    is_deleted BIT default 0 null,
    create_time datetime default CURRENT_TIMESTAMP null
);

create table "USER"
(
    id INT identity ( 10000,1) primary key,
    department_id int default 0 not null,
    username varchar(20) null,
    gender varchar(20) null,
    birthdate date null,
    character varchar(100) null,
    is_deleted BIT default 0 null,
    create_time datetime default CURRENT_TIMESTAMP null,
    local_datetime datetime null
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
    id INT identity ( 10000,1) primary key,
    parent_id   int          null,
    region_id   int          not null,
    region_name varchar(100) null
);

CREATE TABLE db_goods_goods_info (
                                     goods_id bigint DEFAULT NULL,
                                     goods_nm varchar(10) DEFAULT NULL,
                                     create_ts datetime default CURRENT_TIMESTAMP null,
                                     update_ts  datetime null,
                                     is_del BIT DEFAULT 0
);

CREATE TABLE db_purchase_rel_plan_goods (
                                            rel_id bigint DEFAULT NULL,
                                            purchase_form_plan_id bigint DEFAULT NULL,
                                            goods_id bigint DEFAULT NULL,
                                            create_ts datetime default CURRENT_TIMESTAMP null,
                                            update_ts  datetime null,
                                            is_del BIT DEFAULT 0
);

CREATE TABLE db_purchase_form_plan (
                                       purchase_form_plan_id bigint DEFAULT NULL,
                                       name varchar(100) DEFAULT NULL,
                                       is_del BIT DEFAULT 0,
                                       create_ts datetime default CURRENT_TIMESTAMP null,
                                       update_ts  datetime null
);

-- 上传文件表

CREATE TABLE test_upload_file (
                                  uuid varchar(32) NOT NULL primary key,
                                  rel_obj_type varchar(50) DEFAULT NULL,
                                  rel_obj_id varchar(32) DEFAULT NULL,
                                  rel_obj_field varchar(50) DEFAULT NULL,
                                  file_name varchar(100) NOT NULL,
                                  storage_path varchar(200) NOT NULL,
                                  access_url varchar(200) NULL,
                                  file_type varchar(20) DEFAULT NULL,
                                  is_deleted   BIT default 0                 not null,
                                  create_time  datetime   default CURRENT_TIMESTAMP not null
);

-- playground.demo_test definition

CREATE TABLE demo_test (
                           id bigint identity ( 10000,1) primary key,
                           is_deleted BIT NOT NULL DEFAULT 0,
                           name varchar(100) NOT NULL,
                           age bigint NOT NULL,
                           id_card varchar(100) DEFAULT NULL,
                           mobile_phone varchar(100) DEFAULT NULL,
                           email varchar(100) DEFAULT NULL,
                           sex varchar(100) DEFAULT NULL,
                           birthday date DEFAULT NULL,
                           sss_img varchar(200) DEFAULT NULL,
                           mmm_img varchar(1000) DEFAULT NULL ,
                           data_file varchar(1000) DEFAULT NULL ,
                           file_test varchar(200) DEFAULT NULL ,
                           create_by bigint DEFAULT '0' ,
                           create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                           update_time datetime NULL DEFAULT CURRENT_TIMESTAMP
);

-- playground.demo_test_join definition

CREATE TABLE demo_test_join (
                                id bigint identity ( 10000,1) primary key,
                                demo_test_id bigint DEFAULT NULL,
                                name varchar(100) DEFAULT NULL ,
                                email varchar(100) DEFAULT NULL ,
                                is_deleted BIT NOT NULL DEFAULT 0,
                                create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初始化样例数据
INSERT INTO department (id, parent_id, org_id, name, character) VALUES (10001, 0, 100001, '产品部', 'WW'), (10002, 10001, 100001, '研发组', '1001'), (10003, 10001, 100001, '测试组', '1001,1002'),
                                                                       (10004, 10001, 100001, '市场部', '1001,1002'), (10005, 10003, 100001, '自动化测试', null), (10006, 10003, 100001, '功能测试', null);
INSERT INTO dictionary (id, parent_id, app_module, type, item_name, item_value) VALUES (1, 0, '', 'GENDER', '性别', null), (2, 1, '', 'GENDER', '男', 'M'), (3, 1, '', 'GENDER', '女', 'F');
SET IDENTITY_INSERT organization ON;
INSERT INTO organization (id, parent_id, name, telphone, manager_id) VALUES (100001, 0, '苏州帝博', '0512-62988949', 1001), (100002, 0, '成都帝博', '028-62988949', 1002);
SET IDENTITY_INSERT role ON;
INSERT INTO role (id, name, code) VALUES (101, '管理员', 'ADMIN'), (102, '操作员', 'OPERATOR');
SET IDENTITY_INSERT "USER" ON;
INSERT INTO "USER" (id, department_id, username, gender, character) VALUES (1001, 10002, '张三', 'M', 'test123456'), (1002, 10002, '李四', 'F', 'test123456,test234567'), (1003, 10001, '王五', 'M', 'WW');
INSERT INTO user_role (user_type, user_id, role_id) VALUES ('SysUser', 1001, 101),('SysUser', 1001, 102),('OrgUser', 1002, 102);
SET IDENTITY_INSERT cc_city_info ON;
INSERT INTO cc_city_info (id, parent_id, region_id, region_name) VALUES (10000, 0, 10000, '江苏省'), (10010, 10000, 10010, '苏州市'), (10020, 10010, 10020, '园区');
INSERT INTO db_goods_goods_info (goods_id, goods_nm, is_del) VALUES(1001, 'abcde', 0), (1002, 'abcd', 0);
INSERT INTO db_purchase_rel_plan_goods(rel_id, purchase_form_plan_id, goods_id, is_del)VALUES(1, 1, 1001, 0), (2, 1, 1002, 0);
INSERT INTO db_purchase_form_plan(purchase_form_plan_id, name, is_del)VALUES(1, '5月份采购计划', 0);
INSERT INTO test_upload_file(uuid, rel_obj_type, rel_obj_id, file_name, access_url, storage_path)
values ('test123456', 'IamUser', 1001, '123456.jpg', 'http://www.baidu.com', '/temp'),
       ('test234567', 'IamUser', 1001, '234567.jpg', 'http://www.baidu.com', '/temp');
SET IDENTITY_INSERT demo_test ON;
INSERT INTO demo_test
(id, is_deleted, name, age, id_card, mobile_phone, email, sex, birthday, sss_img, mmm_img, data_file, file_test, create_by)
VALUES(10000074, 0, '666', 666, 'kdt3kHX4lAEshODwXMaqAg==', 'dI7AKyanaPYiWL0c5CfuPQ==', 'kdt3kHX4lAEshODwXMaqAg==', NULL, NULL, NULL, NULL, NULL, NULL, 0),
      (10000075, 0, '1', 111, 'kBzIU3XgeowKFQnyeGZfZZHbd5B1+JQBLITg8FzGqgI=', 'AYdMZnNVJwYmpHh8VmC11A==', '5tEbe9Q9hti2Z0spAE5fsA==', NULL, NULL, NULL, NULL, NULL, NULL, 0);
SET IDENTITY_INSERT demo_test_join ON;
INSERT INTO demo_test_join (id, demo_test_id, name, email)
VALUES(10000081, 10000074, '111', 'L9vrF7wJBKbbLRZChI33WA=='),
      (10000084, 10000074, '114', 'A2Y6CZ4tv0V7QxPbq9EYkg=='),
      (10000089, 10000075, '119', 'VVnnuR7fXzsIHCMilEsgLw=='),
      (10000091, 10000075, '112', 'BqAefnSadfixkCebXakUDg=='),
      (10000093, 10000075, '114', 'A2Y6CZ4tv0V7QxPbq9EYkg==');
