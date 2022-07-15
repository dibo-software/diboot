import type { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_crudTemplate'
import type { UserPosition } from '@/views/orgStructure/position/type'

export const dataList: UserPosition[] = []

const crud = crudTemplate({
  baseApi: '/userPosition',
  dataList,
  keywordsKeys: ['name', 'code'],
  fuzzyMatchKeys: ['name', 'code']
})

export default [...Object.values(crud.api)] as MockMethod[]
