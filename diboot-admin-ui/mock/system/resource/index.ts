import type { Resource } from '@/views/system/resource/type'
import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../../_util'
import { JsonResult } from '../../_util'
import Mock, { Random } from 'mockjs'
import realResourcePermissionData from './_data/real-resource-data'
import dbRestPermissionDataList from './_data/real-rest-permission-data'
import dbCloudRestPermissionDataList from './_data/real-cloud-rest-permission-data'
import realRoleResourcePermissionData from './_data/real-role-resource-data'
const baseUrl = '/api/iam/resource'
const deleteDataIds: string[] = []
const dbDataList = realResourcePermissionData as unknown as Resource[]
const dbRoleResourcePermissionData = realRoleResourcePermissionData as unknown as Resource[]
/**
 * tree转化为list
 * @param tree
 */
const tree2List = (tree: Resource[]): Resource[] => {
  const list: Resource[] = []
  for (const resourcePermission of tree) {
    if (resourcePermission.children && resourcePermission.children.length > 0) {
      for (const child of tree2List(resourcePermission.children)) {
        list.push(child)
      }
    }
    // 移除children
    const temp: Resource = { parentId: '', routeMeta: { icon: 'Plus' } }
    Object.assign(temp, resourcePermission)
    delete temp.children
    list.push(temp)
  }
  return list
}
/**
 * list转化为tree
 * @param originList
 */
const list2Tree = (originList: Resource[]) => {
  const parentTopList: Resource[] = []
  const removeIds: string[] = []
  for (const resourcePermission of originList) {
    resourcePermission.children = []
    if (resourcePermission.parentId === '0') {
      removeIds.push(resourcePermission.id as string)
      parentTopList.push(resourcePermission)
      continue
    }
  }
  originList = originList.filter(item => !removeIds.includes(item.id as string))
  buildChildren(parentTopList, originList)
  return parentTopList
}
/**
 * list转化为tree 构建子项
 * @param originList
 */
const buildChildren = (parents: Resource[], originList: Resource[]) => {
  if (!parents || parents.length === 0) {
    return
  }
  const removeIds: string[] = []
  for (const parent of parents) {
    parent.children = []
    for (const resourcePermission of originList) {
      if (resourcePermission.parentId === parent.id) {
        removeIds.push(resourcePermission.id as string)
        parent.children.push(resourcePermission)
      }
    }
    originList = originList.filter(item => !removeIds.includes(item.id as string))
    if (parent.children && parent.children.length > 0) {
      buildChildren(parent.children, originList)
    }
  }
}
export default [
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(dbRoleResourcePermissionData)
    }
  },
  {
    url: `${baseUrl}/menu-tree`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const list = tree2List(dbDataList)
      const result = list.filter(item => !deleteDataIds.includes(item.id as string))
      return JsonResult.OK(list2Tree(result))
    }
  },
  {
    url: `${baseUrl}/`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Resource>) => {
      const mock = Mock.mock({ id: '@id' })
      const id = mock.id
      Object.assign(body, { id: id })
      dbDataList.unshift(body)
      return JsonResult.OK(id)
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'put',
    response: ({ body }: ApiRequest<Resource>) => {
      const list = tree2List(dbDataList)
      const index = list.findIndex(item => item.id === body.id)
      list.splice(index, 1, body)
      dbDataList.length = 0

      dbDataList.push(...list2Tree(list))
      return JsonResult.OK(true)
    }
  },
  {
    url: `${baseUrl}/api-list`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(dbRestPermissionDataList)
      // return JsonResult.OK(dbCloudRestPermissionDataList)
    }
  },
  {
    url: `${baseUrl}/:id`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const list = tree2List(dbDataList)
      return JsonResult.OK(list.find(item => item.id === query.id))
    }
  },
  {
    url: `${baseUrl}/batch-delete`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Array<string>>) => {
      deleteDataIds.push(...body)
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/check-code-duplicate`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      const id = query.id
      const isExistence = tree2List(dbDataList)
        .filter(item => item.id !== id)
        .some(item => item.resourceCode === query.code)
      return isExistence ? JsonResult.FAIL_VALIDATION('该编码已存在') : JsonResult.OK()
    }
  }
] as MockMethod[]
