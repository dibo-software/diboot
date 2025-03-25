import { createApp } from 'vue'

import App from './App.vue'
import pinia from './stores'
import router from './router'

import { Toast, Dialog, Notify, ImagePreview } from 'vant'
import 'vant/es/toast/style'
import 'vant/es/dialog/style'
import 'vant/es/notify/style'
import 'vant/es/image-preview/style'
import i18n from './i18n'

import { checkRole, checkPermission } from './utils/permission'

const app = createApp(App)

app.use(pinia)
app.use(router)
app.use(i18n)

app.use(Toast)
app.use(Dialog)
app.use(Notify)
app.use(ImagePreview)

app.directive('hasRole', {
  mounted: (el, binding) => {
    const { not, all } = binding.modifiers
    if (!checkRole(binding.value, not, all)) el.remove()
  }
})
app.directive('hasPermission', {
  mounted: (el, binding) => {
    const { not, all } = binding.modifiers
    if (!checkPermission(binding.value, not, all)) el.remove()
  }
})

app.mount('#app')
