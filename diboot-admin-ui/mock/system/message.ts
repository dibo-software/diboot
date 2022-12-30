import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { Message } from '@/views/system/message/type'
import { Random } from 'mockjs'
import { JsonResult } from '../_util'
import type { ApiRequest } from '../_util'

const dataList: Message[] = Array.from({ length: 100 }).map((_, index) => {
  const id = String(100 - index)
  const method = Random.pick(['GET', 'POST', 'PUT', 'DELETE', 'PATCH'])
  return {
    id,
    appModule: Random.pick(['SYS', 'CRM', 'OA']),
    templateId: Random.id(),
    businessType: Random.pick(['Customer', 'Contract', 'OrderForm']),
    businessCode: Random.id(),
    sender: Random.pick(['张三', '李四', null]),
    receiver: Random.pick(['张三', '李四', null]),
    title: Random.pick(['这是一条系统发送的消息通知', null]),
    content: '发送内容测试',
    channel: Random.pick(['EMAIL', 'SMS', 'SYS']),
    channelLabel: Random.pick(['邮件', '短信', 'WebSocket', '系统消息']),
    status: Random.pick(['PENDING', 'FAILED', 'DELIVERY', 'READ']),
    statusLabel: Random.pick(['发送中', '发送异常', '已送达', '未读', '已读']),
    result: Random.pick(['待发送', '已发送']),
    scheduleTime: Random.datetime('yyyy-MM-dd HH:mm:ss'),
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss'),
    updateTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  } as Message
})

const crud = crudTemplate({
  baseApi: '/message',
  dataList
})

const ownList: Message[] = [
  {
    id: '0',
    sender: 'Diboot',
    status: 'PENDING',
    businessType: Random.pick(['Customer', 'Contract', 'OrderForm']),
    businessCode: Random.id(),
    receiver: '',
    channel: 'SYSTEM',
    title: '欢迎使用 diboot-admin-ui',
    content:
      '一个基于 Vue.js 3.x & TypeScript & Vite 的开箱即用的中后台管理系统；包含基本的身份认证和鉴权，DevTools 代码自动生成。',
    createTime: Random.now('yyyy-MM-dd HH:mm:ss'),
    updateTime: Random.now('yyyy-MM-dd HH:mm:ss')
  }
]

export default [
  crud.api.getList,
  crud.api.getById,
  {
    url: `${crud.baseUrl}/own`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(ownList)
    }
  },
  {
    url: `${crud.baseUrl}/read`,
    timeout: Random.natural(50, 300),
    method: 'patch',
    response: ({ body }: ApiRequest<string[]>) => {
      const ids = body
      ownList.forEach(e => {
        if (ids.includes(e.id)) {
          e.status = 'READ'
        }
      })
      return JsonResult.OK()
    }
  }
] as MockMethod[]
