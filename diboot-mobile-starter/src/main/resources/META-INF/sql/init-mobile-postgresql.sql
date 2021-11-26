-- 移动端用户表
CREATE TABLE iam_member (
  id bigserial not null,
  tenant_id bigint default 0  not null,
  org_id bigint default 0 not null,
  user_id bigint default 0 not null,
  user_type VARCHAR(100) NOT NULL DEFAULT 'IamUser',
  openid VARCHAR(50) NOT NULL,
  nickname VARCHAR(100) NOT NULL,
  avatar_url VARCHAR(255),
  country VARCHAR(50),
  province VARCHAR(50),
  city VARCHAR(100),
  mobile_phone VARCHAR(20),
  email VARCHAR(100),
  gender VARCHAR(10),
  status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
  description VARCHAR(200),
  is_deleted   BOOLEAN default FALSE   not null,
  create_by bigint DEFAULT 0 NOT NULL,
  create_time  timestamp default CURRENT_TIMESTAMP   not null,
  update_time  timestamp   null
);
comment on column iam_member.id is 'ID';
comment on column iam_member.tenant_id is '租户ID';
comment on column iam_member.org_id is '组织';
comment on column iam_member.user_id is '用户id';
comment on column iam_member.user_type is '用户类型';
comment on column iam_member.openid is 'openid';
comment on column iam_member.nickname is '昵称';
comment on column iam_member.avatar_url is '头像';
comment on column iam_member.country is '国家';
comment on column iam_member.province is '省';
comment on column iam_member.city is '市';
comment on column iam_member.mobile_phone is '手机号';
comment on column iam_member.gender is '性别';
comment on column iam_member.status is '状态';
comment on column iam_member.description is '备注';
comment on column iam_member.is_deleted is '是否删除';
comment on column iam_member.update_time is '更新时间';
comment on column iam_member.create_time is '创建时间';
comment on table iam_member is '移动端用户';
-- 索引
create index idx_member_tenant on iam_member (tenant_id);
create index idx_member_orgid on iam_member (org_id);
create index idx_member_openid on iam_member (openid);
create index idx_member_phone on iam_member (mobile_phone);
create index idx_member_user on iam_member(user_id, user_type);