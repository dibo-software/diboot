import * as Element from '@element-plus/icons-vue'
import LocalSvg from '@/assets/svg'

export const library: any = { Element, LocalSvg }

/**
 * 查找图标
 * @param name 图标名称 [库别名]:[(路径)图标名]
 */
export const findIcon = (name?: string) => {
  if (!name) return
  const split = name.split(/:/)
  const lib = library[split[0]]
  return lib && lib[split[1]]
}
