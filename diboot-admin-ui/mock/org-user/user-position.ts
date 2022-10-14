import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_util/crud-template'
import type { UserPosition } from '@/views/org-structure/position/type'

export const dataList: UserPosition[] = []

const crud = crudTemplate({
  baseApi: '/user-position',
  dataList,
  keywordsKeys: ['name', 'code'],
  fuzzyMatchKeys: ['name', 'code']
})

export default [...Object.values(crud.api)] as MockMethod[]
