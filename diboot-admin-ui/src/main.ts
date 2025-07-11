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
import { i18nInstall } from './i18n'

// import { initFunction } from './utils/initFunction'

import { ElDialog, ElPagination } from 'element-plus'
// 点击弹窗外区域是否关闭弹窗
ElDialog.props.closeOnClickModal.default = false
// 默认分页数
ElPagination.props.pageSizes.default = [10, 15, 20, 30, 50, 100]

const app = createApp(AppView)
app.use(router)
app.use(pinia)
app.use(directives)
app.use(i18nInstall)
app.mount('#app')

// 若需要iframe授权信息注入，可打开下方注释
// initFunction()
