-- 建表
create table dictionary (
     id                   serial not null,
     parent_id            int            not null,
     type                 VARCHAR(50)    not null,
     item_name            VARCHAR(100)  not null,
     item_value           VARCHAR(100)  null,
     description          VARCHAR(100)  null,
     extdata              VARCHAR(200)  null,
     sort_id              SMALLINT      not null default 99,
     is_deletable         BOOLEAN       not null default FALSE,
     is_editable          BOOLEAN       not null default TRUE,
     is_deleted           BOOLEAN       not null default FALSE,
     create_time          DATE          not null default CURRENT_TIMESTAMP,
     constraint PK_dictionary primary key (id)
);
-- 添加备注
comment on column dictionary.id is 'ID';
comment on column dictionary.parent_id is '父ID';
comment on column dictionary.type is '字典类型';
comment on column dictionary.item_name is '显示名';
comment on column dictionary.item_value is '存储值';
comment on column dictionary.description is '描述说明';
comment on column dictionary.extdata is '扩展JSON';
comment on column dictionary.sort_id is '排序号';
comment on column dictionary.is_editable is '是否可改';
comment on column dictionary.is_deletable is '是否可删';
comment on column dictionary.is_deleted is '删除标记';
comment on column dictionary.create_time is '创建时间';

comment on table dictionary is '数据字典';
-- 创建索引
create index idx_directory on dictionary(type, item_value);