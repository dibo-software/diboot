-- 字典表
create table ${SCHEMA}.dbt_dictionary (
    id                 varchar(32) NOT NULL primary key,
    parent_id          varchar(32) NULL,
    tenant_id          varchar(32) NOT NULL DEFAULT '0',
    app_module         VARCHAR(150),
    type               VARCHAR(150)          not null,
    item_name          VARCHAR(300)         not null,
    item_name_i18      VARCHAR(300),
    item_value         VARCHAR(300),
    description        VARCHAR(300),
    extension          VARCHAR(600),
    sort_id            SMALLINT           default 99  not null,
    is_deletable       BIT          default 1  not null,
    is_editable        BIT          default 1  not null,
    is_deleted         BIT          default 0  not null,
    create_time        DATETIME    null
);
-- 添加备注
comment on column ${SCHEMA}.dbt_dictionary.id is 'ID';
comment on column ${SCHEMA}.dbt_dictionary.parent_id is '父ID';
comment on column ${SCHEMA}.dbt_dictionary.tenant_id is '租户ID';
comment on column ${SCHEMA}.dbt_dictionary.app_module is '应用模块';
comment on column ${SCHEMA}.dbt_dictionary.type is '字典类型';
comment on column ${SCHEMA}.dbt_dictionary.item_name is '显示名';
comment on column ${SCHEMA}.dbt_dictionary.item_name is '显示名国际化资源标识';
comment on column ${SCHEMA}.dbt_dictionary.item_value is '存储值';
comment on column ${SCHEMA}.dbt_dictionary.description is '备注';
comment on column ${SCHEMA}.dbt_dictionary.extension is '扩展JSON';
comment on column ${SCHEMA}.dbt_dictionary.sort_id is '排序号';
comment on column ${SCHEMA}.dbt_dictionary.is_editable is '是否可改';
comment on column ${SCHEMA}.dbt_dictionary.is_deletable is '是否可删';
comment on column ${SCHEMA}.dbt_dictionary.is_deleted is '删除标记';
comment on column ${SCHEMA}.dbt_dictionary.create_time is '创建时间';

comment on table ${SCHEMA}.dbt_dictionary is '数据字典';

-- 创建索引
create index idx_dbt_directory on ${SCHEMA}.dbt_dictionary (type, item_value);
create index idx_dbt_directory_tenant on ${SCHEMA}.dbt_dictionary(tenant_id);

-- 国际化表
create table ${SCHEMA}.dbt_i18n_config
(
    id          varchar(32)                           not null primary key,
    tenant_id          varchar(32) NOT NULL DEFAULT '0',
    type        VARCHAR(20) default 'CUSTOM'          not null,
    language    VARCHAR(20)                           not null,
    code        VARCHAR(200)                          not null,
    content     VARCHAR(1000)                         not null,
    is_deleted  BIT         default 0                 not null,
    create_time DATETIME    default CURRENT_TIMESTAMP not null
);
-- 添加备注
comment on column ${SCHEMA}.dbt_i18n_config.id is 'ID';
comment on column ${SCHEMA}.dbt_i18n_config.tenant_id is '租户ID';
comment on column ${SCHEMA}.dbt_i18n_config.type is '类型';
comment on column ${SCHEMA}.dbt_i18n_config.language is '语言';
comment on column ${SCHEMA}.dbt_i18n_config.code is '资源标识';
comment on column ${SCHEMA}.dbt_i18n_config.content is '内容';
comment on column ${SCHEMA}.dbt_i18n_config.is_deleted is '删除标记';
comment on column ${SCHEMA}.dbt_i18n_config.create_time is '创建时间';
comment on table ${SCHEMA}.dbt_i18n_config is '国际化配置';
-- 创建索引
create index idx_dbt_i18n_config on ${SCHEMA}.dbt_i18n_config (code, language);