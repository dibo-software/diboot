-- 移动端用户表
CREATE TABLE iam_member (
    id bigint identity,
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
    is_deleted   tinyint not null DEFAULT 0,
    create_by bigint DEFAULT 0 NOT NULL,
    create_time  datetime default CURRENT_TIMESTAMP   not null,
    update_time  datetime   null,
    constraint PK_iam_member primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'组织', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'org_id';
execute sp_addextendedproperty 'MS_Description', N'用户id', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'user_id';
execute sp_addextendedproperty 'MS_Description', N'用户类型', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'user_type';
execute sp_addextendedproperty 'MS_Description', N'openid', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'openid';
execute sp_addextendedproperty 'MS_Description', N'昵称', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'nickname';
execute sp_addextendedproperty 'MS_Description', N'头像', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'avatar_url';
execute sp_addextendedproperty 'MS_Description', N'国家', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'country';
execute sp_addextendedproperty 'MS_Description', N'省', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'province';
execute sp_addextendedproperty 'MS_Description', N'市', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'city';
execute sp_addextendedproperty 'MS_Description', N'手机号', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'mobile_phone';
execute sp_addextendedproperty 'MS_Description', N'性别', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'gender';
execute sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'status';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'是否删除', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建人', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'create_by';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', iam_member, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'移动端用户', 'SCHEMA', '${SCHEMA}', 'table', iam_member, null, null;
-- 索引
create nonclustered index idx_member_tenant on iam_member (tenant_id);
create nonclustered index idx_member_orgid on iam_member (org_id);
create nonclustered index idx_member_openid on iam_member (openid);
create nonclustered index idx_member_phone on iam_member (mobile_phone);
create nonclustered index idx_member_user on iam_member (user_id, user_type);