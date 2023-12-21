-- 文件记录
CREATE TABLE ${SCHEMA}.dbt_file_record
(
    id          varchar(32)  NOT NULL,
    tenant_id     varchar(32)  NOT NULL DEFAULT '0',
    app_module    varchar(50),
    md5           varchar(32),
    file_name     varchar(100) NOT NULL,
    file_type     varchar(20),
    file_size     bigint       NOT NULL,
    storage_path  varchar(1000) NOT NULL,
    access_url    varchar(1000),
    thumbnail_url varchar(200),
    description   varchar(100),
    is_deleted    tinyint      NOT NULL DEFAULT 0,
    create_by     varchar(32)  NOT NULL DEFAULT '0',
    create_time   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint PK_dbt_file_record primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'UUID', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块','SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'MD5标识', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'md5';
execute sp_addextendedproperty 'MS_Description', N'文件名', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'file_name';
execute sp_addextendedproperty 'MS_Description', N'文件类型', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'file_type';
execute sp_addextendedproperty 'MS_Description', N'文件大小', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'file_size';
execute sp_addextendedproperty 'MS_Description', N'存储路径', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'storage_path';
execute sp_addextendedproperty 'MS_Description', N'访问地址', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'access_url';
execute sp_addextendedproperty 'MS_Description', N'缩略图地址', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'thumbnail_url';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建人', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'create_by';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'文件记录', 'SCHEMA', '${SCHEMA}', 'table', dbt_file_record, null, null;
-- 索引
create nonclustered index idx_dbt_file_record_md5 on dbt_file_record (md5);
create nonclustered index idx_dbt_file_record_tenant on dbt_file_record (tenant_id);