-- 建表
create table dictionary (
   id                   SERIAL not null,
   parent_id            INT4                 not null,
   type                 VARCHAR(50)          not null,
   item_name            VARCHAR(100)         not null,
   item_value           VARCHAR(100)         null,
   comment              VARCHAR(200)         null,
   extdata              VARCHAR(200)         null,
   sort_id              INT2                 not null default 99,
   system               INT2                 not null default 0,
   editable             INT2                 not null default 1,
   deleted              INT2                 not null default 0,
   create_time          DATE                 not null default CURRENT_TIMESTAMP,
   constraint PK_dictionary primary key (id)
);
-- 添加备注
comment on column dictionary.id is 'ID';
comment on column dictionary.parent_id is '父ID';
comment on column dictionary.type is '字典类型';
comment on column dictionary.item_name is '显示名';
comment on column dictionary.item_value is '存储值';
comment on column dictionary.comment is '备注';
comment on column dictionary.extdata is '扩展JSON';
comment on column dictionary.sort_id is '排序号';
comment on column dictionary.system is '是否系统预置';
comment on column dictionary.editable is '是否可编辑';
comment on column dictionary.deleted is '删除标记';
comment on column dictionary.create_time is '创建时间';

comment on table dictionary is '数据字典';
-- 创建索引
create index idx_directory on dictionary(type, item_value);