-- 定时任务表
CREATE TABLE schedule_job (
  id bigint identity,
  tenant_id          bigint           default 0  not null,
  job_key    VARCHAR(100)          not null,
  job_name       VARCHAR(100)          not null,
  cron       VARCHAR(50),
  param_json VARCHAR(200),
  init_strategy VARCHAR(50),
  job_status       VARCHAR(10)   default 'A'  not null,
  job_comment      VARCHAR(200),
  is_deleted   tinyint not null DEFAULT 0,
  create_time  datetime default CURRENT_TIMESTAMP   not null,
  create_by bigint DEFAULT 0 NOT NULL,
  update_time  datetime   null,
  constraint PK_schedule_job primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'job编码', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'job_key';
execute sp_addextendedproperty 'MS_Description', N'job名称', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'job_name';
execute sp_addextendedproperty 'MS_Description', N'定时表达式', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'cron';
execute sp_addextendedproperty 'MS_Description', N'参数', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'param_json';
execute sp_addextendedproperty 'MS_Description', N'初始化策略', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'init_strategy';
execute sp_addextendedproperty 'MS_Description', N'状态', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'job_status';
execute sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'job_comment';
execute sp_addextendedproperty 'MS_Description', N'创建人', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'create_by';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'定时任务', 'SCHEMA', '${SCHEMA}', 'table', schedule_job, null, null;
-- 索引
create nonclustered index idx_schedule_job on schedule_job (job_key);
create nonclustered index idx_schedule_job_tenant on schedule_job (tenant_id);

-- 定时任务日志表
CREATE TABLE schedule_job_log (
  id bigint identity,
  tenant_id          bigint           default 0  not null,
  job_id    VARCHAR(100)          not null,
  job_name       VARCHAR(100)          not null,
  cron       VARCHAR(50),
  param_json VARCHAR(200),
  start_time datetime         null,
  end_time datetime          null,
  elapsed_seconds int,
  run_status       VARCHAR(10)   default 'A'  not null,
  data_count int,
  execute_msg  VARCHAR(500)  not null,
  is_deleted   tinyint not null DEFAULT 0,
  create_time  datetime default CURRENT_TIMESTAMP   not null,
  create_by bigint DEFAULT 0 NOT NULL,
  update_time  datetime   null,
  constraint PK_schedule_job_log primary key (id)
);
-- 添加备注
execute sp_addextendedproperty 'MS_Description', N'ID', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'id';
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'tenant_id';
execute sp_addextendedproperty 'MS_Description', N'job编码', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'job_id';
execute sp_addextendedproperty 'MS_Description', N'job名称', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'job_name';
execute sp_addextendedproperty 'MS_Description', N'定时表达式', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'cron';
execute sp_addextendedproperty 'MS_Description', N'参数', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'param_json';
execute sp_addextendedproperty 'MS_Description', N'开始时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'start_time';
execute sp_addextendedproperty 'MS_Description', N'结束时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'end_time';
execute sp_addextendedproperty 'MS_Description', N'耗时(s)', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'elapsed_seconds';
execute sp_addextendedproperty 'MS_Description', N'运行状态', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'run_status';
execute sp_addextendedproperty 'MS_Description', N'数据计数', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'data_count';
execute sp_addextendedproperty 'MS_Description', N'执行结果信息', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'execute_msg';
execute sp_addextendedproperty 'MS_Description', N'创建人', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'create_by';
execute sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'update_time';
execute sp_addextendedproperty 'MS_Description', N'删除标记', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'is_deleted';
execute sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, 'column', 'create_time';
execute sp_addextendedproperty 'MS_Description', N'定时任务日志', 'SCHEMA', '${SCHEMA}', 'table', schedule_job_log, null, null;
-- 索引
create nonclustered index idx_sch_job_log on schedule_job_log (job_id);
create nonclustered index idx_sch_job_log_tenant on schedule_job_log (tenant_id);