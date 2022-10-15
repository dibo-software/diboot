import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { OperationLog } from '@/views/system/operation-log/type'
import { Random } from 'mockjs'

const operationMap: Record<string, string> = {
  GET: `@pick(['查看列表', '查看详情'])`,
  POST: '创建数据',
  PUT: '更新数据',
  DELETE: '删除数据',
  PATCH: '撤销删除'
}

const dataList: OperationLog[] = Array.from({ length: 100 }).map((_, index) => {
  const id = String(100 - index)
  const method = Random.pick(['GET', 'POST', 'PUT', 'DELETE', 'PATCH'])
  return {
    id,
    businessObj: Random.pick(['Customer', 'Contract', 'OrderForm']),
    operation: operationMap[method],
    userType: Random.pick(['User', 'Mobile']),
    userId: Random.id(),
    userRealname: 'DIBOOT',
    requestUri: Random.url('http', 'localhost').split('localhost')[1],
    requestMethod: method,
    requestParams: Random.pick(['{Long:10000}', null]),
    requestIp: Random.ip(),
    statusCode: Random.pick([0, 404, 0, 500, 0]),
    errorMsg: '',
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  } as OperationLog
})

const crud = crudTemplate({
  baseApi: '/iam/operation-log',
  dataList
})

export default [crud.api.getList, crud.api.getById] as MockMethod[]
