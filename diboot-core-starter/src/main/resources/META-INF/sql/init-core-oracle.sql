-- 字典表
create table ${SCHEMA}.dbt_dictionary (
    id                 VARCHAR2(32) NOT NULL,
    parent_id          VARCHAR2(32)          null,
    tenant_id          VARCHAR2(32)          default '0'  not null,
    app_module         VARCHAR2(50),
    type               VARCHAR2(50)          not null,
    item_name          VARCHAR2(100)         not null,
    item_name_i18n     VARCHAR2(200),
    item_value         VARCHAR2(100),
    description        VARCHAR2(100),
    extension          VARCHAR2(200),
    sort_id            SMALLINT           default 99  not null,
    is_deletable       NUMBER(1)          default 1  not null,
    is_editable        NUMBER(1)          default 1  not null,
    is_deleted         NUMBER(1)          default 0  not null,
    create_time        TIMESTAMP          default CURRENT_TIMESTAMP  not null,
    constraint PK_dbt_dictionary primary key (id)
);
-- 添加备注
comment on column ${SCHEMA}.dbt_dictionary.id is 'ID';
comment on column ${SCHEMA}.dbt_dictionary.parent_id is '父ID';
comment on column ${SCHEMA}.dbt_dictionary.tenant_id is '租户ID';
comment on column ${SCHEMA}.dbt_dictionary.app_module is '应用模块';
comment on column ${SCHEMA}.dbt_dictionary.type is '字典类型';
comment on column ${SCHEMA}.dbt_dictionary.item_name is '显示名';
comment on column ${SCHEMA}.dbt_dictionary.item_name_18n is '国际化资源标识';
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
    id          VARCHAR2(32)                            NOT NULL,
    tenant_id          VARCHAR2(32)          default '0'  not null,
    type        VARCHAR2(20)  default 'CUSTOM'          NOT NULL,
    language    VARCHAR2(20)                            NOT NULL,
    code        VARCHAR2(200)                           NOT NULL,
    content     VARCHAR2(1000)                          NOT NULL,
    is_deleted  NUMBER(1)     default 0                 NOT NULL,
    create_time TIMESTAMP     default CURRENT_TIMESTAMP NOT NULL,
    constraint PK_dbt_dictionary primary key (id)
);
-- 添加备注
comment on column ${SCHEMA}.dbt_i18n_config.id is 'ID';
comment on column ${SCHEMA}.dbt_i18n_config.type is '类型';
comment on column ${SCHEMA}.dbt_i18n_config.tenant_id is '租户ID';
comment on column ${SCHEMA}.dbt_i18n_config.language is '语言';
comment on column ${SCHEMA}.dbt_i18n_config.code is '资源标识';
comment on column ${SCHEMA}.dbt_i18n_config.content is '内容';
comment on column ${SCHEMA}.dbt_i18n_config.is_deleted is '删除标记';
comment on column ${SCHEMA}.dbt_i18n_config.create_time is '创建时间';
comment on table ${SCHEMA}.dbt_i18n_config is '国际化配置';
-- 创建索引
create index idx_dbt_i18n_config on ${SCHEMA}.dbt_i18n_config (code, language);