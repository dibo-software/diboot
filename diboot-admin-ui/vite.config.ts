import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import eslintPlugin from "vite-plugin-eslint/dist";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        // eslint 自动修复
        eslintPlugin({fix: true}),
    ]
})
