-- 文件记录
CREATE TABLE ${SCHEMA}.file_record
(
    uuid          VARCHAR2(32) NOT NULL,
    tenant_id     NUMBER(20) DEFAULT 0 NOT NULL,
    app_module    VARCHAR2(50),
    md5           VARCHAR2(32),
    file_name     VARCHAR2(100) NOT NULL,
    file_type     VARCHAR2(20),
    file_size     NUMBER(20) NOT NULL,
    storage_path  VARCHAR2(200) NOT NULL,
    access_url    VARCHAR2(200),
    thumbnail_url VARCHAR2(200),
    description   VARCHAR2(100),
    is_deleted    NUMBER(1) DEFAULT 0 not null,
    create_by     NUMBER(20) DEFAULT 0,
    create_time   timestamp DEFAULT CURRENT_TIMESTAMP not null,
    constraint PK_file_record primary key (uuid)
);
-- 添加备注
comment on column ${SCHEMA}.file_record.uuid is 'UUID';
comment on column ${SCHEMA}.file_record.tenant_id is '租户ID';
comment on column ${SCHEMA}.file_record.app_module is '应用模块';
comment on column ${SCHEMA}.file_record.md5 is 'MD5标识';
comment on column ${SCHEMA}.file_record.file_name is '文件名';
comment on column ${SCHEMA}.file_record.file_type is '文件类型';
comment on column ${SCHEMA}.file_record.file_size is '文件大小';
comment on column ${SCHEMA}.file_record.storage_path is '存储路径';
comment on column ${SCHEMA}.file_record.access_url is '访问地址';
comment on column ${SCHEMA}.file_record.preview_url is '缩略图地址';
comment on column ${SCHEMA}.file_record.description is '备注';
comment on column ${SCHEMA}.file_record.is_deleted is '删除标记';
comment on column ${SCHEMA}.file_record.create_by is '创建人';
comment on column ${SCHEMA}.file_record.create_time is '创建时间';
comment on table ${SCHEMA}.file_record is '文件记录';
-- 索引
create index idx_file_record_md5 on file_record (md5);
create index idx_file_record_tenant on file_record (tenant_id);