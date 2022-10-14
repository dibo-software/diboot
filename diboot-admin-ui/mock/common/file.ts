import type { MockMethod } from 'vite-plugin-mock'
import { Random } from 'mockjs'
import { JsonResult } from '../_util'

const baseUrl = '/api/file'

export default [
  {
    url: `${baseUrl}/upload`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持上传文件')
    }
  },
  {
    url: `${baseUrl}/batch-uload`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持批量上传文件')
    }
  },
  {
    url: `${baseUrl}/fetch-upload`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持上传文件')
    }
  },
  {
    url: `${baseUrl}/:fileId`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持下载文件')
    }
  },
  {
    url: `${baseUrl}`,
    timeout: Random.natural(50, 300),
    method: 'post',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持打包下载文件')
    }
  },
  {
    url: `${baseUrl}/:fileId/image`,
    timeout: Random.natural(50, 300),
    method: 'get',
    response: () => {
      return JsonResult.FAIL_OPERATION('mock环境不支持预览图片')
    }
  }
] as MockMethod[]
