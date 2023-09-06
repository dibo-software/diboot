import { createApp } from 'vue'

import App from './App.vue'
import pinia from './stores'
import router from './router'

import { Toast, Dialog, Notify, ImagePreview } from 'vant'
import 'vant/es/toast/style'
import 'vant/es/dialog/style'
import 'vant/es/notify/style'
import 'vant/es/image-preview/style'

const app = createApp(App)

app.use(pinia)
app.use(router)

app.use(Toast)
app.use(Dialog)
app.use(Notify)
app.use(ImagePreview)

app.mount('#app')
