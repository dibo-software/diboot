import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import eslintPlugin from 'vite-plugin-eslint/dist'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // eslint 自动修复
    eslintPlugin({ fix: true }),
    AutoImport({
      // 解析器
      resolvers: [ElementPlusResolver()],
      // 自动导入Api
      imports: ['vue', 'vue-router', 'pinia', { lodash: [['*', '_']] }, { '@/utils/request': ['api', 'baseURL'] }],
      // 为true时在项目根目录自动创建
      dts: 'types/auto-imports.d.ts'
    }),
    Components({
      // 解析器
      resolvers: [ElementPlusResolver()],
      // 自动加载的组件目录，默认值为 ['src/components']
      dirs: ['src/components'],
      // 组件名称包含目录，防止同名组件冲突
      directoryAsNamespace: true,
      // 指定类型声明文件，为true时在项目根目录创建
      dts: 'types/components.d.ts',
      // 导入路径变换
      importPathTransform: path => path.replace(/^.+\/src/g, '@')
    })
  ],
  resolve: {
    alias: [
      { find: '@', replacement: resolve(__dirname, 'src') },
      { find: '#', replacement: resolve(__dirname, 'types') }
    ]
  }
})
