import type { Locale } from './zhCN'
const en: Locale = {
  scheduleJob: {
    jobKey: 'Scheduled Task',
    jobKeyAlias: 'Execution Class',
    jobName: 'Task Name',
    cron: 'Cron Expression',
    cronAlias: 'Schedule Rule',
    cronExplain: {
      grammar: 'Format: * * 1 * *?',
      implication: 'Meaning: Seconds Minutes Hours Day Month Week Year'
    },
    onlineEditor: 'Online Editor',
    initStrategy: 'Initialization Strategy',
    initStrategyAlias: 'Execution Strategy',
    paramJson: 'Parameters',
    jobStatus: 'Status',
    saveLog: 'Logs',
    saveLogAlias: 'Log Status',
    jobComment: 'Note',
    open: 'Enable',
    close: 'Disable',
    ready: 'Ready',
    retry: 'Retry Later',
    immediately: 'Confirm to execute once now?'
  },
  scheduleJobLog: {
    startTime: 'Start Time',
    endTime: 'End Time',
    runStatus: 'Execution Status',
    triggerMode: 'Trigger Mode',
    elapsedSeconds: 'Elapsed Time',
    paramJson: 'Parameters',
    executeMsg: 'Execution Result Information',
    success: 'Success',
    fail: 'Failure'
  }
}

export default en
