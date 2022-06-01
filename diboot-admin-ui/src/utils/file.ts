import { isExternal } from './validate'

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
 */
export const fileDownload = (url: string) => {
  if (isExternal(url)) window.location.href = url
  else
    api
      .download(url)
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
      })
      .catch(err => {
        ElMessage.error(err.msg ?? err.message ?? '下载文件失败')
      })
}
