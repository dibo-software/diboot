import '@/styles/index.scss'
import 'nprogress/nprogress.css'

import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/notification/style/css'

import type { App } from 'vue'
import AppView from './App.vue'
import router from './router'
import pinia from './store'
import directives from './directives'

const app = createApp(AppView)

router.install(app as App)
pinia.install(app as App)

directives(app as App)

app.mount('#app')

import moment from 'moment'
import 'moment/dist/locale/zh-cn'

moment.locale('zh-cn')
