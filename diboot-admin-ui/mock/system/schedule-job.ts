import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import type { ScheduleJob, Job, ScheduleJobLog } from '@/views/system/schedule-job/type'

const jobList: ScheduleJob[] = [
  {
    id: '1',
    jobKey: 'ClearOperationLogJob',
    jobName: '清除过期操作日志',
    cron: '0 0 1 * * ?',
    initStrategy: 'FIRE_AND_PROCEED',
    initStrategyLabel: '立即执行一次，并周期执行',
    jobStatus: 'A',
    saveLog: true,
    createByName: 'Diboot',
    createTime: '2022-05-23 22:22:22',
    updateTime: '2022-05-23 22:22:22'
  }
]

const scheduleJob = crudTemplate<ScheduleJob>({
  baseApi: '/schedule-job',
  dataList: jobList,
  enablePagination: false
})

const jobLogList: ScheduleJobLog[] = []

const jobLog = crudTemplate<ScheduleJobLog>({
  baseApi: '/schedule-job/log',
  dataList: jobLogList
})

export default [
  {
    url: `${scheduleJob.baseUrl}/all-job`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK([
        {
          jobKey: 'ClearOperationLogJob',
          jobName: '清除过期操作日志',
          jobCron: '0 0 1 * * ?',
          paramJsonExample: '{"daysBefore":30}'
        }
      ] as Job[])
    }
  },
  ...Object.values(scheduleJob.api),
  {
    url: `${scheduleJob.baseUrl}/execute-once/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ query }: ApiRequest) => {
      const natural = Random.natural(1_000, 30_000)
      const startTime = Random.now('yyyy-MM-dd HH:mm:ss')
      setTimeout(() => {
        const scheduleJob = jobList.find(e => e.id === query.id) as ScheduleJob
        const endTime = Random.now('yyyy-MM-dd HH:mm:ss')
        jobLogList.push({
          id: String(jobLogList.length + 1),
          jobId: query.id,
          paramJson: scheduleJob.paramJson,
          startTime,
          endTime,
          elapsedSeconds: String(Math.round(natural / 1000)),
          triggerMode: 'MANUAL',
          triggerModeLabel: '手动',
          runStatus: Random.pick(['S', 'F']),
          createTime: endTime
        })
      }, natural)
      return JsonResult.OK()
    }
  },
  jobLog.api.getList,
  jobLog.api.getById,
  jobLog.api.remove,
  jobLog.api.batchRemove
] as MockMethod[]
