import type { UploadRequestOptions, UploadUserFile } from 'element-plus'
import type { ApiData } from '@/utils/request'
import { imageBindSrc } from '@/utils/file'

export default (setValue: (fileUids?: string) => void, getFileList: () => FileRecord[] | undefined) => {
  const uploadFileList: UploadUserFile[] = reactive([])

  watch(getFileList, value => {
    uploadFileList.splice(0)
    if (value)
      uploadFileList.push(
        ...value.map(e => ({
          uuid: e.uuid,
          url: imageBindSrc(e).src,
          name: e.fileName,
          accessUrl: e.accessUrl
        }))
      )
  })

  const getFileUids = () => {
    return uploadFileList.map(e => String(e.uuid)).join()
  }

  const onSuccess = (response: ApiData<FileRecord>, uploadFile: UploadUserFile) => {
    const data = response.data
    if (data) {
      uploadFile.uuid = data.uuid
      uploadFile.url = imageBindSrc(data).src
      uploadFile.accessUrl = data.accessUrl
    }
    setValue(getFileUids())
  }

  const httpRequest = async (options: UploadRequestOptions) => {
    const formData = new FormData()
    formData.set('file', options.file)
    api
      .upload<FileRecord>(options.action, formData)
      .then(res => options.onSuccess(res))
      .catch(err => options.onError(err))
  }

  return {
    action: `/file/upload`,
    httpRequest,
    fileList: uploadFileList,
    onSuccess,
    onRemove: () => setValue(getFileUids())
  }
}
