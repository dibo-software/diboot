import type { ResourcePermission, RestPermission } from '@/views/system/resourcePermission/type'
import Mock, { Random } from 'mockjs'
import { ApiRequest, JsonResult } from '../_util'
import { MockMethod } from 'vite-plugin-mock'
import dbDataList from './_realResourcePermissionData'
import dbRestPermissionDataList from './_realRestPermissionData'
const baseUrl = '/api/resourcePermission'
const deleteDataIds: string[] = []
/**
 * tree转化为list
 * @param tree
 */
const tree2List = (tree: ResourcePermission[]): ResourcePermission[] => {
  const list: ResourcePermission[] = []
  for (const resourcePermission of tree) {
    if (resourcePermission.children && resourcePermission.children.length > 0) {
      for (const child of tree2List(resourcePermission.children)) {
        list.push(child)
      }
    }
    // 移除children
    const temp: ResourcePermission = { parentId: '', routeMeta: { icon: 'Plus' } }
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
const list2Tree = (originList: ResourcePermission[]) => {
  const parentTopList: ResourcePermission[] = []
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
const buildChildren = (parents: ResourcePermission[], originList: ResourcePermission[]) => {
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
    url: `${baseUrl}/getMenuTreeList`,
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
    response: ({ body }: ApiRequest<ResourcePermission>) => {
      const mock = Mock.mock({ id: '@id' })
      const id = mock.id
      Object.assign(body, { id: id, displayName: '未命名' + id })
      dbDataList.unshift(body)
      return JsonResult.OK(id)
    }
  },
  {
    url: `${baseUrl}/apiList`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(dbRestPermissionDataList)
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
    url: `${baseUrl}/batchDelete`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<Array<string>>) => {
      deleteDataIds.push(...body)
      return JsonResult.OK()
    }
  }
] as MockMethod[]
