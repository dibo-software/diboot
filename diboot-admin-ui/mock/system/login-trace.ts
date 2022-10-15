import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { LoginTrace } from '@/views/system/login-trace/type'
import { Random } from 'mockjs'

const dataList: LoginTrace[] = Array.from({ length: 100 }).map((_, index) => {
  const id = String(100 - index)
  return {
    id,
    userId: Random.id(),
    userType: Random.pick(['User', 'Mobile']),
    authType: Random.pick(['PWD', 'WX', 'SSO']),
    authAccount: Random.first(),
    success: Random.boolean(),
    ipAddress: Random.ip(),
    userAgent: 'headers',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  } as LoginTrace
})

const crud = crudTemplate({
  baseApi: '/iam/login-trace',
  dataList: dataList,
  fuzzyMatchKeys: ['authAccount']
})

export default [crud.api.getList] as MockMethod[]
