-- 移动端用户表
CREATE TABLE ${SCHEMA}.iam_member (
  id NUMBER(20) generated as identity ( start with 100000 nocycle noorder),
  tenant_id NUMBER(20) default 0  not null,
  org_id NUMBER(20)  default 0 not null,
  user_id NUMBER(20) default 0 not null,
  user_type VARCHAR2(100) DEFAULT 'IamUser' NOT NULL,
  openid VARCHAR2(50) NOT NULL,
  nickname VARCHAR2(100),
  avatar_url VARCHAR2(255),
  country VARCHAR2(50),
  province VARCHAR2(50),
  city VARCHAR2(100),
  mobile_phone VARCHAR2(20),
  email VARCHAR2(100),
  gender VARCHAR2(10),
  status VARCHAR2(20) DEFAULT 'NORMAL' NOT NULL,
  description VARCHAR2(200),
  is_deleted   NUMBER(1) DEFAULT 0    not null,
  create_time  timestamp default CURRENT_TIMESTAMP   not null,
  update_time  timestamp default CURRENT_TIMESTAMP  null,
  constraint PK_iam_member primary key (id)
);
comment on column ${SCHEMA}.iam_member.id is 'ID';
comment on column ${SCHEMA}.iam_member.tenant_id is '租户ID';
comment on column ${SCHEMA}.iam_member.org_id is '组织';
comment on column ${SCHEMA}.iam_member.user_id is '用户id';
comment on column ${SCHEMA}.iam_member.user_type is '用户类型';
comment on column ${SCHEMA}.iam_member.openid is 'openid';
comment on column ${SCHEMA}.iam_member.nickname is '昵称';
comment on column ${SCHEMA}.iam_member.avatar_url is '头像';
comment on column ${SCHEMA}.iam_member.country is '国家';
comment on column ${SCHEMA}.iam_member.province is '省';
comment on column ${SCHEMA}.iam_member.city is '市';
comment on column ${SCHEMA}.iam_member.mobile_phone is '手机号';
comment on column ${SCHEMA}.iam_member.gender is '性别';
comment on column ${SCHEMA}.iam_member.status is '状态';
comment on column ${SCHEMA}.iam_member.description is '备注';
comment on column ${SCHEMA}.iam_member.is_deleted is '是否删除';
comment on column ${SCHEMA}.iam_member.update_time is '更新时间';
comment on column ${SCHEMA}.iam_member.create_time is '创建时间';
comment on table ${SCHEMA}.iam_member is '移动端用户';
-- 索引
create index idx_member_tenant on ${SCHEMA}.iam_member (tenant_id);
create index idx_member_orgid on ${SCHEMA}.iam_member (org_id);
create index idx_member_openid on ${SCHEMA}.iam_member (openid);
create index idx_member_phone on ${SCHEMA}.iam_member (mobile_phone);
create index idx_member_user on ${SCHEMA}.iam_member(user_id, user_type);
