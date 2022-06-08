import type { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { mock, Random } from 'mockjs'
import { list2tree } from '../_treeUtil'
import type { OrgModel } from '@/views/orgUser/org/type'
import crudTemplate from '../_crudTemplate'

const baseUrl = '/api/org'

const arrList: any[][] = [
  ['1', '0', '0', '帝博集团', '帝博集团', 'COMP', 'DIBO_GROUP', '0', 1, '', '2022-06-01'],
  ['2', '1', '1', '苏州帝博', '苏州帝博', 'COMP', 'SUZHOU_DIBO', '0', 2, '', '2022-06-01'],
  ['3', '1', '1', '成都帝博', '成都帝博', 'COMP', 'CHENGDU_DIBO', '0', 2, '', '2022-06-01'],
  ['4', '2', '2', '总经理办公室', '总经办', 'DEPT', 'ZJB', '0', 3, '', '2022-06-01'],
  ['5', '1', '1', '财务部', '财务部', 'DEPT', 'CWB', '0', 3, '', '2022-06-01'],
  ['6', '1', '1', 'HR', 'HR', 'DEPT', 'HR', '0', 3, '', '2022-06-01'],
  ['7', '1', '1', '研发事业部', '研发部', 'DEPT', 'XMSYB', '0', 3, '', '2022-06-01'],
  ['8', '1', '1', '市场部', '市场部', 'DEPT', 'SCB', '0', 3, '', '2022-06-01']
]

const dataList = initOrgList(arrList)

const crud = crudTemplate({
  baseApi: '/org',
  dataList,
  fuzzyMatchKeys: ['name', 'shortName', 'code']
})

const mockMethods: MockMethod[] = [
  {
    url: `${baseUrl}/tree`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: any) => {
      const { deleteDataIds } = crud
      const validList = dataList.filter((item: OrgModel) => {
        if (!deleteDataIds || deleteDataIds.length === 0) {
          return true
        }
        if (item.id) {
          return !deleteDataIds.includes(item.id)
        } else {
          return false
        }
      })
      return JsonResult.OK(buildOrgTree(validList))
    }
  }
]
mockMethods.push(...Object.values(crud.api))

export default mockMethods

function initOrgList(arrList: any[][]): OrgModel[] {
  if (!arrList || arrList.length === 0) {
    return []
  }
  return arrList
    .filter((arr: any[]) => arr.length >= 11)
    .map((arr: any[]) => {
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
      } as OrgModel
    })
}

function buildOrgTree(list: OrgModel[]) {
  return list2tree(list)
}
