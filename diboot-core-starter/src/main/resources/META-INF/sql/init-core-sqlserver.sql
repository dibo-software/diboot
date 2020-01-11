-- 建表
create table ${SCHEMA}.dictionary (
   id                   int                  identity,
   parent_id            int                  not null,
   type                 varchar(50)          not null,
   item_name            varchar(100)         not null,
   item_value           varchar(100)         null,
   description          varchar(100)         null,
   extdata              varchar(200)         null,
   sort_id              smallint             not null default 99,
   is_deletable        tinyint             not null default 0,
   is_editable          tinyint             not null default 1,
   is_deleted           tinyint             not null default 0,
   create_time          datetime             not null default CURRENT_TIMESTAMP,
   constraint PK_dictionary primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'父ID','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'字典类型','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'type';
execute sp_addextendedproperty 'MS_Description', N'显示名','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'item_name';
execute sp_addextendedproperty 'MS_Description', N'存储值','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'item_value';
execute sp_addextendedproperty 'MS_Description', N'备注','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'扩展JSON','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'extdata';
execute sp_addextendedproperty 'MS_Description', N'排序号','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'是否可删除','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'is_deletable';
execute sp_addextendedproperty 'MS_Description', N'是否可编辑','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'is_editable';
execute sp_addextendedproperty 'MS_Description', N'删除标记','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'create_time';

execute sp_addextendedproperty 'MS_Description', N'数据字典','SCHEMA', '${SCHEMA}', 'table', dictionary, null, null;
-- 创建索引
create nonclustered index idx_directory on ${SCHEMA}.dictionary(type, item_value);
