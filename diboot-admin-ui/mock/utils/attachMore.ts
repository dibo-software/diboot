import { MockMethod } from 'vite-plugin-mock'
import JsonResult from '../_util'
import { Random } from 'mockjs'

const baseUrl = '/api'

export default [
  {
    url: `${baseUrl}/common/attachMore`,
    timeout: Random.natural(50, 100),
    method: 'post',
    response: () => {
      return JsonResult.OK({
        genderOptions: [
          {
            label: '女',
            value: 'F'
          },
          {
            label: '男',
            value: 'M'
          }
        ],
        iamUserOptions: [
          {
            label: '超级管理员',
            value: 10000,
            ext: '000'
          }
        ]
      })
    }
  },
  {
    url: `${baseUrl}/iamResource/attachMore`,
    timeout: Random.natural(50, 100),
    method: 'get',
    response: () => {
      return JsonResult.OK({
        iamResourceOptions: [
          {
            label: '测试权限',
            value: 10000
          }
        ]
      })
    }
  }
]
