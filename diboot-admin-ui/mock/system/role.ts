import { MockMethod } from 'vite-plugin-mock'
import crudTemplate from '../_crudTemplate'
import type { Role } from '@/views/system/role/type'
import Mock from 'mockjs'

const dataList: Role[] = Array.from({ length: 50 }).map((_, index) => {
  const id = String(50 - index)
  return Mock.mock({
    id,
    name: '角色' + id,
    code: 'role' + id,
    description: '@csentence',
    createTime: '@datetime',
    updateTime: '@datetime'
  } as Role)
})

const crud = crudTemplate({
  baseApi: '/role',
  dataList,
  fuzzyMatchKeys: ['name', 'code']
})

export default [...Object.values(crud.api)] as MockMethod[]
