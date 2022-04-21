import { MockMethod } from 'vite-plugin-mock'
import JsonResult from '../_util'
import { Random } from 'mockjs'

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
    response: ({ body }: any) => {
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
    response: () => {
      return JsonResult.OK({
        realname: Random.cname(),
        email: Random.email(),
        avatar: Random.image('250x250')
      })
    }
  },
  {
    url: `${baseUrl}/logout`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.OK()
    }
  },
  {
    url: `${baseUrl}/ping`,
    timeout: Random.natural(50, 300),
    method: 'get',
    rawResponse: (req, res) => {
      res.setHeader('Authorization', Random.string('lower', 32, 32))
      res.end()
    }
  }
] as MockMethod[]
