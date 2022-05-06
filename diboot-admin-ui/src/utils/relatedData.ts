import { api } from '@/utils/request'
export interface RelatedDataConfig {
  target: string // 对象名 / 字典名
  cache?: boolean // 数据是否缓存 TODO
  module?: string // 模块名（用于cloud指定module，默认当前module）
  alias?: string // 指定别名（默认为：target的小驼峰+Options）
  label?: string // 对象的属性名，查询作为label的属性名称
  value?: string // 对象的属性名，，需要查询作为value的属性名称
  ext?: string // 对象的属性名，需要查询作为ext的属性名称 (扩展值，一般用于特殊情景)
  condition?: { [label: string]: string | Array<any> | null } // 查询条件
}
export interface ILabelValue {
  label: string
  value: any
  ext?: any
}
export type LabelValue = { [label: string]: ILabelValue[] }
/**
 * useLoadRelatedData 请求
 * @param relatedDataConfigList
 * @param baseApi 设置api，表示发起个性化请求
 */
export const useLoadRelatedData = (relatedDataConfigList: RelatedDataConfig[], baseApi?: string): LabelValue => {
  const more = reactive<LabelValue>({})
  const requestList = []
  // add 通用接口
  relatedDataConfigList &&
    relatedDataConfigList.length > 0 &&
    requestList.push(api.post<LabelValue>('/common/attachMore', relatedDataConfigList))
  // add 个性化接口
  baseApi && requestList.push(api.get<LabelValue>(`${baseApi}/attachMore`))
  // send api and set more
  requestList.length > 0 &&
    Promise.all(requestList).then(results => {
      results.forEach(result => {
        Object.assign(more, result.code === 0 ? result.data : {})
      })
    })
  return more
}
