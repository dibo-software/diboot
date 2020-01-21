-- 建表
create table ${SCHEMA}.dictionary (
    id                 NUMBER(20) generated as identity ( start with 10000 nocycle noorder),
    parent_id          NUMBER(20)           default 0  not null,
    type               VARCHAR2(50)          not null,
    item_name          VARCHAR2(100)         not null,
    item_value         VARCHAR2(100),
    description        VARCHAR2(100),
    extdata            VARCHAR2(200),
    sort_id            SMALLINT           default 99  not null,
    is_deletable       NUMBER(1)          default 0  not null,
    is_editable        NUMBER(1)          default 1  not null,
    is_deleted         NUMBER(1)          default 0  not null,
    create_time        TIMESTAMP          default CURRENT_TIMESTAMP  not null,
    constraint PK_dictionary primary key (id)
);
-- 添加备注
comment on column ${SCHEMA}.dictionary.id is 'ID';
comment on column ${SCHEMA}.dictionary.parent_id is '父ID';
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


