-- 字典表
create table ${SCHEMA}.dbt_dictionary (
   id                   varchar(32)         not null,
   parent_id            varchar(32)         null,
   tenant_id            varchar(32)         not null default '0',
   app_module           varchar(50),
   type                 varchar(50)          not null,
   item_name            varchar(100)         not null,
   item_name_i18n       varchar(200)         null,
   item_value           varchar(100)         null,
   description          varchar(100)         null,
   extension            varchar(200)         null,
   sort_id              smallint             not null default 99,
   is_deletable        tinyint             not null default 1,
   is_editable          tinyint             not null default 1,
   is_deleted           tinyint             not null default 0,
   create_time          datetime             not null default CURRENT_TIMESTAMP,
   constraint PK_dbt_dictionary primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'父ID','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'parent_id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'应用模块','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'app_module';
execute sp_addextendedproperty 'MS_Description', N'字典类型','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'type';
execute sp_addextendedproperty 'MS_Description', N'显示名','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'item_name';
execute sp_addextendedproperty 'MS_Description', N'显示名国际化资源标识','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'item_name_18n';
execute sp_addextendedproperty 'MS_Description', N'存储值','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'item_value';
execute sp_addextendedproperty 'MS_Description', N'备注','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'description';
execute sp_addextendedproperty 'MS_Description', N'扩展JSON','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'extension';
execute sp_addextendedproperty 'MS_Description', N'排序号','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'sort_id';
execute sp_addextendedproperty 'MS_Description', N'是否可删除','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'is_deletable';
execute sp_addextendedproperty 'MS_Description', N'是否可编辑','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'is_editable';
execute sp_addextendedproperty 'MS_Description', N'删除标记','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, 'column', 'create_time';

execute sp_addextendedproperty 'MS_Description', N'数据字典','SCHEMA', '${SCHEMA}', 'table', dbt_dictionary, null, null;
-- 创建索引
create nonclustered index idx_dbt_directory on ${SCHEMA}.dbt_dictionary(type, item_value);
create nonclustered index idx_dbt_directory_tenant on ${SCHEMA}.dbt_dictionary(tenant_id);

-- 国际化表
create table ${SCHEMA}.dbt_i18n_config
(
    id          varchar(32)   not null,
    tenant_id            varchar(32)         not null default '0',
    type        varchar(20)   not null default 'CUSTOM',
    language    varchar(20)   not null,
    code        varchar(200)  not null,
    content     varchar(1000) not null,
    is_deleted  tinyint       not null default 0,
    create_time datetime      not null default CURRENT_TIMESTAMP,
    constraint PK_dbt_i18n_config primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'类型','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'type';
execute sp_addextendedproperty 'MS_Description', N'语言','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'language';
execute sp_addextendedproperty 'MS_Description', N'资源标识','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'code';
execute sp_addextendedproperty 'MS_Description', N'内容','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'content';
execute sp_addextendedproperty 'MS_Description', N'删除标记','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'国际化配置','SCHEMA', '${SCHEMA}', 'table', dbt_i18n_config, null, null;
-- 创建索引
create nonclustered index ${SCHEMA}.idx_dbt_i18n_config on dbt_i18n_config(code, language)