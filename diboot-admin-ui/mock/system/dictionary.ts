import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import { cloneDeep } from 'lodash'
import type { Dictionary } from '@/views/system/dictionary/type'
import crudTemplate from '../_util/crud-template'

const baseUrl = '/api/dictionary'

const deleteDataIds: string[] = []

let nextId = 9

const dictionaryDataMap = {
  list: [
    {
      id: '1',
      type: 'GENDER',
      parentId: '0',
      itemName: '用户性别',
      itemValue: '',
      description: '用户性别数据字典',
      createTime: '2022-05-11',
      children: [
        {
          id: '2',
          parentId: '1',
          type: 'GENDER',
          itemName: '男',
          itemValue: 'M',
          description: '男性',
          createTime: '2022-05-11'
        },
        {
          id: '3',
          parentId: '1',
          type: 'GENDER',
          itemName: '女',
          itemValue: 'F',
          description: '女性',
          createTime: '2022-05-11'
        }
      ]
    },
    {
      id: '4',
      parentId: '0',
      type: 'ACCOUNT_STATUS',
      itemName: '账号状态',
      itemValue: '',
      description: '用户账号状态信息',
      createTime: '2022-05-11',
      children: [
        {
          id: '5',
          parentId: '4',
          type: 'ACCOUNT_STATUS',
          itemName: '有效',
          itemValue: 'A',
          description: '有效',
          createTime: '2022-05-11'
        },
        {
          id: '6',
          parentId: '4',
          type: 'ACCOUNT_STATUS',
          itemName: '无效',
          itemValue: 'I',
          description: '无效',
          createTime: '2022-05-11'
        },
        {
          id: '7',
          parentId: '4',
          type: 'ACCOUNT_STATUS',
          itemName: '锁定',
          itemValue: 'L',
          description: '锁定',
          createTime: '2022-05-11'
        },
        {
          id: '8',
          parentId: '4',
          type: 'ACCOUNT_STATUS',
          itemName: '停用',
          itemValue: 'S',
          description: '停用',
          createTime: '2022-05-11'
        }
      ]
    }
  ],
  detail: {
    id: '4',
    type: 'ACCOUNT_STATUS',
    itemName: '账号状态',
    itemValue: '',
    description: '用户账号状态信息',
    createTime: '2022-05-11',
    children: [
      {
        id: '5',
        type: 'ACCOUNT_STATUS',
        itemName: '有效',
        itemValue: 'A',
        description: '有效',
        createTime: '2022-05-11'
      },
      {
        id: '6',
        type: 'ACCOUNT_STATUS',
        itemName: '无效',
        itemValue: 'I',
        description: '无效',
        createTime: '2022-05-11'
      },
      {
        id: '7',
        type: 'ACCOUNT_STATUS',
        itemName: '锁定',
        itemValue: 'L',
        description: '锁定',
        createTime: '2022-05-11'
      },
      {
        id: '8',
        type: 'ACCOUNT_STATUS',
        itemName: '停用',
        itemValue: 'S',
        description: '停用',
        createTime: '2022-05-11'
      }
    ]
  }
}

const dataList = Array.from({ length: 50 }).map((row, index): Record<string, any> => {
  const i = index % 2
  const item = cloneDeep(dictionaryDataMap.list[i])
  item.id = `${++nextId}`
  const { children } = item
  if (children && children.length > 0) {
    children.forEach(row => {
      row.id = `${++nextId}`
      row.parentId = item.id
    })
  }
  return item
})

const crud = crudTemplate({
  baseApi: '/dictionary',
  dataList,
  keywordsKeys: ['type', 'itemName'],
  fuzzyMatchKeys: ['type', 'itemName']
})

const mockMethods: MockMethod[] = [
  crud.api.getList,
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      console.log('dictionary custom get', query.id)
      const { id } = query
      if (!id) {
        return JsonResult.OK()
      }
      const item = dataList.find(item => item.id === id)
      return JsonResult.OK(item)
    }
  },
  {
    url: `${baseUrl}/`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: (request: any) => {
      const formModel: Dictionary = request?.body
      if (formModel) {
        formModel.id = `${++nextId}`
        formModel.parentId = `0`
        const { children } = formModel
        if (children && children.length > 0) {
          children.forEach(item => {
            item.id = `${++nextId}`
            item.parentId = formModel.id
          })
        }
        dataList.push(formModel)
      }
      return JsonResult.OK(formModel)
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: (request: any) => {
      const formModel: Dictionary = request?.body
      const currentId = request?.query?.id
      formModel.id = String(dataList.length + 1)
      const { children } = formModel
      if (children && children.length > 0) {
        children.forEach(item => {
          item.id = `${++nextId}`
          item.parentId = formModel.id
        })
      }
      // 获取当前currentId的序号，使用当前对象替换已有对象
      const index = dataList.findIndex(item => item.id === currentId)
      if (index !== -1) {
        dataList.splice(index, 1, formModel)
      }
      return JsonResult.OK(formModel)
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
    url: `${baseUrl}/batch-delete`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: any) => {
      deleteDataIds.push(...body)
      console.log(deleteDataIds)
      return JsonResult.OK()
    }
  },
  {
    url: `${crud.baseUrl}/check-type-duplicate`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const id = query.id
      const isExistence = dataList.filter(item => item.id !== id).some(item => item.type === query.type)
      return isExistence ? JsonResult.FAIL_VALIDATION('该编码已存在') : JsonResult.OK()
    }
  }
] as MockMethod[]

export default mockMethods
