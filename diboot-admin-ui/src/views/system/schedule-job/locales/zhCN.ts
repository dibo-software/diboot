const zhCN = {
  scheduleJob: {
    jobKey: '定时任务',
    jobKeyAlias: '执行类',
    jobName: '任务名称',
    cron: '定时表达式',
    cronAlias: '定时规则',
    cronExplain: {
      grammar: '格式： * * 1 * * ?',
      implication: '含义: 秒 分 时 日 月 星期 年'
    },
    onlineEditor: '在线编辑器',
    initStrategy: '初始化策略',
    initStrategyAlias: '执行策略',
    paramJson: '参数',
    jobStatus: '状态',
    saveLog: '日志',
    saveLogAlias: '日志状态',
    jobComment: '备注',
    open: '启用',
    close: '停用',
    ready: '准备就绪',
    retry: '稍后重试',
    immediately: '确认立即执行一次吗?'
  },
  scheduleJobLog: {
    startTime: '开始时间',
    startTimeAlias: '执行时间',
    endTime: '结束时间',
    runStatus: '执行状态',
    triggerMode: '触发方式',
    elapsedSeconds: '耗时',
    paramJson: '参数',
    executeMsg: '执行结果信息',
    success: '成功',
    fail: '失败',
    title: '日志',
    triggerModeOptions: {
      auto: '自动',
      manual: '手动'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
