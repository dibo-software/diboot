import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import objectDataListMap from './_data/object-data-list-map'
import type { RelatedData, AsyncRelatedData } from '@/hooks/use-option'

const line2Hump = (value: string, between = '_') =>
  value.toLowerCase().replace(RegExp(`${between}\\w`, 'g'), str => str.charAt(1).toUpperCase())

const baseUrl = '/api/common'

const buildExtData = (data: Record<string, unknown>, extProp?: string | string[]) => {
  if (!extProp || extProp.length == 0) return
  if (extProp instanceof Array) {
    const extData: Record<string, unknown> = {}
    extProp.forEach(key => (extData[key] = data[key]))
    return extData
  }
  return data[extProp]
}

// 字典列表
const dictList: Record<string, LabelValue<string>[]> = {
  // 性别
  GENDER: [
    { value: 'F', label: '女', ext: 'red' },
    { value: 'M', label: '男', ext: 'blue' }
  ],
  // 定时任务初始化策略
  INIT_STRATEGY: [
    { value: 'DO_NOTHING', label: '周期执行' },
    { value: 'FIRE_AND_PROCEED', label: '立即执行一次，并周期执行' },
    { value: 'IGNORE_MISFIRES', label: '超期立即执行，并周期执行' }
  ],
  // 定时任务初始化策略
  RESOURCE_CODE: [
    { value: 'detail', label: '详情' },
    { value: 'create', label: '新建' },
    { value: 'update', label: '更新' },
    { value: 'delete', label: '删除' },
    { value: 'export', label: '导出' },
    { value: 'import', label: '导入' }
  ],
  // 发送通道
  MESSAGE_CHANNEL: [
    { value: 'WEBSOCKET', label: '系统消息' },
    { value: 'SMS', label: '短信' },
    { value: 'EMAIL', label: '邮件' }
  ],
  // 消息状态
  MESSAGE_STATUS: [
    { value: 'PENDING', label: '待发送' },
    { value: 'FAILED', label: '发送失败' },
    { value: 'DELIVERY', label: '已送达' },
    { value: 'READ', label: '已读' }
  ],
  DATA_PERMISSION_TYPE: [
    { label: '本人', value: 'SELF' },
    { label: '本人及下属', value: 'SELF_AND_SUB' },
    { label: '本部门', value: 'DEPT' },
    { label: '本部门及下属部门', value: 'DEPT_AND_SUB' },
    { label: '全部', value: 'ALL' }
  ],
  POSITION_GRADE_OPTIONS: [
    { label: '初级', value: 'E1' },
    { label: '中级', value: 'E2' },
    { label: '高级', value: 'E3' },
    { label: '专家', value: 'E4' },
    { label: '公司领导', value: 'M4' }
  ],
  TENANT_STATUS: [
    { label: '有效', value: 'A' },
    { label: '无效', value: 'I' }
  ]
}

const remoteRelatedDataFilter: MockMethod = {
  url: `${baseUrl}/load-related-data`,
  timeout: Random.natural(50, 300),
  method: 'get',
  response: ({ query }: ApiRequest<AsyncRelatedData & { parentValue?: string; parentType?: string }>) => {
    const labelValueList = objectDataListMap[query.type]
      ?.filter(e => (query.keyword ? `${e[query.label]}`.match(query.keyword) : true))
      .map(
        item =>
          ({
            value: item.id,
            label: item[query.label],
            ext: buildExtData(item, query.ext)
          } as LabelValue<Record<string, unknown>>)
      )
    return JsonResult.OK(labelValueList)
  }
}

export default [
  // 绑定字典接口
  {
    url: `${baseUrl}/load-related-dict`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<string[]>) => {
      const more: Record<string, LabelValue<string>[]> = {}
      body.forEach(type => (more[`${line2Hump(type)}Options`] = dictList[type]))
      return JsonResult.OK(more)
    }
  },
  // 绑定数据接口
  {
    url: `${baseUrl}/load-related-data`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Record<string, RelatedData>>) => {
      const more: Record<string, LabelValue[]> = {}
      Object.keys(body).forEach(type => {
        const bindData = body[type]
        more[type] = objectDataListMap[bindData.type].map(
          item =>
            ({
              value: item.id,
              label: item[bindData.label],
              ext: buildExtData(item, bindData.ext)
            } as LabelValue<Record<string, unknown>>)
        ) as Array<LabelValue>
      })
      return JsonResult.OK(more)
    }
  },
  // 绑定数据过滤接口
  remoteRelatedDataFilter,
  {
    ...remoteRelatedDataFilter,
    url: `${baseUrl}/load-related-data/:parentValue`
  },
  {
    ...remoteRelatedDataFilter,
    url: `${baseUrl}/load-related-data/:parentValue/:parentType`
  }
] as MockMethod[]
