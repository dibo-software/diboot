-- 上传文件表
CREATE TABLE upload_file (
  uuid varchar(32) NOT NULL COMMENT '编号' primary key,
  tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  app_module  varchar(50)   null comment '应用模块',
  rel_obj_type varchar(50) DEFAULT NULL COMMENT '关联对象类',
  rel_obj_id varchar(32) DEFAULT NULL COMMENT '关联对象ID',
  rel_obj_field varchar(50) DEFAULT NULL COMMENT '关联对象属性名称',
  file_name varchar(100) NOT NULL COMMENT '文件名',
  storage_path varchar(200) NOT NULL COMMENT '存储路径',
  access_url varchar(200) NULL COMMENT '访问地址',
  file_type varchar(20) DEFAULT NULL COMMENT '文件类型',
  data_count int DEFAULT 0 COMMENT '数据量',
  description varchar(100) DEFAULT NULL COMMENT '备注',
  is_deleted   tinyint(1)  default 0                 not null comment '是否删除',
  create_time  timestamp   default CURRENT_TIMESTAMP not null comment '创建时间'
) DEFAULT CHARSET=utf8 COMMENT='上传文件';
-- 索引
create index idx_upload_file on upload_file (rel_obj_type, rel_obj_id, rel_obj_field);
create index idx_upload_file_tenant on upload_file (tenant_id);