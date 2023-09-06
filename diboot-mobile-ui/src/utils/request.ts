import type { AxiosRequestHeaders, AxiosResponse } from 'axios'
import axios from 'axios'
import auth, { AUTH_HEADER_KEY } from './auth'
import router from '@/router'
import qs from 'qs'

// baseURL
const BASE_URL = import.meta.env.VITE_APP_BASE_URL
// 创建 axios 实例
const service = axios.create({
  // API 请求的默认前缀
  baseURL: BASE_URL,
  timeout: 30_000 // 请求超时时间
})

// 添加请求拦截器
service.interceptors.request.use(config => {
  // 让每个请求携带自定义 token 请根据实际情况自行修改
  const token = auth.getToken()
  if (token) (config.headers as AxiosRequestHeaders)[AUTH_HEADER_KEY] = token

  // 只针对get方式进行序列化
  if (config.method === 'get')
    config.paramsSerializer = { serialize: params => qs.stringify(params, { arrayFormat: 'repeat' }) }

  return config
})

// 添加响应拦截器
service.interceptors.response.use(
  response => {
    // 检查是否携带有新的token
    const newToken = response.headers[AUTH_HEADER_KEY.toLowerCase()]
    if (newToken) auth.setToken(newToken)

    // 如果请求成功，则重置心跳定时器
    if (response.status === 200) resetPingTimer()

    // 如果返回的自定义状态码为 4001， 则token过期，需要清理掉token并跳转至登录页面重新登录
    if (response.data && response.data.code === 4001) {
      auth.clearToken()
      const route = router.currentRoute.value
      router.push({ name: 'Login', query: { redirect: route.path, ...route.query } }).finally()
      throw new Error('登录过期，请重新登录')
    }

    return response
  },
  error => {
    let message
    if (error && error.response && error.response.status) {
      switch (error.response.status) {
        case 500:
          message = '服务器好像开小差了，重试下吧！'
          break
        case 400:
          message = '保存数据出错'
          break
        case 401:
          message = '没有权限'
          break
        case 403:
          message = '无权访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        default:
          message = '网络可能出现问题'
      }
      console.error(message)
    }
    return Promise.reject(error)
  }
)

// token 自动刷新（发送心跳）的时间间隔（分钟）
const TOKEN_REFRESH_EXPIRE = 10
// 心跳计时器
let pingTimer: number
resetPingTimer()

/**
 * 重置心跳定时器
 */
function resetPingTimer() {
  clearTimeout(pingTimer)
  pingTimer = setTimeout(() => {
    service.get('/auth/ping').then()
    resetPingTimer()
  }, TOKEN_REFRESH_EXPIRE * 60 * 1000)
}

export interface ApiData<T = never> {
  code: number
  msg: string
  data: T
  page?: Pagination
}

interface Pagination {
  pageIndex: number
  pageSize: number
  totalCount: number
  orderBy?: string
}

/**
 * 请求拆包
 * @param request 请求
 */
function unpack<T>(request: Promise<AxiosResponse<ApiData<T>>>): Promise<ApiData<T>> {
  return new Promise((resolve, reject) => {
    request
      .then(res => {
        // 操作成功时（code = 0）【其他情况自行调整】
        if (res.data.code === 0) {
          resolve(res.data)
        } else {
          reject(res.data)
        }
      })
      .catch(err => {
        reject(err)
      })
  })
}

const api = {
  get<T = never>(url: string, params?: unknown) {
    return unpack(service.get<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, { params }))
  },
  post<T = never>(url: string, data?: unknown) {
    return unpack(
      service.post<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, JSON.stringify(data), {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    )
  },
  put<T = never>(url: string, data?: unknown) {
    return unpack(
      service.put<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, JSON.stringify(data), {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    )
  },
  patch<T = never>(url: string, data?: unknown) {
    return unpack(
      service.patch<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, JSON.stringify(data), {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    )
  },
  delete<T = never>(url: string, params?: unknown) {
    return unpack(
      service.delete<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, {
        params,
        headers: {
          'X-Requested-With': 'XMLHttpRequest',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    )
  },
  /**
   * 上传文件接口
   *
   * @param url
   * @param formData
   */
  upload<T = FileRecord>(url: string, formData: FormData) {
    return unpack(service.post<ApiData<T>, AxiosResponse<ApiData<T>>, unknown>(url, formData))
  },
  /**
   * GET下载文件
   *
   * @param url
   * @param params
   * @param onDownloadProgress
   */
  download(url: string, params?: unknown, onDownloadProgress?: (percentage: number) => void) {
    return new Promise<ApiData<ArrayBuffer> & { filename: string }>((resolve, reject) => {
      service
        .get<ArrayBuffer, AxiosResponse<ArrayBuffer>, unknown>(url, {
          responseType: 'arraybuffer',
          params,
          headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Content-Type': 'application/json;charset=UTF-8'
          },
          onDownloadProgress: evt => {
            if (onDownloadProgress) onDownloadProgress(evt.total ? (evt.loaded / evt.total) * 100 : 0)
          }
        })
        .then(res => {
          if (res.headers.filename) {
            resolve({
              data: res.data,
              filename: decodeURI(res.headers.filename),
              code: parseInt(res.headers['code'] || '0'),
              msg: decodeURI(res.headers['msg'] || '')
            })
          } else {
            const decoder = new TextDecoder('utf-8')
            const result = JSON.parse(decoder.decode(new Uint8Array(res.data)))
            reject(result)
          }
        })
        .catch(err => {
          reject(err)
        })
    })
  },
  /**
   * POST下载文件（常用于提交json数据下载文件）
   * @param url
   * @param data
   * @param onDownloadProgress
   */
  postDownload(url: string, data?: unknown, onDownloadProgress?: (percentage: number) => void) {
    return new Promise<ApiData<ArrayBuffer> & { filename: string }>((resolve, reject) => {
      service
        .post<ArrayBuffer, AxiosResponse<ArrayBuffer>, unknown>(url, JSON.stringify(data), {
          responseType: 'arraybuffer',
          headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Content-Type': 'application/json;charset=UTF-8'
          },
          onDownloadProgress: evt => {
            if (onDownloadProgress) onDownloadProgress(evt.total ? (evt.loaded / evt.total) * 100 : 0)
          }
        })
        .then(res => {
          if (res.headers.filename) {
            resolve({
              data: res.data,
              filename: decodeURI(res.headers.filename),
              code: parseInt(res.headers['code'] || '0'),
              msg: decodeURI(res.headers['msg'] || '')
            })
          } else {
            const decoder = new TextDecoder('utf-8')
            const result = JSON.parse(decoder.decode(new Uint8Array(res.data)))
            reject(result)
          }
        })
        .catch(err => {
          reject(err)
        })
    })
  }
}

export { BASE_URL as baseURL, service as axios, api }
