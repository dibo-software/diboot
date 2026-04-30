import type { UploadFile, UploadFiles, UploadProgressEvent, UploadRequestOptions, UploadUserFile } from 'element-plus'

import type { ApiData } from '@/utils/request'
import { imageBindSrc } from '@/utils/file'

export default (setValue: (fileIds?: string) => void, getFileList: () => FileRecord[] | undefined) => {
  const uploadFileList = ref<UploadUserFile[]>([])

  watch(getFileList, value => {
    uploadFileList.value.length = 0
    if (value)
      uploadFileList.value.push(
        ...value.map(e => ({
          url: imageBindSrc(e).src,
          name: e.fileName,
          ...e
        }))
      )
  })

  const getFileIds = () => uploadFileList.value.map(e => String(e.id)).join()

  const onSuccess = (response: ApiData<FileRecord>, uploadFile: UploadUserFile, uploadFiles: UploadUserFile[]) => {
    if (!response) return
    const data = response.data
    if (data) {
      uploadFile.id = data.id
      uploadFile.url = imageBindSrc(data).src
      uploadFile.accessUrl = data.accessUrl
    }
    uploadFileList.value = uploadFiles
    setValue(getFileIds())
  }

  const onRemove = (uploadFile: UploadUserFile, uploadFiles: UploadUserFile[]) => {
    uploadFileList.value = uploadFiles
    setValue(getFileIds())
  }

  const httpRequest = async (options: UploadRequestOptions) => {
    const formData = new FormData()
    formData.set('file', options.file)
    api
      .upload<FileRecord>(options.action, formData)
      .then(res => options.onSuccess(res))
      .catch(err => options.onError(err))
  }
  const processPercents = ref<Record<number, number>>({})
  const onProgress = (evt: UploadProgressEvent, uploadFile: UploadFile, uploadFiles: UploadFiles) => {
    processPercents.value[uploadFile.uid] = evt.percent.toFixed(2)
  }
  const onError = (error: Error) => ElMessage.error(error.message)

  return {
    action: `/file/upload`,
    httpRequest,
    processPercents,
    fileList: uploadFileList,
    onSuccess,
    onRemove,
    onProgress,
    onError
  }
}
