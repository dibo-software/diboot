import { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import { list2tree } from '../_treeUtil'

const baseUrl = '/api/org'

const deleteDataIds: string[] = []

interface OrgModel {
  id?: string
  parentId: string
  topOrgId: string
  name: string
  shortName: string
  type: string
  code: string
  managerId: string
  depth: number
  orgComment?: string
  createTime: string
  children?: OrgModel[]
}

const arrList = [
  ['1', '0', '0', '帝博集团', '帝博集团', 'COMP', 'DIBO_GROUP', '0', 1, '', '2022-06-01'],
  ['2', '1', '1', '苏州帝博', '苏州帝博', 'COMP', 'SUZHOU_DIBO', '0', 2, '', '2022-06-01'],
  ['3', '1', '1', '成都帝博', '成都帝博', 'COMP', 'CHENGDU_DIBO', '0', 2, '', '2022-06-01'],
  ['4', '2', '2', '总经理办公室', '总经办', 'DEPT', 'ZJB', '0', 3, '', '2022-06-01'],
  ['5', '1', '1', '财务部', '财务部', 'DEPT', 'CWB', '0', 3, '', '2022-06-01'],
  ['6', '1', '1', 'HR', 'HR', 'DEPT', 'HR', '0', 3, '', '2022-06-01'],
  ['7', '1', '1', '研发事业部', '研发部', 'DEPT', 'XMSYB', '0', 3, '', '2022-06-01'],
  ['8', '1', '1', '市场部', '市场部', 'DEPT', 'SCB', '0', 3, '', '2022-06-01']
]

const nextId = 9

const dataList = initOrgList(arrList)

export default [
  {
    url: `${baseUrl}/tree`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      return JsonResult.OK(buildOrgTree(dataList))
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      const { id } = query
      if (!id) {
        return JsonResult.OK()
      }
      const item = dataList.find(item => item.id === id)
      return JsonResult.OK(item)
    }
  }
]

function initOrgList(arrList): OrgModel[] {
  if (!arrList || arrList.lenght === 0) {
    return []
  }
  return arrList
    .map(arr => {
      if (arr.length < 11) {
        return undefined
      }
      return {
        id: arr[0],
        parentId: arr[1],
        topOrgId: arr[2],
        name: arr[3],
        shortName: arr[4],
        type: arr[5],
        code: arr[6],
        managerId: arr[7],
        depth: arr[8],
        orgComment: arr[9],
        createTime: arr[10]
      }
    })
    .filter(item => item !== undefined)
}

function buildOrgTree(list) {
  return list2tree(list)
}
