-- 定时任务表
CREATE TABLE dbt_schedule_job (
  id  varchar(32) not null,
  tenant_id varchar(32) default '0' not null,
  job_key    VARCHAR(100)          not null,
  job_name       VARCHAR(100)          not null,
  cron       VARCHAR(50),
  param_json VARCHAR(200),
  init_strategy VARCHAR(50),
  job_status       VARCHAR(10)   default 'A'  not null,
  save_log     BOOLEAN default TRUE    not null,
  job_comment      VARCHAR(200),
  is_deleted   BOOLEAN default FALSE   not null,
  create_by varchar(32) DEFAULT '0' NOT NULL,
  create_time  timestamp default CURRENT_TIMESTAMP   not null,
  update_time  timestamp default CURRENT_TIMESTAMP  null
);
comment on column dbt_schedule_job.id is 'ID';
comment on column dbt_schedule_job.tenant_id is '租户ID';
comment on column dbt_schedule_job.job_key is 'job编码';
comment on column dbt_schedule_job.job_name is 'job名称';
comment on column dbt_schedule_job.cron is '定时表达式';
comment on column dbt_schedule_job.param_json is '参数';
comment on column dbt_schedule_job.init_strategy is '初始化策略';
comment on column dbt_schedule_job.job_status is '状态';
comment on column dbt_schedule_job.job_comment is '备注';
comment on column dbt_schedule_job.save_log is '是否记录日志';
comment on column dbt_schedule_job.is_deleted is '是否删除';
comment on column dbt_schedule_job.create_by is '创建人';
comment on column dbt_schedule_job.create_time is '创建时间';
comment on column dbt_schedule_job.update_time is '更新时间';
comment on table dbt_schedule_job is '定时任务';
create index idx_dbt_schedule_job on dbt_schedule_job (job_key);
create index idx_dbt_schedule_job_tenant on dbt_schedule_job (tenant_id);

-- 定时任务日志表
CREATE TABLE dbt_schedule_job_log (
  id  varchar(32) not null,
  tenant_id varchar(32) default '0' not null,
  job_id    VARCHAR(100)          not null,
  job_name       VARCHAR(100)          not null,
  cron       VARCHAR(50),
  param_json VARCHAR(200),
  start_time timestamp         null,
  end_time timestamp          null,
  elapsed_seconds int,
  run_status       VARCHAR(10)   default 'A'  not null,
  data_count int,
  execute_msg  VARCHAR(500)  not null,
  is_deleted   BOOLEAN default FALSE  not null,
  create_time  timestamp default CURRENT_TIMESTAMP   not null
);
comment on column dbt_schedule_job_log.id is 'ID';
comment on column dbt_schedule_job_log.tenant_id is '租户ID';
comment on column dbt_schedule_job_log.job_id is 'job编码';
comment on column dbt_schedule_job_log.job_name is 'job名称';
comment on column dbt_schedule_job_log.cron is '定时表达式';
comment on column dbt_schedule_job_log.param_json is '参数';
comment on column dbt_schedule_job_log.start_time is '开始时间';
comment on column dbt_schedule_job_log.end_time is '结束时间';
comment on column dbt_schedule_job_log.elapsed_seconds is '耗时(s)';
comment on column dbt_schedule_job_log.run_status is '运行状态';
comment on column dbt_schedule_job_log.data_count is '数据计数';
comment on column dbt_schedule_job_log.execute_msg is '执行结果信息';
comment on column dbt_schedule_job_log.is_deleted is '是否删除';
comment on column dbt_schedule_job_log.create_time is '创建时间';
comment on table dbt_schedule_job_log is '定时任务日志';
create index idx_sch_job_log on dbt_schedule_job_log (job_id);
create index idx_sch_job_log_tenant on dbt_schedule_job_log (tenant_id);
