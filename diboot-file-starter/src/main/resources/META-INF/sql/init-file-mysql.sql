-- 文件记录
create table file_record
(
    uuid          varchar(32)                        not null comment 'ID' primary key,
    tenant_id     bigint   default 0                 not null comment '租户ID',
    app_module    varchar(50) null comment '应用模块',
    md5           varchar(32) null comment 'MD5标识',
    file_name     varchar(100)                       not null comment '文件名称',
    file_type     varchar(20) null comment '文件类型',
    file_size     bigint                             not null comment '文件大小',
    storage_path  varchar(200)                       not null comment '存储路径',
    access_url    varchar(200) null comment '访问地址',
    thumbnail_url varchar(200) null comment '缩略图地址',
    description   varchar(100) null comment '备注',
    is_deleted    tinyint(1) default 0 not null comment '删除标记',
    create_by     bigint   default 0 null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间'
) comment '文件存储' charset = utf8mb4;
-- 索引
create index idx_file_record_md5 on file_record (md5);
create index idx_file_record_tenant on file_record (tenant_id);
