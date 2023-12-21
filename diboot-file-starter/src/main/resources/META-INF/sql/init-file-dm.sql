-- 文件记录
CREATE TABLE ${SCHEMA}.dbt_file_record
(
    id          VARCHAR(32) NOT NULL primary key,
    tenant_id     BIGINT DEFAULT 0 NOT NULL,
    app_module    VARCHAR(50),
    md5           VARCHAR(32),
    file_name     VARCHAR(100) NOT NULL,
    file_type     VARCHAR(20),
    file_size     BIGINT NOT NULL,
    storage_path  VARCHAR(1000) NOT NULL,
    access_url    VARCHAR(1000),
    thumbnail_url VARCHAR(200),
    description   VARCHAR(100),
    is_deleted    BIT DEFAULT 0 not null,
    create_by     VARCHAR(32),
    create_time   timestamp DEFAULT CURRENT_TIMESTAMP not null
);
-- 添加备注
comment on column ${SCHEMA}.dbt_file_record.id is 'ID';
comment on column ${SCHEMA}.dbt_file_record.tenant_id is '租户ID';
comment on column ${SCHEMA}.dbt_file_record.app_module is '应用模块';
comment on column ${SCHEMA}.dbt_file_record.md5 is 'MD5标识';
comment on column ${SCHEMA}.dbt_file_record.file_name is '文件名';
comment on column ${SCHEMA}.dbt_file_record.file_type is '文件类型';
comment on column ${SCHEMA}.dbt_file_record.file_size is '文件大小';
comment on column ${SCHEMA}.dbt_file_record.storage_path is '存储路径';
comment on column ${SCHEMA}.dbt_file_record.access_url is '访问地址';
comment on column ${SCHEMA}.dbt_file_record.thumbnail_url is '缩略图地址';
comment on column ${SCHEMA}.dbt_file_record.description is '备注';
comment on column ${SCHEMA}.dbt_file_record.is_deleted is '删除标记';
comment on column ${SCHEMA}.dbt_file_record.create_by is '创建人';
comment on column ${SCHEMA}.dbt_file_record.create_time is '创建时间';
comment on table ${SCHEMA}.dbt_file_record is '文件记录';
-- 索引
create index idx_dbt_file_record_md5 on ${SCHEMA}.dbt_file_record (md5);
create index idx_dbt_file_record_tenant on ${SCHEMA}.dbt_file_record (tenant_id);