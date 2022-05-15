import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
import eslintPlugin from 'vite-plugin-eslint/dist'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { viteMockServe } from 'vite-plugin-mock'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ command }) => {
  return {
    plugins: [
      vue(),
      VueSetupExtend(),
      // eslint 自动修复
      eslintPlugin({ fix: true }),
      AutoImport({
        // 解析器
        resolvers: [ElementPlusResolver({ importStyle: 'sass' })],
        // 自动导入Api
        imports: [
          'vue',
          'vue-router',
          'pinia',
          { lodash: [['*', '_']] },
          { 'element-plus': ['ElMessage', 'ElMessageBox', 'ElNotification'] },
          { '@/utils/request': ['api', 'baseURL'] },
          { '@/hooks/list': [['default', 'useList']] }
        ],
        // 为true时在项目根目录自动创建
        dts: 'types/auto-imports.d.ts',
        // 启用 eslint
        eslintrc: { enabled: true, globalsPropValue: 'readonly' }
      }),
      Components({
        // 解析器
        resolvers: [ElementPlusResolver({ importStyle: 'sass' })],
        // 自动加载的组件目录，默认值为 ['src/components']
        dirs: ['src/components'],
        // 组件名称包含目录，防止同名组件冲突
        directoryAsNamespace: true,
        // 指定类型声明文件，为true时在项目根目录创建
        dts: 'types/components.d.ts',
        // 导入路径变换
        importPathTransform: path => path.replace(/^.+\/src/g, '@')
      }),
      viteMockServe({
        // 忽略以_开头的文件及目录
        ignore: /^_|\/_/,
        // 开发打包开关(默认开启)
        localEnabled: command === 'serve',
        // 生产打包开关(默认不打包)
        prodEnabled: command !== 'serve',
        // 注入代码(用于生产需要mock)
        injectCode: `
        import { setupProdMockServer } from '../mock/_prodServer';
        
        setupProdMockServer();
        `
      })
    ],
    css: {
      preprocessorOptions: {
        scss: {
          // javascriptEnabled: true,
          additionalData: `@use "@/styles/theme/index.scss" as *;`
        }
      }
    },
    resolve: {
      alias: [
        { find: '@', replacement: resolve(__dirname, 'src') },
        { find: '#', replacement: resolve(__dirname, 'types') }
      ]
    }
  }
})
