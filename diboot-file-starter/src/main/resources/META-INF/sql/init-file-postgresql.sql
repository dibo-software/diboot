-- 上传文件表
CREATE TABLE upload_file (
   uuid varchar(32) NOT NULL,
   rel_obj_type varchar(50),
   rel_obj_id varchar(32),
   rel_obj_field varchar(50),
   file_name varchar(100) NOT NULL,
   storage_path varchar(200) NOT NULL,
   access_url varchar(200),
   file_type varchar(20),
   data_count int  not null DEFAULT 0,
   description varchar(100),
   is_deleted BOOLEAN not null DEFAULT FALSE,
   create_time  timestamp   not null default CURRENT_TIMESTAMP,
   constraint PK_upload_file primary key (uuid)
);
-- 添加备注,
comment on column upload_file.uuid is 'UUID';
comment on column upload_file.rel_obj_type is '关联对象类';
comment on column upload_file.rel_obj_id is '关联对象ID';
comment on column upload_file.rel_obj_field is '关联对象属性名称';
comment on column upload_file.file_name is '文件名';
comment on column upload_file.storage_path is '存储路径';
comment on column upload_file.access_url is '访问地址';
comment on column upload_file.file_type is '文件类型';
comment on column upload_file.data_count is '数据量';
comment on column upload_file.description is '备注';
comment on column upload_file.is_deleted is '删除标记';
comment on column upload_file.create_time is '创建时间';
comment on table upload_file is '上传文件';
-- 索引
create index idx_upload_file on upload_file (rel_obj_type, rel_obj_id, rel_obj_field);