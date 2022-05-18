import { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'

const baseUrl = '/api/dictionary'

export default [
  {
    url: `${baseUrl}/list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(dictionaryDataMap.list)
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
