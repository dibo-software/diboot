-- 租户表
create table if not exists dbt_iam_tenant
(
    id           varchar(32) NOT NULL comment 'ID'  primary key,
    name        varchar(100)                          not null comment '租户名称',
    code        varchar(20)                           not null comment '租户编码',
    start_date  date                                  not null comment '有效开始日期',
    end_date    date                                  not null comment '有效结束日期',
    manager     varchar(50)                           null comment '负责人',
    phone       varchar(20)                           null comment '联系电话',
    description varchar(300)                          null comment '描述',
    status      varchar(10) default 'A'               not null comment '租户状态',
    is_deleted  tinyint(1)  default 0                 not null comment '删除标记',
    create_by   bigint      default 0                 null comment '创建人',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP null comment '更新时间'
) comment '租户';
create index idx_iam_tenant_code on dbt_iam_tenant (code);

-- 租户资源
create table if not exists dbt_iam_tenant_resource
(
    id          varchar(32) NOT NULL comment 'ID'  primary key,
    tenant_id   varchar(32)     default 0                 not null comment '租户ID',
    resource_id varchar(32)                               not null comment '资源ID',
    is_deleted  tinyint(1) default 0                 not null comment '删除标记',
    create_time datetime   default CURRENT_TIMESTAMP not null comment '创建时间'
) comment '租户资源';
create index idx_iam_tenant_res_tid_rid on dbt_iam_tenant_resource (tenant_id, resource_id);