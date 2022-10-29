import type { MockMethod } from 'vite-plugin-mock'
import type { I18nConfig } from '@/views/system/i18n-config/type'
import { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import crudTemplate from '../_util/crud-template'

const dataList: I18nConfig[] = [
  {
    id: '1',
    type: 'SYSTEM',
    language: 'zh_CN',
    code: 'email.not.null',
    content: '邮箱不能为空',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: '2',
    type: 'SYSTEM',
    language: 'zh_TW',
    code: 'email.not.null',
    content: '郵箱不能爲空',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: '3',
    type: 'SYSTEM',
    language: 'en',
    code: 'email.not.null',
    content: 'E-mail cannot be empty',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: '4',
    type: 'CUSTOM',
    language: 'zh_CN',
    code: 'phone.verification.failed',
    content: '手机号验证不通过',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: '5',
    type: 'CUSTOM',
    language: 'zh_TW',
    code: 'phone.verification.failed',
    content: '手機號驗證不通過',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: '6',
    type: 'CUSTOM',
    language: 'en',
    code: 'phone.verification.failed',
    content: 'Phone number verification failed',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  }
]

const crud = crudTemplate({
  baseApi: '/i18n-config',
  dataList
})

export default [
  {
    url: `${crud.baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const list = query.code ? dataList.filter(e => e.code.match(query.code)) : dataList
      const data = list.reduce((map: Record<string, I18nConfig[]>, item: I18nConfig) => {
        const element = map[item.code]
        if (!element) map[item.code] = [item]
        else element.push(item)
        return map
      }, {})
      return JsonResult.PAGINATION(query.pageIndex, query.pageSize, Object.values(data))
    }
  },
  {
    url: `${crud.baseUrl}/:code`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => JsonResult.OK(dataList.filter(e => e.code === query.code))
  },
  {
    url: `${crud.baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<I18nConfig[]>) => {
      if (!Array.isArray(body)) return JsonResult.FAIL_VALIDATION()
      for (const item of body) {
        if (item.id) {
          dataList.splice(
            dataList.findIndex(e => e.id === item.id),
            1,
            item
          )
        } else {
          item.id = String(dataList.length + 1)
          dataList.push(item)
        }
      }
      return JsonResult.OK()
    }
  },
  {
    url: `${crud.baseUrl}/check-code-duplicate/:code`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ query, body }: ApiRequest<string[], I18nConfig>) => {
      const isExistence = dataList.filter(item => !body.includes(item.id)).some(item => item.code === query.code)
      return isExistence ? JsonResult.FAIL_VALIDATION('该编码已存在') : JsonResult.OK()
    }
  },
  crud.api.batchRemove
] as MockMethod[]
