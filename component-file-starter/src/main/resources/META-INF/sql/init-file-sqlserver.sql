-- 上传文件表
CREATE TABLE ${SCHEMA}.upload_file (
    uuid varchar(32) NOT NULL,
    rel_obj_type varchar(50),
    rel_obj_id bigint,
    file_name varchar(100) NOT NULL,
    storage_path varchar(200) NOT NULL,
    file_type varchar(20),
    data_count int  not null DEFAULT 0,
    description varchar(100),
    is_deleted tinyint not null DEFAULT 0,
    create_time datetime not null default CURRENT_TIMESTAMP,
    constraint PK_upload_file primary key (uuid)
);
-- 添加备注,
execute sp_addextendedproperty 'MS_Description', N'UUID', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'uuid';
execute sp_addextendedproperty 'MS_Description', N'关联对象类', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'rel_obj_type';
execute sp_addextendedproperty 'MS_Description', N'关联对象ID', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'rel_obj_id';
execute sp_addextendedproperty 'MS_Description', N'文件名', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'file_name';
execute sp_addextendedproperty 'MS_Description', N'存储路径', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'storage_path';
execute sp_addextendedproperty 'MS_Description', N'文件类型', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'file_type';
execute sp_addextendedproperty 'MS_Description', N'数据量', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'data_count';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', upload_file, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'上传文件', 'SCHEMA', '${SCHEMA}', 'table', upload_file, null, null;
-- 索引
create nonclustered index idx_upload_file on upload_file(rel_obj_type, rel_obj_id);
