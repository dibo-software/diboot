import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { Position } from '@/views/org-structure/position/type'
import { Random } from 'mockjs'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'

const jsonStr = `{"code":0,"msg":"操作成功","data":[{"id":"1527966280585904129","createTime":"2022-05-21 10:55:24","name":"产品总监","code":"kc6c","isVirtual":false,"gradeName":"高级","gradeValue":"E3","dataPermissionType":"SELF_AND_SUB","updateTime":"2022-05-21 10:55:24","dataPermissionTypeLabel":"本人及下属"},{"id":"9","createTime":"2021-11-27 03:16:59","name":"人事专员","code":"HR","isVirtual":false,"gradeName":"初级","gradeValue":"E1","dataPermissionType":"ALL","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"全部"},{"id":"8","createTime":"2021-11-26 08:51:48","name":"财务专员","code":"CWZY","isVirtual":false,"gradeName":"初级","gradeValue":"E1","dataPermissionType":"ALL","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"全部"},{"id":"7","createTime":"2021-11-26 08:41:44","name":"总经理","code":"ZJL","isVirtual":false,"gradeName":"公司领导","gradeValue":"M4","dataPermissionType":"ALL","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"全部"},{"id":"6","createTime":"2021-11-26 08:40:03","name":"副总经理","code":"FZJL","isVirtual":false,"gradeName":"公司领导","gradeValue":"M4","dataPermissionType":"ALL","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"全部"},{"id":"5","createTime":"2021-11-26 08:35:40","name":"部门主管","code":"BMZG","isVirtual":false,"gradeName":"高级","gradeValue":"E3","dataPermissionType":"DEPT_AND_SUB","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"本部门及下属部门"},{"id":"4","createTime":"2021-06-16 06:02:11","name":"研发工程师","code":"YF","isVirtual":false,"gradeName":"初级","gradeValue":"E1","dataPermissionType":"SELF","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"本人"},{"id":"3","createTime":"2021-01-27 15:36:45","name":"市场专员","code":"SCZY","isVirtual":false,"gradeName":"初级","gradeValue":"E1","dataPermissionType":"SELF","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"本人"},{"id":"2","createTime":"2021-01-27 15:28:44","name":"技术总监","code":"TECH_DIRECTOR","isVirtual":false,"gradeName":"专家","gradeValue":"E4","dataPermissionType":"DEPT_AND_SUB","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"本部门及下属部门"},{"id":"1","createTime":"2021-01-27 15:27:42","name":"项目经理","code":"PM","isVirtual":false,"gradeName":"中级","gradeValue":"E2","dataPermissionType":"DEPT","updateTime":"2022-03-16 06:24:38","dataPermissionTypeLabel":"本部门"}],"page":{"pageIndex":1,"pageSize":20,"totalCount":"10","orderBy":"id:DESC","entityClass":"com.diboot.iam.entity.IamPosition","totalPage":1},"ok":true}`
const obj = JSON.parse(jsonStr)
const list: Record<string, any>[] = obj.data
const arrList = list.map((item: Record<string, any>) => {
  return [
    item.name,
    item.code,
    item.gradeName,
    item.gradeValue,
    item.dataPermissionType,
    item.dataPermissionTypeLabel,
    item.isVirtual,
    item.createTime,
    item.updateTime
  ]
})
let nextId = 1
export const dataList: Position[] = arrList
  .filter(arr => arr.length >= 9)
  .map(arr => {
    return {
      id: `${++nextId}`,
      name: arr[0],
      code: arr[1],
      gradeName: arr[2],
      gradeValue: arr[3],
      dataPermissionType: arr[4],
      dataPermissionTypeLabel: arr[5],
      isVirtual: arr[6],
      createTime: arr[7],
      updateTime: arr[8]
    } as Position
  })

const crud = crudTemplate({
  baseApi: '/iam/position',
  dataList,
  keywordsKeys: ['name', 'code'],
  fuzzyMatchKeys: ['name', 'code']
})

export default [
  ...Object.values(crud.api),
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
] as MockMethod[]
