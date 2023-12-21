import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { Role } from '@/views/system/role/type'
import Mock, { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'

export const dataList: Role[] = Array.from({ length: 50 }).map((_, index) => {
  const id = String(50 - index)
  return Mock.mock({
    id,
    name: '租户' + id,
    code: '租户' + id,
    startDate: '@date',
    endDate: '@date',
    status: 'A',
    manager: '@cname',
    phone: '13466785432',
    statusLabel: {
      label: '有效',
      value: 'A',
      ext: {
        color: '#409Eff'
      }
    },
    description: '@csentence',
    createTime: '@datetime',
    updateTime: '@datetime'
  } as Role)
})

const crud = crudTemplate({
  baseApi: '/iam/tenant',
  dataList,
  fuzzyMatchKeys: ['name', 'code']
})

export default [
  ...Object.values(crud.api),
  {
    url: `${crud.baseUrl}/check-code-duplicate`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const id = query.id
      const isExistence = dataList.filter(item => item.id !== id).some(item => item.code === query.code)
      return isExistence ? JsonResult.FAIL_VALIDATION('该编码已存在') : JsonResult.OK()
    }
  }
] as MockMethod[]
