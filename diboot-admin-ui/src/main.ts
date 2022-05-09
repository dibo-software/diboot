import App from './App.vue'
import router from './router'
import pinia from './store'

const app = createApp(App)
app.use(router)
app.use(pinia)
app.mount('#app')

import '@/styles/index.scss'
import 'nprogress/nprogress.css'
