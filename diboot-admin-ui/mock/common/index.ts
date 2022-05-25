import { MockMethod } from 'vite-plugin-mock'
import { JsonResult, ApiRequest } from '../_util'
import { Random } from 'mockjs'
import objectDataListMap from './_objectDataListMap'
import { BindData, AsyncBindData, LabelValue } from '@/hooks/more_default'
import { line2Hump } from '@/utils/str'

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
  ]
}

export default [
  // 绑定字典接口
  {
    url: `${baseUrl}/bindDict`,
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
    url: `${baseUrl}/bindData`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Record<string, BindData>>) => {
      const more: Record<string, LabelValue[]> = {}
      Object.keys(body).forEach(type => {
        const bindData = body[type]
        more[type] = objectDataListMap[bindData.type].map(
          item =>
            ({
              value: item[bindData.value || 'id'],
              label: item[bindData.label],
              ext: buildExtData(item, bindData.ext)
            } as LabelValue<Record<string, unknown>>)
        ) as Array<LabelValue>
      })
      return JsonResult.OK(more)
    }
  },
  // 绑定数据过滤接口
  {
    url: `${baseUrl}/bindData/{parentValue}/{parentType}`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest<AsyncBindData>) => {
      const labelValueList = objectDataListMap[query.type]
        .filter(e => (query.keyword ? `${e[query.label]}`.match(query.keyword) : true))
        .map(
          item =>
            ({
              value: item[query.value || 'id'],
              label: item[query.label],
              ext: buildExtData(item, query.ext)
            } as LabelValue<Record<string, unknown>>)
        )
      return JsonResult.OK(labelValueList)
    }
  }
] as MockMethod[]
