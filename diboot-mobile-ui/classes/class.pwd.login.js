import {service as dibootApi} from '@/utils/dibootApi.js'
import Member from './class.member.js'

export default class PwdLogin extends Member {
	constructor() {
		super()
	}
	/**
	 * 登陆
	 */
	go(form) {
		return new Promise(async (reslove, reject) => {
			try {
				uni.showLoading({title: '登陆中'})
				const res = await dibootApi.post('/h5/auth/login', form)
				if(res.code === 0) {
					uni.setStorageSync("authtoken", res.data)
					this.getMemberInfo()
					let tipMsg = { title: '登录成功', type: 'success' }
					this.$tip ? this.$tip.show(tipMsg) : uni.showToast(tipMsg)
					reslove({code: true})
				} else {
					this.$tip ? this.$tip.show({ title: res.msg, type: 'error', duration: '3000'}) : uni.showToast({ title: res.msg, icon: 'error'})
				}
			} catch(e) {
				console.log(e)
				this.$tip ? this.$tip.show({ title: e.errMsg, type: 'error', duration: '3000'})  : uni.showToast({ title: '网络异常', icon: 'error'})
			} finally {
				uni.hideLoading()
			}
		})
	}

}
