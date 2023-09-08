import { isExternal } from './validate'
import qs from 'qs'

/**
 * 构建图像源
 *
 * @param url
 */
export const buildImgSrc = (url: string) => {
  return isExternal(url) ? url : baseURL + url + '/image'
}

/**
 * el-image 绑定图片地址
 *
 * @param file
 */
export const imageBindSrc = (file: FileRecord) => {
  const accessUrl = buildImgSrc(file.accessUrl)
  return {
    src: file.thumbnailUrl ? buildImgSrc(file.thumbnailUrl) : accessUrl,
    previewSrcList: [accessUrl]
  }
}

/**
 * 下载文件
 *
 * @param url
 * @param params
 * @param onDownloadProgress
 */
export const fileDownload = (url: string, params?: unknown, onDownloadProgress?: (percentage: number) => void) => {
  if (isExternal(url)) window.open(url + (/\?/.test(url) ? '&' : '?') + qs.stringify(params, { arrayFormat: 'repeat' }))
  else {
    return new Promise<void>((resolve, reject) => {
      ;(Array.isArray(params)
        ? api.postDownload(url, params, onDownloadProgress)
        : api.download(url, params, onDownloadProgress)
      )
        .then(res => {
          if (res.data) {
            const blob = new Blob([res.data])
            const elink = document.createElement('a')
            elink.download = res.filename ?? ''
            elink.style.display = 'none'
            elink.href = URL.createObjectURL(blob)
            document.body.appendChild(elink)
            elink.click()
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink)
          }
          resolve()
        })
        .catch(err => {
          showNotify(err.msg ?? err.message ?? '下载文件失败')
          reject(err)
        })
    })
  }
}
