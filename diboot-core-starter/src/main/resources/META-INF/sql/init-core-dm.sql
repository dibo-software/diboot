-- 字典表
create table ${SCHEMA}.dictionary (
    id                 BIGINT identity ( 10000,1) primary key,
    parent_id          BIGINT           default 0  not null,
    tenant_id          BIGINT           default 0  not null,
    app_module          VARCHAR(150),
    type               VARCHAR(150)          not null,
    item_name          VARCHAR(300)         not null,
    item_value         VARCHAR(300),
    description        VARCHAR(300),
    extdata            VARCHAR(600),
    sort_id            SMALLINT           default 99  not null,
    is_deletable       BIT          default 1  not null,
    is_editable        BIT          default 1  not null,
    is_deleted         BIT          default 0  not null,
    create_time        DATETIME    null
);
-- 添加备注
comment on column ${SCHEMA}.dictionary.id is 'ID';
comment on column ${SCHEMA}.dictionary.parent_id is '父ID';
comment on column ${SCHEMA}.dictionary.tenant_id is '租户ID';
comment on column ${SCHEMA}.dictionary.app_module is '应用模块';
comment on column ${SCHEMA}.dictionary.type is '字典类型';
comment on column ${SCHEMA}.dictionary.item_name is '显示名';
comment on column ${SCHEMA}.dictionary.item_value is '存储值';
comment on column ${SCHEMA}.dictionary.description is '备注';
comment on column ${SCHEMA}.dictionary.extdata is '扩展JSON';
comment on column ${SCHEMA}.dictionary.sort_id is '排序号';
comment on column ${SCHEMA}.dictionary.is_editable is '是否可改';
comment on column ${SCHEMA}.dictionary.is_deletable is '是否可删';
comment on column ${SCHEMA}.dictionary.is_deleted is '删除标记';
comment on column ${SCHEMA}.dictionary.create_time is '创建时间';

comment on table ${SCHEMA}.dictionary is '数据字典';

-- 创建索引
create index idx_directory on ${SCHEMA}.dictionary (type, item_value);
create index idx_directory_tenant on ${SCHEMA}.dictionary(tenant_id);