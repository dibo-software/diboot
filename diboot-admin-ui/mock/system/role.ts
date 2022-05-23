import { MockMethod } from 'vite-plugin-mock'
import { JsonResult, ApiRequest } from '../_util'
import { Random } from 'mockjs'
import type { Role } from '@/views/system/role/type'

const baseUrl = '/api/role'

const deleteDataIds: string[] = []

const dataList: Role[] = Array.from({ length: 50 }).map((_, index) => {
  const id = String(50 - index)
  return {
    id,
    name: '角色' + id,
    code: 'role' + id,
    description: '@csentence',
    createTime: '@datetime',
    updateTime: '@datetime'
  }
})

export default [
  {
    url: `${baseUrl}/list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.PAGINATION(
        query.pageIndex,
        query.pageSize,
        dataList
          .filter(e => !deleteDataIds.includes(e.id))
          .filter(e => e.name.match(query.name) && e.code.match(query.code))
      )
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(dataList.find(e => e.id === query.id))
    }
  },
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Role>) => {
      dataList.unshift(body)
      return JsonResult.OK((body.id = String(dataList.length + 1)))
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ body, query }: ApiRequest<Role>) => {
      dataList.splice(
        dataList.findIndex(e => e.id === query.id),
        1,
        body
      )
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'delete',
    response: ({ query }: ApiRequest) => {
      deleteDataIds.push(query.id)
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/cancelDeleted`,
    timeout: Random.natural(50, 300),
    method: 'patch',
    response: ({ body }: ApiRequest<Array<string>>) => {
      deleteDataIds.splice(0, deleteDataIds.length, ...deleteDataIds.filter(e => !body.includes(e)))
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/batchDelete`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Array<string>>) => {
      deleteDataIds.push(...body)
      return JsonResult.OK()
    }
  }
] as MockMethod[]
