/**
 * Created by uu
 * http配置
 */

import Request from '@/utils/luch-request/index.js'
import constant from '@/utils/constant.js'

// token在Header中的key
const JWT_HEADER_KEY = 'authtoken'
const JWT_REFRESH_TOKEN_KEY = 'refreshtoken'
const REDIRECT = 'redirect'
const dibootApi = new Request({
	baseURL: constant.host(),
	header: {
		"content-type": "application/json;charset=utf-8"
	}
})

/**
 * 请求发送之前需要做的处理
 */
dibootApi.interceptors.request.use((config) => { // 可使用async await 做异步操作
  config.header.authtoken = uni.getStorageSync(JWT_HEADER_KEY) || ''
  config.header.refreshtoken = uni.getStorageSync(JWT_REFRESH_TOKEN_KEY) || ''
  return config
}, config => { // 可使用async await 做异步操作
  return Promise.reject(config)
})

/* 响应结束需要做的处理*/
dibootApi.interceptors.response.use((response) => { 
	if(response.data.code === 4001) {
		uni.removeStorageSync(JWT_HEADER_KEY)
		uni.removeStorageSync(REDIRECT)
		//#ifdef H5
		// 重置为首页地址
		window.location.href = constant.frontIndex()
		//#endif
	}
	// 检查是否携带有新的token
	  const newToken = response.header[JWT_HEADER_KEY]
	  if (newToken) {
	    // 将该token设置到vuex以及本地存储中
	    uni.setStorageSync(JWT_HEADER_KEY, newToken)
	  }
	// 截取JsonResult提示
	if(response.data.msg && response.data.msg.indexOf(": ") > 0) {
		response.data.msg = response.data.msg.split(": ")[1]
	}
  return response.data
}, (response) => { /*  对响应错误做点什么 （statusCode !== 200）*/
  return Promise.reject(response)
})
export default dibootApi

