import dibootApi from '@/utils/dibootApi.js'
import constant from '@/utils/constant.js'
import Member from './class.member.js'

/**
 * 微信公众号登陆
 */
export default class MpLogin extends Member {
	constructor() {
		super()
	}

	/**
	 * 微信公众号登陆
	 */
	redirect() {
		let authtoken = uni.getStorageSync("authtoken")
		let redirect = uni.getStorageSync("redirect")
		if (!authtoken && !redirect) {
			this.buildOauth2Url()
		}
	}
	/**
	 * 获取授权URL
	 */
	async buildOauth2Url() {
		const res = await dibootApi.get(`/wx-mp/auth/buildOAuthUrl?url=${encodeURIComponent(constant.frontIndex())}`)
		if (res.code === 0) {
			window.location.href = res.data
			uni.setStorageSync('redirect', true)
		} else {
			console.log('buildOauth2Url错误：', res)
		}
	}
	/**
	 * 登陆
	 */
	go() {
		return new Promise(async (reslove, reject) => {
			try {
				uni.showLoading({title: '登陆中'})
				const res = await dibootApi.get('/wx-mp/auth/apply', {
					params: {
						code: this.getQueryString4hash('code'),
						state: this.getQueryString4hash('state')
					}
				})
				if(res.code === 0) {
					uni.setStorageSync("authtoken", res.data)
					uni.removeStorageSync("redirect")
					let tipMsg = { title: '登录成功', type: 'success' }
					this.$tip ? this.$tip.show(tipMsg) : uni.showToast(tipMsg)
					reslove({code: true})
				} else {
					this.$tip ? this.$tip.show({ title: res.msg, type: 'error', duration: '3000'})  : uni.showToast({ title: res.msg, icon: 'error'})
				}
			} catch(e) {
				console.log(e)
				this.$tip ? this.$tip.show({ title: e.errMsg, type: 'error', duration: '3000'})  : uni.showToast({ title: '网络异常', icon: 'error'})
			} finally {
				uni.hideLoading()
			}
		})
	}
	/**
	 * 获取code和state值
	 * @param {Object} name
	 */
	getQueryString4hash(name) {
		const href = window.location.href
		const arr1 = href.split('?')
		if (arr1 && arr1.length > 1) {
			const paramStr = arr1[1]
			const params = paramStr.split('&')
			for (let i = 0; i < params.length; i++) {
				const paramObjs = params[i].split('=')
				if (paramObjs && paramObjs.length > 1 && paramObjs[0] === name) {
					return paramObjs[1]
				}
			}
		}
		return ''
	}

}
