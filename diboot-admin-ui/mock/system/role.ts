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
    name: '角色' + id,
    code: 'role' + id,
    permissionList: [],
    description: '@csentence',
    createTime: '@datetime',
    updateTime: '@datetime'
  } as Role)
})

const crud = crudTemplate({
  baseApi: '/iam/role',
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
