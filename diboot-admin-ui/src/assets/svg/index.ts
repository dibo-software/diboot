// 加载当前目录下所有 svg
const svgFiles = import.meta.globEager('./**/*.svg', { as: 'raw' })

export default Object.keys(svgFiles).reduce((all: any, svgPath: string) => {
  const svgName = svgPath.replace(/\.\/(.*)\.svg/, '$1')
  all[svgName] = defineComponent({ name: svgName, template: svgFiles[svgPath] })
  return all
}, {})
