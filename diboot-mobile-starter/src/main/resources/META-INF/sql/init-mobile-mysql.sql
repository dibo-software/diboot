-- 移动端成员表
CREATE TABLE `iam_member` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
    `org_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '组织',
    `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
    `user_type` varchar(100) NOT NULL DEFAULT 'IamUser' COMMENT '用户类型',
    `openid` varchar(32) NOT NULL COMMENT 'openid',
    `nickname` varchar(100) COMMENT '昵称',
    `avatar_url` varchar(255) COMMENT '头像',
    `country` varchar(50) COMMENT '国家',
    `province` varchar(50) COMMENT '省',
    `city` varchar(100) COMMENT '市',
    `mobile_phone` varchar(20) COMMENT '手机号',
    `email` varchar(100) COMMENT '邮箱',
    `gender` varchar(10) COMMENT '性别',
    `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态',
    `description` varchar(200) DEFAULT NULL COMMENT '备注',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='移动端用户';
-- 索引
create index idx_member_tenant on iam_member (tenant_id);
create index idx_member_orgid on iam_member (org_id);
create index idx_member_openid on iam_member (openid);
create index idx_member_phone on iam_member (mobile_phone);
create index idx_member_user on iam_member (user_id, user_type);