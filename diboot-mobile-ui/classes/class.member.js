import dibootApi from '@/utils/dibootApi.js'
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
		const res = await dibootApi.get('/h5/auth/userInfo')
		if (res.code === 0) {
			uni.setStorageSync("member", JSON.stringify(res.data))
		} else {
			console.log('login错误：', res)
		}
	}
}