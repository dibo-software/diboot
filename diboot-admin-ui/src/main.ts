import '@/styles/index.scss'
import '@/styles/common.scss'
import 'nprogress/nprogress.css'

import 'element-plus/theme-chalk/el-loading.css'
import 'element-plus/theme-chalk/el-message.css'
import 'element-plus/theme-chalk/el-message-box.css'
import 'element-plus/theme-chalk/el-notification.css'

import AppView from './App.vue'
import router from './router'
import pinia from './store'
import directives from './directives'
import i18n from './i18n'

import { initFunction } from './utils/initFunction'

const app = createApp(AppView)
app.use(router)
app.use(pinia)
app.use(directives)
app.use(i18n)
app.mount('#app')

// 若需要iframe授权信息注入，可打开下方注释
// initFunction()
