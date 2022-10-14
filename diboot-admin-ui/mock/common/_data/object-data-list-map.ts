import { dataList as roleList } from '../../system/role'

export default {
  Role: roleList as Array<unknown>
} as Record<string, Array<Record<string, unknown>>>
