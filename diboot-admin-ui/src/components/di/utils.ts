import type { Control, FormItem } from '@/components/di/type'
import type { LinkageControl, RelatedData, RelatedDataOption } from '@/hooks/use-option'

/**
 * 构建选项获取参数
 *
 * @param formItemList 表单元素列表
 * @return RelatedDataOption
 */
export const buildOptionProps = (formItemList?: FormItem[]) => {
  if (!formItemList || !formItemList.length) return {}
  const optionProps = formItemList
    .filter(e => e.type !== 'list-selector')
    .filter(e => e['loader' as keyof typeof e])
    .reduce((option: RelatedDataOption, e) => {
      const loader = e['loader' as keyof typeof e] as string | RelatedData
      if (typeof loader === 'string') {
        const dicts = option.dict ? (option.dict as string[]) : (option.dict = [])
        dicts.push(loader)
      } else if (e['remote' as keyof typeof e] || e['lazy' as keyof typeof e]) {
        const asyncLoad = option.asyncLoad ? option.asyncLoad : (option.asyncLoad = {})
        loader.lazyChild = !!e['lazy' as keyof typeof e]
        asyncLoad[e.prop] = loader as RelatedData
      } else {
        loader.lazyChild = false
        const load = option.load ? option.load : (option.load = {})
        load[e.prop] = loader as RelatedData
      }
      return option
    }, {})
  formItemList
    .filter(e => e.type !== 'list-selector')
    .filter(e => e['control' as keyof typeof e])
    .reduce((option: RelatedDataOption, e) => {
      const control: Control = e['control' as keyof typeof e] as any
      if (!(control.prop && control.condition)) {
        // 未完全配置，则不生效
        return option
      }
      const asyncLoad = option.asyncLoad ? option.asyncLoad : (option.asyncLoad = {})
      let isAsyncLoad = !(asyncLoad[e.prop] ?? {}).parent
      if ((option.load ?? {})[e.prop]) {
        isAsyncLoad = false
        asyncLoad[e.prop] = (option.load ?? {})[e.prop]
        delete (option.load ?? {})[e.prop]
      }
      ;(asyncLoad[e.prop] ?? {}).disabled = true // 选项受控 阻止自动加载
      ;(asyncLoad[e.prop] ?? {}).lazyChild = false // tree结构选项受控不支持懒加载
      const linkageControl = option.linkageControl ? option.linkageControl : (option.linkageControl = {})
      const controls = linkageControl[control.prop]
        ? (linkageControl[control.prop] as LinkageControl[])
        : (linkageControl[control.prop] = [])
      controls.push({
        prop: e.prop,
        loader: e.prop,
        condition: control.condition,
        autoLoad: !isAsyncLoad
      })

      return option
    }, optionProps)
  return optionProps
}

// snake_case 转 camelCase
const line2Hump = (value: string) => {
  if (!value) return value
  if (!/[_-]/.test(value))
    if (value.toLocaleUpperCase() === value) return value.toLocaleLowerCase()
    else return value.charAt(0).toLocaleLowerCase() + value.substring(1)
  let result: string | undefined = undefined
  for (const word of value.split(/[_-]/)) {
    if (!word) continue
    if (result == null) result = word.toLowerCase()
    else result += word.charAt(0).toLocaleUpperCase() + word.substring(1).toLocaleLowerCase()
  }
  if (value.endsWith('_') && result != null) result += '_'
  return result
}

/**
 * 构建获取选项函数
 *
 * @param relatedData
 * @return (prop: FormItem) => LabelValue[]
 */
export const buildGetRelatedData = (relatedData: Record<string, LabelValue[]>) => (prop: FormItem) => {
  const loader = prop['loader' as keyof typeof prop]
  if (!loader) return
  return typeof loader === 'string' ? relatedData[`${line2Hump(loader)}Options`] : relatedData[prop.prop]
}
