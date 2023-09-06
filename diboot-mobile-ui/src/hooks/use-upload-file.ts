import type { UploaderFileListItem } from 'vant'
import { imageBindSrc } from '@/utils/file'
import type { UploaderAfterRead } from 'vant/es/uploader/types'

export type UploaderFileItem = UploaderFileListItem & { id?: string; accessUrl?: string }

export default (setValue: (fileIds?: string) => void, getFileList: () => FileRecord[] | undefined) => {
  const fileList = ref<UploaderFileItem[]>([])

  watch(getFileList, value => {
    fileList.value.length = 0
    if (value)
      fileList.value.push(
        ...value.map(e => ({
          id: e.id,
          url: imageBindSrc(e).src,
          file: { name: e.fileName } as any,
          accessUrl: e.accessUrl
        }))
      )
  })

  const getFileIds = () =>
    fileList.value
      .map(e => e.id)
      .filter(e => !!e)
      .map(String)
      .join()

  const onRemove = () => setValue(getFileIds())

  const uploadFileHandle = (file: UploaderFileItem | UploaderFileItem[]) => {
    if (Array.isArray(file)) return // 多文件上传待扩展
    file.status = 'uploading'
    file.message = '上传中...'
    const formData = new FormData()
    formData.set('file', file.file as File)
    api
      .upload('/file/upload', formData)
      .then(res => {
        const data = res.data
        file.id = data.id
        file.url = imageBindSrc(data).src
        file.accessUrl = data.accessUrl
        file.status = 'done'
        file.message = ''
      })
      .catch(err => {
        showFailToast(err.msg || err.message || err)
        file.status = 'failed'
        file.message = '上传失败'
      })
      .finally(() => setValue(getFileIds()))
  }

  return {
    fileList,
    uploadFileHandle,
    onRemove
  }
}
