import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_crudTemplate'
import type { MessageTemplate } from '@/views/system/messageTemplate/type'
import { Random } from 'mockjs'
import { JsonResult } from '../_util'

const dataList: MessageTemplate[] = Array.from({ length: 100 }).map((_, index) => {
  const id = String(100 - index)
  const method = Random.pick(['GET', 'POST', 'PUT', 'DELETE', 'PATCH'])
  return {
    id,
    requestUri: Random.url('http', 'localhost').split('localhost')[1],
    requestMethod: method,
    appModule: Random.pick(['SYS', 'CRM', 'OA']),
    code: Random.pick(['Customer', 'Contract', 'OrderForm']),
    title: Random.pick(['订阅通知消息模板', '短信验证码模板', null]),
    content: '发送内容模板测试，这里有变量#{用户姓名}: 您好',
    createByName: Random.pick(['张三', '李四', null]),
    createTime: Random.datetime('yyyy-MM-dd HH:mm:ss'),
    updateTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
  } as MessageTemplate
})

const crud = crudTemplate({
  baseApi: '/messageTemplate',
  dataList
})

export default [
  crud.api.getList,
  crud.api.getById,
  {
    url: `${crud.baseUrl}/variableList`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK(['${用户姓名}', '${称呼}', '${手机号}', '${验证码}'] as string[])
    }
  }
] as MockMethod[]