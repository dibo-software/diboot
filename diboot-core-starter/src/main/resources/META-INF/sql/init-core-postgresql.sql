-- 字典表
create table dbt_dictionary (
     id                   VARCHAR(32) not null,
     parent_id            VARCHAR(32)   null,
     tenant_id            VARCHAR(32)   not null default '0',
     app_module           VARCHAR(50),
     type                 VARCHAR(50)   not null,
     item_name            VARCHAR(100)  not null,
     item_name_18n        VARCHAR(200),
     item_value           VARCHAR(100)  null,
     description          VARCHAR(100)  null,
     extension            VARCHAR(200)  null,
     sort_id              SMALLINT      not null default 99,
     is_deletable         BOOLEAN       not null default TRUE,
     is_editable          BOOLEAN       not null default TRUE,
     is_deleted           BOOLEAN       not null default FALSE,
     create_time          timestamp     not null default CURRENT_TIMESTAMP,
     constraint PK_dbt_dictionary primary key (id)
);
-- 添加备注
comment on column dbt_dictionary.id is 'ID';
comment on column dbt_dictionary.parent_id is '父ID';
comment on column dbt_dictionary.tenant_id is '租户ID';
comment on column dbt_dictionary.app_module is '应用模块';
comment on column dbt_dictionary.type is '字典类型';
comment on column dbt_dictionary.item_name is '显示名';
comment on column dbt_dictionary.item_name_18n is '显示名国际化资源标识';
comment on column dbt_dictionary.item_value is '存储值';
comment on column dbt_dictionary.description is '描述说明';
comment on column dbt_dictionary.extension is '扩展JSON';
comment on column dbt_dictionary.sort_id is '排序号';
comment on column dbt_dictionary.is_editable is '是否可改';
comment on column dbt_dictionary.is_deletable is '是否可删';
comment on column dbt_dictionary.is_deleted is '删除标记';
comment on column dbt_dictionary.create_time is '创建时间';

comment on table dbt_dictionary is '数据字典';
-- 创建索引
create index idx_directory on dbt_dictionary(type, item_value);
create index idx_directory_tenant on dbt_dictionary(tenant_id);

-- 国际化表
create table dbt_i18n_config
(
    id          VARCHAR(32)   NOT NULL,
    tenant_id   VARCHAR(32)   not null default '0',
    type        VARCHAR(20)   NOT NULL default 'CUSTOM',
    language    VARCHAR(20)   NOT NULL,
    code        VARCHAR(200)  NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    is_deleted  BOOLEAN       NOT NULL default 0,
    create_time TIMESTAMP     NOT NULL default CURRENT_TIMESTAMP,
    constraint PK_dbt_dictionary primary key (id)
);
-- 添加备注
comment on column dbt_i18n_config.id is 'ID';
comment on column dbt_i18n_config.tenant_id is '租户ID';
comment on column dbt_i18n_config.type is '类型';
comment on column dbt_i18n_config.language is '语言';
comment on column dbt_i18n_config.code is '资源标识';
comment on column dbt_i18n_config.content is '内容';
comment on column dbt_i18n_config.is_deleted is '删除标记';
comment on column dbt_i18n_config.create_time is '创建时间';
comment on table dbt_i18n_config is '国际化配置';
-- 创建索引
create index idx_dbt_i18n_config on dbt_i18n_config (code, language);