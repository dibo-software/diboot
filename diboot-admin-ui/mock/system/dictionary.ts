import { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import { AxiosRequestConfig } from 'axios'

const baseUrl = '/api/dictionary'

const deleteDataIds: string[] = []

const dictionaryDataMap = {
  list: [
    {
      id: '1',
      type: 'GENDER',
      itemName: '用户性别',
      itemValue: '',
      description: '用户性别数据字典',
      createTime: '2022-05-11',
      children: [
        {
          id: '2',
          type: 'GENDER',
          itemName: '男',
          itemValue: 'M',
          description: '男性',
          createTime: '2022-05-11'
        },
        {
          id: '3',
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

interface FormModel {
  id?: string
  itemName: string
  itemValue: string
  description?: string
  color?: string
  children?: FormModel[]
}

const dataList = Array.from({ length: 50 }).map((_, index): Record<string, any> => {
  const i = index % 2
  const item = dictionaryDataMap.list[i]
  item.id = String(index + 1)
  return item
})

export default [
  {
    url: `${baseUrl}/list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      return JsonResult.PAGINATION(
        query.pageIndex,
        query.pageSize,
        dataList.filter(e => !deleteDataIds.includes(e.id)).reverse()
      )
    }
  },
  {
    url: `${baseUrl}/`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: (request) => {
      console.log('request', request)
      const formModel: FormModel = request?.body
      if (formModel) {
        formModel.id = `${dataList.length + 1}`
        dataList.push(formModel)
      }
      return JsonResult.OK(formModel)
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: (formModel: FormModel) => {
      console.log('formModel', formModel)
      formModel.id = String(dataList.length + 1)
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
  },
  {
    url: `${baseUrl}/detail`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(dictionaryDataMap.detail)
    }
  }
] as MockMethod[]
