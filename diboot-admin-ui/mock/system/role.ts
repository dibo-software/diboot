import { MockMethod } from 'vite-plugin-mock'
import JsonResult from '../_util'
import { Random } from 'mockjs'

const baseUrl = '/api/role'

const deleteDataIds: string[] = []

export default [
  {
    url: `${baseUrl}/list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      return JsonResult.PAGINATION(
        query.pageIndex,
        query.pageSize,
        Array.from({ length: 50 })
          .map((_, index) => ({
            id: String(index + 1),
            name: '角色' + (index + 1),
            code: 'role' + (index + 1),
            description: '@csentence',
            createTime: '@datetime',
            updateTime: '@datetime'
          }))
          .filter(e => !deleteDataIds.includes(e.id))
          .filter(e => e.name.match(query.name) && e.code.match(query.code))
      )
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'delete',
    response: ({ query }: any) => {
      deleteDataIds.push(query.id)
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/cancelDeleted`,
    timeout: Random.natural(50, 300),
    method: 'patch',
    response: ({ body }: any) => {
      deleteDataIds.splice(0, deleteDataIds.length, ...deleteDataIds.filter(e => !body.includes(e)))
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/batchDelete`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: any) => {
      deleteDataIds.push(...body)
      console.log(deleteDataIds)
      return JsonResult.OK()
    }
  }
] as MockMethod[]
