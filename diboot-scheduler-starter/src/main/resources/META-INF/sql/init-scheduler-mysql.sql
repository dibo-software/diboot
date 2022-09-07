-- 定时任务表
create table schedule_job
(
  id        varchar(32) NOT NULL COMMENT 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  job_key    varchar(100)          not null comment 'job编码',
  job_name       varchar(100)          not null comment 'job名称',
  cron       varchar(50)           comment '定时表达式',
  param_json varchar(200)          comment '参数',
  init_strategy varchar(50)        comment '初始化策略',
  job_status       varchar(10)   default 'A'  not null comment '状态',
  save_log     tinyint(1)   default 1                 not null comment '是否记录日志',
  job_comment      varchar(200)  comment '备注',
  is_deleted   tinyint(1)   default 0                 not null comment '是否删除',
  create_by bigint NOT NULL DEFAULT 0 COMMENT '创建人',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  update_time  datetime   null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间'
)
DEFAULT CHARSET=utf8 COMMENT '定时任务';
-- 创建索引
create index idx_schedule_job_tenant on schedule_job (tenant_id);

-- 定时任务日志表
create table schedule_job_log
(
  id        varchar(32) NOT NULL COMMENT 'ID' primary key,
  tenant_id varchar(32) NOT NULL DEFAULT '0' COMMENT '租户ID',
  job_id    varchar(100)          not null comment 'job ID',
  job_name  varchar(100)          not null comment 'job名称',
  cron       varchar(50)           comment '定时表达式',
  param_json varchar(200)          comment '参数',
  start_time datetime         null comment '开始时间',
  end_time datetime          null comment '结束时间',
  elapsed_seconds int        comment '耗时(s)',
  run_status       varchar(10)   default 'A'  not null comment '运行状态',
  data_count int        comment '数据计数',
  execute_msg  varchar(500)  not null comment '执行结果信息',
  is_deleted   tinyint(1)   default 0                 not null comment '是否删除',
  create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间'
)
  DEFAULT CHARSET=utf8 COMMENT '定时任务日志';
-- 创建索引
create index idx_sch_job_log_1 on schedule_job_log (job_id);
create index idx_sch_job_log_tenant on schedule_job_log (tenant_id);
