import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import {setTip} from '@/utils/common.js'
import color from '@/utils/color.js'
Vue.config.productionTip = false
App.mpType = 'app'

import uView from "uview-ui";
Vue.use(uView);

import {dibootApi} from './utils/dibootApi.js'
import constant from './utils/constant.js'
import Member from './classes/class.member.js'
import PwdLogin from './classes/class.pwd.login.js'
// import MpLogin from './classes/class.mp.login.js'
import MiniLogin from './classes/class.mini.login.js'

Vue.prototype.$dibootApi = dibootApi
Vue.prototype.$cons = constant
Vue.prototype.$member = new Member
Vue.prototype.$pwdLogin = new PwdLogin
// Vue.prototype.$mpLogin = new MpLogin
Vue.prototype.$miniLogin = new MiniLogin

Vue.prototype.$tip = setTip
Vue.prototype.$color = color

const app = new Vue({
    ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif