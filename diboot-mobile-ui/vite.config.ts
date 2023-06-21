import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      // 解析器
      resolvers: [VantResolver()],
      // 自动导入Api
      imports: [
        'vue',
        'vue-router',
        'pinia',
        { lodash: [['*', '_']] },
        {
          vant: [
            'showToast',
            'showLoadingToast',
            'showSuccessToast',
            'showFailToast',
            'closeToast',
            'showNotify',
            'showConfirmDialog'
          ]
        },
        // { '@/utils/permission': ['checkPermission', 'checkRole'] },
        { '@/utils/request': ['api', 'baseURL'] },
        { '@/hooks/use-list': [['default', 'useList']] },
        { '@/hooks/use-detail': [['default', 'useDetail']] },
        { '@/hooks/use-form': [['default', 'useForm']] },
        { '@/hooks/use-option': [['default', 'useOption']] },
        { '@/hooks/use-sort': [['default', 'useSort']] },
        { '@/hooks/use-upload-file': [['default', 'useUploadFile']] }
      ],
      // 为true时在项目根目录自动创建
      dts: 'types/auto-imports.d.ts',
      // 启用 eslint
      eslintrc: { enabled: true, globalsPropValue: 'readonly' }
    }),
    Components({
      // 解析器
      resolvers: [VantResolver()],
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
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: true,
    port: 5120,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
