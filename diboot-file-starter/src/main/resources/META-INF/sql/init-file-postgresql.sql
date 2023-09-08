-- 文件记录
CREATE TABLE dbt_file_record
(
    id          varchar(32)  NOT NULL,
    tenant_id     varchar(32)       NOT NULL DEFAULT '0',
    app_module    varchar(50),
    md5           varchar(32),
    file_name     varchar(100) NOT NULL,
    file_type     varchar(20),
    file_size     bigint       NOT NULL,
    storage_path  varchar(200) NOT NULL,
    access_url    varchar(200),
    thumbnail_url varchar(200),
    description   varchar(100),
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    create_by     varchar(32)  NOT NULL DEFAULT '0',
    create_time   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint PK_dbt_file_record primary key (id)
);
-- 添加备注
comment on column dbt_file_record.id is 'ID';
comment on column dbt_file_record.tenant_id is '租户ID';
comment on column dbt_file_record.app_module is '应用模块';
comment on column dbt_file_record.md5 is 'MD5标识';
comment on column dbt_file_record.file_name is '文件名';
comment on column dbt_file_record.file_type is '文件类型';
comment on column dbt_file_record.file_size is '文件大小';
comment on column dbt_file_record.storage_path is '存储路径';
comment on column dbt_file_record.access_url is '访问地址';
comment on column dbt_file_record.thumbnail_url is '缩略图地址';
comment on column dbt_file_record.description is '备注';
comment on column dbt_file_record.is_deleted is '删除标记';
comment on column dbt_file_record.create_by is '创建人';
comment on column dbt_file_record.create_time is '创建时间';
comment on table dbt_file_record is '文件记录';
-- 索引
create index idx_dbt_file_record_md5 on dbt_file_record (md5);
create index idx_dbt_file_record_tenant on dbt_file_record (tenant_id);