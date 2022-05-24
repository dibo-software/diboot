import { MockMethod } from 'vite-plugin-mock'
import { JsonResult, ApiRequest } from './_util'
import { Random } from 'mockjs'
import moment from 'moment'

export interface Option<T> {
  // 请求接口基础路径
  baseApi: string
  // 数据列表（默认：[]）
  dataList?: Array<T>
  // 主键属性（默认：id）
  primaryKey?: string
  // 模糊查询属性列表（默认：[]）
  fuzzyMatchKeys?: Array<string>
  // 开启分页（默认：true）
  enablePagination?: boolean
}

export default <T>(option: Option<T>) => {
  const baseUrl = '/api' + option.baseApi
  const primaryKey = option.primaryKey || 'id'
  const dataList = option.dataList ?? []
  const fuzzyMatchKeys = option.fuzzyMatchKeys ?? []
  // 删除数据ID列表
  const deleteDataIds: Array<string> = []
  return {
    baseUrl,
    deleteDataIds,
    // 通用接口
    api: <Record<string, MockMethod>>{
      getList: {
        url: `${baseUrl}/list`,
        timeout: Random.natural(50, 300),
        method: 'get',
        response: ({ query }: ApiRequest) => {
          let list = dataList.filter(e => !deleteDataIds.includes(`${e[primaryKey as keyof T]}`))
          const keys = Object.keys(query ?? {})
          for (const key of keys) {
            if (['pageIndex', 'pageSize', 'orderBy'].includes(key)) continue // 忽略分页排序属性
            const value = query[key]
            if (value) {
              if (/\d{4}-\d{2}-\d{2}/.test(value)) {
                if (key.endsWith('Begin')) {
                  list = list.filter(
                    e => new Date(`${e[key.replace(/Begin$/, '') as keyof T]}`.slice(0, 10)) >= new Date(value)
                  )
                } else if (key.endsWith('End')) {
                  list = list.filter(
                    e => new Date(`${e[key.replace(/End$/, '') as keyof T]}`.slice(0, 10)) <= new Date(value)
                  )
                } else {
                  list = list.filter(e => `${e[key as keyof T]}`.slice(0, 10) === value)
                }
              } else {
                list = list.filter(e =>
                  fuzzyMatchKeys.includes(key) ? `${e[key as keyof T]}`.match(value) : e[key as keyof T] === value
                )
              }
            }
          }
          return option.enablePagination === false
            ? JsonResult.OK(list)
            : JsonResult.PAGINATION(query.pageIndex, query.pageSize, list)
        }
      },
      getById: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'get',
        response: ({ query }: ApiRequest) => {
          return JsonResult.OK(dataList.find(e => e[primaryKey as keyof T] === query[primaryKey]))
        }
      },
      create: {
        url: `${baseUrl}`,
        timeout: Random.natural(50, 300),
        method: 'post',
        response: ({ body }: ApiRequest<T>) => {
          const id = String(dataList.length + 1)
          const now = moment().format('yyyy-MM-DD HH:mm:ss')
          Object.assign(body, { [primaryKey]: id, createTime: now, updateTime: now })
          dataList.unshift(body)
          return JsonResult.OK(id)
        }
      },
      updata: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'put',
        response: ({ body, query }: ApiRequest<T>) => {
          Object.assign(body, { updateTime: moment().format('yyyy-MM-DD HH:mm:ss') })
          dataList.splice(
            dataList.findIndex(e => e[primaryKey as keyof T] === query[primaryKey as keyof T]),
            1,
            body
          )
          return JsonResult.OK()
        }
      },
      remove: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'delete',
        response: ({ query }: ApiRequest) => {
          deleteDataIds.push(query.id)
          return JsonResult.OK()
        }
      },
      batchRemove: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'delete',
        response: ({ query }: ApiRequest) => {
          deleteDataIds.push(query.id)
          return JsonResult.OK()
        }
      },
      cancelRemove: {
        url: `${baseUrl}/cancelDeleted`,
        timeout: Random.natural(50, 300),
        method: 'patch',
        response: ({ body }: ApiRequest<Array<string>>) => {
          deleteDataIds.splice(0, deleteDataIds.length, ...deleteDataIds.filter(e => !body.includes(e)))
          return JsonResult.OK()
        }
      }
    }
  }
}
