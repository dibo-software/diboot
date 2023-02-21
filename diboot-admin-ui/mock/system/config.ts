import type { MockMethod } from 'vite-plugin-mock'
import { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import type { SystemConfig } from '@/views/system/config/type'

const baseUrl = '/api/system-config'

const configItems: SystemConfig[] = [
  {
    id: '1',
    category: '邮箱配置',
    propKey: '服务',
    propValue: 'smtp.136.com',
    dataType: 'text'
  },

  {
    id: '2',
    category: '邮箱配置',
    propKey: '用户名',
    propValue: '',
    dataType: 'text'
  },
  {
    id: '3',
    category: '邮箱配置',
    propKey: '授权码',
    dataType: 'text'
  },
  {
    id: '4',
    category: '邮箱配置',
    propKey: '开启邮箱',
    dataType: 'boolean'
  },
  {
    id: '5',
    propKey: '全局配置',
    propValue: '示例',
    dataType: 'text'
  }
]

const deleteIds: string[] = []

const getConfigItems = () => configItems.filter(e => !deleteIds.includes(e.id))

export default [
  // 获取配置类型列表
  {
    url: `${baseUrl}/category`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK([
        ...new Set(
          getConfigItems()
            .map(e => e.category)
            .filter(e => !!e)
        )
      ])
    }
  },
  // 获取指定类型配置信息列表
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(
        getConfigItems().filter(
          e => e.category === query.category || (!e.category?.length && !!e.category === !!query.category)
        )
      )
    }
  },
  // 删除配置值
  {
    url: `${baseUrl}/value`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const map = getConfigItems()
        .filter(e => e.category === query.category || (!e.category?.length && !!e.category === !!query.category))
        .reduce((map, e) => {
          map[e.propKey] = map.propValue
          return map
        }, {} as Record<string, unknown>)
      return JsonResult.OK(query.propKey ? map[query.propKey] : map)
    }
  },
  // 检查属性值是否重复
  {
    url: `${baseUrl}/check-prop-key-duplicate`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      if (!query.propKey) {
        return JsonResult.OK()
      }
      let list = getConfigItems()
      if (query.id) list = list.filter(e => e.id !== query.id)
      if (query.category) list = list.filter(e => e.category === query.category)
      else list = list.filter(e => !e.category)
      list = list.filter(e => e.propKey === query.propKey)
      if (list.length) {
        return JsonResult.FAIL_VALIDATION(
          (query.category ? '类别[' + query.category + ']中' : '') + '属性名[' + query.propKey + ']已存在'
        )
      }
      return JsonResult.OK()
    }
  },
  // 获取指定类型配置信息
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(getConfigItems().find(e => e.id === query.id))
    }
  },
  // 新建配置值
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<SystemConfig>) => {
      body.id = `${configItems.length}`
      configItems.push(body)
      return JsonResult.OK()
    }
  },
  // 修改配置值
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ query, body }: ApiRequest<SystemConfig>) => {
      configItems.splice(Number(query.id) - 1, 1, body)
      return JsonResult.OK()
    }
  },
  // 批量修改配置值
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ body }: ApiRequest<SystemConfig[]>) => {
      body.forEach(e => configItems.splice(Number(e.id) - 1, 1, e))
      return JsonResult.OK()
    }
  },
  // 删除配置值
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'delete',
    response: ({ query }: ApiRequest) => {
      if (!deleteIds.includes(query.id)) deleteIds.push(query.id)
      return JsonResult.OK()
    }
  }
] as MockMethod[]
