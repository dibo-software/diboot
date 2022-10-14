import type { MockMethod } from 'vite-plugin-mock'
import { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import type { SystemConfig } from '@/views/system/config/type'

const baseUrl = '/api/system-config'

const typeList: LabelValue<string[]>[] = [
  {
    label: '邮箱配置',
    value: 'EmailConfig',
    ext: ['to', 'title', 'content']
  }
]

const configItemsMap: Record<string, SystemConfig[]> = {
  EmailConfig: [
    {
      type: 'EmailConfig',
      prop: 'host',
      value: 'smtp.136.com',
      propLabel: '主机',
      defaultValue: 'smtp.qq.com',
      options: ['smtp.qq.com', 'smtp.136.com'],
      required: true
    },
    {
      type: 'EmailConfig',
      prop: 'username',
      propLabel: '用户名',
      required: true
    },
    {
      type: 'EmailConfig',
      prop: 'password',
      value: '123456',
      propLabel: '授权码',
      defaultValue: 123456,
      required: false
    }
  ]
}

export default [
  // 获取配置类型列表
  {
    url: `${baseUrl}/type-list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(typeList)
    }
  },
  // 获取指定类型配置信息
  {
    url: `${baseUrl}/:type`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      if (!query.type) {
        return JsonResult.FAIL_INVALID_PARAM('type 不能为空')
      }
      return JsonResult.OK(configItemsMap[query.type])
    }
  },
  // 修改配置值
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<SystemConfig>) => {
      const config = configItemsMap[body.type]?.find(e => e.prop === body.prop)
      if (config) {
        config.value = body.value
        return JsonResult.OK()
      }
      return JsonResult.FAIL_INVALID_PARAM('修改失败')
    }
  },
  // 重置接口
  {
    url: `${baseUrl}/:type/:prop`,
    timeout: Random.natural(50, 300),
    method: 'delete',
    response: ({ query }: ApiRequest) => {
      const configItems = configItemsMap[query.type]
      if (configItems == null) return JsonResult.FAIL_INVALID_PARAM(`type: ${query.type} 不存在，重置失败`)
      if (query.prop) {
        const config = configItems.find(e => e.prop === query.prop)
        if (config == null)
          return JsonResult.FAIL_INVALID_PARAM(`type: ${query.type} prop: ${query.prop} 不存在，重置失败`)
        config.value = String(config.defaultValue)
      } else {
        configItems.forEach(e => (e.value = String(e.defaultValue)))
      }
      return JsonResult.OK()
    }
  },
  // 测试接口
  {
    url: `${baseUrl}/:type`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ query, body }: ApiRequest) => {
      const type = typeList.find(e => e.value === query.type)
      if (!type) {
        return JsonResult.FAIL_VALIDATION(`type: ${query.type} 不存在，无法测试方法`)
      } else if (type.ext?.length) {
        const nullAttribute = type.ext.filter(e => !body[e])
        if (nullAttribute?.length) return JsonResult.FAIL_VALIDATION(`${nullAttribute.join('、')} 值不能为空`)
      } else {
        return JsonResult.FAIL_INVALID_PARAM(`type: ${query.type} 无测试方法`)
      }
      return JsonResult.OK()
    }
  }
] as MockMethod[]
