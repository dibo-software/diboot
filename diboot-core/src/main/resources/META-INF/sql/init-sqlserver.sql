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
execute sp_addextendedproperty 'MS_Description', N'ID', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'父ID', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'字典类型', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'type';
execute sp_addextendedproperty 'MS_Description', N'显示名', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'item_name';
execute sp_addextendedproperty 'MS_Description', N'存储值', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'item_value';
execute sp_addextendedproperty 'MS_Description', N'备注', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'comment';
execute sp_addextendedproperty 'MS_Description', N'扩展JSON', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', N'排序号', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'是否系统预置', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'system';
execute sp_addextendedproperty 'MS_Description', N'是否可编辑', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'editable';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'Schema', '${SCHEMA}', 'table', 'dictionary', 'column', 'create_time';

execute sp_addextendedproperty 'MS_Description', N'数据字典', 'Schema', '${SCHEMA}', 'table', 'dictionary', null, null;
-- 创建索引
create nonclustered index idx_directory on dictionary(type, item_value);
