import * as Element from '@element-plus/icons-vue'
import { capitalize, line2Hump } from '@/utils/str'

/**
 * 加载当前目录下所有图标（vue）
 */
const iconVueFiles = import.meta.glob<Record<string, unknown>>(['./**/*.vue', '!./index.vue'], {
  import: 'default',
  eager: true
})

/**
 * 构建本地图标
 */
const Local = Object.keys(iconVueFiles).reduce((all: Record<string, unknown>, path: string) => {
  const name = capitalize(line2Hump(path.replace(/\.\/(.*)\.vue/, '$1'), '/'))
  all[name] = { name, ...iconVueFiles[path] }
  return all
}, {})

/**
 * 导出所有图标资源
 */
export default { Element, Local } as Record<string, Record<string, unknown>>
