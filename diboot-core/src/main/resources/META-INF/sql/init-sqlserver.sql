-- 建表
create table dictionary (
   id                   int                  identity,
   parent_id            int                  not null,
   type                 varchar(50)          not null,
   item_name            varchar(100)         not null,
   item_value           varchar(100)         null,
   comment              varchar(200)         null,
   extdata              varchar(200)         null,
   sort_id              smallint             not null default 99,
   system               smallint             not null default 0,
   editable             smallint             not null default 1,
   deleted              smallint             not null default 0,
   create_time          datetime             not null default CURRENT_TIMESTAMP,
   constraint PK_dictionary primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', 'ID', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'id';
execute sp_addextendedproperty 'MS_Description', '父ID', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', '字典类型', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'type';
execute sp_addextendedproperty 'MS_Description', '显示名', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'item_name';
execute sp_addextendedproperty 'MS_Description', '存储值', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'item_value';
execute sp_addextendedproperty 'MS_Description', '备注', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'comment';
execute sp_addextendedproperty 'MS_Description', '扩展JSON', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', '排序号', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', '是否系统预置', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'system';
execute sp_addextendedproperty 'MS_Description', '是否可编辑', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'editable';
execute sp_addextendedproperty 'MS_Description', '删除标记', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'deleted';
execute sp_addextendedproperty 'MS_Description', '创建时间', 'user', '${SCHEMA}', 'table', 'dictionary', 'column', 'create_time';

execute sp_addextendedproperty 'MS_Description','数据字典','user','${SCHEMA}','table','dictionary',null,null;
-- 创建索引
create nonclustered index idx_directory on dictionary(type, item_value);
