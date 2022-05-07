import * as Element from '@element-plus/icons-vue'

/**
 * 加载 @/assets/svg 目录下所有 svg
 */
const svgFiles = import.meta.glob('@/assets/svg/**/*.svg', { as: 'raw' })

/**
 * 构建本地 svg 图标库
 */
const LocalSvg = Object.keys(svgFiles).reduce((all: any, svgPath: string) => {
  const svgName = svgPath.replace(/.*svg\/(.*)\.svg/, '$1')
  all[svgName] = defineComponent({ name: svgName, render: () => h('i', { innerHTML: svgFiles[svgPath] }) })
  return all
}, {})

/**
 * 导出所有图标资源
 */
export default { Element, LocalSvg } as any
