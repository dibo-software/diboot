import { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_crudTemplate'
import { Random } from 'mockjs'
import { ApiRequest, JsonResult } from '../_util'
import type { ScheduleJob, Job, ScheduleJobLog } from '@/views/system/scheduleJob/type'
import moment from 'moment'

const jobList: ScheduleJob[] = [
  {
    id: '1',
    jobKey: 'ClearOperationLogJob',
    jobName: '清除过期操作日志',
    cron: '0 0 1 * * ?',
    initStrategy: 'FIRE_AND_PROCEED',
    initStrategyLabel: '立即执行一次，并周期执行',
    jobStatus: true,
    saveLog: true,
    createByName: 'Diboot',
    createTime: '2022-05-23 22:22:22',
    updateTime: '2022-05-23 22:22:22'
  }
]

const scheduleJob = crudTemplate<ScheduleJob>({
  baseApi: '/scheduleJob',
  dataList: jobList,
  enablePagination: false
})

const jobLogList: ScheduleJobLog[] = []

const jobLog = crudTemplate<ScheduleJobLog>({
  baseApi: '/scheduleJob/log',
  dataList: jobLogList
})

export default [
  {
    url: `${scheduleJob.baseUrl}/allJob`,
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
    url: `${scheduleJob.baseUrl}/executeOnce/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ query }: ApiRequest) => {
      const natural = Random.natural(1_000, 30_000)
      const startTime = moment().format('yyyy-MM-DD HH:mm:ss')
      setTimeout(() => {
        const scheduleJob = jobList.find(e => e.id === query.id) as ScheduleJob
        const endTime = moment().format('yyyy-MM-DD HH:mm:ss')
        jobLogList.push({
          id: String(jobLogList.length + 1),
          jobId: query.id,
          paramJson: scheduleJob.paramJson,
          startTime,
          endTime,
          elapsedSeconds: String(natural),
          triggerMode: 'MANUAL',
          triggerModeLabel: '手动',
          runStatus: Random.boolean(),
          createTime: endTime
        })
      }, natural)
      return JsonResult.OK()
    }
  },
  jobLog.api.getList,
  jobLog.api.getById,
  jobLog.api.remove,
  jobLog.api.batchRemove,
  jobLog.api.cancelRemove
] as MockMethod[]
