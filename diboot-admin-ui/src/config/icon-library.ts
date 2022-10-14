import * as Element from '@element-plus/icons-vue'

/**
 * 加载 @/assets/icon 目录下所有图标（svg|vue）
 */
const iconSvgFiles = import.meta.glob('@/assets/icon/**/*.svg', { as: 'raw', eager: true })
const iconVueFiles = import.meta.glob<Record<string, unknown>>('@/assets/icon/**/*.vue', {
  import: 'default',
  eager: true
})

/**
 * 构建本地图标
 */
const Local = Object.keys(iconSvgFiles).reduce((all: Record<string, unknown>, path: string) => {
  const name = path.replace(/.*icon\/(.*)\.svg/, '$1')
  all[name] = defineComponent({
    name,
    render: () => h('i', { innerHTML: iconSvgFiles[path], style: { display: 'inline-flex' } })
  })
  return all
}, {})
Object.keys(iconVueFiles).reduce((all: Record<string, unknown>, path: string) => {
  const name = path.replace(/.*icon\/(.*)\.vue/, '$1')
  all[name] = { name, ...iconVueFiles[path] }
  return all
}, Local)

/**
 * 导出所有图标资源
 */
export default { Element, Local } as Record<string, Record<string, unknown>>
