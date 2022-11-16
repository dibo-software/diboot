import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import { list2Tree } from '../_util/list'
import type { OrgModel } from '@/views/org-structure/org/type'
import crudTemplate from '../_util/crud-template'

const baseUrl = '/api/org'

const arrList: any[][] = [
  ['1', '0', '0', '帝博集团', 'COMP', 'DIBO_GROUP', '0', 1, '', '2022-06-01'],
  ['2', '1', '1', '苏州帝博', 'COMP', 'SUZHOU_DIBO', '0', 2, '', '2022-06-01'],
  ['3', '1', '1', '成都帝博', 'COMP', 'CHENGDU_DIBO', '0', 2, '', '2022-06-01'],
  ['4', '2', '2', '总经理办公室', 'DEPT', 'ZJB', '0', 3, '', '2022-06-01'],
  ['5', '1', '1', '财务部', 'DEPT', 'CWB', '0', 3, '', '2022-06-01'],
  ['6', '1', '1', 'HR', 'DEPT', 'HR', '0', 3, '', '2022-06-01'],
  ['7', '1', '1', '研发事业部', 'DEPT', 'XMSYB', '0', 3, '', '2022-06-01'],
  ['8', '1', '1', '市场部', 'DEPT', 'SCB', '0', 3, '', '2022-06-01']
]

const dataList = initOrgList(arrList)

const crud = crudTemplate({
  baseApi: '/iam/org',
  dataList,
  keywordsKeys: ['name', 'code'],
  fuzzyMatchKeys: ['name', 'code']
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
  },
  {
    url: `${crud.baseUrl}/check-code-duplicate`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const id = query.id
      const isExistence = dataList.filter(item => item.id !== id).some(item => item.code === query.code)
      return isExistence ? JsonResult.FAIL_VALIDATION('该编码已存在') : JsonResult.OK()
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
        type: arr[4],
        code: arr[5],
        managerId: arr[6],
        depth: arr[7],
        orgComment: arr[8],
        createTime: arr[9]
      } as OrgModel
    })
}

function buildOrgTree(list: OrgModel[]) {
  return list2Tree(list)
}
