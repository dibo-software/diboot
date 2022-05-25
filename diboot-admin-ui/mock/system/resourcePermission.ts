import type { ResourcePermission } from '@/views/system/iamResourcePermission/type'
import { Random } from 'mockjs'
import { ApiRequest, JsonResult } from '../_util'
import { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_crudTemplate'
const baseUrl = '/api/iam/resourcePermission'

const dbDataList: ResourcePermission[] = [
  {
    id: '10000',
    createTime: '2022-05-19 01:18:33',
    parentId: '0',
    displayType: 'MENU',
    displayName: '系统管理',
    resourceCode: 'system',
    status: 'A',
    updateTime: '2022-05-19 01:18:33',
    children: [
      {
        id: '10001',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '数据字典管理',
        routePath: '/directory',
        redirectPath: '/directory/list',
        resourceCode: 'Dictionary',
        permissionCode: 'Dictionary:read',
        meta: '{"componentName": "Directory", "keepAlive":true}',
        status: 'A',
        sortId: '10030',
        updateTime: '2022-05-19 03:48:05',
        permissionCodes: ['Dictionary:read']
      },
      {
        id: '10006',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'IFRAME',
        displayName: '系统用户管理',
        routePath: 'http://www.baidu.com',
        resourceCode: 'IamUser',
        permissionCode: 'IamUser:read',
        meta: '{"componentName": "Directory", "keepAlive":false}',
        status: 'A',
        sortId: '10029',
        updateTime: '2022-05-19 10:42:41',
        permissionCodes: ['IamUser:read']
      },
      {
        id: '10012',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'OUTSIDE_URL',
        displayName: '角色资源管理',
        routePath: 'www',
        resourceCode: 'IamRole',
        permissionCode: 'IamRole:read',
        status: 'A',
        sortId: '10023',
        updateTime: '2022-05-19 10:43:06',
        permissionCodes: ['IamRole:read']
      },
      {
        id: '10017',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '资源权限管理',
        resourceCode: 'IamResourcePermission',
        permissionCode: 'IamResourcePermission:read',
        status: 'A',
        sortId: '10017',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamResourcePermission:read']
      },
      {
        id: '10023',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '定时任务管理',
        resourceCode: 'ScheduleJob',
        permissionCode: 'ScheduleJob:read',
        status: 'A',
        sortId: '10012',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['ScheduleJob:read']
      },
      {
        id: '10031',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '消息模板管理',
        resourceCode: 'MessageTemplate',
        permissionCode: 'MessageTemplate:read',
        status: 'A',
        sortId: '10010',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['MessageTemplate:read']
      },
      {
        id: '10036',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '消息记录管理',
        resourceCode: 'Message',
        permissionCode: 'Message:read',
        status: 'A',
        sortId: '10009',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['Message:read']
      },
      {
        id: '10038',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '上传文件管理',
        resourceCode: 'UploadFile',
        permissionCode: 'UploadFile:read',
        status: 'A',
        sortId: '10008',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['UploadFile:read']
      },
      {
        id: '10041',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '系统配置管理',
        resourceCode: 'SystemConfig',
        permissionCode: 'SystemConfig:read',
        status: 'A',
        sortId: '10007',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['SystemConfig:read']
      },
      {
        id: '10043',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '操作日志查看',
        resourceCode: 'IamOperationLog',
        permissionCode: 'IamOperationLog:read',
        status: 'A',
        sortId: '10006',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamOperationLog:read']
      },
      {
        id: '10045',
        createTime: '2022-05-19 01:18:33',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '登录日志查看',
        resourceCode: 'IamLoginTrace',
        permissionCode: 'IamLoginTrace:read',
        status: 'A',
        sortId: '10001',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamLoginTrace:read']
      }
    ]
  },
  {
    id: '10046',
    createTime: '2022-05-19 01:18:33',
    parentId: '0',
    displayType: 'MENU',
    displayName: '组织机构',
    resourceCode: 'orgStructure',
    status: 'A',
    updateTime: '2022-05-19 01:18:33',
    children: [
      {
        id: '10047',
        createTime: '2022-05-19 01:18:33',
        parentId: '10046',
        displayType: 'MENU',
        displayName: '组织机构管理',
        resourceCode: 'IamOrg',
        permissionCode: 'IamOrg:read',
        status: 'A',
        sortId: '10044',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamOrg:read']
      },
      {
        id: '10053',
        createTime: '2022-05-19 01:18:33',
        parentId: '10046',
        displayType: 'MENU',
        displayName: '岗位管理',
        resourceCode: 'IamPosition',
        permissionCode: 'IamPosition:read',
        status: 'A',
        sortId: '10038',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamPosition:read']
      },
      {
        id: '10058',
        createTime: '2022-05-19 01:18:33',
        parentId: '10046',
        displayType: 'MENU',
        displayName: '组织人员管理',
        resourceCode: 'IamOrgUser',
        permissionCode: 'IamOrg:read,IamUser:read',
        status: 'A',
        sortId: '10032',
        updateTime: '2022-05-19 01:18:33',
        permissionCodes: ['IamOrg:read', 'IamUser:read']
      }
    ]
  }
]
const tree2List = (tree: ResourcePermission[]): ResourcePermission[] => {
  const list: ResourcePermission[] = []
  for (const resourcePermission of tree) {
    if (resourcePermission.children && resourcePermission.children.length > 0) {
      for (const child of tree2List(resourcePermission.children)) {
        list.push(child)
      }
    }
    // 移除children
    const temp: ResourcePermission = { parentId: '', metaConfig: { icon: 'Plus' } }
    Object.assign(temp, resourcePermission)
    delete temp.children
    list.push(temp)
  }
  return list
}
export default [
  {
    url: `${baseUrl}/getMenuTreeList`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ query }: ApiRequest) => {
      return JsonResult.OK(dbDataList)
    }
  },
  {
    url: `${baseUrl}/`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest<ResourcePermission>) => {
      const id = String(dbDataList.length + 1)
      Object.assign(body, { id: id, displayName: '未命名' + id, displayType: 'MEUN' })
      dbDataList.unshift(body)
      return JsonResult.OK(id)
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
  }
] as MockMethod[]
