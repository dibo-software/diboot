import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
// import checker from 'vite-plugin-checker'
import eslintPlugin from 'vite-plugin-eslint'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { viteMockServe } from 'vite-plugin-mock'
import { fileURLToPath, URL } from 'url'
import fs from 'fs'
const optimizeDepsElementPlusIncludes = [
  'vue',
  'vue-router',
  'pinia',
  '@vueuse/core',
  'axios',
  'echarts',
  '@wangeditor/editor',
  '@wangeditor/editor-for-vue',
  'sortablejs',
  'vue-clipboard3'
]
fs.readdirSync(`./node_modules/element-plus/es/components`).map(dirname => {
  fs.access(`./node_modules/element-plus/es/components/${dirname}/style/css.mjs`, err => {
    if (!err) {
      optimizeDepsElementPlusIncludes.push(`element-plus/es/components/${dirname}/style/css`)
    }
  })
})
// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  return {
    plugins: [
      vue(),
      VueSetupExtend(),
      // checker({ vueTsc: true }),
      eslintPlugin({ fix: true }),
      AutoImport({
        // 解析器
        resolvers: [ElementPlusResolver()],
        // 自动导入Api
        imports: [
          'vue',
          'vue-router',
          'pinia',
          { lodash: [['*', '_']] },
          { 'element-plus': ['ElMessage', 'ElMessageBox', 'ElNotification'] },
          { '@/utils/request': ['api', 'baseURL'] },
          { '@/hooks/use-list': [['default', 'useList']] },
          { '@/hooks/use-detail': [['default', 'useDetail']] },
          { '@/hooks/use-form': [['default', 'useForm']] },
          { '@/hooks/use-option': [['default', 'useOption']] },
          { '@/hooks/use-sort': [['default', 'useSort']] },
          { '@/hooks/use-upload-file': [['default', 'useUploadFile']] },
          { '@/hooks/use-tree-crud': [['default', 'useTreeCrud']] },
          { '@/utils/permission': ['checkPermission', 'checkRole'] }
        ],
        // 为true时在项目根目录自动创建
        dts: 'types/auto-imports.d.ts',
        // 启用 eslint
        eslintrc: { enabled: true, globalsPropValue: 'readonly' }
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
        import { setupProdMockServer } from '../mock/server-config/_prod';

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
    // 预加载项目必需的组件
    optimizeDeps: {
      include: optimizeDepsElementPlusIncludes
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '#': fileURLToPath(new URL('./types', import.meta.url)),
        'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js'
      }
    },
    server: {
      host: true,
      proxy: {
        '/api': 'http://localhost:8080'
      }
    }
  }
})
