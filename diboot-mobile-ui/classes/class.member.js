import {service as dibootApi} from '@/utils/dibootApi.js'
export default class Member {
	constructor(){
		this.$vue = null
		this.$tip = null
	}
	/**
	 * 设置tip对象
	 * @param {Object} $tip
	 */
	setTip($tip) {
		this.$tip = $tip
		return this
	}
	/**
	 * 设置vue对象
	 * @param {Object} $vue
	 */
	setVue($vue) {
		this.$vue = $vue
		return this
	}
	/**
	 * 获取用户信息
	 */
	async getMemberInfo() {
		const res = await dibootApi.get('/h5/userInfo')
		if (res.code === 0) {
			uni.setStorageSync("userInfo", JSON.stringify(res.data))
		} else {
			console.log('加载用户错误：', res)
			uni.clearStorageSync()
			let timer = setTimeout(() => {
				clearTimeout(timer)
			      uni.redirectTo({
			          url: 'pages/login/login'
			      })
			  }, 0)
		}
	}
}