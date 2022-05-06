import { api } from '@/utils/request'
export interface IAttachMore {
  target: string // 对象名 / 字典名
  module?: string // 模块名（用于cloud指定module，默认当前module）
  alias?: string // 指定别名（默认为：target的小驼峰+Options）since v2.5.0
  label?: string // 对象的属性名，查询作为label的属性名称
  value?: string // 对象的属性名，，需要查询作为value的属性名称
  ext?: string // 对象的属性名，需要查询作为ext的属性名称 (扩展值，一般用于特殊情景)
  condition?: { [label: string]: string | Array<any> | null } // 查询条件
}
//
export interface ILabelValue {
  label: string
  value: any
  ext?: any
}

export type LabelValue = { [label: string]: ILabelValue[] }
/**
 * attachMore请求
 * @param attachMoreList
 * @param baseApi 设置api，表示发起个性化请求
 */
export const useAttachMore = (attachMoreList?: IAttachMore[], baseApi?: string): LabelValue => {
  const more = reactive<LabelValue>({})
  api.post<LabelValue>('/common/attachMore', attachMoreList).then(res => {
    Object.assign(more, res.code === 0 ? res.data : {})
  })
  baseApi &&
    api.get<LabelValue>(`${baseApi}/attachMore`).then(res => {
      Object.assign(more, res.code === 0 ? res.data : {})
    })
  return more
}
