import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import * as Element from '@element-plus/icons-vue'

const baseUrl = '/api/auth'

export default [
  {
    url: `${baseUrl}/captcha`,
    timeout: Random.natural(50, 100),
    method: 'get',
    // response: () => Random.dataImage('200x100', 'Diboot')
    rawResponse: (req, res) => {
      res.setHeader('Content-Type', 'image/gif')
      res.setHeader('Pragma', 'No-cache')
      res.setHeader('Cache-Control', 'no-cache')
      res.write(Random.dataImage('200x100', 'Diboot'))
    }
  },
  {
    url: `${baseUrl}/login`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest) => {
      if (body.username === 'admin' && body.password === '123456') {
        return JsonResult.OK({ token: Random.string('lower', 32, 32) })
      }
      return JsonResult.FAIL_OPERATION('用户名或密码错误')
    }
  },
  {
    url: `${baseUrl}/userInfo`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ headers }: ApiRequest) => {
      const token = headers.authorization
      if (token && token.length >= 32) {
        const name = Random.cname()
        return JsonResult.OK({
          realname: name,
          email: Random.email(),
          avatar: Random.image('50x50', Random.color(), Random.color(), name[0]),
          roles: [Random.pick(['admin', 'develop', 'test'])]
        })
      }
      return JsonResult.FAIL_INVALID_TOKEN()
    }
  },
  {
    url: `${baseUrl}/logout`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: () => {
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/ping`,
    timeout: Random.natural(50, 300),
    method: 'get',
    rawResponse: (req, res) => {
      const token = req.headers.authorization
      if (token && token.length >= 32) res.setHeader('Authorization', Random.string('lower', 32, 32))
      res.end()
    }
  },
  {
    url: `${baseUrl}/menu`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(authMenu)
    }
  }
] as MockMethod[]

// 随机按钮权限
const permission = '@pick(["detail", "create", "update", "delete", "import", "export"])'
// 随机图标
const icon = `Element:@pick(${Object.keys(Element)})`
// 避免@转义
const prefix = '@pick(["@"])/views/'

// 授权菜单
const authMenu = [
  {
    path: '/example',
    name: 'Example',
    meta: { title: '组件示例', icon: 'Element:Guide' },
    children: [
      {
        path: 'editor',
        name: 'Editor',
        meta: {
          title: '富文本编辑器',
          componentPath: prefix + 'example/index.vue',
          sort: 1
        }
      }
    ]
  },
  {
    path: 'external',
    name: 'External ',
    meta: { title: '外部链接', icon: 'Element:Connection' },
    children: [
      {
        path: 'vue-js',
        name: 'VueJs',
        meta: {
          title: 'Iframe嵌套',
          url: 'https://www.diboot.com',
          iframe: true
        }
      },
      {
        path: 'diboot',
        name: 'diboot',
        meta: { title: '外链打开', url: 'https://www.diboot.com' }
      }
    ]
  },
  {
    path: 'orgUser',
    name: 'OrgUser ',
    redirect: '/orgUser/org',
    meta: { title: '组织架构', icon: 'Element:User' },
    children: [
      {
        path: 'org',
        name: 'Org',
        meta: {
          title: '组织部门',
          componentPath: prefix + 'orgUser/org/index.vue',
          sort: '@natural',
          keepAlive: false,
          icon: 'Element:Folder'
        }
      },
      {
        path: 'position',
        name: 'Position',
        meta: {
          title: '岗位管理',
          componentPath: prefix + 'orgUser/position/list.vue',
          sort: '@natural',
          keepAlive: false,
          hollow: false,
          icon: 'Element:Guide'
        }
      },
      {
        path: 'user',
        name: 'User',
        meta: {
          title: '人员管理',
          componentPath: prefix + 'orgUser/user/index.vue',
          sort: '@natural',
          keepAlive: false,
          icon: 'Element:User'
        }
      }
    ]
  },
  {
    path: 'system',
    name: 'System',
    redirect: '/system/resourcePermission',
    meta: { title: '系统管理', icon: 'Element:SetUp' },
    children: [
      {
        path: 'resourcePermission',
        name: 'resourcePermission-@increment',
        meta: {
          title: '资源权限管理',
          componentPath: prefix + 'system/resourcePermission/list.vue',
          sort: 2,
          keepAlive: false
        }
      },
      {
        path: 'role',
        name: 'RoleList',
        meta: {
          title: '用户角色管理',
          componentPath: prefix + 'system/role/list.vue',
          sort: 3
        }
      },
      {
        path: 'dictionary',
        name: 'Dictionary',
        meta: {
          title: '数据字典管理',
          componentPath: prefix + 'system/dictionary/list.vue',
          keepAlive: false
        }
      },
      {
        path: 'scheduleJob',
        name: 'ScheduleJob',
        meta: {
          title: '定时任务管理',
          componentPath: prefix + 'system/scheduleJob/list.vue',
          keepAlive: false,
          sort: 6
        }
      },
      {
        path: 'messageTemplate',
        name: 'messageTemplate',
        meta: {
          title: '消息模板管理',
          componentPath: prefix + 'system/messageTemplate/list.vue',
          keepAlive: false,
          sort: 7
        }
      },
      {
        path: 'message',
        name: 'message',
        meta: {
          title: '消息记录管理',
          componentPath: prefix + 'system/message/list.vue',
          keepAlive: false,
          sort: 8
        }
      },
      {
        path: 'operationLog',
        name: 'OperationLog',
        meta: {
          title: '操作日志管理',
          componentPath: prefix + 'system/operationLog/list.vue',
          sort: 9
        }
      },
      {
        path: 'loginTrace',
        name: 'LoginTrace',
        meta: {
          title: '登录日志管理',
          componentPath: prefix + 'system/loginTrace/list.vue',
          sort: 10
        }
      }
    ]
  }
]
