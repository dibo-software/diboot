import App from './App.vue'
import router from './router'
import pinia from './store'
import directives from './directives'

const app = createApp(App)
app.use(router)
app.use(pinia)
app.mount('#app')

app.use(directives)

import '@/styles/index.scss'
import 'nprogress/nprogress.css'

import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/notification/style/css'
