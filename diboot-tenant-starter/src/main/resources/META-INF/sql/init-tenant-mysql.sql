-- 租户表
create table dbt_iam_tenant
(
    id          varchar(32) NOT NULL comment 'ID'  primary key,
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

create index idx_dbt_directory_tenant on dbt_dictionary(tenant_id);
create index idx_dbt_iam_user_tenant on dbt_iam_user (tenant_id);
create index idx_dbt_iam_account_tenant on dbt_iam_account (tenant_id);
create index idx_dbt_iam_role_tenant on dbt_iam_role (tenant_id);
create index idx_dbt_iam_u_r_tenant on dbt_iam_user_role (tenant_id);
create index idx_dbt_iam_res_tenant on dbt_iam_resource (tenant_id);
create index idx_dbt_iam_role_res_tenant on dbt_iam_role_resource (tenant_id);
create index idx_dbt_iam_login_trace_tenant on dbt_iam_login_trace (tenant_id);
create index idx_dbt_iam_oper_log_tenant on dbt_iam_operation_log (tenant_id);
create index idx_dbt_iam_org_tenant on dbt_iam_org (tenant_id);
create index idx_dbt_iam_pos_tenant on dbt_iam_position (tenant_id);
create index idx_dbt_sys_cfg_tenant on dbt_system_config (tenant_id);
create index idx_dbt_iam_u_p_tenant on dbt_iam_user_position (tenant_id);
create index idx_dbt_mem_tenant on iam_member (tenant_id);
create index idx_dbt_file_record_tenant on dbt_file_record (tenant_id);
create index idx_dbt_msg_tmpl_tenant on dbt_message_template (tenant_id);
create index idx_dbt_msg_tenant on dbt_message (tenant_id);
create index idx_dbt_sch_job_tenant on dbt_schedule_job (tenant_id);
create index idx_dbt_sch_job_log_tenant on dbt_schedule_job_log (tenant_id);
