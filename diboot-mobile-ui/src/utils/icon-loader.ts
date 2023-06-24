import IconLibrary from '@/components/icon/icon-library'

/**
 * 获取图标组件
 *
 * @param name
 */
export default (name?: string) => {
  if (!name) return
  const split = name.includes(':') ? name.split(/:/) : ['Local', name]
  const lib = IconLibrary[split[0]]
  return lib ? lib[split[1]] : undefined
}
