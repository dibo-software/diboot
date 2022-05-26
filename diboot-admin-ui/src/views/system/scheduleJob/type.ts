/**
 * 定时任务
 */
export interface ScheduleJob {
  id: string
  // job key
  jobKey: string
  // job名称
  jobName: string
  // 定时执行表达式
  cron: string
  // 参数json字符串
  paramJson?: string
  // 初始策略
  initStrategy: string
  initStrategyLabel: string
  // 状态
  jobStatus: boolean
  // 是否保存日志，默认true
  saveLog: boolean
  // 备注
  jobComment?: string
  // 创建人
  createByName: string
  // 更新时间
  createTime: string
  // 更新时间
  updateTime: string
}

/**
 * 任务
 */
export interface Job {
  jobKey: string
  jobName: string
  jobCron: string
  paramJsonExample: string
}

/**
 * 定时任务日志
 */
export interface ScheduleJobLog {
  id: string
  // 任务id
  jobId: string
  // 参数json字符串
  paramJson?: string
  // 开始时间
  startTime: string
  // 结束时间
  endTime: string
  // 耗时(秒)
  elapsedSeconds: string
  // 触发方式
  triggerMode: string
  triggerModeLabel: string
  // 执行状态
  runStatus: boolean
  // 执行结果信息
  executeMsg?: string
  // 创建时间
  createTime: string
}
