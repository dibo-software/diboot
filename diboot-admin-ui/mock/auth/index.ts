import type { MockMethod } from 'vite-plugin-mock'
import type { ApiRequest } from '../_util'
import { JsonResult } from '../_util'
import { Random } from 'mockjs'
import * as Element from '@element-plus/icons-vue'

const baseUrl = '/api/auth'

export default [
  // {
  //   url: `${baseUrl}/captcha`,
  //   timeout: Random.natural(50, 100),
  //   method: 'get',
  //   // response: () => Random.dataImage('130x48', 'Diboot')
  //   rawResponse: (req, res) => {
  //     res.setHeader('Content-Type', 'image/gif')
  //     res.setHeader('Pragma', 'No-cache')
  //     res.setHeader('Cache-Control', 'no-cache')
  //     res.write(Random.dataImage('130x48', 'Diboot'))
  //   }
  // },
  {
    url: `${baseUrl}/login`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: ({ body }: ApiRequest) => {
      if (body.username === 'admin' && body.password != null) {
        return JsonResult.OK(Random.string('lower', 32, 32))
      }
      return JsonResult.FAIL_OPERATION('用户名或密码错误')
    }
  },
  {
    url: `${baseUrl}/user-info`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: ({ headers }: ApiRequest) => {
      const token = headers.authorization
      if (token && token.length >= 32) {
        const name = Random.cname()
        return JsonResult.OK({
          info: {
            realname: name,
            email: Random.email()
          },
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
    url: `${baseUrl}/route`,
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
        path: 'rich-text',
        name: 'RichText',
        meta: {
          title: '富文本编辑器',
          icon: 'Element:Edit',
          componentPath: prefix + 'example/RichText.vue',
          sort: 1
        }
      },
      {
        path: 'document',
        name: 'Document',
        meta: {
          title: '预览打印',
          icon: 'Element:Printer',
          componentPath: prefix + 'example/Document.vue',
          sort: 3
        }
      },
      {
        path: 'watermarkExample',
        name: 'WatermarkExample',
        meta: {
          title: '水印',
          icon: 'Element:MagicStick',
          componentPath: prefix + 'example/WatermarkExample.vue',
          sort: 4
        }
      },
      {
        path: 'createQRCode',
        name: 'CreateQRCode',
        meta: {
          title: '二维码',
          icon: 'Element:FullScreen',
          componentPath: prefix + 'example/CreateQRCode.vue',
          sort: 5
        }
      },
      {
        path: 'echarts',
        name: 'Echarts',
        meta: {
          title: '图表',
          icon: 'Element:Histogram',
          componentPath: prefix + 'example/Echarts.vue',
          sort: 6
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
        path: 'diboot-website-iframe',
        name: 'Diboot-Iframe',
        meta: {
          title: 'iframe嵌套',
          icon: 'Element:Promotion',
          url: 'https://www.diboot.com',
          iframe: true
        }
      },
      {
        path: 'diboot-website-href',
        name: 'Diboot-href',
        meta: { title: '外链打开', icon: 'Element:Promotion', url: 'https://www.diboot.com' }
      }
    ]
  },
  {
    path: 'tenant',
    name: 'TenantMgt ',
    meta: { title: '租户管理', icon: 'Element:Platform' },
    children: [
      {
        path: 'tenant-info',
        name: 'TenantInfo',
        meta: {
          title: '租户信息管理',
          icon: 'Element:Platform',
          componentPath: prefix + 'tenant/tenant-info/List.vue',
          sort: 2,
          keepAlive: false,
          permissions: ['detail', 'create', 'update', 'delete']
        }
      },
    ]
  },
  {
    path: 'org-structure',
    name: 'OrgStructure ',
    redirect: '/org-structure/user',
    meta: { title: '组织人员', icon: 'Element:User' },
    children: [
      {
        path: 'user',
        name: 'User',
        meta: {
          title: '人员管理',
          componentPath: prefix + 'org-structure/user/index.vue',
          sort: '1',
          keepAlive: false,
          icon: 'Element:User',
          permissions: ['detail', 'create', 'update', 'delete', 'import', 'export', 'position', 'addPosition']
        }
      },
      {
        path: 'org',
        name: 'Org',
        meta: {
          title: '组织部门',
          componentPath: prefix + 'org-structure/org/index.vue',
          sort: '2',
          keepAlive: false,
          icon: 'Element:Folder',
          permissions: ['detail', 'create', 'update', 'delete', 'sort']
        }
      },
      {
        path: 'position',
        name: 'Position',
        meta: {
          title: '岗位管理',
          componentPath: prefix + 'org-structure/position/List.vue',
          sort: '3',
          keepAlive: false,
          hollow: false,
          icon: 'Element:Postcard',
          permissions: ['detail', 'create', 'update', 'delete']
        }
      }
    ]
  },
  {
    path: 'system',
    name: 'System',
    meta: { title: '系统管理', icon: 'Element:SetUp' },
    children: [
      {
        path: 'dictionary',
        name: 'Dictionary',
        meta: {
          title: '数据字典管理',
          icon: 'Element:Collection',
          componentPath: prefix + 'system/dictionary/List.vue',
          sort: 1,
          keepAlive: false,
          permissions: ['detail', 'create', 'update', 'delete']
        }
      },
      {
        path: 'resource',
        name: 'Resource',
        meta: {
          title: '菜单资源管理',
          icon: 'Element:Menu',
          componentPath: prefix + 'system/resource/index.vue',
          sort: 2,
          keepAlive: false,
          permissions: ['create', 'update', 'delete']
        }
      },
      {
        path: 'role',
        name: 'Role',
        meta: {
          title: '用户角色管理',
          icon: 'Element:User',
          componentPath: prefix + 'system/role/List.vue',
          sort: 3,
          permissions: ['detail', 'create', 'update', 'delete']
        }
      },
      {
        path: 'schedule-job',
        name: 'ScheduleJob',
        meta: {
          title: '定时任务管理',
          icon: 'Element:AlarmClock',
          componentPath: prefix + 'system/schedule-job/List.vue',
          keepAlive: false,
          sort: 4,
          permissions: ['create', 'update', 'delete', 'executeOnce', 'logList', 'logDelete']
        }
      },
      {
        path: 'message-template',
        name: 'MessageTemplate',
        meta: {
          title: '消息模板管理',
          icon: 'Element:MessageBox',
          componentPath: prefix + 'system/message-template/List.vue',
          keepAlive: false,
          sort: 5,
          permissions: ['detail', 'create', 'update', 'delete']
        }
      },
      {
        path: 'message',
        name: 'Message',
        meta: {
          title: '消息记录管理',
          icon: 'Element:Message',
          componentPath: prefix + 'system/message/List.vue',
          keepAlive: false,
          sort: 6,
          permissions: ['detail', 'delete']
        }
      },
      {
        path: 'file-record',
        name: 'FileRecord',
        meta: {
          title: '文件记录管理',
          icon: 'Element:FolderOpened',
          componentPath: prefix + 'system/file-record/List.vue',
          keepAlive: false,
          sort: 8,
          permissions: ['detail', 'update']
        }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        meta: {
          title: '系统配置管理',
          icon: 'Element:Setting',
          componentPath: prefix + 'system/config/index.vue',
          keepAlive: false,
          sort: 8,
          permissions: ['create', 'update', 'delete']
        }
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        meta: {
          title: '操作日志管理',
          icon: 'Element:Pointer',
          componentPath: prefix + 'system/operation-log/List.vue',
          sort: 9,
          permissions: ['detail']
        }
      },
      {
        path: 'login-trace',
        name: 'LoginTrace',
        meta: {
          title: '登录日志管理',
          icon: 'Element:Document',
          componentPath: prefix + 'system/login-trace/List.vue',
          sort: 10
        }
      }
      // {
      //   path: 'i18n-config',
      //   name: 'I18nConfig',
      //   meta: {
      //     title: '国际化管理',
      //     icon: 'Element:Setting',
      //     componentPath: prefix + 'system/i18n-config/List.vue',
      //     keepAlive: false,
      //     sort: 11,
      //     permissions: ['update']
      //   }
      // }
    ]
  }
]
