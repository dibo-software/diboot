import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'

export const dataList: FileRecord[] = [
  {
    uuid: '037d8cbdd45f4cfa9844942eeb95504c',
    fileName: 'logo.png',
    fileType: 'png',
    fileSize: '1908530',
    accessUrl: '/file/037d8cbdd45f4cfa9844942eeb95504c.png',
    createByName: 'DIBOOT',
    createTime: '2022-07-18 13:51:59'
  }
]

const crud = crudTemplate({
  baseApi: '/file-record',
  primaryKey: 'uuid',
  dataList,
  fuzzyMatchKeys: ['fileName']
})

export default [crud.api.getList, crud.api.getById, crud.api.update] as MockMethod[]
