-- 上传文件表
CREATE TABLE ${SCHEMA}.upload_file (
    uuid VARCHAR2(32) NOT NULL,
    tenant_id          NUMBER(20)           default 0  not null,
    app_module          VARCHAR2(50),
    rel_obj_type VARCHAR2(50),
    rel_obj_id VARCHAR2(32),
    rel_obj_field VARCHAR2(50),
    file_name VARCHAR2(100) NOT NULL,
    storage_path VARCHAR2(200) NOT NULL,
    access_url VARCHAR2(200),
    file_type VARCHAR2(20),
    data_count NUMBER(9)   DEFAULT 0 not null,
    description VARCHAR2(100),
    is_deleted NUMBER(1)   DEFAULT 0 not null,
    create_by NUMBER(20)   default 0 ,
    create_time timestamp   default CURRENT_TIMESTAMP not null,
    constraint PK_upload_file primary key (uuid)
);
-- 添加备注,
comment on column ${SCHEMA}.upload_file.uuid is 'UUID';
comment on column ${SCHEMA}.upload_file.tenant_id is '租户ID';
comment on column ${SCHEMA}.upload_file.app_module is '应用模块';
comment on column ${SCHEMA}.upload_file.rel_obj_type is '关联对象类';
comment on column ${SCHEMA}.upload_file.rel_obj_id is '关联对象ID';
comment on column ${SCHEMA}.upload_file.rel_obj_field is '关联对象属性名称';
comment on column ${SCHEMA}.upload_file.file_name is '文件名';
comment on column ${SCHEMA}.upload_file.storage_path is '存储路径';
comment on column ${SCHEMA}.upload_file.access_url is '访问地址';
comment on column ${SCHEMA}.upload_file.file_type is '文件类型';
comment on column ${SCHEMA}.upload_file.data_count is '数据量';
comment on column ${SCHEMA}.upload_file.description is '备注';
comment on column ${SCHEMA}.upload_file.is_deleted is '删除标记';
comment on column ${SCHEMA}.upload_file.create_by is '创建人';
comment on column ${SCHEMA}.upload_file.create_time is '创建时间';
comment on table ${SCHEMA}.upload_file is '上传文件';
-- 索引
create index idx_upload_file on upload_file (rel_obj_type, rel_obj_id, rel_obj_field);
create index idx_upload_file_tenant on upload_file (tenant_id);