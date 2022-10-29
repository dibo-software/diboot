import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from './index'
import { JsonResult } from './index'
import { Random } from 'mockjs'

/**
 * 选项
 */
export interface Option<T> {
  // 请求接口基础路径
  baseApi: string
  // 数据列表（默认：[]）
  dataList?: Array<T>
  // 附加数据
  attachMore?: Record<string, LabelValue>
  // 主键属性（默认：id）
  primaryKey?: string
  // 关键词搜索字段列表
  keywordsKeys?: Array<string>
  // 模糊查询属性列表（默认：[]）
  fuzzyMatchKeys?: Array<string>
  // 开启分页（默认：true）
  enablePagination?: boolean
}

/**
 * 通用接口
 */
export interface Api {
  // 获取列表（默认启用分页）
  getList: MockMethod
  // 根据IDs获取列表
  listByIds: MockMethod
  // 获取详情
  getById: MockMethod
  // 创建数据
  create: MockMethod
  // 更新数据
  update: MockMethod
  // 删除数据
  remove: MockMethod
  // 批量删除
  batchRemove: MockMethod
}

/**
 * 通用 CRUD
 *
 * @param option
 */
export default <T extends {}>(option: Option<T>) => {
  const baseUrl = '/api' + option.baseApi
  const primaryKey = option.primaryKey || 'id'
  const dataList = option.dataList ?? []
  const attachMore = option.attachMore || {}
  const keywordsKeys = option.keywordsKeys ?? []
  const fuzzyMatchKeys = option.fuzzyMatchKeys ?? []
  // 删除数据ID列表
  const deleteDataIds: Array<string> = []
  return {
    baseUrl,
    deleteDataIds,
    api: <Readonly<Api>>{
      getList: {
        url: `${baseUrl}`,
        timeout: Random.natural(50, 300),
        method: 'get',
        response: ({ query }: ApiRequest) => {
          // 过滤逻辑删除数据
          let list = dataList.filter(e => !deleteDataIds.includes(`${e[primaryKey as keyof T]}`))
          const keys = Object.keys(query ?? {})
          for (const key of keys) {
            // 忽略分页排序属性
            if (['pageIndex', 'pageSize', 'orderBy'].includes(key)) continue
            const queryValue = query[key]
            // 该属性的查询参数无值时不进行过滤
            if (queryValue == null || queryValue === '') continue
            // 判断查询参数是否为日期类型
            if (/\d{4}-\d{2}-\d{2}/.test(queryValue)) {
              // 过滤出大与等于指定日期的数据
              if (key.endsWith('Begin'))
                list = list.filter(e => `${e[key.replace(/Begin$/, '') as keyof T]}`.slice(0, 10) >= queryValue)
              // 过滤出小与等于指定日期的数据
              else if (key.endsWith('End'))
                list = list.filter(e => `${e[key.replace(/End$/, '') as keyof T]}`.slice(0, 10) <= queryValue)
              // 过滤出指定日期的数据
              else list = list.filter(e => `${e[key as keyof T]}`.slice(0, 10) === queryValue)
            } else {
              list = list.filter(e => {
                const itemValue = e[key as keyof T]
                if (key === 'keywords') {
                  for (const wordsKey of keywordsKeys) {
                    const val = e[wordsKey as keyof T]
                    if (fuzzyMatchKeys.includes(wordsKey) && `${val}`.match(queryValue)) {
                      return true
                    }
                  }
                  return false
                } else if (itemValue == null) return false
                // 指定属性 模糊匹配
                else if (fuzzyMatchKeys.includes(key)) return `${itemValue}`.match(queryValue)
                // 属性值与查询参数同时为数组，判断属性值中是否包含查询参数中的某个
                else if (queryValue instanceof Array && itemValue instanceof Array)
                  return itemValue.some(e => queryValue.includes(e))
                // 属性值为数组时，判断查询参数是否被包含
                else if (itemValue instanceof Array) return itemValue.includes(queryValue)
                // 查询参数为数组时 判断属性值是否被包含
                else if (queryValue instanceof Array) return queryValue.includes(itemValue)
                // 转为字符串进行忽略类型比较
                else return String(itemValue) === String(queryValue)
              })
            }
          }
          return option.enablePagination === false
            ? JsonResult.OK(list)
            : JsonResult.PAGINATION(query.pageIndex, query.pageSize, list)
        }
      },
      listByIds: {
        url: `${baseUrl}/ids`,
        timeout: Random.natural(50, 300),
        method: 'post',
        response: ({ body }: ApiRequest<string[]>) => {
          if (!body || body.length) return JsonResult.OK()
          const validList = dataList.filter(item => {
            return body.includes(item[primaryKey as keyof typeof item] as string)
          })
          return JsonResult.OK(validList)
        }
      },
      attachMore: {
        url: `${baseUrl}/attachMore`,
        timeout: Random.natural(50, 300),
        method: 'get',
        response: () => {
          return JsonResult.OK(attachMore)
        }
      },
      getById: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'get',
        response: ({ query }: ApiRequest) => {
          return JsonResult.OK(dataList.find(e => e[primaryKey as keyof T] === query.id))
        }
      },
      create: {
        url: `${baseUrl}`,
        timeout: Random.natural(50, 300),
        method: 'post',
        response: ({ body }: ApiRequest<T>) => {
          const id = String(dataList.length + 1)
          const now = Random.now('yyyy-MM-DD HH:mm:ss')
          Object.assign(body, { [primaryKey]: id, createTime: now, updateTime: now })
          dataList.unshift(body)
          return JsonResult.OK(id)
        }
      },
      update: {
        url: `${baseUrl}/:id`,
        timeout: Random.natural(50, 300),
        method: 'put',
        response: ({ body, query }: ApiRequest<T>) => {
          Object.assign(body, { updateTime: Random.now('yyyy-MM-DD HH:mm:ss') })
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
        url: `${baseUrl}/batch-delete`,
        timeout: Random.natural(50, 300),
        method: 'post',
        response: ({ body }: ApiRequest<Array<string>>) => {
          deleteDataIds.push(...body)
          return JsonResult.OK()
        }
      }
    }
  }
}
