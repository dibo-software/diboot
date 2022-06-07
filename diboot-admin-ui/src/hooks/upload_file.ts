import type { UploadUserFile } from 'element-plus'
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

  return {
    action: `${baseURL}/file/upload`,
    fileList: uploadFileList,
    onSuccess,
    onRemove: () => setValue(getFileUids())
  }
}
